package it.smartcommunitylab.cartella.asl.model.ext.infotn;

public class UnitaInfoTn {
	private String origin;
	private String extId;
	private String dateFrom;
	private String dateTo;
	private IstituzioneInfoTn instituteRef;
	private TeachingUnitInfoTn teachingUnit;

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

	public TeachingUnitInfoTn getTeachingUnit() {
		return teachingUnit;
	}

	public void setTeachingUnit(TeachingUnitInfoTn teachingUnit) {
		this.teachingUnit = teachingUnit;
	}

	public IstituzioneInfoTn getInstituteRef() {
		return instituteRef;
	}

	public void setInstituteRef(IstituzioneInfoTn instituteRef) {
		this.instituteRef = instituteRef;
	}

}


