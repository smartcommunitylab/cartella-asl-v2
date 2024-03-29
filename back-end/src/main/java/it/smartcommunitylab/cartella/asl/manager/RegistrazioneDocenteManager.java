package it.smartcommunitylab.cartella.asl.manager;

import java.util.ArrayList;
import java.util.List;

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
import it.smartcommunitylab.cartella.asl.model.Istituzione;
import it.smartcommunitylab.cartella.asl.model.ProfessoriClassi;
import it.smartcommunitylab.cartella.asl.model.ReferenteAlternanza;
import it.smartcommunitylab.cartella.asl.model.RegistrazioneDocente;
import it.smartcommunitylab.cartella.asl.model.report.DocentiClassiReport;
import it.smartcommunitylab.cartella.asl.model.report.ReportDocenteClasse;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;
import it.smartcommunitylab.cartella.asl.model.users.ASLUserRole;
import it.smartcommunitylab.cartella.asl.repository.AssociazioneDocentiClassiRepository;
import it.smartcommunitylab.cartella.asl.repository.IstituzioneRepository;
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
  @Autowired
  IstituzioneRepository istituzioneRepository;

  public List<RegistrazioneDocente> getRegistrazioneDocente(String istitutoId) throws Exception {
    List<RegistrazioneDocente> list = registrazioneDocenteRepository.findByIstitutoId(istitutoId, Sort.by(Sort.Direction.ASC, "emailDocente"));
    list.forEach(rd -> updateClassiRegistrazioneDocente(rd));
    return list;
  }

  public RegistrazioneDocente getRegistrazioneDocente(Long registrazioneDocenteId) throws Exception {
    RegistrazioneDocente rd = registrazioneDocenteRepository.findById(registrazioneDocenteId).orElse(null);
    if(rd == null) {
      throw new BadRequestException("registrazione non trovata");
    }
    updateClassiRegistrazioneDocente(rd);
    return rd;
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
    reg.setClassiList(classiAssociate);
    StringBuffer sb = new StringBuffer();
    classiAssociate.forEach(c -> {
      sb.append(c + " - ");
    });
    if(sb.length() > 4) {
      reg.setClassi(sb.toString().substring(0, sb.length() - 3));
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
      throw new BadRequestException("email non presente per " + ra.getCf());
    }
    RegistrazioneDocente reg = registrazioneDocenteRepository.findOneByCfDocente(ra.getCf());
    if(reg != null) {
      throw new BadRequestException("codice fiscale " + ra.getCf() + " già registrato");
    }
    Istituzione istituto = istituzioneRepository.findById(istitutoId).orElse(null);
    if(istituto == null) {
    	throw new BadRequestException("istituto non trovato");
    }
    ASLUser user = userManager.getASLUserByCf(ra.getCf());
    if(user == null) {
    	user = userManager.getExistingASLUser(ra.getEmail());
    }
    if(user != null) {
      //check dirigente o funzione strumentale
    	ASLUserRole dirigente = userManager.findASLUserRole(user.getId(),
          ASLRole.DIRIGENTE_SCOLASTICO, istitutoId);
    	ASLUserRole funzione = userManager.findASLUserRole(user.getId(),
          ASLRole.FUNZIONE_STRUMENTALE, istitutoId);
    	if((dirigente != null) || (funzione != null)) {
    		throw new BadRequestException("codice fiscale " + ra.getCf() + " già presente con ruolo di gestione alternanza");
    	}
			user.setName(ra.getName());
			user.setSurname(ra.getSurname());
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
    reg.setNomeIstituto(istituto.getName());
    reg.setUserId(user.getId());
    reg.setEmailDocente(ra.getEmail());
    reg.setCfDocente(ra.getCf());
    reg.setNominativoDocente(ra.getName() + " " + ra.getSurname());
    mailService.inviaRuoloDocente(reg);
    registrazioneDocenteRepository.save(reg);
    return reg;
  }

  public RegistrazioneDocente deleteRegistrazioneDocente(String istitutoId, Long registrazioneId) throws Exception {
    RegistrazioneDocente reg = registrazioneDocenteRepository.findById(registrazioneId).orElse(null);
    if(reg == null) {
      throw new BadRequestException("registrazione non trovata");
    }
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

  public Page<DocentiClassiReport> getAssociazioneDocentiClassi(String istitutoId, String annoScolastico, 
      Long registrazioneId, String text, Pageable pageRequest) throws Exception {
    RegistrazioneDocente reg = registrazioneDocenteRepository.findById(registrazioneId).orElse(null);
    if(reg == null) {
      throw new BadRequestException("registrazione non trovata");
    }
    if(!reg.getIstitutoId().equals(istitutoId)) {
      throw new BadRequestException("istituto non corrispondente");
    }
    ReferenteAlternanza docente = referenteAlternanzaRepository.findReferenteAlternanzaByCf(reg.getCfDocente());
    if(docente == null) {
      throw new BadRequestException("cf docente non trovato");
    }

    StringBuilder sb = new StringBuilder("SELECT DISTINCT r.courseId, r.course, r.classroom FROM Registration r");
		sb.append(" WHERE r.instituteId=(:istitutoId)");
    sb.append(" AND r.schoolYear=(:annoScolastico)");
    if(Utils.isNotEmpty(text)) {
      sb.append(" AND (UPPER(r.course) LIKE (:text) OR UPPER(r.classroom) LIKE (:text))");
    }
    sb.append(" ORDER BY r.course ASC, r.classroom ASC");
    String q = sb.toString();

    Query query = em.createQuery(q);
    query.setParameter("annoScolastico", annoScolastico);
    query.setParameter("istitutoId", istitutoId);
    if(Utils.isNotEmpty(text)) {
      query.setParameter("text", text);
    }
    query.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
		query.setMaxResults(pageRequest.getPageSize());
    List<Object[]> result = query.getResultList();

    List<DocentiClassiReport> reportList = new ArrayList<>();
    for(Object[] obj : result) {
      String corsoId = (String) obj[0];
      String corso = (String) obj[1];
      String classe = (String) obj[2];
      DocentiClassiReport report = new DocentiClassiReport();
      report.setAnnoScolastico(annoScolastico);
      report.setClasse(classe);
      report.setCorso(corso);
      report.setCorsoId(corsoId);
      //report.setStudenti(getStudentiIscritti(istitutoId, corsoId, annoScolastico, classe));
      reportList.add(report);
    }

    Query cQuery = queryToCount(q.replaceAll("DISTINCT r.courseId, r.course, r.classroom","COUNT(DISTINCT r.classroom)"), query);
		long total = (Long) cQuery.getSingleResult();

    Page<DocentiClassiReport> page = new PageImpl<DocentiClassiReport>(reportList, pageRequest, total);
    return page;
  }

  private Long getStudentiIscritti(String istitutoId, String corsoId, String annoScolastico, String classe) {
    String q = "SELECT COUNT(DISTINCT r) FROM Registration r WHERE r.schoolYear=(:annoScolastico) AND r.instituteId=(:istitutoId)" 
      + " AND r.courseId=(:corsoId) AND r.classroom=(:classe)";
    TypedQuery<Long> query = em.createQuery(q, Long.class);
    query.setParameter("annoScolastico", annoScolastico);
    query.setParameter("istitutoId", istitutoId);
    query.setParameter("corsoId", corsoId);
    query.setParameter("classe", classe);
    Long studenti = query.getSingleResult();
    return studenti;
  }

  public List<AssociazioneDocentiClassi> updateAssociazioneDocentiClassi(String istitutoId, String annoScolastico,
      Long registrazioneId, List<String> classi) throws Exception {
    List<AssociazioneDocentiClassi> result = new ArrayList<>();
    RegistrazioneDocente reg = registrazioneDocenteRepository.findById(registrazioneId).orElse(null);
    if(reg == null) {
      throw new BadRequestException("registrazione non trovata");
    }
    if(!reg.getIstitutoId().equals(istitutoId)) {
      throw new BadRequestException("istituto non corrispondente");
    }
    ASLUser user = userManager.getASLUserById(reg.getUserId());
		if(user == null) {
			throw new BadRequestException("utente non trovato");
		}
    List<AssociazioneDocentiClassi> associazioni = associazioneDocentiClassiRepository.findByRegistrazioneDocenteId(registrazioneId);
    associazioneDocentiClassiRepository.deleteAll(associazioni);
    boolean tutorClasse = false;
    for(String classe : classi) {
      AssociazioneDocentiClassi adc = new AssociazioneDocentiClassi();
      adc.setRegistrazioneDocenteId(registrazioneId);
      adc.setAnnoScolastico(annoScolastico);
      adc.setClasse(classe.trim());
      associazioneDocentiClassiRepository.save(adc);
      result.add(adc);
      tutorClasse = true;
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
