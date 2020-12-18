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
import it.smartcommunitylab.cartella.asl.util.Utils;

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
	
	public RegistrazioneEnte creaRichiestaRegistrazione(String istitutoId, String enteId, String email) throws Exception {
		Optional<RegistrazioneEnte> regOp = registrazioneEnteRepository.findOneByAziendaId(enteId);
		if(regOp.isPresent()) {
			throw new BadRequestException("richiesta registrazione per questo ente già presente");
		}
		RegistrazioneEnte reg = new RegistrazioneEnte();
		reg.setAziendaId(enteId);
		reg.setIstitutoId(istitutoId);
		reg.setOwnerId(new Long(-1));
		reg.setNominativoInvito("Sistema");
		reg.setToken(Utils.getUUID());
		reg.setDataInvito(LocalDate.now());
		reg.setEmail(email);
		reg.setStato(Stato.inviato);
		//TODO send email
		registrazioneEnteRepository.save(reg);
		return reg;
	}
	
	public RegistrazioneEnte confermaRichiestaRegistrazione(String token) throws Exception {
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
			ASLUser user = userManager.getExistingASLUser(null, registrazioneEnte.getEmail());
			if(user == null) {
				user = new ASLUser();
				user.setEmail(registrazioneEnte.getEmail());
				user = userManager.createASLUser(user);
			}
			registrazioneEnte.setUserId(user.getId());
			userManager.addASLUserRole(user.getId(), ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, registrazioneEnte.getAziendaId());
			registrazioneEnte.setStato(Stato.confermato);
			registrazioneEnteRepository.save(registrazioneEnte);
			return registrazioneEnte;
		} else {
			throw new BadRequestException("token non trovato");
		}
	}
	
	public RegistrazioneEnte aggiungiRuoloReferenteAzienda(String enteId, String nome, 
			String cognome, String email, String cf, Long ownerId) throws Exception {
		ASLUser owner = userManager.getASLUserById(ownerId);
		if(owner == null) {
			throw new BadRequestException("gestore non trovato");
		}
		ASLUserRole ownerRole = userManager.findASLUserRole(ownerId, 
				ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, enteId);
		if(ownerRole == null) {
			ownerRole = userManager.findASLUserRole(ownerId, 
					ASLRole.REFERENTE_AZIENDA, enteId);
		}
		if(ownerRole == null) {
			throw new BadRequestException("gestore non autorizzato");
		}
		ASLUser user = userManager.getExistingASLUser(cf, email);
		if(user != null) {
			if(!user.getEmail().equals(email)) {
				throw new BadRequestException("codice fiscale già presente con una diversa email");
			}
			user.setCf(cf);
			user.setName(nome);
			user.setSurname(cognome);
			userManager.updateASLUser(user);
		} else {
			user = new ASLUser();
			user.setEmail(email);
			user.setCf(cf);
			user.setName(nome);
			user.setSurname(cognome);
			userManager.createASLUser(user);
		}
		ASLUserRole userRole = userManager.findASLUserRole(user.getId(), 
				ASLRole.REFERENTE_AZIENDA, enteId);
		if(userRole == null) {
			userRole = userManager.addASLUserRole(user.getId(), 
					ASLRole.REFERENTE_AZIENDA, enteId);
		}		
		RegistrazioneEnte reg = new RegistrazioneEnte();
		reg.setUserId(user.getId());
		reg.setOwnerId(ownerId);
		reg.setDataInvito(LocalDate.now());
		reg.setDataAccettazione(LocalDate.now());
		reg.setEmail(email);
		reg.setNominativoInvito(owner.getName() + " " + owner.getSurname());
		reg.setToken(Utils.getUUID());
		reg.setStato(Stato.confermato);
		reg.setRole(ASLRole.REFERENTE_AZIENDA);
		registrazioneEnteRepository.save(reg);
		return reg;
	}
	
	public RegistrazioneEnte cancellaRuoloReferenteAzienda(Long registrazioneId) throws Exception {
		Optional<RegistrazioneEnte> optional = registrazioneEnteRepository.findById(registrazioneId);
		if(!optional.isPresent()) {
			throw new BadRequestException("registrazione non trovata");
		}
		RegistrazioneEnte reg = optional.get();
		if(!ASLRole.REFERENTE_AZIENDA.equals(reg.getRole())) {
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
