package it.smartcommunitylab.cartella.asl.services;

import java.util.List;

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
import it.smartcommunitylab.cartella.asl.model.Azienda;
import it.smartcommunitylab.cartella.asl.model.MetaInfo;
import it.smartcommunitylab.cartella.asl.model.ext.infotn.AziendaInfoTn;
import it.smartcommunitylab.cartella.asl.repository.AziendaRepository;
import it.smartcommunitylab.cartella.asl.util.Constants;
import it.smartcommunitylab.cartella.asl.util.HttpsUtils;
import it.smartcommunitylab.cartella.asl.util.Utils;


@Service
public class InfoTnImportAziende {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnImportAziende.class);

	@Value("${infoTN.api}")
	private String infoTNAPIUrl;
	@Value("${infoTN.startingYear}")
	private int startingYear;
	@Value("${infoTN.user}")
	private String user;
	@Value("${infoTN.pass}")
	private String pass;

	private String apiKey = Constants.API_AZIENDE_KEY;
	private String auth;

	@Autowired
	private APIUpdateManager apiUpdateManager;
	@Autowired
	private HttpsUtils httpsUtils;	
	@Autowired
	private AziendaRepository aziendaRepository;

	public void updateAzienda(MetaInfo metaInfo) throws Exception {
		logger.info("start importAziendaFromRESTAPI");
		int total = 0;
		int stored = 0;
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url;

		// read epoc timestamp from db(if exist)
		if (metaInfo.getEpocTimestamp() > 0) {
			url = infoTNAPIUrl + "/azienda?timestamp=" + metaInfo.getEpocTimestamp();
		} else {
			metaInfo.setEpocTimestamp(System.currentTimeMillis()); //set it for the first time.
			url = infoTNAPIUrl + "/azienda";
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
				AziendaInfoTn azienda = jp.readValueAs(AziendaInfoTn.class);
				
				if (Utils.isEmpty(azienda.getPartita_iva())) {
					logger.warn("Certifier without piva");
					continue;
				}
				
				Azienda aziendaDb = aziendaRepository.findByExtIdAndOrigin(azienda.getExtId(), azienda.getOrigin());
				if (aziendaDb != null) {
					logger.warn(String.format("Azienda already exists: %s - %s", azienda.getOrigin(),
							azienda.getExtId()));
					if((aziendaDb.getIdTipoAzienda() == 0) && (azienda.getIdTipoAzienda() > 0)) {
						aziendaDb.setIdTipoAzienda(azienda.getIdTipoAzienda());
						aziendaRepository.save(aziendaDb);
					}
					continue;
				}
				List<Azienda> list = aziendaRepository.findByPartitaIva(azienda.getPartita_iva());
				if (!list.isEmpty()) {
					logger.warn(String.format("Azienda already exists: %s", azienda.getPartita_iva()));
					continue;
				}
				
				logger.info("converting " + azienda.getExtId());
				Azienda newAzienda = convertToAzienda(azienda);
				aziendaRepository.save(newAzienda);
				stored += 1;
				logger.info(String.format("Save Azienda: %s - %s - %s", newAzienda.getOrigin(), newAzienda.getExtId(),
						newAzienda.getId()));
			}

			// update time stamp (if all works fine).
			metaInfo.setEpocTimestamp(metaInfo.getEpocTimestamp() + 1);
			metaInfo.setTotalRead(total);
			metaInfo.setTotalStore(stored);
		}

	}

	private Azienda convertToAzienda(AziendaInfoTn azienda) {
		Azienda result = new Azienda();
		result.setOrigin(azienda.getOrigin());
		result.setExtId(azienda.getExtId());
		result.setId(Utils.getUUID());
		result.setPartita_iva(azienda.getPartita_iva());
		result.setNome(getMaxString(azienda.getDescription()));
		result.setAddress(getMaxString(azienda.getAddress()));
		result.setDescription(getMaxString(azienda.getDescription()));
		result.setEmail(azienda.getEmail());
		result.setPec(azienda.getPec());
		result.setPhone(azienda.getPhone());
		result.setBusinessName(getMaxString(azienda.getName()));
		result.setIdTipoAzienda(azienda.getIdTipoAzienda());
		return result;
	}
	
	private String getMaxString(String content) {
		String result = "";
		if(Utils.isNotEmpty(content)) {
			if(content.length() > 1024) {
				result = content.substring(0, 1024);
			} else {
				result = content;
			}
		}
		return result;
	}

	public String importAziendaFromRESTAPI() {
		try {
			List<MetaInfo> savedMetaInfoList = apiUpdateManager.fetchMetaInfoForAPI(apiKey);

			if (savedMetaInfoList == null || savedMetaInfoList.isEmpty()) {
				// call generic method to create metaInfos (apiKey, year?)
				savedMetaInfoList = apiUpdateManager.createMetaInfoForAPI(apiKey, false);
			}

			for (MetaInfo metaInfo : savedMetaInfoList) {
				if (!metaInfo.isBlocked()) {
					updateAzienda(metaInfo);
				}
			}

			apiUpdateManager.saveMetaInfoList(apiKey, savedMetaInfoList);

			return "OK";

		} catch (Exception e) {
			logger.error(e.getMessage());
			return e.getMessage();
		}

	}

	// public String importAziendeFromEmpty() throws Exception {
	// logger.info("start importAziendeFromEmpty");
	// int total = 0;
	// int stored = 0;
	// FileReader fileReader = new FileReader(sourceFolder + "FBK_COMPANY
	// v.02.json");
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
	// Azienda azienda = jp.readValueAs(Azienda.class);
	// logger.info("converting " + azienda.getExtid());
	// Certifier certifierDb =
	// certifierRepository.findByExtId(azienda.getOrigin(),
	// azienda.getExtid());
	// if (certifierDb != null) {
	// logger.warn(String.format("Certifier already exists: %s - %s",
	// azienda.getOrigin(),
	// azienda.getExtid()));
	// continue;
	// }
	// Certifier certifier = convertToCertifier(azienda);
	// certifierRepository.save(certifier);
	// stored += 1;
	// logger.info(String.format("Save TeachingUnit: %s - %s - %s",
	// azienda.getOrigin(),
	// azienda.getExtid(), certifier.getId()));
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
