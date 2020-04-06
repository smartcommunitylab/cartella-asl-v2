package it.smartcommunitylab.cartella.asl.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

import it.smartcommunitylab.cartella.asl.exception.ASLCustomException;
import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.manager.ASLRolesValidator;
import it.smartcommunitylab.cartella.asl.manager.IstituzioneManager;
import it.smartcommunitylab.cartella.asl.manager.QueriesManager;
import it.smartcommunitylab.cartella.asl.manager.StudenteManager;
import it.smartcommunitylab.cartella.asl.model.Istituzione;
import it.smartcommunitylab.cartella.asl.model.Studente;
import it.smartcommunitylab.cartella.asl.model.users.ASLAuthCheck;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;

@RestController
public class RegistrationController implements AslController {

	@Autowired
	private StudenteManager studenteManager;
	@Autowired
	private IstituzioneManager istituzioneManager;
	@Autowired
	private ASLRolesValidator usersValidator;		
	//TODO controller to delete?
	private static Log logger = LogFactory.getLog(RegistrationController.class);

//	@GetMapping("/api/registration/classi/{corsoId}/{istitutoId}/{annoScolastico}")
//	public @ResponseBody List<String> getClassi(@PathVariable String corsoId, @PathVariable String istitutoId, @PathVariable String annoScolastico, @RequestParam(required=false) String annoCorso, HttpServletRequest request) throws Exception {
//		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
//		
//		return aslManager.getClasses(corsoId, istitutoId, annoScolastico, annoCorso);
//	}		

//	@GetMapping("/api/studenti/profiles/{istitutoId}")
//	public @ResponseBody Page<Studente> getStudenti(@PathVariable String istitutoId, @RequestParam(required=false) String corsoId, @RequestParam(required=true) String annoScolastico, @RequestParam(required=false) String classe, @RequestParam(required=false) String nome, Pageable pageRequest, HttpServletRequest request) throws Exception {
//		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));
//		
//		return studenteManager.findStudentiPaged(istitutoId, corsoId, annoScolastico, classe, nome, pageRequest);
//	}
	
//	@GetMapping("/api/profile/istituto/{istitutoId}")
//	public @ResponseBody Istituzione getIstitutoProfile(
//			@PathVariable String istitutoId, 
//			HttpServletRequest request) throws Exception {
//		Istituzione istituto = istituzioneManager.getIstituto(istitutoId);
//		if(istituto == null) {
//			throw new BadRequestException("entity not found");
//		}
//		if (logger.isInfoEnabled()) {
//			logger.info(String.format("/profile/istituto/{%s}", istitutoId));
//		}
//		return istituto;
//	}
	
//	@GetMapping("/api/studenti/classe/{classe}/{corsoId}/{istitutoId}/{annoScolastico}/{annoCorso}")
//	public @ResponseBody Page<Studente> getStudentiByClasse(@PathVariable String classe, @PathVariable String corsoId, @PathVariable String istitutoId, @PathVariable String annoScolastico, @PathVariable Integer annoC, Pageable pageRequest, HttpServletRequest request) throws Exception {
//		usersValidator.validate(request, Lists.newArrayList(new ASLAuthCheck(ASLRole.DIRIGENTE_SCOLASTICO, istitutoId), new ASLAuthCheck(ASLRole.FUNZIONE_STRUMENTALE, istitutoId)));		
//		
//		return aslManager.findStudentiByClasse(classe, corsoId, istitutoId, annoScolastico, pageRequest);
//	}		
	
	
}
