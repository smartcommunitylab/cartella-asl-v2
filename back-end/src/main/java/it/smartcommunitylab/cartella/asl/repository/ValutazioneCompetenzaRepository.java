package it.smartcommunitylab.cartella.asl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.ValutazioneCompetenza;

@Repository
public interface ValutazioneCompetenzaRepository extends JpaRepository<ValutazioneCompetenza, Long> {
	public List<ValutazioneCompetenza> findByEsperienzaSvoltaIdOrderByOrdineAsc(Long esperienzaSvoltaId);
}
