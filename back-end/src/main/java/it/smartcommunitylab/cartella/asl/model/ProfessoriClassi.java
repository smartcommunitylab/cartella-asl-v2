package it.smartcommunitylab.cartella.asl.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "professori_classi",
	indexes = {
			@Index(name = "extId_idx", columnList = "extId", unique = false),
			@Index(name = "istitutoId_idx", columnList = "istitutoId", unique = false)
	}
)
public class ProfessoriClassi {

	@Id
	private String id;
	private String extId;
	private String origin;
	private String classroom;
	private String courseExtId;
	private String corsoId;
	private String corso;
	private String instituteExtId;
	private String istitutoId;
	private LocalDate dateFrom;
	private LocalDate dateTo;
	private String schoolYear;
	private String teacherExtId;
	private String referenteAlternanzaId;

	public String getCorsoId() {
		return corsoId;
	}

	public void setCorsoId(String corsoId) {
		this.corsoId = corsoId;
	}

	public String getCorso() {
		return corso;
	}

	public void setCorso(String corso) {
		this.corso = corso;
	}

	public String getInstituteExtId() {
		return instituteExtId;
	}

	public void setInstituteExtId(String instituteExtId) {
		this.instituteExtId = instituteExtId;
	}

	public String getIstitutoId() {
		return istitutoId;
	}

	public void setIstitutoId(String istitutoId) {
		this.istitutoId = istitutoId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getExtId() {
		return extId;
	}

	public void setExtId(String extId) {
		this.extId = extId;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getClassroom() {
		return classroom;
	}

	public void setClassroom(String classroom) {
		this.classroom = classroom;
	}

	public String getCourseExtId() {
		return courseExtId;
	}

	public void setCourseExtId(String courseExtId) {
		this.courseExtId = courseExtId;
	}

	public LocalDate getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(LocalDate dateFrom) {
		this.dateFrom = dateFrom;
	}

	public LocalDate getDateTo() {
		return dateTo;
	}

	public void setDateTo(LocalDate dateTo) {
		this.dateTo = dateTo;
	}

	public String getSchoolYear() {
		return schoolYear;
	}

	public void setSchoolYear(String schoolYear) {
		this.schoolYear = schoolYear;
	}

	public String getTeacherExtId() {
		return teacherExtId;
	}

	public void setTeacherExtId(String teacherExtId) {
		this.teacherExtId = teacherExtId;
	}

	public String getReferenteAlternanzaId() {
		return referenteAlternanzaId;
	}

	public void setReferenteAlternanzaId(String referenteAlternanzaId) {
		this.referenteAlternanzaId = referenteAlternanzaId;
	}

}
