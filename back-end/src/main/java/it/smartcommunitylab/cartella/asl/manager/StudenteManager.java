package it.smartcommunitylab.cartella.asl.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
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
import it.smartcommunitylab.cartella.asl.model.CorsoDiStudio;
import it.smartcommunitylab.cartella.asl.model.CorsoDiStudioBean;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;
import it.smartcommunitylab.cartella.asl.model.NotificheStudente;
import it.smartcommunitylab.cartella.asl.model.PianoAlternanza;
import it.smartcommunitylab.cartella.asl.model.PresenzaGiornaliera;
import it.smartcommunitylab.cartella.asl.model.Registration;
import it.smartcommunitylab.cartella.asl.model.Studente;
import it.smartcommunitylab.cartella.asl.model.report.ReportDettaglioAttivitaEsperienza;
import it.smartcommunitylab.cartella.asl.model.report.ReportDettaglioStudente;
import it.smartcommunitylab.cartella.asl.model.report.ReportEsperienzaStudente;
import it.smartcommunitylab.cartella.asl.model.report.ReportStudenteRicerca;
import it.smartcommunitylab.cartella.asl.model.report.ReportStudenteSommario;
import it.smartcommunitylab.cartella.asl.repository.CorsoDiStudioRepository;
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
	private CorsoDiStudioRepository corsoDiStudioRepository;
	@Autowired
	private PresenzaGiornalieraManager presenzaGiornalieraManager;
	@Autowired
	private EsperienzaSvoltaManager esperienzaSvoltaManager;
	@Autowired
	private CompetenzaManager competenzaManager;
	@Autowired
	private NotificheStudenteRepository notificheStudenteRepository;
	@Autowired
	private FirebaseService firebaseService;
	
	private static final String STUDENT_REGISTRATION = "SELECT r0 FROM Registration r0 WHERE r0.studentId = (:id) AND r0.dateTo = (SELECT max(rm.dateTo) FROM Registration rm WHERE rm.studentId = (:id)) ";

	public Page<Studente> findStudentiPaged(String istitutoId, String corsoId, String annoScolastico, 
			String text, Pageable pageRequest) {
		StringBuilder sb = new StringBuilder(DataEntityManager.STUDENTS_AND_REGISTRATIONS_QUERY);

		if (Utils.isNotEmpty(corsoId)) {
			sb.append(" AND r0.courseId = (:courseId) ");
		}
		if (Utils.isNotEmpty(text)) {
			sb.append(" AND (UPPER(r0.classroom) LIKE (:text) OR UPPER(s0.surname) LIKE (:text) OR UPPER(s0.name) LIKE (:text))");
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
			String text, Pageable pageRequest) {
		Page<Studente> studentsProfiles = findStudentiPaged(istitutoId, corsoId, annoScolastico, text, pageRequest);

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

			List<EsperienzaSvolta> esList = esperienzeSvolteMap.get(s.getId());
			if(esList == null) {
				esList = new ArrayList<>();
			}

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
			}
							
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
			
			CorsoDiStudio corsoStudio = corsoDiStudioRepository.findCorsoDiStudioByIstituto(istitutoId, 
					s.getCorsoDiStudio().getCourseId(), s.getAnnoScolastico());
			if(corsoStudio != null) {
				studenteReport.setOreTotali(corsoStudio.getOreAlternanza());
			}

			studenteReport.getOreSvolteTerza().setHours(oreSvolteTerza);
			studenteReport.getOreSvolteTerza().setTotal(oreTotaliTerza);
			studenteReport.getOreSvolteQuarta().setHours(oreSvolteQuarta);
			studenteReport.getOreSvolteQuarta().setTotal(oreTotaliQuarta);
			studenteReport.getOreSvolteQuinta().setHours(oreSvolteQuinta);
			studenteReport.getOreSvolteQuinta().setTotal(oreTotaliQuinta);
			studenteReport.setOreValidate(oreSvolteTerza + oreSvolteQuarta + oreSvolteQuinta);
			studenteReport.setOreProgrammate(oreTotaliTerza + oreTotaliQuarta + oreTotaliQuinta);
			studenteReport.setClasse(s.getClassroom());
			studenteReport.setAnnoScolastico(annoScolastico);
			studentsRicerca.add(studenteReport);
		});

		Page<ReportStudenteRicerca> pagedstudentsRicerca = new PageImpl<>(studentsRicerca, pageRequest,
				studentsProfiles.getTotalElements());
		return pagedstudentsRicerca;
	}
	
	public List<ReportDettaglioStudente> getReportDettaglioStudente(String istitutoId, String annoScolastico, 
			String corsoId, String classe) {
		List<ReportDettaglioStudente> studentsRicerca = new ArrayList<>();
		Pageable pageRequest = PageRequest.of(0, 100);
		Page<Studente> studentsProfiles = findStudentiPaged(istitutoId, corsoId, annoScolastico, classe, pageRequest);
		studentsProfiles.forEach(s -> {
			if(classe.equalsIgnoreCase(s.getClassroom())) {
				ReportDettaglioStudente studenteReport = getReportDettaglioStudente(istitutoId, s.getId());
				studentsRicerca.add(studenteReport);				
			}
		});		
		return studentsRicerca;
	}

	private Optional<PianoAlternanza> pianoForClassrom(String istitutoId, String corsoDiStudioId, String classroom,
			String annoScolastico) {
		List<PianoAlternanza> pianiAttivi = pianoAlternanzaManager.findPianoAlternanzaAttivoForCorso(istitutoId,
				corsoDiStudioId);
		final LocalDate startAnnoScolastico = Utils.startAnnoScolasticoDate(annoScolastico);
		
		return pianiAttivi.stream()
				.filter(p -> p.getDataAttivazione().isBefore(startAnnoScolastico)
						|| p.getDataAttivazione().isEqual(startAnnoScolastico))
				.filter(p -> { 
					pianoAlternanzaManager.calculateAnni(p);
					return p.getAnni().contains(Utils.annoDiCorso(classroom));
				}).findFirst();
	}

	public Page<Studente> findStudenti(String cf, String text, Pageable pageRequest) {
		StringBuilder sb = new StringBuilder("SELECT DISTINCT st FROM Studente st");

		if (cf != null) {
			sb.append(" AND st.cf = (:cf) ");
		}
		if (text != null && !text.isEmpty()) {
			sb.append(" AND (lower(st.name) LIKE (:text) OR lower(st.surname) LIKE (:text))");
		}

		sb.append(" ORDER BY st.surname, st.name");

		String q = sb.toString();
		q = q.replaceFirst(" AND ", " WHERE ");

		TypedQuery<Studente> query = em.createQuery(q, Studente.class);

		if (cf != null) {
			query.setParameter("cf", cf);
		}
		if (text != null) {
			query.setParameter("text", "%" + text.trim() + "%");
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

	public ReportDettaglioStudente getReportDettaglioStudente(String istitutoId, String studenteId) {
		ReportDettaglioStudente result = new ReportDettaglioStudente();
		Studente studente = findStudente(studenteId);
		result.setStudente(studente);
		
		List<ReportEsperienzaStudente> esperienzeStudente = attivitaAlternanzaManager.getReportEsperienzaStudente(istitutoId, 
				studenteId);
		result.getEsperienze().addAll(esperienzeStudente);
		
		List<Competenza> competenze = competenzaManager.getCompetenzeByStudente(istitutoId, studenteId);
		result.getCompetenze().addAll(competenze);
		
		CorsoDiStudio corsoStudio = corsoDiStudioRepository.findCorsoDiStudioByIstituto(istitutoId, 
				studente.getCorsoDiStudio().getCourseId(), Utils.annoScolastico(new Date()));
		if(corsoStudio != null) {
			result.setOreTotali(corsoStudio.getOreAlternanza());
		}

		Optional<PianoAlternanza> pianoForClassrom = pianoForClassrom(istitutoId, 
				studente.getCorsoDiStudio().getCourseId(), studente.getClassroom(), studente.getAnnoScolastico());
		if(pianoForClassrom.isPresent()) {
			result.setTitoloPiano(pianoForClassrom.get().getTitolo());
			result.setPianoId(pianoForClassrom.get().getId());
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

	public Page<ReportEsperienzaStudente> getReportEsperienzaStudenteList(String studenteId, Pageable pageRequest) {
		Page<ReportEsperienzaStudente> page = attivitaAlternanzaManager.getReportEsperienzaStudente(studenteId, pageRequest);
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
		return new ReportDettaglioAttivitaEsperienza(aa, es, oreValidate, oreDaValidare, aa.getOre());
	}
	
	public List<PresenzaGiornaliera> getPresenzeStudente(Long esperienzaSvoltaId, LocalDate dateFrom, 
			LocalDate dateTo) {
		String q = "SELECT pg FROM EsperienzaSvolta es, PresenzaGiornaliera pg WHERE es.id=(:esperienzaSvoltaId)"
				+ " AND pg.esperienzaSvoltaId=es.id AND pg.giornata >= (:dateFrom) AND pg.giornata <= (:dateTo) ORDER BY pg.giornata ASC";
		
		TypedQuery<PresenzaGiornaliera> query = em.createQuery(q, PresenzaGiornaliera.class);
		query.setParameter("esperienzaSvoltaId", esperienzaSvoltaId);
		query.setParameter("dateFrom", dateFrom);
		query.setParameter("dateTo", dateTo);
	
		List<PresenzaGiornaliera> resultList = query.getResultList();
		return resultList;
	}
	
	public void aggiornaPresenze(Long esperienzaSvoltaId, List<PresenzaGiornaliera> presenze) {
		for(PresenzaGiornaliera pg : presenze) {
			pg.setEsperienzaSvoltaId(esperienzaSvoltaId);
			presenzaGiornalieraManager.aggiornaPresenza(pg);
		}
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
		
		CorsoDiStudio corsoStudio = corsoDiStudioRepository.findCorsoDiStudioByIstituto(studente.getIstitutoId(), 
				studente.getCorsoDiStudio().getCourseId(), Utils.annoScolastico(new Date()));
		if(corsoStudio != null) {
			report.setOreTotali(corsoStudio.getOreAlternanza());
		}
		
		List<ReportEsperienzaStudente> esperienzeStudente = attivitaAlternanzaManager.getReportEsperienzaStudente(studenteId);
		int oreValidate = 0;
		int oreTotali = 0;
		int esperienzeConcluse = 0;
		int espereinzeInCorso = 0;
		for(ReportEsperienzaStudente reportEsp : esperienzeStudente) {
			oreValidate += reportEsp.getOreValidate();
			oreTotali += reportEsp.getOreTotali();
			if(reportEsp.getStato().equals("archiviata")) {
				esperienzeConcluse++;
			}
			if(reportEsp.getStato().equals("in_corso")) {
				esperienzeConcluse++;
			}
		}
		report.setOreTotali(oreTotali);
		report.setOreValidate(oreValidate);
		report.setEsperienzeConcluse(esperienzeConcluse);
		report.setEspereinzeInCorso(espereinzeInCorso);
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
		sendNotificaEspereinzeStudenti(null); 
	}

}
