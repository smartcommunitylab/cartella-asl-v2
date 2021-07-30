package it.smartcommunitylab.cartella.asl.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.cartella.asl.manager.ASLRolesValidator;
import it.smartcommunitylab.cartella.asl.manager.CleanDataManager;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;

@RestController
public class CleanDataController implements AslController {
	private static Log logger = LogFactory.getLog(CleanDataController.class);
	
	@Autowired
	private ASLRolesValidator usersValidator;

	@Autowired
	CleanDataManager cleanDataManager;
	
	@RequestMapping(value = "/data/clean/istituto", method = RequestMethod.POST)
	public @ResponseBody String cancellaIstitutoNonAttivi(HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		logger.info("start cancellaIstitutoNonAttivi");
		cleanDataManager.cancellaIstitutoNonAttivi();
		logger.info("stop cancellaIstitutoNonAttivi");
		return "OK";
	}
	
	@RequestMapping(value = "/data/clean/tu", method = RequestMethod.POST)
	public @ResponseBody String cancellaTeachingUnitNonAttivi(HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		logger.info("start cancellaTeachingUnitNonAttivi");
		cleanDataManager.cancellaTeachingUnitNonAttivi();
		logger.info("stop cancellaTeachingUnitNonAttivi");
		return "OK";
	}
	
	@RequestMapping(value = "/data/clean/corso", method = RequestMethod.POST)
	public @ResponseBody String cancellaCorsoDiStudioNonAttivi(HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		logger.info("start cancellaCorsoDiStudioNonAttivi");
		cleanDataManager.cancellaCorsoDiStudioNonAttivi();
		logger.info("stop cancellaCorsoDiStudioNonAttivi");
		return "OK";
	}
	
	@RequestMapping(value = "/data/clean/registrazione", method = RequestMethod.POST)
	public @ResponseBody String cancellaRegistrazioneNonAttivi(HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		logger.info("start cancellaRegistrazioneNonAttivi");
		cleanDataManager.cancellaRegistrazioneNonAttivi();
		logger.info("stop cancellaRegistrazioneNonAttivi");
		return "OK";
	}
	
	@RequestMapping(value = "/data/clean/studente", method = RequestMethod.POST)
	public @ResponseBody String cancellaStudenteNonAttivo(HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		logger.info("start cancellaStudenteNonAttivo");
		cleanDataManager.cancellaStudenteNonAttivo();
		logger.info("stop cancellaStudenteNonAttivo");
		return "OK";
	}


}
