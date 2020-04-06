package it.smartcommunitylab.cartella.asl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.CorsoDiStudio;

@Repository
public interface CorsoDiStudioRepository extends JpaRepository<CorsoDiStudio, String> {

	List<CorsoDiStudio> findCorsoDiStudioByIstitutoId(String istitutoId);
	List<CorsoDiStudio> findCorsoDiStudioByIstitutoIdAndAnnoScolastico(String istitutoId, String annoScolastico);
	
	CorsoDiStudio findCorsoDiStudioByIstitutoIdAndAnnoScolasticoAndCourseId(String istitutoId, String annoScolastico, String courseId);
	
	@Query("SELECT DISTINCT cds FROM CorsoDiStudio cds WHERE cds.istitutoId=(:istitutoId) AND cds.courseId=(:courseId) AND cds.annoScolastico=(:annoScolastico)")
	CorsoDiStudio findCorsoDiStudioByIstituto(String istitutoId, String courseId, String annoScolastico);
	
	
	@Query("SELECT cds0.istitutoId FROM CorsoDiStudio cds0 WHERE cds0.courseId = (:courseId)")
	public String findCorsoDiStudioIstitutoId(@Param("courseId") String courseId);
	
	@Query("SELECT DISTINCT cds0.nome FROM CorsoDiStudio cds0 WHERE cds0.courseId = (:courseId)")
	String findNomeCorsoDiStudioByCorsoId(String courseId);
}
