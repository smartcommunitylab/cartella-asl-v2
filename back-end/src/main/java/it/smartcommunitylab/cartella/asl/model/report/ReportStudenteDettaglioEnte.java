package it.smartcommunitylab.cartella.asl.model.report;

import java.util.ArrayList;
import java.util.List;

import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.Studente;

public class ReportStudenteDettaglioEnte {
	private Studente studente;
	private List<AttivitaAlternanza> attivitaList = new ArrayList<>();
	private String istituto;
	
	public Studente getStudente() {
		return studente;
	}
	public void setStudente(Studente studente) {
		this.studente = studente;
	}
	public List<AttivitaAlternanza> getAttivitaList() {
		return attivitaList;
	}
	public void setAttivitaList(List<AttivitaAlternanza> attivitaList) {
		this.attivitaList = attivitaList;
	}
	public String getIstituto() {
		return istituto;
	}
	public void setIstituto(String istituto) {
		this.istituto = istituto;
	}
}
