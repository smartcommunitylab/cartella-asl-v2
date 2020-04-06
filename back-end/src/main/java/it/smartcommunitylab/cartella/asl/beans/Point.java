package it.smartcommunitylab.cartella.asl.beans;

import javax.persistence.Embeddable;

import org.hibernate.search.spatial.Coordinates;

@Embeddable
public class Point implements Coordinates {

	private Double latitude = 0.0;
	private Double longitude = 0.0;	
	
	public Point() {
	}
	
	public Point(Double latitude, Double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
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
	
	public Double[] toCoordinates() {
		return new Double[] { latitude, longitude};
	}
	
	public boolean hasDefaultValue() {
		if ((latitude == null || longitude == null)) {
			return true;
		} 
		return latitude == 0.0 && longitude == 0.0;
	}
	
	
	
}
