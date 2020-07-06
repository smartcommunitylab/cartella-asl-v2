package it.smartcommunitylab.cartella.asl.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.smartcommunitylab.cartella.asl.manager.APIUpdateManager;
import it.smartcommunitylab.cartella.asl.model.Azienda;
import it.smartcommunitylab.cartella.asl.model.MetaInfo;
import it.smartcommunitylab.cartella.asl.repository.AziendaRepository;
import it.smartcommunitylab.cartella.asl.util.Constants;
import it.smartcommunitylab.cartella.asl.util.HttpsUtils;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Service
public class CartellaImportAziende {
	private static final transient Log logger = LogFactory.getLog(CartellaImportAziende.class);

	@Value("${cartella.api}")
	private String cartellaAPIUrl;

	private String apiKey = Constants.API_AZIENDE_KEY;
	
	private int totalSaved = 0;
	private int totalRead = 0;

	@Autowired
	private AziendaRepository aziendaRepository;
	
	@Autowired
	private APIUpdateManager apiUpdateManager;
	@Autowired
	private HttpsUtils httpsUtils;
	
	private String getMaxString(String content) {
		String result = "";
		if(Utils.isNotEmpty(content)) {
			if(content.length() > 1024) {
				result = content.substring(0, 1024);
			} else {
				result = content;
			}
		}
		return result;
	}

	private void updateAziende(int page, int size, MetaInfo metaInfo) throws Exception {

		logger.info("start import Aziende from CARTELLA");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url;

		if (metaInfo.getEpocTimestamp() > 0) {
			url = cartellaAPIUrl + "/extsource/aziende?page=" + page + "&size=" + size + "&timestamp="
					+ metaInfo.getEpocTimestamp();
		} else {
			url = cartellaAPIUrl + "/extsource/aziende?page=" + page + "&size=" + size;
		}

		// 1. Fetch all students from cartella.
		logger.info("Call " + url);
		String bearerHeader = null;

		String aziendeResponse = httpsUtils.sendGET(url, "application/json", "application/json", bearerHeader, -1);
		if (aziendeResponse != null && !aziendeResponse.isEmpty()) {

			@SuppressWarnings("unchecked")
			Map<String, Object> pagedResponseMap = mapper.readValue(aziendeResponse, Map.class);

			List<it.smartcommunitylab.cartella.asl.model.ext.Azienda> temp = mapper.convertValue(
					pagedResponseMap.get("content"), mapper.getTypeFactory().constructCollectionType(List.class,
							it.smartcommunitylab.cartella.asl.model.ext.Azienda.class));

			int numberOfElements = Integer.valueOf(pagedResponseMap.get("numberOfElements").toString());
			totalRead = totalRead + numberOfElements;
			// convert.
			List<Azienda> tobeSaved = new ArrayList<Azienda>();
			for (it.smartcommunitylab.cartella.asl.model.ext.Azienda aziendaExt : temp) {
				// find existing.
				Azienda stored = aziendaRepository.findById(aziendaExt.getId()).orElse(null);
				if (stored == null) {
					Azienda result = new Azienda();
					result.setId(aziendaExt.getId());
					result.setAddress(getMaxString(aziendaExt.getAddress()));
					result.setDescription(getMaxString(aziendaExt.getDescription()));
					result.setEmail(aziendaExt.getEmail());
					result.setExtId(aziendaExt.getExtId());
					result.setNome(getMaxString(aziendaExt.getName()));
					result.setOrigin(aziendaExt.getOrigin());
					result.setPartita_iva(aziendaExt.getCf());
					result.setPec(aziendaExt.getPec());
					result.setPhone(aziendaExt.getPhone());
					result.setBusinessName(getMaxString(aziendaExt.getBusinessName()));
					if (aziendaExt.getGeocode() != null) {
						result.setLatitude(aziendaExt.getGeocode()[1]);
						result.setLongitude(aziendaExt.getGeocode()[0]);
					}
					if(aziendaExt.getAtecoCode().size() > 0) {
						String[] codes = new String[aziendaExt.getAtecoCode().size()]; 
						result.setAtecoCode(aziendaExt.getAtecoCode().toArray(codes));
					}
					if(aziendaExt.getAtecoDesc().size() > 0) {
						String[] descs = new String[aziendaExt.getAtecoDesc().size()]; 
						result.setAtecoDesc(aziendaExt.getAtecoDesc().toArray(descs));
					}
					result.setIdTipoAzienda(aziendaExt.getIdTipoAzienda());
					totalSaved = totalSaved + 1;
					tobeSaved.add(result);
				} else {
				   	stored.setIdTipoAzienda(aziendaExt.getIdTipoAzienda());				   	
				}
			}

			aziendaRepository.saveAll(tobeSaved);

			System.err.println(
					numberOfElements + " cartella aziende received with total of " + totalSaved + " saved inside ASL");

			// call recursively.
			if (numberOfElements == size) {
				updateAziende(++page, size, metaInfo);
			}
		}

	}

	public String importAziendeFromRESTAPI() {
		totalSaved = 0;
		totalRead = 0;
		try {
			List<MetaInfo> savedMetaInfoList = apiUpdateManager.fetchMetaInfoForAPI(apiKey);

			if (savedMetaInfoList == null || savedMetaInfoList.isEmpty()) {
				// call generic method to create metaInfos (apiKey, year?)
				savedMetaInfoList = apiUpdateManager.createMetaInfoForAPI(apiKey, false);
			}

			for (MetaInfo metaInfo : savedMetaInfoList) {
				if (!metaInfo.isBlocked()) {
					updateAziende(0, 1000, metaInfo);
					// update time stamp (if all works fine).
					if (metaInfo.getEpocTimestamp() < 0) {
						metaInfo.setEpocTimestamp(System.currentTimeMillis()); //set it for the first time.	
					} else {
						metaInfo.setEpocTimestamp(metaInfo.getEpocTimestamp() + 1);	
					}		
					metaInfo.setTotalStore(totalSaved);
					metaInfo.setTotalRead(totalRead);
					logger.info("(read,stored) -> (" + totalRead + "," + totalSaved + ")");
				}
			}
			apiUpdateManager.saveMetaInfoList(apiKey, savedMetaInfoList);

			return "OK";

		} catch (Exception e) {
			logger.error(e.getMessage());
			return e.getMessage();
		}

	}

}
