package it.smartcommunitylab.cartella.asl.services;

import java.util.ArrayList;
import java.util.List;
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
import it.smartcommunitylab.cartella.asl.model.MetaInfo;
import it.smartcommunitylab.cartella.asl.model.ProfessoriClassi;
import it.smartcommunitylab.cartella.asl.model.ReferenteAlternanza;
import it.smartcommunitylab.cartella.asl.model.ext.Professor;
import it.smartcommunitylab.cartella.asl.repository.ProfessoriClassiRepository;
import it.smartcommunitylab.cartella.asl.repository.ReferenteAlternanzaRepository;
import it.smartcommunitylab.cartella.asl.util.Constants;
import it.smartcommunitylab.cartella.asl.util.HttpsUtils;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Service
public class InfoTnImportProfessori {

	private static final transient Log logger = LogFactory.getLog(InfoTnImportProfessori.class);

	@Value("${infoTN.api}")
	private String infoTNAPIUrl;
	@Value("${infoTN.startingYear}")
	private int startingYear;
	@Value("${infoTN.user}")
	private String user;
	@Value("${infoTN.pass}")
	private String pass;

	private String apiKey = Constants.API_PROFESSORI_KEY;
	private String auth;

	@Autowired
	private APIUpdateManager apiUpdateManager;
	@Autowired
	private ReferenteAlternanzaRepository referenteAlternanzaRepository;
	@Autowired
	private ProfessoriClassiRepository professoriClassiRepository;
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

	public void updateProfessori(MetaInfo metaInfo) throws Exception {
		
		logger.info("start importProfessoriFromRESTAPI");
		int total = 0;
		int stored = 0;
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url;

		if (metaInfo.getEpocTimestamp() > 0) {
			url = infoTNAPIUrl + "/professori";
			//url = infoTNAPIUrl + "/professori?timestamp=" + metaInfo.getEpocTimestamp();
		} else {
			metaInfo.setEpocTimestamp(System.currentTimeMillis()); //set it for the first time.
			url = infoTNAPIUrl + "/professori";
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

			List<ReferenteAlternanza> tobeSaved = new ArrayList<ReferenteAlternanza>();
			while (jp.nextToken() != JsonToken.END_ARRAY) {
				total += 1;
				it.smartcommunitylab.cartella.asl.model.ext.Professor professorExt = jp
						.readValueAs(it.smartcommunitylab.cartella.asl.model.ext.Professor.class);
				//TODO test
				professorExt.setOrigin("INFOTNISTRUZIONE");
				// save.
				ReferenteAlternanza professorLocal = referenteAlternanzaRepository
						.findReferenteAlternanzaByExtId(professorExt.getExtId());
				if (professorLocal != null) {
					logger.info("existing Professor: " + professorExt.getExtId());
					if(Utils.isNotEmpty(professorExt.getEmail())) {
						String email = professorExt.getEmail().trim();
						if(Utils.isEmpty(professorLocal.getEmail()) || (!professorLocal.getEmail().equals(email))) {
							professorLocal.setEmail(email);
							referenteAlternanzaRepository.save(professorLocal);							
						}
					}
					continue;
				} 
				List<ProfessoriClassi> classi = professoriClassiRepository.findByTeacherExtId(professorExt.getExtId());
				if(classi.size() == 0) {
					continue;
				}
				logger.info("converting " + professorExt.getExtId());
				ReferenteAlternanza ra = convertToLocalReferenteAlternanzaBean(professorExt);
				tobeSaved.add(ra);
				// update professori-classi
				for(ProfessoriClassi pc : classi) {
					pc.setReferenteAlternanzaId(ra.getId());
					professoriClassiRepository.save(pc);
				}
			}

			stored = tobeSaved.size();
			referenteAlternanzaRepository.saveAll(tobeSaved);

			// update time stamp (if all works fine).
			metaInfo.setEpocTimestamp(metaInfo.getEpocTimestamp() + 1);
			metaInfo.setTotalRead(total);
			metaInfo.setTotalStore(stored);

		}

	}

	private ReferenteAlternanza convertToLocalReferenteAlternanzaBean(Professor professorExt) {
		ReferenteAlternanza result = new ReferenteAlternanza();
		result.setId(UUID.randomUUID().toString());
		result.setCf(professorExt.getCf());
		if (Utils.isNotEmpty(professorExt.getEmail())) {
			result.setEmail(professorExt.getEmail().trim());
		}
		result.setExtId(professorExt.getExtId());
		result.setIndirizzo(professorExt.getIndirizzo());
		result.setName(professorExt.getName());
		result.setOrigin(professorExt.getOrigin());
		result.setSurname(professorExt.getSurname());
		return result;
	}

	public String importProfessoriFromRESTAPI() {
		try {
			List<MetaInfo> savedMetaInfoList = apiUpdateManager.fetchMetaInfoForAPI(apiKey);

			if (savedMetaInfoList == null || savedMetaInfoList.isEmpty()) {
				// call generic method to create metaInfos (apiKey, year?)
				savedMetaInfoList = apiUpdateManager.createMetaInfoForAPI(apiKey, false);
			}

			for (MetaInfo metaInfo : savedMetaInfoList) {
				if (!metaInfo.isBlocked()) {
					updateProfessori(metaInfo);
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
