package it.smartcommunitylab.cartella.asl.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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
import it.smartcommunitylab.cartella.asl.manager.AttivitaAlternanzaManager;
import it.smartcommunitylab.cartella.asl.manager.AuditManager;
import it.smartcommunitylab.cartella.asl.manager.ConvenzioneManager;
import it.smartcommunitylab.cartella.asl.manager.EsperienzaSvoltaManager;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.Convenzione;
import it.smartcommunitylab.cartella.asl.model.Documento;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;
import it.smartcommunitylab.cartella.asl.model.Documento.TipoDoc;
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
	@Autowired
	private AttivitaAlternanzaManager attivitaAlternanzaManager;
	@Autowired
	private ConvenzioneManager convenzioneManager;
	
	private static Log logger = LogFactory.getLog(FileController.class);


	@GetMapping("/api/download/document/{uuid}/istituto/{istitutoId}")
	public void downloadFileIstituto(
			@PathVariable String uuid, 
			@PathVariable String istitutoId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(
				new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId),
				new ASLAuthCheck(ASLRole.TUTOR_SCOLASTICO, istitutoId)));
		if(logger.isInfoEnabled()) {
			logger.info(String.format("downloadFileIstituto(%s", uuid + ")"));
		}
		checkAccessoAttivita(uuid, istitutoId, true, user);
		downloadContent(uuid, response, null);
	}
	
	@GetMapping("/api/download/document/convenzione/{uuid}/istituto/{istitutoId}")
	public void downloadFileConvenzioneIstituto(
			@PathVariable String uuid, 
			@PathVariable String istitutoId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(
				new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		Convenzione c = convenzioneManager.getConvenzioneByUuid(uuid);
		if(c == null) {
			throw new BadRequestException("convenzione non esistente");
		}	
		if(!istitutoId.equals(c.getIstitutoId())) {
			throw new BadRequestException("convenzione non autorizzata");
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("downloadFileConvenzioneIstituto:%s - %s", uuid, istitutoId));
		}
		downloadContent(c.getUuidFile(), response, null);
	}
	
	@GetMapping("/api/download/document/convenzione/{uuid}/ente/{enteId}")
	public void downloadFileConvenzioneEnte(
			@PathVariable String uuid, 
			@PathVariable String enteId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(
				new ASLAuthCheck(ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, enteId), 
				new ASLAuthCheck(ASLRole.REFERENTE_AZIENDA, enteId)));
		Convenzione c = convenzioneManager.getConvenzioneByUuid(uuid);
		if(c == null) {
			throw new BadRequestException("convenzione non esistente");
		}	
		if(!enteId.equals(c.getEnteId())) {
			throw new BadRequestException("convenzione non autorizzata");
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("downloadFileConvenzioneEnte:%s - %s", uuid, enteId));
		}
		downloadContent(c.getUuidFile(), response, null);
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
		downloadContent(uuid, response, Lists.newArrayList(TipoDoc.valutazione_esperienza, TipoDoc.piano_formativo,
				TipoDoc.valutazione_studente, TipoDoc.doc_generico));
	}

	@GetMapping("/api/download/document/{uuid}/ente/{enteId}")
	public void downloadFileEnte(
			@PathVariable String uuid, 
			@PathVariable String enteId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, enteId), 
				new ASLAuthCheck(ASLRole.REFERENTE_AZIENDA, enteId)));
		if(logger.isInfoEnabled()) {
			logger.info(String.format("downloadFileEnte(%s", uuid + ")"));
		}
		checkAttivitaEnte(uuid, enteId, true);
		downloadContent(uuid, response, Lists.newArrayList(TipoDoc.valutazione_studente, TipoDoc.piano_formativo,
				TipoDoc.convenzione, TipoDoc.doc_generico));
	}
	
	@DeleteMapping("/api/remove/document/convenzione/{uuid}/istituto/{istitutoId}")
	public @ResponseBody boolean removeIstitutoConvenzioneDocument(
			@PathVariable String istitutoId,
			@PathVariable String uuid, 
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(
				new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		Convenzione c = convenzioneManager.getConvenzioneByUuid(uuid);
		if(c == null) {
			throw new BadRequestException("convenzione non esistente");
		}
		if(!istitutoId.equals(c.getIstitutoId())) {
			throw new BadRequestException("convenzione non autorizzata");
		}		
		if(logger.isInfoEnabled()) {
			logger.info(String.format("removeIstitutoConvenzioneDocument:%s / %s", uuid, istitutoId));
		}
		return removeDocument(c.getUuidFile(), request, user, null);
	}
	
	@DeleteMapping("/api/remove/document/{uuid}/istituto/{istitutoId}")
	public @ResponseBody boolean removeIstitutoDocument(
			@PathVariable String istitutoId,
			@PathVariable String uuid, 
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(
				new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId),
				new ASLAuthCheck(ASLRole.TUTOR_SCOLASTICO, istitutoId)));
		if(logger.isInfoEnabled()) {
			logger.info(String.format("removeIstitutoDocument(%s", uuid + ")"));
		}
		checkAccessoAttivita(uuid, istitutoId, true, user);
		return removeDocument(uuid, request, user, null);
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
		return removeDocument(uuid, request, user, Lists.newArrayList(TipoDoc.valutazione_esperienza, TipoDoc.doc_generico));
	}

	@DeleteMapping("/api/remove/document/{uuid}/ente/{enteId}")
	public @ResponseBody boolean removeEnteDocument(
			@PathVariable String enteId,
			@PathVariable String uuid, 
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, enteId), 
				new ASLAuthCheck(ASLRole.REFERENTE_AZIENDA, enteId)));
		if(logger.isInfoEnabled()) {
			logger.info(String.format("removeEnteDocument(%s", uuid + ")"));
		}
		checkAttivitaEnte(uuid, enteId, true);
		return removeDocument(uuid, request, user, Lists.newArrayList(TipoDoc.valutazione_studente, TipoDoc.doc_generico));
	}
	
	@PostMapping("/api/upload/document/risorsa/{uuid}/istituto/{istitutoId}")
	public @ResponseBody Documento uploadDocumentoForRisorsaIstituto(
			@PathVariable String uuid, 
			@PathVariable String istitutoId, 
			@RequestParam("tipo") TipoDoc tipo,
			@RequestParam("data") MultipartFile data, 
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(
				new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId),
				new ASLAuthCheck(ASLRole.TUTOR_SCOLASTICO, istitutoId)));
		if(logger.isInfoEnabled()) {
			logger.info(String.format("uploadDocumentoForRisorsaIstituto:%s - %s", uuid, istitutoId));
		}
		checkAccessoAttivita(uuid, istitutoId, false, user);
		Documento documento = uploadContent(uuid, tipo, data, request, user, null);
		return documento;
	}
	
	@PostMapping("/api/upload/document/convenzione/{uuid}/istituto/{istitutoId}")
	public @ResponseBody Documento uploadDocumentoForConvezioneIstituto(
			@PathVariable String uuid, 
			@PathVariable String istitutoId, 
			@RequestParam("tipo") TipoDoc tipo,
			@RequestParam("data") MultipartFile data, 
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(
				new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
		Convenzione c = convenzioneManager.getConvenzioneByUuid(uuid);
		if(c == null) {
			throw new BadRequestException("convenzione non esistente");
		}
		if(!istitutoId.equals(c.getIstitutoId())) {
			throw new BadRequestException("convenzione non autorizzata");
		}		
		if(logger.isInfoEnabled()) {
			logger.info(String.format("uploadDocumentoForConvezioneIstituto:%s - %s", uuid, istitutoId));
		}
		Documento documento = uploadContent(uuid, tipo, data, request, user, null);
		return documento;
	}
	
	@PostMapping("/api/upload/document/risorsa/{uuid}/studente/{studenteId}")
	public @ResponseBody Documento uploadDocumentoForRisorsaStudente(
			@PathVariable String uuid, 
			@PathVariable String studenteId,
			@RequestParam("tipo") TipoDoc tipo,
			@RequestParam("data") MultipartFile data, 
			HttpServletRequest request) throws Exception {
		ASLUser user =  usersValidator.validate(request, new ASLAuthCheck(ASLRole.STUDENTE, studenteId));
		if(logger.isInfoEnabled()) {
			logger.info(String.format("uploadDocumentoForRisorsaStudente:%s - %s", uuid, studenteId));
		}
		checkEsperienzeStudente(uuid, studenteId, false);
		Documento documento = uploadContent(uuid, tipo, data, request, user, Lists.newArrayList(TipoDoc.valutazione_esperienza,
				TipoDoc.doc_generico));
		return documento;
	}	

	@PostMapping("/api/upload/document/risorsa/{uuid}/ente/{enteId}")
	public @ResponseBody Documento uploadDocumentoForRisorsaEnte(
			@PathVariable String uuid, 
			@PathVariable String enteId,
			@RequestParam("tipo") TipoDoc tipo,
			@RequestParam("data") MultipartFile data, 
			HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, enteId), 
				new ASLAuthCheck(ASLRole.REFERENTE_AZIENDA, enteId)));
		if(logger.isInfoEnabled()) {
			logger.info(String.format("uploadDocumentoForRisorsaEnte:%s - %s", uuid, enteId));
		}
		checkAttivitaEnte(uuid, enteId, false);
		Documento documento = uploadContent(uuid, tipo, data, request, user, Lists.newArrayList(TipoDoc.valutazione_studente,
				TipoDoc.doc_generico));
		return documento;
	}
	
	@GetMapping("/api/download/document/risorsa/{uuid}/istituto/{istitutoId}/attivita")
	public @ResponseBody List<Documento> getDocumentiIstitutoURLForRisorsaAttivita(
			@PathVariable String uuid, 
			@PathVariable String istitutoId, 
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(
				new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId),
				new ASLAuthCheck(ASLRole.TUTOR_SCOLASTICO, istitutoId)));		
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getDocumentiIstitutoURLForRisorsaAttivita(%s", uuid + ")"));
		}
		return documentManager.getDocumentByAttivita(uuid);
	}
	
	@GetMapping("/api/download/document/risorsa/{uuid}/istituto/{istitutoId}/piano")
	public @ResponseBody List<Documento> getDocumentiIstitutoURLForRisorsaPiano(
			@PathVariable String uuid, 
			@PathVariable String istitutoId, 
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), 
				new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));		
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getDocumentiIstitutoURLForRisorsaPiano(%s", uuid + ")"));
		}
		return documentManager.getDocument(uuid);
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
		return documentManager.getDocumentByStudente(uuid);
	}	
	
	@GetMapping("/api/download/document/risorsa/{uuid}/ente/{enteId}")
	public @ResponseBody List<Documento> getDocumentiEnteURLForRisorsa(
			@PathVariable String uuid, 
			@PathVariable String enteId, 
			HttpServletRequest request) throws Exception {
		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, enteId), 
				new ASLAuthCheck(ASLRole.REFERENTE_AZIENDA, enteId)));
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getDocumentiEnteURLForRisorsa(%s", uuid + ")"));
		}
		checkAttivitaEnte(uuid, enteId, false);
		return documentManager.getDocumentByEnte(uuid);
	}

	private void checkAccessoAttivita(String uuid, String istitutoId, boolean doc, ASLUser user) throws BadRequestException {
		if(doc) {
			Documento document = documentManager.getEntity(uuid);
			if(document != null) {
				AttivitaAlternanza aa = attivitaAlternanzaManager.findByUuid(document.getRisorsaId());
				if(aa != null) {
					attivitaAlternanzaManager.getAttivitaAlternanzaDetails(aa, user);
				}		
			}
		} else {
			AttivitaAlternanza aa = attivitaAlternanzaManager.findByUuid(uuid);
			if(aa != null) {
				attivitaAlternanzaManager.getAttivitaAlternanzaDetails(aa, user);
			}		
		}
	}
	
	private void checkEsperienzeStudente(String uuid, String studenteId, boolean doc)
			throws BadRequestException, UnauthorizedException {
		EsperienzaSvolta esperienzaSvolta = null;
		if(doc) {
			Documento document = documentManager.getEntity(uuid);
			if(document != null) {
				esperienzaSvolta = esperienzaSvoltaManager.findByUuid(document.getRisorsaId());
				if(esperienzaSvolta == null) {
					AttivitaAlternanza aa = attivitaAlternanzaManager.findByUuid(document.getRisorsaId());
					if(aa != null) {
						List<EsperienzaSvolta> list = esperienzaSvoltaManager.getEsperienzeByAttivitaAndStudente(aa.getId(), studenteId);
						if(list.size() > 0) {
							esperienzaSvolta = list.get(0);
						}
					}
				}
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
	
	private void checkAttivitaEnte(String uuid, String enteId, boolean doc) throws Exception {
		AttivitaAlternanza aa = null;
		if(doc) {
			Documento document = documentManager.getEntity(uuid);
			if(document != null) {
				aa = attivitaAlternanzaManager.findByUuid(document.getRisorsaId());
			}
		} else {
			aa = attivitaAlternanzaManager.findByUuid(uuid);
		}
		if(aa == null) {
			throw new BadRequestException("AttivitaAlternanza not found");
		}
		if(!enteId.equals(aa.getEnteId())) {
			throw new UnauthorizedException("uuid not authorized");
		}
		if((aa.getTipologia() != 7) && (aa.getTipologia() != 10)) {
			throw new BadRequestException("typology not visible");
		}		
	}
	
	private void checkTipologia(TipoDoc tipo, ArrayList<TipoDoc> list) throws Exception {
		if(!list.contains(tipo)) {
			throw new BadRequestException("tipologia di file non corretta");
		}
	}
	
	private void downloadContent(String uuid, HttpServletResponse response, 
			ArrayList<TipoDoc> list) throws Exception {
		Documento doc = documentManager.getEntity(uuid);
		if(doc == null) {
			throw new BadRequestException("documento non trovato");
		}
		if(list != null) {
			checkTipologia(doc.getTipo(), list);
		}
		try {
			File file = documentManager.loadFile(doc.getUuid());
			response.setContentType(doc.getFormatoDocumento());
			response.setHeader("Content-Disposition", "attachment; filename=\"" + doc.getNomeFile() + "\"");
			response.getOutputStream().write(FileUtils.readFileToByteArray(file));
		} catch (FileNotFoundException e) {
			throw new BadRequestException("file documento non trovato");
		}			
	}
	
	private Documento uploadContent(String uuid, TipoDoc tipo, MultipartFile data, 
			HttpServletRequest request, ASLUser user, ArrayList<TipoDoc> list) throws Exception {
		if(list != null) {
			checkTipologia(tipo, list);
		}
		try {
			Documento doc = documentManager.addDocumentToRisorsa(uuid, tipo, data, request);
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
	
	private boolean removeDocument(String uuid, HttpServletRequest request, ASLUser user, 
			ArrayList<TipoDoc> list) throws Exception {
		Documento doc = documentManager.getEntity(uuid);
		if(doc == null) {
			throw new BadRequestException("documento non trovato");
		}
		if(list != null) {
			checkTipologia(doc.getTipo(), list);
		}
		try {
			boolean result = documentManager.removeDocument(uuid);
			AuditEntry audit = new AuditEntry(request.getMethod(), Documento.class, uuid, user, new Object(){});
			auditManager.save(audit);			
			return result;
		} catch (Exception e) {
			throw new BadRequestException("file documento non trovato");
		}
	}
}
