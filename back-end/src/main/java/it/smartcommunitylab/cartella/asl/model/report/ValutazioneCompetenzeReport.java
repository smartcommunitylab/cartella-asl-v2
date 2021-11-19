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

}
