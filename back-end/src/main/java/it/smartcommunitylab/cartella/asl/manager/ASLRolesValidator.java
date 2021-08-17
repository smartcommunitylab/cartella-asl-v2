package it.smartcommunitylab.cartella.asl.manager;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import it.smartcommunitylab.cartella.asl.exception.UnauthorizedException;
import it.smartcommunitylab.cartella.asl.model.users.ASLAuthCheck;
import it.smartcommunitylab.cartella.asl.model.users.ASLAuthError;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;
import it.smartcommunitylab.cartella.asl.model.users.ASLUserRole;
import it.smartcommunitylab.cartella.asl.security.AACConnector;
import it.smartcommunitylab.cartella.asl.util.ErrorLabelManager;

@Component
public class ASLRolesValidator {

	@Autowired
	private AACConnector aac;
	
	@Autowired
	private ErrorLabelManager errorLabelManager;		
	
	private static Log logger = LogFactory.getLog(ASLRolesValidator.class);

	public ASLUser getUser(HttpServletRequest request) throws UnauthorizedException {
		ASLUser user = aac.getASLUser(request);
		return user;
	}	
	
	public void checkRoles(ASLUser user, Set<ASLRole> roles) throws UnauthorizedException {
//		if (Sets.intersection(user.getRoles(), roles).isEmpty()) {
		if (!user.getRoles().stream().filter(x -> roles.contains(x.getRole())).findFirst().isPresent()) {
			throw new UnauthorizedException("Unauthorized: " + ASLAuthError.WRONG_ROLE);
		}
	}		
	
	public ASLUser checkRoles(HttpServletRequest request, Set<ASLRole> roles) throws UnauthorizedException {
		ASLUser user = getUser(request);
		boolean result = false;
		for(ASLRole role : roles) {
			if(hasRole(user, role)) {
				result = true;
			}
		}
		if(!result) {
			throw new UnauthorizedException("Unauthorized: " + ASLAuthError.WRONG_ROLE);
		}
		return user;
	}
	
	public ASLUser checkRole(HttpServletRequest request, ASLRole role) throws UnauthorizedException {
		ASLUser user = hasRole(request, role);
		if (user == null) {
			throw new UnauthorizedException("Unauthorized: " + ASLAuthError.WRONG_ROLE);
		}
		return user;
	}	
	
	public boolean hasRole(ASLUser user, ASLRole role) {
		return user.getRoles().stream().filter(x -> role.equals(x.getRole())).findFirst().isPresent();
	}
	
	public boolean hasRole(ASLUser user, ASLRole role, String domainId) {
		return user.getRoles().stream().filter(x -> {
			return x.getRole().equals(role) && x.getDomainId().equals(domainId);  
		}).findFirst().isPresent();
	}
	
	public boolean hasOnlyRole(HttpServletRequest request, ASLRole role) throws UnauthorizedException {
		ASLUser user = aac.getASLUser(request);
		return user.getRoles().size() == 1 && user.getRoles().stream().map(x -> x.getRole()).collect(Collectors.toSet()).contains(role);
	}	
	
	private ASLUser hasRole(HttpServletRequest request, ASLRole role) throws UnauthorizedException { // ...
		ASLUser user = aac.getASLUser(request);
		return user.getRoles().stream().map(x -> x.getRole()).collect(Collectors.toSet()).contains(role) ? user : null;
	}
	
//	private boolean hasRoles(ASLUser user, Set<ASLRole> roles) throws UnauthorizedException { // OK
//		return !Sets.intersection(user.getRoles(), roles).isEmpty();
//		
//	}	
//	
//	public boolean hasRole(HttpServletRequest request,  Set<ASLRole> roles) throws UnauthorizedException {
//		ASLUser user = aac.getASLUser(request);
//		return hasRoles(user, roles);
//	}	
	
//	public boolean hasAnyRole(HttpServletRequest request) throws UnauthorizedException {
//		ASLUser user = aac.getASLUser(request);
//		return !user.getRoles().isEmpty();
//	}	
//	

	public ASLUser validate(HttpServletRequest request, ASLAuthCheck check) throws UnauthorizedException {
		ASLUser user = aac.getASLUser(request);
		validate(user, check);
		return user;
	}	
	
	private void validate(ASLUser user, ASLAuthCheck check) throws UnauthorizedException {
		ASLAuthError error = validateUser(user, check);
		if (error != ASLAuthError.OK) {
			logger.error("User '" + user.getEmail() + "' unauthorized: " + error);
//			throw new UnauthorizedException("Unauthorized: " + buildUnauthorizedString(check, error));
			throw new UnauthorizedException(errorLabelManager.get("api.access.error") + buildUnauthorizedString(check, error));
		}
	}

	public ASLUser validate(HttpServletRequest request, Collection<ASLAuthCheck> checks) throws UnauthorizedException {
		ASLUser user = aac.getASLUser(request);
		validate(user, checks);
		return user;
	}
	
	private void validate(ASLUser user, Collection<ASLAuthCheck> checks) throws UnauthorizedException {
		Map<ASLAuthCheck, ASLAuthError> errors = Maps.newHashMap();
		for (ASLAuthCheck check: checks) {
			ASLAuthError error = validateUser(user, check);
			errors.put(check, error);
		}
		if (!errors.containsValue(ASLAuthError.OK)) {
			logger.error("User '" + user.getEmail() + "' unauthorized: " + Joiner.on(", ").join(buildUnauthorizedString(errors)));
			throw new UnauthorizedException(errorLabelManager.get("api.access.error") + Joiner.on(", ").join(Sets.newHashSet(errors.values())));
		}
	}	
	
	
	private List<String> buildUnauthorizedString(Map<ASLAuthCheck, ASLAuthError> errors) {
		return errors.keySet().stream().map(x -> buildUnauthorizedString(x,errors.get(x))).collect(Collectors.toList());
	}	
	
	private String buildUnauthorizedString(ASLAuthCheck check, ASLAuthError error) {
		return error + " for " + check;
	}
	
//	private void validate(HttpServletRequest request, List<ASLAuthCheck> checks) throws UnauthorizedException {
//		ASLUser user = aac.getASLUser(request);
//		validate(user, checks);
//	}

	private ASLAuthError validateUser(ASLUser user, ASLAuthCheck check) throws UnauthorizedException {
		ASLAuthError error = null;

		if (user == null) {
			error = ASLAuthError.USER_NOT_FOUND;
		} else if (check.getEntityIds().size() == 1 && check.getEntityIds().contains(null)){
			error = ASLAuthError.MISSING_ENTITY_ID;
		} else {
			if (!user.getRoles().stream().filter(x -> check.getRole().equals(x.getRole())).findAny().isPresent()) {
				error = ASLAuthError.WRONG_ROLE;
//			if (!user.getRoles().contains(check.getRole())) {
//				error = ASLAuthError.WRONG_ROLE;
			} else {
					error = ASLAuthError.WRONG_ENTITY_ID;
					for (ASLUserRole role: user.getRoles()) {
						if (!check.getRole().equals(role.getRole())) {
							continue;
						}
						if (check.getEntityIds().contains(role.getDomainId())) {
							error = ASLAuthError.OK;
							break;
						}
					}
					
//					switch (check.getRole()) {
//					case STUDENTE:
//						if (Collections.disjoint(user.getStudentiId(), check.getEntityIds())) {
//							error = ASLAuthError.WRONG_ENTITY_ID;
//						}
//						break;
//					case REFERENTE_AZIENDA:
//						if (Collections.disjoint(user.getAziendeId(), check.getEntityIds())) {
//							error = ASLAuthError.WRONG_ENTITY_ID;
//						}
//						break;
//					case DIRIGENTE_SCOLASTICO:
//						if (Collections.disjoint(user.getIstitutiId(), check.getEntityIds())) {
//							error = ASLAuthError.WRONG_ENTITY_ID;
//						}
//						break;
//					}
				}
			}

		return error;
	}	
	
	public void checkDeclaredUserRole(ASLUser admin, ASLRole userRole) throws UnauthorizedException {
		if (!hasRole(admin, ASLRole.ADMIN)) {
			if (!hasRole(admin, userRole)) {
				throw new UnauthorizedException(errorLabelManager.get("wrong.user.role"));
			}
		}
	}
	
	public void checkUserRolesCompatibility(ASLRole userRole, String userDomainId, ASLRole role) throws UnauthorizedException {
			if (userDomainId == null) {
				throw new UnauthorizedException(errorLabelManager.get("missing.domainId"));
			} else if (userRole == null) {
				throw new UnauthorizedException(errorLabelManager.get("missing.user.role"));
			}			
			if (ASLRole.STUDENTE.equals(userRole) || ASLRole.FUNZIONE_STRUMENTALE.equals(userRole) || ASLRole.REFERENTE_AZIENDA.equals(userRole)) {
				throw new UnauthorizedException(errorLabelManager.get("wrong.user.role"));
			}
			if (ASLRole.DIRIGENTE_SCOLASTICO.equals(userRole)) {
				if (!ASLRole.DIRIGENTE_SCOLASTICO.equals(role) && !ASLRole.FUNZIONE_STRUMENTALE.equals(role) && !ASLRole.STUDENTE.equals(role)) {
					throw new UnauthorizedException(errorLabelManager.get("wrong.user.role"));
				}
			}
			if (ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA.equals(userRole)) {
				if (!ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA.equals(role) && !ASLRole.REFERENTE_AZIENDA.equals(role)) {
					throw new UnauthorizedException(errorLabelManager.get("wrong.user.role"));
				}
			}
//			if (!userDomainId.equals(aslUserRole.getDomainId())) {
//				throw new UnauthorizedException(errorLabelManager.get("wrong.domainId"));
//			}
	}
	
//	public void validate(HttpServletRequest request, ASLAuthCheck check) throws UnauthorizedException {
//		ASLUser user = aac.getASLUser(request);
//		validate(user, Lists.newArrayList(check));
//	}
//
//	private void validate(HttpServletRequest request, List<ASLAuthCheck> checks) throws UnauthorizedException {
//		ASLUser user = aac.getASLUser(request);
//		validate(user, checks);
//	}
//
//	private void validate(ASLUser user, List<ASLAuthCheck> checks) throws UnauthorizedException {
//		ASLAuthError error = null;
//
//		if (user == null) {
//			error = ASLAuthError.USER_NOT_FOUND;
//		} else {
//			if (Collections.disjoint(user.getRoles(), checks.stream().map(x -> x.getRole()).collect(Collectors.toSet()))) {
//				error = ASLAuthError.WRONG_ROLE;
//			} else {
//
//				for (ASLAuthCheck check : checks) {
//
//					switch (check.getRole()) {
//					case STUDENTE:
//						if (Collections.disjoint(user.getStudentiId(), check.getEntityIds())) {
//							error = ASLAuthError.WRONG_ENTITY_ID;
//							continue;
//						}
//						return;
//					case REFERENTE_AZIENDA:
//						if (Collections.disjoint(user.getAziendeId(), check.getEntityIds())) {
//							error = ASLAuthError.WRONG_ENTITY_ID;
//							continue;
//						}
//						return;
//					case DIRIGENTE_SCOLASTICO:
//						if (Collections.disjoint(user.getIstitutiId(), check.getEntityIds())) {
//							error = ASLAuthError.WRONG_ENTITY_ID;
//							continue;
//						}
//						return;
//					}
//				}
//			}
//		}
//
//		if (error != null) {
//			throw new UnauthorizedException("Unauthorized: " + error);
//		}
//
//	}

}
