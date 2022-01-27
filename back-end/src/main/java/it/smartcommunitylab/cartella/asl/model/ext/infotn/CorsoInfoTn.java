package it.smartcommunitylab.cartella.asl.model.ext.infotn;

public class CorsoInfoTn {
	private String extId;
	private String origin;
	private String course;
	private String dateFrom;
	private String dateTo;
	private CorsoInfoTn corsoRef;
	private IstituzioneInfoTn instituteRef;
	private String schoolYear;
	private TeachingUnitInfoTn teachingUnitRef;

	public String getExtId() {
		return extId;
	}

	public void setExtId(String extId) {
		this.extId = extId;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getSchoolYear() {
		return schoolYear;
	}

	public void setSchoolYear(String schoolYear) {
		this.schoolYear = schoolYear;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public CorsoInfoTn getCorsoRef() {
		return corsoRef;
	}

	public void setCorsoRef(CorsoInfoTn corsoRef) {
		this.corsoRef = corsoRef;
	}

	public IstituzioneInfoTn getInstituteRef() {
		return instituteRef;
	}

	public void setInstituteRef(IstituzioneInfoTn instituteRef) {
		this.instituteRef = instituteRef;
	}

	public TeachingUnitInfoTn getTeachingUnitRef() {
		return teachingUnitRef;
	}

	public void setTeachingUnitRef(TeachingUnitInfoTn teachingUnitRef) {
		this.teachingUnitRef = teachingUnitRef;
	}


}
