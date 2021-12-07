package it.smartcommunitylab.cartella.asl.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

import it.smartcommunitylab.cartella.asl.manager.ASLRolesValidator;
import it.smartcommunitylab.cartella.asl.manager.AuditManager;
import it.smartcommunitylab.cartella.asl.manager.ValutazioniManager;
import it.smartcommunitylab.cartella.asl.model.ValutazioneAttivita;
import it.smartcommunitylab.cartella.asl.model.ValutazioneCompetenza;
import it.smartcommunitylab.cartella.asl.model.audit.AuditEntry;
import it.smartcommunitylab.cartella.asl.model.report.ValutazioneAttivitaReport;
import it.smartcommunitylab.cartella.asl.model.report.ValutazioneCompetenzeReport;
import it.smartcommunitylab.cartella.asl.model.users.ASLAuthCheck;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;

@RestController
public class ValutazioniController implements AslController {
	private static Log logger = LogFactory.getLog(ValutazioniController.class);
	
	@Autowired
	private ASLRolesValidator usersValidator;
	@Autowired
	private AuditManager auditManager;
	@Autowired
	private ValutazioniManager valutazioniManager;
	
	@GetMapping("/api/valutazione/attivita/istituto")
	public ValutazioneAttivitaReport getValutazioneAttivitaReportByIstituto(
			@RequestParam String istitutoId,
			@RequestParam Long esperienzaSvoltaId,
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(
				new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId),
				new ASLAuthCheck(ASLRole.TUTOR_SCOLASTICO, istitutoId)));
		ValutazioneAttivitaReport report = valutazioniManager.getValutazioneAttivitaReportByIstituto(esperienzaSvoltaId, istitutoId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getValutazioneAttivitaReportByIstituto:%s - %s", istitutoId, esperienzaSvoltaId));
		}
		return report;
	}
	
	@GetMapping("/api/valutazione/attivita/studente")
	public ValutazioneAttivitaReport getValutazioneAttivitaReportByStudente(
			@RequestParam String studenteId,
			@RequestParam Long esperienzaSvoltaId,
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(
				new ASLAuthCheck(ASLRole.STUDENTE, studenteId)));
		ValutazioneAttivitaReport report = valutazioniManager.getValutazioneAttivitaReportByStudente(esperienzaSvoltaId, studenteId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getValutazioneAttivitaReportByStudente:%s - %s", studenteId, esperienzaSvoltaId));
		}
		return report;
	}
	
	@PostMapping("/api/valutazione/attivita/studente")
	public ValutazioneAttivitaReport saveValutazioneAttivita(
			@RequestParam String studenteId,
			@RequestParam Long esperienzaSvoltaId,
			@RequestBody List<ValutazioneAttivita> valutazioni,
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(
				new ASLAuthCheck(ASLRole.STUDENTE, studenteId)));
		ValutazioneAttivitaReport report = valutazioniManager.saveValutazioneAttivita(studenteId, esperienzaSvoltaId, valutazioni);
		AuditEntry audit = new AuditEntry(request.getMethod(), ValutazioneAttivita.class, esperienzaSvoltaId, user, new Object(){});
		auditManager.save(audit);					
		if(logger.isInfoEnabled()) {
			logger.info(String.format("saveValutazioneAttivita:%s - %s", studenteId, esperienzaSvoltaId));
		}
		return report;
	}

	@GetMapping("/api/valutazione/competenze/istituto")
	public ValutazioneCompetenzeReport getValutazioneCompetenzeReportByIstituto(
			@RequestParam String istitutoId,
			@RequestParam Long esperienzaSvoltaId,
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(
				new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId),
				new ASLAuthCheck(ASLRole.TUTOR_SCOLASTICO, istitutoId)));
		ValutazioneCompetenzeReport report = valutazioniManager.getValutazioneCompetenzeReportByIstituto(esperienzaSvoltaId, istitutoId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getValutazioneCompetenzeReportByIstituto:%s - %s", istitutoId, esperienzaSvoltaId));
		}
		return report;
	}

	@GetMapping("/api/valutazione/competenze/studente")
	public ValutazioneCompetenzeReport getValutazioneCompetenzeReportByStudente(
			@RequestParam String studenteId,
			@RequestParam Long esperienzaSvoltaId,
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(
				new ASLAuthCheck(ASLRole.STUDENTE, studenteId)));
		ValutazioneCompetenzeReport report = valutazioniManager.getValutazioneCompetenzeReportByStudente(esperienzaSvoltaId, studenteId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getValutazioneCompetenzeReportByStudente:%s - %s", studenteId, esperienzaSvoltaId));
		}
		return report;
	}
	
	@GetMapping("/api/valutazione/competenze/ente")
	public ValutazioneCompetenzeReport getValutazioneCompetenzeReportByEnte(
			@RequestParam String enteId,
			@RequestParam Long esperienzaSvoltaId,
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(
				new ASLAuthCheck(ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, enteId),
				new ASLAuthCheck(ASLRole.REFERENTE_AZIENDA, enteId)));
		ValutazioneCompetenzeReport report = valutazioniManager.getValutazioneCompetenzeReportByEnte(esperienzaSvoltaId, enteId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getValutazioneCompetenzeReportByEnte:%s - %s", enteId, esperienzaSvoltaId));
		}
		return report;
	}
	
	@PostMapping("/api/valutazione/competenze/ente")
	public ValutazioneCompetenzeReport saveValutazioneCompetenze(
			@RequestParam String enteId,
			@RequestParam Long esperienzaSvoltaId,
			@RequestBody List<ValutazioneCompetenza> valutazioni,
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(
				new ASLAuthCheck(ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, enteId),
				new ASLAuthCheck(ASLRole.REFERENTE_AZIENDA, enteId)));
		ValutazioneCompetenzeReport report = valutazioniManager.saveValutazioneCompetenze(enteId, esperienzaSvoltaId, valutazioni);
		AuditEntry audit = new AuditEntry(request.getMethod(), ValutazioneCompetenza.class, esperienzaSvoltaId, user, new Object(){});
		auditManager.save(audit);					
		if(logger.isInfoEnabled()) {
			logger.info(String.format("saveValutazioneCompetenze:%s - %s", enteId, esperienzaSvoltaId));
		}
		return report;
	}			

}
