package it.smartcommunitylab.cartella.asl.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

import it.smartcommunitylab.cartella.asl.beans.OffertaIstitutoStub;
import it.smartcommunitylab.cartella.asl.manager.ASLRolesValidator;
import it.smartcommunitylab.cartella.asl.manager.AuditManager;
import it.smartcommunitylab.cartella.asl.manager.OffertaManager;
import it.smartcommunitylab.cartella.asl.model.Offerta;
import it.smartcommunitylab.cartella.asl.model.audit.AuditEntry;
import it.smartcommunitylab.cartella.asl.model.users.ASLAuthCheck;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;

@RestController
public class OffertaController implements AslController {
	private static Log logger = LogFactory.getLog(OffertaController.class);
	
	@Autowired
	private OffertaManager offertaManager;
	@Autowired
	private ASLRolesValidator usersValidator;
	@Autowired
	private AuditManager auditManager;

	@GetMapping("/api/offerta/search")
	public @ResponseBody Page<Offerta> searchOfferta(
			@RequestParam String istitutoId,
			@RequestParam(required = false) String text,
			@RequestParam(required = false, defaultValue="0") int tipologia,
			@RequestParam(required = false) Boolean ownerIstituto,
			@RequestParam(required = false) String stato,
			Pageable pageRequest, 
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		Page<Offerta> result = offertaManager.findOfferta(istitutoId, text, tipologia, ownerIstituto, stato, pageRequest);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchOfferta:%s / %s / %s", istitutoId, text, tipologia));
		}
		return result;
	}
	
	@GetMapping("/api/offerta/{id}")
	public @ResponseBody Offerta getOfferta(
			@PathVariable Long id,
			@RequestParam String istitutoId,
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		Offerta offerta = offertaManager.getOfferta(id);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getOfferta:%s / %s", id, istitutoId));
		}
		return offerta;
	}
	
	@DeleteMapping("/api/offerta/{id}")
	public @ResponseBody Offerta deleteOfferta(
			@PathVariable Long id,
			@RequestParam String istitutoId,
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		Offerta offerta = offertaManager.deleteOfferta(id, istitutoId);
		AuditEntry audit = new AuditEntry(request.getMethod(), Offerta.class, id, user, new Object(){});
		auditManager.save(audit);			
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteOfferta:%s / %s", id, istitutoId));
		}		
		return offerta;
	}
	
	@PostMapping("/api/offerta")
	public @ResponseBody Offerta saveOfferta(
			@RequestParam String istitutoId,
			@RequestBody Offerta offerta,
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		Offerta result = offertaManager.saveOffertaIstituto(offerta, istitutoId);
		AuditEntry audit = new AuditEntry(request.getMethod(), Offerta.class, offerta.getId(), user, new Object(){});
		auditManager.save(audit);			
		if(logger.isInfoEnabled()) {
			logger.info(String.format("saveOfferta:%s / %s", result.getId(), istitutoId));
		}		
		return result;
	}

	@PostMapping("/api/offerta/ente")
	public @ResponseBody Offerta saveOffertaByEnte(
			@RequestParam String enteId,
			@RequestBody Offerta offerta,
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, enteId), 
				new ASLAuthCheck(ASLRole.REFERENTE_AZIENDA, enteId)));
		Offerta result = offertaManager.saveOffertaByEnte(offerta, enteId);
		AuditEntry audit = new AuditEntry(request.getMethod(), Offerta.class, offerta.getId(), user, new Object(){});
		auditManager.save(audit);			
		if(logger.isInfoEnabled()) {
			logger.info(String.format("saveOffertaByEnte:%s / %s", result.getId(), enteId));
		}		
		return result;
	}

	@DeleteMapping("/api/offerta/{id}/ente")
	public @ResponseBody Offerta deleteOffertaByEnte(
			@PathVariable Long id,
			@RequestParam String enteId,
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, enteId), 
				new ASLAuthCheck(ASLRole.REFERENTE_AZIENDA, enteId)));
		Offerta offerta = offertaManager.deleteOffertaByEnte(id, enteId);
		AuditEntry audit = new AuditEntry(request.getMethod(), Offerta.class, id, user, new Object(){});
		auditManager.save(audit);			
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteOffertaByEnte:%s / %s", id, enteId));
		}		
		return offerta;
	}
	
	@PostMapping("/api/offerta/{id}/istituti")
	public void associaIstitutiByEnte(
			@PathVariable Long id,
			@RequestParam String enteId,
			@RequestBody List<OffertaIstitutoStub> istituti,
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, enteId), 
				new ASLAuthCheck(ASLRole.REFERENTE_AZIENDA, enteId)));
		offertaManager.associaIstitutiByEnte(id, enteId, istituti);
		AuditEntry audit = new AuditEntry(request.getMethod(), Offerta.class, id, user, new Object(){});
		auditManager.save(audit);			
		if(logger.isInfoEnabled()) {
			logger.info(String.format("associaIstitutiByEnte:%s / %s", id, enteId));
		}		
	}

	@GetMapping("/api/offerta/{id}/ente")
	public @ResponseBody Offerta getOffertaByEnte(
			@PathVariable Long id,
			@RequestParam String enteId,
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, enteId), 
				new ASLAuthCheck(ASLRole.REFERENTE_AZIENDA, enteId)));
		Offerta offerta = offertaManager.getOffertaByEnte(id, enteId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getOffertaByEnte:%s / %s", id, enteId));
		}
		return offerta;
	}
	
	@GetMapping("/api/offerta/search/ente")
	public @ResponseBody Page<Offerta> searchOffertaByEnte(
			@RequestParam String enteId,
			@RequestParam(required = false) String text,
			@RequestParam(required = false) String stato,
			Pageable pageRequest, 
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, enteId), 
				new ASLAuthCheck(ASLRole.REFERENTE_AZIENDA, enteId)));
		Page<Offerta> result = offertaManager.findOffertaByEnte(enteId, text, stato, pageRequest);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchOffertaByEnte:%s / %s /", enteId, text));
		}
		return result;
	}

}
