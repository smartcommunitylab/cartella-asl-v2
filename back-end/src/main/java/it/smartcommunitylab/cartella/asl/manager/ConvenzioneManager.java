package it.smartcommunitylab.cartella.asl.manager;

import java.time.LocalDate;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ConvenzioneManager extends DataEntityManager {
	public LocalDate getDataConvenzioneAttiva(String istitutoId, String enteId) {
		String q = "SELECT MAX(c.dataFine) FROM Convenzione c WHERE c.enteId=(:enteId) AND c.istitutoId=(:istitutoId)";
		TypedQuery<LocalDate> query = em.createQuery(q, LocalDate.class);
		query.setParameter("enteId", enteId);
		query.setParameter("istitutoId", istitutoId);
		LocalDate date = query.getSingleResult();
		return date;
	}
}
