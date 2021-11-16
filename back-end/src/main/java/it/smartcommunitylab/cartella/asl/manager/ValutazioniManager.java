package it.smartcommunitylab.cartella.asl.manager;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza.Stati;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;
import it.smartcommunitylab.cartella.asl.model.ValutazioneAttivita;
import it.smartcommunitylab.cartella.asl.model.report.ValutazioneAttivitaReport;
import it.smartcommunitylab.cartella.asl.model.report.ValutazioneAttivitaReport.Stato;
import it.smartcommunitylab.cartella.asl.repository.AttivitaAlternanzaRepository;
import it.smartcommunitylab.cartella.asl.repository.EsperienzaSvoltaRepository;
import it.smartcommunitylab.cartella.asl.repository.ValutazioneAttivitaRepository;

@Repository
@Transactional
public class ValutazioniManager extends DataEntityManager {
	@Autowired
	ValutazioneAttivitaRepository valutazioneAttivitaRepository;
	
	@Autowired
	AttivitaAlternanzaRepository attivitaAlternanzaRepository;
	
	@Autowired
	EsperienzaSvoltaRepository esperienzaSvoltaRepository;
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	private final int risposteChiuse = 13;
	
	private ObjectMapper mapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	
	private ValutazioneAttivitaReport getValutazioneAttivitaReport(Long esperienzaSvoltaId) throws Exception {
		List<ValutazioneAttivita> valutazioni = valutazioneAttivitaRepository.findByEsperienzaSvoltaIdOrderByPosizioneAsc(esperienzaSvoltaId);
		ValutazioneAttivitaReport report = new ValutazioneAttivitaReport();
		int count = countDomandeCompilate(valutazioni);
		if(count == 0) {
			report.setStato(Stato.non_compilata);
		} else if(count == risposteChiuse) {
			report.setStato(Stato.compilata);
		} else {
			report.setStato(Stato.incompleta);
		}
		if(valutazioni.size() > 0) {
			report.setUltimaModifica(valutazioni.get(0).getUltimaModifica());
		}
		if(valutazioni.size() == 0) {
			report.setMedia("0.0");
			report.setValutazioni(getEmptyValutazioneAttivita());
		} else {
			report.setMedia(getMedia(valutazioni));
			report.setValutazioni(valutazioni);	
		}
		return report;
	}
	
	public ValutazioneAttivitaReport getValutazioneAttivitaReportByIstituto(Long esperienzaSvoltaId, String istitutoId) throws Exception {
		EsperienzaSvolta es = esperienzaSvoltaRepository.findById(esperienzaSvoltaId).orElse(null);
		if(es == null) {
			throw new BadRequestException("esperienza non trovata");
		}
		if(!es.getIstitutoId().equals(istitutoId)) {
			throw new BadRequestException("istituto non autorizzato");
		}
		return getValutazioneAttivitaReport(esperienzaSvoltaId);
	}
	
	public ValutazioneAttivitaReport getValutazioneAttivitaReportByStudente(Long esperienzaSvoltaId, String studenteId) throws Exception {
		EsperienzaSvolta es = esperienzaSvoltaRepository.findById(esperienzaSvoltaId).orElse(null);
		if(es == null) {
			throw new BadRequestException("esperienza non trovata");
		}
		if(!es.getStudenteId().equals(studenteId)) {
			throw new BadRequestException("utente non autorizzato");
		}
		return getValutazioneAttivitaReport(esperienzaSvoltaId);
	}
	
	public ValutazioneAttivitaReport saveValutazioneAttivita(String studenteId, Long esperienzaSvoltaId, 
			List<ValutazioneAttivita> valutazioni) throws Exception {
		EsperienzaSvolta es = esperienzaSvoltaRepository.findById(esperienzaSvoltaId).orElse(null);
		if(es == null) {
			throw new BadRequestException("esperienza non trovata");
		}
		if(!es.getStudenteId().equals(studenteId)) {
			throw new BadRequestException("utente non autorizzato");
		}
		AttivitaAlternanza aa = attivitaAlternanzaRepository.findById(es.getAttivitaAlternanzaId()).orElse(null);
		if(aa == null) {
			throw new BadRequestException("attività non trovata");
		}
		if(aa.getStato().equals(Stati.archiviata)) {
			throw new BadRequestException("attività archiviata");
		}
		String istitutoId = es.getIstitutoId();
		Long attivitaAlternanzaId = es.getAttivitaAlternanzaId();
		LocalDate ultimaModifica = LocalDate.now();
		for(ValutazioneAttivita va : valutazioni) {
			va.setAttivitaAlternanzaId(attivitaAlternanzaId);
			va.setIstitutoId(istitutoId);
			va.setEsperienzaSvoltaId(esperienzaSvoltaId);
			va.setStudenteId(studenteId);
			va.setUltimaModifica(ultimaModifica);
			valutazioneAttivitaRepository.save(va);
		}
		return getValutazioneAttivitaReport(esperienzaSvoltaId);
	}
	
	private int countDomandeCompilate(List<ValutazioneAttivita> valutazioni) {
		int count = 0;
		for(ValutazioneAttivita va : valutazioni) {
			if(va.getRispostaChiusa() && (va.getPunteggio() > 0)) {
				count++;
			}			
		}
		return count;
	}
	
	private String getMedia(List<ValutazioneAttivita> valutazioni) {
		int punteggio = 0;
		int numRisposte = 0;
		for(ValutazioneAttivita va : valutazioni) {
			if(va.getRispostaChiusa() && (va.getPunteggio() > 0)) {
				numRisposte++;
				punteggio = punteggio + va.getPunteggio();
			}
		}
		DecimalFormat df = new DecimalFormat("#.#");
		df.setRoundingMode(RoundingMode.CEILING);
		return df.format((double)punteggio / numRisposte);
	}
	
	private List<ValutazioneAttivita> getEmptyValutazioneAttivita() throws Exception {
		Resource resource = resourceLoader.getResource("classpath:dataset/valutazioneAttivita.json");
		List<ValutazioneAttivita> valutazioni = mapper.readValue(resource.getInputStream(), new TypeReference<List<ValutazioneAttivita>>() {});
		return valutazioni;
	}
}
