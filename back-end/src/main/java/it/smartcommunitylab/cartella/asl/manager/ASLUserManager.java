package it.smartcommunitylab.cartella.asl.manager;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;

import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.model.Azienda;
import it.smartcommunitylab.cartella.asl.model.Consenso;
import it.smartcommunitylab.cartella.asl.model.Istituzione;
import it.smartcommunitylab.cartella.asl.model.Studente;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;
import it.smartcommunitylab.cartella.asl.model.users.ASLUserRole;
import it.smartcommunitylab.cartella.asl.repository.ASLUserRepository;
import it.smartcommunitylab.cartella.asl.repository.ASLUserRoleRepository;
import it.smartcommunitylab.cartella.asl.repository.AziendaRepository;
import it.smartcommunitylab.cartella.asl.repository.ConsensoRepository;
import it.smartcommunitylab.cartella.asl.repository.IstituzioneRepository;
import it.smartcommunitylab.cartella.asl.repository.StudenteRepository;
import it.smartcommunitylab.cartella.asl.util.ErrorLabelManager;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Repository
@Transactional
public class ASLUserManager extends DataEntityManager {
	@Autowired
	private StudenteRepository studenteRepository;		
	@Autowired
	private AziendaRepository aziendaRepository;	
	@Autowired
	private ConsensoRepository consensoRepository;
	@Autowired
	private ASLUserRepository userRepository;
	@Autowired
	private ASLUserRoleRepository roleRepository;
	@Autowired
	private IstituzioneRepository istituzioneRepository;
	@Autowired
	private ErrorLabelManager errorLabelManager;

	public void completeASLUser(ASLUser user) {
		Map<String, Studente> studenti = Maps.newTreeMap();
		Map<String, Azienda> aziende = Maps.newTreeMap();
		Map<String, Istituzione> istituti = Maps.newTreeMap();
		List<ASLUserRole> roles = roleRepository.findByUserId(user.getId());
		
		for (ASLUserRole role: roles) {
			if (ASLRole.STUDENTE.equals(role.getRole())) {
				Studente studente = studenteRepository.getOne(role.getDomainId());
				if (studente != null) {
					studenti.put(studente.getId(), studente);
				}
			}
			if (ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA.equals(role.getRole()) || ASLRole.REFERENTE_AZIENDA.equals(role.getRole())) {
				Azienda azienda = aziendaRepository.getOne(role.getDomainId());
				if (azienda != null) {
					aziende.put(azienda.getId(), azienda);
				}
			}
			if (ASLRole.DIRIGENTE_SCOLASTICO.equals(role.getRole()) 
				|| ASLRole.FUNZIONE_STRUMENTALE.equals(role.getRole())
				|| ASLRole.TUTOR_SCOLASTICO.equals(role.getRole())) {
				Istituzione istituto = istituzioneRepository.getOne(role.getDomainId());
				if (istituto != null) {
					istituti.put(istituto.getId(), istituto);
				}
			}		
		}
		
		Consenso consenso = null;
		if (Utils.isNotEmpty(user.getEmail())) {
			consenso = consensoRepository.findByEmail(user.getEmail());	
		} else if (Utils.isNotEmpty(user.getCf())) {
			consenso = consensoRepository.findByCF(user.getCf());
		}
		
		if (consenso != null) {
			user.setAuthorized(consenso.getAuthorized());
		}		
		
		user.setAziende(aziende);
		user.setIstituti(istituti);
		user.setStudenti(studenti);
		user.getRoles().addAll(roles);
	}
	
	public ASLUser createASLUser(ASLUser user) {
		user.setCf(user.getCf().trim().toUpperCase());
		user.setEmail(user.getEmail().trim().toLowerCase());
		ASLUser old = getExistingASLUser(user);
		if (old != null) {
			user.setId(old.getId());
		}
		userRepository.save(user);
//		user.getRoles().forEach(role -> {
//			role.setUserId(user.getId());
//			roleRepository.save(role);
//		});
		return user;
	}
	
	public ASLUser getExistingASLUser(ASLUser user) {
		ASLUser old = userRepository.findByCfOrEmail(user.getCf(), user.getEmail());
		return old;
	}
	
	public ASLUser getExistingASLUser(String email) {
		ASLUser old = userRepository.findByEmail(email);
		return old;
	}
	
	public ASLUser getASLUserByCf(String cf) {
		ASLUser old = userRepository.findByCf(cf);
		return old;		
	}
	
	public ASLUser getASLUser(long id) {
		ASLUser user = userRepository.getOne(id);
		if (user != null) {
			completeASLUser(user);
		}
		return user;
	}
	
	public ASLUser getASLUserById(Long userId) {
		Optional<ASLUser> userOp = userRepository.findById(userId);
		if(userOp.isPresent()) {
			return userOp.get();
		}
		return null;
	}
	
	public ASLUserRole findASLUserRole(Long userId, ASLRole role, String domainId) {
		Optional<ASLUserRole> userRoleOp = roleRepository.findRole(userId, role, domainId);
		if(userRoleOp.isPresent()) {
			return userRoleOp.get();
		}
		return null;
	}
	
	public void updateASLUser(ASLUser user) {
		user.setCf(user.getCf().trim().toUpperCase());
		user.setEmail(user.getEmail().trim().toLowerCase());
		userRepository.update(user);
	}
	
	public void deleteASLUser(long id) {
		List<ASLUserRole> roles = roleRepository.findByUserId(id);
		roles.forEach(role -> roleRepository.deleteById(role.getId()));
		userRepository.deleteById(id);
	}
	
	public ASLUserRole addASLUserRole(Long userId, ASLRole role, String domainId) {
		Optional<ASLUserRole> userRoleOp = roleRepository.findRole(userId, role, domainId);
		if(userRoleOp.isPresent()) {
			return userRoleOp.get();
		}
		ASLUserRole userRole = new ASLUserRole(role, domainId, userId);
		roleRepository.save(userRole);
		return userRole;
	}
	
	public ASLUserRole deleteASLUserRole(Long userId, ASLRole role, String domainId) {
		Optional<ASLUserRole> userRoleOp = roleRepository.findRole(userId, role, domainId);
		if(userRoleOp.isPresent()) {
			roleRepository.delete(userRoleOp.get());
			return userRoleOp.get();
		}
		return null;
	}
	
	public ASLUser updateASLUserRoles(Long id, ASLRole role, String domainId) throws BadRequestException {
		ASLUser user = userRepository.getOne(id);
		if (user == null) {
			throw new BadRequestException(errorLabelManager.get("api.access.error"));
		}
		ASLUserRole userRole = new ASLUserRole(role, domainId, user.getId());
		roleRepository.save(userRole);
		completeASLUser(user);
		return user;
	}		
	
	public ASLUser deleteASLUserRoles(Long id, ASLRole role, String oldId) throws BadRequestException {
		ASLUser user = userRepository.getOne(id);
		if (user == null) {
			throw new BadRequestException(errorLabelManager.get("api.access.error"));
		}
		List<ASLUserRole> roles = roleRepository.findByUserId(id);
		boolean haveDomainId = Utils.isNotEmpty(oldId);
		roles.forEach(userRole -> {
			if(haveDomainId) {
				if(role.equals(userRole.getRole()) && oldId.contentEquals(userRole.getDomainId())) {
					roleRepository.delete(userRole);
				}
			} else {
				if(role.equals(userRole.getRole())) {
					roleRepository.delete(userRole);
				}				
			}
		});
		completeASLUser(user);
		return user;
	}	
	
	public Page<ASLUser> findASLUsers(ASLRole role, String text, ASLRole userRole, String userDomainId, Pageable pageRequest) {
		StringBuilder sb = new StringBuilder("SELECT DISTINCT u FROM ASLUser u LEFT JOIN ASLUserRole r ON r.userId=u.id");
		if (role != null) {
			sb.append(" AND r.role=(:role)");
		}
		if (Utils.isNotEmpty(userDomainId)) {
			sb.append(" AND r.domainId=(:domainId)");
		}
		if (Utils.isNotEmpty(text)) {
			sb.append(
					" AND ((UPPER(u.name) LIKE (:text)) OR (UPPER(u.surname) LIKE (:text)) OR (UPPER(u.email) LIKE (:text)) "
					+ "OR (UPPER(u.cf) LIKE (:text)))");
		}
		sb.append(" ORDER BY u.email");

		String q = sb.toString();
		q = q.replaceFirst(" AND ", " WHERE ");

		TypedQuery<ASLUser> query = em.createQuery(q, ASLUser.class);
		if (Utils.isNotEmpty(text)) {
			query.setParameter("text", "%" + text.trim().toUpperCase() + "%");
		}
		if (role != null) {
			query.setParameter("role", role);
		}
		if (Utils.isNotEmpty(userDomainId)) {
			query.setParameter("domainId", userDomainId);
		}
		query.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
		query.setMaxResults(pageRequest.getPageSize());

		List<ASLUser> result = query.getResultList();
		result.forEach(user -> {
			completeASLUser(user);
		});

		/*if (role != null) {
			if (ASLRole.ADMIN.equals(userRole)) {
				result.removeIf(x -> !x.getRoles().stream().filter(y -> role.equals(y.getRole())).findFirst().isPresent());
				if (userDomainId != null) {
					result.removeIf(x -> !x.getRoles().stream().filter(y -> userDomainId.equals(y.getDomainId())).findFirst().isPresent());	
				}
			} else {
				ASLUserRole resultRole = new ASLUserRole(role, userDomainId, null);
				result.removeIf(x -> !x.getRoles().contains(resultRole));
			}
		}*/

		Query cQuery = queryToCount(q.replaceAll("DISTINCT u","COUNT(DISTINCT u)"), query);
		long total = (Long) cQuery.getSingleResult();
		
		Page<ASLUser> page = new PageImpl<ASLUser>(result, pageRequest, total);
		return page;
	}

}
