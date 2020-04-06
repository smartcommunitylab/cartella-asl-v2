package it.smartcommunitylab.cartella.asl.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.PianoAlternanza;
import it.smartcommunitylab.cartella.asl.model.PianoAlternanza.Stati;

@Repository
public interface PianoAlternanzaRepository extends JpaRepository<PianoAlternanza, Long> {

	@Modifying
	@Query("update PianoAlternanza pa0 set titolo=:#{#pa.titolo},corsoDiStudio=:#{#pa.corsoDiStudio},corsoDiStudioId=:#{#pa.corsoDiStudioId},oreTerzoAnno=:#{#pa.oreTerzoAnno},oreQuartoAnno=:#{#pa.oreQuartoAnno},oreQuintoAnno=:#{#pa.oreQuintoAnno},dataAttivazione=:#{#pa.dataAttivazione},note=:#{#pa.note} where id = :#{#pa.id}")
	public void update(@Param("pa") PianoAlternanza pa);		

	public List<PianoAlternanza> findByTitoloContainingIgnoreCaseAndCorsoDiStudioIdAndIstitutoId(String titolo, String corsoDiStudioId, String istitutoId);
	public List<PianoAlternanza> findByTitoloContainingIgnoreCaseAndIstitutoId(String titolo, String istitutoId);
	public List<PianoAlternanza> findPianoAlternanzaByCorsoDiStudioIdAndIstitutoId(String corsoDiStudioId, String istitutoId);
	public List<PianoAlternanza> findPianoAlternanzaByCorsoDiStudioIdAndIstitutoIdOrderByDataAttivazioneAsc(String corsoDiStudioId, String istitutoId);
	public List<PianoAlternanza> findPianoAlternanzaByIstitutoId(String istitutoId);
	
	public List<PianoAlternanza> findPianoAlternanzaByCorsoDiStudioIdAndIstitutoIdAndStato(String corsoDiStudioId, String istitutoId, Stati stato);
	public List<PianoAlternanza> findPianoAlternanzaByCorsoDiStudioIdAndIstitutoIdAndStatoAndDataAttivazioneGreaterThanEqualAndDataAttivazioneLessThanEqual(String corsoDiStudioId, String istitutoId, Stati stato, LocalDate start, LocalDate end);
	public List<PianoAlternanza> findPianoAlternanzaByStatoAndDataAttivazioneLessThanEqual(Stati stato, LocalDate now);
	
	public List<PianoAlternanza> findPianoAlternanzaByStato(Stati stato);
	
    @Query("SELECT pa0.istitutoId FROM PianoAlternanza pa0 WHERE pa0.id = (:id)")
    public String findPianoAlternanzaIstitutoId(@Param("id") Long id);
    
    @Query("SELECT COUNT(pa) FROM PianoAlternanza pa WHERE pa.uuid IN (:ids)")
    int getCountOfPianoAlternanza(@Param("ids") String[] ids);
	
	
}
