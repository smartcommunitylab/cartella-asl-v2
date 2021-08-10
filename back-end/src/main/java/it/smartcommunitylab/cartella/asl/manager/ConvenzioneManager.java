package it.smartcommunitylab.cartella.asl.manager;

import java.time.LocalDate;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;

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
	
	public void checkAttivitaByEnte(AttivitaAlternanza aa, String enteId) throws Exception {
		if(aa == null) {
			throw new BadRequestException("attività non trovata");
		}
		if(!enteId.equals(aa.getEnteId())) {
			throw new BadRequestException("attività non visibile");
		}
		if((aa.getTipologia() != 7) && (aa.getTipologia() != 10)) {
			throw new BadRequestException("tipologia non corretta");
		}
		LocalDate today = LocalDate.now();
		LocalDate unAnnoFa = today.minusYears(1);
		LocalDate dataConvenzioneAttiva = getDataConvenzioneAttiva(aa.getIstitutoId(), enteId);
		if(today.isAfter(dataConvenzioneAttiva)) {
			throw new BadRequestException("convenzione scaduta");
		}
		if(unAnnoFa.isAfter(aa.getDataFine())) {
			throw new BadRequestException("attività scaduta");
		}
	}
}
