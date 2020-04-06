package it.smartcommunitylab.cartella.asl.model.statistics;

public class Instance {

	private String id;
	
	private String courseId;

	private String instituteId;

	private String companyId;

	private String internshipId;
	
	private int number;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCompanyId() {
		return this.companyId;
	}

	public String getCourseId() {
		return this.courseId;
	}

	public String getInstituteId() {
		return this.instituteId;
	}

	public int getNumber() {
		return this.number;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	public void setInstituteId(String internshipId) {
		this.instituteId = internshipId;
	}

	public String getInternshipId() {
		return internshipId;
	}

	public void setInternshipId(String internshipId) {
		this.internshipId = internshipId;
	}

	public void setNumber(int number) {
		this.number = number;
	}
}