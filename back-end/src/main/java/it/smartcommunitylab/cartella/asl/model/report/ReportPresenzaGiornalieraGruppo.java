package it.smartcommunitylab.cartella.asl.model.report;

import java.util.ArrayList;
import java.util.List;

import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;
import it.smartcommunitylab.cartella.asl.model.PresenzaGiornaliera;

public class ReportPresenzaGiornalieraGruppo {
	private Long esperienzaSvoltaId;
	private String istitutoId;
	private String studenteId;
	private String nominativoStudente;
	private String cfStudente;
	private String classeStudente;
	private List<PresenzaGiornaliera> presenze = new ArrayList<PresenzaGiornaliera>();
	private int oreValidate;
	private int oreDaValidare;
	private int oreTotali;
	
	public ReportPresenzaGiornalieraGruppo() {}
	
	public ReportPresenzaGiornalieraGruppo(EsperienzaSvolta esperienza) {
		esperienzaSvoltaId = esperienza.getId();
		istitutoId = esperienza.getIstitutoId();
		studenteId = esperienza.getStudenteId();
		nominativoStudente = esperienza.getNominativoStudente();
		cfStudente = esperienza.getCfStudente();
		classeStudente = esperienza.getClasseStudente();
	}
	
	public void addOreValidate(int ore) {
		oreValidate += ore;
	}
	
	public void addOreDaValidare(int ore) {
		oreDaValidare += ore;
	}
	
	public Long getEsperienzaSvoltaId() {
		return esperienzaSvoltaId;
	}
	public void setEsperienzaSvoltaId(Long esperienzaSvoltaId) {
		this.esperienzaSvoltaId = esperienzaSvoltaId;
	}
	public String getIstitutoId() {
		return istitutoId;
	}
	public void setIstitutoId(String istitutoId) {
		this.istitutoId = istitutoId;
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

	public int getOreValidate() {
		return oreValidate;
	}

	public void setOreValidate(int oreValidate) {
		this.oreValidate = oreValidate;
	}

	public int getOreDaValidare() {
		return oreDaValidare;
	}

	public void setOreDaValidare(int oreDaValidare) {
		this.oreDaValidare = oreDaValidare;
	}

	public List<PresenzaGiornaliera> getPresenze() {
		return presenze;
	}

	public void setPresenze(List<PresenzaGiornaliera> presenze) {
		this.presenze = presenze;
	}

	public int getOreTotali() {
		return oreTotali;
	}

	public void setOreTotali(int oreTotali) {
		this.oreTotali = oreTotali;
	}	

}
