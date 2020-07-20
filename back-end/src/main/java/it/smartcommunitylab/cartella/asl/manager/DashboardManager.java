package it.smartcommunitylab.cartella.asl.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import it.smartcommunitylab.cartella.asl.model.report.ReportDashboardAttivita;
import it.smartcommunitylab.cartella.asl.model.report.ReportDashboardUsoSistema;

@Repository
@Transactional
public class DashboardManager {
	@Autowired
	EntityManager em;

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
	
	public ReportDashboardAttivita getReportAttivita(String istitutoId, String annoScolastico) {
		String q = "SELECT COUNT(DISTINCT es) FROM AttivitaAlternanza aa, EsperienzaSvolta es"
				+ " WHERE es.attivitaAlternanzaId=aa.id"
				+ " AND aa.istitutoId=(:istitutoId) AND aa.annoScolastico=(:annoScolastico)";
		TypedQuery<Long> query = em.createQuery(q, Long.class);
		query.setParameter("istitutoId", istitutoId);
		query.setParameter("annoScolastico", annoScolastico);
		Long numeroEsperienze = query.getSingleResult();
		
		q = "SELECT COUNT(DISTINCT es) FROM AttivitaAlternanza aa, EsperienzaSvolta es"
				+ " WHERE es.attivitaAlternanzaId=aa.id"
				+ " AND aa.istitutoId=(:istitutoId) AND aa.annoScolastico=(:annoScolastico) AND es.stato='annullata'";
		query = em.createQuery(q, Long.class);
		query.setParameter("istitutoId", istitutoId);
		query.setParameter("annoScolastico", annoScolastico);
		Long numeroEsperienzeAnnullate = query.getSingleResult();
		
		q = "SELECT COUNT(DISTINCT esa) FROM AttivitaAlternanza aa, EsperienzaSvolta es, EsperienzaSvoltaAllineamento esa"
				+ " WHERE es.attivitaAlternanzaId=aa.id AND esa.espSvoltaId=es.id"
				+ " AND aa.istitutoId=(:istitutoId) AND aa.annoScolastico=(:annoScolastico) AND esa.allineato=true";
		query = em.createQuery(q, Long.class);
		query.setParameter("istitutoId", istitutoId);
		query.setParameter("annoScolastico", annoScolastico);
		Long numeroEsperienzeAllineate = query.getSingleResult();

		q = "SELECT COUNT(aa) FROM AttivitaAlternanza aa"
				+ " WHERE aa.istitutoId=(:istitutoId) AND aa.annoScolastico=(:annoScolastico)";		
		query = em.createQuery(q, Long.class);
		query.setParameter("istitutoId", istitutoId);
		query.setParameter("annoScolastico", annoScolastico);
		Long numeroAttivita = query.getSingleResult();
		
		q = "SELECT COUNT(aa) FROM AttivitaAlternanza aa"
				+ " WHERE aa.istitutoId=(:istitutoId) AND aa.annoScolastico=(:annoScolastico) AND aa.stato = 'archiviata'";
		query = em.createQuery(q, Long.class);
		query.setParameter("istitutoId", istitutoId);
		query.setParameter("annoScolastico", annoScolastico);
		Long numeroAttivitaArchiviate = query.getSingleResult();
		
		q = "SELECT COUNT(DISTINCT aa) FROM AttivitaAlternanza aa, EsperienzaSvolta es"
				+ " WHERE es.attivitaAlternanzaId = aa.id"
				+ " AND aa.istitutoId=(:istitutoId) AND aa.annoScolastico=(:annoScolastico)";
		query = em.createQuery(q, Long.class);
		query.setParameter("istitutoId", istitutoId);
		query.setParameter("annoScolastico", annoScolastico);
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
	
	
}
