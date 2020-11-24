package it.smartcommunitylab.cartella.asl.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta.Stati;
import it.smartcommunitylab.cartella.asl.model.PresenzaGiornaliera;
import it.smartcommunitylab.cartella.asl.model.Registration;
import it.smartcommunitylab.cartella.asl.model.Studente;
import it.smartcommunitylab.cartella.asl.model.TeachingUnit;
import it.smartcommunitylab.cartella.asl.model.report.ReportEsperienzaRegistration;
import it.smartcommunitylab.cartella.asl.repository.EsperienzaSvoltaRepository;
import it.smartcommunitylab.cartella.asl.storage.LocalDocumentManager;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Repository
@Transactional
public class EsperienzaSvoltaManager extends DataEntityManager {
	
	@Autowired
	EsperienzaSvoltaRepository esperienzaSvoltaRepository;
	@Autowired
	LocalDocumentManager documentManager;
	@Autowired
	PresenzaGiornalieraManager presenzaManager;
	@Autowired
	OffertaManager offertaManager;
	@Autowired
	RegistrationManager registrationManager;
	@Autowired
	TeachingUnitManager tuManager;
	@Autowired
	EsperienzaAllineamentoManager esperienzaAllineamentoManager;

	public EsperienzaSvolta getEsperienzaSvolta(Long id) {
		return esperienzaSvoltaRepository.findById(id).orElse(null);
	}
	
	public List<EsperienzaSvolta> getEsperienzeByAttivita(AttivitaAlternanza attivitaAlternanza, Sort sort) {
		return esperienzaSvoltaRepository.findByIstitutoAndAttivita(attivitaAlternanza.getIstitutoId(), 
				attivitaAlternanza.getId(), sort);
	}

	public List<EsperienzaSvolta> getEsperienzeByAttivitaIds(List<Long> ids) {
		return esperienzaSvoltaRepository.findByAttivitaAlternanzaIdIn(ids);
	}
	
	public List<EsperienzaSvolta> getEsperienzeByAttivitaAndStudente(Long attivitaAlternanzaId, String studenteId) {
		return esperienzaSvoltaRepository.findByAttivitaAlternanzaIdAndStudenteId(attivitaAlternanzaId, studenteId);		
	}
	
	public void changeDateRange(EsperienzaSvolta esperienza, LocalDate dataInizio, LocalDate dataFine) {
		List<PresenzaGiornaliera> presenze = presenzaManager.findByEsperienzaSvolta(esperienza.getId());
		presenze.forEach(presenza -> {
			if(presenza.getGiornata().isBefore(dataInizio) || presenza.getGiornata().isAfter(dataFine)) {
				presenzaManager.deletePresenza(presenza);
			}
		});
	}

	public int deleteEsperienzeByAttivita(AttivitaAlternanza aa) {
		List<EsperienzaSvolta> esperienze = esperienzaSvoltaRepository.findByIstitutoAndAttivita(aa.getIstitutoId(), 
				aa.getId(), Sort.by(Sort.Direction.DESC, "cfStudente"));
		esperienze.forEach(esperienza -> {
			deleteEsperienza(esperienza);
		});
		return esperienze.size();
	}

	public void deleteEsperienza(EsperienzaSvolta esperienza) {
		presenzaManager.deletePresenzeByEsperienza(esperienza.getId());
		documentManager.deleteDocumentsByRisorsaId(esperienza.getUuid());
		esperienzaSvoltaRepository.deleteById(esperienza.getId());
	}

	public void addEsperienza(EsperienzaSvolta esperienza) throws Exception {
		esperienza.setId(null);
		esperienza.setUuid(Utils.getUUID());
		esperienza.setStato(EsperienzaSvolta.Stati.da_definire);
		Registration registration = registrationManager.findById(esperienza.getRegistrazioneId());
		if(registration == null) {
			throw new BadRequestException("registration not found:" + esperienza.getRegistrazioneId());
		}
		TeachingUnit teachingUnit = tuManager.findById(registration.getTeachingUnitId());
		if(teachingUnit == null) {
			throw new BadRequestException("teachingUnit not found:" + registration.getTeachingUnitId());
		}
		esperienza.setCodiceMiur(teachingUnit.getCodiceMiur());
		esperienzaSvoltaRepository.save(esperienza);
	}
	
	public EsperienzaSvolta findById(Long id) {
		Optional<EsperienzaSvolta> optional = esperienzaSvoltaRepository.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}
	
	public EsperienzaSvolta findByUuid(String uuid) {
		return esperienzaSvoltaRepository.findByUuid(uuid);
	}
	
	private void updateStato(Long id, Stati stato) {
		esperienzaSvoltaRepository.updateStato(id, stato);
	}
	
	public void chiudiEsperienza(Long id, boolean valida) {
		EsperienzaSvolta es = findById(id);
		if(es != null) {
			Stati stato = valida ? Stati.valida : Stati.annullata;
			updateStato(id, stato);
			if(valida) {
				esperienzaAllineamentoManager.addEsperienzaSvoltaAllineamento(id);
			}
		}
	}
	
	public void apriEsperienza(Long id) {
		EsperienzaSvolta es = findById(id);
		if(es != null) {
			updateStato(id, Stati.da_definire);
			esperienzaAllineamentoManager.deleteEsperienzaSvoltaAllineamento(id);
		}
	}
	
	public Page<ReportEsperienzaRegistration> findEsperienze(String istitutoId, String annoScolastico, 
			String text, Pageable pageRequest) {
		StringBuilder sb = new StringBuilder("SELECT s0,r0 FROM Studente s0, Registration r0 "
				+ "WHERE s0.id = r0.studentId AND r0.instituteId = (:instituteId) AND r0.schoolYear = (:annoScolastico)");
		if(Utils.isNotEmpty(text)) {
			sb.append(" AND (UPPER(s0.name) LIKE (:text) OR UPPER(s0.surname) LIKE (:text) OR UPPER(r0.classroom) LIKE (:text))");
		}
		sb.append(" ORDER BY s0.surname, s0.name, r0.classroom");
		String q = sb.toString();
		
		Query query = em.createQuery(q);
		query.setParameter("instituteId", istitutoId);
		query.setParameter("annoScolastico", annoScolastico);
		if(Utils.isNotEmpty(text)) {
			query.setParameter("text", "%" + text.trim().toUpperCase() + "%");
		}
		
		query.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
		query.setMaxResults(pageRequest.getPageSize());
		List<Object[]> result = query.getResultList();
		
		List<ReportEsperienzaRegistration> espRegList = new ArrayList<ReportEsperienzaRegistration>();
		for (Object[] obj : result) {
			Studente s = (Studente) obj[0];
			Registration r = (Registration) obj[1];
			ReportEsperienzaRegistration espReg = new ReportEsperienzaRegistration();
			espReg.setCfStudente(s.getCf());
			espReg.setClasseStudente(r.getClassroom());
			espReg.setIstitutoId(r.getInstituteId());
			espReg.setNominativoStudente(s.getSurname() + " " + s.getName());
			espReg.setRegistrazioneId(r.getId());
			espReg.setStudenteId(s.getId());
			espRegList.add(espReg);
		}
		
		Query cQuery = queryToCountQuery(q, query);
		long total = (Long) cQuery.getSingleResult();
		
		Page<ReportEsperienzaRegistration> page = new PageImpl<ReportEsperienzaRegistration>(espRegList, pageRequest, total);
		return page;
	}

}
