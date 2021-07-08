package it.smartcommunitylab.cartella.asl.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.model.AssociazioneDocentiClassi;
import it.smartcommunitylab.cartella.asl.model.ProfessoriClassi;
import it.smartcommunitylab.cartella.asl.model.ReferenteAlternanza;
import it.smartcommunitylab.cartella.asl.model.RegistrazioneDocente;
import it.smartcommunitylab.cartella.asl.model.report.ReportDocenteClasse;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;
import it.smartcommunitylab.cartella.asl.model.users.ASLUserRole;
import it.smartcommunitylab.cartella.asl.repository.AssociazioneDocentiClassiRepository;
import it.smartcommunitylab.cartella.asl.repository.ProfessoriClassiRepository;
import it.smartcommunitylab.cartella.asl.repository.ReferenteAlternanzaRepository;
import it.smartcommunitylab.cartella.asl.repository.RegistrazioneDocenteRepository;
import it.smartcommunitylab.cartella.asl.services.MailService;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Repository
@Transactional
public class RegistrazioneDocenteManager extends DataEntityManager {
  @Autowired
	ASLUserManager userManager;
	@Autowired
	MailService mailService;
  @Autowired
  ReferenteAlternanzaRepository referenteAlternanzaRepository;
  @Autowired
  RegistrazioneDocenteRepository registrazioneDocenteRepository;
  @Autowired
  ProfessoriClassiRepository professoriClassiRepository;
  @Autowired
  AssociazioneDocentiClassiRepository associazioneDocentiClassiRepository;

  public List<RegistrazioneDocente> getRegistrazioneDocente(String istitutoId) throws Exception {
    List<RegistrazioneDocente> list = registrazioneDocenteRepository.findByIstitutoId(istitutoId, Sort.by(Sort.Direction.ASC, "emailDocente"));
    list.forEach(rd -> updateClassiRegistrazioneDocente(rd));
    return list;
  }

  private List<String> getClassiAssociateRegistrazioneDocente(Long registrazioneDocenteId) {
    List<String> result = new ArrayList<>();
    List<AssociazioneDocentiClassi> classi = associazioneDocentiClassiRepository.findByRegistrazioneDocenteId(registrazioneDocenteId);
    for(AssociazioneDocentiClassi adc : classi) {
      result.add(adc.getClasse());
    }
    return result;
  }

  private void updateClassiRegistrazioneDocente(RegistrazioneDocente reg) {
    List<String> classiAssociate = getClassiAssociateRegistrazioneDocente(reg.getId());
    StringBuffer sb = new StringBuffer();
    classiAssociate.forEach(c -> {
      sb.append(c + " - ");
    });
    if(sb.length() > 4) {
      reg.setClassi(sb.toString().substring(sb.length() - 3));
    }
  }

  public List<String> getClassiAssociateRegistrazioneDocente(String istitutoId, String cfDocente) {
    List<String> result = new ArrayList<>();
    RegistrazioneDocente reg = registrazioneDocenteRepository.findOneByIstitutoIdAndCfDocente(istitutoId, cfDocente);
    if(reg != null) {
      result = getClassiAssociateRegistrazioneDocente(reg.getId());
    }
    return result;
  }

  public Page<RegistrazioneDocente> searchRegistrazioneDocente(String istitutoId, String text,
    Pageable pageRequest) throws Exception {
    StringBuilder sb = new StringBuilder("SELECT DISTINCT reg FROM RegistrazioneDocente reg");
    sb.append(" WHERE reg.istitutoId=(:istitutoId)");
    if(Utils.isNotEmpty(text)) {
      sb.append(" AND (UPPER(reg.nominativoDocente) LIKE (:text) OR UPPER(reg.emailDocente) LIKE (:text) OR UPPER(reg.cfDocente) LIKE (:text))");
    }
    sb.append(" ORDER BY reg.nominativoDocente ASC");
    String q = sb.toString();

    TypedQuery<RegistrazioneDocente> query = em.createQuery(q, RegistrazioneDocente.class);
    query.setParameter("istitutoId", istitutoId);
    if(Utils.isNotEmpty(text)) {
      query.setParameter("text", "%" + text.trim().toUpperCase() + "%");
    }
		query.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
		query.setMaxResults(pageRequest.getPageSize());
    List<RegistrazioneDocente> list = query.getResultList();
    list.forEach(rd -> updateClassiRegistrazioneDocente(rd));

		Query cQuery = queryToCount(q.replaceAll("DISTINCT reg","COUNT(DISTINCT reg)"), query);
		long total = (Long) cQuery.getSingleResult();

    Page<RegistrazioneDocente> page = new PageImpl<RegistrazioneDocente>(list, pageRequest, total);
    return page;
  }

  public Page<ReportDocenteClasse> getDocentiClassi(String istitutoId, String annoScolastico, 
  String text, Pageable pageRequest) {
    StringBuilder sb = new StringBuilder("SELECT DISTINCT ra FROM ReferenteAlternanza ra, ProfessoriClassi pc");
		sb.append(" WHERE pc.referenteAlternanzaId=ra.id AND pc.schoolYear=(:annoScolastico) AND pc.istitutoId=(:istitutoId)");
    if(Utils.isNotEmpty(text)) {
      sb.append(" AND (UPPER(ra.name) LIKE (:text) OR UPPER(ra.surname) LIKE (:text) OR UPPER(ra.cf) LIKE (:text))");
    }
    sb.append(" ORDER BY ra.email ASC");
    String q = sb.toString();

    TypedQuery<ReferenteAlternanza> query = em.createQuery(q, ReferenteAlternanza.class);
    query.setParameter("istitutoId", istitutoId);
    query.setParameter("annoScolastico", annoScolastico);
    if(Utils.isNotEmpty(text)) {
      query.setParameter("text", "%" + text.trim().toUpperCase() + "%");
    }
		query.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
		query.setMaxResults(pageRequest.getPageSize());
    List<ReferenteAlternanza> raList = query.getResultList();

    List<ReportDocenteClasse> result = new ArrayList<>();
    for(ReferenteAlternanza ra : raList) {
      ReportDocenteClasse report = new ReportDocenteClasse();
      report.setReferenteAlternanza(ra);
      List<ProfessoriClassi> associazioneClassi = professoriClassiRepository.findByReferenteAlternanzaId(ra.getId());
      report.setAssociazioneClassi(associazioneClassi);
      result.add(report);
      RegistrazioneDocente reg = registrazioneDocenteRepository.findOneByCfDocente(ra.getCf());
      if(reg != null) {
        report.setAttivo(true);
      }
    }
    
		Query cQuery = queryToCount(q.replaceAll("DISTINCT ra","COUNT(DISTINCT ra)"), query);
		long total = (Long) cQuery.getSingleResult();

    Page<ReportDocenteClasse> page = new PageImpl<ReportDocenteClasse>(result, pageRequest, total);
    return page;
  }

  public RegistrazioneDocente addRegistrazioneDocente(String istitutoId, String referenteAlternanzaId) throws Exception {
    ReferenteAlternanza ra = referenteAlternanzaRepository.findById(referenteAlternanzaId).orElse(null);
    if(ra == null) {
      throw new BadRequestException("referente non trovato");
    }
    if(Utils.isEmpty(ra.getEmail())) {
      throw new BadRequestException("codice fiscale " + ra.getCf() + " email non presente");
    }
    RegistrazioneDocente reg = registrazioneDocenteRepository.findOneByCfDocente(ra.getCf());
    if(reg != null) {
      throw new BadRequestException("codice fiscale " + ra.getCf() + " già registrato");
    }
    ASLUser user = userManager.getExistingASLUser(ra.getCf());
    if(user != null) {
			user.setName(ra.getName());
			user.setSurname(ra.getSurname());
      user.setEmail(ra.getEmail());
			userManager.updateASLUser(user);
		} else {
			user = new ASLUser();
			user.setEmail(ra.getEmail());
			user.setCf(ra.getCf());
			user.setName(ra.getName());
			user.setSurname(ra.getSurname());
			userManager.createASLUser(user);
    }
    ASLUserRole userRole = userManager.findASLUserRole(user.getId(), 
      ASLRole.TUTOR_SCOLASTICO, istitutoId);
    if(userRole == null) {
      userRole = userManager.addASLUserRole(user.getId(), 
          ASLRole.TUTOR_SCOLASTICO, istitutoId);
    }		
    reg = new RegistrazioneDocente();
    reg.setIstitutoId(istitutoId);
    reg.setUserId(user.getId());
    reg.setEmailDocente(ra.getEmail());
    reg.setCfDocente(ra.getCf());
    reg.setNominativoDocente(ra.getName() + " " + ra.getSurname());
    //TODO invio mail
    //mailService.inviaRuoloDocente(reg);
    registrazioneDocenteRepository.save(reg);
    return reg;
  }

  public RegistrazioneDocente deleteRegistrazioneDocente(String istitutoId, Long registrazioneId) throws Exception {
    Optional<RegistrazioneDocente> optional = registrazioneDocenteRepository.findById(registrazioneId);
    if(optional.isEmpty()) {
      throw new BadRequestException("registrazione non trovata");
    }
    RegistrazioneDocente reg = optional.get();
    ASLUser user = userManager.getASLUserById(reg.getUserId());
		if(user == null) {
			throw new BadRequestException("utente non trovato");
		}
    if(!reg.getIstitutoId().equals(istitutoId)) {
      throw new BadRequestException("istituto non corrispondente");
    }
		userManager.deleteASLUserRole(reg.getUserId(), ASLRole.TUTOR_SCOLASTICO, reg.getIstitutoId());
    userManager.deleteASLUserRole(reg.getUserId(), ASLRole.TUTOR_CLASSE, reg.getIstitutoId());
    registrazioneDocenteRepository.delete(reg);        
    return reg;
  }

  public List<ProfessoriClassi> getAssociazioneDocentiClassi(String istitutoId, Long registrazioneId) throws Exception {
    Optional<RegistrazioneDocente> optional = registrazioneDocenteRepository.findById(registrazioneId);
    if(optional.isEmpty()) {
      throw new BadRequestException("registrazione non trovata");
    }
    RegistrazioneDocente reg = optional.get();
    if(!reg.getIstitutoId().equals(istitutoId)) {
      throw new BadRequestException("istituto non corrispondente");
    }

    StringBuilder sb = new StringBuilder("SELECT DISTINCT pc FROM ProfessoriClassi pc, AssociazioneDocentiClassi adc");
		sb.append(" WHERE adc.registrazioneDocenteId=(:registrazioneId) AND adc.professoriClassiId=pc.id ORDER BY pc.corso ASC, classroom ASC");
    String q = sb.toString();

    TypedQuery<ProfessoriClassi> query = em.createQuery(q, ProfessoriClassi.class);
    query.setParameter("registrazioneId", registrazioneId);
    List<ProfessoriClassi> list = query.getResultList();
    for(ProfessoriClassi pc : list) {
      pc.setStudenti(getStudentiIscritti(pc));
    }
    return list;
  }

  private Long getStudentiIscritti(ProfessoriClassi pc) {
    String q = "SELECT COUNT(DISTINCT r) FROM Registration r WHERE r.schoolYear=(:schoolYear) AND r.instituteId=(:istitutoId) AND r.courseId=(:corsoId)";
    TypedQuery<Long> query = em.createQuery(q, Long.class);
    query.setParameter("schoolYear", pc.getSchoolYear());
    query.setParameter("istitutoId", pc.getIstitutoId());
    query.setParameter("corsoId", pc.getCorsoId());
    Long studenti = query.getSingleResult();
    return studenti;
  }

  public List<ProfessoriClassi> updateAssociazioneDocentiClassi(String istitutoId, Long registrazioneId, 
    List<String> docentiClassiIds) throws Exception {
    List<ProfessoriClassi> result = new ArrayList<>();
    Optional<RegistrazioneDocente> optional = registrazioneDocenteRepository.findById(registrazioneId);
    if(optional.isEmpty()) {
      throw new BadRequestException("registrazione non trovata");
    }
    RegistrazioneDocente reg = optional.get();
    if(!reg.getIstitutoId().equals(istitutoId)) {
      throw new BadRequestException("istituto non corrispondente");
    }
    ASLUser user = userManager.getASLUserById(reg.getUserId());
		if(user == null) {
			throw new BadRequestException("utente non trovato");
		}
    List<AssociazioneDocentiClassi> classi = associazioneDocentiClassiRepository.findByRegistrazioneDocenteId(registrazioneId);
    associazioneDocentiClassiRepository.deleteAll(classi);
    boolean tutorClasse = false;
    for(String docentiClassiId : docentiClassiIds) {
      ProfessoriClassi pc = professoriClassiRepository.findById(docentiClassiId).orElse(null);
      if(pc != null) {
        AssociazioneDocentiClassi adc = new AssociazioneDocentiClassi();
        adc.setRegistrazioneDocenteId(registrazioneId);
        adc.setProfessoriClassiId(pc.getId());
        adc.setAnnoScolastico(pc.getSchoolYear());
        adc.setClasse(pc.getClassroom());
        associazioneDocentiClassiRepository.save(adc);
        result.add(pc);
        tutorClasse = true;
      }
    }
    ASLUserRole userRole = userManager.findASLUserRole(user.getId(), 
      ASLRole.TUTOR_CLASSE, reg.getIstitutoId());
    if(tutorClasse) {
      if(userRole == null) {
        userRole = userManager.addASLUserRole(user.getId(), ASLRole.TUTOR_CLASSE, istitutoId);
      }		
    } else {
      if(userRole != null) {
        userManager.deleteASLUserRole(user.getId(), ASLRole.TUTOR_CLASSE, istitutoId);
      }
    }
    return result;
  }

}
