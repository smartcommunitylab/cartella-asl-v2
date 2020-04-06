package it.smartcommunitylab.cartella.asl.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.PresenzaGiornaliera;

@Repository
public interface PresenzaGiornaliereRepository extends JpaRepository<PresenzaGiornaliera, Long> {

    public List<PresenzaGiornaliera> findByEsperienzaSvoltaIdAndVerificata(Long esperienzaSvoltaId,
            boolean verifica);

    public List<PresenzaGiornaliera> findByEsperienzaSvoltaIdInAndVerificata(List<Long> esperienzaSvoltaIds,
        boolean verifica);
    
    public List<PresenzaGiornaliera> findByEsperienzaSvoltaId(Long esperienzaSvoltaId, Sort sort);
    
  	@Modifying
  	@Query("update PresenzaGiornaliera pg set pg.attivitaSvolta=(:attivitaSvolta), pg.oreSvolte=(:oreSvolte), pg.verificata=true where pg.id=(:id)")
  	public void validaPresenza(Long id, String attivitaSvolta, int oreSvolte);
  	
  	@Modifying
  	@Query("UPDATE PresenzaGiornaliera pg SET pg.attivitaSvolta=(:attivitaSvolta), pg.oreSvolte=(:oreSvolte) WHERE pg.id=(:id) AND pg.esperienzaSvoltaId=(:esperienzaSvoltaId) AND pg.verificata=false")
  	public void aggiornaPresenza(Long id, Long esperienzaSvoltaId, String attivitaSvolta, int oreSvolte);
  	
  	@Query("SELECT COUNT(pg.oreSvolte) FROM PresenzaGiornaliera pg WHERE pg.esperienzaSvoltaId=(:esperienzaSvoltaId) AND pg.verificata=TRUE")
  	public Long getOreValidateByEsperienzaId(Long esperienzaSvoltaId);

}
