package it.smartcommunitylab.cartella.asl.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza.Stati;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;
import it.smartcommunitylab.cartella.asl.model.Istituzione;
import it.smartcommunitylab.cartella.asl.model.Offerta;
import it.smartcommunitylab.cartella.asl.model.PresenzaGiornaliera;
import it.smartcommunitylab.cartella.asl.model.report.ReportArchiviaEsperienza;
import it.smartcommunitylab.cartella.asl.model.report.ReportAttivitaAlternanzaDettaglio;
import it.smartcommunitylab.cartella.asl.model.report.ReportAttivitaAlternanzaRicerca;
import it.smartcommunitylab.cartella.asl.model.report.ReportAttivitaAlternanzaRicercaEnte;
import it.smartcommunitylab.cartella.asl.model.report.ReportAttivitaAlternanzaStudenti;
import it.smartcommunitylab.cartella.asl.model.report.ReportAttivitaAlternanzaStudentiEnte;
import it.smartcommunitylab.cartella.asl.model.report.ReportEsperienzaRegistration;
import it.smartcommunitylab.cartella.asl.model.report.ReportEsperienzaStudente;
import it.smartcommunitylab.cartella.asl.model.report.ReportPresenzaGiornalieraGruppo;
import it.smartcommunitylab.cartella.asl.model.report.ReportPresenzeAttvitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.report.ValutazioneAttivitaReport;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;
import it.smartcommunitylab.cartella.asl.repository.AttivitaAlternanzaRepository;
import it.smartcommunitylab.cartella.asl.storage.LocalDocumentManager;
import it.smartcommunitylab.cartella.asl.util.ErrorLabelManager;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Repository
@Transactional
public class AttivitaAlternanzaManager extends DataEntityManager {
	
	@Autowired
	AttivitaAlternanzaRepository attivitaAlternanzaRepository;
	@Autowired
	EsperienzaSvoltaManager esperienzaSvoltaManager;
	@Autowired
	OffertaManager offertaManager;
	@Autowired
	LocalDocumentManager documentManager;
	@Autowired
	PresenzaGiornalieraManager presenzaGiornalieraManager;
	@Autowired
	CompetenzaManager competenzaManager;
	@Autowired
	IstituzioneManager istituzioneManager;
	@Autowired
	RegistrazioneDocenteManager registrazioneDocenteManager;
	@Autowired
	ConvenzioneManager convenzioneManager;
	@Autowired
	ValutazioniManager valutazioniManager;
	@Autowired
	ErrorLabelManager errorLabelManager;
	@Autowired
	ASLRolesValidator usersValidator;
	
	public AttivitaAlternanza saveAttivitaAlternanza(AttivitaAlternanza aa, String istitutoId) 
			throws BadRequestException {
		aa.setIstitutoId(istitutoId);
		aa.setStato(Stati.attiva);
		AttivitaAlternanza aaDb = getAttivitaAlternanza(aa.getId());
		if(aaDb == null) {
			aa.setUuid(Utils.getUUID());
			return attivitaAlternanzaRepository.save(aa);
		} else {
			if(aaDb.getStato().equals(Stati.archiviata)) {
				throw new BadRequestException(errorLabelManager.get("attivita.noteditable"));
			}
			List<EsperienzaSvolta> esperienze = esperienzaSvoltaManager.getEsperienzeByAttivita(aaDb, 
					Sort.by(Sort.Direction.ASC, "nominativoStudente"));
			if(esperienze.size() > 0) {
				if(!aaDb.getAnnoScolastico().equalsIgnoreCase(aa.getAnnoScolastico())) {
					throw new BadRequestException(errorLabelManager.get("attivita.error.schoolYear"));
				}				
			}
			if(!aaDb.getDataInizio().isEqual(aa.getDataInizio()) || 
					!aaDb.getDataFine().isEqual(aa.getDataFine())) {
				for(EsperienzaSvolta esperienza : esperienze) {
					if(aa.getRendicontazioneCorpo()) {
						setPresenzeCorpo(aa, esperienza);
					} else {
						esperienzaSvoltaManager.changeDateRange(esperienza, aa.getDataInizio(), aa.getDataFine());
					}
				}
			}
			attivitaAlternanzaRepository.updateAttivitaAlternanza(aa);
			return aa;
		}
	}
	
	public AttivitaAlternanza getAttivitaAlternanza(Long id) {
		if(id != null) {
			Optional<AttivitaAlternanza> optional = attivitaAlternanzaRepository.findById(id);
			if(optional.isPresent()) {
				return optional.get();
			}
		}
		return null;
	}
	
	public AttivitaAlternanza getAttivitaAlternanzaStub(Long id) {
		AttivitaAlternanza aa = getAttivitaAlternanza(id);
		if(aa != null) {
			AttivitaAlternanza attivita = aa.clona();
			attivita.setStato(getStato(aa));
			return attivita;
		}
		return null;
	}
	
	public void deleteAttivitaAlternanza(AttivitaAlternanza aa) throws Exception {
		if(aa.getStato().equals(Stati.archiviata)) {
			throw new BadRequestException(errorLabelManager.get("attivita.noteditable"));
		}
		int numEsperienze = esperienzaSvoltaManager.deleteEsperienzeByAttivita(aa);
		if(aa.getOffertaId() != null) {
			offertaManager.rimuoviPostiEsperienze(aa.getOffertaId(), numEsperienze);
		}
		documentManager.deleteDocumentsByRisorsaId(aa.getUuid());
		competenzaManager.deleteAssociatedCompetenzeByRisorsaId(aa.getUuid());
		attivitaAlternanzaRepository.deleteById(aa.getId());
	}
	
	public void deleteAttivitaAlternanzaById(Long id) throws Exception {
		attivitaAlternanzaRepository.deleteById(id);
	}
	
	public Page<ReportAttivitaAlternanzaRicerca> findAttivita(String istitutoId, String text, String annoScolastico, int tipologia, String stato,
			Pageable pageRequest, ASLUser user) {
		Map<String, Object> parameters = new HashMap<>();
		boolean tutorScolatico = usersValidator.hasRole(user, ASLRole.TUTOR_SCOLASTICO, istitutoId);
		boolean tutorClasse = usersValidator.hasRole(user, ASLRole.TUTOR_CLASSE, istitutoId);
		List<String> classiAssociate = registrazioneDocenteManager.getClassiAssociateRegistrazioneDocente(istitutoId, user.getCf());
		StringBuilder sb = new StringBuilder("SELECT DISTINCT aa FROM AttivitaAlternanza aa LEFT JOIN EsperienzaSvolta es");
		sb.append(" ON es.attivitaAlternanzaId=aa.id");
		if(tutorClasse) {
			sb.append(" WHERE aa.istitutoId=(:istitutoId)");
			sb.append(" AND (aa.referenteScuolaCF=(:referenteCf) OR es.classeStudente IN (:classiAssociate))");
		} else {
			if(tutorScolatico) {
				sb.append(" WHERE aa.istitutoId=(:istitutoId) AND aa.referenteScuolaCF=(:referenteCf)");
				sb.append(" AND aa.stato!='" + Stati.archiviata.toString() + "'");
			} else {
				sb.append(" WHERE aa.istitutoId=(:istitutoId)");
			}
		}
		
		if(Utils.isNotEmpty(text)) {
			sb.append(" AND (UPPER(aa.titolo) LIKE (:text) OR UPPER(es.nominativoStudente) LIKE (:text) OR UPPER(es.cfStudente) = (:textCf) OR UPPER(es.classeStudente) LIKE (:text))");
		}
		
		if(Utils.isNotEmpty(annoScolastico)) {
			sb.append(" AND aa.annoScolastico=(:annoScolastico)");
		}
		
		if(tipologia > 0) {
			sb.append(" AND aa.tipologia=(:tipologia)");
		}
		
		boolean setDataParam = false;
		if(Utils.isNotEmpty(stato)) {
			Stati statoEnum = Stati.valueOf(stato);
			if(statoEnum == Stati.archiviata) {
				if(tutorClasse || (!tutorScolatico && !tutorClasse)) {
					sb.append(" AND aa.stato='" + Stati.archiviata.toString() + "'");
				}
			} else {
				sb.append(" AND aa.stato='" + Stati.attiva.toString() + "'");
				setDataParam = true;
				if(statoEnum == Stati.in_attesa) {
					sb.append(" AND aa.dataInizio > (:data)");
				}
				if(statoEnum == Stati.revisione) {
					sb.append(" AND aa.dataFine < (:data)");
					setDataParam = true;
				}
				if(statoEnum == Stati.in_corso) {
					sb.append(" AND aa.dataInizio <= (:data) AND aa.dataFine >= (:data)");
					setDataParam = true;
				}
			}
		}
				
		sb.append(" ORDER BY aa.dataInizio DESC, aa.titolo ASC");
		String q = sb.toString();

		TypedQuery<AttivitaAlternanza> query = em.createQuery(q, AttivitaAlternanza.class);
		
		query.setParameter("istitutoId", istitutoId);
		parameters.put("istitutoId", istitutoId);
		if(Utils.isNotEmpty(text)) {
			String like = "%" + text.trim().toUpperCase() + "%";
			query.setParameter("text", like);			
			parameters.put("text", like);
			String cf = text.trim().toUpperCase();
			query.setParameter("textCf", cf);
			parameters.put("textCf", cf);
		}
		if(Utils.isNotEmpty(annoScolastico)) {
			query.setParameter("annoScolastico", annoScolastico);
			parameters.put("annoScolastico", annoScolastico);			
		}
		if(tipologia > 0) {
			query.setParameter("tipologia", tipologia);
			parameters.put("tipologia", tipologia);
		}
		if(setDataParam) {
			LocalDate localDate = LocalDate.now(); 
			query.setParameter("data", localDate);
			parameters.put("data", localDate);
		}
		if(tutorScolatico) {
			query.setParameter("referenteCf", user.getCf());
			parameters.put("referenteCf", user.getCf());
		}
		if(tutorClasse) {
			query.setParameter("classiAssociate", classiAssociate);
			parameters.put("classiAssociate", classiAssociate);
		}
		
		query.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
		query.setMaxResults(pageRequest.getPageSize());
		List<AttivitaAlternanza> aaList = query.getResultList();
		
		List<ReportAttivitaAlternanzaRicerca> reportList = new ArrayList<>();
		for (AttivitaAlternanza aa : aaList) {
			List<EsperienzaSvolta> esperienze = esperienzaSvoltaManager.getEsperienzeByAttivita(aa, Sort.by(Sort.Direction.ASC, "id"));
			checkAccessoAttivita(aa, esperienze, user);
			ReportAttivitaAlternanzaRicerca report = new ReportAttivitaAlternanzaRicerca(aa); 
			report.setStato(getStato(aa).toString());
			reportList.add(report);
			for(EsperienzaSvolta es : esperienze) {
				report.getStudenti().add(es.getNominativoStudente());
				report.getClassi().add(es.getClasseStudente());
			}
		}
		
		Query cQuery = queryToCount(q.replaceAll("DISTINCT aa","COUNT(DISTINCT aa)"), parameters);
		long total = (Long) cQuery.getSingleResult();
		
		Page<ReportAttivitaAlternanzaRicerca> page = new PageImpl<ReportAttivitaAlternanzaRicerca>(reportList, pageRequest, total);
		return page;
	}
	
	public Stati getStato(AttivitaAlternanza attivita) {
		if(attivita.getStato() == Stati.archiviata) {
			return Stati.archiviata;
		}
		LocalDate localDate = LocalDate.now();
		if(localDate.isBefore(attivita.getDataInizio())) {
			return Stati.in_attesa;
		}
		if(localDate.isEqual(attivita.getDataInizio()) || localDate.isEqual(attivita.getDataFine()) ||
				(localDate.isAfter(attivita.getDataInizio()) && localDate.isBefore(attivita.getDataFine()))) {
			return Stati.in_corso;
		}
		if(localDate.isAfter(attivita.getDataFine())) {
			return Stati.revisione;
		}
		return attivita.getStato();
	}

	public ReportAttivitaAlternanzaStudenti getStudentInfo(AttivitaAlternanza attivitaAlternanza) {
		ReportAttivitaAlternanzaStudenti report = new ReportAttivitaAlternanzaStudenti(attivitaAlternanza);
		List<EsperienzaSvolta> esperienze = esperienzaSvoltaManager.getEsperienzeByAttivita(attivitaAlternanza, 
				Sort.by(Sort.Direction.ASC, "nominativoStudente"));
		for(EsperienzaSvolta esperienza : esperienze) {
			report.getStudenti().add(esperienza.getNominativoStudente());
			List<PresenzaGiornaliera> presenze = presenzaGiornalieraManager.findByEsperienzaSvolta(esperienza.getId());
			for(PresenzaGiornaliera presenza :  presenze) {
				if(!presenza.getVerificata()) {
					report.addOreDaValidare(presenza.getOreSvolte());
				}
			}
			if(report.getNumeroOreDaValidare() > 0) {
				report.addStudenteDaValidare();
			}
			if(EsperienzaSvolta.Stati.valida == esperienza.getStato()) {
				report.addEsperienzaCompletata();
			}
		}
		return report;
	}

	public ReportAttivitaAlternanzaDettaglio getAttivitaAlternanzaDetails(AttivitaAlternanza aa, ASLUser user) throws BadRequestException {
		AttivitaAlternanza attivita = aa.clona();
		attivita.setStato(getStato(aa));
		ReportAttivitaAlternanzaDettaglio report = new ReportAttivitaAlternanzaDettaglio();
		report.setAttivitaAlternanza(attivita);
		Istituzione istituto = istituzioneManager.getIstituto(aa.getIstitutoId(), null);
		if(istituto != null) {
			report.setNomeIstituto(istituto.getName());
		}
		List<EsperienzaSvolta> esperienze = esperienzaSvoltaManager.getEsperienzeByAttivita(aa, 
				Sort.by(Sort.Direction.ASC, "nominativoStudente"));
		if(!checkAccessoAttivita(attivita, esperienze, user)) {
			throw new BadRequestException("accesso all'attività non consentito");
		}
		for (EsperienzaSvolta esperienza : esperienze) {
			ReportEsperienzaRegistration espReg = new ReportEsperienzaRegistration(esperienza);
			report.getEsperienze().add(espReg);
		}
		return report;
	}

	private boolean checkAccessoAttivita(AttivitaAlternanza aa, List<EsperienzaSvolta> esperienze, ASLUser user) {
		if(user == null) {
			return true;
		}
		/*if(usersValidator.hasRole(user, ASLRole.ADMIN)) {
			return true;
		}*/
		if(usersValidator.hasRole(user, ASLRole.DIRIGENTE_SCOLASTICO, aa.getIstitutoId())) {
			return true;
		}
		if(usersValidator.hasRole(user, ASLRole.FUNZIONE_STRUMENTALE, aa.getIstitutoId())) {
			return true;
		}
		if(usersValidator.hasRole(user, ASLRole.TUTOR_SCOLASTICO, aa.getIstitutoId())) {
			if(user.getCf().equals(aa.getReferenteScuolaCF()) && !aa.getStato().equals(Stati.archiviata)) {
				aa.setTutorScolastico(true);
				return true;
			}
		}
		if(usersValidator.hasRole(user, ASLRole.TUTOR_CLASSE, aa.getIstitutoId())) {
			List<String> classiAssociate = registrazioneDocenteManager.getClassiAssociateRegistrazioneDocente(aa.getIstitutoId(), user.getCf());
			for(EsperienzaSvolta es : esperienze) {
				if(classiAssociate.contains(es.getClasseStudente())) {
					aa.setTutorClasse(true);
					return true;
				}
			}
		}
		return false;
	}

	public void setStudentList(AttivitaAlternanza aa, List<ReportEsperienzaRegistration> listaEspReg) 
			throws Exception {
		if(aa.getStato().equals(Stati.archiviata)) {
			throw new BadRequestException(errorLabelManager.get("attivita.noteditable"));
		}
		List<EsperienzaSvolta> esperienze = esperienzaSvoltaManager.getEsperienzeByAttivita(aa, 
				Sort.by(Sort.Direction.ASC, "nominativoStudente"));
		if(aa.getOffertaId() != null) {
			Offerta offerta = offertaManager.getOfferta(aa.getOffertaId(), true);
			if(offerta != null) {
				int toDelete = 0;
				int toKeep = 0;
				for(EsperienzaSvolta esperienzaDb : esperienze) {
					if(!checkExistingEsperienza(esperienzaDb, listaEspReg)) {
						toDelete++;
					} else {
						toKeep++;
					}
		 		}
				int postiRimanenti = offerta.getPostiRimanenti() + toDelete - (listaEspReg.size() - toKeep);
				if((postiRimanenti) < 0) {
					throw new BadRequestException(errorLabelManager.get("offerta.postiRimanenti"));
				}
				offertaManager.updatePostiRimanenti(offerta.getId(), postiRimanenti);
			}
		}
		for(EsperienzaSvolta esperienzaDb : esperienze) {
			if(!checkExistingEsperienza(esperienzaDb, listaEspReg)) {
				esperienzaSvoltaManager.deleteEsperienza(esperienzaDb);
			}
 		}
		for(ReportEsperienzaRegistration espReg : listaEspReg) {
			if(espReg.getEsperienzaSvoltaId() ==  null) {
				if(!checkExistingEsperienza(espReg, esperienze)) {
					EsperienzaSvolta newEsperienza = new EsperienzaSvolta();
					newEsperienza.setAttivitaAlternanzaId(aa.getId());
					newEsperienza.setCfStudente(espReg.getCfStudente());
					newEsperienza.setClasseStudente(espReg.getClasseStudente());
					newEsperienza.setIstitutoId(aa.getIstitutoId());
					newEsperienza.setNominativoStudente(espReg.getNominativoStudente());
					newEsperienza.setRegistrazioneId(espReg.getRegistrazioneId());
					newEsperienza.setStudenteId(espReg.getStudenteId());
					esperienzaSvoltaManager.addEsperienza(newEsperienza);					
				}
			}
		}
	}
	
	private boolean checkExistingEsperienza(EsperienzaSvolta esperienzaOrig, List<ReportEsperienzaRegistration> listaEspReg) {
		for(ReportEsperienzaRegistration espReg : listaEspReg) {
			if(espReg.isCompatible(esperienzaOrig)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkExistingEsperienza(ReportEsperienzaRegistration espReg, List<EsperienzaSvolta> listaEsperienze) {
		for(EsperienzaSvolta esperienza : listaEsperienze) {
			if(espReg.isCompatible(esperienza)) {
				return true;
			}
		}
		return false;
	}
	
	public void archiveAttivitaAlternanza(AttivitaAlternanza aa, List<ReportArchiviaEsperienza> listaEsperienze) 
			throws Exception {
		if(aa.getStato().equals(Stati.archiviata)) {
			throw new BadRequestException(errorLabelManager.get("attivita.noteditable"));
		}
		for(ReportArchiviaEsperienza report : listaEsperienze) {
			EsperienzaSvolta esperienzaSvolta = esperienzaSvoltaManager.findById(report.getEsperienzaSvoltaId());
			if(esperienzaSvolta == null) {
				throw new BadRequestException(errorLabelManager.get("esperienza.notfound"));
			}
			esperienzaSvoltaManager.chiudiEsperienza(report.getEsperienzaSvoltaId(), report.isValida());
		}
		aa.setDataArchiviazione(LocalDate.now());
		aa.setStato(Stati.archiviata);
		attivitaAlternanzaRepository.updateStato(aa.getId(), Stati.archiviata);
		attivitaAlternanzaRepository.updateDataArchiviazione(aa.getId(), aa.getDataArchiviazione());
	}
	
	public AttivitaAlternanza activateAttivitaAlternanza(Long id) throws Exception {
		AttivitaAlternanza aa = getAttivitaAlternanza(id);
		if(aa == null) {
			throw new BadRequestException(errorLabelManager.get("attivita.alt.error.notfound"));
		}
		if(!aa.getStato().equals(Stati.archiviata)) {
			throw new BadRequestException(errorLabelManager.get("attivita.error.activate"));
		}
		List<EsperienzaSvolta> esperienze = esperienzaSvoltaManager.getEsperienzeByAttivita(aa, Sort.by(Sort.Direction.ASC, "id"));
		for(EsperienzaSvolta es : esperienze) {
			esperienzaSvoltaManager.apriEsperienza(es.getId());
		}
		aa.setDataArchiviazione(null);
		aa.setStato(Stati.attiva);
		attivitaAlternanzaRepository.updateStato(id, Stati.attiva);
		attivitaAlternanzaRepository.updateDataArchiviazione(id, null);
		return aa;
	}

	public List<ReportArchiviaEsperienza> getArchiveAttivitaAlternanza(AttivitaAlternanza aa) {
		List<ReportArchiviaEsperienza> result = new ArrayList<>();
		List<EsperienzaSvolta> esperienze = esperienzaSvoltaManager.getEsperienzeByAttivita(aa, 
				Sort.by(Sort.Direction.ASC, "nominativoStudente"));
		for(EsperienzaSvolta esperienza : esperienze) {
			ReportArchiviaEsperienza report = new ReportArchiviaEsperienza(esperienza);
			report.setOreAttivita(aa.getOre());
			List<PresenzaGiornaliera> presenze = presenzaGiornalieraManager.findByEsperienzaSvolta(esperienza.getId());
			for(PresenzaGiornaliera presenza :  presenze) {
				if(presenza.getVerificata()) {
					report.adOreValidate(presenza.getOreSvolte());
				}
			}
			if(EsperienzaSvolta.Stati.valida.equals(esperienza.getStato())) {
				report.setValida(true);
			}
			if(EsperienzaSvolta.Stati.da_definire.equals(esperienza.getStato())) {
				if(calculatePercentage(report.getOreValidate(), report.getOreAttivita()) >= 75) {
					report.setValida(true);
				}
			}
			result.add(report);
		}
		return result;
	}
	
	private int calculatePercentage(int obtained, int total) {
		return obtained * 100 / total;
	}

	public Page<ReportEsperienzaRegistration> findEsperienze(String istitutoId, String annoScolastico, 
			String text, Pageable pageRequest) {
		return esperienzaSvoltaManager.findEsperienze(istitutoId, annoScolastico, text, pageRequest);
	}
	
	public ReportPresenzeAttvitaAlternanza getReportPresenzeAttvitaAlternanzaIndividuale(AttivitaAlternanza aa) 
			throws Exception {
		
		List<EsperienzaSvolta> esperienze = esperienzaSvoltaManager.getEsperienzeByAttivita(aa, Sort.by(Sort.Direction.ASC, "id"));
		if(esperienze.size() == 0) {
			throw new BadRequestException(errorLabelManager.get("esperienza.notfound"));
		}
		EsperienzaSvolta esperienza = esperienze.get(0);
		
		int oreValidate = 0;
		int oreInserite = 0;
		int giornateValidate = 0;
		int giornateDaValidare = 0;
		List<PresenzaGiornaliera> presenze = presenzaGiornalieraManager.findByEsperienzaSvolta(esperienza.getId());
		for (PresenzaGiornaliera presenza : presenze) {
			oreInserite += presenza.getOreSvolte();
			if(presenza.getVerificata()) {
				oreValidate += presenza.getOreSvolte();
				giornateValidate++;
			} else {
				giornateDaValidare++;
			}
		}
		
		ReportPresenzeAttvitaAlternanza report = new ReportPresenzeAttvitaAlternanza(aa);
		report.setOreValidate(oreValidate);
		report.setOreInserite(oreInserite);
		report.setGiornateValidate(giornateValidate);
		report.setGiornateDaValidare(giornateDaValidare);
		return report;
	}
	
	public ReportPresenzeAttvitaAlternanza getReportPresenzeAttvitaAlternanzaGruppo(AttivitaAlternanza aa) 
			throws Exception {
		String q = "SELECT COUNT(DISTINCT es) FROM EsperienzaSvolta es LEFT JOIN PresenzaGiornaliera pg ON pg.esperienzaSvoltaId=es.id"
				+ " WHERE es.attivitaAlternanzaId=(:attivitaAlternanzaId) AND pg.verificata=false";
		TypedQuery<Long> query = em.createQuery(q, Long.class);
		query.setParameter("attivitaAlternanzaId", aa.getId());
		Long studentiDaValidare = query.getSingleResult();
	
		q = "SELECT COUNT(DISTINCT pg) FROM EsperienzaSvolta es, PresenzaGiornaliera pg WHERE es.attivitaAlternanzaId=(:attivitaAlternanzaId)"
			+ " AND pg.esperienzaSvoltaId=es.id AND pg.verificata=true";
		query = em.createQuery(q, Long.class);
		query.setParameter("attivitaAlternanzaId", aa.getId());
		Long giornateValidate = query.getSingleResult();
	
		q = "SELECT COUNT(DISTINCT pg) FROM EsperienzaSvolta es, PresenzaGiornaliera pg WHERE es.attivitaAlternanzaId=(:attivitaAlternanzaId)"
			+ " AND pg.esperienzaSvoltaId=es.id AND pg.verificata=false";
		query = em.createQuery(q, Long.class);
		query.setParameter("attivitaAlternanzaId", aa.getId());
		Long giornateDaValidare = query.getSingleResult();
		
		ReportPresenzeAttvitaAlternanza report = new ReportPresenzeAttvitaAlternanza(aa);
		report.setStudentiDaValidare(studentiDaValidare.intValue());
		report.setGiornateValidate(giornateValidate.intValue());
		report.setGiornateDaValidare(giornateDaValidare.intValue());
		return report;
	}
	
	public List<PresenzaGiornaliera> getPresenzeAttivitaIndividuale(AttivitaAlternanza aa, LocalDate dateFrom, 
			LocalDate dateTo, ASLUser user) throws Exception {
		//check accesso
		getAttivitaAlternanzaDetails(aa, user);	
		String q = "SELECT pg FROM EsperienzaSvolta es, PresenzaGiornaliera pg WHERE es.attivitaAlternanzaId=(:attivitaAlternanzaId)"
				+ " AND pg.esperienzaSvoltaId=es.id AND pg.giornata >= (:dateFrom) AND pg.giornata <= (:dateTo) ORDER BY pg.giornata ASC";
		
		TypedQuery<PresenzaGiornaliera> query = em.createQuery(q, PresenzaGiornaliera.class);
		query.setParameter("attivitaAlternanzaId", aa.getId());
		query.setParameter("dateFrom", dateFrom);
		query.setParameter("dateTo", dateTo);
	
		List<PresenzaGiornaliera> list = query.getResultList();
		//fix duplicated days in db
		List<PresenzaGiornaliera> resultList = removeDuplicatedDays(list);
		return resultList;
	}

	public List<PresenzaGiornaliera> validaPresenzeAttivita(AttivitaAlternanza aa, List<PresenzaGiornaliera> presenze) throws Exception {
		List<PresenzaGiornaliera> result = new ArrayList<>();
		for(PresenzaGiornaliera pg : presenze) {
			PresenzaGiornaliera presenzaGiornaliera = presenzaGiornalieraManager.validaPresenza(pg);
			if(presenzaGiornaliera != null) {
				result.add(presenzaGiornaliera);	
			}
		}
		return  result;
	}
	
	public List<PresenzaGiornaliera> validaPresenzeAttivitaByEnte(AttivitaAlternanza aa, List<PresenzaGiornaliera> presenze) throws Exception {
		List<PresenzaGiornaliera> result = new ArrayList<>();
		for(PresenzaGiornaliera pg : presenze) {
			PresenzaGiornaliera presenzaGiornaliera = presenzaGiornalieraManager.validaPresenzaByEnte(pg);
			if(presenzaGiornaliera != null) {
				result.add(presenzaGiornaliera);	
			}
		}
		return  result;
	}
	
	public List<ReportPresenzaGiornalieraGruppo> getPresenzeAttivitaGruppo(AttivitaAlternanza aa, LocalDate dateFrom, 
			LocalDate dateTo, ASLUser user) throws Exception {
		//check accesso
		getAttivitaAlternanzaDetails(aa, user);	

		List<ReportPresenzaGiornalieraGruppo> reportList = new ArrayList<ReportPresenzaGiornalieraGruppo>();
		
		String qEsperienze = "SELECT DISTINCT es FROM EsperienzaSvolta es WHERE es.attivitaAlternanzaId=(:attivitaAlternanzaId)"
				+ " ORDER BY es.nominativoStudente, es.classeStudente, es.studenteId ASC";
		
		TypedQuery<EsperienzaSvolta> queryEsperienze = em.createQuery(qEsperienze, EsperienzaSvolta.class);
		queryEsperienze.setParameter("attivitaAlternanzaId", aa.getId());
		
		List<EsperienzaSvolta> esperienze = queryEsperienze.getResultList();
		esperienze.forEach(e -> {
			ReportPresenzaGiornalieraGruppo report = new ReportPresenzaGiornalieraGruppo(e);
			report.setOreTotali(aa.getOre());
			reportList.add(report);
		});
		
		String qPresenze = "SELECT pg FROM EsperienzaSvolta es, PresenzaGiornaliera pg WHERE es.attivitaAlternanzaId=(:attivitaAlternanzaId)"
				+ " AND pg.esperienzaSvoltaId=es.id AND pg.giornata >= (:dateFrom) AND pg.giornata <= (:dateTo)"
				+ " ORDER BY es.studenteId, pg.giornata ASC";
		
		TypedQuery<PresenzaGiornaliera> queryPresenze = em.createQuery(qPresenze, PresenzaGiornaliera.class);
		queryPresenze.setParameter("attivitaAlternanzaId", aa.getId());
		queryPresenze.setParameter("dateFrom", dateFrom);
		queryPresenze.setParameter("dateTo", dateTo);
		List<PresenzaGiornaliera> list = queryPresenze.getResultList();
		//fix duplicated days in db
		List<PresenzaGiornaliera> presenze = removeDuplicatedDays(list);

		for (PresenzaGiornaliera pg : presenze) {
			ReportPresenzaGiornalieraGruppo report = checkReport(pg.getEsperienzaSvoltaId(), reportList);
			if(report != null) {
				report.getPresenze().add(pg);
				if(pg.getVerificata()) {
					report.addOreValidate(pg.getOreSvolte());
				} else {
					report.addOreDaValidare(pg.getOreSvolte());
				}				
			}
		}
		return reportList;
	}

	private ReportPresenzaGiornalieraGruppo checkReport(Long esperienzaSvoltaId, 
			List<ReportPresenzaGiornalieraGruppo> reportList) {
		for (ReportPresenzaGiornalieraGruppo report : reportList) {
			if(report.getEsperienzaSvoltaId().equals(esperienzaSvoltaId)) {
				return report;
			}
		}
		return null;
	}
	
	public List<ReportEsperienzaStudente> getReportEsperienzaStudente(String istitutoId, String studenteId) {
		String qEsperienze = "SELECT aa,es FROM AttivitaAlternanza aa, EsperienzaSvolta es WHERE es.attivitaAlternanzaId=aa.id"
				+ " AND aa.istitutoId=(:istitutoId) AND es.studenteId=(:studenteId)"
				+ " ORDER BY aa.dataInizio DESC, aa.titolo ASC";
		Query query = em.createQuery(qEsperienze);
		query.setParameter("istitutoId", istitutoId);
		query.setParameter("studenteId", studenteId);
		@SuppressWarnings("unchecked")
		List<Object[]> result = query.getResultList();
		
		List<ReportEsperienzaStudente> reportList = new ArrayList<>();
		Map<Long, ReportEsperienzaStudente> reportMap = new HashMap<>();
		for (Object[] obj : result) {
			AttivitaAlternanza aa = (AttivitaAlternanza) obj[0];
			EsperienzaSvolta es = (EsperienzaSvolta) obj[1];
			ReportEsperienzaStudente report = new ReportEsperienzaStudente(aa, es);
			report.setStato(getStato(aa).toString());
			reportList.add(report);
			reportMap.put(es.getId(), report);
		}
		
		fillReportEsperienzaStudenteWithPresenze(studenteId, reportMap);
		return reportList;
	}
	
	public Page<ReportEsperienzaStudente> getReportEsperienzaStudente(String studenteId, String stato, Pageable pageRequest) {
		String qEsperienze = "SELECT aa,es FROM AttivitaAlternanza aa, EsperienzaSvolta es"
				+ " WHERE es.attivitaAlternanzaId=aa.id AND es.studenteId=(:studenteId)";
		
		boolean setDataParam = false;
		if(Utils.isNotEmpty(stato)) {
			Stati statoEnum = Stati.valueOf(stato);
			if(statoEnum == Stati.archiviata) {
				qEsperienze += " AND aa.stato='" + Stati.archiviata.toString() + "'";
			} else {
				qEsperienze += " AND aa.stato='" + Stati.attiva.toString() + "'";
				setDataParam = true;
				if(statoEnum == Stati.in_attesa) {
					qEsperienze += " AND aa.dataInizio > (:data)";
				}
				if(statoEnum == Stati.revisione) {
					qEsperienze += " AND aa.dataFine < (:data)";
				}
				if(statoEnum == Stati.in_corso) {
					qEsperienze += " AND aa.dataInizio <= (:data) AND aa.dataFine >= (:data)";
				}
			}
		}
		
		qEsperienze += " ORDER BY aa.dataInizio DESC, aa.titolo ASC";

		Query query = em.createQuery(qEsperienze);
		query.setParameter("studenteId", studenteId);
		if(setDataParam) {
			LocalDate localDate = LocalDate.now(); 
			query.setParameter("data", localDate);
		}

		query.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
		query.setMaxResults(pageRequest.getPageSize());
		@SuppressWarnings("unchecked")
		List<Object[]> result = query.getResultList();
		
		List<ReportEsperienzaStudente> reportList = new ArrayList<>();
		Map<Long, ReportEsperienzaStudente> reportMap = new HashMap<>();
		for (Object[] obj : result) {
			AttivitaAlternanza aa = (AttivitaAlternanza) obj[0];
			EsperienzaSvolta es = (EsperienzaSvolta) obj[1];
			ReportEsperienzaStudente report = new ReportEsperienzaStudente(aa, es);
			report.setStato(getStato(aa).toString());
			try {
				ValutazioneAttivitaReport valutazione = valutazioniManager.getValutazioneAttivitaReportByStudente(es.getId(), studenteId);
				report.setValutazioneAttivita(valutazione.getStato().toString());
			} catch (Exception e) {
				report.setValutazioneAttivita(ValutazioneAttivitaReport.Stato.non_compilata.toString());
			}
			reportList.add(report);
			reportMap.put(es.getId(), report);
		}
		fillReportEsperienzaStudenteWithPresenze(studenteId, reportMap);
		
		Query cQuery = queryToCountQuery(qEsperienze, query);
		long total = (Long) cQuery.getSingleResult();
		
		Page<ReportEsperienzaStudente> page = new PageImpl<ReportEsperienzaStudente>(reportList, pageRequest, total);
		return page;
	}
	
	private void fillReportEsperienzaStudenteWithPresenze(String studenteId,
			Map<Long, ReportEsperienzaStudente> reportMap) {
		String qPresenze = "SELECT pg FROM EsperienzaSvolta es, PresenzaGiornaliera pg WHERE es.studenteId=(:studenteId)"
				+ " AND pg.esperienzaSvoltaId=es.id";
		TypedQuery<PresenzaGiornaliera> queryPresenze = em.createQuery(qPresenze, PresenzaGiornaliera.class);
		queryPresenze.setParameter("studenteId", studenteId);
		List<PresenzaGiornaliera> presenze = queryPresenze.getResultList();
		for (PresenzaGiornaliera pg : presenze) {
			ReportEsperienzaStudente report = reportMap.get(pg.getEsperienzaSvoltaId());
			if(report != null) {
				if(pg.getVerificata()) {
					report.setOreValidate(report.getOreValidate() + pg.getOreSvolte());
					if(pg.getSmartWorking()) {
						report.setOreSmartWorking(report.getOreSmartWorking() + pg.getOreSvolte());
					}
				} else {
					report.setOreDaValidare(report.getOreDaValidare() + pg.getOreSvolte());
				}
			}
		}
	}

	public AttivitaAlternanza associaOfferta(long offertaId, String istitutoId, Boolean rendicontazioneCorpo) 
			throws BadRequestException {
		Offerta offerta = offertaManager.getOfferta(offertaId, true);
		if(offerta == null) {
			throw new BadRequestException(errorLabelManager.get("offerta.notfound"));
		}
		if(Utils.isEmpty(offerta.getEnteId()) || Utils.isEmpty(offerta.getReferenteEsterno())) {
			throw new BadRequestException("offerta di riferimento con dati incompleti");
		}
		AttivitaAlternanza aa = creaAttivitaFromOfferta(offerta, istitutoId, rendicontazioneCorpo);
		return saveAttivitaAlternanza(aa, istitutoId);
	}

	private AttivitaAlternanza creaAttivitaFromOfferta(Offerta offerta, String istitutoId, Boolean rendicontazioneCorpo) {
		String titolo = offerta.getTitolo() + " - " + 
				(attivitaAlternanzaRepository.countByOffertaIdAndIstitutoId(offerta.getId(), istitutoId) + 1);
		AttivitaAlternanza aa = new AttivitaAlternanza();
		aa.setTitolo(titolo);
		aa.setTipologia(offerta.getTipologia());
		aa.setAnnoScolastico(Utils.annoScolastico(offerta.getDataInizio()));
		aa.setDescrizione(offerta.getDescrizione());
		aa.setDataInizio(offerta.getDataInizio());
		aa.setDataFine(offerta.getDataFine());
		aa.setOraInizio(offerta.getOraInizio());
		aa.setOraFine(offerta.getOraFine());
		aa.setOre(offerta.getOre());
		aa.setOffertaId(offerta.getId());
		aa.setTitoloOfferta(offerta.getTitolo());
		aa.setEnteId(offerta.getEnteId());
		aa.setNomeEnte(offerta.getNomeEnte());
		aa.setReferenteScuola(offerta.getReferenteScuola());
		aa.setReferenteScuolaCF(offerta.getReferenteScuolaCF());
		aa.setReferenteEsterno(offerta.getReferenteEsterno());
		aa.setReferenteEsternoCF(offerta.getReferenteEsternoCF());
		aa.setFormatore(offerta.getFormatore());
		aa.setFormatoreCF(offerta.getFormatoreCF());
		aa.setLuogoSvolgimento(offerta.getLuogoSvolgimento());
		aa.setLatitude(offerta.getLatitude());
		aa.setLongitude(offerta.getLongitude());
		aa.setRendicontazioneCorpo(rendicontazioneCorpo);
		return aa;
	}

	public List<ReportEsperienzaStudente> getReportEsperienzaStudente(String studenteId) {
		String qEsperienze = "SELECT aa,es FROM AttivitaAlternanza aa, EsperienzaSvolta es"
				+ " WHERE es.attivitaAlternanzaId=aa.id AND es.studenteId=(:studenteId)"
				+ " ORDER BY aa.dataInizio DESC, aa.titolo ASC";
		Query query = em.createQuery(qEsperienze);
		query.setParameter("studenteId", studenteId);
		@SuppressWarnings("unchecked")
		List<Object[]> result = query.getResultList();
		
		List<ReportEsperienzaStudente> reportList = new ArrayList<>();
		Map<Long, ReportEsperienzaStudente> reportMap = new HashMap<>();
		for (Object[] obj : result) {
			AttivitaAlternanza aa = (AttivitaAlternanza) obj[0];
			EsperienzaSvolta es = (EsperienzaSvolta) obj[1];
			ReportEsperienzaStudente report = new ReportEsperienzaStudente(aa, es);
			report.setStato(getStato(aa).toString());
			reportList.add(report);
			reportMap.put(es.getId(), report);
		}
		fillReportEsperienzaStudenteWithPresenze(studenteId, reportMap);
		return reportList;
	}

	public Page<ReportAttivitaAlternanzaRicercaEnte> findAttivitaByEnte(String enteId, String text, 
			String stato, String istitutoId, Pageable pageRequest) {
		StringBuilder sb = new StringBuilder("SELECT DISTINCT aa FROM AttivitaAlternanza aa, EsperienzaSvolta es, Istituzione i, Convenzione c");
		sb.append(" WHERE es.attivitaAlternanzaId=aa.id");
		sb.append(" AND aa.istitutoId=i.id AND aa.enteId=(:enteId)");
		sb.append(" AND (aa.tipologia=7 OR aa.tipologia=10)");
		sb.append(" AND c.istitutoId=aa.istitutoId AND c.enteId=(:enteId)");
		sb.append(" AND c.dataFine>=(:oggi) AND aa.dataFine>=(:unAnnoFa)");
		if(Utils.isNotEmpty(istitutoId)) {
			sb.append(" AND aa.istitutoId=(:istitutoId)");
		}
		if(Utils.isNotEmpty(text)) {
			sb.append(" AND (UPPER(aa.titolo) LIKE (:text) OR UPPER(es.nominativoStudente) LIKE (:text) OR UPPER(i.name) LIKE (:text))");
		}
		
		boolean setDataParam = false;
		if(Utils.isNotEmpty(stato)) {
			Stati statoEnum = Stati.valueOf(stato);
			if(statoEnum == Stati.archiviata) {
				sb.append(" AND aa.stato='" + Stati.archiviata.toString() + "'");
			} else {
				sb.append(" AND aa.stato='" + Stati.attiva.toString() + "'");
				setDataParam = true;
				if(statoEnum == Stati.in_attesa) {
					sb.append(" AND aa.dataInizio > (:data)");
				}
				if(statoEnum == Stati.revisione) {
					sb.append(" AND aa.dataFine < (:data)");
					setDataParam = true;
				}
				if(statoEnum == Stati.in_corso) {
					sb.append(" AND aa.dataInizio <= (:data) AND aa.dataFine >= (:data)");
					setDataParam = true;
				}
			}
		}
				
		sb.append(" ORDER BY aa.dataInizio DESC, aa.titolo ASC");
		String q = sb.toString();

		TypedQuery<AttivitaAlternanza> query = em.createQuery(q, AttivitaAlternanza.class);
		
		query.setParameter("enteId", enteId);
		query.setParameter("oggi", LocalDate.now());
		query.setParameter("unAnnoFa", LocalDate.now().minusYears(1));				
		if(Utils.isNotEmpty(istitutoId)) {
			query.setParameter("istitutoId", istitutoId);
		}
		if(Utils.isNotEmpty(text)) {
			query.setParameter("text", "%" + text.trim().toUpperCase() + "%");
		}
		if(setDataParam) {
			LocalDate localDate = LocalDate.now(); 
			query.setParameter("data", localDate);
		}
		
		query.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
		query.setMaxResults(pageRequest.getPageSize());
		List<AttivitaAlternanza> aaList = query.getResultList();
		
		List<ReportAttivitaAlternanzaRicercaEnte> reportList = new ArrayList<>();
		Map<Long, ReportAttivitaAlternanzaRicercaEnte> reportMap = new HashMap<>();
		List<Long> aaIdList = new ArrayList<>();
		for (AttivitaAlternanza aa : aaList) {
			ReportAttivitaAlternanzaRicercaEnte report = new ReportAttivitaAlternanzaRicercaEnte(aa); 
			report.setStato(getStato(aa).toString());
			Istituzione istituto = istituzioneManager.getIstituto(aa.getIstitutoId(), null);
			if(istituto != null) {
				report.setNomeIstituto(istituto.getName());
			}
			reportMap.put(aa.getId(), report);
			reportList.add(report);
			aaIdList.add(aa.getId());
		}
		
		List<EsperienzaSvolta> esperienze = esperienzaSvoltaManager.getEsperienzeByAttivitaIds(aaIdList);
		for(EsperienzaSvolta es : esperienze) {
			ReportAttivitaAlternanzaRicercaEnte report = reportMap.get(es.getAttivitaAlternanzaId());
			if(report != null) {
				report.getStudenti().add(es.getNominativoStudente());
			}
		}
		
		Query cQuery = queryToCount(q.replaceAll("DISTINCT aa","COUNT(DISTINCT aa)"), query);
		long total = (Long) cQuery.getSingleResult();
		
		Page<ReportAttivitaAlternanzaRicercaEnte> page = new PageImpl<ReportAttivitaAlternanzaRicercaEnte>(reportList, pageRequest, total);
		return page;
	}

	public AttivitaAlternanza updateAttivitaAlternanzaByEnte(AttivitaAlternanza aa, String enteId) throws Exception {
		AttivitaAlternanza aaDb = getAttivitaAlternanza(aa.getId());
		if(aaDb.getStato().equals(Stati.archiviata)) {
			throw new BadRequestException(errorLabelManager.get("attivita.noteditable"));
		}
		convenzioneManager.checkAttivitaByEnte(aa, enteId);
		attivitaAlternanzaRepository.updateAttivitaAlternanzaByEnte(aa);
		return getAttivitaAlternanza(aa.getId());
	}

	public ReportAttivitaAlternanzaStudentiEnte getStudentInfoEnte(AttivitaAlternanza attivitaAlternanza) {
		ReportAttivitaAlternanzaStudentiEnte report = new ReportAttivitaAlternanzaStudentiEnte(attivitaAlternanza);
		List<EsperienzaSvolta> esperienze = esperienzaSvoltaManager.getEsperienzeByAttivita(attivitaAlternanza, 
				Sort.by(Sort.Direction.ASC, "nominativoStudente"));
		for(EsperienzaSvolta esperienza : esperienze) {
			List<PresenzaGiornaliera> presenze = presenzaGiornalieraManager.findByEsperienzaSvolta(esperienza.getId());
			for(PresenzaGiornaliera presenza :  presenze) {
				if(!presenza.getValidataEnte() && !presenza.getVerificata()) {
					report.addOreDaValidare(presenza.getOreSvolte());
				}
			}
			if(report.getNumeroOreDaValidare() > 0) {
				report.addStudenteDaValidare();
			}
		}
		return report;
	}

	public List<AttivitaAlternanza> findAttivitaByStudenteAndEnte(String studenteId, String enteId) {
		StringBuilder sb = new StringBuilder("SELECT DISTINCT aa FROM AttivitaAlternanza aa, EsperienzaSvolta es, Convenzione c");
		sb.append(" WHERE es.attivitaAlternanzaId=aa.id AND aa.enteId=(:enteId) AND es.studenteId=(:studenteId)");
		sb.append(" AND (aa.tipologia=7 OR aa.tipologia=10)");
		sb.append(" AND c.istitutoId=aa.istitutoId AND c.enteId=(:enteId)");
		sb.append(" AND c.dataFine>=(:oggi) AND aa.dataFine>=(:unAnnoFa)");
		sb.append(" ORDER BY aa.dataInizio DESC");

		TypedQuery<AttivitaAlternanza> query = em.createQuery(sb.toString(), AttivitaAlternanza.class);
		query.setParameter("enteId", enteId);
		query.setParameter("studenteId", studenteId);
		query.setParameter("oggi", LocalDate.now());
		query.setParameter("unAnnoFa", LocalDate.now().minusYears(1));
		List<AttivitaAlternanza> list = query.getResultList();
		List<AttivitaAlternanza> result = new ArrayList<>();
		for(AttivitaAlternanza aa : list) {
			AttivitaAlternanza attivita = aa.clona();
			attivita.setStato(getStato(aa));
			int oreSvolte = 0;
			List<EsperienzaSvolta> esperienze = esperienzaSvoltaManager.getEsperienzeByAttivitaAndStudente(aa.getId(), studenteId);
			if(esperienze.size() > 0) {
				List<PresenzaGiornaliera> presenze = presenzaGiornalieraManager.findByEsperienzaSvolta(esperienze.get(0).getId());
				for(PresenzaGiornaliera pg : presenze) {
					if(pg.getVerificata() || pg.getValidataEnte()) {
						oreSvolte += pg.getOreSvolte();
					}
				}
			}
			attivita.setOreSvolte(oreSvolte);
			result.add(attivita);
		}
		return result;
	}
	
	public List<AttivitaAlternanza> findAttivitaByIstitutoAndEnte(String istitutoId, String enteId) {
		StringBuilder sb = new StringBuilder("SELECT DISTINCT aa FROM AttivitaAlternanza aa, Convenzione c");
		sb.append(" WHERE aa.enteId=(:enteId) AND aa.istitutoId=(:istitutoId)");
		sb.append(" AND (aa.tipologia=7 OR aa.tipologia=10)");
		sb.append(" AND c.istitutoId=aa.istitutoId AND c.enteId=(:enteId) ");
		sb.append(" AND c.dataFine>=(:oggi) AND aa.dataFine>=(:unAnnoFa)");
		sb.append(" ORDER BY aa.dataInizio DESC");

		TypedQuery<AttivitaAlternanza> query = em.createQuery(sb.toString(), AttivitaAlternanza.class);
		query.setParameter("enteId", enteId);
		query.setParameter("istitutoId", istitutoId);
		query.setParameter("oggi", LocalDate.now());
		query.setParameter("unAnnoFa", LocalDate.now().minusYears(1));
		List<AttivitaAlternanza> result = query.getResultList();
		return result;
	}
	
	public AttivitaAlternanza findByUuid(String uuid) {
		return attivitaAlternanzaRepository.findByUuid(uuid);
	}

	public AttivitaAlternanza duplicaAttivitaAlternanza(Long attivitaId, String istitutoId) throws Exception {
		AttivitaAlternanza aaDb = getAttivitaAlternanza(attivitaId);
		if(aaDb == null) {
			throw new BadRequestException(errorLabelManager.get("attivita.alt.error.notfound"));		
		}
		if(!aaDb.getIstitutoId().equals(istitutoId)) {
			throw new BadRequestException("istituto non corrispondente");
		}
		AttivitaAlternanza aa = new AttivitaAlternanza();
		aa.setAnnoScolastico(aaDb.getAnnoScolastico());
		aa.setDataArchiviazione(aaDb.getDataArchiviazione());
		aa.setDataFine(aaDb.getDataFine());
		aa.setDataInizio(aaDb.getDataInizio());
		aa.setDescrizione(aaDb.getDescrizione());
		aa.setEnteId(aaDb.getEnteId());
		aa.setFormatore(aaDb.getFormatore());
		aa.setFormatoreCF(aaDb.getFormatoreCF());
		aa.setIstitutoId(istitutoId);
		aa.setLatitude(aaDb.getLatitude());
		aa.setLongitude(aaDb.getLongitude());
		aa.setLuogoSvolgimento(aaDb.getLuogoSvolgimento());
		aa.setNomeEnte(aaDb.getNomeEnte());
		aa.setOffertaId(aaDb.getOffertaId());
		aa.setOraFine(aaDb.getOraFine());
		aa.setOraInizio(aaDb.getOraInizio());
		aa.setOre(aaDb.getOre());
		aa.setReferenteEsterno(aaDb.getReferenteEsterno());
		aa.setReferenteEsternoCF(aaDb.getReferenteEsternoCF());
		aa.setReferenteEsternoTelefono(aaDb.getReferenteEsternoTelefono());
		aa.setReferenteScuola(aaDb.getReferenteScuola());
		aa.setReferenteScuolaCF(aaDb.getReferenteScuolaCF());
		aa.setReferenteScuolaTelefono(aaDb.getReferenteScuolaTelefono());
		aa.setStato(Stati.attiva);
		aa.setTipologia(aaDb.getTipologia());
		aa.setTitolo(aaDb.getTitolo());
		aa.setTitoloOfferta(aaDb.getTitoloOfferta());
		aa.setUuid(Utils.getUUID());
		aa.setRendicontazioneCorpo(aaDb.getRendicontazioneCorpo());
		attivitaAlternanzaRepository.save(aa);
		return aa;
	}
	
	public List<ReportEsperienzaRegistration> getReportPresenzeCorpo(AttivitaAlternanza aa) {
		List<ReportEsperienzaRegistration> result = new ArrayList<>();
		List<EsperienzaSvolta> esperienze = esperienzaSvoltaManager.getEsperienzeByAttivita(aa, 
				Sort.by(Sort.Direction.ASC, "nominativoStudente"));
		for (EsperienzaSvolta esperienza : esperienze) {
			ReportEsperienzaRegistration espReg = new ReportEsperienzaRegistration(esperienza);
			result.add(espReg);
		}
		return result;
	}
	
	public List<ReportEsperienzaRegistration> aggiornaReportPresenzeCorpo(AttivitaAlternanza aa, 
			List<ReportEsperienzaRegistration> presenze) {
		List<ReportEsperienzaRegistration> result = new ArrayList<>();
		if(aa.getRendicontazioneCorpo()) {
			for(ReportEsperienzaRegistration report : presenze) {
				EsperienzaSvolta esperienzaSvolta = esperienzaSvoltaManager.findById(report.getEsperienzaSvoltaId());
				if((esperienzaSvolta != null) && (esperienzaSvolta.getAttivitaAlternanzaId().equals(aa.getId()))) {
					esperienzaSvolta.setOreRendicontate(report.getOreRendicontate());
					setPresenzeCorpo(aa, esperienzaSvolta);
					esperienzaSvoltaManager.updateOreRendicontate(report.getEsperienzaSvoltaId(), report.getOreRendicontate());
					ReportEsperienzaRegistration espReg = new ReportEsperienzaRegistration(esperienzaSvolta);
					result.add(espReg);					
				}
			}			
		}
		return result;
	}
	
	private void setPresenzeCorpo(AttivitaAlternanza aa, EsperienzaSvolta es) {
		presenzaGiornalieraManager.deletePresenzeByEsperienza(es.getId());
		PresenzaGiornaliera pg = new PresenzaGiornaliera();
		pg.setEsperienzaSvoltaId(es.getId());
		pg.setGiornata(aa.getDataInizio());
		pg.setIstitutoId(aa.getIstitutoId());
		pg.setOreSvolte(es.getOreRendicontate());
		pg.setSmartWorking(false);
		pg.setValidataEnte(false);
		pg.setVerificata(true);
		presenzaGiornalieraManager.savePresenza(pg);		
	}
	
}
