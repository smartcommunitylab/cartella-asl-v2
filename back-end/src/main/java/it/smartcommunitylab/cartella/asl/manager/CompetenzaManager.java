package it.smartcommunitylab.cartella.asl.manager;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import it.smartcommunitylab.cartella.asl.model.AssociazioneCompetenze;
import it.smartcommunitylab.cartella.asl.model.Competenza;
import it.smartcommunitylab.cartella.asl.model.Istituzione;
import it.smartcommunitylab.cartella.asl.model.report.ReportCompetenzaDettaglio;
import it.smartcommunitylab.cartella.asl.repository.AssociazioneCompetenzeRepository;
import it.smartcommunitylab.cartella.asl.repository.AttivitaAlternanzaRepository;
import it.smartcommunitylab.cartella.asl.repository.CompetenzaRepository;
import it.smartcommunitylab.cartella.asl.repository.IstituzioneRepository;
import it.smartcommunitylab.cartella.asl.repository.OffertaRepository;
import it.smartcommunitylab.cartella.asl.repository.PianoAlternanzaRepository;
import it.smartcommunitylab.cartella.asl.repository.StudenteRepository;
import it.smartcommunitylab.cartella.asl.util.Constants;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Repository
@Transactional
public class CompetenzaManager extends DataEntityManager {

	@Autowired
	CompetenzaRepository competenzaRepository;
	@Autowired
	AssociazioneCompetenzeRepository associazioneCompetenzeRepository;
	@Autowired
	PianoAlternanzaRepository pianoAlternanzaRepository;
	@Autowired
	StudenteRepository studenteRepository;
	@Autowired
	AttivitaAlternanzaRepository attivitaAlternanzaRepository;
	@Autowired
	OffertaRepository offertaRepository;
	@Autowired
	IstituzioneRepository istituzioneRepository;
	
	private static final String COMPETENZE_ORDER_BY_ISTITUTO = "SELECT c FROM Competenza c WHERE c.ownerId IN (:ownerIds)";

	public Competenza saveCompetenza(Competenza c) {
		Istituzione ist = istituzioneRepository.findById(c.getOwnerId()).get();
		c.setOwnerName(ist.getName());
		c.setUri(Constants.COMPETENZA_URI_INITIAL + "://" + c.getSource() + "/" + c.getClassificationCode());		
		return competenzaRepository.save(c);
	}

	public Competenza getCompetenza(long id) {
		return competenzaRepository.findById(id).orElse(null);
	}
	
	public ReportCompetenzaDettaglio getCompetenzaReport(long id) {
		ReportCompetenzaDettaglio reportCompetenzaDettaglio = new ReportCompetenzaDettaglio();
		reportCompetenzaDettaglio.setCompetenza(competenzaRepository.findById(id).orElse(null));
		List<String> listIds = new ArrayList<>();
		
		for (AssociazioneCompetenze ac: associazioneCompetenzeRepository.findByCompetenzaId(id)) {
			listIds.add(ac.getRisorsaId());
		}
		
		String[] ids = listIds.toArray(new String[0]);
		
		if (ids.length > 0) {
			reportCompetenzaDettaglio.setNumeroPianoAltAssociate(pianoAlternanzaRepository.getCountOfPianoAlternanza(ids));
			reportCompetenzaDettaglio.setNumeroStudentiAssociate(studenteRepository.getCountOfStudente(ids));
			reportCompetenzaDettaglio.setNumeroAttivitaAltAssociate(attivitaAlternanzaRepository.getCountOfAttivitaAlternanza(ids));
			reportCompetenzaDettaglio.setNumeroOfferteAssociate(offertaRepository.getCountOfOfferta(ids));
		}		
		
		return reportCompetenzaDettaglio;
	}

	public List<Competenza> getCompetenze() {
		return competenzaRepository.findAll();
	}

	public Page<Competenza> getCompetenzeOrderByIstituto(List<String> ownerIds, String filterText, String stato, Pageable pageRequest) {

		StringBuilder sb = new StringBuilder(COMPETENZE_ORDER_BY_ISTITUTO);

		if (Utils.isNotEmpty(filterText)) {
			sb.append(" AND (lower(c.titolo) LIKE (:filterText))");
		}

		if (Utils.isNotEmpty(stato)) {
			sb.append(" AND c.attiva = (:stato) ");
		}

		String q = sb.toString() + " ORDER BY c.ownerName DESC";
		
		TypedQuery<Competenza> query = em.createQuery(q, Competenza.class);
		query.setParameter("ownerIds", ownerIds);

		if (Utils.isNotEmpty(stato)) {
			query.setParameter("stato", Boolean.valueOf(stato));
		}

		if (Utils.isNotEmpty(filterText)) {
			query.setParameter("filterText", "%" + filterText.toLowerCase() + "%");
		}

		String q2 = q.replaceAll("SELECT [^ ]* FROM", "SELECT COUNT(*) FROM");
		q2 = q2.replaceAll("SELECT DISTINCT ([^ ]*) FROM", "SELECT COUNT(DISTINCT $1) FROM");
		TypedQuery<Long> countQ = em.createQuery(q2, Long.class);
		countQ.setParameter("ownerIds", ownerIds);

		if (Utils.isNotEmpty(stato)) {
			countQ.setParameter("stato", Boolean.valueOf(stato));
		}

		if (Utils.isNotEmpty(filterText)) {
			countQ.setParameter("filterText", "%" + filterText.toLowerCase() + "%");
		}
		
		long t = (Long) countQ.getSingleResult();
		query.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
		query.setMaxResults(pageRequest.getPageSize());
		List<Competenza> result = query.getResultList();
		Page<Competenza> page = new PageImpl<Competenza>(result, pageRequest, t);
		return page;

	}

	public List<Competenza> getCompetenze(List<Long> ids) {
		return competenzaRepository.findAllById(ids);
	}

	public void updateCompetenza(Competenza c) {
		Competenza old = competenzaRepository.getOne(c.getId());
		if (old == null) {
			competenzaRepository.save(c);
		} else {
			old.setAbilita(c.getAbilita());
			old.setConoscenze(c.getConoscenze());
			old.setClassificationCode(c.getClassificationCode());
			old.setLivelloEQF(c.getLivelloEQF());
			old.setTitolo(c.getTitolo());
			competenzaRepository.save(old);
		}
	}

	public void deleteCompetenza(long id) throws Exception {
		Competenza competenza = competenzaRepository.findById(id).orElse(null);
		ReportCompetenzaDettaglio report = getCompetenzaReport(id);
		if (report.getNumeroAttivitaAltAssociate() > 0 || report.getNumeroOfferteAssociate() > 0
				|| report.getNumeroPianoAltAssociate() > 0 || report.getNumeroStudentiAssociate() > 0) {
			competenza.setAttiva(Boolean.FALSE);
			competenzaRepository.save(competenza);				
		} else {
			competenzaRepository.deleteById(id);
		}
	}
	
	public void deleteAssociatedCompetenzeByRisorsaId(String risorsaId) throws Exception {
		for (AssociazioneCompetenze ac : associazioneCompetenzeRepository.findByRisorsaId(risorsaId)) {
			deleteCompetenza(ac.getCompetenzaId());
		}
	}

	public String findCompetenzaOwnerId(long id) {
		return competenzaRepository.findCompetenzaOwnerId(id);
	}

	public Page<Competenza> findCompetenze(String filterText, String ownerId, Pageable pageRequest) throws Exception {
		Page<Competenza> page = null;
		List<Competenza> result = null;
		long total = 0;

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Competenza> cq1 = cb.createQuery(Competenza.class);
		Root<Competenza> competenza = cq1.from(Competenza.class);

		CriteriaQuery<Long> cq2 = cb.createQuery(Long.class);
		cq2.select(cb.count(cq2.from(Competenza.class)));

		List<Predicate> predicates = Lists.newArrayList();
		if (filterText != null && !filterText.isEmpty()) {
			String filter = "%" + filterText.trim() + "%";
			Predicate predicate1 = cb.like(competenza.get("titolo").as(String.class), filter);
			Predicate predicate2 = cb.like(competenza.get("conoscenze").as(String.class), filter);
			Predicate predicate3 = cb.like(competenza.get("abilita").as(String.class), filter);

			Predicate predicateF = cb.or(predicate1, predicate2, predicate3);
			predicates.add(predicateF);
		}
		if (ownerId != null) {
			Predicate predicateO = cb.equal(competenza.get("ownerId").as(String.class), ownerId);
			predicates.add(predicateO);
		}

		cq1.where(predicates.toArray(new Predicate[predicates.size()]));
		cq2.where(predicates.toArray(new Predicate[predicates.size()]));

		TypedQuery<Competenza> query1 = em.createQuery(cq1);
		TypedQuery<Long> query2 = em.createQuery(cq2);

		query1.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
		query1.setMaxResults(pageRequest.getPageSize());

		result = query1.getResultList();
		total = query2.getSingleResult();

		page = new PageImpl<Competenza>(result, pageRequest, total);

		return page;

	}

	public List<Competenza> getRisorsaCompetenze(String uuid) {
		String q = "SELECT DISTINCT c FROM Competenza c, AssociazioneCompetenze ac WHERE ac.competenzaId=c.id AND ac.risorsaId=(:uuid) ORDER BY ac.ordine ASC";
		TypedQuery<Competenza> query = em.createQuery(q, Competenza.class);
		query.setParameter("uuid", uuid);
		return query.getResultList();
	}

	public Boolean addCompetenzeToPianoAlternanza(String uuid, List<Long> ids) {
		boolean saved = false;

		// remove non existing associazione competenze.
		List<AssociazioneCompetenze> existingAC = associazioneCompetenzeRepository.findByRisorsaId(uuid);
		for (AssociazioneCompetenze ac : existingAC) {
			if (!ids.contains(ac.getCompetenzaId())) {
				associazioneCompetenzeRepository.delete(ac);
			}
		}

		List<AssociazioneCompetenze> updatedAC = associazioneCompetenzeRepository.findByRisorsaId(uuid);
		ids.forEach(id -> {
			boolean found = false;
			for (AssociazioneCompetenze ac : updatedAC) {
				if (ac.getCompetenzaId() == id) {
					found = true;
					break;
				}
			}
			if (!found) {
				AssociazioneCompetenze ac = new AssociazioneCompetenze();
				ac.setCompetenzaId(id);
				ac.setRisorsaId(uuid);
				ac.setOrdine(0);
				associazioneCompetenzeRepository.save(ac);
			}
		});

		saved = true;
		return saved;
	}
	
	public List<Competenza> getCompetenzeByStudente(String istitutoId, String studenteId) {
		String qEsperienze = "SELECT DISTINCT(c) FROM AssociazioneCompetenze ac, Competenza c"
				+ " WHERE ac.competenzaId=c.id"
				+ " AND ac.risorsaId IN ("
				+ " SELECT aa.uuid FROM AttivitaAlternanza aa, EsperienzaSvolta es"
				+ " WHERE es.attivitaAlternanzaId=aa.id"
				+ " AND aa.istitutoId=(:istitutoId) AND es.studenteId=(:studenteId)"
				+ " ) ORDER BY c.titolo ASC";
		TypedQuery<Competenza> query = em.createQuery(qEsperienze, Competenza.class);
		query.setParameter("istitutoId", istitutoId);
		query.setParameter("studenteId", studenteId);
		List<Competenza> competenzeList = query.getResultList();
		return competenzeList;
	}

}
