package it.smartcommunitylab.cartella.asl.services;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import it.smartcommunitylab.cartella.asl.model.CorsoMetaInfo;
import it.smartcommunitylab.cartella.asl.model.Istituzione;
import it.smartcommunitylab.cartella.asl.model.MetaInfo;
import it.smartcommunitylab.cartella.asl.model.Registration;
import it.smartcommunitylab.cartella.asl.model.Studente;
import it.smartcommunitylab.cartella.asl.model.TeachingUnit;
import it.smartcommunitylab.cartella.asl.model.ext.infotn.IscrizioneCorsoInfoTn;
import it.smartcommunitylab.cartella.asl.repository.CorsoDiStudioRepository;
import it.smartcommunitylab.cartella.asl.repository.CorsoMetaInfoRepository;
import it.smartcommunitylab.cartella.asl.repository.IstituzioneRepository;
import it.smartcommunitylab.cartella.asl.repository.RegistrationRepository;
import it.smartcommunitylab.cartella.asl.repository.StudenteRepository;
import it.smartcommunitylab.cartella.asl.repository.TeachingUnitRepository;
import it.smartcommunitylab.cartella.asl.util.Constants;
import it.smartcommunitylab.cartella.asl.util.HttpsUtils;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Service
public class InfoTnImportIscrizioneCorsi {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportIscrizioneCorsi.class);

	@Value("${infoTN.api}")
	private String infoTNAPIUrl;
	@Value("${infoTN.startingYear}")
	private int startingYear;
	@Value("${infoTN.user}")
	private String user;
	@Value("${infoTN.pass}")
	private String pass;

	private String apiKey = Constants.API_REGISTRATION_KEY;
	private String auth;

	@Autowired
	APIUpdateManager apiUpdateManager;
	@Autowired
	HttpsUtils httpsUtils;
	@Autowired
	StudenteRepository studenteRepository;
	@Autowired
	CorsoDiStudioRepository corsoDiStudioRepository;
	@Autowired
	RegistrationRepository registrationRepository;
	@Autowired
	IstituzioneRepository istituzioneRepository;
	@Autowired
	TeachingUnitRepository teachingUnitRepository;
	@Autowired
	CorsoMetaInfoRepository corsoMetaInfoRepository;

	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	//SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);
	
	@PostConstruct()
	public void init() {
		if (Utils.isNotEmpty(user) && Utils.isNotEmpty(pass)) {
			String authString = user + ":" + pass;
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			auth = "Basic " + new String(authEncBytes);
		}	
	}

	private void updateIscirzioneCorsi(MetaInfo metaInfo) throws Exception {
		int total = 0;
		int stored = 0;
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url;
		int nextYear = metaInfo.getSchoolYear() + 1;
		String schoolYear = metaInfo.getSchoolYear() + "/" + String.valueOf(nextYear).substring(2);

		// read epoc timestamp from db(if exist)
		if (metaInfo.getEpocTimestamp() > 0) {
			url = infoTNAPIUrl + "/iscrizioni?schoolYear=" + schoolYear + "&timestamp=" + metaInfo.getEpocTimestamp();
		} else {
			metaInfo.setEpocTimestamp(System.currentTimeMillis()); //set it for the first time.
			url = infoTNAPIUrl + "/iscrizioni?schoolYear=" + schoolYear;
		}
		logger.info("start importIscirzioneCorsiUsingRESTAPI for year " + schoolYear);

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
				IscrizioneCorsoInfoTn iscrizione = jp.readValueAs(IscrizioneCorsoInfoTn.class);
				Registration registrationDb = registrationRepository.findByExtIdAndOrigin(iscrizione.getExtId(), iscrizione.getOrigin());
				if (registrationDb != null) {
					logger.warn(String.format("Registration already exists: %s - %s", iscrizione.getOrigin(),
							iscrizione.getExtId()));
					registrationDb.setDateFrom(LocalDate.parse(iscrizione.getStudent().getDateFrom(), dtf));
					registrationDb.setDateTo(LocalDate.parse(iscrizione.getStudent().getDateTo(), dtf));
					registrationDb.setClassroom(iscrizione.getStudent().getClassRoom());
					registrationRepository.save(registrationDb);
					continue;
				}
				
				CorsoMetaInfo courseMetaInfoDb = corsoMetaInfoRepository
						.findByExtId(iscrizione.getCourseRef().getExtId(), iscrizione.getCourseRef().getOrigin());
				if (courseMetaInfoDb == null) {
					logger.warn(String.format("CourseMetaInfo not found: %s - %s",
							iscrizione.getCourseRef().getOrigin(), iscrizione.getCourseRef().getExtId()));
					continue;
				}
				Istituzione instituteDb = istituzioneRepository.findIstitutzioneByExtId(iscrizione.getInstituteRef().getOrigin(),
						iscrizione.getInstituteRef().getExtId());
				if (instituteDb == null) {
					logger.warn(String.format("Institute not found: %s - %s", iscrizione.getInstituteRef().getOrigin(),
							iscrizione.getInstituteRef().getExtId()));
					continue;
				}
				TeachingUnit teachingUnitDb = teachingUnitRepository.findByExtIdAndOrigin(iscrizione.getTeachingUnitRef().getExtId(),
						iscrizione.getTeachingUnitRef().getOrigin());
				if (teachingUnitDb == null) {
					logger.warn(String.format("TeachingUnit not found: %s - %s",
							iscrizione.getTeachingUnitRef().getOrigin(), iscrizione.getTeachingUnitRef().getExtId()));
					continue;
				}
				Studente student = studenteRepository.findByExtIdAndOrigin(iscrizione.getStudent().getExtId(),
						iscrizione.getStudent().getOrigin());
				if (student == null) {
					logger.warn(String.format("Student not found: %s", iscrizione.getStudent().getExtId()));
					continue;
				}

				logger.info("converting " + iscrizione.getExtId());
				
				Registration registration = convertToRegistration(iscrizione, schoolYear);
				registration.setInstituteId(instituteDb.getId());
				registration.setTeachingUnitId(teachingUnitDb.getId());
				registration.setCourseId(courseMetaInfoDb.getId());
				registration.setCourse(courseMetaInfoDb.getCourse());
				registration.setStudentId(student.getId());
				registrationRepository.save(registration);
				stored += 1;
				logger.info(String.format("Save Registration: %s - %s - %s", iscrizione.getOrigin(),
						iscrizione.getExtId(), registration.getId()));
			}

			// update time stamp (if all works fine).
			metaInfo.setEpocTimestamp(metaInfo.getEpocTimestamp() + 1);
			metaInfo.setTotalRead(total);
			metaInfo.setTotalStore(stored);

		}

	}

	private Registration convertToRegistration(IscrizioneCorsoInfoTn iscrizioneCorso, String schoolYear)
			throws ParseException {
		Registration result = new Registration();
		result.setOrigin(iscrizioneCorso.getOrigin());
		result.setExtId(iscrizioneCorso.getExtId());
		result.setId(Utils.getUUID());
		result.setCreationDate(LocalDate.now());
		result.setDateFrom(LocalDate.parse(iscrizioneCorso.getStudent().getDateFrom(), dtf));
		result.setDateTo(LocalDate.parse(iscrizioneCorso.getStudent().getDateTo(), dtf));
		result.setSchoolYear(getSchoolYear(schoolYear));
		result.setClassroom(iscrizioneCorso.getStudent().getClassRoom());
		return result;
	}

	private String getSchoolYear(String annoScolastico) {
		return annoScolastico.replace("/", "-");
	}

	public String importIscrizioneCorsiFromRESTAPI() {
		try {
			List<MetaInfo> savedMetaInfoList = apiUpdateManager.fetchMetaInfoForAPI(apiKey);

			if (savedMetaInfoList == null || savedMetaInfoList.isEmpty()) {
				// call generic method to create metaInfos (apiKey, year?)
				savedMetaInfoList = apiUpdateManager.createMetaInfoForAPI(apiKey, true);
			}

			for (MetaInfo metaInfo : savedMetaInfoList) {
				if (!metaInfo.isBlocked()) {
					updateIscirzioneCorsi(metaInfo);
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
