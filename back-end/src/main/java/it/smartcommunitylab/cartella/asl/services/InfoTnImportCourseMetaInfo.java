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
import it.smartcommunitylab.cartella.asl.model.CorsoMetaInfo;
import it.smartcommunitylab.cartella.asl.model.MetaInfo;
import it.smartcommunitylab.cartella.asl.model.ext.infotn.CorsoMetaInfoInfoTn;
import it.smartcommunitylab.cartella.asl.repository.CorsoMetaInfoRepository;
import it.smartcommunitylab.cartella.asl.util.Constants;
import it.smartcommunitylab.cartella.asl.util.HttpsUtils;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Service
public class InfoTnImportCourseMetaInfo {

	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportCourseMetaInfo.class);

	@Value("${infoTN.api}")
	private String infoTNAPIUrl;
	@Value("${infoTN.startingYear}")
	private int startingYear;
	@Value("${infoTN.user}")
	private String user;
	@Value("${infoTN.pass}")
	private String pass;

	private String apiKey = Constants.API_COURSE_METAINFO_KEY;
	private String auth;

	@Autowired
	private APIUpdateManager apiUpdateManager;
	
	@Autowired
	CorsoMetaInfoRepository corsoMetaInfoRepository;
	
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

	public void updateCourseMetaInfo(MetaInfo metaInfo) throws Exception {
		logger.info("start importCourseMetaInfoFromRESTAPI");
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		if (metaInfo.getEpocTimestamp() < 0) {
			metaInfo.setEpocTimestamp(System.currentTimeMillis()); //set it for the first time.
		}
		String url = infoTNAPIUrl + "/corsi";
		int stored = 0;
		int total = 0;

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
			}
			while (jp.nextToken() != JsonToken.END_ARRAY) {
				total += 1;
				CorsoMetaInfoInfoTn temp = jp.readValueAs(CorsoMetaInfoInfoTn.class);
				if(temp.getYears() != 4) {
					temp.setYears(5);
				}
				logger.info("processing " + temp.getExtId());
				CorsoMetaInfo corsoMetaInfo = corsoMetaInfoRepository.findByExtId(temp.getExtId(), temp.getOrigin());
				if (corsoMetaInfo != null) {
					if(corsoMetaInfo.getYears() == null) {
						corsoMetaInfo.setYears(temp.getYears());
						corsoMetaInfoRepository.save(corsoMetaInfo);
					}
					logger.info(String.format("CMI already exists: %s - %s", corsoMetaInfo.getOrigin(),
							corsoMetaInfo.getExtId()));
					continue;
				}
				CorsoMetaInfo tobeSaved = convertToCourse(temp);
				corsoMetaInfoRepository.save(tobeSaved);
				stored += 1;

			}

			// update time stamp (if all works fine).
			metaInfo.setEpocTimestamp(metaInfo.getEpocTimestamp() + 1);
			metaInfo.setTotalRead(total);
			metaInfo.setTotalStore(stored);
		}

	}

	private CorsoMetaInfo convertToCourse(CorsoMetaInfoInfoTn corso) throws ParseException {
		CorsoMetaInfo result = new CorsoMetaInfo();
		result.setOrigin(corso.getOrigin());
		result.setExtId(corso.getExtId());
		result.setCourse(corso.getCourse());
		result.setYears(corso.getYears());
		result.setId(Utils.getUUID());
		result.setCodMiur(corso.getCodMiur());
		return result;
	}

	public String importCourseMetaInfoFromRESTAPI() {
		try {
			List<MetaInfo> savedMetaInfoList = apiUpdateManager.fetchMetaInfoForAPI(apiKey);

			if (savedMetaInfoList == null || savedMetaInfoList.isEmpty()) {
				// call generic method to create metaInfos (apiKey, year?)
				savedMetaInfoList = apiUpdateManager.createMetaInfoForAPI(apiKey, false);
			}

			for (MetaInfo metaInfo : savedMetaInfoList) {
				if (!metaInfo.isBlocked()) {
					updateCourseMetaInfo(metaInfo);
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
