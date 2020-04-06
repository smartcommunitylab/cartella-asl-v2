package it.smartcommunitylab.cartella.asl.manager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.Azienda;
import it.smartcommunitylab.cartella.asl.model.CorsoDiStudio;
import it.smartcommunitylab.cartella.asl.model.Documento;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;
import it.smartcommunitylab.cartella.asl.model.Istituzione;
import it.smartcommunitylab.cartella.asl.model.Offerta;
import it.smartcommunitylab.cartella.asl.model.ReferenteAlternanza;
import it.smartcommunitylab.cartella.asl.model.Registration;
import it.smartcommunitylab.cartella.asl.model.Studente;
import it.smartcommunitylab.cartella.asl.model.TipologiaAttivita;
import it.smartcommunitylab.cartella.asl.model.TipologiaTipologiaAttivita;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;
import it.smartcommunitylab.cartella.asl.repository.ASLUserRepository;
import it.smartcommunitylab.cartella.asl.repository.AssociazioneCompetenzeRepository;
import it.smartcommunitylab.cartella.asl.repository.AttivitaAlternanzaRepository;
import it.smartcommunitylab.cartella.asl.repository.AziendaRepository;
import it.smartcommunitylab.cartella.asl.repository.CompetenzaRepository;
import it.smartcommunitylab.cartella.asl.repository.ConsensoRepository;
import it.smartcommunitylab.cartella.asl.repository.CorsoDiStudioRepository;
import it.smartcommunitylab.cartella.asl.repository.DocumentoRepository;
import it.smartcommunitylab.cartella.asl.repository.EsperienzaAllineamentoRepository;
import it.smartcommunitylab.cartella.asl.repository.EsperienzaSvoltaRepository;
import it.smartcommunitylab.cartella.asl.repository.IstituzioneRepository;
import it.smartcommunitylab.cartella.asl.repository.OffertaRepository;
import it.smartcommunitylab.cartella.asl.repository.PianoAlternanzaRepository;
import it.smartcommunitylab.cartella.asl.repository.ReferenteAlternanzaRepository;
import it.smartcommunitylab.cartella.asl.repository.RegistrationRepository;
import it.smartcommunitylab.cartella.asl.repository.StudenteRepository;
import it.smartcommunitylab.cartella.asl.repository.TipologiaAttivitaRepository;
import it.smartcommunitylab.cartella.asl.repository.TipologiaTipologiaAttivitaRepository;
import it.smartcommunitylab.cartella.asl.util.Constants;
import it.smartcommunitylab.cartella.asl.util.ErrorLabelManager;

@Repository
@Transactional
@Validated
public class EntitiesManager {
	
	private static final transient Log logger = LogFactory.getLog(EntitiesManager.class);
	
	@Autowired
	protected EntityManager em;	
	
	@Autowired
	protected PianoAlternanzaRepository pianoAlternanzaRepository;	
	
	@Autowired
	protected AssociazioneCompetenzeRepository associazioneCompetenzeRepository;

	@Autowired
	protected TipologiaAttivitaRepository tipologiaAttivitaRepository;		
	
	@Autowired
	protected TipologiaTipologiaAttivitaRepository tipologiaTipologiaAttivitaRepository;		
	
	@Autowired
	protected CompetenzaRepository competenzaRepository;	
	
	@Autowired
	protected AttivitaAlternanzaRepository attivitaAlternanzaRepository;

	@Autowired
	protected OffertaRepository offertaRepository;	
	
	@Autowired
	protected EsperienzaSvoltaRepository esperienzaSvoltaRepository;
	
	@Autowired
	protected EsperienzaAllineamentoRepository esperienzaAllineamentoRepository;
	
	@Autowired
	protected StudenteRepository studenteRepository;		
	
	@Autowired
	protected ReferenteAlternanzaRepository referenteAlternanzaRepository;			

	@Autowired
	protected DocumentoRepository documentoRepository;	
	
	@Autowired
	protected AziendaRepository aziendaRepository;	
	
	@Autowired
	protected CorsoDiStudioRepository corsoDiStudioRepository;
	
	@Autowired
	protected RegistrationRepository registrationRepository;	
	
	@Autowired
	protected IstituzioneRepository istituzioneRepository;
	
	@Autowired
	protected ASLUserRepository aslUserRepository;	
	
	@Autowired
	private ErrorLabelManager errorLabelManager;
	
	@Autowired
	protected AuditManager auditManager;
	
	@Autowired
	protected ConsensoRepository consensoRepository;
	
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	
	public void reset() {
		pianoAlternanzaRepository.deleteAll();
		tipologiaAttivitaRepository.deleteAll();
		attivitaAlternanzaRepository.deleteAll();
		esperienzaSvoltaRepository.deleteAll();
		documentoRepository.deleteAll();		
		offertaRepository.deleteAll();	
		
	}

	
	// AttivitaAlternanza
	public AttivitaAlternanza saveAttivitaAlternanza(AttivitaAlternanza aa) {
		return attivitaAlternanzaRepository.save(aa);
	}
	
	public AttivitaAlternanza getAttivitaAlternanza(long id, boolean addStudenti) {
		AttivitaAlternanza aa =  attivitaAlternanzaRepository.getOne(id);
		
		if (aa != null && addStudenti) {
//			List<EsperienzaSvolta> ess = findEsperienzaSvoltaByAttivitaAlternanza(aa);
// NAWAZ			aa.getStudenti().addAll(ess.stream().filter(x -> x.getStudente() != null).map(x -> x.getStudente()).collect(Collectors.toSet()));
		}
		
		return aa;
	}
	
//	public Page<AttivitaAlternanza> findAttivitaAlternanzaByAnnoAlternanzaId(long aaId, Pageable pageRequest) {
//		return attivitaAlternanzaRepository.findAttivitaAlternanzaByAnnoAlternanzaId(aaId, pageRequest);
//	}	
	
	public List<AttivitaAlternanza> getAttivitaAlternanza(List<Long> ids) {
		return attivitaAlternanzaRepository.findAllById(ids);
	}	
	
//	public void updateAttivitaAlternanza(AttivitaAlternanza aa) {
//		attivitaAlternanzaRepository.update(aa);
//	}	
	
/** NAWAZ	public void updateAttivitaAlternanzaAtCurrentDate(long id, AttivitaAlternanzaStub aaStub) throws BadRequestException, ParseErrorException {
		AttivitaAlternanza aa = attivitaAlternanzaRepository.getOne(id);
		
		if (aa == null) {
			throw new BadRequestException(errorLabelManager.get("attivita.alt.error.notfound"));
		}		
		
		// CARTELLA-323 - alignment with registration data check.
		Date upperAA = new Date(aaStub.getDataInizio());
		Date lowerAA = new Date(aaStub.getDataFine());
		for (String studenteId : aaStub.getStudentiId()) {
			if (!validDatesAA(aa.getCorsoId(), aa.getIstitutoId(), aa.getAnnoScolastico(),
					String.valueOf(aa.getAnnoCorso()), studenteId, upperAA, lowerAA)) {
				throw new BadRequestException(errorLabelManager.get("registration.date.error"));
			}
		}
		
		if (aaStub.getAnnoScolastico() != null) {
			aa.setAnnoScolastico(aaStub.getAnnoScolastico());
		}
		if (aaStub.getDataInizio() != null) {
			aa.setDataInizio(aaStub.getDataInizio());
		}
		if (aaStub.getDataFine() != null) { 
			aa.setDataFine(aaStub.getDataFine());
		}
		if (aaStub.getCompetenzeId() != null && !aaStub.getCompetenzeId().isEmpty()) {
			aa.setCompetenzeFromSet(Sets.newHashSet(competenzaRepository.findAll(aaStub.getCompetenzeId())));
		}	
		if (aaStub.getOre() != null) {
			aa.setOre(aaStub.getOre());
		}
		if (aaStub.getReferenteScuola() != null) {
			aa.setReferenteScuola(aaStub.getReferenteScuola());
		}	
		if (aaStub.getReferenteScuolaCF() != null) {
			aa.setReferenteScuolaCF(aaStub.getReferenteScuolaCF());
		}		
		if (aaStub.getTags() != null) {
			aa.setTags(aaStub.getTags());
		}
		if (aaStub.getTitolo() != null) {
			aa.setTitolo(aaStub.getTitolo());
		}		
		
		if (aaStub.getStudentiId() != null) {
			List<Studente> newStudenti = studenteRepository.findAll(aaStub.getStudentiId());

			List<EsperienzaSvolta> ess = esperienzaSvoltaRepository.findEsperienzaSvoltaByAttivitaAlternanza(aa);
			List<Studente> oldStudenti = ess.stream().map(x -> x.getStudente()).collect(Collectors.toList());

			List<EsperienzaSvolta> essToRemove = Lists.newArrayList();
			List<Studente> studentiToAdd = Lists.newArrayList();

			for (EsperienzaSvolta es : ess) {
				if (!newStudenti.contains(es.getStudente())) {
					essToRemove.add(es);
				}
			}

			for (Studente sta : newStudenti) {
				if (!oldStudenti.contains(sta)) {
					studentiToAdd.add(sta);
				}
			}

			if (aa.getOpportunita() != null) {
				boolean available = decreasePostiRimanenti(aa.getOpportunita(), essToRemove.size(),
						studentiToAdd.size());
				if (!available) {
					throw new BadRequestException(errorLabelManager.get("opportunita.error.posti"));
				}
				oppCorsoRepository.save(aa.getOpportunita());
			}

			for (EsperienzaSvolta es : ess) {
				if (!essToRemove.contains(es.getStudente())) {
					// CARTELLA-317 (ALIGN GIORNATE PER DATA MODIFICATA)
					try {
						alignPresenzeData(es, aaStub.getDataInizio(), aaStub.getDataFine());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						throw new ParseErrorException(e.getMessage());
					}
				}
			}

			for (Studente studente : studentiToAdd) {
				EsperienzaSvolta es = new EsperienzaSvolta();

				es.setStudente(studente);
				es.setAttivitaAlternanza(aa);

				if (aaStub.getCompetenzeId() != null && !aaStub.getCompetenzeId().isEmpty()) {
					es.setCompetenzeFromSet(Sets.newHashSet(competenzaRepository.findAll(aaStub.getCompetenzeId())));
				}

				// save teaching unit Id inside ES(CARTELLA-316).
				es.setTeachingUnitId(getLastRegistration(aa.getCorsoId(), aa.getIstitutoId(), aa.getAnnoScolastico(),
						String.valueOf(aa.getAnnoCorso()), studente.getId()).getTeachingUnitId());

				es = saveEsperienzaSvolta(es);
			}

			for (EsperienzaSvolta es : essToRemove) {
				es.setAttivitaAlternanza(null);
				es.setCompetenze(null);
				esperienzaSvoltaRepository.save(es);
				esperienzaSvoltaRepository.delete(es);
			}

			attivitaAlternanzaRepository.save(aa);
		}
		
	}		
	
	private void alignPresenzeData(EsperienzaSvolta es, Long dataInizio, Long dataFine) throws ParseException {
		Set<PresenzaGiornaliera> removeGiornateList = Sets.newHashSet();
		Date upper = new Date(dataInizio);
		Date lower = new Date(dataFine);
		for (PresenzaGiornaliera pg: es.getPresenze().getGiornate()) {
			Date date = format.parse(pg.getData());
			if ( (date.after(upper) || date.equals(upper)) && (date.equals(lower) || date.before(lower))) {
				continue;
			}
			removeGiornateList.add(pg);
		}
		es.getPresenze().getGiornate().removeAll(removeGiornateList);
		esperienzaSvoltaRepository.save(es);		
	}

	public void deleteAttivitaAlternanza(Long id) throws BadRequestException {
		AttivitaAlternanza aa = attivitaAlternanzaRepository.getOne(id);
		
		if (aa == null) {
			throw new BadRequestException(errorLabelManager.get("attivita.alt.error.notfound"));
		}		
		
		List<EsperienzaSvolta> ess = esperienzaSvoltaRepository.findEsperienzaSvoltaByAttivitaAlternanza(aa);
		
		Opportunita opp = aa.getOpportunita();
		if (opp != null) {
			opp.setPostiRimanenti(opp.getPostiRimanenti() + ess.size());
			oppCorsoRepository.save(opp);
		}		
		
		aa.setAnnoAlternanza(null);
		aa.setCorsoInterno(null);
		aa.setOpportunita(null);
		attivitaAlternanzaRepository.save(aa);
		
		
		for (EsperienzaSvolta es: ess) {
			es.setAttivitaAlternanza(null);
			es.setCompetenze(null);
			esperienzaSvoltaRepository.save(es);
			esperienzaSvoltaRepository.delete(es);
		}
		
		attivitaAlternanzaRepository.delete(aa);
	} **/	
	
//	public Page<AttivitaAlternanza> findAttivitaAlternanzaByIstitutoIdAndTags(String istitutoId, String tag, Pageable pageRequest) {
//		return attivitaAlternanzaRepository.findAttivitaAlternanzaByIstitutoIdAndTags(istitutoId, Splitter.on(",").splitToList(tag), pageRequest);
//	}
//	
//	public void setCompletata(long id, boolean completata) {
//		attivitaAlternanzaRepository.updateCompletata(id, completata);
//	}
	
	// TipologiaAttivita
	public TipologiaAttivita saveTipologiaAttivita(TipologiaAttivita ta) {
		return tipologiaAttivitaRepository.save(ta);
	}
	
	public TipologiaAttivita getTipologiaAttivita(long id) {
		return tipologiaAttivitaRepository.getOne(id);
	}
	
	public List<TipologiaAttivita> getTipologieAttivita(List<Long> ids) {
		return tipologiaAttivitaRepository.findAllById(ids);
	}	
	
	public List<TipologiaAttivita> getTipologieAttivita() {
		return tipologiaAttivitaRepository.findAll();
	}		
	
	/** NAWAZ public void updateTipologiaAttivita(TipologiaAttivita ta, Long annoAlternanzaId) throws BadRequestException {
		TipologiaAttivita oldTa = tipologiaAttivitaRepository.getOne(ta.getId());
		
		if (oldTa != null) {
			AnnoAlternanza aaFrom = annoAlternanzaRepository.findAnnoAlternanzaByTipologieAttivita(oldTa);
			
			if (aaFrom == null) {
				throw new BadRequestException(errorLabelManager.get("attivita.alt.error.notfound"));
			}			
			
			AuditEntry audit = new AuditEntry("update", AnnoAlternanza.class, aaFrom.getId(), null, new Object(){});
			auditManager.save(audit);				
			
			if (!aaFrom.getTipologieAttivita().remove(oldTa)) {
				throw new BadRequestException(errorLabelManager.get("anno.alt.error.empty.tipo"));
			}
			annoAlternanzaRepository.save(aaFrom);
		}
		AnnoAlternanza aaTo = annoAlternanzaRepository.getOne(annoAlternanzaId);
		
		if (aaTo == null) {
			throw new BadRequestException(errorLabelManager.get("attivita.alt.error.notfound"));
		}		
		
		aaTo.getTipologieAttivita().add(ta);
		
		annoAlternanzaRepository.save(aaTo);
		
		AuditEntry audit = new AuditEntry("update", AnnoAlternanza.class, aaTo.getId(), null, new Object(){});
		auditManager.save(audit);						
		
		// TODO check
//		tipologiaAttivitaRepository.update(ta);
		
	
		
	}	*/
	
	public void deleteTipologiaAttivita(long id) {
		tipologiaAttivitaRepository.deleteById(id);
	}	
	
	// TipologiaTipologiaAttivita
	
	public TipologiaTipologiaAttivita getTipologiaTipologiaAttivita(long id) {
		return tipologiaTipologiaAttivitaRepository.getOne(id);
	}		
	
	public List<TipologiaTipologiaAttivita> findTipologiaTipologiaAttivitaByTipologia(List<Integer> tipologie) {
		return tipologiaTipologiaAttivitaRepository.findByTipologia(tipologie);
	}		
	
	public List<TipologiaTipologiaAttivita> getTipologieTipologiaAttivita() {
		return tipologiaTipologiaAttivitaRepository.findAll();
	}	
	
	/** NAWAZ // Opportunita
	public Offerta saveOpportunita(Offerta o) {
		fillOpportunita(o);
		return opportunitaRepository.save(o);
	}
	
	private void fillOpportunita(Opportunita o) {
		if (o.getAzienda() != null && o.getAzienda().getId() != null) {
			Azienda az = getAzienda(o.getAzienda().getId());
			o.setAzienda(az);
			if (o.getCoordinate() == null || o.getCoordinate().hasDefaultValue()) {
				o.setCoordinate(az.getCoordinate());
			}
		}
		o.setPostiRimanenti(o.getPostiDisponibili());
//		if (o.getReferenteAzienda() != null && o.getReferenteAzienda().getId() != null) {
//			ReferenteAzienda ref = getReferenteAzienda(o.getReferenteAzienda().getId());
//			o.setReferenteAzienda(ref);
//		}
	}	
	
	public Opportunita getOpportunita(long id) {
		return opportunitaRepository.getOne(id);
	}
	
	public void updateOpportunita(Opportunita o) {
//		opportunitaRepository.update(o);
		
		Opportunita old = opportunitaRepository.getOne(o.getId());
		
		if (old == null) {
			opportunitaRepository.save(o);
		} else {
			old.setCoordinate(o.getCoordinate());
			old.setDataFine(o.getDataFine());
			old.setOraFine(o.getOraFine());
			old.setDataInizio(o.getDataInizio());
			old.setOraInizio(o.getOraInizio());			
			old.setDescrizione(o.getDescrizione());
			old.setIndividuale(o.isIndividuale());
			old.setInterna(o.isInterna());
			old.setOre(o.getOre());
			old.setPostiDisponibili(o.getPostiDisponibili());
			old.setPostiRimanenti(o.getPostiRimanenti());
			old.setPrerequisiti(o.getPrerequisiti());
			old.setReferente(o.getReferente());
			old.setReferenteCF(o.getReferenteCF());
			old.setTipologia(o.getTipologia());
			old.setTitolo(o.getTitolo());
			old.setAzienda(o.getAzienda());
			
			opportunitaRepository.save(old);
		}
	}	
	
	public void deleteOpportunita(long id) throws BadRequestException {
		Opportunita o = getOpportunita(id);

		if (o == null) {
			throw new BadRequestException(errorLabelManager.get("opportunita.error.notfound"));
		}

		// check if attivita alternanza has used this oppurtunita.
		List<AttivitaAlternanza> aAList = attivitaAlternanzaRepository.findAttivitaAlternanzaByOpportunita(o);

		if (aAList != null && !aAList.isEmpty()) {
			throw new BadRequestException(errorLabelManager.get("opportunita.error.associato"));
		}

		o.setCompetenzeFromSet(Sets.newHashSet());
		saveOpportunita(o);
		List<AttivitaAlternanza> aas = attivitaAlternanzaRepository.findAttivitaAlternanzaByOpportunita(o);
		aas.forEach(x -> {
			x.setOpportunita(null);
			saveAttivitaAlternanza(x);
		});
		opportunitaRepository.delete(id);
	}		**/
	
	// EsperienzaSvolta
	public EsperienzaSvolta saveEsperienzaSvolta(EsperienzaSvolta es) {
		return esperienzaSvoltaRepository.save(es);
	}
	
	public EsperienzaSvolta getEsperienzaSvolta(long id) {
		return esperienzaSvoltaRepository.getOne(id);
	}
	
//	public void updateEsperienzaSvolta(EsperienzaSvolta es) {
//		esperienzaSvoltaRepository.update(es);
//	}	
	
	public void deleteEsperienzaSvolta(long id) {
		esperienzaSvoltaRepository.deleteById(id);
	}	
	
//	public List<EsperienzaSvolta> findEsperienzaSvoltaByAttivitaAlternanza(AttivitaAlternanza aa) {
//		return esperienzaSvoltaRepository.findEsperienzaSvoltaByAttivitaAlternanza(aa);
//	}
//	
//	public List<EsperienzaSvolta> findEsperienzaSvoltaByStudente(Studente studente) {
//		return esperienzaSvoltaRepository.findEsperienzaSvoltaByStudente(studente);
//	}	
//	
	// Studente
	public Studente saveStudente(Studente s) {
		return studenteRepository.save(s);
	}
	
//	public Studente getStudente(String id) {
//		return findS
//	}
	
	public List<Studente> getStudenti(List<String> ids) {
		return studenteRepository.findAllById(ids);
	}	
	
	public void updateStudente(Studente s) {
		studenteRepository.update(s);
	}	
	
	public void deleteStudente(String id) {
		studenteRepository.deleteById(id);
	}
	
//	public List<Studente> findStudentiByClasse(String classe, String corsoDiStudioId, String istitutoId, String annoScolastico) {
//		List<Object[]> srs = studenteRepository.findStudentiByClasse(classe, corsoDiStudioId, istitutoId, annoScolastico);
//		List<Studente> result = Lists.newArrayList();
//		
//		for (Object[] sr: srs) {
//			Studente s = (Studente)sr[0];
//			Registration r = (Registration)sr[0];
//			s.setClassroom(r.getClassroom());
//			s.setIstitutoId(r.getInstituteId());
//			
//			result.add(s);
//		}
//		
//		return result;
//	}
	
	
	public String findStudenteCorsoId(String id) {
		return studenteRepository.findStudenteCorsoId(id);
	}

//	public String findStudenteClassroom(String id) {
//		return studenteRepository.findStudenteClassroom(id);
//	}	
	
	public String findStudenteClassroomBySchoolYear(String id, String schoolYear, String instituteId, String courseId) {
		return studenteRepository.findStudenteClassroomBySchoolYear(id, schoolYear, instituteId, courseId);
	}
	
	// ReferenteAlternanza
	public ReferenteAlternanza saveReferenteAlternanza(ReferenteAlternanza ra) {
		return referenteAlternanzaRepository.save(ra);
	}
	
	public ReferenteAlternanza getReferenteAlternanza(String id) {
		return referenteAlternanzaRepository.getOne(id);
	}
	
	public void updateReferenteAlternanza(ReferenteAlternanza ra) {
		referenteAlternanzaRepository.update(ra);
	}	
	
	public void deleteReferenteAlternanza(String id) {
		referenteAlternanzaRepository.deleteById(id);
	}		
	
	// Documento
	public Documento saveDocumento(Documento d) {
		return documentoRepository.save(d);
	}
	
//	public Documento getDocumento(String id) {
//		return documentoRepository.findDocumentoById(id);
//	}
//	
//	public void updateDocumento(Documento d) {
//		documentoRepository.update(d);
//	}	
//	
//	public void deleteDocumento(String id) throws BadRequestException {
//		Documento doc = documentoRepository.findDocumentoById(id);
//		if (doc != null) {
//			documentoRepository.delete(doc);
//		} else {
//			throw new BadRequestException(errorLabelManager.get("doc.error.notfound"));
//		}
//	}		
	
		
	// Azienda
	
	// Corso Di Studio
	
	public CorsoDiStudio getCorsoDiStudio(String istitutoId, String annoScolastico, String corsoId) {
		return corsoDiStudioRepository.findCorsoDiStudioByIstitutoIdAndAnnoScolasticoAndCourseId(istitutoId, annoScolastico, corsoId);
	}
	
	
	// Registration
	public List<String> getClasses(String courseId, String instituteId, String schoolYear, String annoCorso) {
		List<String> classi = registrationRepository.getClasses(courseId, instituteId, schoolYear);
		if (annoCorso != null) {
			classi.removeIf(x -> !x.startsWith(annoCorso));
		}
		return classi;
	}

	public Istituzione findIstituto(String istitutoId) throws BadRequestException {

		Istituzione ist = istituzioneRepository.getOne(istitutoId);
		if (ist == null) {
			throw new BadRequestException(errorLabelManager.get("istituto.error.notfound"));
		}
		return ist;
	}
	
	//
	/** NAWAZ
	public List<OppCorso> findOppCorsoNear(double lat, double lon, double distance) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
	    CriteriaQuery<OppCorso> cq1 = cb.createQuery(OppCorso.class);
	    Root<OppCorso> oppcorso = cq1.from(OppCorso.class);

	    double t1 = distance / Math.abs(Math.cos(Math.toRadians(lat)) * Constants.KM_IN_ONE_LAT);
	    double t2 = distance / Constants.KM_IN_ONE_LAT;

	    double lonA = lon - t1;
	    double lonB = lon + t1;

	    double latA = lat - t2;
	    double latB = lat + t2;
	    
	    Predicate predicate1 = cb.between(oppcorso.get("latitude").as(Double.class), latA, latB);
	    Predicate predicate2 = cb.between(oppcorso.get("longitude").as(Double.class), lonA, lonB);
	    Predicate predicate = cb.and(predicate1, predicate2);
	    
	    cq1.where(predicate);
	    
	    TypedQuery<OppCorso> query1 = em.createQuery(cq1);
	    
	    List<OppCorso> result = query1.getResultList(); 
		
//	    result = result.stream().filter(x -> Utils.harvesineDistance(x.getLatitude(), x.getLongitude(), lat, lon) <= distance).collect(Collectors.toList());
	    
	    return result;
		
	}		**/
	
	public List<Azienda> findAziendaNear(double lat, double lon, double distance) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
	    CriteriaQuery<Azienda> cq1 = cb.createQuery(Azienda.class);
	    Root<Azienda> azienda = cq1.from(Azienda.class);

	    double t1 = distance / Math.abs(Math.cos(Math.toRadians(lat)) * Constants.KM_IN_ONE_LAT);
	    double t2 = distance / Constants.KM_IN_ONE_LAT;

	    double lonA = lon - t1;
	    double lonB = lon + t1;

	    double latA = lat - t2;
	    double latB = lat + t2;
	    
	    Predicate predicate1 = cb.between(azienda.get("latitude").as(Double.class), latA, latB);
	    Predicate predicate2 = cb.between(azienda.get("longitude").as(Double.class), lonA, lonB);
	    Predicate predicate = cb.and(predicate1, predicate2);
	    
	    cq1.where(predicate);
	    
	    TypedQuery<Azienda> query1 = em.createQuery(cq1);
		
	    List<Azienda> result = query1.getResultList(); 
		
	    return result;
	}	

	// ASL User
	public ASLUser saveASLUser(ASLUser user) {
		return aslUserRepository.save(user);
	}
	
	
	
	
	
	
	
	
	

	
//	public List<ASLUser> createStudentiASLUsers(String ids) {
//		List<String> sIds = Splitter.on(",").splitToList(ids);
//		
//		List<Studente> studenti = studenteRepository.findStudentiByIds(sIds);
//		
//		return studenti.stream().map(x -> {
//			ASLUser user = new ASLUser();
//			user.setCf(x.getCf());
//			user.setName(x.getName());
//			user.setSurname(x.getSurname());
//			user.getRoles().add(ASLRole.STUDENTE);
//			user.getStudentiId().add(x.getId());
//			
//			System.err.println(user);
//			return user;
//		}).collect(Collectors.toList());
//		
//	}
	
	// Note azienda
	
	public EsperienzaSvolta saveNoteStudente(long id, String note) throws BadRequestException {

		EsperienzaSvolta esp = getEsperienzaSvolta(id);
		if (esp == null) {
			throw new BadRequestException(errorLabelManager.get("esp.error.notfound"));
		}

//		esp.setNoteStudente(note);
		// save.
		saveEsperienzaSvolta(esp);

		return esp;
	}
	
	public EsperienzaSvolta saveNoteAzienda(long id, String note) throws BadRequestException {

		EsperienzaSvolta esp = getEsperienzaSvolta(id);
		if (esp == null) {
			throw new BadRequestException(errorLabelManager.get("esp.error.notfound"));
		}

//		esp.setNoteAzienda(note);
		// save.
		saveEsperienzaSvolta(esp);

		return esp;
	}	
	
	// Istituzione
	
	public Double getIstituzioneHoursThreshold(String id) {
		Double ht = istituzioneRepository.findIstitutoHoursThreshold(id);
		if (ht == null) {
			ht = 0.2;
		}
		return ht;
	}
	
	
	// delegates
	
//	public String findAnnoAlternanzaIstitutoId(Long id) {
//		return annoAlternanzaRepository.findAnnoAlternanzaIstitutoId(id);
//	}	
//	
//	public String findTipologiaAttivitaIstitutoId(Long id) {
//		return tipologiaAttivitaRepository.findTipologiaAttivitaIstitutoId(id);
//	}
//	
	public String findPianoAlternanzaIstitutoId(Long id) {
		return pianoAlternanzaRepository.findPianoAlternanzaIstitutoId(id);
	}

//	public String findAttivitaAlternanzaIstitutoId(Long id) {
//		return attivitaAlternanzaRepository.findAttivitaAlternanzaIstitutoId(id);
//	}

//	public String findEsperienzaSvoltaIstitutoId(Long id) {
//		return esperienzaSvoltaRepository.findEsperienzaSvoltaIstitutoId(id);
//	}
//	
//	public String findEsperienzaSvoltaAziendaId(Long id) {
//		return esperienzaSvoltaRepository.findEsperienzaSvoltaAziendaId(id);
//	}

	public String findCorsoDiStudioIstitutoId(String id) {
		return corsoDiStudioRepository.findCorsoDiStudioIstitutoId(id);
	}

	public String findStudenteIstitutoId(String id) {
		return studenteRepository.findStudenteIstitutoId(id);
	}

//	public String findOpportunitaAziendaId(Long id) {
//		return opportunitaRepository.findOpportunitaAziendaId(id);
//	}

//	public String findEsperienzaSvoltaStudenteId(Long id) {
//		return esperienzaSvoltaRepository.findEsperienzaSvoltaStudenteId(id);
//	}

//	public String findDocumentoStudenteId(String docId) {
//		return documentoRepository.findDocumentoStudenteId(docId);
//	}
//	
	/**
	 * 
	 * @param opportunita
	 * @param removed integer (removed students)
	 * @param used integer (assigned students)
	 * @return
	 */
	public boolean decreasePostiRimanenti(Offerta opportunita, int removed, int used) {
		if ((opportunita.getPostiRimanenti() + removed - used) < 0) {
			return false;
		}
		opportunita.setPostiRimanenti(opportunita.getPostiRimanenti() + removed - used);
		return true;
	}
	
	/**
	 * Teaching unit of last updated registration.
	 * @param courseId
	 * @param instituteId
	 * @param schoolYear
	 * @param annoCorso
	 * @param StudentId
	 * @return
	 * @throws BadRequestException
	 */
	public Registration getLastRegistration(String courseId, String instituteId, String schoolYear, String annoCorso,
			String studentId) throws BadRequestException {
		Registration reg = registrationRepository.findTeachingUnit(courseId, schoolYear, instituteId, studentId);
		if (reg != null && reg.getClassroom().startsWith(annoCorso)) {
			return reg;
		} else {
			throw new BadRequestException(errorLabelManager.get("studente.error.notfound"));
		}

	}
	
	/**
	 * Check if Attivita Alternanza start/end date lies within registration date range.
	 * @param courseId
	 * @param instituteId
	 * @param schoolYear
	 * @param annoCorso
	 * @param studentId
	 * @param aaStartDate AttivitaAlternanza start date.
	 * @param aaEndDate AttivitaAlternanza end date.
	 * @return
	 */
	public boolean validDatesAA(String courseId, String instituteId, String schoolYear, String annoCorso,
			String studentId, Date aaStartDate, Date aaEndDate) {

		Registration reg = registrationRepository.findTeachingUnit(courseId, schoolYear, instituteId, studentId);
		if (reg != null && reg.getClassroom().startsWith(annoCorso)) {
			return true; 
//			(Utils.isWithinRange(aaStartDate, reg.getDateFrom(), reg.getDateTo()) && Utils.isWithinRange(aaEndDate, reg.getDateFrom(), reg.getDateTo()));
		} else {
			return false;
		}
	}
	


	
		
}
