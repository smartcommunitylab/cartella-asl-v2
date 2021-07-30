package it.smartcommunitylab.cartella.asl.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import it.smartcommunitylab.cartella.asl.beans.Point;

@Entity
//@Indexed
@Table(name = "istituzione")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Istituzione {

	@Id
	private String id;
	private String name;
	private String cf;
	private String origin;
	private String extId;
	private String address;
	private String phone;
	private String email;
	private String pec;
	
	private Double hoursThreshold;
	
//	@Spatial
//	private Point coordinate = new Point();		
	
	private Double latitude;
	private Double longitude;		
	
	private String rdpAddress;
	private String rdpEmail;
	private String rdpName;
	private String rdpPhoneFax;
	private String privacyLink;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCf() {
		return cf;
	}

	public void setCf(String cf) {
		this.cf = cf;
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

	public Point getCoordinate() {
//		return coordinate;
		return new Point(latitude, longitude);
	}

	public void setCoordinate(Point coordinate) {
//		this.coordinate = coordinate;
		this.latitude = coordinate.getLatitude();
		this.longitude = coordinate.getLongitude();
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Double getHoursThreshold() {
		return hoursThreshold;
	}

	public void setHoursThreshold(Double hoursThreshold) {
		this.hoursThreshold = hoursThreshold;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPec() {
		return pec;
	}

	public void setPec(String pec) {
		this.pec = pec;
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

	public String getPrivacyLink() {
		return privacyLink;
	}

	public void setPrivacyLink(String privacyLink) {
		this.privacyLink = privacyLink;
	}
}
