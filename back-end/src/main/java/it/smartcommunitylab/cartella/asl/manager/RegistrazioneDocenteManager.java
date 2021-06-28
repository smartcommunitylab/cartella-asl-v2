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
import it.smartcommunitylab.cartella.asl.model.ProfessoriClassi;
import it.smartcommunitylab.cartella.asl.model.ReferenteAlternanza;
import it.smartcommunitylab.cartella.asl.model.RegistrazioneDocente;
import it.smartcommunitylab.cartella.asl.model.report.ReportDocenteClasse;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;
import it.smartcommunitylab.cartella.asl.model.users.ASLUserRole;
import it.smartcommunitylab.cartella.asl.repository.AssociazioneDocentiClassiRepository;
import it.smartcommunitylab.cartella.asl.repository.ProfessoriClassiRepository;
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
  RegistrazioneDocenteRepository registrazioneDocenteRepository;
  @Autowired
  ProfessoriClassiRepository professoriClassiRepository;
  @Autowired
  AssociazioneDocentiClassiRepository associazioneDocentiClassiRepository;

  List<RegistrazioneDocente> getRegistrazioneDocente(String istitutoId) throws Exception {
    List<RegistrazioneDocente> list = registrazioneDocenteRepository.findByIstitutoId(istitutoId, Sort.by(Sort.Direction.ASC, "emailDocente"));
    return list;
  }

  Page<ReportDocenteClasse> getDocentiClassi(String istitutoId, String annoScolastico, 
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

  public RegistrazioneDocente addRegistrazioneDocente(String istitutoId, ReferenteAlternanza ra) throws Exception {
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
}
