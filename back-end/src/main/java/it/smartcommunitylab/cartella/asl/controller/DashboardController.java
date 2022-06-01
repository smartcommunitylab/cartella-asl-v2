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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.manager.ASLRolesValidator;
import it.smartcommunitylab.cartella.asl.manager.AttivitaAlternanzaManager;
import it.smartcommunitylab.cartella.asl.manager.AuditManager;
import it.smartcommunitylab.cartella.asl.manager.AziendaManager;
import it.smartcommunitylab.cartella.asl.manager.DashboardManager;
import it.smartcommunitylab.cartella.asl.manager.IstituzioneManager;
import it.smartcommunitylab.cartella.asl.manager.RegistrazioneEnteManager;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.Azienda;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;
import it.smartcommunitylab.cartella.asl.model.Istituzione;
import it.smartcommunitylab.cartella.asl.model.RegistrazioneEnte;
import it.smartcommunitylab.cartella.asl.model.audit.AuditEntry;
import it.smartcommunitylab.cartella.asl.model.report.ReportAttivitaAlternanzaDettaglio;
import it.smartcommunitylab.cartella.asl.model.report.ReportDashboardAttivita;
import it.smartcommunitylab.cartella.asl.model.report.ReportDashboardDettaglioAttivita;
import it.smartcommunitylab.cartella.asl.model.report.ReportDashboardEsperienza;
import it.smartcommunitylab.cartella.asl.model.report.ReportDashboardRegistrazione;
import it.smartcommunitylab.cartella.asl.model.report.ReportDashboardUsoSistema;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;

@RestController
public class DashboardController {
	private static Log logger = LogFactory.getLog(DashboardController.class);
	
	@Autowired
	private DashboardManager dashboardManager;
	@Autowired
	private ASLRolesValidator usersValidator;
	@Autowired
	private AuditManager auditManager;
	@Autowired
	private RegistrazioneEnteManager registrazioneEnteManager;
	@Autowired
	private AziendaManager aziendaManager;
	@Autowired
	private AttivitaAlternanzaManager attivitaAlternanzaManager;
	@Autowired
	private IstituzioneManager istituzioneManager;

	@GetMapping("/api/dashboard/sistema")
	public @ResponseBody ReportDashboardUsoSistema getReportUtilizzoSistema (
			@RequestParam String istitutoId,
			@RequestParam String annoScolastico,
			HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		ReportDashboardUsoSistema report = dashboardManager.getReportUtilizzoSistema(istitutoId, annoScolastico);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getReportUtilizzoSistema:%s - %s", istitutoId, annoScolastico));
		}
		return report;
	}
	
	@GetMapping("/api/dashboard/attivita/report")
	public @ResponseBody ReportDashboardAttivita getReportAttivita (
			@RequestParam String istitutoId,
			@RequestParam String annoScolastico,
			@RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
			@RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,			
			HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		ReportDashboardAttivita report = dashboardManager.getReportAttivita(istitutoId, annoScolastico, dateFrom, dateTo);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getReportAttivita:%s - %s", istitutoId, annoScolastico));
		}
		return report;
	}

	@GetMapping("/api/dashboard/attivita")
	public @ResponseBody List<ReportDashboardDettaglioAttivita> getAttivita (
			@RequestParam String istitutoId,
			@RequestParam String annoScolastico,
			@RequestParam(required=false) String text,
			HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		List<ReportDashboardDettaglioAttivita> report = dashboardManager.getReportDettaglioAttivita(istitutoId, annoScolastico, text);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getAttivita:%s - %s", istitutoId, annoScolastico));
		}
		return report;
	}
	
	@GetMapping("/api/dashboard/esperienze")
	public @ResponseBody List<ReportDashboardEsperienza> getReportEsperienze (
			@RequestParam(required=false) String istitutoId,
			@RequestParam(required=false) String annoScolastico,
			@RequestParam(required=false) String text,
			@RequestParam(required=false) String stato,
			@RequestParam(required=false) boolean getErrors,
			HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		List<ReportDashboardEsperienza> list = dashboardManager.getReportEsperienze(istitutoId, annoScolastico, text, stato, getErrors);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getReportEsperienze:%s - %s", istitutoId, annoScolastico));
		}
		return list;
	}
	
	@GetMapping("/api/dashboard/registrazioni")
	public @ResponseBody List<ReportDashboardRegistrazione> getReportRegistrazioni (
			@RequestParam String istitutoId,
			@RequestParam String cf,
			HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		List<ReportDashboardRegistrazione> list = dashboardManager.getReportRegistrazioni(istitutoId, cf);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getReportRegistrazioni:%s - %s", istitutoId, cf));
		}
		return list;		
	}
	
	@DeleteMapping("/api/dashboard/attivita")
	public @ResponseBody AttivitaAlternanza deleteAttivita(
			@RequestParam Long attivitaId,
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.checkRole(request, ASLRole.ADMIN);
		AttivitaAlternanza aa = dashboardManager.deleteAttivita(attivitaId);
		AuditEntry audit = new AuditEntry(request.getMethod(), AttivitaAlternanza.class, attivitaId, user, new Object(){});
		auditManager.save(audit);			
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteAttivita:%s", attivitaId));
		}	
		return aa;
	}
	
	@DeleteMapping("/api/dashboard/esperienza")
	public @ResponseBody EsperienzaSvolta deleteEsperienza(
			@RequestParam Long esperienzaId,
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.checkRole(request, ASLRole.ADMIN);
		EsperienzaSvolta esperienza = dashboardManager.deleteEsperienza(esperienzaId);
		AuditEntry audit = new AuditEntry(request.getMethod(), EsperienzaSvolta.class, esperienzaId, user, new Object(){});
		auditManager.save(audit);			
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteEsperienza:%s", esperienzaId));
		}	
		return esperienza;
	}
	
	@GetMapping("/api/dashboard/attivita/attiva")
	public @ResponseBody AttivitaAlternanza activateAttivita(
			@RequestParam Long attivitaId,
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.checkRole(request, ASLRole.ADMIN);
		AttivitaAlternanza aa = dashboardManager.activateAttivita(attivitaId);
		AuditEntry audit = new AuditEntry(request.getMethod(), AttivitaAlternanza.class, attivitaId, user, new Object(){});
		auditManager.save(audit);			
		if(logger.isInfoEnabled()) {
			logger.info(String.format("activateAttivita:%s", attivitaId));
		}	
		return aa;
	}
	
	@GetMapping("/api/dashboard/registrazione-ente")
	public @ResponseBody List<RegistrazioneEnte> getRuoliByEnte(
			@RequestParam String enteId,
			HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		List<RegistrazioneEnte> list = registrazioneEnteManager.getRuoliByEnte(enteId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getRuoliByEnte:%s - %s", enteId, list.size()));
		}		
		return list;
	}

	@DeleteMapping("/api/dashboard/registrazione-ente")
	public @ResponseBody RegistrazioneEnte cancellaRuoloByEnte(
			@RequestParam Long registrazioneId,
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.checkRole(request, ASLRole.ADMIN);
		RegistrazioneEnte registrazioneEnte = registrazioneEnteManager.cancellaRuolo(registrazioneId);
		AuditEntry audit = new AuditEntry(request.getMethod(), RegistrazioneEnte.class, registrazioneEnte.getId(), user, new Object(){});
		auditManager.save(audit);			
		if(logger.isInfoEnabled()) {
			logger.info(String.format("cancellaRuoloByEnte:%s", registrazioneId));
		}		
		return registrazioneEnte;		
	}
	
	@GetMapping("/api/dashboard/enti")
	public @ResponseBody Page<Azienda> searchEnti(
			@RequestParam String text,
			Pageable pageRequest,
			HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		Page<Azienda> page = aziendaManager.findAziende(text, null, pageRequest);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchEnti:%s", text));
		}		
		return page;
	}
	
	@GetMapping("/api/dashboard/istituti")
	public @ResponseBody Page<Istituzione> searchIstituti(
			@RequestParam String text,
			Pageable pageRequest,
			HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		Page<Istituzione> page = istituzioneManager.findIstituti(text, pageRequest);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchIstituti:%s", text));
		}		
		return page;
	}
	
	@GetMapping("/api/dashboard/attivita/detail")
	public @ResponseBody ReportAttivitaAlternanzaDettaglio getAttivitaAlternanza(
			@RequestParam Long attivitaId,
			HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		AttivitaAlternanza aa = attivitaAlternanzaManager.getAttivitaAlternanza(attivitaId);
		if(aa == null) {
			throw new BadRequestException("entity not found");
		}
		ReportAttivitaAlternanzaDettaglio report = attivitaAlternanzaManager.getAttivitaAlternanzaDetails(aa, null);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getAttivitaAlternanza:%s", attivitaId));
		}
		return report;
	}
	

}
