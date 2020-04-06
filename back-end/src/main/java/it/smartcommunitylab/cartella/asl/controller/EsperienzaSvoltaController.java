package it.smartcommunitylab.cartella.asl.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import it.smartcommunitylab.cartella.asl.exception.ASLCustomException;
import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.manager.ASLRolesValidator;
import it.smartcommunitylab.cartella.asl.manager.AuditManager;
import it.smartcommunitylab.cartella.asl.manager.QueriesManager;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;
import it.smartcommunitylab.cartella.asl.model.audit.AuditEntry;
import it.smartcommunitylab.cartella.asl.model.users.ASLAuthCheck;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;
import it.smartcommunitylab.cartella.asl.util.ErrorLabelManager;

// TODO acl for aziende?
@RestController
public class EsperienzaSvoltaController implements AslController {

	@Autowired
	private QueriesManager aslManager;
	
	@Autowired
	private ASLRolesValidator usersValidator;		
	
	@Autowired
	private ErrorLabelManager errorLabelManager;
	
	@Autowired
	private AuditManager auditManager;		
	
	private static Log logger = LogFactory.getLog(EsperienzaSvoltaController.class);


	@GetMapping("/api/esperienzaSvolta")
	public Page<EsperienzaSvolta> findEsperienzaSvoltaByAziendaId(@RequestParam(required=false) String istitutoId, @RequestParam String aziendaId, @RequestParam(required=false) Long dataInizio, @RequestParam(required=false) Long dataFine, @RequestParam(required=false) String stato, @RequestParam(required=false) String tipologia, @RequestParam(required=false) String filterText, @RequestParam(required=false) Boolean individuale, HttpServletRequest request, Pageable pageRequest) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, aziendaId), new ASLAuthCheck(ASLRole.REFERENTE_AZIENDA, aziendaId)));
		
		List<Integer> statoList = null;
		List<Integer> tipologiaList = null;
		if (stato != null) {
			statoList = Splitter.on(",").splitToList(stato).stream().map(x -> Integer.parseInt(x)).collect(Collectors.toList());
		}
		if (tipologia != null) {
		 tipologiaList = Splitter.on(",").splitToList(tipologia).stream().map(x -> Integer.parseInt(x)).collect(Collectors.toList());
		}
		Page<EsperienzaSvolta> result = aslManager.findEsperienzaSvoltaByIstitutoAndAziendaIds(istitutoId, aziendaId, dataInizio, dataFine, statoList, tipologiaList, filterText, individuale, pageRequest);
		
		return result;
	}

//	@GetMapping("/api/esperienzaSvolta/details/{id}")
//	public EsperienzaSvolta getEsperienzaSvolta(@PathVariable long id, HttpServletRequest request) throws Exception {
//		String aziendaId = aslManager.findEsperienzaSvoltaAziendaId(id);
//		String istitutoId = aslManager.findEsperienzaSvoltaIstitutoId(id);
//		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, aziendaId), new ASLAuthCheck(ASLRole.REFERENTE_AZIENDA, aziendaId), new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));			
//		
//		EsperienzaSvolta result = aslManager.getEsperienzaSvolta(id);
//		
//		if (result == null) {
//			throw new BadRequestException(errorLabelManager.get("esp.error.notfound"));
//		}		
//		
//	result.getPresenze().computeOreSvolte();
//		return result;
//	}
	
//	@DeleteMapping("/api/esperienzaSvolta/{attivitaAlternanzaId}/{studenteId}")
//	public void deleteEsperienzaSvolta(@PathVariable long attivitaAlternanzaId, @PathVariable String studenteId, HttpServletRequest request) throws Exception {
//		String istitutoId = aslManager.findAttivitaAlternanzaIstitutoId(attivitaAlternanzaId);
//		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));			
//		
//		Long id = aslManager.deleteEsperienzaSvolta(attivitaAlternanzaId, studenteId);
//		
//		if (id != null) {
//			AuditEntry audit = new AuditEntry(request.getMethod(), EsperienzaSvolta.class, id, user, new Object(){});
//			auditManager.save(audit);
//		}
//	}	
	
//	
//	@PostMapping("/api/esperienzaSvolta/noteAzienda/{esId}")
//	public EsperienzaSvolta saveNoteAzienda(@PathVariable long esId, @RequestParam String note, HttpServletRequest request)
//			throws Exception {
//		String aziendaId = aslManager.findEsperienzaSvoltaAziendaId(esId);
//		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, aziendaId), new ASLAuthCheck(ASLRole.REFERENTE_AZIENDA, aziendaId)));		
//		
//		try {
//			if (logger.isInfoEnabled()) {
//				logger.info(String.format("/esperienzaSvolta/noteAzienda/[%s]", esId));
//			}
//			EsperienzaSvolta result =  aslManager.saveNoteAzienda(esId, note);
//			
//			if (result != null) {
//				AuditEntry audit = new AuditEntry(request.getMethod(), EsperienzaSvolta.class, esId, user, new Object(){});
//				auditManager.save(audit);
//			}
//			
//			return result;
//
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			throw new ASLCustomException(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
//		}
//	}
	
//	@GetMapping("/api/schedaValutazioneAzienda/details/{id}")
//	public SchedaValutazione getSchedaValutazioneAzienda(@PathVariable long id, HttpServletRequest request) throws Exception {
//		String aziendaId = aslManager.findEsperienzaSvoltaAziendaId(id);
//		String istitutoId = aslManager.findEsperienzaSvoltaIstitutoId(id);
//		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, aziendaId), new ASLAuthCheck(ASLRole.REFERENTE_AZIENDA, aziendaId), new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
//		
//		EsperienzaSvolta es = aslManager.findEsperienzaSvoltaById(id);
//		if (es == null) {
//			throw new BadRequestException(errorLabelManager.get("esp.error.notfound"));
//		}			
//		return es.getSchedaValutazioneAzienda();
//	}	
	
//	@PostMapping("/api/schedaValutazioneAzienda/{id}")
//	public SchedaValutazione saveSchedaValutazioneAzienda(@PathVariable long id, @RequestBody SchedaValutazione sva, HttpServletRequest request) throws Exception {
//		checkId(id);
//		
//		String aziendaId = aslManager.findEsperienzaSvoltaAziendaId(id);
//		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, aziendaId), new ASLAuthCheck(ASLRole.REFERENTE_AZIENDA, aziendaId)));				
//		
//		EsperienzaSvolta es = aslManager.findEsperienzaSvoltaById(id);
//		if (es == null) {
//			throw new BadRequestException(errorLabelManager.get("esp.error.notfound"));
//		}
//		
//		es.setSchedaValutazioneAzienda(sva);
//		es = aslManager.saveEsperienzaSvolta(es);
//		SchedaValutazione result = es.getSchedaValutazioneAzienda();
//		
//		if (result != null) {
//			AuditEntry audit = new AuditEntry(request.getMethod(), SchedaValutazione.class, result.getId(), user, new Object(){});
//			auditManager.save(audit);
//		}
//		
//		return result;
//	}	
	
}
