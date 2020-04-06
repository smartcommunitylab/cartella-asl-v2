package it.smartcommunitylab.cartella.asl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.smartcommunitylab.cartella.asl.model.Istituzione;

public interface IstituzioneRepository extends JpaRepository<Istituzione, String> {

	@Modifying
	@Query("update Istituzione ist0 set name=:#{#ist.name} where id = :#{#ist.id}")
	public void update(@Param("ist") Istituzione ist);

    @Query("SELECT is0.hoursThreshold FROM Istituzione is0 WHERE is0.id = (:id)")  
    public Double findIstitutoHoursThreshold(@Param("id") String id); 	
	
    @Query("SELECT is0.name FROM Istituzione is0 WHERE is0.id = (:id)")  
    public String findIstitutoName(@Param("id") String id); 	    
    
	@Modifying
	@Query("update Istituzione ist0 set hoursThreshold= (:hours) where id = (:id)")
	public void updateHoursThreshold(@Param("id") String id, @Param("hours") Double hours); 
	
	@Query("SELECT is0 FROM Istituzione is0 WHERE is0.extId = (:extId) AND is0.origin = (:origin)")
	public Istituzione findIstitutzioneByExtId(@Param("origin") String origin, @Param("extId") String extId);
    
}
