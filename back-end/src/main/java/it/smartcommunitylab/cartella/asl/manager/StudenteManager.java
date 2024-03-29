package it.smartcommunitylab.cartella.asl.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.exception.UnauthorizedException;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.Competenza;
import it.smartcommunitylab.cartella.asl.model.CorsoDiStudioBean;
import it.smartcommunitylab.cartella.asl.model.CorsoMetaInfo;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;
import it.smartcommunitylab.cartella.asl.model.Istituzione;
import it.smartcommunitylab.cartella.asl.model.NotificheStudente;
import it.smartcommunitylab.cartella.asl.model.PianoAlternanza;
import it.smartcommunitylab.cartella.asl.model.PresenzaGiornaliera;
import it.smartcommunitylab.cartella.asl.model.Registration;
import it.smartcommunitylab.cartella.asl.model.Studente;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza.Stati;
import it.smartcommunitylab.cartella.asl.model.report.ReportDettaglioAttivitaEsperienza;
import it.smartcommunitylab.cartella.asl.model.report.ReportDettaglioStudente;
import it.smartcommunitylab.cartella.asl.model.report.ReportEsperienzaStudente;
import it.smartcommunitylab.cartella.asl.model.report.ReportStudenteDettaglioEnte;
import it.smartcommunitylab.cartella.asl.model.report.ReportStudenteEnte;
import it.smartcommunitylab.cartella.asl.model.report.ReportStudenteRicerca;
import it.smartcommunitylab.cartella.asl.model.report.ReportStudenteSommario;
import it.smartcommunitylab.cartella.asl.model.report.ValutazioneAttivitaReport;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;
import it.smartcommunitylab.cartella.asl.repository.CorsoMetaInfoRepository;
import it.smartcommunitylab.cartella.asl.repository.EsperienzaSvoltaRepository;
import it.smartcommunitylab.cartella.asl.repository.NotificheStudenteRepository;
import it.smartcommunitylab.cartella.asl.repository.PresenzaGiornaliereRepository;
import it.smartcommunitylab.cartella.asl.repository.StudenteRepository;
import it.smartcommunitylab.cartella.asl.services.FirebaseService;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Repository
@Transactional
public class StudenteManager extends DataEntityManager {
	private static final transient Log logger = LogFactory.getLog(StudenteManager.class);

	@Autowired
	private StudenteRepository studenteRepository;
	@Autowired
	private EsperienzaSvoltaRepository esperienzaSvoltaRepository;
	@Autowired
	private PresenzaGiornaliereRepository presenzeRepository;
	@Autowired
	private PianoAlternanzaManager pianoAlternanzaManager;
	@Autowired
	private AttivitaAlternanzaManager attivitaAlternanzaManager;
	@Autowired
	private PresenzaGiornalieraManager presenzaGiornalieraManager;
	@Autowired
	private EsperienzaSvoltaManager esperienzaSvoltaManager;
	@Autowired
	private CompetenzaManager competenzaManager;
	@Autowired
	private IstituzioneManager istituzioneManager;
	@Autowired
	private ValutazioniManager valutazioniManager;
	@Autowired
	private CorsoMetaInfoRepository corsoMetaInfoRepository;
	@Autowired
	private NotificheStudenteRepository notificheStudenteRepository;
	@Autowired
	private FirebaseService firebaseService;
	@Autowired
	RegistrazioneDocenteManager registrazioneDocenteManager;
	@Autowired
	ASLRolesValidator usersValidator;
	
	private static final String STUDENT_REGISTRATION = "SELECT r0 FROM Registration r0 WHERE r0.studentId = (:id) AND r0.dateTo = (SELECT max(rm.dateTo) FROM Registration rm WHERE rm.studentId = (:id)) ";

	@SuppressWarnings("unchecked")
	public Page<Studente> findStudentiPaged(String istitutoId, String corsoId, String annoScolastico, 
			String text, Pageable pageRequest) {
		StringBuilder sb = new StringBuilder(DataEntityManager.STUDENTS_AND_REGISTRATIONS_QUERY);

		if (Utils.isNotEmpty(corsoId)) {
			sb.append(" AND r0.courseId = (:courseId) ");
		}
		if (Utils.isNotEmpty(text)) {
			sb.append(" AND (UPPER(r0.classroom) LIKE (:text) OR UPPER(s0.surname) LIKE (:text) OR UPPER(s0.cf) = (:textCf))");
		}
		sb.append(" ORDER BY s0.surname, s0.name, r0.classroom");
		String q = sb.toString();

		Query query = em.createQuery(q);

		query.setParameter("instituteId", istitutoId);
		query.setParameter("annoScolastico", annoScolastico);
		if (Utils.isNotEmpty(corsoId)) {
			query.setParameter("courseId", corsoId);
		}
		if (Utils.isNotEmpty(text)) {
			query.setParameter("text", "%" + text.trim().toUpperCase() + "%");
			query.setParameter("textCf", text.trim().toUpperCase());
		}

		query.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
		query.setMaxResults(pageRequest.getPageSize());
		List<Object[]> result = query.getResultList();

		List<Studente> students = Lists.newArrayList();
		for (Object[] obj : result) {
			Studente s = fillStudenteWithRegistration(obj);
			students.add(s);
		}

		Query cQuery = queryToCount(q.replaceAll("DISTINCT s0,r0","COUNT(DISTINCT s0)"), query);
		long total = (Long) cQuery.getSingleResult();

		Page<Studente> page = new PageImpl<Studente>(students, pageRequest, total);

		return page;
	}

	private Studente fillStudenteWithRegistration(Object[] sr) {
		Studente s = (Studente) sr[0];
		Registration r = (Registration) sr[1];
		s.setClassroom(r.getClassroom());
		s.setAnnoCorso(Integer.valueOf(r.getClassroom().substring(0, 1)));
		s.setAnnoScolastico(r.getSchoolYear());
		s.setIstitutoId(r.getInstituteId());
		CorsoDiStudioBean corsoBean = new CorsoDiStudioBean(r.getCourseId(), r.getCourse());
		s.setCorsoDiStudio(corsoBean);
		return s;
	}

	public Page<ReportStudenteRicerca> findStudentiRicercaPaged(String istitutoId, String annoScolastico, String corsoId,
			String text, Pageable pageRequest, ASLUser user) {
		Page<Studente> studentsProfiles = null;
		if(usersValidator.hasRole(user, ASLRole.TUTOR_SCOLASTICO, istitutoId)) {		
			studentsProfiles = findStudentiByTutor(istitutoId, corsoId, annoScolastico, text, pageRequest, user);
		} else {
			studentsProfiles = findStudentiPaged(istitutoId, corsoId, annoScolastico, text, pageRequest);
		}

		List<ReportStudenteRicerca> studentsRicerca = new ArrayList<>();
		List<String> studentIds = new ArrayList<>();
		List<Long> esperienzaSvoltaIds = new ArrayList<>();
		studentsProfiles.forEach(s -> {
			studentIds.add(s.getId());
		});
		List<EsperienzaSvolta> esperienzeSvolte = esperienzaSvoltaRepository.findByIstitutoIdAndStudenteIdIn(istitutoId, studentIds);
		Map<String, List<EsperienzaSvolta>> esperienzeSvolteMap = new HashMap<>();
		esperienzeSvolte.forEach(es -> {
			if(!esperienzaSvoltaIds.contains(es.getId())) {
				esperienzaSvoltaIds.add(es.getId());
			}
			List<EsperienzaSvolta> esList = esperienzeSvolteMap.get(es.getStudenteId());
			if(esList == null) {
				esList = new ArrayList<>(); 
				esperienzeSvolteMap.put(es.getStudenteId(), esList);
			}
			esList.add(es);
		});
		List<PresenzaGiornaliera> presenze = presenzeRepository.findByEsperienzaSvoltaIdInAndVerificata(esperienzaSvoltaIds, true);
		Map<Long, List<PresenzaGiornaliera>> presenzeMap = new HashMap<>();
		presenze.forEach(p -> {
			List<PresenzaGiornaliera> presenzeList = presenzeMap.get(p.getEsperienzaSvoltaId());
			if(presenzeList == null) {
				presenzeList = new ArrayList<>(); 
				presenzeMap.put(p.getEsperienzaSvoltaId(), presenzeList);
			}
			presenzeList.add(p);
		});
		
		Map<String, Optional<PianoAlternanza>> pianoMap = new HashMap<>();
		studentsProfiles.forEach(s -> {

			ReportStudenteRicerca studenteReport = new ReportStudenteRicerca(s);
			
			//check corso sperimentale
			studenteReport.setCorsoSperimentale(false);
			CorsoMetaInfo corsoMetaInfo = corsoMetaInfoRepository.findById(s.getCorsoDiStudio().getCourseId()).orElse(null);
			if(corsoMetaInfo != null) {
	  		if((corsoMetaInfo.getYears() != null) && (corsoMetaInfo.getYears() < 5)) {
	  			studenteReport.setCorsoSperimentale(true);
	  		}
	  	}

			List<EsperienzaSvolta> esList = esperienzeSvolteMap.get(s.getId());
			if(esList == null) {
				esList = new ArrayList<>();
			}

			int oreSvolteSeconda = 0;
			int oreSvolteTerza = 0;
			int oreSvolteQuarta = 0;
			int oreSvolteQuinta = 0;

			// get piano for classroom
			String key = s.getCorsoDiStudio().getCourseId() + "_" + s.getClassroom();
			
			Optional<PianoAlternanza> activePianoForClassrom = pianoMap.get(key);
			if(activePianoForClassrom == null) {
				activePianoForClassrom = pianoForClassrom(istitutoId,
						s.getCorsoDiStudio().getCourseId(), s.getClassroom(), annoScolastico);
				pianoMap.put(key, activePianoForClassrom);
			}
			
			if(activePianoForClassrom.isPresent()) {
				studenteReport.setTitoloPiano(activePianoForClassrom.get().getTitolo());
				studenteReport.setPianoId(activePianoForClassrom.get().getId());
				studenteReport.setOreTotali(activePianoForClassrom.get().getOreTerzoAnno() + 
						activePianoForClassrom.get().getOreQuartoAnno() + 
						activePianoForClassrom.get().getOreQuintoAnno());
			}
							
			final int oreTotaliSeconda = activePianoForClassrom.map(p -> p.getOreSecondoAnno()).orElse(0);
			final int oreTotaliTerza = activePianoForClassrom.map(p -> p.getOreTerzoAnno()).orElse(0);
			final int oreTotaliQuarta = activePianoForClassrom.map(p -> p.getOreQuartoAnno()).orElse(0);
			final int oreTotaliQuinta = activePianoForClassrom.map(p -> p.getOreQuintoAnno()).orElse(0);

			for (EsperienzaSvolta esperienza : esList) {
				final Long esperienzaId = esperienza.getId();
				final String annoDiCorso = Utils.annoDiCorso(esperienza.getClasseStudente());
				
				List<PresenzaGiornaliera> presenzeList = presenzeMap.get(esperienzaId);
				if(presenzeList != null) {
					int oreSvolte = presenzeList.stream()
							.mapToInt(presenza -> presenza.getOreSvolte()).sum();
					switch (annoDiCorso) {
					case "2":
						oreSvolteSeconda += oreSvolte;
						break;
					case "3":
						oreSvolteTerza += oreSvolte;
						break;
					case "4":
						oreSvolteQuarta += oreSvolte;
						break;
					case "5":
						oreSvolteQuinta += oreSvolte;
						break;
					default:
						break;
					}					
				}
			}
			
			studenteReport.getOreSvolteSeconda().setHours(oreSvolteSeconda);
			studenteReport.getOreSvolteSeconda().setTotal(oreTotaliSeconda);
			studenteReport.getOreSvolteTerza().setHours(oreSvolteTerza);
			studenteReport.getOreSvolteTerza().setTotal(oreTotaliTerza);
			studenteReport.getOreSvolteQuarta().setHours(oreSvolteQuarta);
			studenteReport.getOreSvolteQuarta().setTotal(oreTotaliQuarta);
			studenteReport.getOreSvolteQuinta().setHours(oreSvolteQuinta);
			studenteReport.getOreSvolteQuinta().setTotal(oreTotaliQuinta);
			studenteReport.setOreValidate(oreSvolteSeconda + oreSvolteTerza + oreSvolteQuarta + oreSvolteQuinta);
			studenteReport.setOreProgrammate(oreTotaliSeconda + oreTotaliTerza + oreTotaliQuarta + oreTotaliQuinta);
			studenteReport.setClasse(s.getClassroom());
			studenteReport.setAnnoScolastico(annoScolastico);
			studentsRicerca.add(studenteReport);
		});

		Page<ReportStudenteRicerca> pagedstudentsRicerca = new PageImpl<>(studentsRicerca, pageRequest,
				studentsProfiles.getTotalElements());
		return pagedstudentsRicerca;
	}
	
	private Page<Studente> findStudentiByTutor(String istitutoId, String corsoId, String annoScolastico, String text,
			Pageable pageRequest, ASLUser user) {
		//boolean tutorScolatico = usersValidator.hasRole(user, ASLRole.TUTOR_SCOLASTICO, istitutoId);
		//boolean tutorClasse = usersValidator.hasRole(user, ASLRole.TUTOR_CLASSE, istitutoId);
		List<String> classiAssociate = registrazioneDocenteManager.getClassiAssociateRegistrazioneDocente(istitutoId, user.getCf());
		
		//TUTOR CLASSE
		List<Studente> studentsTutorClasse = Lists.newArrayList();
		if(!classiAssociate.isEmpty()) {
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT DISTINCT s,r FROM Studente s, Registration r");
			sb.append(" WHERE s.id = r.studentId");
			sb.append(" AND r.instituteId = (:istitutoId) AND r.dateTo = (SELECT max(rm.dateTo) FROM Registration rm WHERE rm.studentId = s.id AND rm.schoolYear = (:annoScolastico) AND rm.instituteId = (:istitutoId))");
			sb.append(" AND r.classroom IN (:classiAssociate)");
			/*if(tutorClasse) {
				sb.append(" AND aa.istitutoId=(:istitutoId)");
				sb.append(" AND ((aa.referenteScuolaCF=(:referenteCf) AND aa.stato!='" + AttivitaAlternanza.Stati.archiviata.toString() + "')");
				sb.append(" OR (es.classeStudente IN (:classiAssociate)))");
			} else {
				if(tutorScolatico) {
					sb.append(" AND aa.istitutoId=(:istitutoId) AND aa.referenteScuolaCF=(:referenteCf)");
					sb.append(" AND aa.stato!='" + AttivitaAlternanza.Stati.archiviata.toString() + "'");
				} else {
					sb.append(" AND aa.istitutoId=(:istitutoId)");
				}
			}*/
			if (Utils.isNotEmpty(corsoId)) {
				sb.append(" AND r.courseId = (:courseId) ");
			}
			if (Utils.isNotEmpty(text)) {
				sb.append(" AND (UPPER(r.classroom) LIKE (:text) OR UPPER(s.surname) LIKE (:text) OR UPPER(s.cf) = (:textCf))");
			}
			sb.append(" ORDER BY s.surname, s.name, r.classroom");
			String q = sb.toString();

			Query queryTutorClasse = em.createQuery(q);

			queryTutorClasse.setParameter("istitutoId", istitutoId);
			queryTutorClasse.setParameter("annoScolastico", annoScolastico);
			queryTutorClasse.setParameter("classiAssociate", classiAssociate);
			if (Utils.isNotEmpty(corsoId)) {
				queryTutorClasse.setParameter("courseId", corsoId);
			}
			if (Utils.isNotEmpty(text)) {
				String like = "%" + text.trim().toUpperCase() + "%";
				queryTutorClasse.setParameter("text", like);
				queryTutorClasse.setParameter("textCf", text.trim().toUpperCase());
			}

			@SuppressWarnings("unchecked")
			List<Object[]> resultTutorClasse = queryTutorClasse.getResultList();

			for (Object[] obj : resultTutorClasse) {
				Studente s = fillStudenteWithRegistration(obj);
				studentsTutorClasse.add(s);
			}			
		}

		//TUTOR SCOLASTICO
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DISTINCT s,r FROM Studente s, Registration r, EsperienzaSvolta es, AttivitaAlternanza aa");
		sb.append(" WHERE s.id = r.studentId AND es.studenteId = s.id AND es.registrazioneId=r.id AND es.attivitaAlternanzaId=aa.id");
		sb.append(" AND r.instituteId = (:istitutoId) AND r.dateTo = (SELECT max(rm.dateTo) FROM Registration rm WHERE rm.studentId = s.id AND rm.schoolYear = (:annoScolastico) AND rm.instituteId = (:istitutoId))");
		sb.append(" AND aa.istitutoId=(:istitutoId) AND aa.referenteScuolaCF=(:referenteCf)");
		sb.append(" AND aa.stato!='" + AttivitaAlternanza.Stati.archiviata.toString() + "'");
		if (Utils.isNotEmpty(corsoId)) {
			sb.append(" AND r.courseId = (:courseId) ");
		}
		if (Utils.isNotEmpty(text)) {
			sb.append(" AND (UPPER(r.classroom) LIKE (:text) OR UPPER(s.surname) LIKE (:text) OR UPPER(s.name) LIKE (:text))");
		}
		sb.append(" ORDER BY s.surname, s.name, r.classroom");
		
		String q = sb.toString();
		Query queryTutorScolastico = em.createQuery(q);
		
		queryTutorScolastico.setParameter("istitutoId", istitutoId);
		queryTutorScolastico.setParameter("annoScolastico", annoScolastico);
		queryTutorScolastico.setParameter("referenteCf", user.getCf());
		if (Utils.isNotEmpty(corsoId)) {
			queryTutorScolastico.setParameter("courseId", corsoId);
		}
		if (Utils.isNotEmpty(text)) {
			String like = "%" + text.trim().toUpperCase() + "%";
			queryTutorScolastico.setParameter("text", like);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> resultTutorScolastico = queryTutorScolastico.getResultList();

		List<Studente> studentsTutorScolastico = Lists.newArrayList();
		for (Object[] obj : resultTutorScolastico) {
			Studente s = fillStudenteWithRegistration(obj);
			studentsTutorScolastico.add(s);
		}
		
		//MERGE LISTA STUDENTI
		List<Studente> students = Lists.newArrayList();
		while (studentsTutorClasse.size() > 0 && studentsTutorScolastico.size() > 0) {
			if(comparaStudenti(studentsTutorClasse.get(0), studentsTutorScolastico.get(0)) < 0) {
				if(!contieneStudente(students, studentsTutorClasse.get(0))) {
					students.add(studentsTutorClasse.get(0));
				}
				studentsTutorClasse.remove(0);
			} else {
				if(!contieneStudente(students, studentsTutorScolastico.get(0))) {
					students.add(studentsTutorScolastico.get(0));
				}
				studentsTutorScolastico.remove(0);
			}
		}
		for(Studente s : studentsTutorClasse) {
			if(!contieneStudente(students, s)) {
				students.add(s);
			}			
		}
		for(Studente s : studentsTutorScolastico) {
			if(!contieneStudente(students, s)) {
				students.add(s);
			}			
		}
    
		int fromIndex = (pageRequest.getPageNumber()) * pageRequest.getPageSize();
		int toIndex = fromIndex + pageRequest.getPageSize();
		toIndex = toIndex > students.size() ? students.size() : toIndex;
		Page<Studente> page = new PageImpl<Studente>(students.subList(fromIndex, toIndex), pageRequest, students.size());

		return page;
	}
	
	private boolean contieneStudente(List<Studente> list, Studente studente) {
		for(Studente s : list) {
			if(s.getId().contentEquals(studente.getId())) {
				return true;
			}
		}
		return false;
	}
	
	private int comparaStudenti(Studente s1, Studente s2) {
		return (s1.getSurname() + " " + s1.getName() + " " + s1.getClassroom()).compareTo(s2.getSurname() + " " + s2.getName() + " " + s2.getClassroom());
	}

	public List<ReportDettaglioStudente> getReportDettaglioStudente(String istitutoId, String annoScolastico, 
			String corsoId, String classe) {
		List<ReportDettaglioStudente> studentsRicerca = new ArrayList<>();
		Pageable pageRequest = PageRequest.of(0, 100);
		Page<Studente> studentsProfiles = findStudentiPaged(istitutoId, corsoId, annoScolastico, classe, pageRequest);
		studentsProfiles.forEach(s -> {
			if(classe.equalsIgnoreCase(s.getClassroom())) {
				ReportDettaglioStudente studenteReport = getReportDettaglioStudente(istitutoId, s.getId(), null);
				studentsRicerca.add(studenteReport);				
			}
		});		
		return studentsRicerca;
	}

	private Optional<PianoAlternanza> pianoForClassrom(String istitutoId, String corsoDiStudioId, String classroom,
			String annoScolastico) {
		List<PianoAlternanza> pianiAttivi = pianoAlternanzaManager.findPianoAlternanzaAttivoForCorso(istitutoId,
				corsoDiStudioId);
		
		return pianiAttivi.stream()
				.filter(p -> { 
					pianoAlternanzaManager.calculateAnni(p);
					return p.getAnni().contains(Utils.annoDiCorso(classroom));
				}).findFirst();
	}

	public Page<Studente> findStudenti(String cf, String text, Pageable pageRequest) {
		StringBuilder sb = new StringBuilder("SELECT DISTINCT st FROM Studente st");

		if (Utils.isNotEmpty(cf)) {
			sb.append(" AND UPPER(st.cf) = (:cf) ");
		}
		if (Utils.isNotEmpty(text)) {
			sb.append(" AND (UPPER(st.name) LIKE (:text) OR UPPER(st.surname) LIKE (:text))");
		}

		sb.append(" ORDER BY st.surname, st.name");

		String q = sb.toString();
		q = q.replaceFirst(" AND ", " WHERE ");

		TypedQuery<Studente> query = em.createQuery(q, Studente.class);

		if (Utils.isNotEmpty(cf)) {
			query.setParameter("cf", cf.trim().toUpperCase());
		}
		if (Utils.isNotEmpty(text)) {
			query.setParameter("text", "%" + text.trim().toUpperCase() + "%");
		}

		Page<Studente> page = null;

		query.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
		query.setMaxResults(pageRequest.getPageSize());
		List<Studente> result = query.getResultList();

		Query cQuery = queryToCountQuery(q, query);
		long t = (Long) cQuery.getSingleResult();

		page = new PageImpl<Studente>(result, pageRequest, t);

		return page;
	}
	
	public Studente findStudente(String id) {
		Studente s = studenteRepository.getOne(id);
		
		StringBuilder sb = new StringBuilder(STUDENT_REGISTRATION);

		String q = sb.toString();

		TypedQuery<Registration> query = em.createQuery(q, Registration.class);

		query.setParameter("id", id);

		Registration r = query.getSingleResult();
		
		Object[] result = new Object[]{s, r};
		s = fillStudenteWithRegistration(result);

		return s;
	}

	public ReportDettaglioStudente getReportDettaglioStudente(String istitutoId, String studenteId, ASLUser user) {
		boolean tutorScolatico = false;
		boolean tutorClasse = false;
		List<String> classiAssociate = null;
		if(user != null) {
			tutorScolatico = usersValidator.hasRole(user, ASLRole.TUTOR_SCOLASTICO, istitutoId);
			tutorClasse = usersValidator.hasRole(user, ASLRole.TUTOR_CLASSE, istitutoId);
			classiAssociate = registrazioneDocenteManager.getClassiAssociateRegistrazioneDocente(istitutoId, user.getCf());
		}
		
		ReportDettaglioStudente result = new ReportDettaglioStudente();
		Studente studente = findStudente(studenteId);
		result.setStudente(studente);
		
		List<ReportEsperienzaStudente> esperienzeStudente = attivitaAlternanzaManager.getReportEsperienzaStudente(istitutoId, 
				studenteId);
		for(ReportEsperienzaStudente esp : esperienzeStudente) {
			if(tutorScolatico) {
				AttivitaAlternanza aa = attivitaAlternanzaManager.getAttivitaAlternanza(esp.getAttivitaAlternanzaId());
				if(!aa.getStato().equals(Stati.archiviata) && user.getCf().equals(aa.getReferenteScuolaCF())) {
					esp.setTutorScolastico(true);
					result.getEsperienze().add(esp);
					continue;
				}
			} 
			if(tutorClasse) {
				if(classiAssociate.contains(esp.getClasseStudente())) {
					esp.setTutorClasse(true);
					result.getEsperienze().add(esp);
					continue;
				}
			}
			if(!tutorScolatico && !tutorClasse) {
				result.getEsperienze().add(esp);
			}
		}
		
		List<Competenza> competenze = competenzaManager.getCompetenzeByStudente(istitutoId, studenteId);
		result.getCompetenze().addAll(competenze);
		
		Optional<PianoAlternanza> pianoForClassrom = pianoForClassrom(istitutoId, 
				studente.getCorsoDiStudio().getCourseId(), studente.getClassroom(), studente.getAnnoScolastico());
		if(pianoForClassrom.isPresent()) {
			result.setTitoloPiano(pianoForClassrom.get().getTitolo());
			result.setPianoId(pianoForClassrom.get().getId());
			result.setOreTotali(pianoForClassrom.get().getOreSecondoAnno() +
					pianoForClassrom.get().getOreTerzoAnno() + 
					pianoForClassrom.get().getOreQuartoAnno() + 
					pianoForClassrom.get().getOreQuintoAnno());
		}
		
		final int oreTotaliTerza = pianoForClassrom.map(p -> p.getOreTerzoAnno()).orElse(0);
		final int oreTotaliQuarta = pianoForClassrom.map(p -> p.getOreQuartoAnno()).orElse(0);
		final int oreTotaliQuinta = pianoForClassrom.map(p -> p.getOreQuintoAnno()).orElse(0);
		
		int oreSvolteTerza = 0;
		int oreSvolteQuarta = 0;
		int oreSvolteQuinta = 0;
		
		for(ReportEsperienzaStudente report : esperienzeStudente) {
			final String annoDiCorso = Utils.annoDiCorso(report.getClasseStudente());
			switch (annoDiCorso) {
			case "3":
				oreSvolteTerza += report.getOreValidate();
				break;
			case "4":
				oreSvolteQuarta += report.getOreValidate();
				break;
			case "5":
				oreSvolteQuinta += report.getOreValidate();
				break;
			default:
				break;
			}
			result.setOreValidate(result.getOreValidate() + report.getOreValidate());
		}
		result.getOreSvolteTerza().setHours(oreSvolteTerza);
		result.getOreSvolteTerza().setTotal(oreTotaliTerza);
		result.getOreSvolteQuarta().setHours(oreSvolteQuarta);
		result.getOreSvolteQuarta().setTotal(oreTotaliQuarta);
		result.getOreSvolteQuinta().setHours(oreSvolteQuinta);
		result.getOreSvolteQuinta().setTotal(oreTotaliQuinta);
		
		return result;
	}

	public Page<ReportEsperienzaStudente> getReportEsperienzaStudenteList(String studenteId, String stato, Pageable pageRequest) {
		Page<ReportEsperienzaStudente> page = attivitaAlternanzaManager.getReportEsperienzaStudente(studenteId, stato, pageRequest);
		return page;
	}

	public ReportDettaglioAttivitaEsperienza getReportDettaglioAttivitaEsperienza(Long esperienzaSvoltaId,
			String studenteId) throws Exception {
		EsperienzaSvolta es = esperienzaSvoltaManager.findById(esperienzaSvoltaId);
		if(es == null) {
			throw new BadRequestException("esperienzaSvolta not found");
		}
		if(!studenteId.equals(es.getStudenteId())) {
			throw new UnauthorizedException("esperienza id not authorized");
		}
		AttivitaAlternanza aa = attivitaAlternanzaManager.getAttivitaAlternanzaStub(es.getAttivitaAlternanzaId());
		if(aa == null) {
			throw new BadRequestException("attivitaAlternanza not found");
		}
		ValutazioneAttivitaReport valutazione = valutazioniManager.getValutazioneAttivitaReportByStudente(esperienzaSvoltaId, studenteId);
		List<PresenzaGiornaliera> presenze = presenzaGiornalieraManager.findByEsperienzaSvolta(esperienzaSvoltaId);
		int oreValidate = 0;
		int oreDaValidare = 0;
		for(PresenzaGiornaliera presenza : presenze) {
			if(presenza.getOreSvolte() > 0) {
				if(presenza.getVerificata()) {
					oreValidate += presenza.getOreSvolte();
				} else {
					oreDaValidare += presenza.getOreSvolte();
				}
			}
		}
		return new ReportDettaglioAttivitaEsperienza(aa, es, oreValidate, oreDaValidare, aa.getOre(), valutazione.getStato().toString());
	}
	
	public List<PresenzaGiornaliera> getPresenzeStudente(Long esperienzaSvoltaId, LocalDate dateFrom, 
			LocalDate dateTo) {
		String q = "SELECT pg FROM EsperienzaSvolta es, PresenzaGiornaliera pg WHERE es.id=(:esperienzaSvoltaId)"
				+ " AND pg.esperienzaSvoltaId=es.id AND pg.giornata >= (:dateFrom) AND pg.giornata <= (:dateTo) ORDER BY pg.giornata ASC";
		
		TypedQuery<PresenzaGiornaliera> query = em.createQuery(q, PresenzaGiornaliera.class);
		query.setParameter("esperienzaSvoltaId", esperienzaSvoltaId);
		query.setParameter("dateFrom", dateFrom);
		query.setParameter("dateTo", dateTo);
	
		List<PresenzaGiornaliera> list = query.getResultList();
		//fix duplicated days in db
		List<PresenzaGiornaliera> resultList = removeDuplicatedDays(list);		
		return resultList;
	}
	
	public List<PresenzaGiornaliera> aggiornaPresenze(Long esperienzaSvoltaId, List<PresenzaGiornaliera> presenze) {
		List<PresenzaGiornaliera> result = new ArrayList<>();
		for(PresenzaGiornaliera pg : presenze) {
			pg.setEsperienzaSvoltaId(esperienzaSvoltaId);
			PresenzaGiornaliera presenzaGiornaliera = presenzaGiornalieraManager.aggiornaPresenza(pg);
			if(presenzaGiornaliera != null) {
				result.add(presenzaGiornaliera);
			}
		}
		return result;
	}
	
	public void checkEsperienzeStudente(Long esperienzaSvoltaId, String studenteId)
			throws BadRequestException, UnauthorizedException {
		EsperienzaSvolta esperienzaSvolta = esperienzaSvoltaManager.findById(esperienzaSvoltaId);
		if(esperienzaSvolta == null) {
			throw new BadRequestException("esperienzaSvolta not found");
		}
		if(!studenteId.equals(esperienzaSvolta.getStudenteId())) {
			throw new UnauthorizedException("uuid not authorized");
		}
	}

	public ReportStudenteSommario getReportStudenteSommario(String studenteId) {
		ReportStudenteSommario report = new ReportStudenteSommario();

		Studente studente = findStudente(studenteId);
		
		Optional<PianoAlternanza> pianoForClassrom = pianoForClassrom(studente.getIstitutoId(), 
				studente.getCorsoDiStudio().getCourseId(), studente.getClassroom(), studente.getAnnoScolastico());
		if(pianoForClassrom.isPresent()) {
			report.setPianoId(pianoForClassrom.get().getId());
			report.setOreTotali(pianoForClassrom.get().getOreSecondoAnno() +
					pianoForClassrom.get().getOreTerzoAnno() + 
					pianoForClassrom.get().getOreQuartoAnno() + 
					pianoForClassrom.get().getOreQuintoAnno());
		}
		
		List<ReportEsperienzaStudente> esperienzeStudente = attivitaAlternanzaManager.getReportEsperienzaStudente(studenteId);
		int oreValidate = 0;
		int esperienzeConcluse = 0;
		int esperienzeInCorso = 0;
		int esperienzeNonCompletate = 0;
		for(ReportEsperienzaStudente reportEsp : esperienzeStudente) {
			oreValidate += reportEsp.getOreValidate();
			if(reportEsp.getStato().equals("archiviata")) {
				esperienzeConcluse++;
			}
			if(reportEsp.getStato().equals("in_corso")) {
				esperienzeInCorso++;
			}
			if(reportEsp.getStato().equals("revisione")) {
				esperienzeNonCompletate++;
			}
		}
		report.setOreValidate(oreValidate);
		report.setEsperienzeConcluse(esperienzeConcluse);
		report.setEsperienzeInCorso(esperienzeInCorso);
		report.setEsperienzeNonCompletate(esperienzeNonCompletate);
		return report;
	}
	
	public void attivaNotifica(String studenteId, String registrationToken) {
		NotificheStudente notifica = notificheStudenteRepository.findByStudenteIdAndRegistrationToken(studenteId, registrationToken);
		if(notifica == null) {
			notifica = new NotificheStudente();
			notifica.setStudenteId(studenteId);
			notifica.setRegistrationToken(registrationToken);
			notifica.setDataAttivazione(LocalDate.now());
			notificheStudenteRepository.save(notifica);
		}
	}
	
	public void disattivaNotifica(String studenteId, String registrationToken) {
		NotificheStudente notifica = notificheStudenteRepository.findByStudenteIdAndRegistrationToken(studenteId, registrationToken);
		if(notifica != null) {
			notificheStudenteRepository.delete(notifica);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<EsperienzaSvolta> getMancataCompilazioneDiarioStudenti(LocalDate giornata) {
		String qPresenze = "SELECT es,pg FROM "
				+ " AttivitaAlternanza aa LEFT JOIN EsperienzaSvolta es ON es.attivitaAlternanzaId = aa.id"
				+ " LEFT JOIN PresenzaGiornaliera pg ON pg.esperienzaSvoltaId = es.id"
				+ " WHERE aa.stato='attiva' AND (dataInizio <= (:giornata)) AND (dataFine >= (:giornata))"
				+ " AND es.studenteId IS NOT NULL"				
				+ " ORDER BY pg.giornata ASC, es.id ASC";
		Query query = em.createQuery(qPresenze);
		query.setParameter("giornata", giornata);
		List<Object[]> result = query.getResultList();
		Map<Long, EsperienzaSvolta> esperienzeMap = new HashMap<>();
		Map<Long, Boolean> compilazioneMap = new HashMap<>();
		for (Object[] obj : result) {
			EsperienzaSvolta es = (EsperienzaSvolta) obj[0];
			PresenzaGiornaliera pg = (PresenzaGiornaliera) obj[1];
			if(!esperienzeMap.containsKey(es.getId())) {
				esperienzeMap.put(es.getId(), es);
				compilazioneMap.put(es.getId(), Boolean.FALSE);
			}
			if(pg == null) {
				continue;
			}
			if(pg.getGiornata().isBefore(giornata)) {
				continue;
			}
			if(pg.getGiornata().isEqual(giornata)) {
				compilazioneMap.put(es.getId(), Boolean.TRUE);
			}
			if(pg.getGiornata().isAfter(giornata)) {
				break;
			}
		}
		List<EsperienzaSvolta> esperienzeStudenti = new ArrayList<>();
		compilazioneMap.forEach((k, v) -> {
			if(!v) {
				esperienzeStudenti.add(esperienzeMap.get(k));
			}
		});
		return esperienzeStudenti;
	}
	
	public List<String> sendNotificaEspereinzeStudenti(LocalDate giornata) {
		if(giornata == null) {
			giornata = LocalDate.now().minusDays(1);
		}
		List<EsperienzaSvolta> esperienzeStudenti = getMancataCompilazioneDiarioStudenti(giornata);
		String title = "EDIT - Notifica";
		String msg = "Ricordati di compilare il diario delle attività di ieri.";
		List<String> studenti = new ArrayList<>();
		esperienzeStudenti.forEach(esp -> {
			if(!studenti.contains(esp.getStudenteId())) {
				studenti.add(esp.getStudenteId());
			}
		});
		firebaseService.sendNotification(title, msg, studenti);
		if(logger.isInfoEnabled()) {
			logger.info("sendNotificaEspereinzeStudenti:" + studenti);
		}
		return studenti;
	}
	
	@Scheduled(cron = "0 00 05 * * ?")
	public void notificaEspereinzeStudenti() throws Exception {
		//sendNotificaEspereinzeStudenti(null); 
	}

	public Studente updateProfile(Studente studente, String studenteId) throws Exception {
		Studente studenteDB = studenteRepository.findById(studenteId).orElse(null);
		if(studenteDB == null) {
			throw new BadRequestException("studente non trovato");
		}
		studenteDB.setEmail(studente.getEmail());
		studenteDB.setPhone(studente.getPhone());
		studenteRepository.save(studenteDB);
		return studenteDB;
	}

	public Page<ReportStudenteEnte> findStudentiByEnte(String enteId, String text, Pageable pageRequest) {
		StringBuilder sb = new StringBuilder("SELECT DISTINCT s.id FROM AttivitaAlternanza aa, EsperienzaSvolta es, Studente s, Istituzione i, Convenzione c");
		sb.append(" WHERE aa.id=es.attivitaAlternanzaId AND es.studenteId=s.id AND aa.istitutoId=i.id");
		sb.append(" AND aa.enteId=(:enteId) AND (aa.tipologia=7 OR aa.tipologia=10)");
		sb.append(" AND c.istitutoId=aa.istitutoId AND c.enteId=(:enteId)");
		sb.append(" AND c.dataFine>=(:oggi) AND aa.dataFine>=(:unAnnoFa)");
		
		if (Utils.isNotEmpty(text)) {
			sb.append(" AND (UPPER(es.classeStudente) LIKE (:text) OR UPPER(s.surname) LIKE (:text) OR UPPER(s.name) LIKE (:text) OR UPPER(i.name) LIKE (:text))");
		}
		sb.append(" GROUP BY s.id ORDER BY MAX(aa.dataInizio) DESC");
		
		TypedQuery<String> query = em.createQuery(sb.toString(), String.class);
		query.setParameter("enteId", enteId);
		query.setParameter("oggi", LocalDate.now());
		query.setParameter("unAnnoFa", LocalDate.now().minusYears(1));		
		if(Utils.isNotEmpty(text)) {
			query.setParameter("text", "%" + text.trim().toUpperCase() + "%");
		}
		query.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
		query.setMaxResults(pageRequest.getPageSize());

		List<String> rows = query.getResultList();
		List<ReportStudenteEnte> list = new ArrayList<>();
		for (String studenteId : rows) {
			ReportStudenteEnte report = new ReportStudenteEnte();
			Studente studente = findStudente(studenteId);
			report.setStudente(studente);
			List<AttivitaAlternanza> attivitaList = attivitaAlternanzaManager.findAttivitaByStudenteAndEnte(studenteId, enteId);
			if(attivitaList.size() > 0) {
				report.setAttivitaAlternanza(attivitaList.get(0));
				Istituzione istituto = istituzioneManager.getIstituto(attivitaList.get(0).getIstitutoId(), null);
				report.setIstituto(istituto);
			}
			report.setStato(getStatoStudenteByEnte(attivitaList));
			list.add(report);
		}
		
		String counterQuery = sb.toString().replace("SELECT DISTINCT s.id", "SELECT COUNT(DISTINCT s.id)")
				.replace("GROUP BY s.id ORDER BY MAX(aa.dataInizio) DESC", "");
		Query cQuery = queryToCount(counterQuery,query);
		long total = (Long) cQuery.getSingleResult();
		Page<ReportStudenteEnte> page = new PageImpl<ReportStudenteEnte>(list, pageRequest, total);
		return page;
	}

	private String getStatoStudenteByEnte(List<AttivitaAlternanza> list) {
		boolean attivo = false;
		boolean inAttesa = false;
		LocalDate today = LocalDate.now();
		for(AttivitaAlternanza aa : list) {
			if(aa.getDataInizio().isBefore(today) && aa.getDataFine().isAfter(today)) {
				attivo = true;
			}
			if(aa.getDataInizio().isAfter(today)) {
				inAttesa = true;
			}
		}
		if(attivo) {
			return "attivo";
		}
		if(inAttesa) {
			return "in_attesa";
		}
		return "inattivo";
	}

	public ReportStudenteDettaglioEnte getStudenteDettaglioEnte(String studenteId, String enteId) {
		ReportStudenteDettaglioEnte report = new ReportStudenteDettaglioEnte();
		Studente studente = findStudente(studenteId);
		report.setStudente(studente);
		Istituzione istituto = istituzioneManager.getIstituto(studente.getIstitutoId(), null);
		if(istituto != null) {
			report.setIstituto(istituto.getName());
		}
		List<AttivitaAlternanza> list = attivitaAlternanzaManager.findAttivitaByStudenteAndEnte(studenteId, enteId);
		report.setAttivitaList(list);
		return report;
	}

}
