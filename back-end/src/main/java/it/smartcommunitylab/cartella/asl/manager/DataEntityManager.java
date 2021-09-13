package it.smartcommunitylab.cartella.asl.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import it.smartcommunitylab.cartella.asl.model.CorsoDiStudio;
import it.smartcommunitylab.cartella.asl.model.CorsoMetaInfo;
import it.smartcommunitylab.cartella.asl.model.PresenzaGiornaliera;
import it.smartcommunitylab.cartella.asl.repository.AttivitaAlternanzaRepository;
import it.smartcommunitylab.cartella.asl.repository.CorsoDiStudioRepository;
import it.smartcommunitylab.cartella.asl.repository.CorsoMetaInfoRepository;
import it.smartcommunitylab.cartella.asl.repository.IstituzioneRepository;

@Repository
@Transactional
public class DataEntityManager {
	
	@Autowired
	EntityManager em;
	
	@Autowired
	private CorsoDiStudioRepository corsoDiStudioRepository;
	@Autowired
	CorsoMetaInfoRepository corsoMetaInfoRepository;	
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
	
	TypedQuery<Long> queryToCount(String q, Map<String, Object> parameters) {
		TypedQuery<Long> query = em.createQuery(q, Long.class);
		for(String key : parameters.keySet()) {
			query.setParameter(key, parameters.get(key));
		}
		return query;
	}
	
	public List<CorsoDiStudio> findCorsiDiStudio(String istitutoId, String annoScolastico) {
		List<CorsoDiStudio> result = null;
		if (annoScolastico == null || annoScolastico.isEmpty()) {
			result = corsoDiStudioRepository.findCorsoDiStudioByIstitutoId(istitutoId);
		} else {
			result = corsoDiStudioRepository.findCorsoDiStudioByIstitutoIdAndAnnoScolastico(istitutoId, annoScolastico);
		}
		result.forEach(corso -> {
			corso.setCorsoSperimentale(Boolean.FALSE);
	  	CorsoMetaInfo corsoMetaInfo = corsoMetaInfoRepository.findById(corso.getCourseId()).orElse(null);
	  	if(corsoMetaInfo != null) {
	  		if((corsoMetaInfo.getYears() != null) && (corsoMetaInfo.getYears() < 5)) {
	  			corso.setCorsoSperimentale(Boolean.TRUE);
	  		}
	  	}			
		});
		return result;
	}
	
	public String getIstituzioneName(String id) {
		return istituzioneRepository.findIstitutoName(id);
	}
	
	public int countAttivitaAlternanzaByOfferta(Long offertaId) {
		return attivitaAlternanzaRepository.countByOffertaId(offertaId);
	}
	
	public List<PresenzaGiornaliera> removeDuplicatedDays(List<PresenzaGiornaliera> list) {
		Map<Long, Map<LocalDate, PresenzaGiornaliera>> mapEsperienze = new HashMap<>();
		for(PresenzaGiornaliera pg : list) {
			Map<LocalDate, PresenzaGiornaliera> mapPresenze = mapEsperienze.get(pg.getEsperienzaSvoltaId()); 
			if(mapPresenze == null) {
				mapPresenze = new HashMap<>();
				mapEsperienze.put(pg.getEsperienzaSvoltaId(), mapPresenze);
			}
			if(mapPresenze.containsKey(pg.getGiornata())) {
				PresenzaGiornaliera pgMap = mapPresenze.get(pg.getGiornata());
				if((pgMap != null) && (pgMap.getId() > pg.getId())) {
					mapPresenze.put(pg.getGiornata(), pg);
				}
			} else {
				mapPresenze.put(pg.getGiornata(), pg);
			}
		}
		List<PresenzaGiornaliera> result = new ArrayList<>();
		for(PresenzaGiornaliera pg : list) {
			Map<LocalDate, PresenzaGiornaliera> mapPresenze = mapEsperienze.get(pg.getEsperienzaSvoltaId());
			PresenzaGiornaliera pgMap = mapPresenze.get(pg.getGiornata());
			if(pgMap != null) {
				result.add(pgMap);
				mapPresenze.remove(pg.getGiornata());
			}
		}
		return result;
	}
}
