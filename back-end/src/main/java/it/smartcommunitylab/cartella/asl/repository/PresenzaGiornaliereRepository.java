package it.smartcommunitylab.cartella.asl.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
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
    
    public List<PresenzaGiornaliera> findByEsperienzaSvoltaIdAndGiornata(Long esperienzaSvoltaId, LocalDate giornata);
    
  	@Query("SELECT SUM(pg.oreSvolte) FROM PresenzaGiornaliera pg WHERE pg.esperienzaSvoltaId=(:esperienzaSvoltaId) AND pg.verificata=TRUE")
  	public Long getOreValidateByEsperienzaId(Long esperienzaSvoltaId);

}
