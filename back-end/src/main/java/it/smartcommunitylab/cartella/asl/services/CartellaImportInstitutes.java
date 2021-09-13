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

import it.smartcommunitylab.cartella.asl.beans.Point;
import it.smartcommunitylab.cartella.asl.manager.APIUpdateManager;
import it.smartcommunitylab.cartella.asl.model.Istituzione;
import it.smartcommunitylab.cartella.asl.model.MetaInfo;
import it.smartcommunitylab.cartella.asl.model.MetaInfoType;
import it.smartcommunitylab.cartella.asl.model.ScheduleUpdate;
import it.smartcommunitylab.cartella.asl.repository.IstituzioneRepository;
import it.smartcommunitylab.cartella.asl.util.Constants;
import it.smartcommunitylab.cartella.asl.util.HttpsUtils;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Service
public class CartellaImportInstitutes {
	private static final transient Log logger = LogFactory.getLog(CartellaImportInstitutes.class);

	@Value("${cartella.api}")
	private String cartellaAPIUrl;

	private String apiKey = Constants.API_ISTITUTI_KEY;
	private int totalSaved = 0;
	private int totalRead = 0;

	@Autowired
	IstituzioneRepository istituzioneRepository;
	@Autowired
	private APIUpdateManager apiUpdateManager;
	@Autowired
	private HttpsUtils httpsUtils;

	public void initIstituti(ScheduleUpdate scheduleUpdate) throws Exception {

		// init.
		MetaInfoType metaInfoType = new MetaInfoType();
		metaInfoType.setType(apiKey);
		metaInfoType.setScheduleUpdate(scheduleUpdate);
		List<MetaInfo> metaInfosIstituti = new ArrayList<MetaInfo>();
		metaInfoType.setMetaInfos(metaInfosIstituti);

		MetaInfo metaInfo = new MetaInfo();
		metaInfo.setName(apiKey);
		updateIstituti(0, 1000, metaInfo);

		// update time stamp (if all works fine).
		metaInfo.setEpocTimestamp(System.currentTimeMillis());
		metaInfo.setTotalStore(totalSaved);
		metaInfo.setTotalRead(totalRead);

		logger.info("(read,stored) -> (" + totalRead + "," + totalSaved + ")");

		metaInfosIstituti.add(metaInfo);
		scheduleUpdate.getUpdateMap().put(apiKey, metaInfoType);

	}

	private void updateIstituti(int page, int size, MetaInfo metaInfo) throws Exception {

		logger.info("start import Istituti from CARTELLA");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url;

		if (metaInfo.getEpocTimestamp() > 0) {
			url = cartellaAPIUrl + "/api/institutes?page=" + page + "&size=" + size + "&timestamp="
					+ metaInfo.getEpocTimestamp();
		} else {
			url = cartellaAPIUrl + "/api/institutes?page=" + page + "&size=" + size;
		}

		// 1. Fetch all students from cartella.
		logger.info("Call " + url);
		String bearerHeader = null;

		String instituteResponse = httpsUtils.sendGET(url, "application/json", "application/json", bearerHeader, -1);
		if (instituteResponse != null && !instituteResponse.isEmpty()) {

			Map<String, Object> pagedResponseMap = mapper.readValue(instituteResponse, Map.class);

			List<it.smartcommunitylab.cartella.asl.model.ext.Istituzione> temp = mapper.convertValue(pagedResponseMap.get("content"),
					mapper.getTypeFactory().constructCollectionType(List.class, it.smartcommunitylab.cartella.asl.model.ext.Istituzione.class));

			for (it.smartcommunitylab.cartella.asl.model.ext.Istituzione istExt : temp) {
				Istituzione stored = istituzioneRepository.findById(istExt.getId()).orElse(null);
				if (stored == null) {
					Istituzione institute = convertToInstitute(istExt);
					istituzioneRepository.save(institute);
					totalSaved = totalSaved + 1;
				} else {
					if(Utils.isEmpty(stored.getRdpAddress())) {
						stored.setRdpAddress(istExt.getRdpAddress());
						istituzioneRepository.save(stored);
					}
					if(Utils.isEmpty(stored.getRdpEmail())) {
						stored.setRdpEmail(istExt.getRdpEmail());
						istituzioneRepository.save(stored);
					}
					if(Utils.isEmpty(stored.getRdpName())) {
						stored.setRdpName(istExt.getRdpName());
						istituzioneRepository.save(stored);
					}
					if(Utils.isEmpty(stored.getRdpPhoneFax())) {
						stored.setRdpPhoneFax(istExt.getRdpPhoneFax());
						istituzioneRepository.save(stored);
					}
				}
			}

			int numberOfElements = Integer.valueOf(pagedResponseMap.get("numberOfElements").toString());
			totalRead = totalRead + numberOfElements;

			System.err.println(numberOfElements + " cartella institutes received with total of " + totalSaved
					+ " saved inside ASL");

			// call recursively.
			if (numberOfElements == size) {
				updateIstituti(++page, size, metaInfo);
			}
		}

	}

	private Istituzione convertToInstitute(it.smartcommunitylab.cartella.asl.model.ext.Istituzione istExt) {
		Istituzione result = new Istituzione();
		result.setId(istExt.getId());
		result.setAddress(istExt.getAddress());
		result.setCf(istExt.getCf());
		result.setExtId(istExt.getExtId());
		
		if (istExt.getGeocode() != null) {
			result.setCoordinate(new Point(istExt.getGeocode()[1], istExt.getGeocode()[0]));			
		}
		
		result.setName(istExt.getName());
		result.setOrigin(istExt.getOrigin());
		
		result.setRdpAddress(istExt.getRdpAddress());
		result.setRdpEmail(istExt.getRdpEmail());
		result.setRdpName(istExt.getRdpName());
		result.setRdpPhoneFax(istExt.getRdpPhoneFax());
		return result;
	}

	public String importIstitutiFromRESTAPI() {
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
					updateIstituti(0, 1000, metaInfo);
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
