package it.smartcommunitylab.cartella.asl.manager;

import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
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
import it.smartcommunitylab.cartella.asl.security.ASLUserDetails;
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
			if (ASLRole.DIRIGENTE_SCOLASTICO.equals(role.getRole()) || ASLRole.FUNZIONE_STRUMENTALE.equals(role.getRole())) {
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
		ASLUser old = getExistingASLUser(user);
		if (old != null) {
			user.setId(old.getId());
		}
		userRepository.save(user);
		user.getRoles().forEach(role -> {
			role.setUserId(user.getId());
			roleRepository.save(role);
		});
		return user;
	}
	
	public ASLUser getExistingASLUser(ASLUser user) {
		ASLUser old = userRepository.findByCfOrEmail(user.getCf(), user.getEmail());
		return old;
	}
	
	public ASLUser getASLUser(long id) {
		ASLUser user = userRepository.getOne(id);
		if (user != null) {
			completeASLUser(user);
		}
		return user;
	}
	
	public void updateASLUser(ASLUser user) {
		userRepository.update(user);
	}
	
	public void deleteASLUser(long id) {
		List<ASLUserRole> roles = roleRepository.findByUserId(id);
		roles.forEach(role -> roleRepository.deleteById(role.getId()));
		userRepository.deleteById(id);
	}	
	
	public ASLUser updateASLUserRoles(Long id, ASLRole role, String domainId) throws BadRequestException {
		ASLUser user = userRepository.getOne(id);
		if (user == null) {
			ASLUserDetails details = (ASLUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (details != null && details.getUser() != null) {
				throw new BadRequestException(String.format(errorLabelManager.get("user.notfound"), details.getUser().getName(), details.getUser().getSurname()));	
			}
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
			ASLUserDetails details = (ASLUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (details != null && details.getUser() != null) {
				throw new BadRequestException(String.format(errorLabelManager.get("user.notfound"), details.getUser().getName(), details.getUser().getSurname()));	
			}
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
	
	public Page<ASLUser> findASLUsers(ASLRole role, String nome, String cf, ASLRole userRole, String userDomainId, Pageable pageRequest) {
		StringBuilder sb = new StringBuilder(ASLUSERS);
//		if (role != null) {
//			sb.append(" JOIN u.roles r AND r = (:role) ");
//		}
		if (nome != null && !nome.isEmpty()) {
			sb.append(
					" AND (lower(u.name) LIKE (:nome) OR lower(u.surname) LIKE (:nome) OR lower(u.email) LIKE (:nome))");
		}
		if (cf != null && !cf.isEmpty()) {
			sb.append(" AND u.cf = (:cf) ");
		}

		sb.append(" ORDER BY u.email");

		String q = sb.toString();
		q = q.replaceFirst(" AND ", " WHERE ");

		TypedQuery<ASLUser> query = em.createQuery(q, ASLUser.class);

		if (nome != null && !nome.isEmpty()) {
			query.setParameter("nome", "%" + nome + "%");
		}
		if (cf != null) {
			query.setParameter("cf", cf);
		}

		List<ASLUser> result = query.getResultList();
		result.forEach(user -> {
			completeASLUser(user);
		});

		if (role != null) {
			if (ASLRole.ADMIN.equals(userRole)) {
				result.removeIf(x -> !x.getRoles().stream().filter(y -> role.equals(y.getRole())).findFirst().isPresent());
				if (userDomainId != null) {
					result.removeIf(x -> !x.getRoles().stream().filter(y -> userDomainId.equals(y.getDomainId())).findFirst().isPresent());	
				}
			} else {
				ASLUserRole resultRole = new ASLUserRole(role, userDomainId, null);
				result.removeIf(x -> !x.getRoles().contains(resultRole));
			}
		}

		long total = result.size();
		int from = pageRequest.getPageNumber() * pageRequest.getPageSize();
		int to = Math.min(result.size(), (pageRequest.getPageNumber() + 1) * pageRequest.getPageSize());
		if (to > from) {
			result = result.subList(from, to);
		} else {
			result = Lists.newArrayList();
		}

		Page<ASLUser> page = new PageImpl<ASLUser>(result, pageRequest, total);
		return page;
	}

}
