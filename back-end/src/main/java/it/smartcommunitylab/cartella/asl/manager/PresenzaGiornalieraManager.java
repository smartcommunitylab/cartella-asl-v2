package it.smartcommunitylab.cartella.asl.manager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import it.smartcommunitylab.cartella.asl.model.PresenzaGiornaliera;
import it.smartcommunitylab.cartella.asl.repository.PresenzaGiornaliereRepository;

@Repository
@Transactional
public class PresenzaGiornalieraManager extends DataEntityManager {
	@Autowired
	PresenzaGiornaliereRepository presenzaRepository;
	
	public List<PresenzaGiornaliera> findByEsperienzaSvolta(Long esperienzaSvoltaId) {
		return presenzaRepository.findByEsperienzaSvoltaId(esperienzaSvoltaId, 
				Sort.by(Sort.Direction.DESC, "giornata"));
	}

	public void deletePresenza(PresenzaGiornaliera presenza) {
		presenzaRepository.deleteById(presenza.getId());
		
	}

	public void deletePresenzeByEsperienza(Long esperienzaSvoltaId) {
		List<PresenzaGiornaliera> presenze = presenzaRepository.findByEsperienzaSvoltaId(esperienzaSvoltaId, 
				Sort.by(Sort.Direction.DESC, "giornata"));
		presenze.forEach(presenza -> {
			presenzaRepository.deleteById(presenza.getId());
		});
	}
	
	public PresenzaGiornaliera validaPresenza(PresenzaGiornaliera pg) {
		List<PresenzaGiornaliera> list = presenzaRepository.findByEsperienzaSvoltaIdAndGiornata(pg.getEsperienzaSvoltaId(), 
				pg.getGiornata());
		if(list.size() == 0) {
			pg.setVerificata(true);
			presenzaRepository.save(pg);
		} else {
			PresenzaGiornaliera pgDb = list.get(0);
			presenzaRepository.validaPresenza(pgDb.getId(), pg.getAttivitaSvolta(), pg.getOreSvolte());
			pg.setId(pgDb.getId());
		}
		return pg;
	}
	
	public PresenzaGiornaliera aggiornaPresenza(PresenzaGiornaliera pg) {
		List<PresenzaGiornaliera> list = presenzaRepository.findByEsperienzaSvoltaIdAndGiornata(pg.getEsperienzaSvoltaId(), 
				pg.getGiornata());
		if(list.size() == 0) {
			pg.setVerificata(false);
			presenzaRepository.save(pg);
		} else {
			PresenzaGiornaliera pgDb = list.get(0);
			presenzaRepository.aggiornaPresenza(pgDb.getId(), pgDb.getEsperienzaSvoltaId(),
					pg.getAttivitaSvolta(), pg.getOreSvolte());
			pg.setId(pgDb.getId());
		}
		return pg;
	}

}
