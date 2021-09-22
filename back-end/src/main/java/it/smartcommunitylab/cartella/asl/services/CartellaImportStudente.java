package it.smartcommunitylab.cartella.asl.services;

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
import it.smartcommunitylab.cartella.asl.model.MetaInfo;
import it.smartcommunitylab.cartella.asl.model.Studente;
import it.smartcommunitylab.cartella.asl.repository.StudenteRepository;
import it.smartcommunitylab.cartella.asl.util.Constants;
import it.smartcommunitylab.cartella.asl.util.HttpsUtils;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Service
public class CartellaImportStudente {
	private static final transient Log logger = LogFactory.getLog(CartellaImportStudente.class);

	@Value("${cartella.api}")
	private String cartellaAPIUrl;

	private String apiKey = Constants.API_STUDENTE_KEY;
	private int totalSaved = 0;
	private int totalRead = 0;

	@Autowired
	StudenteRepository studenteRepository;
	@Autowired
	private APIUpdateManager apiUpdateManager;
	@Autowired
	private HttpsUtils httpsUtils;

	private void updateStudente(int page, int size, MetaInfo metaInfo) throws Exception {

		logger.info("start import Studente from CARTELLA");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url;

		if (metaInfo.getEpocTimestamp() > 0) {
			url = cartellaAPIUrl + "/extsource/students?page=" + page + "&size=" + size + "&timestamp="
					+ metaInfo.getEpocTimestamp();
		} else {
			url = cartellaAPIUrl + "/extsource/students?page=" + page + "&size=" + size;
		}

		// 1. Fetch all students from cartella.
		logger.info("Call " + url);
		String bearerHeader = null;

		String studentResponse = httpsUtils.sendGET(url, "application/json", "application/json", bearerHeader, -1);
		if (studentResponse != null && !studentResponse.isEmpty()) {

			Map<String, Object> pagedResponseMap = mapper.readValue(studentResponse, Map.class);

			List<Studente> temp = mapper.convertValue(pagedResponseMap.get("content"),
					mapper.getTypeFactory().constructCollectionType(List.class, Studente.class));

			for (Studente studentExt : temp) {
				Studente stored = studenteRepository.findById(studentExt.getId()).orElse(null);
				if (stored == null) {
					studenteRepository.save(studentExt);
					totalSaved = totalSaved + 1;
				} else {
					if(Utils.isEmpty(stored.getBirthdate())) {
						stored.setBirthdate(studentExt.getBirthdate());
						studenteRepository.save(stored);						
					}
					if(Utils.isEmpty(stored.getExtId())) {
						stored.setExtId(studentExt.getExtId());
						studenteRepository.save(stored);
					}
					if(Utils.isEmpty(stored.getOrigin())) {
						stored.setOrigin(studentExt.getOrigin());
						studenteRepository.save(stored);
					}
				}
			}

			int numberOfElements = Integer.valueOf(pagedResponseMap.get("numberOfElements").toString());
			totalRead = totalRead + numberOfElements;

			System.err.println(
					numberOfElements + " cartella students received with total of " + totalSaved + " saved inside ASL");

			// call recursively.
			if (numberOfElements == size) {
				updateStudente(++page, size, metaInfo);
			}
		}

	}

	public String importStudentsFromRESTAPI() {
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
					updateStudente(0, 1000, metaInfo);
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
