package it.smartcommunitylab.cartella.asl.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.Lists;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.cartella.asl.manager.ASLRolesValidator;
import it.smartcommunitylab.cartella.asl.manager.AuditManager;
import it.smartcommunitylab.cartella.asl.manager.RegistrazioneDocenteManager;
import it.smartcommunitylab.cartella.asl.model.AssociazioneDocentiClassi;
import it.smartcommunitylab.cartella.asl.model.RegistrazioneDocente;
import it.smartcommunitylab.cartella.asl.model.audit.AuditEntry;
import it.smartcommunitylab.cartella.asl.model.report.DocentiClassiReport;
import it.smartcommunitylab.cartella.asl.model.report.ReportDocenteClasse;
import it.smartcommunitylab.cartella.asl.model.users.ASLAuthCheck;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;

@RestController
public class RegistrazioneDocenteController implements AslController {
  private static Log logger = LogFactory.getLog(RegistrazioneDocenteController.class);

  @Autowired
  private RegistrazioneDocenteManager registrazioneDocenteManager;
  @Autowired
	private ASLRolesValidator usersValidator;
	@Autowired
	private AuditManager auditManager;

  @GetMapping("/api/registrazione-docente")
  public List<RegistrazioneDocente> getRegistrazioneDocente(
    @RequestParam String istitutoId,
    HttpServletRequest request) throws Exception {
    usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
      new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
    List<RegistrazioneDocente> registrazioni = registrazioneDocenteManager.getRegistrazioneDocente(istitutoId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getRegistrazioneDocente:%s", istitutoId));
		}		
    return registrazioni;
  }

  @GetMapping("/api/registrazione-docente/detail")
  public RegistrazioneDocente getRegistrazioneDocenteDetail (
    @RequestParam String istitutoId,
    @RequestParam Long registrazioneId,
    HttpServletRequest request) throws Exception {
    usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
      new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
    RegistrazioneDocente rd = registrazioneDocenteManager.getRegistrazioneDocente(registrazioneId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getRegistrazioneDocenteDetail:%s - %s", istitutoId, registrazioneId));
		}		
    return rd;
  }

  @GetMapping("/api/registrazione-docente/search")
  public Page<RegistrazioneDocente> searchRegistrazioneDocente(
    @RequestParam String istitutoId,
    @RequestParam(required = false) String text,
    Pageable pageRequest, 
    HttpServletRequest request) throws Exception {
    usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
      new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
    Page<RegistrazioneDocente> page = registrazioneDocenteManager.searchRegistrazioneDocente(istitutoId, text, pageRequest);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchRegistrazioneDocente:%s - %s", istitutoId, text));
		}		
    return page;
  }

  @GetMapping("/api/registrazione-docente/docenti-classi")
  public Page<ReportDocenteClasse> getDocentiClassi(
    @RequestParam String istitutoId,
    @RequestParam String annoScolastico,
    @RequestParam(required = false) String text,
    Pageable pageRequest, 
		HttpServletRequest request) throws Exception {
    usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
      new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
    Page<ReportDocenteClasse> page = registrazioneDocenteManager.getDocentiClassi(istitutoId, annoScolastico, text, pageRequest);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getDocentiClassi:%s - %s - %s", istitutoId, annoScolastico, text));
		}		
    return page;
  }

  @PostMapping("/api/registrazione-docente")
  public List<RegistrazioneDocente> addRegistrazioneDocente(
    @RequestParam String istitutoId,
    @RequestBody List<String> referenteAlternanzaIdList,
    HttpServletRequest request) throws Exception {
    ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
      new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
    List<RegistrazioneDocente> result = new ArrayList<>();
    for(String referenteAlternanzaId : referenteAlternanzaIdList) {
      RegistrazioneDocente registrazione = registrazioneDocenteManager.addRegistrazioneDocente(istitutoId, referenteAlternanzaId);
      AuditEntry audit = new AuditEntry(request.getMethod(), RegistrazioneDocente.class, registrazione.getId(), user, new Object(){});
      auditManager.save(audit);			
      if(logger.isInfoEnabled()) {
        logger.info(String.format("addRegistrazioneDocente:%s - %s", istitutoId, referenteAlternanzaId));
      }
      result.add(registrazione);		  
    }
    return result;
  }

  @DeleteMapping("/api/registrazione-docente")
  public RegistrazioneDocente deleteRegistrazioneDocente(
    @RequestParam String istitutoId,
    @RequestParam Long registrazioneId,
    HttpServletRequest request) throws Exception {
    ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
      new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
    RegistrazioneDocente registrazione = registrazioneDocenteManager.deleteRegistrazioneDocente(istitutoId, registrazioneId);
		AuditEntry audit = new AuditEntry(request.getMethod(), RegistrazioneDocente.class, registrazione.getId(), user, new Object(){});
		auditManager.save(audit);			
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteRegistrazioneDocente:%s - %s", istitutoId, registrazioneId));
		}		
    return registrazione;
  } 
  
  @GetMapping("/api/registrazione-docente/reg/classi")
  public Page<DocentiClassiReport> getAssociazioneDocentiClassi(
    @RequestParam String istitutoId,
    @RequestParam String annoScolastico,
    @RequestParam Long registrazioneId,
    @RequestParam(required = false) String text,
    Pageable pageRequest, 
    HttpServletRequest request) throws Exception {
    usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
        new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
    Page<DocentiClassiReport> classi = registrazioneDocenteManager.getAssociazioneDocentiClassi(istitutoId, 
        annoScolastico, registrazioneId, text, pageRequest);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getAssociazioneDocentiClassi:%s - %s", istitutoId, registrazioneId));
		}		
    return classi;
  }

  @PostMapping("/api/registrazione-docente/reg/classi") 
  public List<AssociazioneDocentiClassi> updateAssociazioneDocentiClassi(
    @RequestParam String istitutoId,
    @RequestParam String annoScolastico,
    @RequestParam Long registrazioneId,
    @RequestBody List<String> classi,
    HttpServletRequest request) throws Exception {
    ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
      new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
    List<AssociazioneDocentiClassi> result = registrazioneDocenteManager.updateAssociazioneDocentiClassi(istitutoId, annoScolastico,
      registrazioneId, classi);      
		AuditEntry audit = new AuditEntry(request.getMethod(), RegistrazioneDocente.class, registrazioneId, user, new Object(){});
		auditManager.save(audit);			
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateAssociazioneDocentiClassi:%s - %s", istitutoId, registrazioneId));
		}		
    return result;
  }

}
