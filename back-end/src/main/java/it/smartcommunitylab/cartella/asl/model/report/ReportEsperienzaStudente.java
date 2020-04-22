package it.smartcommunitylab.cartella.asl.model.report;

import java.time.LocalDate;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import it.smartcommunitylab.cartella.asl.beans.LocalDateDeserializer;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;

public class ReportEsperienzaStudente {

	private String titolo;
	private String stato;
	private int tipologia;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataInizio;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataFine;
	private String nomeEnte;
	private Long esperienzaSvoltaId;
	private String classeStudente;
	private int oreValidate;
	private int oreTotali;	
	private Long attivitaAlternanzaId;
	private String referenteScuola;
	
	public ReportEsperienzaStudente() {}
	
	public ReportEsperienzaStudente(AttivitaAlternanza aa, EsperienzaSvolta es) {
		titolo = aa.getTitolo();
		tipologia = aa.getTipologia();
		dataInizio = aa.getDataInizio();
		dataFine = aa.getDataFine();
		nomeEnte = aa.getNomeEnte();
		esperienzaSvoltaId = es.getId();
		classeStudente = es.getClasseStudente();
		oreTotali = aa.getOre();
		attivitaAlternanzaId = aa.getId();
		referenteScuola = aa.getReferenteScuola();
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
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
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

	public String getNomeEnte() {
		return nomeEnte;
	}

	public void setNomeEnte(String nomeEnte) {
		this.nomeEnte = nomeEnte;
	}

	public Long getEsperienzaSvoltaId() {
		return esperienzaSvoltaId;
	}

	public void setEsperienzaSvoltaId(Long esperienzaSvoltaId) {
		this.esperienzaSvoltaId = esperienzaSvoltaId;
	}

	public String getClasseStudente() {
		return classeStudente;
	}

	public void setClasseStudente(String classeStudente) {
		this.classeStudente = classeStudente;
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

	public Long getAttivitaAlternanzaId() {
		return attivitaAlternanzaId;
	}

	public void setAttivitaAlternanzaId(Long attivitaAlternanzaId) {
		this.attivitaAlternanzaId = attivitaAlternanzaId;
	}

	public String getReferenteScuola() {
		return referenteScuola;
	}

	public void setReferenteScuola(String referenteScuola) {
		this.referenteScuola = referenteScuola;
	}	
	
}
