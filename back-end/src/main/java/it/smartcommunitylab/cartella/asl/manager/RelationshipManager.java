package it.smartcommunitylab.cartella.asl.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import it.smartcommunitylab.cartella.asl.beans.AttivitaAlternanzaStub;
import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.Competenza;
import it.smartcommunitylab.cartella.asl.model.PianoAlternanza;
import it.smartcommunitylab.cartella.asl.model.TipologiaAttivita;
import it.smartcommunitylab.cartella.asl.util.ErrorLabelManager;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Repository
@Transactional
public class RelationshipManager extends EntitiesManager {
	
	@Autowired
	private ErrorLabelManager errorLabelManager;

//	public Opportunita setCompetenzeInOpportunita(long oId, List<Long> cIds) throws BadRequestException {
//		Opportunita o = getOpportunita(oId);
//		if (o == null) {
//			throw new BadRequestException(errorLabelManager.get("opportunita.error.notfound"));
//		}			
//		List<Competenza> cs = getCompetenze(cIds);
//		o.setCompetenzeFromSet(Sets.newHashSet(cs));
//		return opportunitaRepository.save(o);
//	}	
	
	
	public AttivitaAlternanza createAttivitaAlternanzaByOppCorsoAndGruppo(long oppCorsoId, long pianoAlternanzaId, long annoCorso, AttivitaAlternanzaStub aaStub) throws BadRequestException {
		/** NAWAZ		List<String> studentiId = aaStub.getStudentiId();
		if (studentiId == null) {
			studentiId = Lists.newArrayList();
		}
		
		PianoAlternanza pa = getPianoAlternanza(pianoAlternanzaId);
		
		if (pa == null) {
			throw new BadRequestException(errorLabelManager.get("piano.error.notfound"));
		}		
		
		AnnoAlternanza annoAlternanza = pa.getAnniAlternanza().stream().filter(x -> x.getAnnoCorso() == annoCorso).findFirst().orElse(null);
		
		if (annoAlternanza == null) {
			throw new BadRequestException(errorLabelManager.get("anno.alt.error.notfound"));
		}
		
		OppCorso oppCorso = getOppCorso(oppCorsoId);
		if (oppCorso == null) {
			throw new BadRequestException(errorLabelManager.get("oppcorso.error.notfound"));
		}
		
		TipologiaTipologiaAttivita tta = getTipologiaTipologiaAttivita(oppCorso.getTipologia());
		if (tta == null) {
			throw new BadRequestException(errorLabelManager.get("tipologia.tipo.error.notfound"));
		}		
	
		if (tta.getIndividuale() != null && tta.getIndividuale() == true && studentiId.size() > 1) {
			throw new BadRequestException(errorLabelManager.get("oppcorso.error.tipo.individuale"));
		}

		// CARTELLA-323 - alignment with registration data check.
		Date upperAA = new Date(aaStub.getDataInizio());
		Date lowerAA = new Date(aaStub.getDataFine());
		for (String studenteId : studentiId) {
			if (!validDatesAA(pa.getCorsoDiStudioId(), pa.getIstitutoId(), pa.getAnnoScolasticoAttivazione(),
					String.valueOf(annoCorso), studenteId, upperAA, lowerAA)) {
				throw new BadRequestException(errorLabelManager.get("registration.date.error"));
			}
		}
		
		if (oppCorso instanceof Opportunita) {
			// remember here it is single student so removed is zero and activity is created for the first time.
			boolean available = decreasePostiRimanenti((Opportunita)oppCorso, 0, studentiId.size()); 
			if (!available) {
				throw new BadRequestException(errorLabelManager.get("opportunita.error.posti"));
			}
			oppCorsoRepository.save(oppCorso);
		}		
		
		AttivitaAlternanza aa = new AttivitaAlternanza();
		
		aa.setTitolo(oppCorso.getTitolo());
		aa.setAnnoCorso(annoAlternanza.getAnnoCorso());
		aa.setAnnoAlternanza(annoAlternanza);
		aa.setIstituto(pa.getIstituto());
		aa.setIstitutoId(pa.getIstitutoId());
		aa.setCorso(pa.getCorsoDiStudio());
		aa.setCorsoId(pa.getCorsoDiStudioId());		
		aa.setDataInizio(oppCorso.getDataInizio());
		aa.setDataFine(oppCorso.getDataFine());	
		aa.setTipologia(oppCorso.getTipologia());
		aa.setOre(oppCorso.getOre());
		

		
		aa.setIndividuale(tta.getIndividuale());
		aa.setInterna(tta.getInterna());		

		if (oppCorso instanceof Opportunita) {
			aa.setOpportunita((Opportunita)oppCorso);
		} else {
			aa.setAnnoScolastico(((CorsoInterno)oppCorso).getAnnoScolastico());
//			aa.setCorsoId(((CorsoInterno)oppCorso).getCorsoId());
			aa.setCorsoInterno((CorsoInterno)oppCorso);
			aa.setOraInizio(((CorsoInterno)oppCorso).getOraInizio());
			aa.setOraFine(((CorsoInterno)oppCorso).getOraFine());			
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
		} else {
			aa.setCompetenzeFromSet(Sets.newHashSet(oppCorso.getCompetenze()));
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
		if (aaStub.getTitolo() != null) {
			aa.setTitolo(aaStub.getTitolo());
		}

		AttivitaAlternanza aa2 = saveAttivitaAlternanza(aa);
		

		for (String studenteId: studentiId) {
			EsperienzaSvolta es = new EsperienzaSvolta();
			Studente studente = studenteRepository.findOne(studenteId);
			
			if (studente == null) {
				throw new BadRequestException(errorLabelManager.get("studente.error.notfound"));
			}			

			es.setStudente(studente);
			es.setAttivitaAlternanza(aa2);
			
			if (aaStub.getCompetenzeId() != null && !aaStub.getCompetenzeId().isEmpty()) {
				es.setCompetenzeFromSet(Sets.newHashSet(competenzaRepository.findAll(aaStub.getCompetenzeId())));
			} else {
				es.setCompetenzeFromSet(Sets.newHashSet(oppCorso.getCompetenze()));
			}				
			
			// save teaching unit Id inside ES(CARTELLA-316).
			es.setTeachingUnitId(getLastRegistration(pa.getCorsoDiStudioId(), pa.getIstitutoId(),
					pa.getAnnoScolasticoAttivazione(), String.valueOf(annoCorso), studenteId).getTeachingUnitId());
			
			es = saveEsperienzaSvolta(es);
			
			AuditEntry audit = new AuditEntry("create", EsperienzaSvolta.class, es.getId(), null, new Object(){});
			auditManager.save(audit);	
			
		}
		
		return aa2;		*/
		return null;
	}	
	
//	public Long deleteEsperienzaSvolta(long attivitaAlternanzaId, String studenteId) throws BadRequestException {
//		Long result = null;
//		
//		AttivitaAlternanza aa = attivitaAlternanzaRepository.findOne(attivitaAlternanzaId);
//		
//		if (aa == null) {
//			throw new BadRequestException(errorLabelManager.get("attivita.alt.error.notfound"));
//		}	
//		
//		Studente studente = studenteRepository.findOne(studenteId);
//		
//		if (studente == null) {
//			throw new BadRequestException(errorLabelManager.get("studente.error.notfound"));
//		}		
//				
//		EsperienzaSvolta es = esperienzaSvoltaRepository.findEsperienzaSvoltaByAttivitaAlternanzaAndStudente(aa, studente);
//		
//		if (es != null) {
//			result = es.getId();
//			if(aa.isIndividuale()) {
//				//remove aa -> es
//				deleteAttivitaAlternanza(aa.getId());
//			} else {
//				List<EsperienzaSvolta> list = esperienzaSvoltaRepository.findEsperienzaSvoltaByAttivitaAlternanza(aa);
//				if(list.size() == 0) {
//					deleteAttivitaAlternanza(aa.getId());
//				} else if(list.size() == 1) {
//					//check if it is the last es
//					if(list.get(0).getId() == es.getId()) {
//						deleteAttivitaAlternanza(aa.getId());
//					}
//				} else if(list.size() > 1) {
//					es.setAttivitaAlternanza(null);
//					esperienzaSvoltaRepository.save(es);
//					esperienzaSvoltaRepository.delete(es);
//				}
//			}
//		}
//		
//		return result;
//	}
//	
	
	public AttivitaAlternanza createEccezioneSolution(long oppCorsoId, long pianoAlternanzaId, List<String> studentiId, long attivitaAlternanzaId, AttivitaAlternanzaStub aaStub) throws BadRequestException {
//		PianoAlternanza pa = getPianoAlternanza(pianoAlternanzaId);
		
//		if (pa == null) {
//			throw new BadRequestException(errorLabelManager.get("piano.error.notfound"));
//		}			
		
		/** NAWAZ AttivitaAlternanza aa0 = getAttivitaAlternanza(attivitaAlternanzaId, false);

		if (aa0 == null) {
			throw new BadRequestException(errorLabelManager.get("attivita.alt.error.notfound"));
		}		
		
		AnnoAlternanza annoAlternanza = pa.getAnniAlternanza().stream().filter(x -> x.getAnnoCorso() == aa0.getAnnoCorso()).findFirst().orElse(null);
		
		if (annoAlternanza == null) {
			throw new BadRequestException(errorLabelManager.get("anno.alt.error.notfound"));
		}		
		
		OppCorso oppCorso = getOppCorso(oppCorsoId);
		if (oppCorso == null) {
			throw new BadRequestException(errorLabelManager.get("oppcorso.error.notfound"));
		}

		AttivitaAlternanza aa = new AttivitaAlternanza();
		
		aa.setTitolo(oppCorso.getTitolo());
		aa.setAnnoCorso(aa0.getAnnoAlternanza().getAnnoCorso());
		aa.setAnnoAlternanza(annoAlternanza);
		aa.setIstituto(aa0.getIstituto());
		aa.setIstitutoId(aa0.getIstitutoId());
		aa.setCorso(aa0.getCorso());
		aa.setCorsoId(aa0.getCorsoId());		
		aa.setDataInizio(oppCorso.getDataInizio());
		aa.setDataFine(oppCorso.getDataFine());	
		aa.setTipologia(oppCorso.getTipologia());
		aa.setOre(oppCorso.getOre());
//		aa.setClasse(aa0.getClasse());
		aa.setAnnoScolastico(aa0.getAnnoScolastico());
		
		TipologiaTipologiaAttivita tta = getTipologiaTipologiaAttivita(aa.getTipologia());
		
		if (tta == null) {
			throw new BadRequestException(errorLabelManager.get("tipologia.tipo.error.notfound"));
		}
		
		aa.setIndividuale(tta.getIndividuale());
		aa.setInterna(tta.getInterna());		

		if (oppCorso instanceof Opportunita) {
			aa.setOpportunita((Opportunita)oppCorso);
		} else {
			aa.setIstituto(((CorsoInterno)oppCorso).getIstitutoId());
			aa.setAnnoScolastico(((CorsoInterno)oppCorso).getAnnoScolastico());
			aa.setCorsoId(((CorsoInterno)oppCorso).getCorsoId());
			aa.setCorsoInterno((CorsoInterno)oppCorso);
			aa.setOraInizio(((CorsoInterno)oppCorso).getOraInizio());
			aa.setOraFine(((CorsoInterno)oppCorso).getOraFine());			
		}

		aa.setEccezioneAttivitaAlternanzaId(aa0.getId());
		aa.setGestioneEccezione(GESTIONE_ECCEZIONE.RISOLVE);
		
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
		} else {
			aa.setCompetenzeFromSet(Sets.newHashSet(oppCorso.getCompetenze()));
		}		
		
		AttivitaAlternanza aa2 = saveAttivitaAlternanza(aa);
		
		aa0.setEccezioneAttivitaAlternanzaId(aa2.getId());
		aa0.setGestioneEccezione(GESTIONE_ECCEZIONE.RISOLTO_DA);
		saveAttivitaAlternanza(aa0);
		
		for (String studenteId: studentiId) {
			EsperienzaSvolta es = new EsperienzaSvolta();
			
			if (aaStub.getCompetenzeId() != null && !aaStub.getCompetenzeId().isEmpty()) {
				es.setCompetenzeFromSet(Sets.newHashSet(competenzaRepository.findAll(aaStub.getCompetenzeId())));
			} else {
				es.setCompetenzeFromSet(Sets.newHashSet(oppCorso.getCompetenze()));
			}			
			
			Studente studente = studenteRepository.findOne(studenteId);
			
			if (tta == null) {
				throw new BadRequestException(errorLabelManager.get("studente.error.notfound"));
			}			

			es.setStudente(studente);
			es.setAttivitaAlternanza(aa2);
			es = saveEsperienzaSvolta(es);
		}
		
		return aa2;*/
		return null;
	}	
	
//	public CorsoInterno setCompetenzeInCorsoInterno(long ciId, List<Long> cIds) throws BadRequestException {
//		CorsoInterno co = getCorsoInterno(ciId);
//		if (co == null) {
//			throw new BadRequestException(errorLabelManager.get("corso.interno.error.notfound"));
//		}			
//		List<Competenza> cs = getCompetenze(cIds);
//		co.setCompetenzeFromSet(Sets.newHashSet(cs));
//		return corsoInternoRepository.save(co);
//	}	
//	
// Eccezioni
	
//	public void solveEccezione(@PathVariable Long idAAProblem, @PathVariable Long idAASolution) throws BadRequestException {
//		AttivitaAlternanza problem = attivitaAlternanzaRepository.findOne(idAAProblem);
//		
//		if (problem == null) {
//			throw new BadRequestException(errorLabelManager.get("attivita.alt.error.problem.notfound"));
//		}		
//		
//		AttivitaAlternanza solution = attivitaAlternanzaRepository.findOne(idAASolution);
//		
//		if (solution == null) {
//			throw new BadRequestException(errorLabelManager.get("attivita.alt.error.solution.notfound"));
//		}		
//		
//		problem.setGestioneEccezione(GESTIONE_ECCEZIONE.RISOLTO_DA);
//		problem.setEccezioneAttivitaAlternanzaId(solution.getId());
//
//		solution.setGestioneEccezione(GESTIONE_ECCEZIONE.RISOLVE);
//		solution.setEccezioneAttivitaAlternanzaId(problem.getId());		
//		
//		attivitaAlternanzaRepository.save(problem);
//		attivitaAlternanzaRepository.save(solution);
//	}
	
	
}
