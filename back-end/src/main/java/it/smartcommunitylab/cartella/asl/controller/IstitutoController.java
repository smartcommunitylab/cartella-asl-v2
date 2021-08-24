package it.smartcommunitylab.cartella.asl.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.manager.ASLRolesValidator;
import it.smartcommunitylab.cartella.asl.manager.AuditManager;
import it.smartcommunitylab.cartella.asl.manager.IstituzioneManager;
import it.smartcommunitylab.cartella.asl.model.Istituzione;
import it.smartcommunitylab.cartella.asl.model.audit.AuditEntry;
import it.smartcommunitylab.cartella.asl.model.report.ReportIstitutoEnte;
import it.smartcommunitylab.cartella.asl.model.users.ASLAuthCheck;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;
import it.smartcommunitylab.cartella.asl.util.ErrorLabelManager;
import it.smartcommunitylab.cartella.asl.util.Utils;

@RestController
public class IstitutoController implements AslController {

	@Autowired
	private IstituzioneManager istituzioneManager;
	@Autowired
	private ASLRolesValidator usersValidator;		
	@Autowired
	private ErrorLabelManager errorLabelManager;
	@Autowired
	private AuditManager auditManager;		
	
	private static Log logger = LogFactory.getLog(IstitutoController.class);
	
	ObjectMapper mapper = new ObjectMapper();
	
	@PutMapping("/api/istituto/{istitutoId}/sogliaOraria/{hoursThreshold}")
	public @ResponseBody void updateHoursThreshold(@PathVariable String istitutoId, @PathVariable Double hoursThreshold, HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(
				new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		
		if (hoursThreshold < 0 || hoursThreshold > 1) {
			throw new BadRequestException(errorLabelManager.get("hoursThreshold.outofrange"));
		}
		
		istituzioneManager.updateIstituzioneHoursThreshold(istitutoId, hoursThreshold);
		
		AuditEntry audit = new AuditEntry(request.getMethod(), Istituzione.class, istitutoId, user, new Object(){});
		auditManager.save(audit);			
	}
	
	@GetMapping(value = "/api/schoolYear/{istitutoId}")
	public Object getSchoolYear(@PathVariable String istitutoId,
			@RequestParam(required = false) Long dateFrom, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String schoolYear = null;
		ASLUser user = usersValidator.validate(request,
				Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId),
						new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId),
						new ASLAuthCheck(ASLRole.TUTOR_SCOLASTICO, istitutoId)));

		if (user != null) {
			if (dateFrom != null) {
				schoolYear = Utils.annoScolastico(dateFrom);
			} else {
				schoolYear = Utils.annoScolastico(new java.util.Date());	
			}
			
		}

		return "{\"schoolYear\":  \"" + schoolYear + "\"}";

	}

	@GetMapping("/api/istituto/{istitutoId}")
	public @ResponseBody Istituzione getIstitutoProfile(
			@PathVariable String istitutoId, 
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(
				new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId),
				new ASLAuthCheck(ASLRole.TUTOR_SCOLASTICO, istitutoId)));
		Istituzione istituto = istituzioneManager.getIstituto(istitutoId, null);
		if(istituto == null) {
			throw new BadRequestException("entity not found");
		}
		if (logger.isInfoEnabled()) {
			logger.info(String.format("/profile/istituto/{%s}", istitutoId));
		}
		return istituto;
	}
	
	@GetMapping(value = "/api/istituto/search")
	public @ResponseBody Page<Istituzione> searchIstituti(
			@RequestParam String istitutoId,
			@RequestParam String text, 
			Pageable pageRequest,
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(
				new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId),
				new ASLAuthCheck(ASLRole.TUTOR_SCOLASTICO, istitutoId)));
		Page<Istituzione> result = istituzioneManager.findIstituti(text, pageRequest);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("searchIstituti: %s", text));
		}		
		return result;
	}
	
	@PutMapping("/api/istituto")
	public @ResponseBody Istituzione updateIstituto(
			@RequestBody Istituzione istituto,
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(
				new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istituto.getId()), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istituto.getId())));
		
		if (logger.isInfoEnabled()) {
			logger.info(String.format("updateIstituto:%s", istituto.getId()));
		}		
		Istituzione result = istituzioneManager.updateIstituto(istituto);
		AuditEntry audit = new AuditEntry(request.getMethod(), Istituzione.class, istituto.getId(), user, new Object(){});
		auditManager.save(audit);			
		return result;
	}
	
	@GetMapping(value = "/api/istituto/search/ente")
	public @ResponseBody Page<ReportIstitutoEnte> searchIstitutiByEnte(
			@RequestParam String enteId,
			@RequestParam(required = false) String text,
			Pageable pageRequest, 
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(
				new ASLAuthCheck(ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, enteId), 
				new ASLAuthCheck(ASLRole.REFERENTE_AZIENDA, enteId)));
		Page<ReportIstitutoEnte> result = istituzioneManager.findIstitutiByEnte(enteId, text, pageRequest);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("searchIstitutiByEnte: %s - %s", enteId, text));
		}		
		return result;		
	}
	
	@GetMapping("/api/istituto/{istitutoId}/ente")
	public @ResponseBody Istituzione getIstitutoByEnte(
			@PathVariable String istitutoId, 
			@RequestParam String enteId,
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(
				new ASLAuthCheck(ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, enteId), 
				new ASLAuthCheck(ASLRole.REFERENTE_AZIENDA, enteId)));
		Istituzione istituto = istituzioneManager.getIstituto(istitutoId, enteId);
		if(istituto == null) {
			throw new BadRequestException("entity not found");
		}
		if (logger.isInfoEnabled()) {
			logger.info(String.format("getIstitutoByEnte: %s / %s", istitutoId, enteId));
		}
		return istituto;
	}
	
	
}
