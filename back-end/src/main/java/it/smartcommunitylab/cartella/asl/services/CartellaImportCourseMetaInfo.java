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
import it.smartcommunitylab.cartella.asl.model.CorsoMetaInfo;
import it.smartcommunitylab.cartella.asl.model.MetaInfo;
import it.smartcommunitylab.cartella.asl.model.ext.CourseMetaInfo;
import it.smartcommunitylab.cartella.asl.repository.CorsoMetaInfoRepository;
import it.smartcommunitylab.cartella.asl.util.Constants;
import it.smartcommunitylab.cartella.asl.util.HttpsUtils;

@Service
public class CartellaImportCourseMetaInfo {
	private static final transient Log logger = LogFactory.getLog(CartellaImportCourseMetaInfo.class);

	@Value("${cartella.api}")
	private String cartellaAPIUrl;

	private String apiKey = Constants.API_COURSE_METAINFO_KEY;
	private int totalSaved = 0;
	private int totalRead = 0;

	@Autowired
	CorsoMetaInfoRepository corsoMetaInfoRepository;
	@Autowired
	private APIUpdateManager apiUpdateManager;
	@Autowired
	private HttpsUtils httpsUtils;

	private void updateCourseMetaInfo(int page, int size, MetaInfo metaInfo) throws Exception {

		logger.info("start import CourseMetaInfo from CARTELLA");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url;

		if (metaInfo.getEpocTimestamp() > 0) {
			url = cartellaAPIUrl + "/api/courses/meta/info?page=" + page + "&size=" + size + "&timestamp="
					+ metaInfo.getEpocTimestamp();
		} else {
			url = cartellaAPIUrl + "/api/courses/meta/info?page=" + page + "&size=" + size;
		}

		// 1. Fetch all students from cartella.
		logger.info("Call " + url);
		String bearerHeader = null;

		String coursesResponse = httpsUtils.sendGET(url, "application/json", "application/json", bearerHeader, -1);
		if (coursesResponse != null && !coursesResponse.isEmpty()) {

			Map<String, Object> pagedResponseMap = mapper.readValue(coursesResponse, Map.class);

			List<CourseMetaInfo> temp = mapper.convertValue(pagedResponseMap.get("content"),
					mapper.getTypeFactory().constructCollectionType(List.class, CourseMetaInfo.class));

			int numberOfElements = Integer.valueOf(pagedResponseMap.get("numberOfElements").toString());
			totalRead = totalRead + numberOfElements;
			// save.
			for (CourseMetaInfo c : temp) {
				CorsoMetaInfo corsoMetaInfo = corsoMetaInfoRepository.findById(c.getId()).orElse(null);
				if (corsoMetaInfo == null) {
					corsoMetaInfo = new CorsoMetaInfo();
					corsoMetaInfo.setId(c.getId());
					corsoMetaInfo.setExtId(c.getExtId());
					corsoMetaInfo.setOrigin(c.getOrigin());
					corsoMetaInfo.setCourse(c.getCourse());
					if (c.getCodMiur() != null)
						corsoMetaInfo.setCodMiur(c.getCodMiur());
					if (c.getYears() != null)
						corsoMetaInfo.setYears(c.getYears());
					corsoMetaInfoRepository.save(corsoMetaInfo);
					// total save count.
					totalSaved = totalSaved + 1;
				} else {
					if ((c.getYears() != null) && (corsoMetaInfo.getYears() == null)) {
						corsoMetaInfo.setYears(c.getYears());
						corsoMetaInfoRepository.save(corsoMetaInfo);
					}
				}
			}

			logger.info(numberOfElements + " cartella meta courses received with total of " + totalSaved
					+ " saved inside ASL");

			// call recursively.
			if (numberOfElements == size) {
				updateCourseMetaInfo(++page, size, metaInfo);
			}
		}

	}

	public String importCourseMetaInfoFromRESTAPI() {
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
					updateCourseMetaInfo(0, 1000, metaInfo);
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
