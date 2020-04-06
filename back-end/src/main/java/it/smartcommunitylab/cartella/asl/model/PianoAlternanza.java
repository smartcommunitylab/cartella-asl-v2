package it.smartcommunitylab.cartella.asl.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import it.smartcommunitylab.cartella.asl.beans.LocalDateDeserializer;

@Entity
@Table(name = "piano_alternanza", 
	indexes = { @Index(name = "istitutoId_idx", columnList = "istitutoId", unique = false) })
public class PianoAlternanza {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String uuid;
	private String istitutoId;
	private String corsoDiStudioId;
	private String corsoDiStudio;
	private String titolo;
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataAttivazione;
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataCreazione;
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataDisattivazione;
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataScadenza;
	private String note;
	private int oreTerzoAnno;
	private int oreQuartoAnno;
	private int oreQuintoAnno;
	@Enumerated(EnumType.STRING)
	private Stati stato;
	@Transient
	private String anni;
	@Transient
	private String periodo;
	@Transient
	private PianoAlternanza pianoCorrelato; 

	public static enum Stati {
		bozza, attivo, in_scadenza, scaduto, in_attesa
	};

	
	public Stati getStato() {
		return stato;
	}

	public void setStato(Stati stato) {
		this.stato = stato;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String nome) {
		this.titolo = nome;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getIstitutoId() {
		return istitutoId;
	}

	public void setIstitutoId(String istitutoId) {
		this.istitutoId = istitutoId;
	}

	public String getCorsoDiStudioId() {
		return corsoDiStudioId;
	}

	public void setCorsoDiStudioId(String corsoDiStudioId) {
		this.corsoDiStudioId = corsoDiStudioId;
	}

	public String getCorsoDiStudio() {
		return corsoDiStudio;
	}

	public void setCorsoDiStudio(String corsoDiStudio) {
		this.corsoDiStudio = corsoDiStudio;
	}

	public LocalDate getDataAttivazione() {
		return dataAttivazione;
	}

	public void setDataAttivazione(LocalDate dataAttivazione) {
		this.dataAttivazione = dataAttivazione;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public int getOreTerzoAnno() {
		return oreTerzoAnno;
	}

	public void setOreTerzoAnno(int oreTerzoAnno) {
		this.oreTerzoAnno = oreTerzoAnno;
	}

	public int getOreQuartoAnno() {
		return oreQuartoAnno;
	}

	public void setOreQuartoAnno(int oreQuartoAnno) {
		this.oreQuartoAnno = oreQuartoAnno;
	}

	public int getOreQuintoAnno() {
		return oreQuintoAnno;
	}

	public void setOreQuintoAnno(int oreQuintoAnno) {
		this.oreQuintoAnno = oreQuintoAnno;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}


	public String getAnni() {
		return anni;
	}

	public void setAnni(String anni) {
		this.anni = anni;
	}

	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public LocalDate getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(LocalDate dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	public LocalDate getDataDisattivazione() {
		return dataDisattivazione;
	}

	public void setDataDisattivazione(LocalDate dataDisattivazione) {
		this.dataDisattivazione = dataDisattivazione;
	}

	public LocalDate getDataScadenza() {
		return dataScadenza;
	}

	public void setDataScadenza(LocalDate dataScadenza) {
		this.dataScadenza = dataScadenza;
	}

	public PianoAlternanza getPianoCorrelato() {
		return pianoCorrelato;
	}

	public void setPianoCorrelato(PianoAlternanza pianoCorrelato) {
		this.pianoCorrelato = pianoCorrelato;
	}
}
