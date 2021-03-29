package it.smartcommunitylab.cartella.asl.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

import it.smartcommunitylab.cartella.asl.manager.ASLRolesValidator;
import it.smartcommunitylab.cartella.asl.manager.DashboardIstitutoManager;
import it.smartcommunitylab.cartella.asl.model.report.ReportDashboardIstituto;
import it.smartcommunitylab.cartella.asl.model.users.ASLAuthCheck;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;

@RestController
public class DashboardIstitutoController {
	private static Log logger = LogFactory.getLog(DashboardIstitutoController.class);
	
	@Autowired
	private DashboardIstitutoManager dashboardManager;
	@Autowired
	private ASLRolesValidator usersValidator;

	@GetMapping("/api/dashboard-ist/sistema")
	public @ResponseBody ReportDashboardIstituto getReportUtilizzoIstituto (
			@RequestParam String istitutoId,
			@RequestParam String annoScolastico,
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		ReportDashboardIstituto report = dashboardManager.getReportUtilizzoIstituto(istitutoId, annoScolastico);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getReportUtilizzoIstituto:%s - %s", istitutoId, annoScolastico));
		}
		return report;
	}
		
	@GetMapping("/api/dashboard-ist/classe")
	public @ResponseBody ReportDashboardIstituto getReportUtilizzoClasse (
			@RequestParam String istitutoId,
			@RequestParam String annoScolastico,
			@RequestParam String classe,
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		ReportDashboardIstituto report = dashboardManager.getReportUtilizzoClasse(istitutoId, annoScolastico, classe);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getReportUtilizzoClasse:%s - %s - %s", istitutoId, annoScolastico, classe));
		}
		return report;
	}

}
