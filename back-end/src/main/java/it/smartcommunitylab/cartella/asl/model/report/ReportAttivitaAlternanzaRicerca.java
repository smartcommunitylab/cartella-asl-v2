package it.smartcommunitylab.cartella.asl.model.report;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import it.smartcommunitylab.cartella.asl.beans.LocalDateDeserializer;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;

public class ReportAttivitaAlternanzaRicerca {
	private Long id;
	private String uuid;
	private String istitutoId;
	private String titolo;
	private String descrizione;
	private int tipologia;
	private String stato;
	private String nomeEnte;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataInizio;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataFine;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataArchiviazione;
	private List<String> studenti = new ArrayList<String>();
	private List<String> classi = new ArrayList<String>();
	
	public ReportAttivitaAlternanzaRicerca() {}
	
	public ReportAttivitaAlternanzaRicerca(AttivitaAlternanza attivita) {
		this.setId(attivita.getId());
		this.setUuid(attivita.getUuid());
		this.setIstitutoId(attivita.getIstitutoId());
		this.setTitolo(attivita.getTitolo());
		this.setDescrizione(attivita.getDescrizione());
		this.setTipologia(attivita.getTipologia());
		this.setNomeEnte(attivita.getNomeEnte());
		this.setDataInizio(attivita.getDataInizio());
		this.setDataFine(attivita.getDataFine());
		this.setDataArchiviazione(attivita.getDataArchiviazione());
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public String getTitolo() {
		return titolo;
	}
	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}
	public String getDescrizione() {
		return descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	public int getTipologia() {
		return tipologia;
	}
	public void setTipologia(int tipologia) {
		this.tipologia = tipologia;
	}
	public String getStato() {
		return stato;
	}
	public void setStato(String stato) {
		this.stato = stato;
	}
	public String getNomeEnte() {
		return nomeEnte;
	}
	public void setNomeEnte(String nomeEnte) {
		this.nomeEnte = nomeEnte;
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

	public LocalDate getDataArchiviazione() {
		return dataArchiviazione;
	}

	public void setDataArchiviazione(LocalDate dataArchiviazione) {
		this.dataArchiviazione = dataArchiviazione;
	}

	public List<String> getStudenti() {
		return studenti;
	}

	public void setStudenti(List<String> studenti) {
		this.studenti = studenti;
	}

	public List<String> getClassi() {
		return classi;
	}

	public void setClassi(List<String> classi) {
		this.classi = classi;
	}


}
