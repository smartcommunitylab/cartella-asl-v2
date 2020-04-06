package it.smartcommunitylab.cartella.asl.util;

import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;
import it.smartcommunitylab.cartella.asl.repository.AttivitaAlternanzaRepository;

@Component
public class EsperienzeSvoltaComparator implements Comparator<EsperienzaSvolta> {

	@Autowired
	AttivitaAlternanzaRepository attivitaAltRepo;
	@Override
	public int compare(EsperienzaSvolta esp1, EsperienzaSvolta esp2) {
		return  attivitaAltRepo.getOne(esp1.getAttivitaAlternanzaId()).getDataInizio().compareTo(attivitaAltRepo.getOne(esp2.getAttivitaAlternanzaId()).getDataInizio());
	}
}
