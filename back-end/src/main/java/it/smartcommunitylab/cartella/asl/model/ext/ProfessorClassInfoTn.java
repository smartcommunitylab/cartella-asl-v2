package it.smartcommunitylab.cartella.asl.model.ext;

public class ProfessorClassInfoTn {

	private String extId;
	private String origin;
	private String classroom;
	private Corso course;
	private String datefrom;
	private String dateto;
	private String extIdInstitute;
	private String schoolyear;
	private Teacher teacher;

	public String getExtId() {
		return extId;
	}

	public String getExtIdInstitute() {
		return extIdInstitute;
	}

	public void setExtIdInstitute(String extIdInstitute) {
		this.extIdInstitute = extIdInstitute;
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

	public Corso getCourse() {
		return course;
	}

	public void setCourse(Corso course) {
		this.course = course;
	}

	public String getDatefrom() {
		return datefrom;
	}

	public void setDatefrom(String datefrom) {
		this.datefrom = datefrom;
	}

	public String getDateto() {
		return dateto;
	}

	public void setDateto(String dateto) {
		this.dateto = dateto;
	}

	public String getSchoolyear() {
		return schoolyear;
	}

	public void setSchoolyear(String schoolyear) {
		this.schoolyear = schoolyear;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

}