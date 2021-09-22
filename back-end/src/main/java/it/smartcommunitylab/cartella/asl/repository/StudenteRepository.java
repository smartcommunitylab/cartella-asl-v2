package it.smartcommunitylab.cartella.asl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.Studente;

@Repository
public interface StudenteRepository extends JpaRepository<Studente, String> {

	@Modifying
	@Query("update Studente s0 set name=:#{#s.name} where id = :#{#s.id}")
	public void update(@Param("s") Studente s);		

//	@Query("SELECT s FROM Studente s WHERE s.id = (SELECT r.id FROM Registration r)")
//	public List<Studente> findStudenteByIstitutoId(String istitutoId);	
	
    @Query("SELECT r.instituteId FROM Studente s, Registration r WHERE s.id = (:id) AND r.studentId = s.id AND r.dateTo = (SELECT max(rm.dateTo) FROM Registration rm WHERE rm.studentId = s.id)")  
    public String findStudenteIstitutoId(@Param("id") String id);  		

    @Query("SELECT r.courseId FROM Studente s, Registration r WHERE s.id = (:id) AND r.studentId = s.id AND r.dateTo = (SELECT max(rm.dateTo) FROM Registration rm WHERE rm.studentId = s.id)")  
    public String findStudenteCorsoId(@Param("id") String id);  		
    
//    @Query("SELECT r.classroom FROM Studente s, Registration r WHERE s.id = (:id) AND r.studentId = s.id AND r.dateTo = (SELECT max(rm.dateTo) FROM Registration rm WHERE rm.studentId = s.id)")  
//    public String findStudenteClassroom(@Param("id") String id);      

    @Query("SELECT r.classroom FROM Studente s, Registration r WHERE s.id = (:id) AND r.studentId = s.id AND r.schoolYear = (:schoolYear) AND r.instituteId = (:instituteId) AND r.courseId = (:courseId) AND r.dateTo = (SELECT max(rm.dateTo) FROM Registration rm WHERE rm.studentId = s.id)")  
    public String findStudenteClassroomBySchoolYear(@Param("id") String id, @Param("schoolYear") String schoolYear, @Param("instituteId") String instituteId, @Param("courseId") String courseId);    
    
	@Query("SELECT s FROM Studente s WHERE s.id IN (:ids)")
	public List<Studente> findStudentiByIds(@Param("ids") List<String> ids);	
	
	public Studente findStudenteByCf(String cf);

	@Query("SELECT COUNT(s) FROM Studente s WHERE s.id IN (:ids)")
	public int getCountOfStudente(String[] ids);

	public Studente findByExtIdAndOrigin(String extId, String origin);
    
}
