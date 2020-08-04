package it.smartcommunitylab.cartella.asl.model.report;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import it.smartcommunitylab.cartella.asl.beans.LocalDateDeserializer;

public class ReportDashboardRegistrazione {
	private String registrationId;
	private String studenteId;
	private String nominativoStudente;
	private String cfStudente;
	private String classeStudente;
	private String annoScolastico;
	private String corso;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataInizio;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataFine;
	
	public String getRegistrationId() {
		return registrationId;
	}
	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
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
	public String getAnnoScolastico() {
		return annoScolastico;
	}
	public void setAnnoScolastico(String annoScolastico) {
		this.annoScolastico = annoScolastico;
	}
	public String getCorso() {
		return corso;
	}
	public void setCorso(String corso) {
		this.corso = corso;
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
		
}
