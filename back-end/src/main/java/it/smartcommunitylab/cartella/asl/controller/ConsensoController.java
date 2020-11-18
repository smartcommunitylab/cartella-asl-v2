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
		Consenso consenso = null;

		if (Utils.isNotEmpty(user.getEmail())) {
			consenso = consensoRepository.findByEmail(user.getEmail());
		}
		if(consenso == null) {
			if (Utils.isNotEmpty(user.getCf())) {
				consenso = consensoRepository.findByCF(user.getCf());
			}			
		}
		if(consenso == null) {
			consenso = new Consenso();
			consenso.setAuthorized(Boolean.TRUE);
			consenso.setCreationDate(date);
			consenso.setCf(user.getCf());
			consenso.setEmail(user.getEmail());
			consensoRepository.save(consenso);
		}

		if (logger.isInfoEnabled()) {
			logger.info(String.format("addAuthorization[%s]", consenso.getId()));
		}

		return consenso;
	}

}
