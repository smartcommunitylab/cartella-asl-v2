package it.smartcommunitylab.cartella.asl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.Registration;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, String> {
	
	public List<Registration> findRegistrationByCourseId(String courseId);
	
	public List<Registration> findRegistrationByCourseIdAndSchoolYear(String courseId, String schoolYear);
	
	public List<Registration> findRegistrationByCourseIdAndSchoolYearAndInstituteIdAndStudentId(String courseId, String schoolYear, String instituteId, String studentId);

	@Query("SELECT DISTINCT r.classroom FROM Registration r WHERE r.courseId = (:courseId) AND r.schoolYear = (:schoolYear) AND r.instituteId = (:instituteId)")
	public List<String> getClasses(@Param("courseId") String courseId, @Param("instituteId") String instituteId, @Param("schoolYear") String schoolYear);
	
	@Query("SELECT r FROM Registration r WHERE r.courseId = (:courseId) AND r.schoolYear = (:schoolYear) AND r.instituteId = (:instituteId) AND  r.studentId = (:studentId) AND r.dateTo = (SELECT max(rm.dateTo) FROM Registration rm WHERE rm.courseId = (:courseId) AND rm.schoolYear = (:schoolYear) AND rm.instituteId = (:instituteId) AND  rm.studentId = (:studentId))")
	public Registration findTeachingUnit(@Param("courseId") String courseId, @Param("schoolYear") String schoolYear, @Param("instituteId") String instituteId, @Param("studentId") String studentId);
}
