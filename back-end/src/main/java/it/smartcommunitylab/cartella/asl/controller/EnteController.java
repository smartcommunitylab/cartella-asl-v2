package it.smartcommunitylab.cartella.asl.controller;

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
import com.google.common.collect.Sets;

import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.manager.ASLRolesValidator;
import it.smartcommunitylab.cartella.asl.manager.AuditManager;
import it.smartcommunitylab.cartella.asl.manager.AziendaManager;
import it.smartcommunitylab.cartella.asl.model.Azienda;
import it.smartcommunitylab.cartella.asl.model.audit.AuditEntry;
import it.smartcommunitylab.cartella.asl.model.report.ReportUtilizzoAzienda;
import it.smartcommunitylab.cartella.asl.model.users.ASLAuthCheck;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;

@RestController
public class EnteController implements AslController {
	private static Log logger = LogFactory.getLog(EnteController.class);
	
	@Autowired
	private AziendaManager aziendaManager;
	@Autowired
	private ASLRolesValidator usersValidator;
	@Autowired
	private AuditManager auditManager;	
	//@Autowired
	//private PARIXService parixService;

	@GetMapping("/api/azienda/search")
	public @ResponseBody Page<Azienda> searchAziende(
			@RequestParam(required = false) String text,
			@RequestParam(required = false) String istitutoId,
			Pageable pageRequest, 
			HttpServletRequest request) throws Exception {		
		usersValidator.checkRoles(request,  Sets.newHashSet(ASLRole.DIRIGENTE_SCOLASTICO, ASLRole.FUNZIONE_STRUMENTALE));
		Page<Azienda> result = aziendaManager.findAziende(text, istitutoId, pageRequest);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchAziende:%s", text));
		}
		return result;
	}
	
	@GetMapping("/api/azienda/{id}")
	public @ResponseBody Azienda getAzienda(
			@PathVariable String id,
			HttpServletRequest request) throws Exception {
		usersValidator.checkRoles(request,  Sets.newHashSet(ASLRole.DIRIGENTE_SCOLASTICO, ASLRole.FUNZIONE_STRUMENTALE));
		Azienda azienda = aziendaManager.getAzienda(id);
		if(azienda == null) {
			throw new BadRequestException("entity not found");
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getAzienda:%s", id));
		}		
		return azienda;
	}
	
	@PostMapping("/api/azienda")
	public @ResponseBody Azienda saveAzienda(
			@RequestBody Azienda azienda,
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.checkRoles(request,  Sets.newHashSet(ASLRole.DIRIGENTE_SCOLASTICO, ASLRole.FUNZIONE_STRUMENTALE));
		Azienda result = aziendaManager.saveAzienda(azienda);
		AuditEntry audit = new AuditEntry(request.getMethod(), Azienda.class, azienda.getId(), user, new Object(){});
		auditManager.save(audit);			
		if(logger.isInfoEnabled()) {
			logger.info(String.format("saveAzienda:%s", result.getId()));
		}		
		return result;
	}
	
	@GetMapping("/api/azienda/{id}/used")
	public @ResponseBody ReportUtilizzoAzienda isAziendaUsed(
			@PathVariable String id,
			HttpServletRequest request) throws Exception {
		usersValidator.checkRoles(request,  Sets.newHashSet(ASLRole.DIRIGENTE_SCOLASTICO, ASLRole.FUNZIONE_STRUMENTALE));
		Azienda azienda = aziendaManager.getAzienda(id);
		if(azienda == null) {
			throw new BadRequestException("entity not found");
		}
		ReportUtilizzoAzienda report = aziendaManager.getReportUtilizzo(id);
		return report;
	}
	
	@DeleteMapping("/api/azienda/{id}")
	public @ResponseBody Azienda deleteAzienda(
			@PathVariable String id,
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.checkRoles(request,  Sets.newHashSet(ASLRole.DIRIGENTE_SCOLASTICO, ASLRole.FUNZIONE_STRUMENTALE));
		Azienda azienda = aziendaManager.deleteAzienda(id);
		AuditEntry audit = new AuditEntry(request.getMethod(), Azienda.class, id, user, new Object(){});
		auditManager.save(audit);			
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteAzienda:%s", id));
		}		
		return azienda;
	}
	
	@GetMapping("/api/azienda/{enteId}/ente")
	public @ResponseBody Azienda getAziendaByEnte(
			@PathVariable String enteId,
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, enteId), 
				new ASLAuthCheck(ASLRole.REFERENTE_AZIENDA, enteId)));
		Azienda azienda = aziendaManager.getAzienda(enteId);
		if(azienda == null) {
			throw new BadRequestException("entity not found");
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getAziendaByEnte:%s", enteId));
		}		
		return azienda;
	}

	@PostMapping("/api/azienda/ente")
	public @ResponseBody Azienda saveAziendaByEnte(
			@RequestBody Azienda azienda,
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, azienda.getId()), 
				new ASLAuthCheck(ASLRole.REFERENTE_AZIENDA, azienda.getId())));
		Azienda result = aziendaManager.updateAziendaByEnte(azienda);
		AuditEntry audit = new AuditEntry(request.getMethod(), Azienda.class, azienda.getId(), user, new Object(){});
		auditManager.save(audit);			
		if(logger.isInfoEnabled()) {
			logger.info(String.format("saveAziendaByEnte:%s", azienda.getId()));
		}		
		return result;
	}
	
	/*@GetMapping("/api/azienda/parix")
	public @ResponseBody DettaglioImpresa getDettagliAziendaParix(
			@RequestParam String cf,
			HttpServletRequest request) throws Exception {
		return parixService.getDettaglioImpresa(cf);
	}*/	

}
