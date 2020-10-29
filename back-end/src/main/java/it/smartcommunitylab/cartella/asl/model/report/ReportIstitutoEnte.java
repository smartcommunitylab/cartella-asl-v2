package it.smartcommunitylab.cartella.asl.model.report;

import it.smartcommunitylab.cartella.asl.model.Istituzione;

public class ReportIstitutoEnte {
	private Istituzione istituto;
	private long attivitaInCorso;
	
	public Istituzione getIstituto() {
		return istituto;
	}
	public void setIstituto(Istituzione istituto) {
		this.istituto = istituto;
	}
	public long getAttivitaInCorso() {
		return attivitaInCorso;
	}
	public void setAttivitaInCorso(long attivitaInCorso) {
		this.attivitaInCorso = attivitaInCorso;
	}
}
