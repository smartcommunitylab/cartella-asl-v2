package it.smartcommunitylab.cartella.asl.controller;

import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Splitter;
import com.google.common.primitives.Doubles;

import it.smartcommunitylab.cartella.asl.exception.UnauthorizedException;
import it.smartcommunitylab.cartella.asl.manager.ASLRolesValidator;
import it.smartcommunitylab.cartella.asl.manager.ASLUserManager;
import it.smartcommunitylab.cartella.asl.manager.AuditManager;
import it.smartcommunitylab.cartella.asl.manager.AziendaManager;
import it.smartcommunitylab.cartella.asl.manager.IstituzioneManager;
import it.smartcommunitylab.cartella.asl.manager.StudenteManager;
import it.smartcommunitylab.cartella.asl.model.Azienda;
import it.smartcommunitylab.cartella.asl.model.Istituzione;
import it.smartcommunitylab.cartella.asl.model.Studente;
import it.smartcommunitylab.cartella.asl.model.audit.AuditEntry;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;
import it.smartcommunitylab.cartella.asl.model.users.ASLUserRole;
import it.smartcommunitylab.cartella.asl.security.AACConnector;
import it.smartcommunitylab.cartella.asl.util.ErrorLabelManager;


@Controller
public class ASLUserController implements AslController {

	@Autowired
	private ASLUserManager aslManager;
	@Autowired
	private AziendaManager aziendaManager;
	@Autowired
	private IstituzioneManager istituzioneManager;
	@Autowired
	private StudenteManager studenteManager;
	@Autowired
	private ASLRolesValidator usersValidator;		
	@Autowired
	private AACConnector aac;	
	@Autowired
	private ErrorLabelManager errorLabelManager;	
	@Autowired
	private AuditManager auditManager;	
	
	private static Log logger = LogFactory.getLog(ASLUserController.class);

	@GetMapping(value = "/api/profile")
	public @ResponseBody ASLUser getProfile(
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		ASLUser user = aac.getASLUser(request);
		aslManager.completeASLUser(user);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getProfile: %s", ((user != null) ? user.getEmail(): "null")));
		}
		return user;
	}	
	
	@PostMapping("/api/user")
	public @ResponseBody ASLUser createUser(
			@RequestParam(required = false) ASLRole userRole, 
			@RequestParam(required = false) String userDomainId, 
			@RequestBody ASLUser user, 
			HttpServletRequest request)
			throws Exception {
		ASLUser admin = usersValidator.getUser(request);
		if (userRole == null && usersValidator.hasRole(admin, ASLRole.ADMIN)) {
			userRole = ASLRole.ADMIN;
		}
		usersValidator.checkDeclaredUserRole(admin, userRole);
		
		checkNullId(user.getId());

		for (ASLUserRole aslUserRole : user.getRoles()) {
			ASLRole role = aslUserRole.getRole();
			if (!ASLRole.ADMIN.equals(userRole)) {
				usersValidator.checkUserRolesCompatibility(userRole, userDomainId, role);
				if (!userDomainId.equals(aslUserRole.getDomainId())) {
					throw new UnauthorizedException(errorLabelManager.get("wrong.domainId"));
				}
			}
		}

		ASLUser result = aslManager.createASLUser(user);

		if (result != null) {
			AuditEntry audit = new AuditEntry(request.getMethod(), ASLUser.class, result.getId(), admin, new Object(){});
			auditManager.save(audit);
		}

		return result;
	}
	
	@GetMapping(value = "/api/users")
	public @ResponseBody Page<ASLUser> getUsers(
			@RequestParam(required=false) ASLRole userRole, 
			@RequestParam(required=false) String userDomainId, 
			@RequestParam(required=false) ASLRole role, 
			@RequestParam(required=false) String nome, 
			@RequestParam(required=false) String cf,
			Pageable pageRequest,
			HttpServletRequest request) throws Exception {
		ASLUser admin = usersValidator.getUser(request);
		if (userRole == null && usersValidator.hasRole(admin, ASLRole.ADMIN)) {
			userRole = ASLRole.ADMIN;
		}		
		usersValidator.checkDeclaredUserRole(admin, userRole);
		if (!ASLRole.ADMIN.equals(userRole)) {
			usersValidator.checkUserRolesCompatibility(userRole, userDomainId, role);			
		}
		return aslManager.findASLUsers(role, nome, cf, userRole, userDomainId, pageRequest);
	}
	
	@GetMapping(value = "/api/user/{id}")
	public @ResponseBody ASLUser getUser(@PathVariable Long id, HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);		
		return aslManager.getASLUser(id);
	}	
	
	@PutMapping(value = "/api/user")
	public @ResponseBody void updateUser(
			@RequestParam(required=false) ASLRole userRole, 
			@RequestParam(required=false) String userDomainId, 
			@RequestBody ASLUser user, 
			HttpServletRequest request) throws Exception {
		ASLUser admin = usersValidator.getUser(request);
		if (userRole == null && usersValidator.hasRole(admin, ASLRole.ADMIN)) {
			userRole = ASLRole.ADMIN;
		}
		usersValidator.checkDeclaredUserRole(admin, userRole);		
		
		checkId(user.getId());
		
		ASLUser oldUser = aslManager.getASLUser(user.getId());
		if (oldUser != null) {
			for (ASLUserRole aslUserRole : oldUser.getRoles()) {
				ASLRole role = aslUserRole.getRole();
				if (user.getRoles() != null) {
					if (!ASLRole.ADMIN.equals(userRole)) {
						usersValidator.checkUserRolesCompatibility(userRole, userDomainId, role);
						if (!userDomainId.equals(aslUserRole.getDomainId())) {
							throw new UnauthorizedException(errorLabelManager.get("wrong.domainId"));
						}
					}
				}
			}

			aslManager.updateASLUser(user);

			AuditEntry audit = new AuditEntry(request.getMethod(), ASLUser.class, user.getId(), admin, new Object() {});
			auditManager.save(audit);
		}
	}	
	
	@DeleteMapping(value = "/api/user/{id}")
	public @ResponseBody void deleteUser(
			@PathVariable Long id, 
			@RequestParam(required = false) ASLRole userRole, 
			@RequestParam(required = false) String userDomainId, 
			HttpServletRequest request)
			throws Exception {
		ASLUser admin = usersValidator.getUser(request);
		if (userRole == null && usersValidator.hasRole(admin, ASLRole.ADMIN)) {
			userRole = ASLRole.ADMIN;
		}
		usersValidator.checkDeclaredUserRole(admin, userRole);

		ASLUser oldUser = aslManager.getASLUser(id);
		if (oldUser != null) {
			for (ASLUserRole aslUserRole : oldUser.getRoles()) {
				ASLRole role = aslUserRole.getRole();

				if (!ASLRole.ADMIN.equals(userRole)) {
					usersValidator.checkUserRolesCompatibility(userRole, userDomainId, role);
					if (!userDomainId.equals(aslUserRole.getDomainId())) {
						throw new UnauthorizedException(errorLabelManager.get("wrong.domainId"));
					}
				}
			}

			aslManager.deleteASLUser(id);

			AuditEntry audit = new AuditEntry(request.getMethod(), ASLUser.class, id, admin, new Object() {});
			auditManager.save(audit);
		}
	}	
	
	@PostMapping(value = "/api/user/{id}/role")
	public @ResponseBody ASLUser updateRole(
			@PathVariable Long id, 
			@RequestParam(required = false) ASLRole userRole,
			@RequestParam(required = false) String userDomainId, 
			@RequestParam(required = true) ASLRole role,
			@RequestParam(required = true) String newId, 
			HttpServletRequest request) throws Exception {
		ASLUser admin = usersValidator.getUser(request);
		if (userRole == null && usersValidator.hasRole(admin, ASLRole.ADMIN)) {
			userRole = ASLRole.ADMIN;
		}		
		
		if (!ASLRole.ADMIN.equals(userRole)) {
			usersValidator.checkUserRolesCompatibility(userRole, userDomainId, role);			
		}		
		
		ASLUser result = aslManager.updateASLUserRoles(id, role, newId);
		if (result != null) {
			AuditEntry audit = new AuditEntry(request.getMethod(), ASLUser.class, result.getId(), admin, new Object(){});
			auditManager.save(audit);
		}
		
		return result;
	}	
	
	@DeleteMapping(value = "/api/user/{id}/role")
	public @ResponseBody ASLUser deleteRole(
			@PathVariable Long id,  
			@RequestParam(required = false) ASLRole userRole, 
			@RequestParam(required = false) String userDomainId, 
			@RequestParam(required = true) ASLRole role, 
			@RequestParam(required = false) String oldId, 
			HttpServletRequest request) throws Exception {
		ASLUser admin = usersValidator.getUser(request);
		if (userRole == null && usersValidator.hasRole(admin, ASLRole.ADMIN)) {
			userRole = ASLRole.ADMIN;
		}		
		
		if (!ASLRole.ADMIN.equals(userRole)) {
			usersValidator.checkUserRolesCompatibility(userRole, userDomainId, role);			
		}			
		
		ASLUser result = aslManager.deleteASLUserRoles(id, role, oldId);
		
		if (result != null) {
			AuditEntry audit = new AuditEntry(request.getMethod(), ASLUser.class, result.getId(), admin, new Object(){});
			auditManager.save(audit);
		}
		
		return result;		
	}	
	
	@GetMapping(value = "/api/list/aziende")
	public @ResponseBody Page<Azienda> getAziende(
			@RequestParam(required=false) String pIva, 
			@RequestParam(required=false) String text, 
			@RequestParam(required = false) String coordinate, 
			@RequestParam(required = false) Integer raggio,
			Pageable pageRequest,
			HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		
		double[] coords = null;
		if (coordinate != null && !coordinate.isEmpty()) {
			coords = Doubles.toArray(Splitter.on(",").splitToList(coordinate).stream().map(x -> Double.parseDouble(x)).collect(Collectors.toList()));
		}		
		
		return aziendaManager.findAziende(pIva, text, coords, raggio, pageRequest);
	}	
	
	@GetMapping(value = "/api/list/istituti")
	public @ResponseBody Page<Istituzione> getIstituti(
			@RequestParam(required=false) String text, 
			@RequestParam(required = false) String coordinate, 
			@RequestParam(required = false) Integer raggio,
			Pageable pageRequest,
			HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		
		double[] coords = null;
		if (coordinate != null && !coordinate.isEmpty()) {
			coords = Doubles.toArray(Splitter.on(",").splitToList(coordinate).stream().map(x -> Double.parseDouble(x)).collect(Collectors.toList()));
		}		
		return istituzioneManager.findIstituti(text, coords, raggio, pageRequest);
	}	
	
	@GetMapping(value = "/api/list/studenti")
	public @ResponseBody Page<Studente> getStudenti(
			@RequestParam(required=false) String cf, 
			@RequestParam(required=false) String text,
			Pageable pageRequest,
			HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		return studenteManager.findStudenti(cf, text, pageRequest);
	}		
	
	
}
