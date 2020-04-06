package it.smartcommunitylab.cartella.asl.controller;

import java.sql.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.cartella.asl.model.Consenso;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;
import it.smartcommunitylab.cartella.asl.repository.ConsensoRepository;
import it.smartcommunitylab.cartella.asl.security.AACConnector;
import it.smartcommunitylab.cartella.asl.util.Utils;

@RestController
public class ConsensoController implements AslController {

	@Autowired
	private ConsensoRepository consensoRepository;

	@Autowired
	private AACConnector aac;

	@RequestMapping(value = "/api/consent/add", method = RequestMethod.PUT)
	public @ResponseBody Consenso addAuthorization(HttpServletRequest request) throws Exception {

		ASLUser user = aac.getASLUser(request);

		Date date = new Date(System.currentTimeMillis());
		Consenso result = new Consenso();

		if (Utils.isNotEmpty(user.getCf())) {
			result.setCf(user.getCf());
		}

		if (Utils.isNotEmpty(user.getEmail())) {
			result.setEmail(user.getEmail());
		}

		result.setAuthorized(Boolean.TRUE);
		result.setCreationDate(date);
		consensoRepository.save(result);

		if (logger.isInfoEnabled()) {
			logger.info(String.format("addAuthorization[%s]", result.getId()));
		}

		return result;
	}

}
