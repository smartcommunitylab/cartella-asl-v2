package it.smartcommunitylab.cartella.asl.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza.Stati;
import it.smartcommunitylab.cartella.asl.model.report.MediaOre;
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
				+ " WHERE aa.istitutoId=(:istitutoId) AND aa.annoScolastico=(:annoScolastico) AND aa.stato!='archiviata'";
		TypedQuery<AttivitaAlternanza> attivitaQuery = em.createQuery(q, AttivitaAlternanza.class);
		attivitaQuery.setParameter("istitutoId", istitutoId);
		attivitaQuery.setParameter("annoScolastico", annoScolastico);		
		Stream<AttivitaAlternanza> stream = attivitaQuery.getResultStream();
		Map<Stati, Long> statoAttivitaMap = new HashMap<>();
		statoAttivitaMap.put(Stati.in_attesa, 0L);
		statoAttivitaMap.put(Stati.in_corso, 0L);
		statoAttivitaMap.put(Stati.revisione, 0L);
		stream.forEach(attivita -> {
			Stati stato = attivitaAlternanzaManager.getStato(attivita);
			if(statoAttivitaMap.containsKey(stato)) {
				statoAttivitaMap.put(stato, statoAttivitaMap.get(stato) + 1);
			}
		});
		
		q = "SELECT aa.tipologia, SUM(pg.oreSvolte) FROM AttivitaAlternanza aa, EsperienzaSvolta es, PresenzaGiornaliera pg WHERE"
				+ " es.attivitaAlternanzaId=aa.id AND pg.esperienzaSvoltaId=es.id AND aa.istitutoId=(:istitutoId) AND aa.annoScolastico=(:annoScolastico)"
				+ " AND pg.verificata=true GROUP BY aa.tipologia";
		Query queryGen = em.createQuery(q);
		queryGen.setParameter("istitutoId", istitutoId);
		queryGen.setParameter("annoScolastico", annoScolastico);
		List<Object[]> result = queryGen.getResultList();
		Map<Integer, Long> oreTipologiaMap = new TreeMap<>();
		long numeroOreTotali = 0L;
		for (Object[] obj : result) {
			Integer tipologia = (Integer) obj[0];
			long oreSvolteTotali = (long) obj[1];
			oreTipologiaMap.put(tipologia, oreSvolteTotali);
			numeroOreTotali += oreSvolteTotali;
		}		
		
		q = "SELECT es.classeStudente, es.studenteId, SUM(pg.oreSvolte) FROM AttivitaAlternanza aa, EsperienzaSvolta es, PresenzaGiornaliera pg WHERE"
				+ " es.attivitaAlternanzaId=aa.id AND pg.esperienzaSvoltaId=es.id AND aa.istitutoId=(:istitutoId) AND aa.annoScolastico=(:annoScolastico)"
				+ " AND pg.verificata=true GROUP BY es.classeStudente, es.studenteId";
		queryGen = em.createQuery(q);
		queryGen.setParameter("istitutoId", istitutoId);
		queryGen.setParameter("annoScolastico", annoScolastico);
		result = queryGen.getResultList();
		Map<String, MediaOre> classiMediaMap = new TreeMap<>();
		for (Object[] obj : result) {
			String classe = (String) obj[0];
			//String studenteId = (String) obj[1];
			long oreSvolteTotali = (long) obj[2];
			if(!classiMediaMap.containsKey(classe)) {
				classiMediaMap.put(classe, new MediaOre());
			}
			MediaOre mediaOre = classiMediaMap.get(classe);
			if(mediaOre != null) {
				mediaOre.addOreStudente(oreSvolteTotali);
			}
		}		
		
		ReportDashboardIstituto report = new ReportDashboardIstituto();
		report.setNumeroOreTotali(numeroOreTotali);
		report.setNumeroAttivitaInAttesa(statoAttivitaMap.get(Stati.in_attesa));
		report.setNumeroAttivitaInCorso(statoAttivitaMap.get(Stati.in_corso));
		report.setNumeroAttivitaInRevisione(statoAttivitaMap.get(Stati.revisione));
		report.setOreClassiMap(classiMediaMap);
		report.setOreTipologiaMap(oreTipologiaMap);
		return report;
	}
	
}
