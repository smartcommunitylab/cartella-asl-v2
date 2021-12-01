package it.smartcommunitylab.cartella.asl.model.report;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import it.smartcommunitylab.cartella.asl.beans.LocalDateDeserializer;
import it.smartcommunitylab.cartella.asl.model.ValutazioneCompetenza;

public class ValutazioneCompetenzeReport {
	public static enum Stato {
		non_compilata, incompleta, compilata
	};

	@Enumerated(EnumType.STRING)
	private Stato stato;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate ultimaModifica;
	private List<ValutazioneCompetenza> valutazioni = new ArrayList<>();
	private int acquisite;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataInizio;	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataFine;
	private int ore;
	private int oreInserite;	
	
	public LocalDate getUltimaModifica() {
		return ultimaModifica;
	}
	public void setUltimaModifica(LocalDate ultimaModifica) {
		this.ultimaModifica = ultimaModifica;
	}
	public List<ValutazioneCompetenza> getValutazioni() {
		return valutazioni;
	}
	public void setValutazioni(List<ValutazioneCompetenza> valutazioni) {
		this.valutazioni = valutazioni;
	}
	public Stato getStato() {
		return stato;
	}
	public void setStato(Stato stato) {
		this.stato = stato;
	}
	public int getAcquisite() {
		return acquisite;
	}
	public void setAcquisite(int acquisite) {
		this.acquisite = acquisite;
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
	public int getOre() {
		return ore;
	}
	public void setOre(int ore) {
		this.ore = ore;
	}
	public int getOreInserite() {
		return oreInserite;
	}
	public void setOreInserite(int oreInserite) {
		this.oreInserite = oreInserite;
	}

}
