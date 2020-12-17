package it.smartcommunitylab.cartella.asl.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;

import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.model.RegistrazioneEnte;
import it.smartcommunitylab.cartella.asl.model.RegistrazioneEnte.Stato;
import it.smartcommunitylab.cartella.asl.model.report.RegistrazioneEnteReport;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;
import it.smartcommunitylab.cartella.asl.model.users.ASLUserRole;
import it.smartcommunitylab.cartella.asl.repository.RegistrazioneEnteRepository;

public class RegistrazioneEnteManager extends DataEntityManager {
	@Autowired
	RegistrazioneEnteRepository registrazioneEnteRepository;
	@Autowired
	ASLUserManager userManager;
	
	public RegistrazioneEnte getRegistrazioneByToken(String token) throws Exception {
		Optional<RegistrazioneEnte> reg = registrazioneEnteRepository.findOneByToken(token);
		if(reg.isPresent()) {
			return reg.get();
		} else {
			throw new BadRequestException("token non trovato");
		}
	}
	
	public RegistrazioneEnte confermaRichiesta(String token) throws Exception {
		Optional<RegistrazioneEnte> reg = registrazioneEnteRepository.findOneByToken(token);
		if(reg.isPresent()) {
			if(Stato.cancellato.equals(reg.get().getStato())) {
				throw new BadRequestException("richiesta registrazione cancellata");
			}
			if(Stato.confermato.equals(reg.get().getStato())) {
				throw new BadRequestException("richiesta registrazione già confermata");
			}
			if(Stato.scaduto.equals(reg.get().getStato())) {
				throw new BadRequestException("richiesta registrazione scaduta");
			}
			RegistrazioneEnte registrazioneEnte = reg.get();
			LocalDate today = LocalDate.now();
			if(today.isAfter(reg.get().getDataInvito().plusDays(5))) {
				registrazioneEnte.setStato(Stato.scaduto);
				registrazioneEnteRepository.save(registrazioneEnte);
				throw new BadRequestException("richiesta registrazione scaduta");
			}
			ASLUser user = new ASLUser();
			user.setEmail(registrazioneEnte.getEmail());
			user = userManager.getExistingASLUser(user);
			if(user == null) {
				user = new ASLUser();
				user.setEmail(registrazioneEnte.getEmail());
				user = userManager.createASLUser(user);
				registrazioneEnte.setUserId(user.getId());
				userManager.addASLUserRole(user.getId(), ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, registrazioneEnte.getAziendaId());
				registrazioneEnte.setOwnerId(new Long(-1));
				registrazioneEnte.setNominativoInvito("Sistema");
				registrazioneEnte.setStato(Stato.confermato);
				registrazioneEnteRepository.save(registrazioneEnte);
			}
			return registrazioneEnte;
		} else {
			throw new BadRequestException("token non trovato");
		}
	}
	
	public RegistrazioneEnte cancellaRuoloReferenteAzienda(Long registrazioneId) throws Exception {
		Optional<RegistrazioneEnte> optional = registrazioneEnteRepository.findById(registrazioneId);
		if(!optional.isPresent()) {
			throw new BadRequestException("registrazione non trovata");
		}
		RegistrazioneEnte reg = optional.get();
		if(!ASLRole.REFERENTE_AZIENDA.equals(reg.getStato())) {
			throw new BadRequestException("ruolo non corrispondente");
		}
		ASLUser owner = userManager.getASLUserById(reg.getOwnerId());
		if(owner == null) {
			throw new BadRequestException("owner registrazione non trovato");
		}
		ASLUserRole userRole = userManager.findASLUserRole(owner.getId(), 
				ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, reg.getAziendaId());
		if(userRole == null) {
			userRole = userManager.findASLUserRole(owner.getId(), 
					ASLRole.REFERENTE_AZIENDA, reg.getAziendaId());
		}
		if(userRole == null) {
			throw new BadRequestException("utente non autorizzato");
		}
		userManager.deleteASLUserRole(reg.getUserId(), reg.getRole(), reg.getAziendaId());
		registrazioneEnteRepository.delete(reg);
		return reg;
	}
	
	@SuppressWarnings("unchecked")
	public List<RegistrazioneEnteReport> getRuoliByEnte(String enteId) throws Exception {
		StringBuilder sb = new StringBuilder("SELECT reg, u FROM RegistrazioneEnte reg, ASLUser u");
		sb.append(" WHERE reg.userId=u.id AND reg.aziendaId=(:enteId) ORDER BY reg.dataInvito DESC");

		Query query = em.createQuery(sb.toString());
		query.setParameter("enteId", enteId);
		List<Object[]> result = query.getResultList();
		
		List<RegistrazioneEnteReport> list = new ArrayList<>();
		for (Object[] obj : result) {
			RegistrazioneEnte reg = (RegistrazioneEnte) obj[0];
			ASLUser user = (ASLUser) obj[1];
			RegistrazioneEnteReport report = new RegistrazioneEnteReport(reg, user);
			list.add(report);
		}
		return list;
	}
}
