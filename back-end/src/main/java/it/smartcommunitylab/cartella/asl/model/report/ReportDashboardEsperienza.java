package it.smartcommunitylab.cartella.asl.model.report;

import java.time.LocalDate;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import it.smartcommunitylab.cartella.asl.beans.LocalDateDeserializer;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta.Stati;

public class ReportDashboardEsperienza {
	private Long esperienzaId;
	private String studenteId;
	private String nominativoStudente;
	private String cfStudente;
	private String classeStudente;
	private String titolo;
	private int tipologia;
	@Enumerated(EnumType.STRING)
	private Stati stato;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataInizio;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataFine;	
	private int oreValidate;
	private int oreTotali;
	private boolean allineato;
	private int numeroTentativi;
	private String errore;
	private String invio;
	
	public Long getEsperienzaId() {
		return esperienzaId;
	}
	public void setEsperienzaId(Long esperienzaId) {
		this.esperienzaId = esperienzaId;
	}
	public String getStudenteId() {
		return studenteId;
	}
	public void setStudenteId(String studenteId) {
		this.studenteId = studenteId;
	}
	public String getNominativoStudente() {
		return nominativoStudente;
	}
	public void setNominativoStudente(String nominativoStudente) {
		this.nominativoStudente = nominativoStudente;
	}
	public String getCfStudente() {
		return cfStudente;
	}
	public void setCfStudente(String cfStudente) {
		this.cfStudente = cfStudente;
	}
	public String getClasseStudente() {
		return classeStudente;
	}
	public void setClasseStudente(String classeStudente) {
		this.classeStudente = classeStudente;
	}
	public String getTitolo() {
		return titolo;
	}
	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}
	public int getTipologia() {
		return tipologia;
	}
	public void setTipologia(int tipologia) {
		this.tipologia = tipologia;
	}
	public Stati getStato() {
		return stato;
	}
	public void setStato(Stati stato) {
		this.stato = stato;
	}
	public int getOreValidate() {
		return oreValidate;
	}
	public void setOreValidate(int oreValidate) {
		this.oreValidate = oreValidate;
	}
	public int getOreTotali() {
		return oreTotali;
	}
	public void setOreTotali(int oreTotali) {
		this.oreTotali = oreTotali;
	}
	public boolean isAllineato() {
		return allineato;
	}
	public void setAllineato(boolean allineato) {
		this.allineato = allineato;
	}
	public String getErrore() {
		return errore;
	}
	public void setErrore(String errore) {
		this.errore = errore;
	}
	public LocalDate getDataInizio() {
		return dataInizio;
	}
	public void setDataInizio(LocalDate dataInizio) {
		this.dataInizio = dataInizio;
	}
	public LocalDate getDataFine() {
		return dataFine;
	}
	public void setDataFine(LocalDate dataFine) {
		this.dataFine = dataFine;
	}
	public int getNumeroTentativi() {
		return numeroTentativi;
	}
	public void setNumeroTentativi(int numeroTentativi) {
		this.numeroTentativi = numeroTentativi;
	}
	public String getInvio() {
		return invio;
	}
	public void setInvio(String invio) {
		this.invio = invio;
	}
}
