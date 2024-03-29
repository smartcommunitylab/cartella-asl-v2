package it.smartcommunitylab.cartella.asl.manager;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza.Stati;
import it.smartcommunitylab.cartella.asl.model.Competenza;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;
import it.smartcommunitylab.cartella.asl.model.PresenzaGiornaliera;
import it.smartcommunitylab.cartella.asl.model.ValutazioneAttivita;
import it.smartcommunitylab.cartella.asl.model.ValutazioneCompetenza;
import it.smartcommunitylab.cartella.asl.model.report.ValutazioneAttivitaReport;
import it.smartcommunitylab.cartella.asl.model.report.ValutazioneCompetenzeReport;
import it.smartcommunitylab.cartella.asl.repository.AttivitaAlternanzaRepository;
import it.smartcommunitylab.cartella.asl.repository.EsperienzaSvoltaRepository;
import it.smartcommunitylab.cartella.asl.repository.PresenzaGiornaliereRepository;
import it.smartcommunitylab.cartella.asl.repository.ValutazioneAttivitaRepository;
import it.smartcommunitylab.cartella.asl.repository.ValutazioneCompetenzaRepository;

@Repository
@Transactional
public class ValutazioniManager extends DataEntityManager {
	@Autowired
	ValutazioneAttivitaRepository valutazioneAttivitaRepository;
	
	@Autowired
	ValutazioneCompetenzaRepository valutazioneCompetenzaRepository;
	
	@Autowired
	AttivitaAlternanzaRepository attivitaAlternanzaRepository;
	
	@Autowired
	EsperienzaSvoltaRepository esperienzaSvoltaRepository;
	
	@Autowired
	private PresenzaGiornaliereRepository presenzeRepository;
	
	@Autowired
	CompetenzaManager competenzaManager;
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	private final int risposteChiuse = 13;
	
	private ObjectMapper mapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	
	private ValutazioneAttivitaReport getValutazioneAttivitaReport(EsperienzaSvolta es, boolean perIstituto) throws Exception {
		List<ValutazioneAttivita> valutazioni = valutazioneAttivitaRepository.findByEsperienzaSvoltaIdOrderByPosizioneAsc(es.getId());
		ValutazioneAttivitaReport report = new ValutazioneAttivitaReport();
		int count = countDomandeCompilate(valutazioni);
		if(count == 0) {
			report.setStato(ValutazioneAttivitaReport.Stato.non_compilata);
		} else if(count == risposteChiuse) {
			report.setStato(ValutazioneAttivitaReport.Stato.compilata);
		} else {
			report.setStato(ValutazioneAttivitaReport.Stato.incompleta);
		}
		if(valutazioni.size() > 0) {
			//TODO cercare ultima modifica più recente
			report.setUltimaModifica(valutazioni.get(0).getUltimaModifica());
		}
		if(valutazioni.size() == 0) {
			report.setMedia("0.0");
			report.setValutazioni(getEmptyValutazioneAttivita());
		} else {
			report.setMedia(getMedia(valutazioni));
			report.setValutazioni(valutazioni);	
		}
		if(perIstituto) {
			AttivitaAlternanza aa = attivitaAlternanzaRepository.findById(es.getAttivitaAlternanzaId()).orElse(null);
			if(aa == null) {
				throw new BadRequestException("attività non trovata");
			}
			report.setDataInizio(aa.getDataInizio());
			report.setDataFine(aa.getDataFine());
			report.setOre(aa.getOre());
			int oreInserite = 0;
			List<PresenzaGiornaliera> presenze = presenzeRepository.findByEsperienzaSvoltaId(es.getId(), Sort.by(Sort.Direction.DESC, "giornata"));
			for(PresenzaGiornaliera p : presenze) {
				oreInserite += p.getOreSvolte();
			}
			report.setOreInserite(oreInserite);
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
		return getValutazioneAttivitaReport(es, true);
	}
	
	public ValutazioneAttivitaReport getValutazioneAttivitaReportByStudente(Long esperienzaSvoltaId, String studenteId) throws Exception {
		EsperienzaSvolta es = esperienzaSvoltaRepository.findById(esperienzaSvoltaId).orElse(null);
		if(es == null) {
			throw new BadRequestException("esperienza non trovata");
		}
		if(!es.getStudenteId().equals(studenteId)) {
			throw new BadRequestException("utente non autorizzato");
		}
		return getValutazioneAttivitaReport(es, false);
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
		return getValutazioneAttivitaReport(es, false);
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
	
	private ValutazioneCompetenzeReport getValutazioneCompetenzeReport(EsperienzaSvolta es, boolean perIstituto) throws Exception {
		List<ValutazioneCompetenza> valutazioni = valutazioneCompetenzaRepository.findByEsperienzaSvoltaIdOrderByOrdineAsc(es.getId());
		ValutazioneCompetenzeReport report = new ValutazioneCompetenzeReport();
		int acquisite = countCompetenzeAcquisite(valutazioni);
		report.setAcquisite(acquisite);
		if(valutazioni.size() > 0) {
			//TODO cercare ultima modifica più recente
			report.setUltimaModifica(valutazioni.get(0).getUltimaModifica());
			report.setValutazioni(valutazioni);
			report.setStato(getValutazioneCompetenzeStato(valutazioni));
		}
		if(valutazioni.size() == 0) {
			AttivitaAlternanza aa = attivitaAlternanzaRepository.findById(es.getAttivitaAlternanzaId()).orElse(null);
			if(aa == null) {
				throw new BadRequestException("attività non trovata");
			}
			report.setValutazioni(getEmptyValutazioneCompetenze(aa));
			report.setStato(ValutazioneCompetenzeReport.Stato.non_compilata);
		}
		if(perIstituto) {
			AttivitaAlternanza aa = attivitaAlternanzaRepository.findById(es.getAttivitaAlternanzaId()).orElse(null);
			if(aa == null) {
				throw new BadRequestException("attività non trovata");
			}
			report.setDataInizio(aa.getDataInizio());
			report.setDataFine(aa.getDataFine());
			report.setOre(aa.getOre());
			int oreInserite = 0;
			List<PresenzaGiornaliera> presenze = presenzeRepository.findByEsperienzaSvoltaId(es.getId(), Sort.by(Sort.Direction.DESC, "giornata"));
			for(PresenzaGiornaliera p : presenze) {
				oreInserite += p.getOreSvolte();
			}
			report.setOreInserite(oreInserite);
		}		
		return report;
	}
	
	public ValutazioneCompetenzeReport getValutazioneCompetenzeReportByStudente(Long esperienzaSvoltaId, String studenteId) throws Exception {
		EsperienzaSvolta es = esperienzaSvoltaRepository.findById(esperienzaSvoltaId).orElse(null);
		if(es == null) {
			throw new BadRequestException("esperienza non trovata");
		}
		if(!es.getStudenteId().equals(studenteId)) {
			throw new BadRequestException("utente non autorizzato");
		}
		return getValutazioneCompetenzeReport(es, false);
	}
	
	public ValutazioneCompetenzeReport getValutazioneCompetenzeReportByIstituto(Long esperienzaSvoltaId, String istitutoId) throws Exception {
		EsperienzaSvolta es = esperienzaSvoltaRepository.findById(esperienzaSvoltaId).orElse(null);
		if(es == null) {
			throw new BadRequestException("esperienza non trovata");
		}
		if(!es.getIstitutoId().equals(istitutoId)) {
			throw new BadRequestException("istituto non autorizzato");
		}
		return getValutazioneCompetenzeReport(es, true);
	}

	public ValutazioneCompetenzeReport getValutazioneCompetenzeReportByEnte(Long esperienzaSvoltaId, String enteId) throws Exception {
		EsperienzaSvolta es = esperienzaSvoltaRepository.findById(esperienzaSvoltaId).orElse(null);
		if(es == null) {
			throw new BadRequestException("esperienza non trovata");
		}
		AttivitaAlternanza aa = attivitaAlternanzaRepository.findById(es.getAttivitaAlternanzaId()).orElse(null);
		if(aa == null) {
			throw new BadRequestException("attività non trovata");
		}
		if(!enteId.equals(aa.getEnteId())) {
			throw new BadRequestException("ente non autorizzato");
		}
		return getValutazioneCompetenzeReport(es, false);
	}
	
	private List<ValutazioneCompetenza> getEmptyValutazioneCompetenze(AttivitaAlternanza aa) {
		List<ValutazioneCompetenza> result = new ArrayList<>();
		List<Competenza> competenze = competenzaManager.getRisorsaCompetenze(aa.getUuid());
		for(int i=0; i < competenze.size(); i++) {
			Competenza c = competenze.get(i);
			ValutazioneCompetenza v = new ValutazioneCompetenza(c);
			v.setOrdine(i+1);
			result.add(v);
		}
		return result;
	}
	
	public ValutazioneCompetenzeReport saveValutazioneCompetenze(String enteId, Long esperienzaSvoltaId, 
			List<ValutazioneCompetenza> valutazioni) throws Exception {
		EsperienzaSvolta es = esperienzaSvoltaRepository.findById(esperienzaSvoltaId).orElse(null);
		if(es == null) {
			throw new BadRequestException("esperienza non trovata");
		}
		AttivitaAlternanza aa = attivitaAlternanzaRepository.findById(es.getAttivitaAlternanzaId()).orElse(null);
		if(aa == null) {
			throw new BadRequestException("attività non trovata");
		}
		if(!enteId.equals(aa.getEnteId())) {
			throw new BadRequestException("ente non autorizzato");
		}
		if(aa.getStato().equals(Stati.archiviata)) {
			throw new BadRequestException("attività archiviata");
		}
		String istitutoId = es.getIstitutoId();
		Long attivitaAlternanzaId = es.getAttivitaAlternanzaId();
		LocalDate ultimaModifica = LocalDate.now();
		for(ValutazioneCompetenza vc : valutazioni) {
			vc.setAttivitaAlternanzaId(attivitaAlternanzaId);
			vc.setIstitutoId(istitutoId);
			vc.setEsperienzaSvoltaId(esperienzaSvoltaId);
			vc.setStudenteId(es.getStudenteId());
			vc.setUltimaModifica(ultimaModifica);
			valutazioneCompetenzaRepository.save(vc);
		}
		return getValutazioneCompetenzeReport(es, true);
	}
	
	public void deleteValutazioniByEsperienzaId(Long esperienzaSvoltaId) {
		List<ValutazioneCompetenza> valutazioniCompetenze = valutazioneCompetenzaRepository.findByEsperienzaSvoltaIdOrderByOrdineAsc(esperienzaSvoltaId);
		List<ValutazioneAttivita> valutazioniAttivita = valutazioneAttivitaRepository.findByEsperienzaSvoltaIdOrderByPosizioneAsc(esperienzaSvoltaId);
		valutazioneCompetenzaRepository.deleteAll(valutazioniCompetenze);
		valutazioneAttivitaRepository.deleteAll(valutazioniAttivita);
	}

	private int countCompetenzeAcquisite(List<ValutazioneCompetenza> valutazioni) {
		int count = 0;
		for(ValutazioneCompetenza v : valutazioni) {
			if(v.getPunteggio() > 1) {
				count++;
			}
		}
		return count;
	}
	
	private ValutazioneCompetenzeReport.Stato getValutazioneCompetenzeStato(List<ValutazioneCompetenza> valutazioni) {
		int count = 0;
		for(ValutazioneCompetenza v : valutazioni) {
			if(v.getPunteggio() > 0) {
				count++;
			}
		}
		if(count == 0) {
			return ValutazioneCompetenzeReport.Stato.non_compilata;
		}
		if(count == valutazioni.size()) {
			return ValutazioneCompetenzeReport.Stato.compilata;
		}
		return ValutazioneCompetenzeReport.Stato.incompleta;
	}
}
