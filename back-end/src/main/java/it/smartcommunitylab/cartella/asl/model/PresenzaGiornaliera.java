package it.smartcommunitylab.cartella.asl.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import it.smartcommunitylab.cartella.asl.beans.LocalDateDeserializer;

@Entity
@Table(name = "presenze_giornaliere", indexes = { 
		@Index(name = "esperienzaSvoltaId_idx", columnList = "esperienzaSvoltaId", unique = false),
		@Index(name = "giornata_idx", columnList = "giornata", unique = false)})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PresenzaGiornaliera {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String istitutoId;
	private Long esperienzaSvoltaId;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate giornata;
	@Column(columnDefinition="VARCHAR(2048)")
	private String attivitaSvolta;
	private int oreSvolte;
	private Boolean verificata;
	private Boolean validataEnte;
	private Boolean smartWorking;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAttivitaSvolta() {
		return attivitaSvolta;
	}

	public void setAttivitaSvolta(String attivitaSvolta) {
		this.attivitaSvolta = attivitaSvolta;
	}

	public Boolean getVerificata() {
		if(verificata == null) {
			return Boolean.FALSE;
		}
		return verificata;
	}

	public void setVerificata(Boolean verificata) {
		this.verificata = verificata;
	}

	public int getOreSvolte() {
		return oreSvolte;
	}

	public void setOreSvolte(int oreSvolte) {
		this.oreSvolte = oreSvolte;
	}

	public String getIstitutoId() {
		return istitutoId;
	}

	public void setIstitutoId(String istitutoId) {
		this.istitutoId = istitutoId;
	}

	public Long getEsperienzaSvoltaId() {
		return esperienzaSvoltaId;
	}

	public void setEsperienzaSvoltaId(Long esperienzaSvoltaId) {
		this.esperienzaSvoltaId = esperienzaSvoltaId;
	}

	public LocalDate getGiornata() {
		return giornata;
	}

	public void setGiornata(LocalDate giornata) {
		this.giornata = giornata;
	}

	public Boolean getValidataEnte() {
		if(validataEnte == null) {
			return Boolean.FALSE;
		}
		return validataEnte;
	}

	public void setValidataEnte(Boolean validataEnte) {
		this.validataEnte = validataEnte;
	}

	public Boolean getSmartWorking() {
		if(smartWorking == null) {
			return Boolean.FALSE;
		}
		return smartWorking;
	}

	public void setSmartWorking(Boolean smartWorking) {
		this.smartWorking = smartWorking;
	}

}
