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

import it.smartcommunitylab.cartella.asl.manager.ExportDataManager;
import it.smartcommunitylab.cartella.asl.model.export.ExportCsv;

@RestController
public class ExportDataController implements AslController {
	private static Log logger = LogFactory.getLog(ExportDataController.class);
	
	@Autowired
	private ExportDataManager exportDataManager;
	
	@GetMapping("/api/export/csv/studente")
	public void getStudenteAttivitaReportCsv(
			@RequestParam String istitutoId,
			@RequestParam String studenteId,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
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
		ExportCsv reportCsv = exportDataManager.getClasseAttivitaReportCsv(istitutoId, annoScolastico,
				corsoId, corso, classe);
		downloadCsv(reportCsv, response);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getClasseAttivitaReportCsv:%s / %s", istitutoId, classe));
		}
	}
	
	private void downloadCsv(ExportCsv reportCsv, HttpServletResponse response) throws IOException {
		response.setContentType(reportCsv.getContentType());
		response.setHeader("Content-Disposition", "attachment; filename=\"" + reportCsv.getFilename() + "\"");
		response.getOutputStream().write(reportCsv.getContent().toString().getBytes(StandardCharsets.UTF_8));
	}
}
