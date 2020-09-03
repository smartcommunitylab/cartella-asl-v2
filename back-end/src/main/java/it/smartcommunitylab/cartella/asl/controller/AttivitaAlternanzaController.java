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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.manager.ASLRolesValidator;
import it.smartcommunitylab.cartella.asl.manager.AttivitaAlternanzaManager;
import it.smartcommunitylab.cartella.asl.manager.AuditManager;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.PresenzaGiornaliera;
import it.smartcommunitylab.cartella.asl.model.audit.AuditEntry;
import it.smartcommunitylab.cartella.asl.model.report.ReportArchiviaEsperienza;
import it.smartcommunitylab.cartella.asl.model.report.ReportAttivitaAlternanzaDettaglio;
import it.smartcommunitylab.cartella.asl.model.report.ReportAttivitaAlternanzaRicerca;
import it.smartcommunitylab.cartella.asl.model.report.ReportAttivitaAlternanzaStudenti;
import it.smartcommunitylab.cartella.asl.model.report.ReportEsperienzaRegistration;
import it.smartcommunitylab.cartella.asl.model.report.ReportPresenzaGiornalieraGruppo;
import it.smartcommunitylab.cartella.asl.model.report.ReportPresenzeAttvitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.users.ASLAuthCheck;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;

@RestController
public class AttivitaAlternanzaController implements AslController {
	private static Log logger = LogFactory.getLog(AttivitaAlternanzaController.class);
	
	@Autowired
	private AttivitaAlternanzaManager attivitaAlternanzaManager;
	@Autowired
	private ASLRolesValidator usersValidator;
	@Autowired
	private AuditManager auditManager;
	
	@GetMapping("/api/attivita/search")
	public @ResponseBody Page<ReportAttivitaAlternanzaRicerca> searchAttivitaAlternanza(
			@RequestParam String istitutoId,
			@RequestParam(required = false) String text,
			@RequestParam(required = false, defaultValue="0") int tipologia,
			@RequestParam(required = false) String stato,
			Pageable pageRequest, 
			HttpServletRequest request) throws Exception {		
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		Page<ReportAttivitaAlternanzaRicerca> result = attivitaAlternanzaManager.findAttivita(istitutoId, text, tipologia, stato, pageRequest);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchAttivitaAlternanza:%s", text));
		}
		return result;
	}
	
	@GetMapping("/api/attivita/{id}")
	public @ResponseBody ReportAttivitaAlternanzaDettaglio getAttivitaAlternanza(
			@PathVariable long id,
			@RequestParam String istitutoId,
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		AttivitaAlternanza aa = attivitaAlternanzaManager.getAttivitaAlternanza(id);
		if(aa == null) {
			throw new BadRequestException("entity not found");
		}
		ReportAttivitaAlternanzaDettaglio report = attivitaAlternanzaManager.getAttivitaAlternanzaDetails(aa);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getAttivitaAlternanza:%s / %s", id, istitutoId));
		}
		return report;
	}
	
	
	@GetMapping("/api/attivita/{id}/report/studenti")
	public @ResponseBody ReportAttivitaAlternanzaStudenti reportAttivitaAlternanzaStudenti(
			@PathVariable long id,
			@RequestParam String istitutoId,
			HttpServletRequest request) throws Exception {		
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		AttivitaAlternanza attivitaAlternanza = attivitaAlternanzaManager.getAttivitaAlternanza(id);
		if(attivitaAlternanza == null) {
			throw new BadRequestException("entity not found");
		}
		ReportAttivitaAlternanzaStudenti result = attivitaAlternanzaManager.getStudentInfo(attivitaAlternanza);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("reportAttivitaAlternanza:%s / %s", id, istitutoId));
		}
		return result;
	}
	
	@PostMapping("/api/attivita")
	public @ResponseBody AttivitaAlternanza saveAttivitaAlternanza(
			@RequestParam String istitutoId,
			@RequestBody AttivitaAlternanza attivita,
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		AttivitaAlternanza result = attivitaAlternanzaManager.saveAttivitaAlternanza(attivita, istitutoId);
		AuditEntry audit = new AuditEntry(request.getMethod(), AttivitaAlternanza.class, attivita.getId(), user, new Object(){});
		auditManager.save(audit);			
		if(logger.isInfoEnabled()) {
			logger.info(String.format("saveAttivitaAlternanza:%s / %s", result.getId(), istitutoId));
		}		
		return result;
	}
	
	@DeleteMapping("/api/attivita/{id}")
	public @ResponseBody AttivitaAlternanza deleteAttivitaAlternanza(
			@PathVariable Long id,
			@RequestParam String istitutoId,
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		AttivitaAlternanza aa = attivitaAlternanzaManager.getAttivitaAlternanza(id);
		if(aa == null) {
			throw new BadRequestException("entity not found");
		}
		attivitaAlternanzaManager.deleteAttivitaAlternanza(aa);
		AuditEntry audit = new AuditEntry(request.getMethod(), AttivitaAlternanza.class, id, user, new Object(){});
		auditManager.save(audit);			
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteAttivitaAlternanza:%s / %s", id, istitutoId));
		}		
		return aa;
	}
	
	@PostMapping("/api/attivita/{id}/studenti")
	public @ResponseBody ReportAttivitaAlternanzaDettaglio assegnaStudentiAdAttivitaAlternanza(
			@PathVariable long id,
			@RequestParam String istitutoId,
			@RequestBody List<ReportEsperienzaRegistration> listaEsperienze,
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		AttivitaAlternanza aa = attivitaAlternanzaManager.getAttivitaAlternanza(id);
		if(aa == null) {
			throw new BadRequestException("entity not found");
		}
		if(!aa.getIstitutoId().equals(istitutoId)) {
			throw new BadRequestException("istitutoId not corresponding");
		}
		attivitaAlternanzaManager.setStudentList(aa, listaEsperienze);
		ReportAttivitaAlternanzaDettaglio report = attivitaAlternanzaManager.getAttivitaAlternanzaDetails(aa);
		AuditEntry audit = new AuditEntry(request.getMethod(), AttivitaAlternanza.class, aa.getId(), user, new Object(){});
		auditManager.save(audit);			
		if(logger.isInfoEnabled()) {
			logger.info(String.format("assegnaStudentiAdAttivitaAlternanza:%s / %s", id, istitutoId));
		}
		return report;
	}
	
	@GetMapping("/api/attivita/{id}/report/esperienze")
	public @ResponseBody List<ReportArchiviaEsperienza> getReportArchiviaEsperienzeAttivitaAlternanza (
			@PathVariable long id,
			@RequestParam String istitutoId,
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		AttivitaAlternanza aa = attivitaAlternanzaManager.getAttivitaAlternanza(id);
		if(aa == null) {
			throw new BadRequestException("entity not found");
		}
		if(!aa.getIstitutoId().equals(istitutoId)) {
			throw new BadRequestException("istitutoId not corresponding");
		}
		List<ReportArchiviaEsperienza> report = attivitaAlternanzaManager.getArchiveAttivitaAlternanza(aa);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getReportArchiviaEsperienzeAttivitaAlternanza:%s / %s", id, istitutoId));
		}
		return report;
	}
	
	@PostMapping("/api/attivita/{id}/archivia")
	public @ResponseBody ReportAttivitaAlternanzaDettaglio archiviaAttivitaAlternanza (
			@PathVariable long id,
			@RequestParam String istitutoId,
			@RequestBody List<ReportArchiviaEsperienza> listaEsperienze,
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		AttivitaAlternanza aa = attivitaAlternanzaManager.getAttivitaAlternanza(id);
		if(aa == null) {
			throw new BadRequestException("entity not found");
		}
		if(!aa.getIstitutoId().equals(istitutoId)) {
			throw new BadRequestException("istitutoId not corresponding");
		}
		attivitaAlternanzaManager.archiveAttivitaAlternanza(aa, listaEsperienze);
		ReportAttivitaAlternanzaDettaglio report = attivitaAlternanzaManager.getAttivitaAlternanzaDetails(aa);
		AuditEntry audit = new AuditEntry(request.getMethod(), AttivitaAlternanza.class, aa.getId(), user, new Object(){});
		auditManager.save(audit);			
		if(logger.isInfoEnabled()) {
			logger.info(String.format("archiviaAttivitaAlternanza:%s / %s", id, istitutoId));
		}
		return report;
	}
	
	@GetMapping("/api/attivita/{id}/registrazioni")
	public @ResponseBody Page<ReportEsperienzaRegistration> searchEsperienzeRegistration(
			@PathVariable long id,
			@RequestParam String istitutoId,
			@RequestParam(required = false) String text,
			Pageable pageRequest, 
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		AttivitaAlternanza aa = attivitaAlternanzaManager.getAttivitaAlternanza(id);
		if(aa == null) {
			throw new BadRequestException("entity not found");
		}
		if(!aa.getIstitutoId().equals(istitutoId)) {
			throw new BadRequestException("istitutoId not corresponding");
		}
		Page<ReportEsperienzaRegistration> result = attivitaAlternanzaManager.findEsperienze(istitutoId, 
				aa.getAnnoScolastico(), text, pageRequest);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchEsperienzeRegistration:%s / %s / %s", id, istitutoId, text));
		}
		return result;		
	}

	@GetMapping("/api/attivita/{id}/presenze/individuale")
	public @ResponseBody List<PresenzaGiornaliera> getPresenzeAttivitaIndividuale(
			@PathVariable long id,
			@RequestParam String istitutoId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		AttivitaAlternanza aa = attivitaAlternanzaManager.getAttivitaAlternanza(id);
		if(aa == null) {
			throw new BadRequestException("entity not found");
		}
		if(!aa.getIstitutoId().equals(istitutoId)) {
			throw new BadRequestException("istitutoId not corresponding");
		}
		List<PresenzaGiornaliera> result = attivitaAlternanzaManager.getPresenzeAttivitaIndividuale(aa, dateFrom, dateTo);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getPresenzeAttivitaIndividuale:%s / %s / %s", id, istitutoId, dateFrom));
		}
		return result;
	}
	
	@PostMapping("/api/attivita/{id}/presenze/individuale")
	public List<PresenzaGiornaliera> validaPresenzeAttivitaIndividuale(
			@PathVariable long id,
			@RequestParam String istitutoId,
			@RequestBody List<PresenzaGiornaliera> presenze,
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		AttivitaAlternanza aa = attivitaAlternanzaManager.getAttivitaAlternanza(id);
		if(aa == null) {
			throw new BadRequestException("entity not found");
		}
		if(!aa.getIstitutoId().equals(istitutoId)) {
			throw new BadRequestException("istitutoId not corresponding");
		}
		List<PresenzaGiornaliera> list = attivitaAlternanzaManager.validaPresenzeAttivita(aa, presenze);
		AuditEntry audit = new AuditEntry(request.getMethod(), AttivitaAlternanza.class, aa.getId(), user, new Object(){});
		auditManager.save(audit);			
		if(logger.isInfoEnabled()) {
			logger.info(String.format("validaPresenzeAttivitaIndividuale:%s / %s", id, istitutoId));
		}
		return list;
	}
	
	@GetMapping("/api/attivita/{id}/presenze/individuale/report")
	public @ResponseBody ReportPresenzeAttvitaAlternanza getReportPresenzeAttvitaAlternanzaIndividuale(
			@PathVariable long id,
			@RequestParam String istitutoId,
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		AttivitaAlternanza aa = attivitaAlternanzaManager.getAttivitaAlternanza(id);
		if(aa == null) {
			throw new BadRequestException("entity not found");
		}
		if(!aa.getIstitutoId().equals(istitutoId)) {
			throw new BadRequestException("istitutoId not corresponding");
		}
		ReportPresenzeAttvitaAlternanza report = attivitaAlternanzaManager.getReportPresenzeAttvitaAlternanzaIndividuale(aa);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getReportPresenzeAttvitaAlternanzaIndividuale:%s / %s", id, istitutoId));
		}	
		return report;		
	}

	@GetMapping("/api/attivita/{id}/presenze/gruppo/report")
	public @ResponseBody ReportPresenzeAttvitaAlternanza getReportPresenzeAttvitaAlternanzaGruppo(
			@PathVariable long id,
			@RequestParam String istitutoId,
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		AttivitaAlternanza aa = attivitaAlternanzaManager.getAttivitaAlternanza(id);
		if(aa == null) {
			throw new BadRequestException("entity not found");
		}
		if(!aa.getIstitutoId().equals(istitutoId)) {
			throw new BadRequestException("istitutoId not corresponding");
		}
		ReportPresenzeAttvitaAlternanza report = attivitaAlternanzaManager.getReportPresenzeAttvitaAlternanzaGruppo(aa);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getReportPresenzeAttvitaAlternanzaGruppo:%s / %s", id, istitutoId));
		}	
		return report;
	}
	
	@GetMapping("/api/attivita/{id}/presenze/gruppo")
	public @ResponseBody List<ReportPresenzaGiornalieraGruppo> getPresenzeAttivitaGruppo(
			@PathVariable long id,
			@RequestParam String istitutoId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		AttivitaAlternanza aa = attivitaAlternanzaManager.getAttivitaAlternanza(id);
		if(aa == null) {
			throw new BadRequestException("entity not found");
		}
		if(!aa.getIstitutoId().equals(istitutoId)) {
			throw new BadRequestException("istitutoId not corresponding");
		}
		List<ReportPresenzaGiornalieraGruppo> reportList = attivitaAlternanzaManager.getPresenzeAttivitaGruppo(aa, dateFrom, dateTo);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getPresenzeAttivitaGruppo:%s / %s / %s / %s", id, istitutoId, dateFrom, dateTo));
		}
		return reportList;
	}

	@PostMapping("/api/attivita/{id}/presenze/gruppo")
	public List<PresenzaGiornaliera> validaPresenzeAttivitaGruppo(
			@PathVariable long id,
			@RequestParam String istitutoId,
			@RequestBody List<PresenzaGiornaliera> presenze,
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		AttivitaAlternanza aa = attivitaAlternanzaManager.getAttivitaAlternanza(id);
		if(aa == null) {
			throw new BadRequestException("entity not found");
		}
		if(!aa.getIstitutoId().equals(istitutoId)) {
			throw new BadRequestException("istitutoId not corresponding");
		}
		List<PresenzaGiornaliera> list = attivitaAlternanzaManager.validaPresenzeAttivita(aa, presenze);
		AuditEntry audit = new AuditEntry(request.getMethod(), AttivitaAlternanza.class, aa.getId(), user, new Object(){});
		auditManager.save(audit);			
		if(logger.isInfoEnabled()) {
			logger.info(String.format("validaPresenzeAttivitaGruppo:%s / %s", id, istitutoId));
		}
		return list;
	}
	
	@PostMapping("/api/attivita/offerta/{offertaId}/associa")
	public @ResponseBody AttivitaAlternanza associaOfferta(
			@PathVariable long offertaId,
			@RequestParam String istitutoId,
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		AttivitaAlternanza aa = attivitaAlternanzaManager.associaOfferta(offertaId, istitutoId);
		AuditEntry audit = new AuditEntry(request.getMethod(), AttivitaAlternanza.class, aa.getId(), user, new Object(){});
		auditManager.save(audit);			
		if(logger.isInfoEnabled()) {
			logger.info(String.format("associaOfferta:%s / %s / %s", aa.getId(), offertaId, istitutoId));
		}		
		return aa;
	}

}
