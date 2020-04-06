package it.smartcommunitylab.cartella.asl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.TipologiaTipologiaAttivita;

@Repository
public interface TipologiaTipologiaAttivitaRepository extends JpaRepository<TipologiaTipologiaAttivita, Long> {

	@Query("SELECT tta FROM TipologiaTipologiaAttivita tta WHERE tta.tipologia IN (:tipologie)")
	public List<TipologiaTipologiaAttivita> findByTipologia(@Param("tipologie") List<Integer> tipologie);
	
	@Query("SELECT tta FROM TipologiaTipologiaAttivita tta WHERE tta.tipologia = :tipologia")
	public TipologiaTipologiaAttivita findByTipologia(Integer tipologia);
}
