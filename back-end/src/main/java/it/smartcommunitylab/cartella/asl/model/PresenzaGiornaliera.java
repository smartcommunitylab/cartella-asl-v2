package it.smartcommunitylab.cartella.asl.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import it.smartcommunitylab.cartella.asl.beans.LocalDateDeserializer;

@Entity
@Table(name = "presenze_giornaliere", indexes = { 
		@Index(name = "esperienzaSvoltaId_idx", columnList = "esperienzaSvoltaId", unique = false),
		@Index(name = "giornata_idx", columnList = "giornata", unique = false)})
public class PresenzaGiornaliera {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String istitutoId;
	private Long esperienzaSvoltaId;
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate giornata;
	private String attivitaSvolta;
	private int oreSvolte;
	private Boolean verificata;

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

}
