package it.smartcommunitylab.cartella.asl.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import it.smartcommunitylab.cartella.asl.model.MetaInfo;
import it.smartcommunitylab.cartella.asl.model.Studente;
import it.smartcommunitylab.cartella.asl.model.ext.infotn.StudenteInfoTn;
import it.smartcommunitylab.cartella.asl.repository.StudenteRepository;
import it.smartcommunitylab.cartella.asl.util.Constants;
import it.smartcommunitylab.cartella.asl.util.HttpsUtils;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Service
public class InfoTnImportStudenti {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportStudenti.class);

	@Value("${infoTN.api}")
	private String infoTNAPIUrl;
	@Value("${infoTN.startingYear}")
	private int startingYear;
	@Value("${infoTN.user}")
	private String user;
	@Value("${infoTN.pass}")
	private String pass;

	private String apiKey = Constants.API_STUDENTE_KEY;
	private String auth;

	@Autowired
	private APIUpdateManager apiUpdateManager;
	
	@Autowired
	StudenteRepository studenteRepository;
	
	@Autowired
	private HttpsUtils httpsUtils;

	SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy", Locale.ITALY);
	SimpleDateFormat sdfStandard = new SimpleDateFormat("dd/MM/yyyy");
	
	@PostConstruct()
	public void init() {
		if (Utils.isNotEmpty(user) && Utils.isNotEmpty(pass)) {
			String authString = user + ":" + pass;
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			auth = "Basic " + new String(authEncBytes);
		}	
	}

	private void updateStudenti(MetaInfo metaInfo) throws Exception {
		int total = 0;
		int stored = 0;
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url;
		int nextYear = metaInfo.getSchoolYear() + 1;
		String year = metaInfo.getSchoolYear() + "/" + String.valueOf(nextYear).substring(2);

		// read epoc timestamp from db(if exist)
		if (metaInfo.getEpocTimestamp() > 0) {
			url = infoTNAPIUrl + "/studenti?schoolYear=" + year + "&timestamp=" + metaInfo.getEpocTimestamp();
		} else {
			metaInfo.setEpocTimestamp(System.currentTimeMillis()); //set it for the first time.
			url = infoTNAPIUrl + "/studenti?schoolYear=" + year;
		}
		logger.info("start importStudentiUsingRESTAPI for year " + year);

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
				StudenteInfoTn studenteInfoTn = jp.readValueAs(StudenteInfoTn.class);
				logger.info("converting " + studenteInfoTn.getExtId());
				Studente studentDb = studenteRepository.findByExtIdAndOrigin(studenteInfoTn.getExtId(), studenteInfoTn.getOrigin());
				if (studentDb == null) {
					Studente studente = convertToStudent(studenteInfoTn);
					studenteRepository.save(studente);
					stored += 1;
					logger.info(String.format("Save Student: %s - %s - %s", studente.getOrigin(), studente.getExtId(),
							studente.getId()));
				} else {
					logger.warn(String.format("Student already exists: %s - %s", studenteInfoTn.getOrigin(), studenteInfoTn.getExtId()));
				}
			}
			// update time stamp (if all works fine).
			metaInfo.setEpocTimestamp(metaInfo.getEpocTimestamp() + 1);
			metaInfo.setTotalRead(total);
			metaInfo.setTotalStore(stored);
		}

	}

	private Studente convertToStudent(StudenteInfoTn studente) throws ParseException {
		Studente result = new Studente();
		result.setOrigin(studente.getOrigin());
		result.setExtId(studente.getExtId());
		result.setId(Utils.getUUID());
		result.setCf(studente.getCf());
		result.setName(studente.getName());
		result.setSurname(studente.getSurname());
		// result.setBirthdate(getBirthdate(studente.getBirthdate()));
		result.setBirthdate(studente.getBirthdate());
		result.setPhone(studente.getPhone());
		result.setEmail(studente.getEmail());
		return result;
	}

	@SuppressWarnings("unused")
	private String getAddress(StudenteInfoTn studente) {
		StringBuffer sb = new StringBuffer(studente.getAddress());
		sb.append(", ");
		sb.append(studente.getCap());
		sb.append(" ");
		sb.append(studente.getComune());
		return sb.toString();
	}

	@SuppressWarnings("unused")
	private String getBirthdate(String dataNascita) throws ParseException {
		Date date = sdf.parse(dataNascita);
		String result = sdfStandard.format(date);
		return result;
	}

	public String importStudentiFromRESTAPI() {
		try {
			List<MetaInfo> savedMetaInfoList = apiUpdateManager.fetchMetaInfoForAPI(apiKey);

			if (savedMetaInfoList == null || savedMetaInfoList.isEmpty()) {
				// call generic method to create metaInfos (apiKey, year?)
				savedMetaInfoList = apiUpdateManager.createMetaInfoForAPI(apiKey, true);
			}

			for (MetaInfo metaInfo : savedMetaInfoList) {
				if (!metaInfo.isBlocked()) {
					updateStudenti(metaInfo);
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
