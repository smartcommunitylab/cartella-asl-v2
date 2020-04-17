package it.smartcommunitylab.cartella.asl.controller;

import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

import it.smartcommunitylab.cartella.asl.manager.ASLRolesValidator;
import it.smartcommunitylab.cartella.asl.manager.AuditManager;
import it.smartcommunitylab.cartella.asl.manager.PianoAlternanzaManager;
import it.smartcommunitylab.cartella.asl.model.CorsoDiStudio;
import it.smartcommunitylab.cartella.asl.model.PianoAlternanza;
import it.smartcommunitylab.cartella.asl.model.TipologiaAttivita;
import it.smartcommunitylab.cartella.asl.model.audit.AuditEntry;
import it.smartcommunitylab.cartella.asl.model.users.ASLAuthCheck;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;
import it.smartcommunitylab.cartella.asl.storage.LocalDocumentManager;

@RestController
public class PianoAlternanzaController implements AslController {
	private static Log logger = LogFactory.getLog(PianoAlternanzaController.class);
	
	@Autowired
	private PianoAlternanzaManager pianoAltManager;	
	@Autowired()
	LocalDocumentManager documentManager;	
	@Autowired
	private ASLRolesValidator usersValidator;
	@Autowired
	private AuditManager auditManager;		
	
	@GetMapping("/api/pianiAlternanza/{istitutoId}")
	public Page<PianoAlternanza> getAllPianiIstituto(
			@PathVariable String istitutoId,
			@RequestParam(required = false) String titolo,
			@RequestParam(required = false) String corsoDiStudioId,
			@RequestParam(required = false) String stato,
			Pageable pageRequest, HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId),
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getAllPianiIstituto(%s", istitutoId + ")"));
		}
		return pianoAltManager.findPianoAlternanza(istitutoId, corsoDiStudioId, titolo, stato,
				pageRequest);
	}	

	@PostMapping("/api/pianoAlternanza")
	public @ResponseBody PianoAlternanza savePianoAlternanza(@RequestBody PianoAlternanza pa, HttpServletRequest request) throws Exception {
		checkNullId(pa.getId());
		pa.setUuid(getUuid());
		String istitutoId = pa.getIstitutoId();
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		if(logger.isInfoEnabled()) {
			logger.info(String.format("savePianoAlternanza(%s", pa.getId() + ")"));
		}
		PianoAlternanza result = pianoAltManager.createPianoAlternanza(pa);
		if (result != null) {
			AuditEntry audit = new AuditEntry(request.getMethod(), PianoAlternanza.class, result.getId(), user, new Object(){});
			auditManager.save(audit);
		}
		return result;
	}
	
	@PutMapping("/api/pianoAlternanza")
	public void updatePianoAlternanza(@RequestBody PianoAlternanza pa, HttpServletRequest request) throws Exception {
		checkId(pa.getId());
		String istitutoId = pa.getIstitutoId();
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));		
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updatePianoAlternanza(%s", pa.getId() + ")"));
		}
		pianoAltManager.updatePianoAlternanza(pa);
		AuditEntry audit = new AuditEntry(request.getMethod(), PianoAlternanza.class, pa.getId(), user, new Object(){});
		auditManager.save(audit);			
	}
	

	@GetMapping("/api/pianoAlternanza/{id}")
	public PianoAlternanza getPianoAlternanza(@PathVariable long id, HttpServletRequest request) throws Exception {
		String istitutoId = pianoAltManager.findPianoAlternanzaIstitutoId(id);
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));		
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getPianoAlternanza(%s", id + ")"));
		}
		return pianoAltManager.getPianoAlternanzaDetail(id);
	}
	
	@GetMapping("/api/corsiDiStudio/{istitutoId}")
	public List<CorsoDiStudio> getCorsiDiStudio(@PathVariable String istitutoId, @RequestParam(required=false) String annoScolastico, HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getCorsiDiStudio(%s", istitutoId + ")"));
		}
		return pianoAltManager.findCorsiDiStudio(istitutoId, annoScolastico);
	}

	@DeleteMapping("/api/pianoAlternanza/{pianoId}")
	public boolean deletePianoAlternanza(@PathVariable long pianoId, HttpServletRequest request) throws Exception {
		String istitutoId = pianoAltManager.findPianoAlternanzaIstitutoId(pianoId);
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));	
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deletePianoAlternanza(%s", pianoId + ")"));
		}
		boolean result = pianoAltManager.deletePianoAlternanza(pianoId);
		AuditEntry audit = new AuditEntry(request.getMethod(), PianoAlternanza.class, pianoId, user, new Object(){});
		auditManager.save(audit);			
		return result;
	}
	
	@GetMapping("/api/pianoAlternanza/{id}/tipologie")
	public Map<String, List<TipologiaAttivita>> getPianoTipologie(@PathVariable long id, HttpServletRequest request) throws Exception {
		String istitutoId = pianoAltManager.findPianoAlternanzaIstitutoId(id);
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));		
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getPianoTipologie(%s", id + ")"));
		}
		return  pianoAltManager.getTipologiePianoAlternanza(id);
		
	}
	
	@PutMapping("/api/pianoAlternanza/{id}/tipologie")
	public Boolean updateTipologieToPianoAlternanza(@PathVariable long id, @RequestBody Map<String, List<TipologiaAttivita>> saveTipos, HttpServletRequest request) throws Exception {
		String istitutoId = pianoAltManager.findPianoAlternanzaIstitutoId(id);
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateTipologieToPianoAlternanza(%s", id + ")"));
		}
		Boolean result =  pianoAltManager.updateTipologieToPianoAlternanza(id, saveTipos);
		if (result != null) {
			AuditEntry audit = new AuditEntry(request.getMethod(), PianoAlternanza.class, id, user, new Object(){});
			auditManager.save(audit);
		}
		return result;		
		
	}
	
	@PostMapping("/api/pianoAlternanza/duplica")
	public PianoAlternanza duplicaPianoAlternanza(@RequestBody PianoAlternanza duplicaPiano, HttpServletRequest request) throws Exception {
		String istitutoId = pianoAltManager.findPianoAlternanzaIstitutoId(duplicaPiano.getId());
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));			
		
		PianoAlternanza result = pianoAltManager.duplicaPianoAlternanza(duplicaPiano);
		
		if (result != null) {
			AuditEntry audit = new AuditEntry(request.getMethod(), PianoAlternanza.class, result.getId(), user, new Object(){});
			auditManager.save(audit);
		}
		
		return result;
	}
	
	@GetMapping("/api/pianoAlternanza/duplica/{id}")
	public PianoAlternanza getDuplicaPianoAlternanza(@PathVariable long id, HttpServletRequest request) throws Exception {
		String istitutoId = pianoAltManager.findPianoAlternanzaIstitutoId(id);
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));			
		
		PianoAlternanza result = pianoAltManager.getDuplicaPianoAlternanza(id);
		
		if (result != null) {
			AuditEntry audit = new AuditEntry(request.getMethod(), PianoAlternanza.class, result.getId(), user, new Object(){});
			auditManager.save(audit);
		}
		
		return result;
	}
	
	
	@PutMapping("/api/pianoAlternanza/activate/{id}")
	public @ResponseBody PianoAlternanza activatePianoAlternanza(@PathVariable Long id, HttpServletRequest request) throws Exception {
		String istitutoId = pianoAltManager.findPianoAlternanzaIstitutoId(id);
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));			
		
		PianoAlternanza result = pianoAltManager.activatePianoAlternanza(id);
		
		if (result != null) {
			AuditEntry audit = new AuditEntry(request.getMethod(), PianoAlternanza.class, result.getId(), user, new Object(){});
			auditManager.save(audit);
		}
		
		return result;		
	}	
	
	@PutMapping("/api/pianoAlternanza/deactivate/{id}")
	public @ResponseBody void dactivatePianoAlternanza(@PathVariable Long id, HttpServletRequest request) throws Exception {
		String istitutoId = pianoAltManager.findPianoAlternanzaIstitutoId(id);
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));			
		
		pianoAltManager.disactivatePianAlternanza(id);
		
		AuditEntry audit = new AuditEntry(request.getMethod(), PianoAlternanza.class, id, user, new Object(){});
		auditManager.save(audit);			
	}		
	
	
}
