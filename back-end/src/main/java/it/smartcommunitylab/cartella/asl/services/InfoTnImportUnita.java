package it.smartcommunitylab.cartella.asl.services;

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
import it.smartcommunitylab.cartella.asl.model.Istituzione;
import it.smartcommunitylab.cartella.asl.model.MetaInfo;
import it.smartcommunitylab.cartella.asl.model.TeachingUnit;
import it.smartcommunitylab.cartella.asl.model.ext.infotn.UnitaInfoTn;
import it.smartcommunitylab.cartella.asl.repository.IstituzioneRepository;
import it.smartcommunitylab.cartella.asl.repository.TeachingUnitRepository;
import it.smartcommunitylab.cartella.asl.util.Constants;
import it.smartcommunitylab.cartella.asl.util.HttpsUtils;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Service
public class InfoTnImportUnita {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportUnita.class);

	@Value("${infoTN.api}")
	private String infoTNAPIUrl;
	@Value("${infoTN.startingYear}")
	private int startingYear;
	@Value("${infoTN.user}")
	private String user;
	@Value("${infoTN.pass}")
	private String pass;

	private String apiKey = Constants.API_TEACHINGUNIT_KEY;
	private String auth;

	@Autowired
	private APIUpdateManager apiUpdateManager;

	@Autowired
	IstituzioneRepository istituzioneRepository;

	@Autowired
	TeachingUnitRepository teachingUnitRepository;
	
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
	
	public void updateUnita(MetaInfo metaInfo) throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url;
		int total = 0;
		int stored = 0;

		if (metaInfo.getEpocTimestamp() > 0) {
			url = infoTNAPIUrl + "/unita?timestamp=" + metaInfo.getEpocTimestamp();
		} else {
			metaInfo.setEpocTimestamp(System.currentTimeMillis()); //set it for the first time.
			url = infoTNAPIUrl + "/unita";
		}

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
				UnitaInfoTn unitaInfoTn = jp.readValueAs(UnitaInfoTn.class);
				logger.info("converting " + unitaInfoTn.getExtId());
				if (Utils.isNotEmpty(unitaInfoTn.getDateTo())) {
					logger.info(String.format("TU with date to: %s - %s", unitaInfoTn.getExtId(), unitaInfoTn.getDateTo()));
				}
				TeachingUnit teachingUnitDb = teachingUnitRepository.findByExtIdAndOrigin(unitaInfoTn.getExtId(), unitaInfoTn.getOrigin());
				if (teachingUnitDb != null) {
					logger.info(String.format("TU already exists: %s - %s", unitaInfoTn.getOrigin(), unitaInfoTn.getExtId()));
					if (Utils.isEmpty(teachingUnitDb.getCodiceMiur())) {
						teachingUnitDb.setCodiceMiur(unitaInfoTn.getTeachingUnit().getCodiceMiur());
						teachingUnitRepository.save(teachingUnitDb);
					}
					continue;
				}
				Istituzione instituteDb = istituzioneRepository.findIstitutzioneByExtId(unitaInfoTn.getInstituteRef().getOrigin(), 
						unitaInfoTn.getInstituteRef().getExtId());
				if (instituteDb == null) {
					logger.info(String.format("Institute not found: %s - %s", unitaInfoTn.getInstituteRef().getOrigin(),
							unitaInfoTn.getInstituteRef().getExtId()));
					continue;
				}
				TeachingUnit teachingUnit = convertToTeachingUnit(unitaInfoTn);
				teachingUnit.setInstituteId(instituteDb.getId());
				teachingUnitRepository.save(teachingUnit);
				stored += 1;
				logger.info(String.format("Save TeachingUnit: %s - %s - %s", unitaInfoTn.getOrigin(), unitaInfoTn.getExtId(),
						teachingUnit.getId()));

			}

			// update time stamp (if all works fine).
			metaInfo.setEpocTimestamp(metaInfo.getEpocTimestamp() + 1);
			metaInfo.setTotalRead(total);
			metaInfo.setTotalStore(stored);
		}

	}

	private TeachingUnit convertToTeachingUnit(UnitaInfoTn unita) {
		TeachingUnit result = new TeachingUnit();
		result.setOrigin(unita.getOrigin());
		result.setExtId(unita.getExtId());
		result.setId(Utils.getUUID());
		result.setName(unita.getTeachingUnit().getName());
		result.setDescription(unita.getTeachingUnit().getDescription());
		result.setAddress(unita.getTeachingUnit().getAddress());
		result.setCodiceMiur(unita.getTeachingUnit().getCodiceMiur());
		return result;
	}

	public String importUnitaFromRESTAPI() {
	
		try {
			List<MetaInfo> savedMetaInfoList = apiUpdateManager.fetchMetaInfoForAPI(apiKey);
			
			if (savedMetaInfoList == null || savedMetaInfoList.isEmpty()) {
				// call generic method to create metaInfos (apiKey, year?)
				savedMetaInfoList = apiUpdateManager.createMetaInfoForAPI(apiKey, false);
			}
			
			for (MetaInfo metaInfo : savedMetaInfoList) {
				if (!metaInfo.isBlocked()) {
					updateUnita(metaInfo);
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
