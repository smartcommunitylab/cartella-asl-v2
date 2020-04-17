package it.smartcommunitylab.cartella.asl.manager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;

import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.CorsoDiStudioBean;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;
import it.smartcommunitylab.cartella.asl.model.Istituzione;
import it.smartcommunitylab.cartella.asl.model.PianoAlternanza;
import it.smartcommunitylab.cartella.asl.model.Registration;
import it.smartcommunitylab.cartella.asl.model.Studente;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;
import it.smartcommunitylab.cartella.asl.model.users.ASLUserRole;
import it.smartcommunitylab.cartella.asl.repository.PianoAlternanzaRepository;
import it.smartcommunitylab.cartella.asl.util.AttivitaAlternanzaComparator;
import it.smartcommunitylab.cartella.asl.util.Constants;
import it.smartcommunitylab.cartella.asl.util.ErrorLabelManager;
import it.smartcommunitylab.cartella.asl.util.EsperienzeSvoltaComparator;

@Repository
@Transactional
public class QueriesManager {

	@Autowired
	protected EntityManager em;

	@Autowired
	private EsperienzeSvoltaComparator esSvoltaComparator;

	@Autowired
	private AttivitaAlternanzaComparator aaCompataror;
	
	@Autowired
	private ASLRolesValidator usersValidator;		

	@Autowired
	private ErrorLabelManager errorLabelManager;
	
	@Autowired
	private PianoAlternanzaRepository pianoAlternanzaRepository;

	private static final String ESPERIENZA_SVOLTA_BY_ISTITUTO_AND_AZIENDA_ID = "SELECT DISTINCT es0 FROM EsperienzaSvolta es0 JOIN es0.attivitaAlternanza aa0 JOIN aa0.opportunita o0 JOIN o0.azienda az0 "
			+ "WHERE az0.id = (:aziendaId) ";
	private static final String ATTIVITA_ALTERNANZA_BY_ISTITUTO_AND_AZIENDA_ID = "SELECT DISTINCT aa0 FROM AttivitaAlternanza aa0 JOIN aa0.opportunita o0 JOIN o0.azienda az0 "
			+ "WHERE az0.id = (:aziendaId) ";

	private static final String ESPERIENZA_SVOLTA_FOR_ECCEZIONI = "SELECT DISTINCT es0 FROM EsperienzaSvolta es0 JOIN es0.attivitaAlternanza aa0 WHERE aa0.istitutoId = (:istitutoId) AND aa0.gestioneEccezione IS NULL ";

	private static final String ESPERIENZA_SVOLTA = "SELECT DISTINCT es0 FROM EsperienzaSvolta es0 LEFT JOIN FETCH es0.schedaValutazioneAzienda LEFT JOIN FETCH es0.schedaValutazioneStudente WHERE es0.id = (:id)";

	private static final String OPPORTUNITA_BY_AZIENDA_ID = "SELECT DISTINCT o0 FROM Opportunita o0 LEFT JOIN o0.azienda az0 WHERE az0.id = (:id)";

	private static final String OPPORTUNITA_BY_ISTITUTO_ID = "SELECT DISTINCT o0 FROM Opportunita o0 WHERE o0.istitutoId = (:id)";

	private static final String PIANO_ALTERNANZA_BY_AZIENDA_AND_CORSO_INUSO = "SELECT DISTINCT pa0.id FROM PianoAlternanza pa0, AttivitaAlternanza ata0, EsperienzaSvolta es0 INNER JOIN pa0.anniAlternanza aa0 WHERE pa0.corsoDiStudioId = (:corsoDiStudioId) AND pa0.istitutoId = (:istitutoId) AND aa0.id = ata0.annoAlternanza.id AND ata0.id = es0.attivitaAlternanza.id AND ata0.dataFine > (:now)";
//	private static final String PIANO_ALTERNANZA_BY_AZIENDA_AND_CORSO_INUSO = "SELECT DISTINCT pa0.id FROM PianoAlternanza pa0, AttivitaAlternanza ata0 INNER JOIN pa0.anniAlternanza aa0 WHERE pa0.corsoDiStudioId = (:corsoDiStudioId) AND pa0.istitutoId = (:istitutoId) AND aa0.id = ata0.annoAlternanza.id AND ata0.dataFine > (:now)";

	private static final String OPPCORSI = "SELECT DISTINCT oc0 FROM OppCorso oc0 LEFT JOIN oc0.competenze c0 #OPPCORSI_AA# ";
	private static final String OPPCORSI_AA = " ";

	private static final String OPPCORSI_BY_ISTITUTO = "SELECT DISTINCT oc0 FROM CorsoInterno oc0 where oc0.istitutoId =(:id)";

	private static final String STUDENT_REGISTRATION = "SELECT r0 FROM Registration r0 WHERE r0.studentId = (:id) AND r0.dateTo = (SELECT max(rm.dateTo) FROM Registration rm WHERE rm.studentId = (:id)) ";
	
	private static final String[] COMPETENZE_FIELDS = { "titolo", "profilo", "conoscenze", "abilita" };

	public static final String ESPERIENZA_SVOLTA_BY_STUDENTE_ID = "SELECT DISTINCT es0 FROM EsperienzaSvolta es0 JOIN es0.attivitaAlternanza aa0 JOIN es0.studente s0 WHERE s0.id = (:studenteId)";

	private static final String ASLUSERS = "SELECT DISTINCT u FROM ASLUser u  ";

	
	private static final String ISTITUTI = "SELECT DISTINCT ist FROM Istituzione ist ";
	private static final String STUDENTI = "SELECT DISTINCT st FROM Studente st ";

	private static final String ATTIVITA_ALTERNANZA_BY_ISTITUTO_ID = "SELECT DISTINCT aa0 FROM AttivitaAlternanza aa0 JOIN aa0.opportunita o0 where aa0.istitutoId = (:istitutoId)";

	private static final String ESPERIENZA_SVOLTA_BY_ISTITUTO_ID = "SELECT DISTINCT es0 FROM EsperienzaSvolta es0 JOIN es0.attivitaAlternanza aa0 JOIN aa0.opportunita where aa0.istitutoId = (:istitutoId)";

	private static final String STUDENTE_BY_CLASSE = "SELECT s,r FROM Studente s, Registration r WHERE s.id = r.studentId AND r.classroom = (:classe) AND r.courseId = (:corsoId) AND r.instituteId = (:istitutoId) AND r.dateTo = (SELECT max(rm.dateTo) FROM Registration rm WHERE rm.studentId = s.id)";
	
	private static final String PIANO_ALTERNANZA_TIPO_ATTIVITA_AND_COMPETENZE_ASSOCIATE = "SELECT pa0,ta0,asc0 FROM PianoAlternanza pa0, TipologiaAttivita ta0, AssociazioneCompetenze asc0 WHERE pa0.corsoDiStudioId = (:corsoDiStudioId) AND pa0.istitutoId = (:istitutoId) AND ta0.pianoAlternanzaId = pa0.id AND asc0.risorsaId = pa0.uuid";
	
	public Page<AttivitaAlternanza> findAttivitaAlternanzaByIstitutoAndAziendaIds(String istitutoId, String aziendaId,
			Long dataInizio, Long dataFine, List<Integer> tipologia, Boolean individuale, Pageable pageRequest) {
		StringBuilder sb = new StringBuilder(ATTIVITA_ALTERNANZA_BY_ISTITUTO_AND_AZIENDA_ID);
		if (istitutoId != null && !istitutoId.isEmpty()) {
			sb.append(" AND aa0.istitutoId = (:istitutoId) ");
		}
		if (dataInizio != null) {
			sb.append(" AND aa0.dataInizio >= (:dataInizio) ");
		}
		if (dataFine != null) {
			sb.append(" AND aa0.dataFine <= (:dataFine) ");
		}
		if (tipologia != null && !tipologia.isEmpty()) {
			sb.append(" AND aa0.tipologia IN (:tipologia) ");
		}
		if (individuale != null) {
			sb.append(" AND aa0.individuale = (:individuale)");
		}

		sb.append(" ORDER BY aa0.dataInizio");

		String q = sb.toString();
		TypedQuery<AttivitaAlternanza> query = em.createQuery(q, AttivitaAlternanza.class);

		if (istitutoId != null && !istitutoId.isEmpty()) {
			query.setParameter("istitutoId", istitutoId);
		}
		query.setParameter("aziendaId", aziendaId);
		if (dataInizio != null) {
			query.setParameter("dataInizio", dataInizio);
		}
		if (dataFine != null) {
			query.setParameter("dataFine", dataFine);
		}
		if (tipologia != null && !tipologia.isEmpty()) {
			query.setParameter("tipologia", tipologia);
		}
		if (individuale != null) {
			query.setParameter("individuale", individuale);
		}

		Query cQuery = queryToCountQuery(q, query);
		query.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
		query.setMaxResults(pageRequest.getPageSize());
		List<AttivitaAlternanza> result = query.getResultList();

		long t = (Long) cQuery.getSingleResult();

		// sort list by AA->dataInizio.
		// Collections.sort(result, aaCompataror);

		// long total = result.size();
		// int from = pageRequest.getPageNumber() * pageRequest.getPageSize();
		// int to = Math.min(result.size(), (pageRequest.getPageNumber() + 1) *
		// pageRequest.getPageSize());
		// if (to > from) {
		// result = result.subList(from, to);
		// } else {
		// result = Lists.newArrayList();
		// }

		Page<AttivitaAlternanza> page = new PageImpl<AttivitaAlternanza>(result, pageRequest, t);

		return page;
	}

	public Page<EsperienzaSvolta> findEsperienzaSvoltaByIstitutoAndAziendaIds(String istitutoId, String aziendaId,
			Long dataInizio, Long dataFine, List<Integer> stato, List<Integer> tipologia, String filterText,
			Boolean individuale, Pageable pageRequest) {
		StringBuilder sb = new StringBuilder(ESPERIENZA_SVOLTA_BY_ISTITUTO_AND_AZIENDA_ID);
		if (istitutoId != null && !istitutoId.isEmpty()) {
			sb.append(" AND aa0.istitutoId = (:istitutoId) ");
		}
		if (dataInizio != null) {
			sb.append(" AND aa0.dataInizio >= (:dataInizio) ");
		}
		if (dataFine != null) {
			sb.append(" AND aa0.dataFine <= (:dataFine) ");
		}
		if (stato != null && !stato.isEmpty()) {
			sb.append(" AND es0.stato IN (:stato) ");
		}
		if (tipologia != null && !tipologia.isEmpty()) {
			sb.append(" AND aa0.tipologia IN (:tipologia) ");
		}
		if (filterText != null && !filterText.isEmpty()) {
			String ft = filterText.toLowerCase();
			sb.append(" AND (lower(es0.nome) LIKE (:filterText) OR lower(es0.noteStudente) LIKE (:filterText) OR lower(es0.noteAzienda) LIKE (:filterText))");
		}
		if (individuale != null) {
			sb.append(" AND aa0.individuale = (:individuale)");
		}

		// order by attivita_alternanza.dataInizio
//		sb.append(" ORDER BY aa0.dataInizio");

		String q = sb.toString();
		TypedQuery<EsperienzaSvolta> query = em.createQuery(q, EsperienzaSvolta.class);

		if (istitutoId != null && !istitutoId.isEmpty()) {
			query.setParameter("istitutoId", istitutoId);
		}
		query.setParameter("aziendaId", aziendaId);

		if (dataInizio != null) {
			query.setParameter("dataInizio", dataInizio);
		}
		if (dataFine != null) {
			query.setParameter("dataFine", dataFine);
		}
		if (stato != null && !stato.isEmpty()) {
			query.setParameter("stato", stato);
		}
		if (tipologia != null && !tipologia.isEmpty()) {
			query.setParameter("tipologia", tipologia);
		}
		if (filterText != null && !filterText.isEmpty()) {
			query.setParameter("filterText", "%" + filterText + "%");
		}
		if (individuale != null) {
			query.setParameter("individuale", individuale);
		}

		List<EsperienzaSvolta> result = query.getResultList();

		// sort list by AA->dataInizio.
		 Collections.sort(result, esSvoltaComparator);

		long total = result.size();
		int from = pageRequest.getPageNumber() * pageRequest.getPageSize();
		int to = Math.min(result.size(), (pageRequest.getPageNumber() + 1) * pageRequest.getPageSize());
		if (to > from) {
			result = result.subList(from, to);
		} else {
			result = Lists.newArrayList();
		}

		Page<EsperienzaSvolta> page = new PageImpl<EsperienzaSvolta>(result, pageRequest, total);

		return page;
	}

	public EsperienzaSvolta findEsperienzaSvoltaById(long id) {
		TypedQuery<EsperienzaSvolta> query = em.createQuery(ESPERIENZA_SVOLTA, EsperienzaSvolta.class);

		query.setParameter("id", id);

		return query.getSingleResult();
	}

//	public Page<Opportunita> findOpportunitaByAziendaId(String aziendaId, Long dataInizio, Long dataFine,
//			List<Integer> tipologia, String filterText, Pageable pageRequest) {
//		StringBuilder sb = new StringBuilder(OPPORTUNITA_BY_AZIENDA_ID);
//		if (dataInizio != null) {
//			sb.append(" AND o0.dataInizio >= (:dataInizio) ");
//		}
//		if (dataFine != null) {
//			sb.append(" AND o0.dataFine <= (:dataFine) ");
//		}
//		if (tipologia != null && !tipologia.isEmpty()) {
//			sb.append(" AND o0.tipologia IN (:tipologia) ");
//		}
//		if (filterText != null && !filterText.isEmpty()) {
//			String ft = filterText.toLowerCase();
//			sb.append(" AND (lower(o0.titolo) LIKE (:filterText) OR lower(o0.descrizione) LIKE (:filterText))");
//		}
//
//		// order by dataInizio.
//		sb.append(" order by o0.dataInizio");
//
//		String q = sb.toString();
//		TypedQuery<Opportunita> query = em.createQuery(q, Opportunita.class);
//
//		if (dataInizio != null) {
//			query.setParameter("dataInizio", dataInizio);
//		}
//		if (dataFine != null) {
//			query.setParameter("dataFine", dataFine);
//		}
//		if (tipologia != null && !tipologia.isEmpty()) {
//			query.setParameter("tipologia", tipologia);
//		}
//		if (filterText != null && !filterText.isEmpty()) {
//			query.setParameter("filterText", "%" + filterText + "%");
//		}
//
//		query.setParameter("id", aziendaId);
//
//		Query cQuery = queryToCountQuery(q, query);
//		query.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
//		query.setMaxResults(pageRequest.getPageSize());
//		List<Opportunita> result = query.getResultList();
//
//		long t = (Long) cQuery.getSingleResult();
//
//		// long total = result.size();
//		// int from = pageRequest.getPageNumber() * pageRequest.getPageSize();
//		// int to = Math.min(result.size(), (pageRequest.getPageNumber() + 1) *
//		// pageRequest.getPageSize());
//		// if (to > from) {
//		// result = result.subList(from, to);
//		// } else {
//		// result = Lists.newArrayList();
//		// }
//
//		Page<Opportunita> page = new PageImpl<Opportunita>(result, pageRequest, t);
//
//		return page;
//	}

//	public Page<Opportunita> findOpportunitaByIstitutoId(String istitutoId, Long dataInizio, Long dataFine,
//			List<Integer> tipologia, String filterText, Pageable pageRequest) {
//		StringBuilder sb = new StringBuilder(OPPORTUNITA_BY_ISTITUTO_ID);
//		if (dataInizio != null) {
//			sb.append(" AND o0.dataInizio >= (:dataInizio) ");
//		}
//		if (dataFine != null) {
//			sb.append(" AND o0.dataFine <= (:dataFine) ");
//		}
//		if (tipologia != null && !tipologia.isEmpty()) {
//			sb.append(" AND o0.tipologia IN (:tipologia) ");
//		}
//		if (filterText != null && !filterText.isEmpty()) {
//			String ft = filterText.toLowerCase();
//			sb.append(" AND (lower(o0.titolo) LIKE (:filterText) OR lower(o0.descrizione) LIKE (:filterText))");
//		}
//
//		// order by dataInizio.
//		sb.append(" order by o0.dataInizio");
//
//		String q = sb.toString();
//		TypedQuery<Opportunita> query = em.createQuery(q, Opportunita.class);
//
//		if (dataInizio != null) {
//			query.setParameter("dataInizio", dataInizio);
//		}
//		if (dataFine != null) {
//			query.setParameter("dataFine", dataFine);
//		}
//		if (tipologia != null && !tipologia.isEmpty()) {
//			query.setParameter("tipologia", tipologia);
//		}
//		if (filterText != null && !filterText.isEmpty()) {
//			query.setParameter("filterText", "%" + filterText + "%");
//		}
//
//		query.setParameter("id", istitutoId);
//
//		Query cQuery = queryToCountQuery(q, query);
//		query.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
//		query.setMaxResults(pageRequest.getPageSize());
//		List<Opportunita> result = query.getResultList();
//
//		long t = (Long) cQuery.getSingleResult();
//
//		Page<Opportunita> page = new PageImpl<Opportunita>(result, pageRequest, t);
//
//		return page;
//	}

//	public Page<CorsoInterno> findCorsoInternoByIstitutoId(String istitutoId, Long dataInizio, Long dataFine,
//			List<Integer> tipologia, String filterText, Pageable pageRequest) {
//		StringBuilder sb = new StringBuilder(OPPCORSI_BY_ISTITUTO);
//
//		if (dataInizio != null) {
//			sb.append(" AND oc0.dataInizio >= (:dataInizio) ");
//		}
//		if (dataFine != null) {
//			sb.append(" AND oc0.dataFine <= (:dataFine) ");
//		}
//		if (tipologia != null && !tipologia.isEmpty()) {
//			sb.append(" AND oc0.tipologia IN (:tipologia) ");
//		}
//		if (filterText != null && !filterText.isEmpty()) {
//			sb.append(" AND (lower(oc0.titolo) LIKE (:filterText) OR lower(oc0.descrizione) LIKE (:filterText))");
//		}
//
//		// order by dataInizio.
//		sb.append(" order by oc0.dataInizio");
//
//		String q = sb.toString();
//		TypedQuery<CorsoInterno> query = em.createQuery(q, CorsoInterno.class);
//
//		query.setParameter("id", istitutoId);
//
//		if (dataInizio != null) {
//			query.setParameter("dataInizio", dataInizio);
//		}
//		if (dataFine != null) {
//			query.setParameter("dataFine", dataFine);
//		}
//		if (tipologia != null && !tipologia.isEmpty()) {
//			query.setParameter("tipologia", tipologia);
//		}
//		if (filterText != null && !filterText.isEmpty()) {
//			query.setParameter("filterText", "%" + filterText + "%");
//		}
//
//		Query cQuery = queryToCountQuery(q, query);
//		query.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
//		query.setMaxResults(pageRequest.getPageSize());
//		List<CorsoInterno> result = query.getResultList();
//
//		long t = (Long) cQuery.getSingleResult();
//
//		// long total = result.size();
//		// int from = pageRequest.getPageNumber() * pageRequest.getPageSize();
//		// int to = Math.min(result.size(), (pageRequest.getPageNumber() + 1) *
//		// pageRequest.getPageSize());
//		// if (to > from) {
//		// result = result.subList(from, to);
//		// } else {
//		// result = Lists.newArrayList();
//		// }
//
//		Page<CorsoInterno> page = new PageImpl<CorsoInterno>(result, pageRequest, t);
//
//		return page;
//	}

	// TODO pagination with intersection?
//	public Page<OppCorso> findOppCorsi(String istitutoId, Integer tipologia, List<Long> competenze, Long dataInizio,
//			Long dataFine, double[] coordinate, Integer raggio, Boolean individuale, String titolo, String orderBy, Pageable pageRequest)
//			throws Exception {
//		StringBuilder sb = new StringBuilder(OPPCORSI);
//		boolean geoQuery = false;
//		if (coordinate != null && raggio != null) {
//			geoQuery = true;
//		}
//
//		StringBuilder asb = new StringBuilder(OPPCORSI_AA);
//		boolean changeAA = false;
//		if (dataInizio != null) {
//			asb.append(" AND oc0.dataInizio >= (:dataInizio) ");
//			changeAA = true;
//		}
//		if (dataFine != null) {
//			asb.append(" AND oc0.dataFine <= (:dataFine) ");
//			changeAA = true;
//		}
//		if (tipologia != null) {
//			sb.append(" AND oc0.tipologia = (:tipologia) ");
//		}
//		if (competenze != null && !competenze.isEmpty()) {
//			sb.append(" AND c0.id IN (:competenze) ");
//		}
//		if (istitutoId != null && !istitutoId.isEmpty()) {
//			sb.append(" AND (oc0.istitutoId = (:istitutoId) OR oc0.istitutoId IS NULL) ");
//		}
//		
//		if (titolo != null && !titolo.isEmpty()) {
//			sb.append(" AND (lower(oc0.titolo) LIKE (:titolo))");
//		}
//
//		sb.append(" ORDER BY oc0." + orderBy);
//
//		String q = sb.toString();
//
//		if (changeAA) {
//			String aq = asb.toString().replaceFirst(" AND ", " ");
//			aq = " WHERE (" + aq + ") ";
//			q = q.replace("#OPPCORSI_AA#", aq);
//		} else {
//			q = q.replace("#OPPCORSI_AA#", "").replaceFirst(" AND ", " WHERE ");
//		}
//
//		TypedQuery<OppCorso> query = em.createQuery(q, OppCorso.class);
//
//		if (dataInizio != null) {
//			query.setParameter("dataInizio", dataInizio);
//		}
//		if (dataFine != null) {
//			query.setParameter("dataFine", dataFine);
//		}
//		if (tipologia != null) {
//			query.setParameter("tipologia", tipologia);
//		}
//		if (competenze != null && !competenze.isEmpty()) {
//			query.setParameter("competenze", competenze);
//		}
//		if (istitutoId != null && !istitutoId.isEmpty()) {
//			query.setParameter("istitutoId", istitutoId);
//		}
//		if (titolo != null && !titolo.isEmpty()) {
//			query.setParameter("titolo", "%" + titolo.toLowerCase() + "%");
//		}
//
//		List<OppCorso> result = query.getResultList();
//
//		if (geoQuery) {
//			List<OppCorso> geoResult = findOppCorsoNear(coordinate[0], coordinate[1], raggio);
//			result = ListUtils.intersection(result, geoResult);
//		}
//
//		for (OppCorso oc : result) {
//			TipologiaTipologiaAttivita tta = getTipologiaTipologiaAttivita(oc.getTipologia());
//			if (tta == null) {
//				throw new BadRequestException(errorLabelManager.get("tipologia.tipo.error.notfound"));
//			}
//			oc.setIndividuale(tta.getIndividuale());
//			oc.setInterna(tta.getInterna());
//		}
//
//		if (individuale != null) {
//			result.removeIf(x -> x.isIndividuale() != individuale);
//		}
//		
//		result.removeIf(x -> x instanceof Opportunita && ((Opportunita)x).getPostiRimanenti() <= 0);
//
//		long total = result.size();
//		int from = pageRequest.getPageNumber() * pageRequest.getPageSize();
//		int to = Math.min(result.size(), (pageRequest.getPageNumber() + 1) * pageRequest.getPageSize());
//		if (to > from) {
//			result = result.subList(from, to);
//		} else {
//			result = Lists.newArrayList();
//		}
//
//		Page<OppCorso> page = new PageImpl<OppCorso>(result, pageRequest, total);
//
//		return page;
//	}

	public Page<PianoAlternanza> findPianoAlternanzaByIstitutoAndCorsoDiStudioIdsInUso(String istitutoId,
			String corsoDiStudioId, Pageable pageRequest) {
		StringBuilder sb = new StringBuilder(PIANO_ALTERNANZA_BY_AZIENDA_AND_CORSO_INUSO);

		String q = sb.toString();

		if (corsoDiStudioId == null || corsoDiStudioId.isEmpty()) {
			q = q.replace("pa0.corsoDiStudioId = (:corsoDiStudioId) AND", "");
		}

		TypedQuery<Long> query = em.createQuery(q, Long.class);

		if (corsoDiStudioId != null && !corsoDiStudioId.isEmpty()) {
			query.setParameter("corsoDiStudioId", corsoDiStudioId);
		}
		query.setParameter("istitutoId", istitutoId);
		query.setParameter("now", System.currentTimeMillis());
		List<Long> inUsoResult = query.getResultList();

		Page<PianoAlternanza> result = null;
/**		 NAWAZ
		if (corsoDiStudioId == null || corsoDiStudioId.isEmpty()) {
			result = pianoAlternanzaRepository.findPianoAlternanzaByIstitutoId(istitutoId, pageRequest);
		} else {
			result = pianoAlternanzaRepository.findPianoAlternanzaByCorsoDiStudioIdAndIstitutoId(corsoDiStudioId,
					istitutoId, pageRequest);
		}
 	result.forEach(x -> {
//			if (inUsoResult.contains(x.getId())) {
//				x.setInUso(true);
//			}
//		});**/

		return result;
	}

	// TODO pagination when removing
	public Page<PianoAlternanza> findPianoAlternanzaByIstitutoAndCorsoDiStudioIdsStato(String istitutoId,
			String corsoDiStudioId, String annoScolasticoCorrente, Integer stato, Pageable pageRequest) {
		List<PianoAlternanza> result = null;

		if (corsoDiStudioId == null || corsoDiStudioId.isEmpty()) {
			result = pianoAlternanzaRepository.findPianoAlternanzaByIstitutoId(istitutoId);
		} else {
			result = pianoAlternanzaRepository.findPianoAlternanzaByCorsoDiStudioIdAndIstitutoId(corsoDiStudioId,
					istitutoId);
		}

//	NAWAZ	result.forEach(x -> {
//			computeStatoForPianoAlternanza(x, annoScolasticoCorrente);
//		});
//
//		if (stato != null) {
//			result.removeIf(x -> x.getStato() != stato);
//		}
		long total = result.size();

		int from = pageRequest.getPageNumber() * pageRequest.getPageSize();
		int to = Math.min(result.size(), (pageRequest.getPageNumber() + 1) * pageRequest.getPageSize());
		if (to > from) {
			result = result.subList(from, to);
		} else {
			result = Lists.newArrayList();
		}

		Page<PianoAlternanza> page = new PageImpl<PianoAlternanza>(result, pageRequest, total);

		return page;
	}

	
	
	public List<Studente> findStudenti(String istitutoId, String corsoId, String annoScolastico, String classe,
			String nome) {
        StringBuilder sb = new StringBuilder(DataEntityManager.STUDENTS_AND_REGISTRATIONS_QUERY);

		if (corsoId != null && !corsoId.isEmpty()) {
			sb.append(" AND r0.courseId = (:courseId) ");
		}
		if (classe != null && !classe.isEmpty()) {
			sb.append(" AND r0.classroom = (:classroom) ");
		}
		if (nome != null && !nome.isEmpty()) {
			sb.append(" AND (lower(s0.surname) LIKE (:nome) OR lower(s0.name) LIKE (:nome))");
		}

		String q = sb.toString();

		Query query = em.createQuery(q);

		if (corsoId != null && !corsoId.isEmpty()) {
			query.setParameter("courseId", corsoId);
		}
		if (classe != null && !classe.isEmpty()) {
			query.setParameter("classroom", classe);
		}
		if (nome != null && !nome.isEmpty()) {
			query.setParameter("nome", "%" + nome.toLowerCase() + "%");
		}

		query.setParameter("instituteId", istitutoId);
		query.setParameter("annoScolastico", annoScolastico);

		List<Object[]> result = query.getResultList();

		List<Studente> students = Lists.newArrayList();
		for (Object[] obj : result) {
			Studente s = fillStudenteWithRegistration(obj);
			students.add(s);
		}

		return students;
	}


	
/** NAWAZ	public List<EccezioniGroup> findEccezioni(String istitutoId, String corsoDiStudioId, Integer annoCorso,
			Long dataInizio, Long dataFine, List<Integer> tipologia, String classe, String studenteId,
			Pageable pageRequest) throws BadRequestException {
		StringBuilder sb = new StringBuilder(ESPERIENZA_SVOLTA_FOR_ECCEZIONI);
		if (corsoDiStudioId != null && !corsoDiStudioId.isEmpty()) {
			sb.append(" AND aa0.corsoId = (:corsoId) ");
		}
		if (dataInizio != null) {
			sb.append(" AND aa0.dataInizio >= (:dataInizio) ");
		}
		if (dataFine != null) {
			sb.append(" AND aa0.dataFine <= (:dataFine) ");
		}
		if (tipologia != null && !tipologia.isEmpty()) {
			sb.append(" AND aa0.tipologia IN (:tipologia) ");
		}
		if (annoCorso != null) {
			sb.append(" AND aa0.annoCorso = (:annoCorso) ");
		}
		if (classe != null) {
			sb.append(" AND aa0.classe = (:classe) ");
		}
		if (studenteId != null) {
			sb.append(" AND es0.studente.id = (:studenteId) ");
		}

		// order by attivita_alternanza.dataInizio
//		sb.append(" ORDER BY aa0.dataInizio");

		String q = sb.toString();
		TypedQuery<EsperienzaSvolta> query = em.createQuery(q, EsperienzaSvolta.class);

		query.setParameter("istitutoId", istitutoId);

		if (corsoDiStudioId != null && !corsoDiStudioId.isEmpty()) {
			query.setParameter("corsoId", corsoDiStudioId);
		}
		if (dataInizio != null) {
			query.setParameter("dataInizio", dataInizio);
		}
		if (dataFine != null) {
			query.setParameter("dataFine", dataFine);
		}
		if (tipologia != null && !tipologia.isEmpty()) {
			query.setParameter("tipologia", tipologia);
		}
		if (annoCorso != null) {
			query.setParameter("annoCorso", annoCorso);
		}
		if (classe != null) {
			query.setParameter("classe", classe);
		}
		if (studenteId != null) {
			query.setParameter("studenteId", studenteId);
		}

		List<EsperienzaSvolta> result = query.getResultList();
		// Collections.sort(result, esSvoltaComparator);

		long now = System.currentTimeMillis();
		result.forEach(x -> x.getPresenze().computeOreSvolte());

//		result = result.stream().filter(x -> x.getPresenze().getOreSvolte() < x.getAttivitaAlternanza().getOre() * 0.8
//				&& x.getAttivitaAlternanza().getDataFine() < now).collect(Collectors.toList());
		
		result = result.stream()
				.filter(x -> !x.isCompletata() && x.isTerminata() && x.getAttivitaAlternanza().isCompletata())
				.collect(Collectors.toList());

		Collections.sort(result, esSvoltaComparator);
		
//		int total = result.size();
//		int from = pageRequest.getPageNumber() * pageRequest.getPageSize();
//		int to = Math.min(result.size(), (pageRequest.getPageNumber() + 1) * pageRequest.getPageSize());
//		if (to > from) {
//			result = result.subList(from, to);
//		} else {
//			result = Lists.newArrayList();
//		}

//		List<EccezioniGroup> eccezioni = buildEccezioni(result);

		return eccezioni;
	}*/

	/** NAWAZprivate List<EccezioniGroup> buildEccezioni(List<EsperienzaSvolta> esperienzeSvolte) throws BadRequestException {
		List<EccezioniGroup> result = Lists.newArrayList();

		Map<String, EccezioniGroup> groups = Maps.newTreeMap();

		for (EsperienzaSvolta es : esperienzeSvolte) {
			PianoAlternanza pa = pianoAlternanzaRepository
					.findPianoAlternanzaByAnniAlternanza(es.getAttivitaAlternanza().getAnnoAlternanza());

			if (pa == null) {
				throw new BadRequestException(errorLabelManager.get("piano.error.notfound"));
			}

			AttivitaAlternanza aa = es.getAttivitaAlternanza();
			
			EccezioniGroup group = groups.getOrDefault(aa.getCorsoId(),
					new EccezioniGroup(aa.getCorso(), aa.getCorsoId()));

			Eccezione eccezione = group.getEccezioni().getOrDefault(aa.getId(), new Eccezione(aa, pa.getId()));
			group.getEccezioni().put(aa.getId(), eccezione);

			DettaglioEccezione dettaglio = new DettaglioEccezione(es.getStudente().getId(),
					es.getStudente().getSurname() + " " + es.getStudente().getName(), findStudenteClassroomBySchoolYear(es.getStudente().getId(), aa.getAnnoScolastico(), aa.getIstitutoId(), aa.getCorsoId()),
					es.getId());
			eccezione.getDettagli().add(dettaglio);

			groups.put(group.getCorsoDiStudioId(), group);
		}

		return Lists.newArrayList(groups.values());
	}*/

	/** NAWAZ private void computeStatoForPianoAlternanza(PianoAlternanza pa, String annoScolastico) {
		if (pa.isAttivo()) {
			pa.setStato(0); // attivo
			return;
		}
		if (pa.getAnnoScolasticoDisattivazione() != null && Utils.annoScolasticoToInt(annoScolastico)
				- Utils.annoScolasticoToInt(pa.getAnnoScolasticoDisattivazione()) < 3) {
			pa.setStato(1); // in scadenza
		} else if (pa.getAnnoScolasticoDisattivazione() != null) {
			pa.setStato(2); // scaduto
		} else {
			pa.setStato(3); // creato non attivo
		}
	}*/

	
	public Page<EsperienzaSvolta> findAllEsperienzaSvoltaByStudenteId(String studenteId, Long dataInizio, Long dataFine,
			List<Integer> stato, List<Integer> tipologia, String filterText, Pageable pageRequest) {
		StringBuilder sb = new StringBuilder(ESPERIENZA_SVOLTA_BY_STUDENTE_ID);

		if (dataInizio != null) {
			sb.append(" AND aa0.dataInizio >= (:dataInizio) ");
		}
		if (dataFine != null) {
			sb.append(" AND aa0.dataFine <= (:dataFine) ");
		}
		if (stato != null && !stato.isEmpty()) {
			sb.append(" AND es0.stato IN (:stato) ");
		}
		if (tipologia != null && !tipologia.isEmpty()) {
			sb.append(" AND aa0.tipologia IN (:tipologia) ");
		}
		if (filterText != null && !filterText.isEmpty()) {
			String ft = filterText.toLowerCase();
			sb.append(
					" AND (lower(es0.nome) LIKE (:filterText) OR lower(es0.noteStudente) LIKE (:filterText) OR lower(es0.noteAzienda) LIKE (:filterText))");
		}

		// order by attivita_alternanza.dataInizio
		//sb.append(" order by aa0.dataInizio");

		String q = sb.toString();
		TypedQuery<EsperienzaSvolta> query = em.createQuery(q, EsperienzaSvolta.class);

		query.setParameter("studenteId", studenteId);

		if (dataInizio != null) {
			query.setParameter("dataInizio", dataInizio);
		}
		if (dataFine != null) {
			query.setParameter("dataFine", dataFine);
		}
		if (stato != null && !stato.isEmpty()) {
			query.setParameter("stato", stato);
		}
		if (tipologia != null && !tipologia.isEmpty()) {
			query.setParameter("tipologia", tipologia);
		}
		if (filterText != null && !filterText.isEmpty()) {
			query.setParameter("filterText", "%" + filterText + "%");
		}

		Query cQuery = queryToCountQuery(q, query);
		List<EsperienzaSvolta> result = query.getResultList();
		long t = (Long) cQuery.getSingleResult();
		
		// sort list by AA->dataInizio.
		Collections.sort(result, esSvoltaComparator);
		
		int from = pageRequest.getPageNumber() * pageRequest.getPageSize();
		int to = Math.min(result.size(), (pageRequest.getPageNumber() + 1) * pageRequest.getPageSize());
		if (to > from) {
			result = result.subList(from, to);
		} else {
			result = Lists.newArrayList();
		}

		Page<EsperienzaSvolta> page = new PageImpl<EsperienzaSvolta>(result, pageRequest, t);

		return page;

	}

	


	private TypedQuery<Long> queryToCountQuery(String q, Query query1) {
		String q2 = q.replaceAll("SELECT [^ ]* FROM", "SELECT COUNT(*) FROM");
		q2 = q2.replaceAll("SELECT DISTINCT ([^ ]*) FROM", "SELECT COUNT(DISTINCT $1) FROM");

		TypedQuery<Long> query2 = em.createQuery(q2, Long.class);
		for (Parameter param : query1.getParameters()) {
			query2.setParameter(param.getName(), query1.getParameterValue(param.getName()));
		}

		return query2;
	}

//	public Page<AttivitaAlternanza> findAttivitaAlternanzaByIstitutoId(String istitutoId, Long dataInizio, Long dataFine, List<Integer> tipologia, Boolean individuale, Boolean completata, Pageable pageRequest) {
//		StringBuilder sb = new StringBuilder(ATTIVITA_ALTERNANZA_BY_ISTITUTO_ID);
//		if (istitutoId != null && !istitutoId.isEmpty()) {
//			sb.append(" AND aa0.istitutoId = (:istitutoId) ");
//		}
//		if (dataInizio != null) {
//			sb.append(" AND aa0.dataInizio >= (:dataInizio) ");
//		}
//		if (dataFine != null) {
//			sb.append(" AND aa0.dataFine <= (:dataFine) ");
//		}
//		if (tipologia != null && !tipologia.isEmpty()) {
//			sb.append(" AND aa0.tipologia IN (:tipologia) ");
//		}
//		if (individuale != null) {
//			sb.append(" AND aa0.individuale = (:individuale)");
//		}
//		if (completata != null) {
//			sb.append(" AND aa0.completata = (:completata)");
//		}
//
//		sb.append(" ORDER BY aa0.dataInizio");
//
//		String q = sb.toString();
//		TypedQuery<AttivitaAlternanza> query = em.createQuery(q, AttivitaAlternanza.class);
//
//		if (istitutoId != null && !istitutoId.isEmpty()) {
//			query.setParameter("istitutoId", istitutoId);
//		}
//
//		if (dataInizio != null) {
//			query.setParameter("dataInizio", dataInizio);
//		}
//		if (dataFine != null) {
//			query.setParameter("dataFine", dataFine);
//		}
//		if (tipologia != null && !tipologia.isEmpty()) {
//			query.setParameter("tipologia", tipologia);
//		}
//		if (individuale != null) {
//			query.setParameter("individuale", individuale);
//		}
//		if (completata != null) {
//			query.setParameter("completata", completata);
//		}
//
//		Query cQuery = queryToCountQuery(q, query);
//		query.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
//		query.setMaxResults(pageRequest.getPageSize());
//		List<AttivitaAlternanza> result = query.getResultList();
//		
//		for (AttivitaAlternanza aa: result) {
//			List<EsperienzaSvolta> ess = findEsperienzaSvoltaByAttivitaAlternanza(aa);
//			for (EsperienzaSvolta es: ess) {
//				es.getPresenze().computeOreSvolte();
//			}
//			aa.getStudenti().addAll(ess.stream().filter(x -> x.getStudente() != null).map(x -> x.getStudente()).collect(Collectors.toSet()));
//		}
//
//		long t = (Long) cQuery.getSingleResult();
//
//		Page<AttivitaAlternanza> page = new PageImpl<AttivitaAlternanza>(result, pageRequest, t);
//
//		return page;
//	}

	public Page<EsperienzaSvolta> findEsperienzaSvoltaByIstitutoId(String istitutoId, Long dataInizio, Long dataFine,
			List<Integer> stato, List<Integer> tipologia, String filterText, Boolean individuale, Boolean terminata,
			String nomeStudente, Pageable pageRequest) {
		StringBuilder sb = new StringBuilder(ESPERIENZA_SVOLTA_BY_ISTITUTO_ID);
		if (istitutoId != null && !istitutoId.isEmpty()) {
			sb.append(" AND aa0.istitutoId = (:istitutoId) ");
		}
		if (dataInizio != null) {
			sb.append(" AND aa0.dataInizio >= (:dataInizio) ");
		}
		if (dataFine != null) {
			sb.append(" AND aa0.dataFine <= (:dataFine) ");
		}
		if (stato != null && !stato.isEmpty()) {
			sb.append(" AND es0.stato IN (:stato) ");
		}
		if (tipologia != null && !tipologia.isEmpty()) {
			sb.append(" AND aa0.tipologia IN (:tipologia) ");
		}
		if (filterText != null && !filterText.isEmpty()) {
			String ft = filterText.toLowerCase();
			sb.append(
					" AND (lower(es0.nome) LIKE (:filterText) OR lower(es0.noteStudente) LIKE (:filterText) OR lower(es0.noteAzienda) LIKE (:filterText))");
		}
		if (individuale != null) {
			sb.append(" AND aa0.individuale = (:individuale)");
		}
		if (terminata != null) {
			sb.append(" AND es0.terminata = (:terminata)");
		}
		if (nomeStudente != null) {
			sb.append(" AND (lower(es0.studente.surname) LIKE (:nomeStudente) OR lower(es0.studente.name) LIKE (:nomeStudente))");
		}

		String q = sb.toString();
		TypedQuery<EsperienzaSvolta> query = em.createQuery(q, EsperienzaSvolta.class);

		if (istitutoId != null && !istitutoId.isEmpty()) {
			query.setParameter("istitutoId", istitutoId);
		}

		if (dataInizio != null) {
			query.setParameter("dataInizio", dataInizio);
		}
		if (dataFine != null) {
			query.setParameter("dataFine", dataFine);
		}
		if (stato != null && !stato.isEmpty()) {
			query.setParameter("stato", stato);
		}
		if (tipologia != null && !tipologia.isEmpty()) {
			query.setParameter("tipologia", tipologia);
		}
		if (filterText != null && !filterText.isEmpty()) {
			query.setParameter("filterText", "%" + filterText + "%");
		}
		if (individuale != null) {
			query.setParameter("individuale", individuale);
		}
		if (terminata != null) {
			query.setParameter("terminata", terminata);
		}
		if (nomeStudente != null) {
			query.setParameter("nomeStudente", "%" + nomeStudente.trim() + "%");
		}

		List<EsperienzaSvolta> result = query.getResultList();

		// sort list by AA->dataInizio.
		 Collections.sort(result, esSvoltaComparator);

		long total = result.size();
		int from = pageRequest.getPageNumber() * pageRequest.getPageSize();
		int to = Math.min(result.size(), (pageRequest.getPageNumber() + 1) * pageRequest.getPageSize());
		if (to > from) {
			result = result.subList(from, to);
		} else {
			result = Lists.newArrayList();
		}

		Page<EsperienzaSvolta> page = new PageImpl<EsperienzaSvolta>(result, pageRequest, total);

		return page;
	}

	public Page<Studente> findStudentiByClasse(String classe, String corsoId, String istitutoId, String annoScolastico, Pageable pageRequest) {
		StringBuilder sb = new StringBuilder(STUDENTE_BY_CLASSE);

		String q = sb.toString();
		TypedQuery<Object[]> query = em.createQuery(q, Object[].class);

		query.setParameter("classe", classe);
		query.setParameter("corsoId", corsoId);
		query.setParameter("istitutoId", istitutoId);
		query.setParameter("annoScolastico", annoScolastico);

//		Query cQuery = queryToCountQuery(q, query);
//		query.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
//		query.setMaxResults(pageRequest.getPageSize());
		List<Object[]> result = query.getResultList();

		result.sort(new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				return ((Studente)o1[0]).compareTo((Studente)o2[0]);
			}
		});		
		
//		long t = (Long) cQuery.getSingleResult();

		int total = result.size();
		int from = pageRequest.getPageNumber() * pageRequest.getPageSize();
		int to = Math.min(result.size(), (pageRequest.getPageNumber() + 1) * pageRequest.getPageSize());
		if (to > from) {
			result = result.subList(from, to);
		} else {
			result = Lists.newArrayList();
		}

		List<Studente> studenti = Lists.newArrayList();
		
		for (Object[] sr: result) {
			Studente s = fillStudenteWithRegistration(sr);
			studenti.add(s);
		}
		
		Page<Studente> studentiPage = new PageImpl<Studente>(studenti, pageRequest, total);
		
		return studentiPage;
	}	

	

//	public void completaAttivitaAlternanza(long id, List<Presenze> updatedES) {
//		
//		// mark ES as per 'completa' flag set terminate to 'true'.
//		for (Presenze pz: updatedES) {
//			EsperienzaSvolta esp = findEsperienzaSvoltaById(pz.getEsperienzaSvoltaId());
//			esp.setCompletata(pz.isCompletato());
//			esp.setTerminata(true);
//			saveEsperienzaSvolta(esp);
//			// create esperienza_allineamento
//			if (esp.isCompletata()) {
//				EsperienzaSvoltaAllineamento esperienzaSvoltaAllineamento = esperienzaAllineamentoRepository
//						.findByEspSvoltaId(esp.getId());
//				if (esperienzaSvoltaAllineamento == null) {
//					esperienzaSvoltaAllineamento = new EsperienzaSvoltaAllineamento();
//					esperienzaSvoltaAllineamento.setEspSvoltaId(esp.getId());
//					esperienzaAllineamentoRepository.save(esperienzaSvoltaAllineamento);
//				};
//			}
//		}
//		// mark AA 'complete' flag to true.
//		AttivitaAlternanza aa = attivitaAlternanzaRepository.findOne(id);
//		aa.setCompletata(true);
//		attivitaAlternanzaRepository.save(aa);
//		
//	}
	
	
	private Studente fillStudenteWithRegistration(Object[] sr) {
		Studente s = (Studente) sr[0];
		Registration r = (Registration) sr[1];
		s.setClassroom(r.getClassroom());
		s.setAnnoCorso(Integer.valueOf(r.getClassroom().substring(0, 1)));
		s.setIstitutoId(r.getInstituteId());
        CorsoDiStudioBean corsoBean = new CorsoDiStudioBean(r.getCourseId(), r.getCourse());
        s.setCorsoDiStudio(corsoBean);
		return s;
	}







}
