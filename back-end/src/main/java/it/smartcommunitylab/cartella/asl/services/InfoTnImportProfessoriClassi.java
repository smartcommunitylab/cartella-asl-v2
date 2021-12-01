package it.smartcommunitylab.cartella.asl.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import it.smartcommunitylab.cartella.asl.model.ProfessoriClassi;
import it.smartcommunitylab.cartella.asl.model.ext.ProfessorClassInfoTn;
import it.smartcommunitylab.cartella.asl.repository.CorsoMetaInfoRepository;
import it.smartcommunitylab.cartella.asl.repository.IstituzioneRepository;
import it.smartcommunitylab.cartella.asl.repository.ProfessoriClassiRepository;
import it.smartcommunitylab.cartella.asl.util.Constants;
import it.smartcommunitylab.cartella.asl.util.HttpsUtils;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Service
public class InfoTnImportProfessoriClassi {

	private static final transient Log logger = LogFactory.getLog(InfoTnImportProfessoriClassi.class);

	@Value("${infoTN.api}")
	private String infoTNAPIUrl;
	@Value("${infoTN.startingYear}")
	private int startingYear;
	@Value("${infoTN.user}")
	private String user;
	@Value("${infoTN.pass}")
	private String pass;

	private String apiKey = Constants.API_PROFESSORI_CLASSI_KEY;
	private String auth;

	@Autowired
	private APIUpdateManager apiUpdateManager;
	@Autowired
	private ProfessoriClassiRepository professoriClassiRepository;
	@Autowired
	private IstituzioneRepository istituzioneRepository;
	@Autowired
	private CorsoMetaInfoRepository corsoMetaInfoRepository;
	@Autowired
	private HttpsUtils httpsUtils;

	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);

	@PostConstruct()
	public void init() {
		if (Utils.isNotEmpty(user) && Utils.isNotEmpty(pass)) {
			String authString = user + ":" + pass;
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			auth = "Basic " + new String(authEncBytes);
		}	
	}
	
	public void updateProfessoriClassi(MetaInfo metaInfo) throws Exception {
		int total = 0;
		int stored = 0;
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url;
		int nextYear = metaInfo.getSchoolYear() + 1;
		String schoolYear = metaInfo.getSchoolYear() + "/" + String.valueOf(nextYear).substring(2);

		// read epoc timestamp from db(if exist)
		if (metaInfo.getEpocTimestamp() > 0) {
			url = infoTNAPIUrl + "/professoriclassi?schoolYear=" + schoolYear;
			//url = infoTNAPIUrl + "/professoriclassi?schoolYear=" + schoolYear + "&timestamp=" + metaInfo.getEpocTimestamp();
		} else {
			metaInfo.setEpocTimestamp(System.currentTimeMillis()); //set it for the first time.
			url = infoTNAPIUrl + "/professoriclassi?schoolYear=" + schoolYear;
		}
		logger.info("start importProfessoriClassiUsingRESTAPI for year " + schoolYear);

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

			List<ProfessoriClassi> tobeSaved = new ArrayList<ProfessoriClassi>();
			while (jp.nextToken() != JsonToken.END_ARRAY) {
				total += 1;
				it.smartcommunitylab.cartella.asl.model.ext.ProfessorClassInfoTn professorClass = jp
						.readValueAs(it.smartcommunitylab.cartella.asl.model.ext.ProfessorClassInfoTn.class);
				//TODO test
				professorClass.setOrigin("INFOTNISTRUZIONE");
				professorClass.getCourse().setOrigin("INFOTNISTRUZIONE");
				ProfessoriClassi professorClassLocal = professoriClassiRepository
						.findProfessoriClassiByExtId(professorClass.getExtId());
				if (professorClassLocal != null) {
					logger.info("skipping existing ProfessoriClassi: " + professorClassLocal.getExtId());
					continue;
				}

				tobeSaved.add(convertToLocalProfessorClassBean(professorClass));

			}

			stored = tobeSaved.size();
			professoriClassiRepository.saveAll(tobeSaved);

			// update time stamp (if all works fine).
			metaInfo.setEpocTimestamp(metaInfo.getEpocTimestamp() + 1);
			metaInfo.setTotalRead(total);
			metaInfo.setTotalStore(stored);

		}

	}

	private ProfessoriClassi convertToLocalProfessorClassBean(ProfessorClassInfoTn professorClass)
			throws ParseException {
		ProfessoriClassi result = new ProfessoriClassi();
		result.setId(UUID.randomUUID().toString());
		result.setClassroom(professorClass.getClassroom());
		result.setCourseExtId(professorClass.getCourse().getExtId());
		result.setDateFrom(sdf.parse(professorClass.getDatefrom()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
		result.setDateTo(sdf.parse(professorClass.getDateto()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
		result.setExtId(professorClass.getExtId());
		result.setOrigin(professorClass.getOrigin());
		result.setSchoolYear(getSchoolYear(professorClass.getSchoolyear()));
		result.setTeacherExtId(professorClass.getTeacher().getExtId());
		result.setInstituteExtId(professorClass.getExtIdInstitute());
		Istituzione istituto = istituzioneRepository.findIstitutzioneByExtId(professorClass.getOrigin(), professorClass.getExtIdInstitute());
		if(istituto != null) {
			result.setIstitutoId(istituto.getId());
		}
		CorsoMetaInfo corso = corsoMetaInfoRepository.findByExtId(professorClass.getCourse().getExtId(), professorClass.getCourse().getOrigin());
		if(corso != null) {
			result.setCorsoId(corso.getId());
			result.setCorso(corso.getCourse());
		}
		return result;
	}

	private String getSchoolYear(String annoScolastico) {
		return annoScolastico.replace("/", "-");
	}

	public String importProfessoriClassiFromRESTAPI() {
		try {
			List<MetaInfo> savedMetaInfoList = apiUpdateManager.fetchMetaInfoForAPI(apiKey);

			if (savedMetaInfoList == null || savedMetaInfoList.isEmpty()) {
				// call generic method to create metaInfos (apiKey, year?)
				savedMetaInfoList = apiUpdateManager.createMetaInfoForAPI(apiKey, true);
			}

			for (MetaInfo metaInfo : savedMetaInfoList) {
				if (!metaInfo.isBlocked()) {
					updateProfessoriClassi(metaInfo);
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
