package it.smartcommunitylab.cartella.asl.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza.Stati;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvoltaAllineamento;
import it.smartcommunitylab.cartella.asl.model.PresenzaGiornaliera;
import it.smartcommunitylab.cartella.asl.model.Registration;
import it.smartcommunitylab.cartella.asl.model.Studente;
import it.smartcommunitylab.cartella.asl.model.report.ReportDashboardAttivita;
import it.smartcommunitylab.cartella.asl.model.report.ReportDashboardDettaglioAttivita;
import it.smartcommunitylab.cartella.asl.model.report.ReportDashboardEsperienza;
import it.smartcommunitylab.cartella.asl.model.report.ReportDashboardRegistrazione;
import it.smartcommunitylab.cartella.asl.model.report.ReportDashboardUsoSistema;
import it.smartcommunitylab.cartella.asl.storage.LocalDocumentManager;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Repository
@Transactional
public class DashboardManager extends DataEntityManager {
	@Autowired
	AttivitaAlternanzaManager attivitaAlternanzaManager;
	@Autowired
	EsperienzaSvoltaManager esperienzaSvoltaManager;
	@Autowired
	OffertaManager offertaManager;
	@Autowired
	LocalDocumentManager documentManager;
	@Autowired
	CompetenzaManager competenzaManager;	
	@Autowired
	EsperienzaAllineamentoManager allineamentoManager;

	@SuppressWarnings("unchecked")
	public ReportDashboardUsoSistema getReportUtilizzoSistema(String istitutoId, String annoScolastico) throws Exception {
		String q = "SELECT COUNT(DISTINCT r.studentId) FROM Registration r"
				+ " WHERE r.instituteId=(:istitutoId) AND r.schoolYear=(:annoScolastico)";
		TypedQuery<Long> query = em.createQuery(q, Long.class);
		query.setParameter("istitutoId", istitutoId);
		query.setParameter("annoScolastico", annoScolastico);		
		Long numeroStudentiIscritti = query.getSingleResult();
		
		q = "SELECT COUNT(DISTINCT es.studenteId) FROM AttivitaAlternanza aa, EsperienzaSvolta es"
				+ " WHERE es.attivitaAlternanzaId=aa.id"
				+ " AND aa.istitutoId=(:istitutoId) AND aa.annoScolastico=(:annoScolastico)";
		query = em.createQuery(q, Long.class);
		query.setParameter("istitutoId", istitutoId);
		query.setParameter("annoScolastico", annoScolastico);		
		Long numeroStudentiConEsperienze = query.getSingleResult();

		q = "SELECT COUNT(*), aa.tipologia FROM AttivitaAlternanza aa"
				+ " WHERE aa.istitutoId=(:istitutoId) AND aa.annoScolastico=(:annoScolastico)"
				+ " GROUP BY aa.tipologia";
		Query queryGen = em.createQuery(q);
		queryGen.setParameter("istitutoId", istitutoId);
		queryGen.setParameter("annoScolastico", annoScolastico);
		List<Object[]> result = queryGen.getResultList();
		Map<Integer, Long> tipologiaMap = new HashMap<>();
		long numeroAttivita = 0;
		for (Object[] obj : result) {
			Long num = (long) obj[0];
			Integer tipologia = (int) obj[1];
			tipologiaMap.put(tipologia, num);
			numeroAttivita += num;
		}
		
		ReportDashboardUsoSistema report = new ReportDashboardUsoSistema();
		report.setNumeroStudentiIscritti(numeroStudentiIscritti);
		report.setNumeroStudentiConEsperienze(numeroStudentiConEsperienze);
		report.setNumeroAttivita(numeroAttivita);
		report.setTipologiaMap(tipologiaMap);
		return report;
	}
	
	public ReportDashboardAttivita getReportAttivita(String istitutoId, String annoScolastico,
			LocalDate dateFrom, LocalDate dateTo) {
		String q = "SELECT COUNT(DISTINCT es) FROM AttivitaAlternanza aa, EsperienzaSvolta es"
				+ " WHERE es.attivitaAlternanzaId=aa.id"
				+ " AND aa.istitutoId=(:istitutoId) AND aa.annoScolastico=(:annoScolastico)";
		if((dateFrom != null) && (dateTo != null)) {
			q += " AND aa.dataInizio >= (:dateFrom) AND aa.dataInizio <= (:dateTo)";
		}
		TypedQuery<Long> query = em.createQuery(q, Long.class);
		query.setParameter("istitutoId", istitutoId);
		query.setParameter("annoScolastico", annoScolastico);
		if((dateFrom != null) && (dateTo != null)) {
			query.setParameter("dateFrom", dateFrom);
			query.setParameter("dateTo", dateTo);
		}
		Long numeroEsperienze = query.getSingleResult();
		
		q = "SELECT COUNT(DISTINCT es) FROM AttivitaAlternanza aa, EsperienzaSvolta es"
				+ " WHERE es.attivitaAlternanzaId=aa.id"
				+ " AND aa.istitutoId=(:istitutoId) AND aa.annoScolastico=(:annoScolastico) AND es.stato='annullata'";
		if((dateFrom != null) && (dateTo != null)) {
			q += " AND aa.dataInizio >= (:dateFrom) AND aa.dataInizio <= (:dateTo)";
		}
		query = em.createQuery(q, Long.class);
		query.setParameter("istitutoId", istitutoId);
		query.setParameter("annoScolastico", annoScolastico);
		if((dateFrom != null) && (dateTo != null)) {
			query.setParameter("dateFrom", dateFrom);
			query.setParameter("dateTo", dateTo);
		}
		Long numeroEsperienzeAnnullate = query.getSingleResult();
		
		q = "SELECT COUNT(DISTINCT esa) FROM AttivitaAlternanza aa, EsperienzaSvolta es, EsperienzaSvoltaAllineamento esa"
				+ " WHERE es.attivitaAlternanzaId=aa.id AND esa.espSvoltaId=es.id"
				+ " AND aa.istitutoId=(:istitutoId) AND aa.annoScolastico=(:annoScolastico) AND esa.allineato=true";
		if((dateFrom != null) && (dateTo != null)) {
			q += " AND aa.dataInizio >= (:dateFrom) AND aa.dataInizio <= (:dateTo)";
		}
		query = em.createQuery(q, Long.class);
		query.setParameter("istitutoId", istitutoId);
		query.setParameter("annoScolastico", annoScolastico);
		if((dateFrom != null) && (dateTo != null)) {
			query.setParameter("dateFrom", dateFrom);
			query.setParameter("dateTo", dateTo);
		}
		Long numeroEsperienzeAllineate = query.getSingleResult();

		q = "SELECT COUNT(aa) FROM AttivitaAlternanza aa"
				+ " WHERE aa.istitutoId=(:istitutoId) AND aa.annoScolastico=(:annoScolastico)";		
		if((dateFrom != null) && (dateTo != null)) {
			q += " AND aa.dataInizio >= (:dateFrom) AND aa.dataInizio <= (:dateTo)";
		}
		query = em.createQuery(q, Long.class);
		query.setParameter("istitutoId", istitutoId);
		query.setParameter("annoScolastico", annoScolastico);
		if((dateFrom != null) && (dateTo != null)) {
			query.setParameter("dateFrom", dateFrom);
			query.setParameter("dateTo", dateTo);
		}
		Long numeroAttivita = query.getSingleResult();
		
		q = "SELECT COUNT(aa) FROM AttivitaAlternanza aa"
				+ " WHERE aa.istitutoId=(:istitutoId) AND aa.annoScolastico=(:annoScolastico) AND aa.stato = 'archiviata'";
		if((dateFrom != null) && (dateTo != null)) {
			q += " AND aa.dataInizio >= (:dateFrom) AND aa.dataInizio <= (:dateTo)";
		}
		query = em.createQuery(q, Long.class);
		query.setParameter("istitutoId", istitutoId);
		query.setParameter("annoScolastico", annoScolastico);
		if((dateFrom != null) && (dateTo != null)) {
			query.setParameter("dateFrom", dateFrom);
			query.setParameter("dateTo", dateTo);
		}
		Long numeroAttivitaArchiviate = query.getSingleResult();
		
		q = "SELECT COUNT(DISTINCT aa) FROM AttivitaAlternanza aa, EsperienzaSvolta es"
				+ " WHERE es.attivitaAlternanzaId = aa.id"
				+ " AND aa.istitutoId=(:istitutoId) AND aa.annoScolastico=(:annoScolastico)";
		if((dateFrom != null) && (dateTo != null)) {
			q += " AND aa.dataInizio >= (:dateFrom) AND aa.dataInizio <= (:dateTo)";
		}
		query = em.createQuery(q, Long.class);
		query.setParameter("istitutoId", istitutoId);
		query.setParameter("annoScolastico", annoScolastico);
		if((dateFrom != null) && (dateTo != null)) {
			query.setParameter("dateFrom", dateFrom);
			query.setParameter("dateTo", dateTo);
		}
		Long numeroAttivitaSenzaStudenti = numeroAttivita - query.getSingleResult();
		
		ReportDashboardAttivita report = new ReportDashboardAttivita();
		report.setNumeroAttivita(numeroAttivita);
		report.setNumeroAttivitaArchiviate(numeroAttivitaArchiviate);
		report.setNumeroAttivitaSenzaStudenti(numeroAttivitaSenzaStudenti);
		report.setNumeroEsperienze(numeroEsperienze);
		report.setNumeroEsperienzeAllineate(numeroEsperienzeAllineate);
		report.setNumeroEsperienzeAnnullate(numeroEsperienzeAnnullate);
		return report;
	}
	
	@SuppressWarnings("unchecked")
	public List<ReportDashboardEsperienza> getReportEsperienze(String istitutoId, String annoScolastico, 
			String text, boolean getErrors) {
		String q = "SELECT es, aa, esa FROM EsperienzaSvolta es LEFT JOIN AttivitaAlternanza aa"
				+ " ON es.attivitaAlternanzaId=aa.id"
				+ " LEFT JOIN EsperienzaSvoltaAllineamento esa"
				+ " ON es.id=esa.espSvoltaId"
				+ " WHERE aa.istitutoId=(:istitutoId) AND aa.annoScolastico=(:annoScolastico)";
		if(Utils.isNotEmpty(text)) {
			q += " AND (UPPER(es.nominativoStudente) LIKE (:text) OR UPPER(es.classeStudente) LIKE (:text) OR UPPER(es.cfStudente) LIKE (:text))";
		}
		if(getErrors) {
			q += " AND esa.allineato = false AND esa.numeroTentativi > 0";
		}
		q += " ORDER BY aa.dataInizio DESC";
		Query query = em.createQuery(q);
		query.setParameter("istitutoId", istitutoId);
		query.setParameter("annoScolastico", annoScolastico);
		if(Utils.isNotEmpty(text)) {
			query.setParameter("text", "%" + text.trim().toUpperCase() + "%");
		}
		query.setMaxResults(50);
		List<Object[]> result = query.getResultList();
		List<ReportDashboardEsperienza> list = new ArrayList<>();
		for (Object[] obj : result) {
			EsperienzaSvolta es = (EsperienzaSvolta) obj[0];
			AttivitaAlternanza aa = (AttivitaAlternanza) obj[1];
			EsperienzaSvoltaAllineamento esa = (EsperienzaSvoltaAllineamento)obj[2];
			int oreValidate = getOreValidate(es.getId());
			ReportDashboardEsperienza report = new ReportDashboardEsperienza();
			report.setAttivitaAlternanzaId(es.getAttivitaAlternanzaId());
			report.setEsperienzaId(es.getId());
			report.setStudenteId(es.getStudenteId());
			report.setNominativoStudente(es.getNominativoStudente());
			report.setCfStudente(es.getCfStudente());
			report.setClasseStudente(es.getClasseStudente());
			report.setTitolo(aa.getTitolo());
			report.setTipologia(aa.getTipologia());
			report.setDataInizio(aa.getDataInizio());
			report.setDataFine(aa.getDataFine());
			report.setStato(es.getStato());
			report.setOreTotali(aa.getOre());
			report.setOreValidate(oreValidate);
			if(esa != null) {
				report.setAllineato(esa.isAllineato());
				report.setNumeroTentativi(esa.getNumeroTentativi());
				report.setErrore(esa.getErrore());
				report.setInvio(esa.getInvio());
				report.setUltimoAllineamento(esa.getUltimoAllineamento());
			}
			list.add(report);
		}
		return list;
	}
	
	private int getOreValidate(Long esId) {
		String qPresenze = "SELECT pg FROM EsperienzaSvolta es, PresenzaGiornaliera pg"
				+ " WHERE pg.esperienzaSvoltaId=es.id AND es.id=(:esId)"
				+ " ORDER BY pg.giornata ASC";
		TypedQuery<PresenzaGiornaliera> queryPresenze = em.createQuery(qPresenze, PresenzaGiornaliera.class);
		queryPresenze.setParameter("esId", esId);
		List<PresenzaGiornaliera> presenze = queryPresenze.getResultList();
		int oreValidate = 0;
		for (PresenzaGiornaliera pg : presenze) {
			if(pg.getVerificata()) {
				oreValidate += pg.getOreSvolte();
			}
		}
		return oreValidate;
	}
	
	@SuppressWarnings("unchecked")
	public List<ReportDashboardRegistrazione> getReportRegistrazioni(String istitutoId, String cf) {
		List<ReportDashboardRegistrazione> list = new ArrayList<>();
		String q = "SELECT r, s FROM Registration r, Studente s"
		+ " WHERE r.studentId=s.id AND r.instituteId=(:istitutoId) AND s.cf=(:cf)"
		+ " ORDER BY r.dateFrom DESC";
		Query query = em.createQuery(q);
		query.setParameter("istitutoId", istitutoId);
		query.setParameter("cf", cf.trim().toUpperCase());
		List<Object[]> result = query.getResultList();
		for (Object[] obj : result) {
			Registration r = (Registration) obj[0];
			Studente s = (Studente) obj[1];
			ReportDashboardRegistrazione report = new ReportDashboardRegistrazione();
			report.setRegistrationId(r.getId());
			report.setStudenteId(s.getId());
			report.setNominativoStudente(s.getSurname() + " " + s.getName());
			report.setCfStudente(s.getCf());
			report.setClasseStudente(r.getClassroom());
			report.setAnnoScolastico(r.getSchoolYear());
			report.setCorso(r.getCourse());
			report.setDataInizio(r.getDateFrom());
			report.setDataFine(r.getDateTo());
			list.add(report);
		}
		return list;
	}

	public List<ReportDashboardDettaglioAttivita> getReportDettaglioAttivita(String istitutoId, 
			String annoScolastico, String text) {
		StringBuilder sb = new StringBuilder("SELECT DISTINCT aa FROM AttivitaAlternanza aa LEFT JOIN EsperienzaSvolta es");
		sb.append(" ON es.attivitaAlternanzaId=aa.id WHERE aa.istitutoId=(:istitutoId) and aa.annoScolastico=(:annoScolastico)");
		
		if(Utils.isNotEmpty(text)) {
			sb.append(" AND (UPPER(aa.titolo) LIKE (:text) OR UPPER(es.nominativoStudente) LIKE (:text) OR UPPER(es.classeStudente) LIKE (:text)"
					+ " OR UPPER(es.cfStudente) LIKE (:text))");
		}
		
		sb.append(" ORDER BY aa.dataInizio DESC, aa.titolo ASC");
		String q = sb.toString();

		TypedQuery<AttivitaAlternanza> query = em.createQuery(q, AttivitaAlternanza.class);
		
		query.setParameter("istitutoId", istitutoId);
		query.setParameter("annoScolastico", annoScolastico);
		if(Utils.isNotEmpty(text)) {
			query.setParameter("text", "%" + text.trim().toUpperCase() + "%");
		}
		
		List<AttivitaAlternanza> aaList = query.getResultList();
		
		List<ReportDashboardDettaglioAttivita> reportList = new ArrayList<>();
		Map<Long, ReportDashboardDettaglioAttivita> reportMap = new HashMap<>();
		List<Long> aaIdList = new ArrayList<>();
		for (AttivitaAlternanza aa : aaList) {
			ReportDashboardDettaglioAttivita report = new ReportDashboardDettaglioAttivita(aa); 
			report.setStato(attivitaAlternanzaManager.getStato(aa).toString());
			reportMap.put(aa.getId(), report);
			reportList.add(report);
			aaIdList.add(aa.getId());
		}
		
		List<EsperienzaSvolta> esperienze = esperienzaSvoltaManager.getEsperienzeByAttivitaIds(aaIdList);
		for(EsperienzaSvolta es : esperienze) {
			ReportDashboardDettaglioAttivita report = reportMap.get(es.getAttivitaAlternanzaId());
			if(report != null) {
				report.getStudenti().add(es.getNominativoStudente());
				report.getClassi().add(es.getClasseStudente());
			}
		}
		
		return reportList;
	}

	public AttivitaAlternanza deleteAttivita(Long attivitaId) throws Exception {
		AttivitaAlternanza aa = attivitaAlternanzaManager.getAttivitaAlternanza(attivitaId);
		if(aa == null) {
			throw new BadRequestException("entity not found");
		}
		if(aa.getStato().equals(Stati.archiviata)) {
			List<EsperienzaSvolta> esperienze = esperienzaSvoltaManager.getEsperienzeByAttivita(aa, Sort.by(Sort.Direction.ASC, "id"));
			for(EsperienzaSvolta es : esperienze) {
				allineamentoManager.deleteEsperienzaSvoltaAllineamento(es.getId());
			}
		}
		int numEsperienze = esperienzaSvoltaManager.deleteEsperienzeByAttivita(aa);
		if(aa.getOffertaId() != null) {
			offertaManager.rimuoviPostiEsperienze(aa.getOffertaId(), numEsperienze);
		}
		documentManager.deleteDocumentsByRisorsaId(aa.getUuid());
		competenzaManager.deleteAssociatedCompetenzeByRisorsaId(aa.getUuid());
		attivitaAlternanzaManager.deleteAttivitaAlternanzaById(attivitaId);
		return aa;
	}

	public EsperienzaSvolta deleteEsperienza(Long esperienzaId) throws Exception {
		EsperienzaSvolta es = esperienzaSvoltaManager.getEsperienzaSvolta(esperienzaId);
		if(es == null) {
			throw new BadRequestException("entity not found");
		}
		allineamentoManager.deleteEsperienzaSvoltaAllineamento(es.getId());
		esperienzaSvoltaManager.deleteEsperienza(es);
		return es;
	}

	public AttivitaAlternanza activateAttivita(Long attivitaId) throws Exception {
		AttivitaAlternanza aa = attivitaAlternanzaManager.activateAttivitaAlternanza(attivitaId);
		return aa;
	}
	
}
