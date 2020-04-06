package it.smartcommunitylab.cartella.asl.model.report;

import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;

public class ReportEsperienzaRegistration {
	private Long esperienzaSvoltaId;
	private String istitutoId;
	private String studenteId;
	private String nominativoStudente;
	private String cfStudente;
	private String classeStudente;
	private String registrazioneId;
	
	public ReportEsperienzaRegistration() {}
	
	public ReportEsperienzaRegistration(EsperienzaSvolta esperienza) {
		esperienzaSvoltaId = esperienza.getId();
		istitutoId = esperienza.getIstitutoId();
		nominativoStudente = esperienza.getNominativoStudente();
		cfStudente = esperienza.getCfStudente();
		classeStudente = esperienza.getClasseStudente();
		studenteId = esperienza.getStudenteId();
		registrazioneId = esperienza.getRegistrazioneId();
	}
	
	public boolean isCompatible(EsperienzaSvolta esperienza) {
		return istitutoId.equals(esperienza.getIstitutoId()) &&
				studenteId.equals(esperienza.getStudenteId()) &&
				cfStudente.equals(esperienza.getCfStudente()) &&
				classeStudente.equals(esperienza.getClasseStudente()) &&
				studenteId.equals(esperienza.getStudenteId()) &&
				registrazioneId.equals(esperienza.getRegistrazioneId());
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

	public String getStudenteId() {
		return studenteId;
	}

	public void setStudenteId(String studenteId) {
		this.studenteId = studenteId;
	}

	public String getRegistrazioneId() {
		return registrazioneId;
	}

	public void setRegistrazioneId(String registrazioneId) {
		this.registrazioneId = registrazioneId;
	}

}
