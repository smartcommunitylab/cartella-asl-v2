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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

import it.smartcommunitylab.cartella.asl.manager.ASLRolesValidator;
import it.smartcommunitylab.cartella.asl.manager.AuditManager;
import it.smartcommunitylab.cartella.asl.manager.CompetenzaManager;
import it.smartcommunitylab.cartella.asl.model.Competenza;
import it.smartcommunitylab.cartella.asl.model.PianoAlternanza;
import it.smartcommunitylab.cartella.asl.model.audit.AuditEntry;
import it.smartcommunitylab.cartella.asl.model.report.ReportCompetenzaDettaglio;
import it.smartcommunitylab.cartella.asl.model.users.ASLAuthCheck;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;
import it.smartcommunitylab.cartella.asl.util.Constants;
import it.smartcommunitylab.cartella.asl.util.Utils;

@RestController
public class CompetenzaController implements AslController {

	@Autowired
	private CompetenzaManager competenzaManager;
	@Autowired
	private ASLRolesValidator usersValidator;
	@Autowired
	private AuditManager auditManager;

	private static Log logger = LogFactory.getLog(CompetenzaController.class);

	@GetMapping("/api/competenza/{id}")
	public ReportCompetenzaDettaglio getCompetenza(@PathVariable long id, HttpServletRequest request) throws Exception {
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getCompetenza(%s", id + ")"));
		}

		return competenzaManager.getCompetenzaReport(id);
	}

	@GetMapping("/api/competenze")
	public Page<Competenza> getCompetenze(@RequestParam(required = false) String filterText,
			@RequestParam(required = false) String istitutoId, Pageable pageRequest) throws Exception {
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getCompetenze(%s", istitutoId + ")"));
		}
		
		return competenzaManager.findCompetenze(filterText, istitutoId, pageRequest);
	}

	@GetMapping("/api/competenze/orderBy/istituto/{istitutoId}")
	public Page<Competenza> getCompetenzeOrderByIstitutoId(@PathVariable String istitutoId,
			@RequestParam() List<String> ownerIds, @RequestParam(required = false) String filterText, @RequestParam(required = false) String stato,
			Pageable pageRequest, HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId),
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		if (logger.isInfoEnabled()) {
			logger.info(String.format("getCompetenzeOrderByIstitutoId(%s", istitutoId + ")"));
		}

		return competenzaManager.getCompetenzeOrderByIstituto(istitutoId, ownerIds, filterText, stato, pageRequest);
	}

	@PostMapping("/api/competenza/istituto/{istitutoId}")
	public Competenza saveCompetenza(@PathVariable String istitutoId, @RequestBody Competenza competenza,
			HttpServletRequest request) throws Exception {
		checkNullId(competenza.getId());

		ASLUser user = usersValidator.validate(request,
				Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId),
						new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		
		if(logger.isInfoEnabled()) {
			logger.info(String.format("saveCompetenza(%s", istitutoId + ")"));
		}

		competenza.setSource(Constants.ORIGIN_CONSOLE);
		competenza.setClassificationCode(Utils.getUUID());
		competenza.setOwnerId(istitutoId);
		competenza.setAttiva(true);
		Competenza result = competenzaManager.saveCompetenza(competenza);

		if (result != null) {
			AuditEntry audit = new AuditEntry(request.getMethod(), Competenza.class, result.getId(), user,
					new Object() {
					});
			auditManager.save(audit);
		}

		return result;
	}

	@PutMapping("/api/competenza/istituto/{istitutoId}")
	public Competenza updateCompetenza(@PathVariable String istitutoId, @RequestBody Competenza competenza,
			HttpServletRequest request) throws Exception {
		checkId(competenza.getId());
		ASLUser user = usersValidator.validate(request,
				Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId),
						new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateCompetenza(%s", istitutoId + ")"));
		}

		Competenza result = competenzaManager.saveCompetenza(competenza);

		if (result != null) {
			AuditEntry audit = new AuditEntry(request.getMethod(), Competenza.class, result.getId(), user,
					new Object() {
					});
			auditManager.save(audit);
		}

		return result;
	}

	@DeleteMapping("/api/competenza/{id}")
	public void deleteCompetenza(@PathVariable long id, HttpServletRequest request) throws Exception {
		String istitutoId = competenzaManager.findCompetenzaOwnerId(id);
		ASLUser user = usersValidator.validate(request,
				Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId),
						new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));

		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteCompetenza(%s", id + ")"));
		}

		competenzaManager.deleteCompetenza(id);
		AuditEntry audit = new AuditEntry(request.getMethod(), Competenza.class, id, user, new Object() {
		});
		auditManager.save(audit);
	}
	
	@GetMapping("/api/risorsa/{uuid}/competenze/istituto/{istitutoId}")
	public List<Competenza> getRisorsaCompetenzeIstituto(
			@PathVariable String uuid, 
			@PathVariable String istitutoId, 
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));		
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getRisorsaCompetenzeIstituto(%s", uuid + ")"));
		}
		return competenzaManager.getRisorsaCompetenze(uuid);
	}

	@GetMapping("/api/risorsa/{uuid}/competenze/studente/{studenteId}")
	public List<Competenza> getRisorsaCompetenzeStudente(
			@PathVariable String uuid, 
			@PathVariable String studenteId, 
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, new ASLAuthCheck(ASLRole.STUDENTE, studenteId));
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getRisorsaCompetenzeStudente(%s", uuid + ")"));
		}
		return competenzaManager.getRisorsaCompetenze(uuid);
	}
	
	@PutMapping("/api/risorsa/{uuid}/competenze/istituto/{istitutoId}")
	public Boolean updateCompetenzeToRisorsa(@PathVariable String uuid, @PathVariable String istitutoId, @RequestBody List<Long> ids, HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateCompetenzeToRisorsa(%s", uuid + ")"));
		}
		Boolean result =  competenzaManager.addCompetenzeToPianoAlternanza(uuid, ids);
		if (result != null) {
			AuditEntry audit = new AuditEntry(request.getMethod(), PianoAlternanza.class, uuid, user, new Object(){});
			auditManager.save(audit);
		}
		return result;
	}	

}
