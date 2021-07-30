package it.smartcommunitylab.cartella.asl.model.ext;

public class Istituzione {
	private String id;
	private String origin;
	private String extId;
	private String datefrom;
	private String name;
	private String description;
	private String address;
	private String phone;
	private String pec;
	private String cf;
	private String email;
	private Double[] geocode;
	private int geocodeAccuracy; // 0 = imported, 1 = geocoding, 2 = istat
	private String codiceIstat;
	private String rdpAddress;
	private String rdpEmail;
	private String rdpName;
	private String rdpPhoneFax;

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

	public String getDatefrom() {
		return datefrom;
	}

	public void setDatefrom(String datefrom) {
		this.datefrom = datefrom;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPec() {
		return pec;
	}

	public void setPec(String pec) {
		this.pec = pec;
	}

	public String getCf() {
		return cf;
	}

	public void setCf(String cf) {
		this.cf = cf;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Double[] getGeocode() {
		return geocode;
	}

	public void setGeocode(Double[] geocode) {
		this.geocode = geocode;
	}

	public int getGeocodeAccuracy() {
		return geocodeAccuracy;
	}

	public void setGeocodeAccuracy(int geocodeAccuracy) {
		this.geocodeAccuracy = geocodeAccuracy;
	}

	public String getCodiceIstat() {
		return codiceIstat;
	}

	public void setCodiceIstat(String codiceIstat) {
		this.codiceIstat = codiceIstat;
	}

	public String getRdpAddress() {
		return rdpAddress;
	}

	public void setRdpAddress(String rdpAddress) {
		this.rdpAddress = rdpAddress;
	}

	public String getRdpEmail() {
		return rdpEmail;
	}

	public void setRdpEmail(String rdpEmail) {
		this.rdpEmail = rdpEmail;
	}

	public String getRdpName() {
		return rdpName;
	}

	public void setRdpName(String rdpName) {
		this.rdpName = rdpName;
	}

	public String getRdpPhoneFax() {
		return rdpPhoneFax;
	}

	public void setRdpPhoneFax(String rdpPhoneFax) {
		this.rdpPhoneFax = rdpPhoneFax;
	}

}
