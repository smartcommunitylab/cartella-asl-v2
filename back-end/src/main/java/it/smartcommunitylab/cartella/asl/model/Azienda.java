package it.smartcommunitylab.cartella.asl.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import it.smartcommunitylab.cartella.asl.beans.Point;

@Entity
@Table(name = "azienda")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Azienda {

	@Id
	private String id;

	@Column(columnDefinition = "VARCHAR(1024)")
	private String nome;

	private String extId;
	private String origin;
	@Column(columnDefinition = "VARCHAR(1024)")
	private String address;
	@Column(columnDefinition = "VARCHAR(1024)")
	private String description;
	private String email;
	private String geocode;
	private String partita_iva;
	private String pec;
	private String phone;
	@Column(columnDefinition = "VARCHAR(1024)")
	private String businessName;

	@Column(columnDefinition = "BLOB")
	private String[] atecoCode;
	@Column(columnDefinition = "BLOB")
	private String[] atecoDesc;

	private Double latitude;
	private Double longitude;
	private int idTipoAzienda;
	
	private String legaleRappresentante;
	private String medicoCompetente;
	private String responsabileSicurezza;
	private String viaPiazza;
	private String cap;
	private String comune;
	private String provincia;
	
	@Transient
	private boolean estera = false;
	
	@Transient
	private RegistrazioneEnte registrazioneEnte;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGeocode() {
		return geocode;
	}

	public void setGeocode(String geocode) {
		this.geocode = geocode;
	}

	public String getPartita_iva() {
		return partita_iva;
	}

	public void setPartita_iva(String partita_iva) {
		this.partita_iva = partita_iva;
	}

	public String getPec() {
		return pec;
	}

	public void setPec(String pec) {
		this.pec = pec;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Point getCoordinate() {
		// return coordinate;
		return new Point(latitude, longitude);
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

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String[] getAtecoCode() {
		return atecoCode;
	}

	public void setAtecoCode(String[] atecoCode) {
		this.atecoCode = atecoCode;
	}

	public String[] getAtecoDesc() {
		return atecoDesc;
	}

	public void setAtecoDesc(String[] atecoDesc) {
		this.atecoDesc = atecoDesc;
	}

	public int getIdTipoAzienda() {
		return idTipoAzienda;
	}

	public void setIdTipoAzienda(int idTipoAzienda) {
		this.idTipoAzienda = idTipoAzienda;
	}

	public boolean isEstera() {
		return estera;
	}

	public void setEstera(boolean estera) {
		this.estera = estera;
	}

	public String getLegaleRappresentante() {
		return legaleRappresentante;
	}

	public void setLegaleRappresentante(String legaleRappresentante) {
		this.legaleRappresentante = legaleRappresentante;
	}

	public String getMedicoCompetente() {
		return medicoCompetente;
	}

	public void setMedicoCompetente(String medicoCompetente) {
		this.medicoCompetente = medicoCompetente;
	}

	public String getResponsabileSicurezza() {
		return responsabileSicurezza;
	}

	public void setResponsabileSicurezza(String responsabileSicurezza) {
		this.responsabileSicurezza = responsabileSicurezza;
	}

	public String getViaPiazza() {
		return viaPiazza;
	}

	public void setViaPiazza(String viaPiazza) {
		this.viaPiazza = viaPiazza;
	}

	public String getCap() {
		return cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	public String getComune() {
		return comune;
	}

	public void setComune(String comune) {
		this.comune = comune;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public RegistrazioneEnte getRegistrazioneEnte() {
		return registrazioneEnte;
	}

	public void setRegistrazioneEnte(RegistrazioneEnte registrazioneEnte) {
		this.registrazioneEnte = registrazioneEnte;
	}

}
