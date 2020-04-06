package it.smartcommunitylab.cartella.asl.model.report;

import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;

public class ReportArchiviaEsperienza {
	private Long esperienzaSvoltaId;
	private String istitutoId;
	private String nominativoStudente;
	private String cfStudente;
	private String classeStudente;
	private int oreAttivita;
	private int oreValidate;
	private boolean valida;
	
	public ReportArchiviaEsperienza() {}
	
	public ReportArchiviaEsperienza(EsperienzaSvolta esperienza) {
		esperienzaSvoltaId = esperienza.getId();
		istitutoId = esperienza.getIstitutoId();
		nominativoStudente = esperienza.getNominativoStudente();
		cfStudente = esperienza.getCfStudente();
		classeStudente = esperienza.getClasseStudente();
	}
	
	public void adOreValidate(int ore) {
		oreValidate += ore;
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
	public int getOreAttivita() {
		return oreAttivita;
	}
	public void setOreAttivita(int oreAttivita) {
		this.oreAttivita = oreAttivita;
	}
	public int getOreValidate() {
		return oreValidate;
	}
	public void setOreValidate(int oreValidate) {
		this.oreValidate = oreValidate;
	}

	public boolean isValida() {
		return valida;
	}

	public void setValida(boolean valida) {
		this.valida = valida;
	}
}
