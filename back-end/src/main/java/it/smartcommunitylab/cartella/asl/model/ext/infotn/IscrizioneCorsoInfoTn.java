package it.smartcommunitylab.cartella.asl.model.ext.infotn;

public class IscrizioneCorsoInfoTn {
	private String extId;
	private String origin;
	private EntityRef courseRef;
	private EntityRef instituteRef;
	private EntityRef teachingUnitRef;
	private StudentRef student;

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

	public EntityRef getCourseRef() {
		return courseRef;
	}

	public void setCourseRef(EntityRef courseRef) {
		this.courseRef = courseRef;
	}

	public StudentRef getStudent() {
		return student;
	}

	public void setStudent(StudentRef student) {
		this.student = student;
	}

	public EntityRef getInstituteRef() {
		return instituteRef;
	}

	public void setInstituteRef(EntityRef instituteRef) {
		this.instituteRef = instituteRef;
	}

	public EntityRef getTeachingUnitRef() {
		return teachingUnitRef;
	}

	public void setTeachingUnitRef(EntityRef teachingUnitRef) {
		this.teachingUnitRef = teachingUnitRef;
	}

}