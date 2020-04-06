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
import it.smartcommunitylab.cartella.asl.model.Registration;
import it.smartcommunitylab.cartella.asl.repository.RegistrationRepository;
import it.smartcommunitylab.cartella.asl.util.Constants;
import it.smartcommunitylab.cartella.asl.util.HttpsUtils;

@Service
public class CartellaImportRegistration {

	private static final transient Log logger = LogFactory.getLog(CartellaImportRegistration.class);

	@Value("${cartella.api}")
	private String cartellaAPIUrl;

	private String apiKey = Constants.API_REGISTRATION_KEY;
	private int totalSaved = 0;
	private int totalRead = 0;

	@Autowired
	private RegistrationRepository registrationRepository;
	@Autowired
	private APIUpdateManager apiUpdateManager;
	@Autowired
	private HttpsUtils httpsUtils;

	/*
	 * public void initRegistration(ScheduleUpdate scheduleUpdate) throws
	 * Exception {
	 * 
	 * // init. MetaInfoType metaInfoType = new MetaInfoType();
	 * metaInfoType.setType(apiKey);
	 * metaInfoType.setScheduleUpdate(scheduleUpdate); List<MetaInfo>
	 * metaInfosRegistrations = new ArrayList<MetaInfo>();
	 * metaInfoType.setMetaInfos(metaInfosRegistrations);
	 * 
	 * MetaInfo metaInfo = new MetaInfo(); metaInfo.setName(apiKey);
	 * updateRegistrations(0, 1000, metaInfo);
	 * 
	 * // update time stamp (if all works fine).
	 * metaInfo.setEpocTimestamp(System.currentTimeMillis() / 1000);
	 * metaInfo.setTotalStore(totalSaved); metaInfo.setTotalRead(totalRead);
	 * 
	 * logger.info("(read,stored) -> (" + totalRead + "," + totalSaved + ")");
	 * 
	 * metaInfosRegistrations.add(metaInfo);
	 * scheduleUpdate.getUpdateMap().put(apiKey, metaInfoType);
	 * 
	 * }
	 */

	private void updateRegistrations(int page, int size, MetaInfo metaInfo) throws Exception {

		logger.info("start import registrations from CARTELLA");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url;

		if (metaInfo.getEpocTimestamp() > 0) {
			url = cartellaAPIUrl + "/extsource/registrations?page=" + page + "&size=" + size + "&timestamp="
					+ metaInfo.getEpocTimestamp();
		} else {
			url = cartellaAPIUrl + "/extsource/registrations?page=" + page + "&size=" + size;
		}

		// 1. Fetch all registrations from cartella.
		logger.info("Call " + url);
		String bearerHeader = null;

		String registrationResponse = httpsUtils.sendGET(url, "application/json", "application/json", bearerHeader, -1);
		if (registrationResponse != null && !registrationResponse.isEmpty()) {

			Map<String, Object> pagedResponseMap = mapper.readValue(registrationResponse, Map.class);

			List<Registration> temp = mapper.convertValue(pagedResponseMap.get("content"),
					mapper.getTypeFactory().constructCollectionType(List.class, Registration.class));

			for (Registration regExt : temp) {
				Registration saved = registrationRepository.findById(regExt.getId()).orElse(null);
				if (saved == null) {
					registrationRepository.save(regExt);
					totalSaved = totalSaved + 1;
				} else {
					saved.setDateFrom(regExt.getDateFrom());
					saved.setDateTo(regExt.getDateTo());
					saved.setTeachingUnitId(regExt.getTeachingUnitId());
					saved.setClassroom(regExt.getClassroom());					
					registrationRepository.save(saved);
				}
			}

			int numberOfElements = Integer.valueOf(pagedResponseMap.get("numberOfElements").toString());
			totalRead = totalRead + numberOfElements;

			System.err.println(numberOfElements + " cartella registrations received with total of " + totalSaved
					+ " saved inside ASL");

			// call recursively.
			if (numberOfElements == size) {
				updateRegistrations(++page, size, metaInfo);
			}
		}

	}

	public String importRegistationFromRESTAPI() {
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
					updateRegistrations(0, 1000, metaInfo);
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
