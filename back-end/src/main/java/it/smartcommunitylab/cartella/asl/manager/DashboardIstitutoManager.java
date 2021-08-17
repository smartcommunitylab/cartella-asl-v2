package it.smartcommunitylab.cartella.asl.manager;

import java.util.ArrayList;
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

import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza.Stati;
import it.smartcommunitylab.cartella.asl.model.Studente;
import it.smartcommunitylab.cartella.asl.model.report.MediaOreClasse;
import it.smartcommunitylab.cartella.asl.model.report.OreStudente;
import it.smartcommunitylab.cartella.asl.model.report.ReportDashboardIstituto;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;
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
	@Autowired
	RegistrazioneDocenteManager registrazioneDocenteManager;
	@Autowired
	ASLRolesValidator usersValidator;

	public List<String> getClassi(String istitutoId, String annoScolastico, ASLUser user) throws Exception {
		boolean tutorClasse = usersValidator.hasRole(user, ASLRole.TUTOR_CLASSE, istitutoId);
		List<String> classiAssociate = registrazioneDocenteManager.getClassiAssociateRegistrazioneDocente(istitutoId, user.getCf());

		String q = "SELECT DISTINCT r0.classroom FROM Registration r0 WHERE r0.instituteId=(:istitutoId)"
				+ " AND r0.schoolYear=(:annoScolastico)";
		if(tutorClasse) {
			q = q + " AND r0.classroom IN (:classiAssociate)";
		}
		q =	q + " ORDER BY r0.classroom ASC";

		TypedQuery<String> query = em.createQuery(q, String.class);
		query.setParameter("istitutoId", istitutoId);
		query.setParameter("annoScolastico", annoScolastico);		
		if(tutorClasse) {
			query.setParameter("classiAssociate", classiAssociate);
		}
		List<String> list = query.getResultList();
		return list;
	}
	
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
		Map<String, MediaOreClasse> classiMediaMap = new TreeMap<>();
		for (Object[] obj : result) {
			String classe = (String) obj[0];
			//String studenteId = (String) obj[1];
			long oreSvolteTotali = (long) obj[2];
			if(!classiMediaMap.containsKey(classe)) {
				MediaOreClasse media = new MediaOreClasse();
				media.setClasse(classe);
				classiMediaMap.put(classe, media);
			}
			MediaOreClasse mediaOre = classiMediaMap.get(classe);
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
	
	@SuppressWarnings("unchecked")
	public ReportDashboardIstituto getReportUtilizzoClasse(String istitutoId, String annoScolastico,
			String classe, ASLUser user) throws Exception {
		boolean tutorClasse = usersValidator.hasRole(user, ASLRole.TUTOR_CLASSE, istitutoId);
		List<String> classiAssociate = registrazioneDocenteManager.getClassiAssociateRegistrazioneDocente(istitutoId, user.getCf());
		if(tutorClasse) {
			if(!classiAssociate.contains(classe)) {
				throw new BadRequestException("classe non gestita");
			}
		}
		String q = "SELECT DISTINCT s0 FROM Studente s0, Registration r0 WHERE s0.id = r0.studentId"
			+ " AND r0.instituteId=(:istitutoId) AND r0.schoolYear=(:annoScolastico) AND r0.classroom=(:classe)"
			+ " ORDER BY s0.surname, s0.name";

		TypedQuery<Studente> queryStudenti = em.createQuery(q, Studente.class);
		queryStudenti.setParameter("istitutoId", istitutoId);
		queryStudenti.setParameter("annoScolastico", annoScolastico);
		queryStudenti.setParameter("classe", classe);
		List<Studente> studentiList = queryStudenti.getResultList();
		Map<String, OreStudente> oreStudentiMap = new TreeMap<>();
		List<String> studentiIdList = new ArrayList<>();
		for(Studente s : studentiList) {
			studentiIdList.add(s.getId());
			//ore interne
			String qInterne = "SELECT SUM(pg.oreSvolte) FROM AttivitaAlternanza aa, EsperienzaSvolta es, PresenzaGiornaliera pg WHERE"
					+ " es.attivitaAlternanzaId=aa.id AND pg.esperienzaSvoltaId=es.id AND aa.istitutoId=(:istitutoId)"
					+ " AND es.studenteId=(:studenteId) AND pg.verificata=true"
					+ " AND aa.tipologia IN (1,2,3,4,5)";
			TypedQuery<Long> query = em.createQuery(qInterne, Long.class);
			query.setParameter("istitutoId", istitutoId);
			query.setParameter("studenteId", s.getId());			
			Long oreInterne = query.getSingleResult();
			
			//ore esterne
			String qEsterne = "SELECT SUM(pg.oreSvolte) FROM AttivitaAlternanza aa, EsperienzaSvolta es, PresenzaGiornaliera pg WHERE"
					+ " es.attivitaAlternanzaId=aa.id AND pg.esperienzaSvoltaId=es.id AND aa.istitutoId=(:istitutoId)"
					+ " AND es.studenteId=(:studenteId) AND pg.verificata=true"
					+ " AND aa.tipologia IN (6,7,8,9,10,11,12)";
			query = em.createQuery(qEsterne, Long.class);
			query.setParameter("istitutoId", istitutoId);
			query.setParameter("studenteId", s.getId());			
			Long oreEsterne = query.getSingleResult();
			
			//ore da validare
			String qValidare = "SELECT SUM(pg.oreSvolte) FROM AttivitaAlternanza aa, EsperienzaSvolta es, PresenzaGiornaliera pg WHERE"
					+ " es.attivitaAlternanzaId=aa.id AND pg.esperienzaSvoltaId=es.id AND aa.istitutoId=(:istitutoId)"
					+ " AND es.studenteId=(:studenteId) AND pg.verificata=false";
			query = em.createQuery(qValidare, Long.class);
			query.setParameter("istitutoId", istitutoId);
			query.setParameter("studenteId", s.getId());			
			Long oreDaValidare = query.getSingleResult();
			
			OreStudente oreStudente = new OreStudente();
			oreStudente.setStudenteId(s.getId());
			oreStudente.setNominativo(s.getSurname() + " " + s.getName());
			oreStudente.setOreInterne(oreInterne != null ? oreInterne : 0);
			oreStudente.setOreEsterne(oreEsterne != null ? oreEsterne : 0);
			oreStudente.setOreDaValidare(oreDaValidare != null ? oreDaValidare : 0);
			oreStudentiMap.put(oreStudente.getNominativo(), oreStudente);
		}

		q = "SELECT DISTINCT aa FROM AttivitaAlternanza aa, EsperienzaSvolta es"
				+ " WHERE es.attivitaAlternanzaId=aa.id AND aa.istitutoId=(:istitutoId)"
				+ " AND aa.stato!='archiviata' AND es.studenteId IN (:studenti)";
		TypedQuery<AttivitaAlternanza> attivitaQuery = em.createQuery(q, AttivitaAlternanza.class);
		attivitaQuery.setParameter("istitutoId", istitutoId);
		attivitaQuery.setParameter("studenti", studentiIdList);
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
				+ " es.attivitaAlternanzaId=aa.id AND pg.esperienzaSvoltaId=es.id AND aa.istitutoId=(:istitutoId)"
				+ " AND pg.verificata=true AND es.studenteId IN (:studenti) GROUP BY aa.tipologia";
		Query queryGen = em.createQuery(q);
		queryGen.setParameter("istitutoId", istitutoId);
		queryGen.setParameter("studenti", studentiIdList);
		List<Object[]> result = queryGen.getResultList();
		Map<Integer, Long> oreTipologiaMap = new TreeMap<>();
		long numeroOreTotali = 0L;
		for (Object[] obj : result) {
			Integer tipologia = (Integer) obj[0];
			long oreSvolteTotali = (long) obj[1];
			oreTipologiaMap.put(tipologia, oreSvolteTotali);
			numeroOreTotali += oreSvolteTotali;
		}		

		ReportDashboardIstituto report = new ReportDashboardIstituto();
		report.setNumeroOreTotali(numeroOreTotali);
		report.setNumeroAttivitaInAttesa(statoAttivitaMap.get(Stati.in_attesa));
		report.setNumeroAttivitaInCorso(statoAttivitaMap.get(Stati.in_corso));
		report.setNumeroAttivitaInRevisione(statoAttivitaMap.get(Stati.revisione));
		report.setOreTipologiaMap(oreTipologiaMap);
		report.setOreStudentiMap(oreStudentiMap);
		return report;	
	}
	
}
