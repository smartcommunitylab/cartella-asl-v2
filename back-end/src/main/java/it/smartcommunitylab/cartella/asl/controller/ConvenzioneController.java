package it.smartcommunitylab.cartella.asl.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

import it.smartcommunitylab.cartella.asl.manager.ASLRolesValidator;
import it.smartcommunitylab.cartella.asl.manager.AuditManager;
import it.smartcommunitylab.cartella.asl.manager.ConvenzioneManager;
import it.smartcommunitylab.cartella.asl.model.Convenzione;
import it.smartcommunitylab.cartella.asl.model.audit.AuditEntry;
import it.smartcommunitylab.cartella.asl.model.users.ASLAuthCheck;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;

@RestController
public class ConvenzioneController implements AslController {
	private static Log logger = LogFactory.getLog(ConvenzioneController.class);
	
	@Autowired
	private ConvenzioneManager convenzioneManager;
	@Autowired
	private ASLRolesValidator usersValidator;
	@Autowired
	private AuditManager auditManager;
	
	@PostMapping("/api/convenzione")
	public @ResponseBody Convenzione saveConvenzione(
			@RequestBody Convenzione convenzione,
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(
				new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, convenzione.getIstitutoId()), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, convenzione.getIstitutoId())));
		Convenzione result = convenzioneManager.saveConvenzione(convenzione);
		AuditEntry audit = new AuditEntry(request.getMethod(), Convenzione.class, result.getId(), user, new Object(){});
		auditManager.save(audit);			
		if(logger.isInfoEnabled()) {
			logger.info(String.format("saveConvenzione:%s / %s / %s", result.getId(), result.getIstitutoId(), result.getEnteId()));
		}		
		return result;
	}
	
	@DeleteMapping("/api/convenzione/{convenzioneId}")
	public @ResponseBody Convenzione deleteConvenzione(
			@PathVariable Long convenzioneId,
			@RequestParam String istitutoId,
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(
				new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		Convenzione c = convenzioneManager.deleteConvenzione(istitutoId, convenzioneId);
		AuditEntry audit = new AuditEntry(request.getMethod(), Convenzione.class, c.getId(), user, new Object(){});
		auditManager.save(audit);			
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteConvenzione:%s / %s", istitutoId, convenzioneId));
		}		
		return c;		
	}
	
	@GetMapping("/api/convenzione/{convenzioneId}")
	public @ResponseBody Convenzione getConvenzione(
			@PathVariable Long convenzioneId,
			@RequestParam String istitutoId,
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(
				new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		Convenzione c = convenzioneManager.getConvenzione(convenzioneId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getConvenzione:%s", convenzioneId));
		}		
		return c;				
	}
	
	@GetMapping("/api/convenzione/ente/{enteId}")
	public @ResponseBody List<Convenzione> getConvenzioneEnteByIstituto(
			@PathVariable String enteId,
			@RequestParam String istitutoId,
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(
				new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		List<Convenzione> convenzioni = convenzioneManager.getConvenzioni(istitutoId, enteId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getConvenzioneEnteByIstituto:%s / %s", istitutoId, enteId));
		}		
		return convenzioni;
	}
	
	@GetMapping("/api/convenzione/ente/{enteId}/attiva")
	public @ResponseBody Convenzione getUltimaConvenzioneAttivaEnteByIstituto(
			@PathVariable String enteId,
			@RequestParam String istitutoId,
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(
				new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		Convenzione c = convenzioneManager.getUltimaConvenzione(istitutoId, enteId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getUltimaConvenzioneAttivaEnteByIstituto:%s / %s", istitutoId, enteId));
		}		
		return c;
	}


}
