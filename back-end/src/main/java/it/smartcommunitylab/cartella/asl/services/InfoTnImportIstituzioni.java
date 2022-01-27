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
import it.smartcommunitylab.cartella.asl.model.ext.infotn.IstituzioneInfoTn;
import it.smartcommunitylab.cartella.asl.repository.IstituzioneRepository;
import it.smartcommunitylab.cartella.asl.util.Constants;
import it.smartcommunitylab.cartella.asl.util.HttpsUtils;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Service
public class InfoTnImportIstituzioni {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportIstituzioni.class);

	@Autowired
	private APIUpdateManager apiUpdateManager;

	@Autowired
	private IstituzioneRepository istituzioneRepository;
	
	@Autowired
	private HttpsUtils httpsUtils;
	
	@Value("${infoTN.api}")
	private String infoTNAPIUrl;
	@Value("${infoTN.startingYear}")
	private int startingYear;
	@Value("${infoTN.user}")
	private String user;
	@Value("${infoTN.pass}")
	private String pass;

	private String apiKey = Constants.API_ISTITUTI_KEY;
	private String auth;

	@PostConstruct()
	public void init() {
		if (Utils.isNotEmpty(user) && Utils.isNotEmpty(pass)) {
			String authString = user + ":" + pass;
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			auth = "Basic " + new String(authEncBytes);
		}	
	}

	private void updateIstitute(MetaInfo metaInfo) throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url;
		int total = 0;
		int stored = 0;

		if (metaInfo.getEpocTimestamp() > 0) {
			url = infoTNAPIUrl + "/istituti?timestamp=" + metaInfo.getEpocTimestamp();
		} else {
			metaInfo.setEpocTimestamp(System.currentTimeMillis()); //set it for the first time.
			url = infoTNAPIUrl + "/istituti";
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
				IstituzioneInfoTn istituzioneInfoTn = jp.readValueAs(IstituzioneInfoTn.class);
				logger.info("converting " + istituzioneInfoTn.getExtId());
				Istituzione instituteDb = istituzioneRepository.findIstitutzioneByExtId(istituzioneInfoTn.getOrigin(), 
						istituzioneInfoTn.getExtId());
				if (instituteDb != null) {
					logger.info(String.format("Institute already exists: %s - %s", istituzioneInfoTn.getOrigin(),
							istituzioneInfoTn.getExtId()));
					// update CF as it was introduced in later stages of project.
					if (Utils.isEmpty(instituteDb.getCf())) {
						instituteDb.setCf(istituzioneInfoTn.getCf());
						istituzioneRepository.save(instituteDb);	
					}	
					// update rdp infos
					if(Utils.isEmpty(instituteDb.getRdpName())) {
						instituteDb.setRdpName(istituzioneInfoTn.getRdpName());
						istituzioneRepository.save(instituteDb);
					}
					if(Utils.isEmpty(instituteDb.getRdpEmail())) {
						instituteDb.setRdpEmail(istituzioneInfoTn.getRdpEmail());
						istituzioneRepository.save(instituteDb);
					}
					if(Utils.isEmpty(instituteDb.getRdpAddress())) {
						instituteDb.setRdpAddress(istituzioneInfoTn.getRdpAddress());
						istituzioneRepository.save(instituteDb);
					}
					if(Utils.isEmpty(instituteDb.getRdpPhoneFax())) {
						instituteDb.setRdpPhoneFax(istituzioneInfoTn.getRdpPhoneFax());
						istituzioneRepository.save(instituteDb);
					}
					continue;
				}
				Istituzione institute = convertToInstitute(istituzioneInfoTn);
				istituzioneRepository.save(institute);
				stored += 1;
				logger.info(String.format("Save Institute: %s - %s - %s", institute.getOrigin(),
						institute.getExtId(), institute.getId()));
			}
			// update time stamp (if all works fine).
			metaInfo.setEpocTimestamp(metaInfo.getEpocTimestamp() + 1);
			metaInfo.setTotalRead(total);
			metaInfo.setTotalStore(stored);
		}

	}

	private Istituzione convertToInstitute(IstituzioneInfoTn istituzioneInfoTn) {
		Istituzione result = new Istituzione();
		result.setOrigin(istituzioneInfoTn.getOrigin());
		result.setExtId(istituzioneInfoTn.getExtId());
		result.setId(Utils.getUUID());
		result.setName(istituzioneInfoTn.getName());
		result.setAddress(istituzioneInfoTn.getAddress());
		result.setPhone(istituzioneInfoTn.getPhone());
		result.setPec(istituzioneInfoTn.getPec());
		result.setEmail(istituzioneInfoTn.getEmail());
		result.setCf(istituzioneInfoTn.getCf());
		result.setRdpName(istituzioneInfoTn.getRdpName());
		result.setRdpEmail(istituzioneInfoTn.getRdpEmail());
		result.setRdpAddress(istituzioneInfoTn.getRdpAddress());
		result.setRdpPhoneFax(istituzioneInfoTn.getRdpPhoneFax());
		return result;
	}

	public String importIstituzioniFromRESTAPI() {
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
				savedMetaInfoList = apiUpdateManager.createMetaInfoForAPI(apiKey, false);
			}

			for (MetaInfo metaInfo : savedMetaInfoList) {
				if (!metaInfo.isBlocked()) {
					updateIstitute(metaInfo);
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