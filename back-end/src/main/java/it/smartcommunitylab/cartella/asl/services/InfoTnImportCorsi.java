package it.smartcommunitylab.cartella.asl.services;

import java.text.ParseException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.smartcommunitylab.cartella.asl.manager.APIUpdateManager;
import it.smartcommunitylab.cartella.asl.model.CorsoDiStudio;
import it.smartcommunitylab.cartella.asl.model.CorsoMetaInfo;
import it.smartcommunitylab.cartella.asl.model.Istituzione;
import it.smartcommunitylab.cartella.asl.model.MetaInfo;
import it.smartcommunitylab.cartella.asl.model.ext.infotn.CorsoInfoTn;
import it.smartcommunitylab.cartella.asl.repository.CorsoDiStudioRepository;
import it.smartcommunitylab.cartella.asl.repository.CorsoMetaInfoRepository;
import it.smartcommunitylab.cartella.asl.repository.IstituzioneRepository;
import it.smartcommunitylab.cartella.asl.util.Constants;
import it.smartcommunitylab.cartella.asl.util.HttpsUtils;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Service
public class InfoTnImportCorsi {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportCorsi.class);

	@Value("${infoTN.api}")
	private String infoTNAPIUrl;
	@Value("${infoTN.startingYear}")
	private int startingYear;
	@Value("${infoTN.user}")
	private String user;
	@Value("${infoTN.pass}")
	private String pass;

	private String apiKey = Constants.API_COURSE_KEY;
	private String auth;

	@Autowired
	CorsoDiStudioRepository corsoDiStudioRepository;
	@Autowired
	CorsoMetaInfoRepository corsoMetaInfoRepository;
	@Autowired
	IstituzioneRepository istituzioneRepository;
	@Autowired
	private APIUpdateManager apiUpdateManager;
	@Autowired
	private HttpsUtils httpsUtils;

	@PostConstruct()
	public void init() {
		if (Utils.isNotEmpty(user) && Utils.isNotEmpty(pass)) {
			String authString = user + ":" + pass;
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			auth = "Basic " + new String(authEncBytes);
		}	
	}
	
	private void updateCorsi(MetaInfo metaInfo) throws Exception {
		int total = 0;
		int stored = 0;
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url;
		int nextYear = metaInfo.getSchoolYear() + 1;
		String year = metaInfo.getSchoolYear() + "/" + String.valueOf(nextYear).substring(2);

		// read epoc timestamp from db(if exist)
		if (metaInfo.getEpocTimestamp() > 0) {
			url = infoTNAPIUrl + "/offerte?schoolYear=" + year + "&timestamp=" + metaInfo.getEpocTimestamp();
		} else {
			metaInfo.setEpocTimestamp(System.currentTimeMillis()); //set it for the first time.
			url = infoTNAPIUrl + "/offerte?schoolYear=" + year;
		}

		logger.info("start importCorsiUsingRESTAPI for year " + year);

		// call api.
		String response = httpsUtils.sendGET(url, "application/json", "application/json", auth, -1);
		if (response != null && !response.isEmpty()) {
			JsonFactory jsonFactory = new JsonFactory();
			jsonFactory.setCodec(objectMapper);
			JsonParser jp = jsonFactory.createParser(response);
			JsonToken current;
			current = jp.nextToken();
			if (current != JsonToken.START_ARRAY) {
				logger.error("Error: root should be array: quiting.");
				throw new Exception("Error: root should be array: quiting.");
			}

			while (jp.nextToken() != JsonToken.END_ARRAY) {
				total += 1;
				CorsoInfoTn corso = jp.readValueAs(CorsoInfoTn.class);
				logger.info("converting " + corso.getExtId());
				CorsoDiStudio courseDb = corsoDiStudioRepository.findByExtIdAndOrigin(corso.getExtId(), corso.getOrigin());
				if (courseDb != null) {
					logger.info(String.format("Course(Offerte) already exists: %s - %s", corso.getOrigin(),
							corso.getExtId()));
					continue;
				}
				Istituzione instituteDb = istituzioneRepository.findIstitutzioneByExtId(corso.getInstituteRef().getOrigin(),
						corso.getInstituteRef().getExtId());
				if (instituteDb == null) {
					logger.info(String.format("Institute not found: %s - %s", corso.getInstituteRef().getOrigin(),
							corso.getInstituteRef().getExtId()));
					continue;
				}
				CorsoMetaInfo courseMetaInfoDb = corsoMetaInfoRepository.findByExtId(corso.getCorsoRef().getExtId(), 
						corso.getCorsoRef().getOrigin());
				if (courseMetaInfoDb == null) {
					logger.info(String.format("CourseMetaInfo not found: %s - %s", corso.getCorsoRef().getOrigin(),
							corso.getCorsoRef().getExtId()));
					continue;
				}

				try {
					CorsoDiStudio course = convertToCourse(corso);
					course.setIstitutoId(instituteDb.getId());
					course.setCourseId(courseMetaInfoDb.getId());
					if (courseMetaInfoDb != null && courseMetaInfoDb.getCodMiur() != null
							&& (courseMetaInfoDb.getCodMiur().startsWith("IP")
									|| courseMetaInfoDb.getCodMiur().startsWith("IT"))) {
						course.setOreAlternanza(400);
					}
					corsoDiStudioRepository.save(course);
					stored += 1;
					logger.info(String.format("Save Course(Offerte): %s - %s - %s", corso.getOrigin(), corso.getExtId(),
							course.getOffertaId()));
				} catch (ParseException e) {
					logger.warn("Parse error:" + e.getMessage());
				}
			}
			// update time stamp (if all works fine).
			metaInfo.setEpocTimestamp(metaInfo.getEpocTimestamp() + 1);
			metaInfo.setTotalRead(total);
			metaInfo.setTotalStore(stored);
		}

	}

	private CorsoDiStudio convertToCourse(CorsoInfoTn corso) throws ParseException {
		CorsoDiStudio result = new CorsoDiStudio();
		result.setOrigin(corso.getOrigin());
		result.setExtId(corso.getExtId());
		result.setOffertaId(Utils.getUUID());
		result.setNome(corso.getCourse());
		result.setAnnoScolastico(getSchoolYear(corso.getSchoolYear()));
		return result;
	}

	private String getSchoolYear(String annoScolastico) {
		return annoScolastico.replace("/", "-");
	}

	public String importCorsiFromRESTAPI() {
		// chedere a APIUpdateManager i propri metadati
		// - scorrere i metadati
		// - se blocked è false:
		// - se richiesto setta schoolYear =
		// "duedigit(MetaInfo.schoolYear)/duedigit((MetaInfo.schoolYear + 1))"
		// // here i need to put year4d/year2d
		// - se richiesto e se epocTimestamp > 0 usare epocTimestamp
		// - invoca API
		// - aggiorna epocTimestamp di MetaInfo (metodo in APIUpdateManager)
		try {
			List<MetaInfo> savedMetaInfoList = apiUpdateManager.fetchMetaInfoForAPI(apiKey);

			if (savedMetaInfoList == null || savedMetaInfoList.isEmpty()) {
				// call generic method to create metaInfos (apiKey, year?)
				savedMetaInfoList = apiUpdateManager.createMetaInfoForAPI(apiKey, true);
			}

			for (MetaInfo metaInfo : savedMetaInfoList) {
				if (!metaInfo.isBlocked()) {
					updateCorsi(metaInfo);
				}
			}

			apiUpdateManager.saveMetaInfoList(apiKey, savedMetaInfoList);

			return "OK";

		} catch (Exception e) {
			logger.error(e.getMessage());
			return e.getMessage();
		}

	}

	// public String importCorsiFromEmpty() throws Exception {
	// logger.info("start importCorsiFromEmpty");
	// int total = 0;
	// int stored = 0;
	// FileReader fileReader = new FileReader(sourceFolder +
	// "FBK_CORSISTUDIO_v.01.json");
	// ObjectMapper objectMapper = new ObjectMapper();
	// objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
	// false);
	// JsonFactory jsonFactory = new JsonFactory();
	// jsonFactory.setCodec(objectMapper);
	// JsonParser jp = jsonFactory.createParser(fileReader);
	// JsonToken current;
	// current = jp.nextToken();
	// if (current != JsonToken.START_OBJECT) {
	// logger.error("Error: root should be object: quiting.");
	// return "Error: root should be object: quiting.";
	// }
	// while (jp.nextToken() != JsonToken.END_OBJECT) {
	// String fieldName = jp.getCurrentName();
	// current = jp.nextToken();
	// if (fieldName.equals("items")) {
	// if (current == JsonToken.START_ARRAY) {
	// while (jp.nextToken() != JsonToken.END_ARRAY) {
	// total += 1;
	// Corso corso = jp.readValueAs(Corso.class);
	// logger.info("converting " + corso.getExtid());
	// Course courseDb = courseRepository.findByExtId(corso.getOrigin(),
	// corso.getExtid());
	// if(courseDb != null) {
	// logger.warn(String.format("Course already exists: %s - %s",
	// corso.getOrigin(), corso.getExtid()));
	// continue;
	// }
	// Institute instituteDb =
	// instituteRepository.findByExtId(corso.getOrigin_institute(),
	// corso.getExtid_institute());
	// if(instituteDb == null) {
	// logger.warn(String.format("Institute not found: %s - %s",
	// corso.getOrigin_institute(), corso.getExtid_institute()));
	// continue;
	// }
	// TeachingUnit teachingUnitDb =
	// teachingUnitRepository.findByExtId(corso.getOrigin_teachingunit(),
	// corso.getExtid_teachingunit());
	// if(teachingUnitDb == null) {
	// logger.warn(String.format("TeachingUnit not found: %s - %s",
	// corso.getOrigin_teachingunit(), corso.getExtid_teachingunit()));
	// continue;
	// }
	// try {
	// Course course = convertToCourse(corso);
	// course.setInstituteId(instituteDb.getId());
	// course.setTeachingUnitId(teachingUnitDb.getId());
	// course.setTeachingUnit(teachingUnitDb.getName());
	// courseRepository.save(course);
	// stored += 1;
	// logger.info(String.format("Save Course: %s - %s - %s", corso.getOrigin(),
	// corso.getExtid(), course.getId()));
	// } catch (ParseException e) {
	// logger.warn("Parse error:" + e.getMessage());
	// }
	// }
	// } else {
	// logger.warn("Error: records should be an array: skipping.");
	// jp.skipChildren();
	// }
	// } else {
	// logger.warn("Unprocessed property: " + fieldName);
	// jp.skipChildren();
	// }
	// }
	// return stored + "/" + total;
	// }

}
