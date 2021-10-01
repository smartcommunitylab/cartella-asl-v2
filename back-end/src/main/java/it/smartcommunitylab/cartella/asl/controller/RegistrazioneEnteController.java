package it.smartcommunitylab.cartella.asl.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.manager.ASLRolesValidator;
import it.smartcommunitylab.cartella.asl.manager.AuditManager;
import it.smartcommunitylab.cartella.asl.manager.RegistrazioneEnteManager;
import it.smartcommunitylab.cartella.asl.model.RegistrazioneEnte;
import it.smartcommunitylab.cartella.asl.model.audit.AuditEntry;
import it.smartcommunitylab.cartella.asl.model.report.RegistrazioneEnteReport;
import it.smartcommunitylab.cartella.asl.model.users.ASLAuthCheck;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;

@RestController
public class RegistrazioneEnteController implements AslController {
	private static Log logger = LogFactory.getLog(RegistrazioneEnteController.class);
	
	@Autowired
	private RegistrazioneEnteManager registrazioneEnteManager;
	@Autowired
	private ASLRolesValidator usersValidator;
	@Autowired
	private AuditManager auditManager;
	
	@GetMapping("/api/registrazione-ente/ruoli")
	public @ResponseBody List<RegistrazioneEnteReport> getRuoliByEnte(
			@RequestParam String enteId,
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, enteId), 
				new ASLAuthCheck(ASLRole.REFERENTE_AZIENDA, enteId)));
		List<RegistrazioneEnteReport> list = registrazioneEnteManager.getRuoliByEnte(enteId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getRuoliByEnte:%s - %s", enteId, list.size()));
		}		
		return list;
	}
	
	@PostMapping("/api/registrazione-ente/user")
	public @ResponseBody ASLUser aggiornaDatiUserAzienda(
			@RequestParam String enteId, 
			@RequestParam String nome, 
			@RequestParam String cognome, 
			@RequestParam Long userId,
			HttpServletRequest request) throws Exception {
		ASLUser caller = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.REFERENTE_AZIENDA, enteId),
				new ASLAuthCheck(ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, enteId)));
		if(!caller.getId().equals(userId)) {
			throw new BadRequestException("id utente non corrispondente");
		}
		ASLUser user = registrazioneEnteManager.aggiornaDatiUtenteAzienda(enteId, nome, cognome, userId);
		AuditEntry audit = new AuditEntry(request.getMethod(), ASLUser.class, userId, caller, new Object(){});
		auditManager.save(audit);			
		if(logger.isInfoEnabled()) {
			logger.info(String.format("aggiornaDatiUserAzienda:%s - %s", enteId, userId));
		}		
		return user;
	}
	
	@GetMapping("/registrazione-ente")
	public @ResponseBody RegistrazioneEnte getRegistrazioneByToken(
			@RequestParam String token,
			HttpServletRequest request) throws Exception {
		RegistrazioneEnte registrazioneEnte = registrazioneEnteManager.getRegistrazioneByToken(token);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getRegistrazioneByToken:%s", token));
		}		
		return registrazioneEnte;
	}
	
	@PostMapping("/registrazione-ente")
	public @ResponseBody RegistrazioneEnte confermaRichiestaRegistrazione(
			@RequestParam String token,
			HttpServletRequest request) throws Exception {
		RegistrazioneEnte registrazioneEnte = registrazioneEnteManager.confermaRichiestaRegistrazione(token);
		AuditEntry audit = new AuditEntry(request.getMethod(), RegistrazioneEnte.class, registrazioneEnte.getId(), null, new Object(){});
		auditManager.save(audit);			
		if(logger.isInfoEnabled()) {
			logger.info(String.format("confermaRichiestaRegistrazione:%s", token));
		}		
		return registrazioneEnte;
	}
	
	@PostMapping("/api/registrazione-ente/richiesta")
	public @ResponseBody RegistrazioneEnte creaRichiestaRegistrazione(
			@RequestParam String istitutoId, 
			@RequestParam String enteId,
			@RequestParam String cf,
			@RequestParam String email,
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		RegistrazioneEnte registrazioneEnte = registrazioneEnteManager.creaRichiestaRegistrazione(istitutoId, enteId, cf, email);
		AuditEntry audit = new AuditEntry(request.getMethod(), RegistrazioneEnte.class, registrazioneEnte.getId(), user, new Object(){});
		auditManager.save(audit);			
		if(logger.isInfoEnabled()) {
			logger.info(String.format("creaRichiestaRegistrazione:%s - %s - %s", istitutoId, enteId, email));
		}		
		return registrazioneEnte;		
	}
	
	@DeleteMapping("/api/registrazione-ente/richiesta")
	public @ResponseBody RegistrazioneEnte annullaRichiestaRegistrazione(
			@RequestParam String istitutoId, 
			@RequestParam Long registrazioneId,
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		RegistrazioneEnte registrazioneEnte = registrazioneEnteManager.annullaRichiestaRegistrazione(istitutoId, registrazioneId);
		AuditEntry audit = new AuditEntry(request.getMethod(), RegistrazioneEnte.class, registrazioneEnte.getId(), user, new Object(){});
		auditManager.save(audit);			
		if(logger.isInfoEnabled()) {
			logger.info(String.format("annullaRichiestaRegistrazione:%s - %s", istitutoId, registrazioneId));
		}		
		return registrazioneEnte;		
	}
	
	@PostMapping("/api/registrazione-ente/ref-azienda")
	public @ResponseBody RegistrazioneEnte aggiungiRuoloReferenteAzienda(
			@RequestParam String enteId, 
			@RequestParam String nome, 
			@RequestParam String cognome, 
			@RequestParam String email, 
			@RequestParam String cf, 
			@RequestParam Long ownerId,
			HttpServletRequest request) throws Exception {
		ASLUser user =  usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.REFERENTE_AZIENDA, enteId), 
				new ASLAuthCheck(ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, enteId)));
		RegistrazioneEnte registrazioneEnte = registrazioneEnteManager.aggiungiRuoloReferenteAzienda(enteId, nome, cognome, email, cf, ownerId);
		AuditEntry audit = new AuditEntry(request.getMethod(), RegistrazioneEnte.class, registrazioneEnte.getId(), user, new Object(){});
		auditManager.save(audit);			
		if(logger.isInfoEnabled()) {
			logger.info(String.format("aggiungiRuoloReferenteAzienda:%s - %s - %s", enteId, email, ownerId));
		}		
		return registrazioneEnte;		
	}

	@DeleteMapping("/api/registrazione-ente/ref-azienda")
	public @ResponseBody RegistrazioneEnte cancellaRuoloReferenteAzienda(
			@RequestParam String enteId,
			@RequestParam Long registrazioneId,
			HttpServletRequest request) throws Exception {
		ASLUser user =  usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.REFERENTE_AZIENDA, enteId), 
				new ASLAuthCheck(ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, enteId)));
		RegistrazioneEnte registrazioneEnte = registrazioneEnteManager.cancellaRuoloReferenteAzienda(registrazioneId);
		AuditEntry audit = new AuditEntry(request.getMethod(), RegistrazioneEnte.class, registrazioneEnte.getId(), user, new Object(){});
		auditManager.save(audit);			
		if(logger.isInfoEnabled()) {
			logger.info(String.format("cancellaRuoloReferenteAzienda:%s - %s", enteId, registrazioneId));
		}		
		return registrazioneEnte;		
	}
		

}
