package it.smartcommunitylab.cartella.asl.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.cartella.asl.manager.TipologiaTipologiaAttivitaManager;
import it.smartcommunitylab.cartella.asl.model.TipologiaTipologiaAttivita;

@RestController
public class TipologiaTipologiaAttivitaController {
	@Autowired
	private TipologiaTipologiaAttivitaManager tipologiaTipologiaAttivitaManager;
	
	@GetMapping("/api/tipologiaTipologiaAttivita/{id}")
	public TipologiaTipologiaAttivita getTipologiaAttivita(@PathVariable long id) {
		return tipologiaTipologiaAttivitaManager.getTipologiaTipologiaAttivita(id);
	}		
	
	@GetMapping("/api/tipologieTipologiaAttivita")
	public List<TipologiaTipologiaAttivita> getTipologiaAttivita() {
		return tipologiaTipologiaAttivitaManager.getTipologieTipologiaAttivita();
	}	
}
