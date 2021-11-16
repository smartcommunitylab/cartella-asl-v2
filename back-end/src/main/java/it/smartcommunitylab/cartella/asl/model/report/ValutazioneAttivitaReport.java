package it.smartcommunitylab.cartella.asl.model.report;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import it.smartcommunitylab.cartella.asl.beans.LocalDateDeserializer;
import it.smartcommunitylab.cartella.asl.model.ValutazioneAttivita;

public class ValutazioneAttivitaReport {
	public static enum Stato {
		non_compilata, incompleta, compilata
	};

	@Enumerated(EnumType.STRING)
	private Stato stato;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate ultimaModifica;
	private String media;
	private List<ValutazioneAttivita> valutazioni = new ArrayList<>();
	
	public LocalDate getUltimaModifica() {
		return ultimaModifica;
	}
	public void setUltimaModifica(LocalDate ultimaModifica) {
		this.ultimaModifica = ultimaModifica;
	}
	public List<ValutazioneAttivita> getValutazioni() {
		return valutazioni;
	}
	public void setValutazioni(List<ValutazioneAttivita> valutazioni) {
		this.valutazioni = valutazioni;
	}
	public Stato getStato() {
		return stato;
	}
	public void setStato(Stato stato) {
		this.stato = stato;
	}
	public String getMedia() {
		return media;
	}
	public void setMedia(String media) {
		this.media = media;
	}

}
