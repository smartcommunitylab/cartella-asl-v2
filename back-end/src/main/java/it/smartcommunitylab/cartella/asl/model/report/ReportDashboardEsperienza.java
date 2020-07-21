package it.smartcommunitylab.cartella.asl.model.report;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

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
	private int oreValidate;
	private int oreTotali;
	private boolean allineato;
	private String errore;
	
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
}
