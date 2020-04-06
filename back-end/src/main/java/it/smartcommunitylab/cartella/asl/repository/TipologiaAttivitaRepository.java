package it.smartcommunitylab.cartella.asl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.TipologiaAttivita;

@Repository
public interface TipologiaAttivitaRepository extends JpaRepository<TipologiaAttivita, Long> {

	@Modifying
	@Query("update TipologiaAttivita ta0 set titolo=:#{#ta.titolo},tipologia=:#{#ta.tipologia},monteOre=:#{#ta.monteOre} where id = :#{#ta.id}")
	public void update(@Param("ta") TipologiaAttivita ta);

	public List<TipologiaAttivita> findByPianoAlternanzaId(long pianoAlternanzaId);
	
	public TipologiaAttivita findByUuid(String uuid);	

//    @Query("SELECT aa0.istitutoId FROM AttivitaAlternanza aa0, AnnoAlternanza ana0, TipologiaAttivita ta0 WHERE ta0.id = (:id)")
//    public String findTipologiaAttivitaIstitutoId(@Param("id") Long id);    

}
