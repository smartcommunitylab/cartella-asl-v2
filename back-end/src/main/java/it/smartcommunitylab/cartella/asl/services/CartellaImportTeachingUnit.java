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
import it.smartcommunitylab.cartella.asl.model.MetaInfo;
import it.smartcommunitylab.cartella.asl.model.MetaInfoType;
import it.smartcommunitylab.cartella.asl.model.ScheduleUpdate;
import it.smartcommunitylab.cartella.asl.model.TeachingUnit;
import it.smartcommunitylab.cartella.asl.repository.TeachingUnitRepository;
import it.smartcommunitylab.cartella.asl.util.Constants;
import it.smartcommunitylab.cartella.asl.util.HttpsUtils;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Service
public class CartellaImportTeachingUnit {
	private static final transient Log logger = LogFactory.getLog(CartellaImportTeachingUnit.class);

	@Value("${cartella.api}")
	private String cartellaAPIUrl;

	private String apiKey = Constants.API_TEACHINGUNIT_KEY;
	private int totalSaved = 0;
	private int totalRead = 0;

	@Autowired
	TeachingUnitRepository teachingUnitRepository;
	@Autowired
	private APIUpdateManager apiUpdateManager;
	@Autowired
	private HttpsUtils httpsUtils;

	public void initTeachingUnit(ScheduleUpdate scheduleUpdate) throws Exception {

		// init.
		MetaInfoType metaInfoType = new MetaInfoType();
		metaInfoType.setType(apiKey);
		metaInfoType.setScheduleUpdate(scheduleUpdate);
		List<MetaInfo> metaInfosIstituti = new ArrayList<MetaInfo>();
		metaInfoType.setMetaInfos(metaInfosIstituti);

		MetaInfo metaInfo = new MetaInfo();
		metaInfo.setName(apiKey);
		updateTeachingUnit(0, 1000, metaInfo);

		// update time stamp (if all works fine).
		metaInfo.setEpocTimestamp(System.currentTimeMillis());
		metaInfo.setTotalStore(totalSaved);
		metaInfo.setTotalRead(totalRead);

		logger.info("(read,stored) -> (" + totalRead + "," + totalSaved + ")");

		metaInfosIstituti.add(metaInfo);
		scheduleUpdate.getUpdateMap().put(apiKey, metaInfoType);

	}

	private void updateTeachingUnit(int page, int size, MetaInfo metaInfo) throws Exception {

		logger.info("start import Unità scolastiche from CARTELLA");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url;

		if (metaInfo.getEpocTimestamp() > 0) {
			url = cartellaAPIUrl + "/api/unita?page=" + page + "&size=" + size + "&timestamp="
					+ metaInfo.getEpocTimestamp();
		} else {
			url = cartellaAPIUrl + "/api/unita?page=" + page + "&size=" + size;
		}

		// 1. Fetch all students from cartella.
		logger.info("Call " + url);
		String bearerHeader = null;

		String teachingUnitResponse = httpsUtils.sendGET(url, "application/json", "application/json", bearerHeader, -1);
		if (teachingUnitResponse != null && !teachingUnitResponse.isEmpty()) {

			Map<String, Object> pagedResponseMap = mapper.readValue(teachingUnitResponse, Map.class);

			List<it.smartcommunitylab.cartella.asl.model.ext.TeachingUnit> temp = mapper.convertValue(
					pagedResponseMap.get("content"), mapper.getTypeFactory().constructCollectionType(List.class,
							it.smartcommunitylab.cartella.asl.model.ext.TeachingUnit.class));

			for (it.smartcommunitylab.cartella.asl.model.ext.TeachingUnit istExt : temp) {
				TeachingUnit stored = teachingUnitRepository.findById(istExt.getId()).orElse(null);
				if (stored == null) {
					TeachingUnit teachingUnit = convertToTeachingUnit(istExt);
					teachingUnitRepository.save(teachingUnit);
					totalSaved = totalSaved + 1;
				} else {
				    if (Utils.isNotEmpty(istExt.getCodiceMiur())) {
				    	stored.setCodiceMiur(istExt.getCodiceMiur());
				    	teachingUnitRepository.save(stored);
				    }
				}
			}

			int numberOfElements = Integer.valueOf(pagedResponseMap.get("numberOfElements").toString());
			totalRead = totalRead + numberOfElements;

			System.err.println(numberOfElements + " cartella teaching unit received with total of " + totalSaved
					+ " saved inside ASL");

			// call recursively.
			if (numberOfElements == size) {
				updateTeachingUnit(++page, size, metaInfo);
			}
		}

	}

	private TeachingUnit convertToTeachingUnit(it.smartcommunitylab.cartella.asl.model.ext.TeachingUnit istExt) {
		TeachingUnit result = new TeachingUnit();
		result.setId(istExt.getId());
		result.setExtId(istExt.getExtId());
		result.setName(istExt.getName());
		result.setOrigin(istExt.getOrigin());
	    result.setAddress(istExt.getAddress());
	    result.setCf(istExt.getCf());
	    result.setDescription(istExt.getDescription());
	    result.setInstituteId(istExt.getInstituteId());
	    if (Utils.isNotEmpty(istExt.getCodiceIstat())) {
	    	result.setCodiceIstat(istExt.getCodiceIstat());	
	    }
	    if (Utils.isNotEmpty(istExt.getCodiceMiur())) {
	    	result.setCodiceMiur(istExt.getCodiceMiur());	
	    }	    	    
		if (istExt.getGeocode() != null) {
		 result.setLatitude(istExt.getGeocode()[1]);
		 result.setLongitude(istExt.getGeocode()[0]);
		}
		
		return result;
	}

	public String importTeachingUnitFromRESTAPI() {
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
					updateTeachingUnit(0, 1000, metaInfo);
					// update time stamp (if all works fine).
					if (metaInfo.getEpocTimestamp() < 0) {
						metaInfo.setEpocTimestamp(System.currentTimeMillis()); // set it for first time.
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
