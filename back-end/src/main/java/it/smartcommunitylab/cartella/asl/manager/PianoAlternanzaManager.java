package it.smartcommunitylab.cartella.asl.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.model.AssociazioneCompetenze;
import it.smartcommunitylab.cartella.asl.model.Competenza;
import it.smartcommunitylab.cartella.asl.model.PianoAlternanza;
import it.smartcommunitylab.cartella.asl.model.PianoAlternanza.Stati;
import it.smartcommunitylab.cartella.asl.model.PianoAlternanzaBean;
import it.smartcommunitylab.cartella.asl.model.TipologiaAttivita;
import it.smartcommunitylab.cartella.asl.repository.AssociazioneCompetenzeRepository;
import it.smartcommunitylab.cartella.asl.repository.CompetenzaRepository;
import it.smartcommunitylab.cartella.asl.repository.PianoAlternanzaRepository;
import it.smartcommunitylab.cartella.asl.repository.TipologiaAttivitaRepository;
import it.smartcommunitylab.cartella.asl.util.ErrorLabelManager;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Repository
@Transactional
public class PianoAlternanzaManager extends DataEntityManager {
	@SuppressWarnings("unused")
	private static final transient Log logger = LogFactory.getLog(PianoAlternanzaManager.class);

	@Autowired
	PianoAlternanzaRepository pianoAlternanzaRepository;	
	@Autowired
	TipologiaAttivitaRepository tipologiaAttivitaRepository;	
	@Autowired
	AssociazioneCompetenzeRepository associazioneCompetenzeRepository;	
	@Autowired
	CompetenzaRepository competenzaRepository;	
	@Autowired
	private ErrorLabelManager errorLabelManager;

	private static final String PIANO_ALTERNANZA_BY_ISTITUTO_CORSO = "SELECT DISTINCT pa0 FROM PianoAlternanza pa0 WHERE pa0.corsoDiStudioId = (:corsoDiStudioId) AND pa0.istitutoId = (:istitutoId)";

	public String findPianoAlternanzaIstitutoId(Long id) {
		return pianoAlternanzaRepository.findPianoAlternanzaIstitutoId(id);
	}

    public List<PianoAlternanza> findPianoAlternanzaAttivoForCorso(String istitutoId,
            String corsoDiStudioId) {
        StringBuilder sb = new StringBuilder(PIANO_ALTERNANZA_BY_ISTITUTO_CORSO);
        sb.append(" AND pa0.stato IN ('attivo','in_scadenza')");
        final String queryString = sb.toString();
        TypedQuery<PianoAlternanza> query = em.createQuery(queryString, PianoAlternanza.class);
        query.setParameter("istitutoId", istitutoId);
        query.setParameter("corsoDiStudioId", corsoDiStudioId);
        return query.getResultList();

    }
	public Page<PianoAlternanza> findPianoAlternanza(String istitutoId, String corsoDiStudioId, String titolo,
			String stato, Pageable pageRequest) {

		StringBuilder sb = new StringBuilder(PIANO_ALTERNANZA_BY_ISTITUTO_CORSO);

		if (Utils.isNotEmpty(stato)) {
			sb.append(" AND pa0.stato = (:stato) ");
		}

		if (Utils.isNotEmpty(titolo)) {
			sb.append(" AND (lower(pa0.titolo) LIKE (:filterText))");
		}
		
		sb.append(" ORDER BY pa0.titolo ASC");

		String q = sb.toString();

		if (Utils.isEmpty(corsoDiStudioId)) {
			q = q.replace("pa0.corsoDiStudioId = (:corsoDiStudioId) AND", "");
		}

		TypedQuery<PianoAlternanza> query = em.createQuery(q, PianoAlternanza.class);

		query.setParameter("istitutoId", istitutoId);

		if (Utils.isNotEmpty(corsoDiStudioId)) {
			query.setParameter("corsoDiStudioId", corsoDiStudioId);
		}

		if (Utils.isNotEmpty(stato)) {
			query.setParameter("stato", PianoAlternanza.Stati.valueOf(stato));
		}

		if (Utils.isNotEmpty(titolo)) {
			query.setParameter("filterText", "%" + titolo.toLowerCase() + "%");
		}

		Query cQuery = queryToCountQuery(q, query);
		long t = (Long) cQuery.getSingleResult();
		query.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
		query.setMaxResults(pageRequest.getPageSize());
		List<PianoAlternanza> result = query.getResultList();

		for (PianoAlternanza pa : result) {
			calculateAnni(pa);
		}

		Page<PianoAlternanza> page = new PageImpl<PianoAlternanza>(result, pageRequest, t);
		return page;

	}
	
	public void calculateAnni(PianoAlternanza pa) {
    LocalDate today = LocalDate.now();
		Long[] gestisciClassi = new Long[3];
		Map<Long, PianoAlternanza> pianiMap = new HashMap<>();
    List<PianoAlternanza> paInscadenza = pianoAlternanzaRepository.findPianoAlternanzaByCorsoDiStudioIdAndIstitutoIdOrderByDataAttivazioneAsc(
    		pa.getCorsoDiStudioId(), pa.getIstitutoId());
    
    for(PianoAlternanza pas : paInscadenza) {
    	if(pas.getStato().equals(Stati.bozza) || pas.getStato().equals(Stati.scaduto)) {
    		continue;
    	}
    	pianiMap.put(pas.getId(), pas);
    	LocalDate dataDisattivazione = null;
    	if(pas.getDataDisattivazione() != null) {
    		dataDisattivazione = pas.getDataDisattivazione();
    	} else {
    		dataDisattivazione = Utils.startAnnoScolasticoDate(today).plusYears(1);
    	}
    	LocalDate dataTerze = dataDisattivazione;
    	LocalDate dataQuarte = Utils.startAnnoScolasticoDate(dataDisattivazione).plusYears(1);
    	LocalDate dataQuinte = Utils.startAnnoScolasticoDate(dataDisattivazione).plusYears(2);
    	if(dataQuinte.isAfter(today)) {
    		if(gestisciClassi[2] == null) {
    			gestisciClassi[2] = pas.getId();
    		}
    	}
    	if(dataQuarte.isAfter(today)) {
    		if(gestisciClassi[1] == null) {
    			gestisciClassi[1] = pas.getId();
    		}
    	}
    	if(dataTerze.isAfter(today)) {
    		if(gestisciClassi[0] == null) {
    			gestisciClassi[0] = pas.getId();
    		}
    	}
    };
    
    if(pa.getStato().equals(Stati.bozza)) {
    	pa.setPeriodo("");
    } else {
      if(pa.getDataScadenza() == null) {
      	pa.setPeriodo(Utils.annoScolastico(pa.getDataAttivazione()) + " / ∞");
      } else {
      	pa.setPeriodo(Utils.annoScolastico(pa.getDataAttivazione()) + " / " + Utils.annoScolastico(pa.getDataScadenza()));
      }    	
    }
    
    String anni = "";
    if(pa.getStato().equals(Stati.attivo) || pa.getStato().equals(Stati.in_scadenza)) {
      if((gestisciClassi[0] != null) && gestisciClassi[0].equals(pa.getId())) {
      	anni = anni + "3"; 
      }
      if((gestisciClassi[1] != null) && gestisciClassi[1].equals(pa.getId())) {
      	if(anni.equals("")) {
      		anni = anni + "4";
      	} else {
      		anni = anni + "-4";
      	}
      }
      if((gestisciClassi[2] != null) && gestisciClassi[2].equals(pa.getId())) {
      	if(anni.equals("")) {
      		anni = anni + "5";
      	} else {
      		anni = anni + "-5";
      	}
      }
    }
    pa.setAnni(anni);
	}
	
	public PianoAlternanza createPianoAlternanza(PianoAlternanza pa) {
		pa.setStato(PianoAlternanza.Stati.bozza);
		pa.setDataCreazione(LocalDate.now());
		pa.setDataAttivazione(null);
		pa.setDataDisattivazione(null);
		pa.setDataScadenza(null);
		return pianoAlternanzaRepository.saveAndFlush(pa);
	}

	public void updatePianoAlternanza(PianoAlternanza pa) {
		pianoAlternanzaRepository.update(pa);
	}
	
	public PianoAlternanza getPianoAlternanzaDetail(long id) {
		PianoAlternanza pa = getPianoAlternanza(id);
		if(pa != null) {
			try {
				PianoAlternanzaBean duplicaPianoAlternanza = getDuplicaPianoAlternanza(id);
				if(duplicaPianoAlternanza != null) {
					pa.setPianoCorrelato(duplicaPianoAlternanza);
				}
			} catch (BadRequestException e) {
				logger.warn("getPianoAlternanzaDetail - getDuplicaPianoAlternanza:" + e.getMessage());
			}
		}
		return pa;
	}
	
	public PianoAlternanza getPianoAlternanza(long id) {
		PianoAlternanza pa = pianoAlternanzaRepository.getOne(id);
		if(pa != null) {
			calculateAnni(pa);
		}
		return pa;
	}
	
	public boolean deletePianoAlternanza(long id) throws BadRequestException {
		PianoAlternanza pa = getPianoAlternanza(id);

		if (pa == null) {
			throw new BadRequestException(errorLabelManager.get("piano.error.notfound"));
		}

		if (pa.getStato().name().equalsIgnoreCase(PianoAlternanza.Stati.bozza.name())) {
			associazioneCompetenzeRepository.deleteAll(associazioneCompetenzeRepository.findByRisorsaId(pa.getUuid()));
			tipologiaAttivitaRepository.deleteAll(tipologiaAttivitaRepository.findByPianoAlternanzaId(pa.getId()));
			pianoAlternanzaRepository.delete(pa);
			return true;
		} else {
			throw new BadRequestException(errorLabelManager.get("piano.alt.error.started"));
		}
	}
	
	public Map<String, List<TipologiaAttivita>> getTipologiePianoAlternanza(long id) {
		
		List<TipologiaAttivita> tas = tipologiaAttivitaRepository.findByPianoAlternanzaId(id);
		Map<String, List<TipologiaAttivita>> result = new HashMap<String, List<TipologiaAttivita>>();
		
		for (TipologiaAttivita ta: tas) {
			if (!result.containsKey(String.valueOf(ta.getAnnoRiferimento()))) {
				result.put(String.valueOf(ta.getAnnoRiferimento()), new ArrayList<TipologiaAttivita>());
				
			}
			result.get(String.valueOf(ta.getAnnoRiferimento())).add(ta);
		}
	
		return result;
	}
	
	public Boolean updateTipologieToPianoAlternanza(long id, Map<String, List<TipologiaAttivita>> addUpdatedTipos)
			throws BadRequestException {
		boolean saved = false;
		PianoAlternanza pa = pianoAlternanzaRepository.getOne(id);
	
		if (pa == null) {
			throw new BadRequestException(errorLabelManager.get("piano.error.notfound"));
		}

		List<String> uuidsToKeep = new ArrayList<String>();
		for (String annoRiferimmento : addUpdatedTipos.keySet()) {
			for (TipologiaAttivita ta : addUpdatedTipos.get(annoRiferimmento)) {
				if (ta.getUuid() != null && !ta.getUuid().isEmpty()) {
					uuidsToKeep.add(ta.getUuid());
				}
			}
		}
		// remove non existing uuids.
		List<TipologiaAttivita> existingTipos = tipologiaAttivitaRepository.findByPianoAlternanzaId(id);
		for (TipologiaAttivita ta : existingTipos) {
			if (!uuidsToKeep.contains(ta.getUuid())) {
				tipologiaAttivitaRepository.delete(ta);
			}
		}

		for (String annoRiferimmento : addUpdatedTipos.keySet()) {
			for (TipologiaAttivita ta : addUpdatedTipos.get(annoRiferimmento)) {
				if (ta.getUuid() != null && !ta.getUuid().isEmpty()) {
					tipologiaAttivitaRepository.update(ta);
				} else {
					ta.setUuid(Utils.getUUID());
					ta.setIstitutoId(pa.getIstitutoId());
					ta.setPianoAlternanzaId(pa.getId());
					tipologiaAttivitaRepository.save(ta);
				}
			}
			saved = true;
		}

		return saved;
	}
	
	public List<Competenza> getCompetenzePianoAlternanza(long pianoId) throws BadRequestException {
		
		PianoAlternanza pa = pianoAlternanzaRepository.getOne(pianoId);
		
		if (pa == null) {
			throw new BadRequestException(errorLabelManager.get("piano.error.notfound"));
		}
		
		List<AssociazioneCompetenze> assciazionecompetenzes = associazioneCompetenzeRepository.findByRisorsaId(pa.getUuid());
		List<Long> ids = assciazionecompetenzes.stream()
                .map(AssociazioneCompetenze::getCompetenzaId).collect(Collectors.toList());
		List<Competenza> result = competenzaRepository.findAllById(ids);
		return result;
	}
	
	public Boolean addCompetenzeToPianoAlternanza(long paId, List<Long> ids) throws BadRequestException {
		boolean saved = false;
		PianoAlternanza pa = getPianoAlternanza(paId);
	
		if (pa == null) {
			throw new BadRequestException(errorLabelManager.get("piano.error.notfound"));
		}

		// remove non existing associazione competenze.
		List<AssociazioneCompetenze> existingAC = associazioneCompetenzeRepository.findByRisorsaId(pa.getUuid());
		for (AssociazioneCompetenze ac : existingAC) {
			if (!ids.contains(ac.getCompetenzaId())) {
				associazioneCompetenzeRepository.delete(ac);
			}
		}

		List<AssociazioneCompetenze> updatedAC = associazioneCompetenzeRepository.findByRisorsaId(pa.getUuid());
		ids.forEach(id -> {
			boolean found = false;
			for (AssociazioneCompetenze ac : updatedAC) {
				if (ac.getCompetenzaId() == id) {
					found = true;
					break;
				}
			}
			if (!found) {
				AssociazioneCompetenze ac = new AssociazioneCompetenze();
				ac.setCompetenzaId(id);
				ac.setRisorsaId(pa.getUuid());
				ac.setOrdine(0);
				associazioneCompetenzeRepository.save(ac);
			}
		});

		saved = true;
		return saved;
	}
	
	public PianoAlternanza activatePianoAlternanza(Long id) throws BadRequestException {
		PianoAlternanza pa = pianoAlternanzaRepository.getOne(id);

		if (pa == null) {
			throw new BadRequestException(errorLabelManager.get("piano.error.notfound")); 
		}
		if (!pa.getStato().equals(Stati.bozza)) {
			throw new BadRequestException(errorLabelManager.get("piano.alt.error.status"));
		}
		
	  List<PianoAlternanza> paInscadenza = pianoAlternanzaRepository
	  		.findPianoAlternanzaByCorsoDiStudioIdAndIstitutoIdAndStato(pa.getCorsoDiStudioId(),
	  				pa.getIstitutoId(), PianoAlternanza.Stati.in_scadenza);
	  
	  if (paInscadenza.size() > 1) {
	  	throw new BadRequestException(errorLabelManager.get("piano.alt.error.activate"));
	  }
        
		List<PianoAlternanza> paAttivi = pianoAlternanzaRepository
				.findPianoAlternanzaByCorsoDiStudioIdAndIstitutoIdAndStato(pa.getCorsoDiStudioId(),
						pa.getIstitutoId(), PianoAlternanza.Stati.attivo);

		LocalDate today = LocalDate.now();
		LocalDate startAnnoScolastico = Utils.startAnnoScolasticoDate(today);
		if (!paAttivi.isEmpty()) {
			//check se esiste un piano in scadenza per lo stesso anno scolastico
			for(PianoAlternanza pas : paInscadenza) {
				LocalDate dataScadenza = Utils.startAnnoScolasticoDate(pas.getDataDisattivazione()).plusYears(1);
				if(dataScadenza.isAfter(today)) {
					throw new BadRequestException(errorLabelManager.get("piano.alt.error.activate"));
				}
			}
			PianoAlternanza pianoAttivo = paAttivi.get(0);
			pianoAttivo.setStato(PianoAlternanza.Stati.in_scadenza);
			pianoAttivo.setDataDisattivazione(today);
			pianoAttivo.setDataScadenza(startAnnoScolastico.plusYears(3));
			pianoAlternanzaRepository.save(pianoAttivo);
			pa.setStato(PianoAlternanza.Stati.attivo);
			pa.setDataAttivazione(today);
		} else {
			pa.setStato(PianoAlternanza.Stati.attivo);
			pa.setDataAttivazione(today);
		}
		
		return pianoAlternanzaRepository.save(pa);
	}

	public PianoAlternanza duplicaPianoAlternanza(PianoAlternanza duplicaPiano) throws BadRequestException {
		PianoAlternanza existingActivePiano = getPianoAlternanza(duplicaPiano.getId());

		if (existingActivePiano == null) {
			throw new BadRequestException(errorLabelManager.get("piano.error.notfound"));
		}

		PianoAlternanza copy = new PianoAlternanza();
		copy.setUuid(Utils.getUUID()); // generate new uuid.
		copy.setCorsoDiStudio(duplicaPiano.getCorsoDiStudio());
		copy.setCorsoDiStudioId(duplicaPiano.getCorsoDiStudioId());
		copy.setIstitutoId(duplicaPiano.getIstitutoId());
		copy.setNote(duplicaPiano.getNote());
		copy.setOreTerzoAnno(duplicaPiano.getOreTerzoAnno());
		copy.setOreQuartoAnno(duplicaPiano.getOreQuartoAnno());
		copy.setOreQuintoAnno(duplicaPiano.getOreQuintoAnno());
		copy.setTitolo(duplicaPiano.getTitolo());
		copy = createPianoAlternanza(copy);
		
		for (TipologiaAttivita ta: tipologiaAttivitaRepository.findByPianoAlternanzaId(existingActivePiano.getId())) {
			TipologiaAttivita taN = new TipologiaAttivita();
			taN.setAnnoRiferimento(ta.getAnnoRiferimento());
			taN.setIstitutoId(ta.getIstitutoId());
			taN.setMonteOre(ta.getMonteOre());
			taN.setPianoAlternanzaId(copy.getId());
			taN.setTipologia(ta.getTipologia());
			taN.setTitolo(ta.getTitolo());
			taN.setUuid(Utils.getUUID());
			tipologiaAttivitaRepository.save(taN);
		}
		
		for (AssociazioneCompetenze ac: associazioneCompetenzeRepository.findByRisorsaId(existingActivePiano.getUuid())) {
			AssociazioneCompetenze acN = new AssociazioneCompetenze();
			acN.setCompetenzaId(ac.getCompetenzaId());
			acN.setRisorsaId(copy.getUuid());
			acN.setOrdine(0);
			associazioneCompetenzeRepository.save(acN);
		}
		
//		updatePianoAlternanza(copy);
		return copy;
		
	}

	public PianoAlternanzaBean getDuplicaPianoAlternanza(long id) throws BadRequestException {
		PianoAlternanza pa = getPianoAlternanza(id);
		if (pa == null) {
			throw new BadRequestException(errorLabelManager.get("piano.error.notfound"));
		}
		if (pa.getStato().equals(PianoAlternanza.Stati.bozza)) {
			List<PianoAlternanza> pianoInScadenza = pianoAlternanzaRepository.findPianoAlternanzaByCorsoDiStudioIdAndIstitutoIdAndStato(
					pa.getCorsoDiStudioId(), pa.getIstitutoId(), PianoAlternanza.Stati.attivo);
			if (!pianoInScadenza.isEmpty())
				return new PianoAlternanzaBean(pianoInScadenza.get(0));
			else
				return null;			
		}		
		if (pa.getStato().equals(PianoAlternanza.Stati.in_scadenza)) {
			List<PianoAlternanza> pianoAttivo = pianoAlternanzaRepository.findPianoAlternanzaByCorsoDiStudioIdAndIstitutoIdAndStato(
					pa.getCorsoDiStudioId(), pa.getIstitutoId(), PianoAlternanza.Stati.attivo);
			if (!pianoAttivo.isEmpty())
				return new PianoAlternanzaBean(pianoAttivo.get(0));
			else 
				return null;
		}
		return null;
	}

	public void disactivatePianAlternanza(Long id) throws BadRequestException {
		PianoAlternanza pa = getPianoAlternanza(id);

		if (pa == null) {
			throw new BadRequestException(errorLabelManager.get("piano.error.notfound"));
		}
		if (!pa.getStato().equals(Stati.in_attesa)) {
			throw new BadRequestException(errorLabelManager.get("piano.alt.error.status"));
		}
		
		List<PianoAlternanza> pianoAltInScadenza = pianoAlternanzaRepository
				.findPianoAlternanzaByCorsoDiStudioIdAndIstitutoIdAndStato(pa.getCorsoDiStudioId(), 
						pa.getIstitutoId(), PianoAlternanza.Stati.in_scadenza);
		
		if (!pianoAltInScadenza.isEmpty()) {
			PianoAlternanza pianoDaAttivare = null;
			for (PianoAlternanza paIs: pianoAltInScadenza) {
				if(pianoDaAttivare == null) {
					pianoDaAttivare = paIs;
				} else {
					if(paIs.getDataDisattivazione().isAfter(pianoDaAttivare.getDataDisattivazione())) {
						pianoDaAttivare = paIs;
					}
				}
			}
			if(pianoDaAttivare != null) {
				pianoDaAttivare.setStato(Stati.attivo);
				pianoDaAttivare.setDataDisattivazione(null);
				pianoDaAttivare.setDataScadenza(null);
				pianoAlternanzaRepository.save(pianoDaAttivare);
			}
		}
		pa.setStato(PianoAlternanza.Stati.bozza);
		pa.setDataAttivazione(null);
		pianoAlternanzaRepository.save(pa);
		
	}
	
	@Scheduled(cron = "0 00 01 * * ?")
	public void alignPianoAlternanza() {
		LocalDate today = LocalDate.now();
		List<PianoAlternanza> list = pianoAlternanzaRepository.findPianoAlternanzaByStato(Stati.in_scadenza);
		for(PianoAlternanza piano : list) {
			if(piano.getDataScadenza() != null) {
				if(piano.getDataScadenza().isBefore(today)) {
					piano.setStato(Stati.scaduto);
					pianoAlternanzaRepository.save(piano);
				}
			}
		}
	}

		

}
