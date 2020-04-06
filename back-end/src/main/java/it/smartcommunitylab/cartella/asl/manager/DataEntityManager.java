package it.smartcommunitylab.cartella.asl.manager;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import it.smartcommunitylab.cartella.asl.model.CorsoDiStudio;
import it.smartcommunitylab.cartella.asl.repository.AttivitaAlternanzaRepository;
import it.smartcommunitylab.cartella.asl.repository.CorsoDiStudioRepository;
import it.smartcommunitylab.cartella.asl.repository.IstituzioneRepository;

@Repository
@Transactional
public class DataEntityManager {
	
	@Autowired
	EntityManager em;
	
	@Autowired
	private CorsoDiStudioRepository corsoDiStudioRepository;
	@Autowired
	private IstituzioneRepository istituzioneRepository;
	@Autowired
	private AttivitaAlternanzaRepository attivitaAlternanzaRepository;

  public static final String STUDENTS_AND_REGISTRATIONS_QUERY =
            "SELECT DISTINCT s0,r0 FROM Studente s0, Registration r0 WHERE s0.id = r0.studentId AND r0.instituteId = (:instituteId) AND r0.dateTo = (SELECT max(rm.dateTo) FROM Registration rm WHERE rm.studentId = s0.id AND rm.schoolYear = (:annoScolastico) AND rm.instituteId = (:instituteId))";
  
  public static final String ASLUSERS = "SELECT DISTINCT u FROM ASLUser u  ";
  
	TypedQuery<Long> queryToCountQuery(String q, Query query1) {
		String q2 = q.replaceAll("^SELECT.*?FROM", "SELECT COUNT(*) FROM");
		//q2 = q2.replaceAll("SELECT DISTINCT ([^ ]*) FROM", "SELECT COUNT(DISTINCT $1) FROM");

		TypedQuery<Long> query2 = em.createQuery(q2, Long.class);
		for (@SuppressWarnings("rawtypes") Parameter param : query1.getParameters()) {
			query2.setParameter(param.getName(), query1.getParameterValue(param.getName()));
		}

		return query2;
	}
	
	TypedQuery<Long> queryToCount(String q, Query query1) {
		TypedQuery<Long> query2 = em.createQuery(q, Long.class);
		for (@SuppressWarnings("rawtypes") Parameter param : query1.getParameters()) {
			query2.setParameter(param.getName(), query1.getParameterValue(param.getName()));
		}
		return query2;
	}
	
	public List<CorsoDiStudio> findCorsiDiStudio(String istitutoId, String annoScolastico) {
		if (annoScolastico == null || annoScolastico.isEmpty()) {
			return corsoDiStudioRepository.findCorsoDiStudioByIstitutoId(istitutoId);
		} else {
			return corsoDiStudioRepository.findCorsoDiStudioByIstitutoIdAndAnnoScolastico(istitutoId, annoScolastico);
		}
	}
	
	public String getIstituzioneName(String id) {
		return istituzioneRepository.findIstitutoName(id);
	}
	
	public int countAttivitaAlternanzaByOfferta(Long offertaId) {
		return attivitaAlternanzaRepository.countByOffertaId(offertaId);
	}

}
