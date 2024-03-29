package it.smartcommunitylab.cartella.asl.controller;

import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

import it.smartcommunitylab.cartella.asl.manager.ASLRolesValidator;
import it.smartcommunitylab.cartella.asl.manager.AuditManager;
import it.smartcommunitylab.cartella.asl.manager.StudenteManager;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;
import it.smartcommunitylab.cartella.asl.model.PresenzaGiornaliera;
import it.smartcommunitylab.cartella.asl.model.Studente;
import it.smartcommunitylab.cartella.asl.model.audit.AuditEntry;
import it.smartcommunitylab.cartella.asl.model.report.ReportDettaglioAttivitaEsperienza;
import it.smartcommunitylab.cartella.asl.model.report.ReportDettaglioStudente;
import it.smartcommunitylab.cartella.asl.model.report.ReportEsperienzaStudente;
import it.smartcommunitylab.cartella.asl.model.report.ReportStudenteDettaglioEnte;
import it.smartcommunitylab.cartella.asl.model.report.ReportStudenteEnte;
import it.smartcommunitylab.cartella.asl.model.report.ReportStudenteRicerca;
import it.smartcommunitylab.cartella.asl.model.report.ReportStudenteSommario;
import it.smartcommunitylab.cartella.asl.model.users.ASLAuthCheck;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;

@RestController
public class StudentController implements AslController {

	@Autowired
	private ASLRolesValidator usersValidator;

	@Autowired
	private AuditManager auditManager;

	@Autowired
	private StudenteManager studentManager;

	private static Log logger = LogFactory.getLog(StudentController.class);

	@GetMapping("/api/studente/{id}")
	public Studente getStudente(
			@PathVariable String id, 
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, new ASLAuthCheck(ASLRole.STUDENTE, id));
		Studente studente = studentManager.findStudente(id);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getStudente:%s", id));
		}
		return studente;
	}

	@GetMapping("/api/studente/ricerca/{istitutoId}")
	public Page<ReportStudenteRicerca> getStudentsByIstitute(
			@PathVariable String istitutoId,
			@RequestParam(required = true) String annoScolastico, 
			@RequestParam(required = false) String corsoId,
			@RequestParam(required = false) String text, 
			Pageable pageRequest, 
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(
				new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId),
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId),
				new ASLAuthCheck(ASLRole.TUTOR_SCOLASTICO, istitutoId)));
		Page<ReportStudenteRicerca> page = studentManager.findStudentiRicercaPaged(istitutoId, annoScolastico, corsoId, 
				text, pageRequest, user);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getStudentsByIstitute:%s / %s", istitutoId, annoScolastico));
		}
		return page; 
	}
	
	@GetMapping("/api/studente/{studenteId}/istituto/report/details")
	public ReportDettaglioStudente getReportDettaglioStudente(
			@PathVariable String studenteId,
			@RequestParam String istitutoId, 
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(
				new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId),
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId),
				new ASLAuthCheck(ASLRole.TUTOR_SCOLASTICO, istitutoId)));
		ReportDettaglioStudente report = studentManager.getReportDettaglioStudente(istitutoId, studenteId, user);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getReportDettaglioStudente:%s / %s", istitutoId, studenteId));
		}
		return report;
	}
	
	@GetMapping("/api/studente/attivita/sommario")
	public ReportStudenteSommario getReportSommarioStudente(
			@RequestParam String studenteId,
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, new ASLAuthCheck(ASLRole.STUDENTE, studenteId));
		ReportStudenteSommario report = studentManager.getReportStudenteSommario(studenteId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getReportSommarioStudente:%s", studenteId));
		}
		return report;
	}
	
	@GetMapping("/api/studente/attivita")
	public Page<ReportEsperienzaStudente> getStudenteListaEsperienze(
			@RequestParam String studenteId,
			@RequestParam(required = false) String stato,
			Pageable pageRequest,
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, new ASLAuthCheck(ASLRole.STUDENTE, studenteId));
		Page<ReportEsperienzaStudente> page = studentManager.getReportEsperienzaStudenteList(studenteId, stato, pageRequest);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getStudenteListaEsperienze:%s", studenteId));
		}
		return page;
	}
	
	@GetMapping("/api/studente/{studenteId}/esperienza/{esperienzaSvoltaId}")
	public ReportDettaglioAttivitaEsperienza getReportDettaglioAttivitaEsperienza(
			@PathVariable String studenteId,
			@PathVariable Long esperienzaSvoltaId,
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, new ASLAuthCheck(ASLRole.STUDENTE, studenteId));
		ReportDettaglioAttivitaEsperienza report = studentManager.getReportDettaglioAttivitaEsperienza(
				esperienzaSvoltaId, studenteId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getReportDettaglioAttivitaEsperienza:%s / %s", studenteId, esperienzaSvoltaId));
		}	
		return report;
	}
	
	@GetMapping("/api/studente/{studenteId}/esperienza/{esperienzaSvoltaId}/presenze")
	public List<PresenzaGiornaliera> getPresenze(
			@PathVariable String studenteId,
			@PathVariable Long esperienzaSvoltaId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, new ASLAuthCheck(ASLRole.STUDENTE, studenteId));
		studentManager.checkEsperienzeStudente(esperienzaSvoltaId, studenteId);
		List<PresenzaGiornaliera> presenze = studentManager.getPresenzeStudente(esperienzaSvoltaId, 
				dateFrom, dateTo);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getPresenze:%s / %s", studenteId, esperienzaSvoltaId));
		}	
		return presenze;
	}
	
	@PostMapping("/api/studente/{studenteId}/esperienza/{esperienzaSvoltaId}/presenze")
	public List<PresenzaGiornaliera> aggiornaPresenze(
			@PathVariable String studenteId,
			@PathVariable Long esperienzaSvoltaId,
			@RequestBody List<PresenzaGiornaliera> presenze,
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, new ASLAuthCheck(ASLRole.STUDENTE, studenteId));
		studentManager.checkEsperienzeStudente(esperienzaSvoltaId, studenteId);
		List<PresenzaGiornaliera> list = studentManager.aggiornaPresenze(esperienzaSvoltaId, presenze);
		AuditEntry audit = new AuditEntry(request.getMethod(), EsperienzaSvolta.class, esperienzaSvoltaId, user, new Object(){});
		auditManager.save(audit);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("aggiornaPresenze:%s / %s", studenteId, esperienzaSvoltaId));
		}	
		return list;
	}
	
	@PostMapping("/api/studente/{studenteId}/notifica")
	public void aggiornaNotifica(
			@PathVariable String studenteId,
			@RequestParam boolean attiva,
			@RequestBody String registrationToken,
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, new ASLAuthCheck(ASLRole.STUDENTE, studenteId));
		if(attiva) {
			studentManager.attivaNotifica(studenteId, registrationToken);
		} else {
			studentManager.disattivaNotifica(studenteId, registrationToken);
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("aggiornaNotifica:%s / %s / %s", studenteId, attiva, registrationToken));
		}			
	}
	
	@PostMapping("/api/studente/{studenteId}")
	public Studente updateProfilo(
			@PathVariable String studenteId,
			@RequestBody Studente studente,
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, new ASLAuthCheck(ASLRole.STUDENTE, studenteId));
		Studente result = studentManager.updateProfile(studente, studenteId);
		AuditEntry audit = new AuditEntry(request.getMethod(), Studente.class, studenteId, user, new Object(){});
		auditManager.save(audit);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateProfilo:%s", studenteId));
		}	
		return result;
	}
	
	@GetMapping("/api/studente/ricerca/{enteId}/ente")
	public Page<ReportStudenteEnte> getStudentsByEnte(
			@PathVariable String enteId,
			@RequestParam(required = false) String text,
			Pageable pageRequest,
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, enteId), 
				new ASLAuthCheck(ASLRole.REFERENTE_AZIENDA, enteId)));
		Page<ReportStudenteEnte> page = studentManager.findStudentiByEnte(enteId, text, pageRequest);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getStudentsByEnte:%s / %s", enteId, text));
		}
		return page; 
	}
	
	@GetMapping("/api/studente/dettaglio/{enteId}/ente")
	public ReportStudenteDettaglioEnte getStudenteDettaglioEnte(
			@PathVariable String enteId,
			@RequestParam String studenteId,
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, enteId), 
				new ASLAuthCheck(ASLRole.REFERENTE_AZIENDA, enteId)));
		ReportStudenteDettaglioEnte report = studentManager.getStudenteDettaglioEnte(studenteId, enteId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getStudenteDettaglioEnte:%s / %s", enteId, studenteId));
		}
		return report;
	}
	

}