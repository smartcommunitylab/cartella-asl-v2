package it.smartcommunitylab.cartella.asl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.ValutazioneAttivita;

@Repository
public interface ValutazioneAttivitaRepository extends JpaRepository<ValutazioneAttivita, Long> {
	public List<ValutazioneAttivita> findByEsperienzaSvoltaIdOrderByPosizioneAsc(Long esperienzaSvoltaId);
}
