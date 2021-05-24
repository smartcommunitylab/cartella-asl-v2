package it.smartcommunitylab.cartella.asl.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta.Stati;

@Repository
public interface EsperienzaSvoltaRepository extends JpaRepository<EsperienzaSvolta, Long> {

	// @Query("SELECT p FROM Person p JOIN FETCH p.roles WHERE p.id = (:id)")
	// @Query("SELECT es0 from EsperienzaSvolta es0 where es0.id = (:aziendaId)")
	// @Query("SELECT es0 from EsperienzaSvolta es0 JOIN FETCH es0.attivitaAlternanza aa0 where aa0.titolo = (:aziendaId)")
	// @Query("SELECT es0 from EsperienzaSvolta es0 JOIN FETCH es0.attivitaAlternanza aa0 JOIN FETCH aa0.opportunita o0 where aa0.titolo = (:aziendaId)")
	// @Query("SELECT es0 from EsperienzaSvolta es0 JOIN FETCH es0.attivitaAlternanza aa0 JOIN FETCH aa0.opportunita o0 where o0.titolo = (:aziendaId)")
//	@Query("SELECT es0 FROM EsperienzaSvolta es0 JOIN FETCH es0.attivitaAlternanza aa0 JOIN FETCH aa0.opportunita o0 JOIN FETCH o0.azienda az0 "
//			+ "WHERE aa0.istituto = (:istituto) AND az0.id = (:aziendaId) AND aa0.dataInizio >= (:dataInizio) AND aa0.dataFine <= (:dataFine) AND es0.stato IN (:stato)")
//	public EsperienzaSvolta findEsperienzaSvoltaByIstitutoAndAzienda(@Param("istituto") String istituto, @Param("aziendaId") long aziendaId, @Param("dataInizio") long dataInizio,
//			@Param("dataFine") long dataFine, @Param("stato") List<Integer> stato);

//	@Modifying
//	@Query("update EsperienzaSvolta es0 set nome=:#{#es.nome},stato=:#{#es.stato},noteStudente=:#{#es.noteStudente},noteAzienda=:#{#es.noteAzienda} where id = :#{#es.id}")
//	public void update(@Param("es") EsperienzaSvolta es);

//	public List<EsperienzaSvolta> findEsperienzaSvoltaByAttivitaAlternanza(AttivitaAlternanza aa);
	
//	public List<EsperienzaSvolta> findEsperienzaSvoltaByStudente(Studente studente);
	
//	public List<EsperienzaSvolta> findEsperienzaSvoltaByCompletata(boolean completata);
	
//	public EsperienzaSvolta findEsperienzaSvoltaByAttivitaAlternanzaAndStudente(AttivitaAlternanza aa, Studente studente);

//    @Query("SELECT es0.attivitaAlternanza.istitutoId FROM EsperienzaSvolta es0 WHERE es0.id = (:id)")
//    public String findEsperienzaSvoltaIstitutoId(@Param("id") Long id);	
	
//    @Query("SELECT es0.attivitaAlternanza.opportunita.azienda.id FROM EsperienzaSvolta es0 WHERE es0.id = (:id)")
//    public String findEsperienzaSvoltaAziendaId(@Param("id") Long id);	
	
//    @Query("SELECT es0.studente.id FROM EsperienzaSvolta es0 WHERE es0.id = (:id)")
//    public String findEsperienzaSvoltaStudenteId(@Param("id") Long id);		
    
    public List<EsperienzaSvolta> findByIstitutoIdAndStudenteId(String istitutoId,
            String studentId);
 
    public List<EsperienzaSvolta> findByIstitutoIdAndStudenteIdIn(String istitutoId,
        List<String> studentIds);
    
    @Query("SELECT es FROM EsperienzaSvolta es WHERE es.istitutoId=?1 AND es.attivitaAlternanzaId=?2")
    public List<EsperienzaSvolta> findByIstitutoAndAttivita(String institutoId, Long attivitaAlternanzaId, Sort sort);
    
    public List<EsperienzaSvolta> findByAttivitaAlternanzaId(Long attivitaAlternanzaId);
    
    public List<EsperienzaSvolta> findByAttivitaAlternanzaIdIn(List<Long> ids);
    
    public EsperienzaSvolta findByUuid(String uuid);
    
  	@Modifying
  	@Query("update EsperienzaSvolta es set es.stato=(:stato) where es.id=(:id)")
  	public void updateStato(Long id, Stati stato);

		public List<EsperienzaSvolta> findByAttivitaAlternanzaIdAndStudenteId(Long attivitaAlternanzaId, String studenteId);
		
  	@Modifying
  	@Query("update EsperienzaSvolta es set es.oreRendicontate=(:ore) where es.id=(:id)")
  	public void updateOreRendicontate(Long id, int ore);
		

}
