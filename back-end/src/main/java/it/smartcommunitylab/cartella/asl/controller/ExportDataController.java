package it.smartcommunitylab.cartella.asl.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

import it.smartcommunitylab.cartella.asl.manager.ASLRolesValidator;
import it.smartcommunitylab.cartella.asl.manager.ExportDataManager;
import it.smartcommunitylab.cartella.asl.model.export.ExportCsv;
import it.smartcommunitylab.cartella.asl.model.users.ASLAuthCheck;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;

@RestController
public class ExportDataController implements AslController {
	private static Log logger = LogFactory.getLog(ExportDataController.class);
	
	@Autowired
	private ExportDataManager exportDataManager;
	@Autowired
	private ASLRolesValidator usersValidator;

	@GetMapping("/api/export/csv/studente")
	public void getStudenteAttivitaReportCsv(
			@RequestParam String istitutoId,
			@RequestParam String studenteId,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		ExportCsv reportCsv = exportDataManager.getStudenteAttivitaReportCsv(istitutoId, studenteId);
		downloadCsv(reportCsv, response);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getStudenteAttivitaReportCsv:%s / %s", istitutoId, studenteId));
		}
	}
	
	@GetMapping("/api/export/csv/classe")
	public void getClasseAttivitaReportCsv(
			@RequestParam String istitutoId,
			@RequestParam String annoScolastico,
			@RequestParam String corsoId,
			@RequestParam String corso,
			@RequestParam String classe,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		ExportCsv reportCsv = exportDataManager.getClasseAttivitaReportCsv(istitutoId, annoScolastico,
				corsoId, corso, classe);
		downloadCsv(reportCsv, response);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getClasseAttivitaReportCsv:%s / %s", istitutoId, classe));
		}
	}
	
	@GetMapping("/api/export/csv/dashboard/esperienze")
	public void getDashboardReportEsperienze(
			@RequestParam(required=false) String istitutoId,
			@RequestParam(required=false) String annoScolastico,
			@RequestParam(required=false) String text,
			@RequestParam(required=false) boolean getErrors,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		ExportCsv reportCsv = exportDataManager.getDashboardEsperienze(istitutoId, annoScolastico, text, getErrors);
		downloadCsv(reportCsv, response);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getDashboardReportEsperienze:%s / %s", istitutoId, annoScolastico));
		}
	}
	
	private void downloadCsv(ExportCsv reportCsv, HttpServletResponse response) throws IOException {
		response.setContentType(reportCsv.getContentType());
		response.setHeader("Content-Disposition", "attachment; filename=" + reportCsv.getFilename());
		response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
		response.getOutputStream().write(reportCsv.getContent().toString().getBytes(StandardCharsets.UTF_8));
	}
}
