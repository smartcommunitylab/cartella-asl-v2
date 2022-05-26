package it.smartcommunitylab.cartella.asl.model.report;

import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.Istituzione;
import it.smartcommunitylab.cartella.asl.model.Studente;

public class ReportIstitutoDettaglioEnte {
	private Studente studente;
	private AttivitaAlternanza attivitaAlternanza;
	private Istituzione istituto;
	
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
	
}
