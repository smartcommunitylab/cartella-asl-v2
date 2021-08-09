package it.smartcommunitylab.cartella.asl.model.report;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import it.smartcommunitylab.cartella.asl.beans.LocalDateDeserializer;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;

public class ReportPresenzeAttvitaAlternanza {
	private Long attivitaId;
	private String istitutoId;
	private String titolo;
	private int tipologia;
	private String annoScolastico;
	private String stato;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataInizio;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataFine;
	private int oreValidate;
	private int oreTotali;
	private int oreInserite;
	private int giornateValidate;
	private int giornateDaValidare;
	private int studentiDaValidare; 
	
	public ReportPresenzeAttvitaAlternanza() {}
	
	public ReportPresenzeAttvitaAlternanza(AttivitaAlternanza attivita) {
		attivitaId = attivita.getId();
		istitutoId = attivita.getIstitutoId();
		titolo = attivita.getTitolo();
		tipologia = attivita.getTipologia();
		annoScolastico = attivita.getAnnoScolastico();
		stato = attivita.getStato().toString();
		dataInizio = attivita.getDataInizio();
		dataFine = attivita.getDataFine();
		oreTotali = attivita.getOre();
	}
	
	public Long getAttivitaId() {
		return attivitaId;
	}
	public void setAttivitaId(Long attivitaId) {
		this.attivitaId = attivitaId;
	}
	public String getIstitutoId() {
		return istitutoId;
	}
	public void setIstitutoId(String istitutoId) {
		this.istitutoId = istitutoId;
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
	public String getAnnoScolastico() {
		return annoScolastico;
	}
	public void setAnnoScolastico(String annoScolastico) {
		this.annoScolastico = annoScolastico;
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
	public int getGiornateValidate() {
		return giornateValidate;
	}
	public void setGiornateValidate(int giornateValidate) {
		this.giornateValidate = giornateValidate;
	}

	public int getGiornateDaValidare() {
		return giornateDaValidare;
	}

	public void setGiornateDaValidare(int giornateDaValidare) {
		this.giornateDaValidare = giornateDaValidare;
	}

	public int getStudentiDaValidare() {
		return studentiDaValidare;
	}

	public void setStudentiDaValidare(int studentiDaValidare) {
		this.studentiDaValidare = studentiDaValidare;
	}

	public int getOreInserite() {
		return oreInserite;
	}

	public void setOreInserite(int oreInserite) {
		this.oreInserite = oreInserite;
	}
	
}
