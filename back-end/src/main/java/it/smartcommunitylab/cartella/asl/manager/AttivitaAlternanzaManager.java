package it.smartcommunitylab.cartella.asl.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza.Stati;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;
import it.smartcommunitylab.cartella.asl.model.Offerta;
import it.smartcommunitylab.cartella.asl.model.PresenzaGiornaliera;
import it.smartcommunitylab.cartella.asl.model.report.ReportArchiviaEsperienza;
import it.smartcommunitylab.cartella.asl.model.report.ReportAttivitaAlternanzaDettaglio;
import it.smartcommunitylab.cartella.asl.model.report.ReportAttivitaAlternanzaRicerca;
import it.smartcommunitylab.cartella.asl.model.report.ReportAttivitaAlternanzaStudenti;
import it.smartcommunitylab.cartella.asl.model.report.ReportEsperienzaRegistration;
import it.smartcommunitylab.cartella.asl.model.report.ReportEsperienzaStudente;
import it.smartcommunitylab.cartella.asl.model.report.ReportPresenzaGiornalieraGruppo;
import it.smartcommunitylab.cartella.asl.model.report.ReportPresenzeAttvitaAlternanza;
import it.smartcommunitylab.cartella.asl.repository.AttivitaAlternanzaRepository;
import it.smartcommunitylab.cartella.asl.storage.LocalDocumentManager;
import it.smartcommunitylab.cartella.asl.util.ErrorLabelManager;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Repository
@Transactional
public class AttivitaAlternanzaManager extends DataEntityManager {
	
	@Autowired
	AttivitaAlternanzaRepository attivitaAlternanzaRepository;
	@Autowired
	EsperienzaSvoltaManager esperienzaSvoltaManager;
	@Autowired
	OffertaManager offertaManager;
	@Autowired
	LocalDocumentManager documentManager;
	@Autowired
	PresenzaGiornalieraManager presenzaGiornalieraManager;
	@Autowired
	CompetenzaManager competenzaManager;
	@Autowired
	ErrorLabelManager errorLabelManager;
	
	public AttivitaAlternanza saveAttivitaAlternanza(AttivitaAlternanza aa, String istitutoId) 
			throws BadRequestException {
		aa.setIstitutoId(istitutoId);
		aa.setStato(Stati.attiva);
		AttivitaAlternanza aaDb = getAttivitaAlternanza(aa.getId());
		if(aaDb == null) {
			aa.setUuid(Utils.getUUID());
			return attivitaAlternanzaRepository.save(aa);
		} else {
			if(aaDb.getStato().equals(Stati.archiviata)) {
				throw new BadRequestException(errorLabelManager.get("attivita.noteditable"));
			}
			List<EsperienzaSvolta> esperienze = esperienzaSvoltaManager.getEsperienzeByAttivita(aaDb, 
					Sort.by(Sort.Direction.ASC, "nominativoStudente"));
			if(aa.getOffertaId() != aaDb.getOffertaId()) {
				offertaManager.rimuoviPostiEsperienze(aaDb.getOffertaId(), esperienze.size());
			}
			if(aa.getOffertaId() != null) {
				offertaManager.aggiungiPostiEsperienze(aa.getOffertaId(), esperienze.size());
			}
			if(!aaDb.getDataInizio().isEqual(aa.getDataInizio()) || 
					!aaDb.getDataFine().isEqual(aa.getDataFine())) {
				for(EsperienzaSvolta esperienza : esperienze) {
					esperienzaSvoltaManager.changeDateRange(esperienza, aa.getDataInizio(), aa.getDataFine());
				}
			}
			attivitaAlternanzaRepository.updateAttivitaAlternanza(aa);
			return aa;
		}
	}
	
	public AttivitaAlternanza getAttivitaAlternanza(Long id) {
		if(id != null) {
			Optional<AttivitaAlternanza> optional = attivitaAlternanzaRepository.findById(id);
			if(optional.isPresent()) {
				return optional.get();
			}
		}
		return null;
	}
	
	public AttivitaAlternanza getAttivitaAlternanzaStub(Long id) {
		AttivitaAlternanza aa = getAttivitaAlternanza(id);
		if(aa != null) {
			AttivitaAlternanza attivita = aa.clona();
			attivita.setStato(getStato(aa));
			return attivita;
		}
		return null;
	}
	
	public void deleteAttivitaAlternanza(AttivitaAlternanza aa) throws Exception {
		if(aa.getStato().equals(Stati.archiviata)) {
			throw new BadRequestException(errorLabelManager.get("attivita.noteditable"));
		}
		int numEsperienze = esperienzaSvoltaManager.deleteEsperienzeByAttivita(aa);
		if(aa.getOffertaId() != null) {
			offertaManager.rimuoviPostiEsperienze(aa.getOffertaId(), numEsperienze);
		}
		documentManager.deleteDocumentsByRisorsaId(aa.getUuid());
		competenzaManager.deleteAssociatedCompetenzeByRisorsaId(aa.getUuid());
		attivitaAlternanzaRepository.deleteById(aa.getId());
	}
	
	public Page<ReportAttivitaAlternanzaRicerca> findAttivita(String istitutoId, String text, int tipologia, String stato,
			Pageable pageRequest) {

		StringBuilder sb = new StringBuilder("SELECT DISTINCT aa FROM AttivitaAlternanza aa LEFT JOIN EsperienzaSvolta es");
		sb.append(" ON es.attivitaAlternanzaId=aa.id WHERE aa.istitutoId=(:istitutoId)");
		
		if(Utils.isNotEmpty(text)) {
			sb.append(" AND (UPPER(aa.titolo) LIKE (:text) OR UPPER(es.nominativoStudente) LIKE (:text) OR UPPER(es.classeStudente) LIKE (:text))");
		}
		
		if(tipologia > 0) {
			sb.append(" AND aa.tipologia=(:tipologia)");
		}
		
		boolean setDataParam = false;
		if(Utils.isNotEmpty(stato)) {
			Stati statoEnum = Stati.valueOf(stato);
			if(statoEnum == Stati.archiviata) {
				sb.append(" AND aa.stato='" + Stati.archiviata.toString() + "'");
			} else {
				sb.append(" AND aa.stato='" + Stati.attiva.toString() + "'");
				setDataParam = true;
				if(statoEnum == Stati.in_attesa) {
					sb.append(" AND aa.dataInizio > (:data)");
				}
				if(statoEnum == Stati.revisione) {
					sb.append(" AND aa.dataFine < (:data)");
					setDataParam = true;
				}
				if(statoEnum == Stati.in_corso) {
					sb.append(" AND aa.dataInizio <= (:data) AND aa.dataFine >= (:data)");
					setDataParam = true;
				}
			}
		}
				
		sb.append(" ORDER BY aa.dataInizio DESC, aa.titolo ASC");
		String q = sb.toString();

		TypedQuery<AttivitaAlternanza> query = em.createQuery(q, AttivitaAlternanza.class);
		
		query.setParameter("istitutoId", istitutoId);
		if(Utils.isNotEmpty(text)) {
			query.setParameter("text", "%" + text.trim().toUpperCase() + "%");
		}
		if(tipologia > 0) {
			query.setParameter("tipologia", tipologia);
		}
		if(setDataParam) {
			LocalDate localDate = LocalDate.now(); 
			query.setParameter("data", localDate);
		}
		
		query.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
		query.setMaxResults(pageRequest.getPageSize());
		List<AttivitaAlternanza> aaList = query.getResultList();
		
		List<ReportAttivitaAlternanzaRicerca> reportList = new ArrayList<>();
		Map<Long, ReportAttivitaAlternanzaRicerca> reportMap = new HashMap<>();
		List<Long> aaIdList = new ArrayList<>();
		for (AttivitaAlternanza aa : aaList) {
			ReportAttivitaAlternanzaRicerca report = new ReportAttivitaAlternanzaRicerca(aa); 
			report.setStato(getStato(aa).toString());
			reportMap.put(aa.getId(), report);
			reportList.add(report);
			aaIdList.add(aa.getId());
		}
		
		List<EsperienzaSvolta> esperienze = esperienzaSvoltaManager.getEsperienzeByAttivitaIds(aaIdList);
		for(EsperienzaSvolta es : esperienze) {
			ReportAttivitaAlternanzaRicerca report = reportMap.get(es.getAttivitaAlternanzaId());
			if(report != null) {
				report.getStudenti().add(es.getNominativoStudente());
				report.getClassi().add(es.getClasseStudente());
			}
		}
		
		Query cQuery = queryToCount(q.replaceAll("DISTINCT aa","COUNT(DISTINCT aa)"), query);
		long total = (Long) cQuery.getSingleResult();
		
		Page<ReportAttivitaAlternanzaRicerca> page = new PageImpl<ReportAttivitaAlternanzaRicerca>(reportList, pageRequest, total);
		return page;
	}
	
	private Stati getStato(AttivitaAlternanza attivita) {
		if(attivita.getStato() == Stati.archiviata) {
			return Stati.archiviata;
		}
		LocalDate localDate = LocalDate.now();
		if(localDate.isBefore(attivita.getDataInizio())) {
			return Stati.in_attesa;
		}
		if(localDate.isEqual(attivita.getDataInizio()) || localDate.isEqual(attivita.getDataFine()) ||
				(localDate.isAfter(attivita.getDataInizio()) && localDate.isBefore(attivita.getDataFine()))) {
			return Stati.in_corso;
		}
		if(localDate.isAfter(attivita.getDataFine())) {
			return Stati.revisione;
		}
		return attivita.getStato();
	}

	public ReportAttivitaAlternanzaStudenti getStudentInfo(AttivitaAlternanza attivitaAlternanza) {
		ReportAttivitaAlternanzaStudenti report = new ReportAttivitaAlternanzaStudenti(attivitaAlternanza);
		List<EsperienzaSvolta> esperienze = esperienzaSvoltaManager.getEsperienzeByAttivita(attivitaAlternanza, 
				Sort.by(Sort.Direction.ASC, "nominativoStudente"));
		for(EsperienzaSvolta esperienza : esperienze) {
			report.getStudenti().add(esperienza.getNominativoStudente());
			List<PresenzaGiornaliera> presenze = presenzaGiornalieraManager.findByEsperienzaSvolta(esperienza.getId());
			for(PresenzaGiornaliera presenza :  presenze) {
				if(!presenza.getVerificata()) {
					report.addOreDaValidare(presenza.getOreSvolte());
				}
			}
			if(report.getNumeroOreDaValidare() > 0) {
				report.addStudenteDaValidare();
			}
			if(EsperienzaSvolta.Stati.valida == esperienza.getStato()) {
				report.addEsperienzaCompletata();
			}
		}
		return report;
	}

	public ReportAttivitaAlternanzaDettaglio getAttivitaAlternanzaDetails(AttivitaAlternanza aa) {
		AttivitaAlternanza attivita = aa.clona();
		attivita.setStato(getStato(aa));
		ReportAttivitaAlternanzaDettaglio report = new ReportAttivitaAlternanzaDettaglio();
		report.setAttivitaAlternanza(attivita);
		List<EsperienzaSvolta> esperienze = esperienzaSvoltaManager.getEsperienzeByAttivita(aa, 
				Sort.by(Sort.Direction.ASC, "nominativoStudente"));
		for (EsperienzaSvolta esperienza : esperienze) {
			ReportEsperienzaRegistration espReg = new ReportEsperienzaRegistration(esperienza);
			report.getEsperienze().add(espReg);
		}
		return report;
	}

	public void setStudentList(AttivitaAlternanza aa, List<ReportEsperienzaRegistration> listaEspReg) 
			throws Exception {
		if(aa.getStato().equals(Stati.archiviata)) {
			throw new BadRequestException(errorLabelManager.get("attivita.noteditable"));
		}
		List<EsperienzaSvolta> esperienze = esperienzaSvoltaManager.getEsperienzeByAttivita(aa, 
				Sort.by(Sort.Direction.ASC, "nominativoStudente"));
		if(aa.getOffertaId() != null) {
			Offerta offerta = offertaManager.getOfferta(aa.getOffertaId());
			if(offerta != null) {
				int toDelete = 0;
				int toKeep = 0;
				for(EsperienzaSvolta esperienzaDb : esperienze) {
					if(!checkExistingEsperienza(esperienzaDb, listaEspReg)) {
						toDelete++;
					} else {
						toKeep++;
					}
		 		}
				int postiRimanenti = offerta.getPostiRimanenti() + toDelete - (listaEspReg.size() - toKeep);
				if((postiRimanenti) < 0) {
					throw new BadRequestException(errorLabelManager.get("offerta.postiRimanenti"));
				}
				offertaManager.updatePostiRimanenti(offerta.getId(), postiRimanenti);
			}
		}
		for(EsperienzaSvolta esperienzaDb : esperienze) {
			if(!checkExistingEsperienza(esperienzaDb, listaEspReg)) {
				esperienzaSvoltaManager.deleteEsperienza(esperienzaDb);
			}
 		}
		for(ReportEsperienzaRegistration espReg : listaEspReg) {
			if(espReg.getEsperienzaSvoltaId() ==  null) {
				if(!checkExistingEsperienza(espReg, esperienze)) {
					EsperienzaSvolta newEsperienza = new EsperienzaSvolta();
					newEsperienza.setAttivitaAlternanzaId(aa.getId());
					newEsperienza.setCfStudente(espReg.getCfStudente());
					newEsperienza.setClasseStudente(espReg.getClasseStudente());
					newEsperienza.setIstitutoId(aa.getIstitutoId());
					newEsperienza.setNominativoStudente(espReg.getNominativoStudente());
					newEsperienza.setRegistrazioneId(espReg.getRegistrazioneId());
					newEsperienza.setStudenteId(espReg.getStudenteId());
					esperienzaSvoltaManager.addEsperienza(newEsperienza);					
				}
			}
		}
	}
	
	private boolean checkExistingEsperienza(EsperienzaSvolta esperienzaOrig, List<ReportEsperienzaRegistration> listaEspReg) {
		for(ReportEsperienzaRegistration espReg : listaEspReg) {
			if(espReg.isCompatible(esperienzaOrig)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkExistingEsperienza(ReportEsperienzaRegistration espReg, List<EsperienzaSvolta> listaEsperienze) {
		for(EsperienzaSvolta esperienza : listaEsperienze) {
			if(espReg.isCompatible(esperienza)) {
				return true;
			}
		}
		return false;
	}
	
	public void archiveAttivitaAlternanza(AttivitaAlternanza aa, List<ReportArchiviaEsperienza> listaEsperienze) 
			throws Exception {
		if(aa.getStato().equals(Stati.archiviata)) {
			throw new BadRequestException(errorLabelManager.get("attivita.noteditable"));
		}
		for(ReportArchiviaEsperienza report : listaEsperienze) {
			EsperienzaSvolta esperienzaSvolta = esperienzaSvoltaManager.findById(report.getEsperienzaSvoltaId());
			if(esperienzaSvolta == null) {
				throw new BadRequestException(errorLabelManager.get("esperienza.notfound"));
			}
			esperienzaSvoltaManager.chiudiEsperienza(report.getEsperienzaSvoltaId(), report.isValida());
		}
		aa.setDataArchiviazione(LocalDate.now());
		aa.setStato(Stati.archiviata);
		attivitaAlternanzaRepository.updateStato(aa.getId(), Stati.archiviata);
		attivitaAlternanzaRepository.updateDataArchiviazione(aa.getId(), aa.getDataArchiviazione());
	}

	public List<ReportArchiviaEsperienza> getArchiveAttivitaAlternanza(AttivitaAlternanza aa) {
		List<ReportArchiviaEsperienza> result = new ArrayList<>();
		List<EsperienzaSvolta> esperienze = esperienzaSvoltaManager.getEsperienzeByAttivita(aa, 
				Sort.by(Sort.Direction.ASC, "nominativoStudente"));
		for(EsperienzaSvolta esperienza : esperienze) {
			ReportArchiviaEsperienza report = new ReportArchiviaEsperienza(esperienza);
			report.setOreAttivita(aa.getOre());
			List<PresenzaGiornaliera> presenze = presenzaGiornalieraManager.findByEsperienzaSvolta(esperienza.getId());
			for(PresenzaGiornaliera presenza :  presenze) {
				if(presenza.getVerificata()) {
					report.adOreValidate(presenza.getOreSvolte());
				}
			}
			if(EsperienzaSvolta.Stati.valida.equals(esperienza.getStato())) {
				report.setValida(true);
			}
			if(EsperienzaSvolta.Stati.da_definire.equals(esperienza.getStato())) {
				if(calculatePercentage(report.getOreValidate(), report.getOreAttivita()) >= 75) {
					report.setValida(true);
				}
			}
			result.add(report);
		}
		return result;
	}
	
	private int calculatePercentage(int obtained, int total) {
		return obtained * 100 / total;
	}

	public Page<ReportEsperienzaRegistration> findEsperienze(String istitutoId, String annoScolastico, 
			String text, Pageable pageRequest) {
		return esperienzaSvoltaManager.findEsperienze(istitutoId, annoScolastico, text, pageRequest);
	}
	
	public ReportPresenzeAttvitaAlternanza getReportPresenzeAttvitaAlternanzaIndividuale(AttivitaAlternanza aa) 
			throws Exception {
		
		List<EsperienzaSvolta> esperienze = esperienzaSvoltaManager.getEsperienzeByAttivita(aa, Sort.by(Sort.Direction.ASC, "id"));
		if(esperienze.size() == 0) {
			throw new BadRequestException(errorLabelManager.get("esperienza.notfound"));
		}
		EsperienzaSvolta esperienza = esperienze.get(0);
		
		int oreValidate = 0;
		int giornateValidate = 0;
		int giornateDaValidare = 0;
		List<PresenzaGiornaliera> presenze = presenzaGiornalieraManager.findByEsperienzaSvolta(esperienza.getId());
		for (PresenzaGiornaliera presenza : presenze) {
			if(presenza.getVerificata()) {
				oreValidate += presenza.getOreSvolte();
				giornateValidate++;
			} else {
				giornateDaValidare++;
			}
		}
		
		ReportPresenzeAttvitaAlternanza report = new ReportPresenzeAttvitaAlternanza(aa);
		report.setOreValidate(oreValidate);
		report.setGiornateValidate(giornateValidate);
		report.setGiornateDaValidare(giornateDaValidare);
		return report;
	}
	
	public ReportPresenzeAttvitaAlternanza getReportPresenzeAttvitaAlternanzaGruppo(AttivitaAlternanza aa) 
			throws Exception {
		String q = "SELECT COUNT(DISTINCT es) FROM EsperienzaSvolta es LEFT JOIN PresenzaGiornaliera pg ON pg.esperienzaSvoltaId=es.id"
				+ " WHERE es.attivitaAlternanzaId=(:attivitaAlternanzaId) AND pg.verificata=false";
		TypedQuery<Long> query = em.createQuery(q, Long.class);
		query.setParameter("attivitaAlternanzaId", aa.getId());
		Long studentiDaValidare = query.getSingleResult();
	
		q = "SELECT COUNT(DISTINCT pg) FROM EsperienzaSvolta es, PresenzaGiornaliera pg WHERE es.attivitaAlternanzaId=(:attivitaAlternanzaId)"
			+ " AND pg.esperienzaSvoltaId=es.id AND pg.verificata=true";
		query = em.createQuery(q, Long.class);
		query.setParameter("attivitaAlternanzaId", aa.getId());
		Long giornateValidate = query.getSingleResult();
	
		q = "SELECT COUNT(DISTINCT pg) FROM EsperienzaSvolta es, PresenzaGiornaliera pg WHERE es.attivitaAlternanzaId=(:attivitaAlternanzaId)"
			+ " AND pg.esperienzaSvoltaId=es.id AND pg.verificata=false";
		query = em.createQuery(q, Long.class);
		query.setParameter("attivitaAlternanzaId", aa.getId());
		Long giornateDaValidare = query.getSingleResult();
		
		ReportPresenzeAttvitaAlternanza report = new ReportPresenzeAttvitaAlternanza(aa);
		report.setStudentiDaValidare(studentiDaValidare.intValue());
		report.setGiornateValidate(giornateValidate.intValue());
		report.setGiornateDaValidare(giornateDaValidare.intValue());
		return report;
	}
	
	public List<PresenzaGiornaliera> getPresenzeAttivitaIndividuale(AttivitaAlternanza aa, LocalDate dateFrom, 
			LocalDate dateTo) {
		String q = "SELECT pg FROM EsperienzaSvolta es, PresenzaGiornaliera pg WHERE es.attivitaAlternanzaId=(:attivitaAlternanzaId)"
				+ " AND pg.esperienzaSvoltaId=es.id AND pg.giornata >= (:dateFrom) AND pg.giornata <= (:dateTo) ORDER BY pg.giornata ASC";
		
		TypedQuery<PresenzaGiornaliera> query = em.createQuery(q, PresenzaGiornaliera.class);
		query.setParameter("attivitaAlternanzaId", aa.getId());
		query.setParameter("dateFrom", dateFrom);
		query.setParameter("dateTo", dateTo);
	
		List<PresenzaGiornaliera> list = query.getResultList();
		//fix duplicated days in db
		List<PresenzaGiornaliera> resultList = removeDuplicatedDays(list);
		return resultList;
	}

	public List<PresenzaGiornaliera> validaPresenzeAttivita(AttivitaAlternanza aa, List<PresenzaGiornaliera> presenze) throws Exception {
		List<PresenzaGiornaliera> result = new ArrayList<>();
		for(PresenzaGiornaliera pg : presenze) {
			result.add(presenzaGiornalieraManager.validaPresenza(pg));
		}
		return  result;
	}
	
	public List<ReportPresenzaGiornalieraGruppo> getPresenzeAttivitaGruppo(AttivitaAlternanza aa, LocalDate dateFrom, 
			LocalDate dateTo) throws Exception {
		List<ReportPresenzaGiornalieraGruppo> reportList = new ArrayList<ReportPresenzaGiornalieraGruppo>();
		
		String qEsperienze = "SELECT DISTINCT es FROM EsperienzaSvolta es WHERE es.attivitaAlternanzaId=(:attivitaAlternanzaId)"
				+ " ORDER BY es.nominativoStudente, es.classeStudente, es.studenteId ASC";
		
		TypedQuery<EsperienzaSvolta> queryEsperienze = em.createQuery(qEsperienze, EsperienzaSvolta.class);
		queryEsperienze.setParameter("attivitaAlternanzaId", aa.getId());
		
		List<EsperienzaSvolta> esperienze = queryEsperienze.getResultList();
		esperienze.forEach(e -> {
			ReportPresenzaGiornalieraGruppo report = new ReportPresenzaGiornalieraGruppo(e);
			report.setOreTotali(aa.getOre());
			reportList.add(report);
		});
		
		String qPresenze = "SELECT pg FROM EsperienzaSvolta es, PresenzaGiornaliera pg WHERE es.attivitaAlternanzaId=(:attivitaAlternanzaId)"
				+ " AND pg.esperienzaSvoltaId=es.id AND pg.giornata >= (:dateFrom) AND pg.giornata <= (:dateTo)"
				+ " ORDER BY es.studenteId, pg.giornata ASC";
		
		TypedQuery<PresenzaGiornaliera> queryPresenze = em.createQuery(qPresenze, PresenzaGiornaliera.class);
		queryPresenze.setParameter("attivitaAlternanzaId", aa.getId());
		queryPresenze.setParameter("dateFrom", dateFrom);
		queryPresenze.setParameter("dateTo", dateTo);
		List<PresenzaGiornaliera> list = queryPresenze.getResultList();
		//fix duplicated days in db
		List<PresenzaGiornaliera> presenze = removeDuplicatedDays(list);

		for (PresenzaGiornaliera pg : presenze) {
			ReportPresenzaGiornalieraGruppo report = checkReport(pg.getEsperienzaSvoltaId(), reportList);
			if(report != null) {
				report.getPresenze().add(pg);
				if(pg.getVerificata()) {
					report.addOreValidate(pg.getOreSvolte());
				} else {
					report.addOreDaValidare(pg.getOreSvolte());
				}				
			}
		}
		return reportList;
	}

	private ReportPresenzaGiornalieraGruppo checkReport(Long esperienzaSvoltaId, 
			List<ReportPresenzaGiornalieraGruppo> reportList) {
		for (ReportPresenzaGiornalieraGruppo report : reportList) {
			if(report.getEsperienzaSvoltaId().equals(esperienzaSvoltaId)) {
				return report;
			}
		}
		return null;
	}
	
	public List<ReportEsperienzaStudente> getReportEsperienzaStudente(String istitutoId, String studenteId) {
		String qEsperienze = "SELECT aa,es FROM AttivitaAlternanza aa, EsperienzaSvolta es WHERE es.attivitaAlternanzaId=aa.id"
				+ " AND aa.istitutoId=(:istitutoId) AND es.studenteId=(:studenteId)"
				+ " ORDER BY aa.dataInizio DESC, aa.titolo ASC";
		Query query = em.createQuery(qEsperienze);
		query.setParameter("istitutoId", istitutoId);
		query.setParameter("studenteId", studenteId);
		List<Object[]> result = query.getResultList();
		
		List<ReportEsperienzaStudente> reportList = new ArrayList<>();
		Map<Long, ReportEsperienzaStudente> reportMap = new HashMap<>();
		for (Object[] obj : result) {
			AttivitaAlternanza aa = (AttivitaAlternanza) obj[0];
			EsperienzaSvolta es = (EsperienzaSvolta) obj[1];
			ReportEsperienzaStudente report = new ReportEsperienzaStudente(aa, es);
			report.setStato(getStato(aa).toString());
			reportList.add(report);
			reportMap.put(es.getId(), report);
		}
		
		fillReportEsperienzaStudenteWithPresenze(studenteId, reportMap);
		return reportList;
	}
	
	public Page<ReportEsperienzaStudente> getReportEsperienzaStudente(String studenteId, String stato, Pageable pageRequest) {
		String qEsperienze = "SELECT aa,es FROM AttivitaAlternanza aa, EsperienzaSvolta es"
				+ " WHERE es.attivitaAlternanzaId=aa.id AND es.studenteId=(:studenteId)";
		
		boolean setDataParam = false;
		if(Utils.isNotEmpty(stato)) {
			Stati statoEnum = Stati.valueOf(stato);
			if(statoEnum == Stati.archiviata) {
				qEsperienze += " AND aa.stato='" + Stati.archiviata.toString() + "'";
			} else {
				qEsperienze += " AND aa.stato='" + Stati.attiva.toString() + "'";
				setDataParam = true;
				if(statoEnum == Stati.in_attesa) {
					qEsperienze += " AND aa.dataInizio > (:data)";
				}
				if(statoEnum == Stati.revisione) {
					qEsperienze += " AND aa.dataFine < (:data)";
				}
				if(statoEnum == Stati.in_corso) {
					qEsperienze += " AND aa.dataInizio <= (:data) AND aa.dataFine >= (:data)";
				}
			}
		}
		
		qEsperienze += " ORDER BY aa.dataInizio DESC, aa.titolo ASC";

		Query query = em.createQuery(qEsperienze);
		query.setParameter("studenteId", studenteId);
		if(setDataParam) {
			LocalDate localDate = LocalDate.now(); 
			query.setParameter("data", localDate);
		}

		query.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
		query.setMaxResults(pageRequest.getPageSize());
		List<Object[]> result = query.getResultList();
		
		List<ReportEsperienzaStudente> reportList = new ArrayList<>();
		Map<Long, ReportEsperienzaStudente> reportMap = new HashMap<>();
		for (Object[] obj : result) {
			AttivitaAlternanza aa = (AttivitaAlternanza) obj[0];
			EsperienzaSvolta es = (EsperienzaSvolta) obj[1];
			ReportEsperienzaStudente report = new ReportEsperienzaStudente(aa, es);
			report.setStato(getStato(aa).toString());
			reportList.add(report);
			reportMap.put(es.getId(), report);
		}
		fillReportEsperienzaStudenteWithPresenze(studenteId, reportMap);
		
		Query cQuery = queryToCountQuery(qEsperienze, query);
		long total = (Long) cQuery.getSingleResult();
		
		Page<ReportEsperienzaStudente> page = new PageImpl<ReportEsperienzaStudente>(reportList, pageRequest, total);
		return page;
	}
	
	private void fillReportEsperienzaStudenteWithPresenze(String studenteId,
			Map<Long, ReportEsperienzaStudente> reportMap) {
		String qPresenze = "SELECT pg FROM EsperienzaSvolta es, PresenzaGiornaliera pg WHERE es.studenteId=(:studenteId)"
				+ " AND pg.esperienzaSvoltaId=es.id";
		TypedQuery<PresenzaGiornaliera> queryPresenze = em.createQuery(qPresenze, PresenzaGiornaliera.class);
		queryPresenze.setParameter("studenteId", studenteId);
		List<PresenzaGiornaliera> presenze = queryPresenze.getResultList();
		for (PresenzaGiornaliera pg : presenze) {
			ReportEsperienzaStudente report = reportMap.get(pg.getEsperienzaSvoltaId());
			if(report != null) {
				if(pg.getVerificata()) {
					report.setOreValidate(report.getOreValidate() + pg.getOreSvolte());
				}
			}
		}
	}

	public AttivitaAlternanza associaOfferta(long offertaId, String istitutoId) 
			throws BadRequestException {
		Offerta offerta = offertaManager.getOfferta(offertaId);
		if(offerta == null) {
			throw new BadRequestException(errorLabelManager.get("offerta.notfound"));
		}
		AttivitaAlternanza aa = creaAttivitaFromOfferta(offerta, istitutoId);
		return saveAttivitaAlternanza(aa, istitutoId);
	}

	private AttivitaAlternanza creaAttivitaFromOfferta(Offerta offerta, String istitutoId) {
		String titolo = offerta.getTitolo() + " - " + 
				(attivitaAlternanzaRepository.countByOffertaIdAndIstitutoId(offerta.getId(), istitutoId) + 1);
		AttivitaAlternanza aa = new AttivitaAlternanza();
		aa.setTitolo(titolo);
		aa.setTipologia(offerta.getTipologia());
		aa.setAnnoScolastico(Utils.annoScolastico(offerta.getDataInizio()));
		aa.setDescrizione(offerta.getDescrizione());
		aa.setDataInizio(offerta.getDataInizio());
		aa.setDataFine(offerta.getDataFine());
		aa.setOraInizio(offerta.getOraInizio());
		aa.setOraFine(offerta.getOraFine());
		aa.setOre(offerta.getOre());
		aa.setOffertaId(offerta.getId());
		aa.setTitoloOfferta(offerta.getTitolo());
		aa.setEnteId(offerta.getEnteId());
		aa.setNomeEnte(offerta.getNomeEnte());
		aa.setReferenteScuola(offerta.getReferenteScuola());
		aa.setReferenteScuolaCF(offerta.getReferenteScuolaCF());
		aa.setReferenteEsterno(offerta.getReferenteEsterno());
		aa.setReferenteEsternoCF(offerta.getReferenteEsternoCF());
		aa.setFormatore(offerta.getFormatore());
		aa.setFormatoreCF(offerta.getFormatoreCF());
		aa.setLuogoSvolgimento(offerta.getLuogoSvolgimento());
		aa.setLatitude(offerta.getLatitude());
		aa.setLongitude(offerta.getLongitude());
		return aa;
	}

	public List<ReportEsperienzaStudente> getReportEsperienzaStudente(String studenteId) {
		String qEsperienze = "SELECT aa,es FROM AttivitaAlternanza aa, EsperienzaSvolta es"
				+ " WHERE es.attivitaAlternanzaId=aa.id AND es.studenteId=(:studenteId)"
				+ " ORDER BY aa.dataInizio DESC, aa.titolo ASC";
		Query query = em.createQuery(qEsperienze);
		query.setParameter("studenteId", studenteId);
		
		List<Object[]> result = query.getResultList();
		
		List<ReportEsperienzaStudente> reportList = new ArrayList<>();
		Map<Long, ReportEsperienzaStudente> reportMap = new HashMap<>();
		for (Object[] obj : result) {
			AttivitaAlternanza aa = (AttivitaAlternanza) obj[0];
			EsperienzaSvolta es = (EsperienzaSvolta) obj[1];
			ReportEsperienzaStudente report = new ReportEsperienzaStudente(aa, es);
			report.setStato(getStato(aa).toString());
			reportList.add(report);
			reportMap.put(es.getId(), report);
		}
		fillReportEsperienzaStudenteWithPresenze(studenteId, reportMap);
		return reportList;
	}
	
}
