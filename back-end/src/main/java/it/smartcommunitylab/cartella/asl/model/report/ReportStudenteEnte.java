package it.smartcommunitylab.cartella.asl.model.report;

import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.Istituzione;
import it.smartcommunitylab.cartella.asl.model.Studente;

public class ReportStudenteEnte {
	private Studente studente;
	private Istituzione istituto;
	private AttivitaAlternanza attivitaAlternanza;
	private String stato;
	
	public Studente getStudente() {
		return studente;
	}
	public void setStudente(Studente studente) {
		this.studente = studente;
	}
	public Istituzione getIstituto() {
		return istituto;
	}
	public void setIstituto(Istituzione istituto) {
		this.istituto = istituto;
	}
	public AttivitaAlternanza getAttivitaAlternanza() {
		return attivitaAlternanza;
	}
	public void setAttivitaAlternanza(AttivitaAlternanza attivitaAlternanza) {
		this.attivitaAlternanza = attivitaAlternanza;
	}
	public String getStato() {
		return stato;
	}
	public void setStato(String stato) {
		this.stato = stato;
	}
}
