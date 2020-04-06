package it.smartcommunitylab.cartella.asl.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;

import it.smartcommunitylab.cartella.asl.exception.ASLCustomException;
import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.exception.UnauthorizedException;
import it.smartcommunitylab.cartella.asl.manager.ASLRolesValidator;
import it.smartcommunitylab.cartella.asl.manager.AuditManager;
import it.smartcommunitylab.cartella.asl.manager.EsperienzaSvoltaManager;
import it.smartcommunitylab.cartella.asl.model.Documento;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;
import it.smartcommunitylab.cartella.asl.model.audit.AuditEntry;
import it.smartcommunitylab.cartella.asl.model.users.ASLAuthCheck;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;
import it.smartcommunitylab.cartella.asl.storage.LocalDocumentManager;

@RestController
public class FileController {

	@Value("${storage.type}")
	private String storageType;

	@Autowired()
	LocalDocumentManager documentManager;
	@Autowired
	private ASLRolesValidator usersValidator;	
	@Autowired
	private AuditManager auditManager;		
	@Autowired
	private EsperienzaSvoltaManager esperienzaSvoltaManager;
	
	private static Log logger = LogFactory.getLog(FileController.class);


	@GetMapping("/api/download/document/{uuid}/istituto/{istitutoId}")
	public void downloadFileIstituto(
			@PathVariable String uuid, 
			@PathVariable String istitutoId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		if(logger.isInfoEnabled()) {
			logger.info(String.format("downloadFileIstituto(%s", uuid + ")"));
		}
		downloadContent(uuid, response);
	}

	@GetMapping("/api/download/document/{uuid}/studente/{studenteId}")
	public void downloadFileStudente(
			@PathVariable String uuid, 
			@PathVariable String studenteId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		usersValidator.validate(request, new ASLAuthCheck(ASLRole.STUDENTE, studenteId));
		if(logger.isInfoEnabled()) {
			logger.info(String.format("downloadFileStudente(%s", uuid + ")"));
		}
		checkEsperienzeStudente(uuid, studenteId, true);
		downloadContent(uuid, response);
	}

	@DeleteMapping("/api/remove/document/{uuid}/istituto/{istitutoId}")
	public @ResponseBody boolean removeIstitutoDocument(
			@PathVariable String istitutoId,
			@PathVariable String uuid, 
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		if(logger.isInfoEnabled()) {
			logger.info(String.format("removeIstitutoDocument(%s", uuid + ")"));
		}
		return removeDocument(uuid, request, user);
	}
	
	@DeleteMapping("/api/remove/document/{uuid}/studente/{studenteId}")
	public @ResponseBody boolean removeStudenteDocument(
			@PathVariable String studenteId,
			@PathVariable String uuid, 
			HttpServletRequest request) throws Exception {
		ASLUser user =  usersValidator.validate(request, new ASLAuthCheck(ASLRole.STUDENTE, studenteId));
		if(logger.isInfoEnabled()) {
			logger.info(String.format("removeStudenteDocument(%s", uuid + ")"));
		}
		checkEsperienzeStudente(uuid, studenteId, true);
		return removeDocument(uuid, request, user);
	}

	
	
	@PostMapping("/api/upload/document/risorsa/{uuid}/istituto/{istitutoId}")
	public @ResponseBody Documento uploadDocumentoForRisorsaIstituto(
			@PathVariable String uuid, 
			@PathVariable String istitutoId,  
			@RequestParam("data") MultipartFile data, 
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		if(logger.isInfoEnabled()) {
			logger.info(String.format("uploadDocumentoForRisorsaIstituto(%s", uuid + ")"));
		}
		Documento documento = uploadContent(uuid, data, request, user);
		return documento;
	}
	
	@PostMapping("/api/upload/document/risorsa/{uuid}/studente/{studenteId}")
	public @ResponseBody Documento uploadDocumentoForRisorsaStudente(
			@PathVariable String uuid, 
			@PathVariable String studenteId,  
			@RequestParam("data") MultipartFile data, 
			HttpServletRequest request) throws Exception {
		ASLUser user =  usersValidator.validate(request, new ASLAuthCheck(ASLRole.STUDENTE, studenteId));
		if(logger.isInfoEnabled()) {
			logger.info(String.format("uploadDocumentoForRisorsaStudente(%s", uuid + ")"));
		}
		checkEsperienzeStudente(uuid, studenteId, false);
		Documento documento = uploadContent(uuid, data, request, user);
		return documento;
	}
	
	@GetMapping("/api/download/document/risorsa/{uuid}/istituto/{istitutoId}/attivita")
	public @ResponseBody List<Documento> getDocumentiIstitutoURLForRisorsaAttivita(
			@PathVariable String uuid, 
			@PathVariable String istitutoId, 
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));		
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getDocumentiIstitutoURLForRisorsaAttivita(%s", uuid + ")"));
		}
		return documentManager.getDocumentByAttivita(uuid, request);
	}
	
	@GetMapping("/api/download/document/risorsa/{uuid}/istituto/{istitutoId}/piano")
	public @ResponseBody List<Documento> getDocumentiIstitutoURLForRisorsaPiano(
			@PathVariable String uuid, 
			@PathVariable String istitutoId, 
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));		
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getDocumentiIstitutoURLForRisorsaPiano(%s", uuid + ")"));
		}
		return documentManager.getDocument(uuid, request);
	}	
	
	@GetMapping("/api/download/document/risorsa/{uuid}/studente/{studenteId}")
	public @ResponseBody List<Documento> getDocumentiStudenteURLForRisorsa(
			@PathVariable String uuid, 
			@PathVariable String studenteId, 
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, new ASLAuthCheck(ASLRole.STUDENTE, studenteId));
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getDocumentiStudenteURLForRisorsa(%s", uuid + ")"));
		}
		checkEsperienzeStudente(uuid, studenteId, false);
		return documentManager.getDocument(uuid, request);
	}	
	
	private void checkEsperienzeStudente(String uuid, String studenteId, boolean doc)
			throws BadRequestException, UnauthorizedException {
		EsperienzaSvolta esperienzaSvolta = null;
		if(doc) {
			Documento document = documentManager.getEntity(uuid);
			if(document != null) {
				esperienzaSvolta = esperienzaSvoltaManager.findByUuid(document.getRisorsaId());
			}
		} else {
			esperienzaSvolta = esperienzaSvoltaManager.findByUuid(uuid);
		}
		if(esperienzaSvolta == null) {
			throw new BadRequestException("esperienzaSvolta not found");
		}
		if(!studenteId.equals(esperienzaSvolta.getStudenteId())) {
			throw new UnauthorizedException("uuid not authorized");
		}
	}
	
	private void downloadContent(String uuid, HttpServletResponse response) throws Exception {
		try {
			Documento doc = documentManager.getEntity(uuid);
			File file = documentManager.loadFile(doc.getUuid());
			response.setContentType(doc.getFormatoDocumento());
			response.setHeader("Content-Disposition", "attachment; filename=\"" + doc.getNomeFile() + "\"");
			response.getOutputStream().write(FileUtils.readFileToByteArray(file));
		} catch (FileNotFoundException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}			
	}
	
	private Documento uploadContent(String uuid, MultipartFile data, 
			HttpServletRequest request, ASLUser user) throws Exception {
		try {
			Documento doc = documentManager.addDocumentToRisorsa(uuid, data, request);
			if (doc != null) {
				AuditEntry audit = new AuditEntry(request.getMethod(), Documento.class, 
						doc.getUuid(), user, new Object(){});
				auditManager.save(audit);	
			}				
			return doc;
		} catch (Exception e) {
			throw new ASLCustomException(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}		
	}
	
	private boolean removeDocument(String uuid, HttpServletRequest request, ASLUser user) throws Exception {
		try {
			boolean result = documentManager.removeDocument(uuid);
			AuditEntry audit = new AuditEntry(request.getMethod(), Documento.class, uuid, user, new Object(){});
			auditManager.save(audit);			
			return result;
		} catch (Exception e) {
			throw new ASLCustomException(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
}
