package it.smartcommunitylab.cartella.asl.controller;

import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.cartella.asl.manager.ASLRolesValidator;
import it.smartcommunitylab.cartella.asl.manager.DashboardManager;
import it.smartcommunitylab.cartella.asl.model.report.ReportDashboardAttivita;
import it.smartcommunitylab.cartella.asl.model.report.ReportDashboardEsperienza;
import it.smartcommunitylab.cartella.asl.model.report.ReportDashboardUsoSistema;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;

@RestController
public class DashboardController {
	private static Log logger = LogFactory.getLog(DashboardController.class);
	
	@Autowired
	private DashboardManager dashboardManager;
	
	@Autowired
	private ASLRolesValidator usersValidator;

	@GetMapping("/api/dashboard/sistema")
	public ReportDashboardUsoSistema getReportUtilizzoSistema (
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
	
	@GetMapping("/api/dashboard/attivita")
	public ReportDashboardAttivita getReportAttivita (
			@RequestParam String istitutoId,
			@RequestParam String annoScolastico,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,			
			HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		ReportDashboardAttivita report = dashboardManager.getReportAttivita(istitutoId, annoScolastico, dateFrom, dateTo);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getReportAttivita:%s - %s", istitutoId, annoScolastico));
		}
		return report;
	}

	@GetMapping("/api/dashboard/esperienza")
	public List<ReportDashboardEsperienza> getReportEsperienze (
			@RequestParam String istitutoId,
			@RequestParam String annoScolastico,
			@RequestParam(required=false) String text,
			HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		List<ReportDashboardEsperienza> list = dashboardManager.getReportEsperienze(istitutoId, annoScolastico, text);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getReportEsperienze:%s - %s", istitutoId, annoScolastico));
		}
		return list;
	}

}
