package it.smartcommunitylab.cartella.asl.services;

import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.Azienda;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;
import it.smartcommunitylab.cartella.asl.model.Istituzione;
import it.smartcommunitylab.cartella.asl.model.Studente;
import it.smartcommunitylab.cartella.asl.model.ext.AlignEsperienza;
import it.smartcommunitylab.cartella.asl.repository.AttivitaAlternanzaRepository;
import it.smartcommunitylab.cartella.asl.repository.AziendaRepository;
import it.smartcommunitylab.cartella.asl.repository.IstituzioneRepository;
import it.smartcommunitylab.cartella.asl.repository.PresenzaGiornaliereRepository;
import it.smartcommunitylab.cartella.asl.repository.StudenteRepository;
import it.smartcommunitylab.cartella.asl.util.ErrorLabelManager;
import it.smartcommunitylab.cartella.asl.util.HttpsUtils;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Service
public class InfoTNAlignExpService {

	private static final transient Log logger = LogFactory.getLog(InfoTNAlignExpService.class);

	@Value("${infoTN.esAlignUri}")
	private String infoTNAPIUrl;
	@Value("${infoTN.esAlignToken}")
	private String token;
	@Value("${infoTN.esAlignOrigine}")
	private String sistemaOrigine;
	@Autowired
	private IstituzioneRepository istitutoRepository;
	@Autowired
	private AttivitaAlternanzaRepository attivitaAlternanzaRepository;
	@Autowired
	private StudenteRepository studenteRepository;
	@Autowired
	private PresenzaGiornaliereRepository presenzaGiornaliereRepository;
	@Autowired
	private AziendaRepository aziendaRepository;
	@Autowired
	private HttpsUtils httpsUtils;
	@Autowired
	private ErrorLabelManager errorLabelManager;

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
	private ObjectMapper mapper = new ObjectMapper();

	Map<String, String> infoTNAslCodDesr = Stream.of(new AbstractMap.SimpleEntry<>("10", "Tirocinio curriculare"),
			new AbstractMap.SimpleEntry<>("11", "Tirocinio estivo retribuito (L.P. 19 16/06/83)"),
			new AbstractMap.SimpleEntry<>("20", "Commessa esterna"),
			new AbstractMap.SimpleEntry<>("25", "Visita aziendale"),
			new AbstractMap.SimpleEntry<>("30", "Impresa formativa simulata/Cooperativa Formativa Scolastica"),
			new AbstractMap.SimpleEntry<>("35", "Attività sportiva"),
			new AbstractMap.SimpleEntry<>("40", "Anno all'estero"),
			new AbstractMap.SimpleEntry<>("50", "Lavoro retribuito"),
			new AbstractMap.SimpleEntry<>("55", "Volontariato"), new AbstractMap.SimpleEntry<>("60", "Formazione"),
			new AbstractMap.SimpleEntry<>("65", "Testimonianze"),
			new AbstractMap.SimpleEntry<>("70", "Elaborazione esperienze/project work"))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

	Map<String, String> infoTNAslAS = Stream
			.of(new AbstractMap.SimpleEntry<>("10", "A"), new AbstractMap.SimpleEntry<>("11", "A"),
					new AbstractMap.SimpleEntry<>("20", "A"), new AbstractMap.SimpleEntry<>("25", "A"),
					new AbstractMap.SimpleEntry<>("30", "A"), new AbstractMap.SimpleEntry<>("35", "A"),
					new AbstractMap.SimpleEntry<>("40", "A"), new AbstractMap.SimpleEntry<>("50", "A"),
					new AbstractMap.SimpleEntry<>("55", "A"), new AbstractMap.SimpleEntry<>("60", "S"),
					new AbstractMap.SimpleEntry<>("65", "S"), new AbstractMap.SimpleEntry<>("70", "S"))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

	Map<String, String> fbkToInfoTNASL = Stream
			.of(new AbstractMap.SimpleEntry<>("7", "10"), new AbstractMap.SimpleEntry<>("10", "11"),
					new AbstractMap.SimpleEntry<>("8", "20"), new AbstractMap.SimpleEntry<>("6", "25"),
					new AbstractMap.SimpleEntry<>("5", "30"), new AbstractMap.SimpleEntry<>("9", "35"),
					new AbstractMap.SimpleEntry<>("4", "40"), new AbstractMap.SimpleEntry<>("11", "50"),
					new AbstractMap.SimpleEntry<>("12", "55"), new AbstractMap.SimpleEntry<>("2", "60"),
					new AbstractMap.SimpleEntry<>("1", "65"), new AbstractMap.SimpleEntry<>("3", "70"))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

	public String alignEsperienza(EsperienzaSvolta es) throws Exception {
		logger.info("call align experience service infoTN");
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String url = infoTNAPIUrl;
		
		AttivitaAlternanza aa = attivitaAlternanzaRepository.getOne(es.getAttivitaAlternanzaId());
		if (aa == null) {
			throw new BadRequestException(errorLabelManager.get("attivita.alt.error.notfound"));
		}
		
		Studente studente = studenteRepository.getOne(es.getStudenteId());
		if (studente == null) {
			throw new BadRequestException(errorLabelManager.get("studente.notfound"));
		}
		
		String mappedCodeASL = fbkToInfoTNASL.get(String.valueOf(aa.getTipologia()));

		AlignEsperienza ae = new AlignEsperienza();
		ae.setSistemaOrigine(sistemaOrigine);
		ae.setCodiceOrigine(es.getId());
		ae.setCodiceMiurUnitaScolastica(es.getCodiceMiur());
		ae.setAnnoScolastico(getSchoolYear(aa.getAnnoScolastico()));
		ae.setCodiceFiscaleStudente(es.getCfStudente());
		ae.setCognomeStudente(studente.getSurname());
		ae.setNomeStudente(studente.getName());
		ae.setDataDiNascitaStudente(studente.getBirthdate());
		ae.setCodiceTipoEsperienzaASL(Long.valueOf(mappedCodeASL));
		ae.setDescTipoEsperienzaASL(infoTNAslCodDesr.get(mappedCodeASL));
		ae.setDenominazioneEsperienzaASL(aa.getTitolo());
		ae.setNumeroOreEsperienzaASL(aa.getOre());
		ae.setDataInizioEsperienzaASL(aa.getDataInizio().format(formatter));
		ae.setDataFineEsperienzaASL(aa.getDataFine().format(formatter));
		ae.setEsperienzaASLAziendaScuola(infoTNAslAS.get(mappedCodeASL));
		ae.setTutorInternoEsperienzaASL(aa.getReferenteScuola());
		ae.setNumeroOreFrequentateStudente(String.valueOf(presenzaGiornaliereRepository.getOreValidateByEsperienzaId(es.getId())));
		/**
		 * Fix for tackling topologies
		 * (Anno all'estero | Impresa formativa simulata/Cooperativa Formativa Scolastica)
		 **/
		if (aa.getTipologia() == 5 || aa.getTipologia() == 4) {
			logger.info(infoTNAslCodDesr.get(mappedCodeASL));
			Istituzione ist = istitutoRepository.getOne(es.getIstitutoId());
			ae.setDenominazioneAzienda(ist.getName());
			ae.setCodiceTipoAzienda(20); // pubblica amministrazione
			ae.setTipoAzienda(ist.getName());
			ae.setPartitaIVAAzienda(ist.getCf());
			ae.setTutorEsternoEsperienzaASL(aa.getReferenteScuola());
		} else if (Utils.isNotEmpty(aa.getEnteId())) { // external.
			Azienda azienda = aziendaRepository.getOne(aa.getEnteId());
			if(azienda != null) {
				if (azienda.getIdTipoAzienda() > 0) {
					ae.setDenominazioneAzienda(azienda.getNome());
					ae.setCodiceTipoAzienda(azienda.getIdTipoAzienda());
					ae.setTipoAzienda(azienda.getNome());
					ae.setPartitaIVAAzienda(azienda.getPartita_iva());
					// obligatory while defining opportunity.
					ae.setTutorEsternoEsperienzaASL(aa.getReferenteEsterno());
					//TODO fix p_iva estera
					if(azienda.getPartita_iva().length() < 11) {
						ae.setInItalia(false);
					}
				} else {
					throw new BadRequestException(errorLabelManager.get("codice.tipo.azienda.error.notfound"));
				}
			}
		}

		String json = mapper.writeValueAsString(ae);
		// logger.info("token" + token);
		// logger.info("url" + url);
		logger.info("body" + json);

		return httpsUtils.sendPOSTSAA(url, "application/json", "application/json", token, json);

	}

	private String getSchoolYear(String annoScolastico) {
		return annoScolastico.replace("-", "/");
	}

}
