package it.smartcommunitylab.cartella.asl.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.model.Azienda;
import it.smartcommunitylab.cartella.asl.model.Istituzione;
import it.smartcommunitylab.cartella.asl.model.RegistrazioneEnte;
import it.smartcommunitylab.cartella.asl.model.RegistrazioneEnte.Stato;
import it.smartcommunitylab.cartella.asl.model.report.RegistrazioneEnteReport;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;
import it.smartcommunitylab.cartella.asl.model.users.ASLUserRole;
import it.smartcommunitylab.cartella.asl.repository.AziendaRepository;
import it.smartcommunitylab.cartella.asl.repository.IstituzioneRepository;
import it.smartcommunitylab.cartella.asl.repository.RegistrazioneEnteRepository;
import it.smartcommunitylab.cartella.asl.services.MailService;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Repository
@Transactional
public class RegistrazioneEnteManager extends DataEntityManager {
	@Autowired
	RegistrazioneEnteRepository registrazioneEnteRepository;
	@Autowired
	IstituzioneRepository istituzioneRepository;
	@Autowired
	AziendaRepository aziendaRepository;
	@Autowired
	ASLUserManager userManager;
	@Autowired
	MailService mailService;
	
	long maxGiorni = 5;
	
	public RegistrazioneEnte getRegistrazioneByToken(String token) throws Exception {
		Optional<RegistrazioneEnte> reg = registrazioneEnteRepository.findOneByToken(token);
		if(reg.isPresent()) {
			return reg.get();
		} else {
			throw new BadRequestException("token non trovato");
		}
	}
	
	public RegistrazioneEnte creaRichiestaRegistrazione(String istitutoId, String enteId, String email) throws Exception {
		Optional<RegistrazioneEnte> regOp = registrazioneEnteRepository.findOneByAziendaIdAndRole(enteId, ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA);
		if(regOp.isPresent()) {
			RegistrazioneEnte reg = regOp.get();
			if(Stato.confermato.equals(reg.getStato())) {
				throw new BadRequestException("richiesta registrazione per questo ente già presente");
			}
			LocalDate today = LocalDate.now();
			if(today.isBefore(reg.getDataInvito().plusDays(maxGiorni))) {
				registrazioneEnteRepository.delete(reg);
				throw new BadRequestException("richiesta registrazione per questo ente già presente");
			}
			registrazioneEnteRepository.delete(reg);
		}
		Optional<Istituzione> istituto = istituzioneRepository.findById(istitutoId);
		Optional<Azienda> ente = aziendaRepository.findById(enteId);
		RegistrazioneEnte reg = new RegistrazioneEnte(); 
		reg.setAziendaId(enteId);
		reg.setIstitutoId(istitutoId);
		reg.setOwnerId(new Long(-1));
		reg.setNominativoInvito("Sistema");
		reg.setToken(Utils.getUUID());
		reg.setDataInvito(LocalDate.now());
		reg.setEmail(email);
		reg.setRole(ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA);
		reg.setStato(Stato.inviato);
		if(istituto.isPresent()) {
			reg.setNomeIstituto(istituto.get().getName());
		}
		if(ente.isPresent())  {
			reg.setNomeEnte(ente.get().getNome());
		}
		mailService.inviaRichiestaRegistrazione(reg);
		registrazioneEnteRepository.save(reg);
		return reg;
	}
	
	public RegistrazioneEnte annullaRichiestaRegistrazione(String istitutoId, Long registrazioneId) throws Exception {
		Optional<RegistrazioneEnte> optional = registrazioneEnteRepository.findById(registrazioneId);
		if(!optional.isPresent()) {
			throw new BadRequestException("registrazione non trovata");
		}
		RegistrazioneEnte reg = optional.get();
		if(!reg.getIstitutoId().equals(istitutoId)) {
			throw new BadRequestException("istituto non autorizzato");
		}
		if(!Stato.inviato.equals(reg.getStato())) {
			throw new BadRequestException("stato registrazione non compatibile");
		}
		registrazioneEnteRepository.delete(reg);
		return reg;
	}
	
	public RegistrazioneEnte confermaRichiestaRegistrazione(String token) throws Exception {
		Optional<RegistrazioneEnte> reg = registrazioneEnteRepository.findOneByToken(token);
		if(reg.isPresent()) {
			if(Stato.confermato.equals(reg.get().getStato())) {
				throw new BadRequestException("richiesta registrazione già confermata");
			}
			RegistrazioneEnte registrazioneEnte = reg.get();
			LocalDate today = LocalDate.now();
			if(today.isAfter(reg.get().getDataInvito().plusDays(maxGiorni))) {
				registrazioneEnteRepository.delete(registrazioneEnte);
				throw new BadRequestException("richiesta registrazione scaduta");
			}
			ASLUser user = userManager.getExistingASLUser(registrazioneEnte.getEmail());
			if(user == null) {
				user = new ASLUser();
				user.setEmail(registrazioneEnte.getEmail());
				user = userManager.createASLUser(user);
			}
			registrazioneEnte.setUserId(user.getId());
			userManager.addASLUserRole(user.getId(), ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, registrazioneEnte.getAziendaId());
			registrazioneEnte.setDataAccettazione(LocalDate.now());
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
		ASLUser user = userManager.getExistingASLUser(email);
		if(user != null) {
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
		Optional<Azienda> ente = aziendaRepository.findById(enteId);
		RegistrazioneEnte reg = new RegistrazioneEnte();
		reg.setUserId(user.getId());
		reg.setOwnerId(ownerId);
		reg.setAziendaId(enteId);
		reg.setDataInvito(LocalDate.now());
		reg.setDataAccettazione(LocalDate.now());
		reg.setEmail(email);
		reg.setNominativoInvito(owner.getName() + " " + owner.getSurname());
		reg.setToken(Utils.getUUID());
		reg.setStato(Stato.confermato);
		reg.setRole(ASLRole.REFERENTE_AZIENDA);
		if(ente.isPresent())  {
			reg.setNomeEnte(ente.get().getNome());
		}
		mailService.inviaRuoloReferenteAzienda(reg);
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
	
	public RegistrazioneEnte cancellaRuolo(Long registrazioneId) throws Exception {
		Optional<RegistrazioneEnte> optional = registrazioneEnteRepository.findById(registrazioneId);
		if(!optional.isPresent()) {
			throw new BadRequestException("registrazione non trovata");
		}
		RegistrazioneEnte reg = optional.get();
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
	
	public ASLUser aggiornaDatiOwnerAzienda(String enteId, String nome, 
			String cognome, String cf, Long ownerId) throws Exception {
		ASLUser owner = userManager.getASLUserById(ownerId);
		if(owner == null) {
			throw new BadRequestException("gestore non trovato");
		}		
		ASLUserRole userRole = userManager.findASLUserRole(ownerId, 
				ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA, enteId);
		if(userRole == null) {
			throw new BadRequestException("utente non autorizzato");
		}
		owner.setName(nome);
		owner.setSurname(cognome);
		owner.setCf(cf);
		userManager.updateASLUser(owner);
		return owner;
	}
	
	public ASLUser aggiornaDatiReferenteAzienda(String enteId, String nome, 
			String cognome, String cf, Long userId) throws Exception {
		ASLUser user = userManager.getASLUserById(userId);
		if(user == null) {
			throw new BadRequestException("gestore non trovato");
		}		
		ASLUserRole userRole = userManager.findASLUserRole(userId, 
				ASLRole.REFERENTE_AZIENDA, enteId);
		if(userRole == null) {
			throw new BadRequestException("utente non autorizzato");
		}
		user.setName(nome);
		user.setSurname(cognome);
		user.setCf(cf);
		userManager.updateASLUser(user);
		return user;
	}
	
	public RegistrazioneEnte getRichiestaRegistrazione(String enteId) {
		Optional<RegistrazioneEnte> regOp = registrazioneEnteRepository.findOneByAziendaIdAndRole(enteId, 
				ASLRole.LEGALE_RAPPRESENTANTE_AZIENDA);
		if(regOp.isPresent()) {
			RegistrazioneEnte reg = regOp.get();
			if(Stato.confermato.equals(reg.getStato())) {
				return reg;
			}
			LocalDate today = LocalDate.now();
			if(today.isBefore(reg.getDataInvito().plusDays(maxGiorni))) {
				return reg;
			}
		}
		return null;
	}
}
