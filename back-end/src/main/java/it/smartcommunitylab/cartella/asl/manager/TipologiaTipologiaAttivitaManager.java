package it.smartcommunitylab.cartella.asl.manager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import it.smartcommunitylab.cartella.asl.model.TipologiaTipologiaAttivita;
import it.smartcommunitylab.cartella.asl.repository.TipologiaTipologiaAttivitaRepository;

@Repository
@Transactional
public class TipologiaTipologiaAttivitaManager {

	@Autowired
	private TipologiaTipologiaAttivitaRepository tipologiaRepository;
	
	public TipologiaTipologiaAttivita getTipologiaTipologiaAttivita(long id) {
		return tipologiaRepository.getOne(id);
	}		
	
	public TipologiaTipologiaAttivita getTipologiaTipologiaAttivitaByTipologia(int tipologia) {
		return tipologiaRepository.findByTipologia(tipologia);
	}
	
	public List<TipologiaTipologiaAttivita> findTipologiaTipologiaAttivitaByTipologia(List<Integer> tipologie) {
		return tipologiaRepository.findByTipologia(tipologie);
	}		
	
	public List<TipologiaTipologiaAttivita> getTipologieTipologiaAttivita() {
		return tipologiaRepository.findAll();
	}	
}
