package it.smartcommunitylab.cartella.asl.model.ext;

public class CourseMetaInfo {
	private String id;
	private String origin;
	private String extId;
	private String course;
	private String codMiur;
	private Integer years;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getExtId() {
		return extId;
	}

	public void setExtId(String extId) {
		this.extId = extId;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public String getCodMiur() {
		return codMiur;
	}

	public void setCodMiur(String codMiur) {
		this.codMiur = codMiur;
	}

	public Integer getYears() {
		return years;
	}

	public void setYears(Integer years) {
		this.years = years;
	}

}
