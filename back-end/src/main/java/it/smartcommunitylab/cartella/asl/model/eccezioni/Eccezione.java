package it.smartcommunitylab.cartella.asl.model.eccezioni;

import java.time.temporal.ChronoField;
import java.util.List;

import com.google.common.collect.Lists;

import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.util.Utils;

public class Eccezione {

	private Long attivitaAlternanzaId;
	private String attivitaAlternanzaTitolo;

	private Long pianoAlternanzaId;

	private String periodo;
	private int tipologia;
	
	List<DettaglioEccezione> dettagli = Lists.newArrayList();

	public Eccezione(AttivitaAlternanza aa, Long paId) {
		attivitaAlternanzaId = aa.getId();
		attivitaAlternanzaTitolo = aa.getTitolo();
		pianoAlternanzaId = paId;
		periodo = Utils.seasons(aa.getDataInizio().getLong(ChronoField.MILLI_OF_SECOND), aa.getDataFine().getLong(ChronoField.MILLI_OF_SECOND));
		tipologia = aa.getTipologia();
	}
	
	public Long getAttivitaAlternanzaId() {
		return attivitaAlternanzaId;
	}

	public void setAttivitaAlternanzaId(Long attivitaAlternanzaId) {
		this.attivitaAlternanzaId = attivitaAlternanzaId;
	}

	public String getAttivitaAlternanzaTitolo() {
		return attivitaAlternanzaTitolo;
	}

	public void setAttivitaAlternanzaTitolo(String attivitaAlternanzaTitolo) {
		this.attivitaAlternanzaTitolo = attivitaAlternanzaTitolo;
	}

	public Long getPianoAlternanzaId() {
		return pianoAlternanzaId;
	}

	public void setPianoAlternanzaId(Long pianoAlternanzaId) {
		this.pianoAlternanzaId = pianoAlternanzaId;
	}

	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public int getTipologia() {
		return tipologia;
	}

	public void setTipologia(int tipologia) {
		this.tipologia = tipologia;
	}

	public List<DettaglioEccezione> getDettagli() {
		return dettagli;
	}

	public void setDettagli(List<DettaglioEccezione> dettagli) {
		this.dettagli = dettagli;
	}

}
