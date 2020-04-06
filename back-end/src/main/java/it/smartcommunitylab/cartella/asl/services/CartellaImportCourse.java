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
import it.smartcommunitylab.cartella.asl.model.CorsoDiStudio;
import it.smartcommunitylab.cartella.asl.model.CorsoMetaInfo;
import it.smartcommunitylab.cartella.asl.model.MetaInfo;
import it.smartcommunitylab.cartella.asl.model.MetaInfoType;
import it.smartcommunitylab.cartella.asl.model.ScheduleUpdate;
import it.smartcommunitylab.cartella.asl.model.ext.Corso;
import it.smartcommunitylab.cartella.asl.repository.CorsoDiStudioRepository;
import it.smartcommunitylab.cartella.asl.repository.CorsoMetaInfoRepository;
import it.smartcommunitylab.cartella.asl.util.Constants;
import it.smartcommunitylab.cartella.asl.util.HttpsUtils;

@Service
public class CartellaImportCourse {
	private static final transient Log logger = LogFactory.getLog(CartellaImportCourse.class);

	@Value("${cartella.api}")
	private String cartellaAPIUrl;

	private String apiKey = Constants.API_COURSE_KEY;
	private int totalSaved = 0;
	private int totalRead = 0;

	@Autowired
	CorsoDiStudioRepository corsoDiStudioRepository;
	@Autowired
	CorsoMetaInfoRepository corsoMetaInfoRepository;
	@Autowired
	private APIUpdateManager apiUpdateManager;
	@Autowired
	private HttpsUtils httpsUtils;

	private void updateCourses(int page, int size, MetaInfo metaInfo) throws Exception {

		logger.info("start import Courses from CARTELLA");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url;

		if (metaInfo.getEpocTimestamp() > 0) {
			url = cartellaAPIUrl + "/api/courses?page=" + page + "&size=" + size + "&timestamp="
					+ metaInfo.getEpocTimestamp();
		} else {
			url = cartellaAPIUrl + "/api/courses?page=" + page + "&size=" + size;
		}

		// 1. Fetch all students from cartella.
		logger.info("Call " + url);
		String bearerHeader = null;

		String coursesResponse = httpsUtils.sendGET(url, "application/json", "application/json", bearerHeader, -1);
		if (coursesResponse != null && !coursesResponse.isEmpty()) {

			Map<String, Object> pagedResponseMap = mapper.readValue(coursesResponse, Map.class);

			List<Corso> temp = mapper.convertValue(pagedResponseMap.get("content"),
					mapper.getTypeFactory().constructCollectionType(List.class, Corso.class));

			for (Corso c : temp) {
				CorsoDiStudio corsoDiStudio = corsoDiStudioRepository
						.findCorsoDiStudioByIstitutoIdAndAnnoScolasticoAndCourseId(c.getInstituteId(),
								c.getSchoolYear(), c.getCourseMetaInfoId());
				// skip if exist already course for institute in the same year
				if (corsoDiStudio == null) {
					corsoDiStudio = new CorsoDiStudio();
					corsoDiStudio.setOffertaId(c.getId());
					corsoDiStudio.setIstitutoId(c.getInstituteId());
					corsoDiStudio.setNome(c.getCourse());
					corsoDiStudio.setCourseId(c.getCourseMetaInfoId());
					corsoDiStudio.setExtId(c.getExtId());
					corsoDiStudio.setOrigin(c.getOrigin());
					corsoDiStudio.setAnnoScolastico(c.getSchoolYear());
					// ore.
					CorsoMetaInfo corsoMetaInfo = corsoMetaInfoRepository.getOne(c.getCourseMetaInfoId());
					if (corsoMetaInfo != null && corsoMetaInfo.getCodMiur() != null
							&& (corsoMetaInfo.getCodMiur().startsWith("IP")
									|| corsoMetaInfo.getCodMiur().startsWith("IT"))) {
						corsoDiStudio.setOreAlternanza(400);
					}

					corsoDiStudioRepository.save(corsoDiStudio);
					totalSaved = totalSaved + 1;
				}
			}

			int numberOfElements = Integer.valueOf(pagedResponseMap.get("numberOfElements").toString());
			totalRead = totalRead + numberOfElements;

			System.err.println(numberOfElements + " cartella course(offerte) received with total of " + totalSaved
					+ " saved inside ASL");

			// call recursively.
			if (numberOfElements == size) {
				updateCourses(++page, size, metaInfo);
			}
		}

	}

	public String importCoursesFromRESTAPI() {
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
					updateCourses(0, 1000, metaInfo);
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
