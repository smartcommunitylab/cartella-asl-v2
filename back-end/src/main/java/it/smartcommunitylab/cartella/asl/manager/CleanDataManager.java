package it.smartcommunitylab.cartella.asl.manager;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import it.smartcommunitylab.cartella.asl.model.IstitutoAttivo;
import it.smartcommunitylab.cartella.asl.repository.IstitutoAttivoRepository;


@Repository
@Transactional
public class CleanDataManager extends DataEntityManager {
	private static Log logger = LogFactory.getLog(CleanDataManager.class);
	
	@Autowired
	IstitutoAttivoRepository istitutoAttivoRepository;
	
	private List<String> getActiveInstituteIds() {
		List<IstitutoAttivo> activeList = istitutoAttivoRepository.findAll();
		List<String> activeIds = new ArrayList<>();
		activeList.forEach(e -> {
			activeIds.add(e.getIstitutoId());
		});
		return activeIds;
	}
	
	public void cancellaIstitutoNonAttivi() {
		List<String> activeIds = getActiveInstituteIds();
		if(activeIds.size() > 3) {
			Query query = em.createQuery("DELETE FROM Istituzione i WHERE i.id NOT IN (:activeIds)");
			query.setParameter("activeIds", activeIds);
			int deletedCount = query.executeUpdate();
			logger.info("removed "+ deletedCount + " entities");  
		}
	}
	
	public void cancellaTeachingUnitNonAttivi() {
		List<String> activeIds = getActiveInstituteIds();
		if(activeIds.size() > 3) {
			Query query = em.createQuery("DELETE FROM TeachingUnit t WHERE t.instituteId NOT IN (:activeIds)");
			query.setParameter("activeIds", activeIds);
			int deletedCount = query.executeUpdate();
			logger.info("removed "+ deletedCount + " entities");  
		}
	}

	public void cancellaCorsoDiStudioNonAttivi() {
		List<String> activeIds = getActiveInstituteIds();
		if(activeIds.size() > 3) {
			Query query = em.createQuery("DELETE FROM CorsoDiStudio c WHERE c.istitutoId NOT IN (:activeIds)");
			query.setParameter("activeIds", activeIds);
			int deletedCount = query.executeUpdate();
			logger.info("removed "+ deletedCount + " entities");  
		}
	}
	
	public void cancellaRegistrazioneNonAttivi() {
		List<String> activeIds = getActiveInstituteIds();
		if(activeIds.size() > 3) {
			Query query = em.createQuery("DELETE FROM Registration r WHERE r.instituteId NOT IN (:activeIds)");
			query.setParameter("activeIds", activeIds);
			int deletedCount = query.executeUpdate();
			logger.info("removed "+ deletedCount + " entities");  
		}
	}
	
	public void cancellaStudenteNonAttivo() {
		List<String> activeIds = getActiveInstituteIds();
		if(activeIds.size() > 3) {
			List<String> studentIds = em.createQuery("SELECT DISTINCT(s.id) FROM Studente s", String.class).getResultList();
			List<String> activeStudentIds = em.createQuery("SELECT DISTINCT(s.id) FROM Studente s, Registration r WHERE r.studentId=s.id", String.class)
					.getResultList();
			int count = 1;
			List<String> studentToDelete = new ArrayList<>();
			for(String studentId : studentIds) {
				if(!activeStudentIds.contains(studentId)) {
					studentToDelete.add(studentId);
					count++;
					if(count > 5000) {
						Query query = em.createQuery("DELETE FROM Studente s WHERE s.id IN (:studentToDelete)");
						query.setParameter("studentToDelete", studentToDelete);
						int deletedCount = query.executeUpdate();
						logger.info("removed " + deletedCount + " students");
						studentToDelete.clear();
						count = 1;
					}					
				}
			}
			if(studentToDelete.size() > 0) {
				Query query = em.createQuery("DELETE FROM Studente s WHERE s.id IN (:studentToDelete)");
				query.setParameter("studentToDelete", studentToDelete);
				int deletedCount = query.executeUpdate();
				logger.info("removed " + deletedCount + " students");
			}			
		}
	}
	
	/*private long getStudentRegistrationNum(String studentId) {
		TypedQuery<Long> query = em.createQuery("SELECT COUNT(r) FROM Registration r WHERE r.studentId=(:studentId)", Long.class);
		query.setParameter("studentId", studentId);
		return query.getSingleResult();
	}*/


}
