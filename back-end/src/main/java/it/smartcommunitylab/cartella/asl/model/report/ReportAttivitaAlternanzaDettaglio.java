package it.smartcommunitylab.cartella.asl.model.report;

import java.util.ArrayList;
import java.util.List;

import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;

public class ReportAttivitaAlternanzaDettaglio {
	private AttivitaAlternanza attivitaAlternanza;
	private List<ReportEsperienzaRegistration> esperienze = new ArrayList<ReportEsperienzaRegistration>();
	
	public AttivitaAlternanza getAttivitaAlternanza() {
		return attivitaAlternanza;
	}
	public void setAttivitaAlternanza(AttivitaAlternanza attivitaAlternanza) {
		this.attivitaAlternanza = attivitaAlternanza;
	}
	public List<ReportEsperienzaRegistration> getEsperienze() {
		return esperienze;
	}
	public void setEsperienze(List<ReportEsperienzaRegistration> esperienze) {
		this.esperienze = esperienze;
	}
	
}
