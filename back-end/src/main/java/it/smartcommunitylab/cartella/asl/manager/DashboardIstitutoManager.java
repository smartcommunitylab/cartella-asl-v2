package it.smartcommunitylab.cartella.asl.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza.Stati;
import it.smartcommunitylab.cartella.asl.model.report.ReportDashboardIstituto;
import it.smartcommunitylab.cartella.asl.storage.LocalDocumentManager;

@Repository
@Transactional
public class DashboardIstitutoManager extends DataEntityManager {
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
	public ReportDashboardIstituto getReportUtilizzoIstituto(String istitutoId, String annoScolastico) throws Exception {
		String q = "SELECT aa FROM AttivitaAlternanza aa"
				+ " WHERE aa.istitutoId=(:istitutoId) AND aa.annoScolastico=(:annoScolastico)";
		TypedQuery<AttivitaAlternanza> attivitaQuery = em.createQuery(q, AttivitaAlternanza.class);
		attivitaQuery.setParameter("istitutoId", istitutoId);
		attivitaQuery.setParameter("annoScolastico", annoScolastico);		
		Stream<AttivitaAlternanza> stream = attivitaQuery.getResultStream();
		Map<Integer, Long> tipologiaMap = new HashMap<>();
		Map<Stati, Long> attivitaMap = new HashMap<>();
		stream.forEach(attivita -> {
			int tipologia = attivita.getTipologia();
			if(!tipologiaMap.containsKey(tipologia)) {
				tipologiaMap.put(tipologia, 0L);
			}
			Long num = tipologiaMap.get(tipologia);
			tipologiaMap.put(tipologia, num+1);
			Stati stato = attivitaAlternanzaManager.getStato(attivita);
			if(stato != null) {
				if(!attivitaMap.containsKey(stato)) {
					attivitaMap.put(stato, 0L);
				}
				num = attivitaMap.get(stato);
				attivitaMap.put(stato, num+1);
			}
		});
		
		q = "SELECT es.classeStudente, es.studenteId, SUM(pg.oreSvolte) FROM AttivitaAlternanza aa, EsperienzaSvolta es, PresenzaGiornaliera pg WHERE"
				+ " es.attivitaAlternanzaId=aa.id AND pg.esperienzaSvoltaId=es.id AND aa.istitutoId=(:istitutoId) AND aa.annoScolastico=(:annoScolastico)"
				+ " AND pg.verificata=true GROUP BY es.classeStudente, es.studenteId";
		Query queryGen = em.createQuery(q);
		queryGen.setParameter("istitutoId", istitutoId);
		queryGen.setParameter("annoScolastico", annoScolastico);
		List<Object[]> result = queryGen.getResultList();
		Map<String, MediaOre> classiMediaMap = new HashMap<>();
		for (Object[] obj : result) {
			String classe = (String) obj[0];
			//String studenteId = (String) obj[1];
			long oreSvolteTotali = (long) obj[2];
			if(!classiMediaMap.containsKey(classe)) {
				classiMediaMap.put(classe, new MediaOre());
			}
			MediaOre mediaOre = classiMediaMap.get(classe);
			if(mediaOre != null) {
				mediaOre.oreSvolte += oreSvolteTotali;
				mediaOre.numStudenti++;
			}
		}		
		Map<String, Double> classiMap = new HashMap<>();
		for(String classe : classiMediaMap.keySet()) {
			MediaOre mediaOre = classiMediaMap.get(classe);
			if(mediaOre != null) {
				classiMap.put(classe, (double) mediaOre.oreSvolte / mediaOre.numStudenti);
			}
		}
		
		ReportDashboardIstituto report = new ReportDashboardIstituto();
		report.setTipologiaMap(tipologiaMap);
		Long num = attivitaMap.get(Stati.in_attesa) != null ? attivitaMap.get(Stati.in_attesa) : 0L;
		report.setNumeroAttivitaInAttesa(num);
		num = attivitaMap.get(Stati.in_corso) != null ? attivitaMap.get(Stati.in_corso) : 0L;
		report.setNumeroAttivitaInCorso(num);
		num = attivitaMap.get(Stati.revisione) != null ? attivitaMap.get(Stati.revisione) : 0L;
		report.setNumeroAttivitaInRevisione(num);
		report.setClassiMap(classiMap);
		return report;
	}
	
	class MediaOre {
		public long oreSvolte;
		public int numStudenti;
	}
	
	
}
