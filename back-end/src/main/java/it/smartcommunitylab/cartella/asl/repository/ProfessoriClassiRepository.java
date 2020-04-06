package it.smartcommunitylab.cartella.asl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.ProfessoriClassi;

@Repository
public interface ProfessoriClassiRepository extends JpaRepository<ProfessoriClassi, String> {

	@Query("SELECT pc FROM ProfessoriClassi pc WHERE pc.extId =:#{#extId}")
	public ProfessoriClassi findProfessoriClassiByExtId(@Param("extId") String extId);

}
