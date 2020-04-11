package it.smartcommunitylab.cartella.asl.model.report;

import java.util.ArrayList;
import java.util.List;

import it.smartcommunitylab.cartella.asl.model.Competenza;
import it.smartcommunitylab.cartella.asl.model.Studente;

public class ReportDettaglioStudente {
	private Studente studente;
	private List<ReportEsperienzaStudente> esperienze = new ArrayList<>();
	private List<Competenza> competenze = new ArrayList<>();
	private int oreValidate;
	private int oreTotali;
	
	public Studente getStudente() {
		return studente;
	}
	public void setStudente(Studente studente) {
		this.studente = studente;
	}
	public List<ReportEsperienzaStudente> getEsperienze() {
		return esperienze;
	}
	public void setEsperienze(List<ReportEsperienzaStudente> esperienze) {
		this.esperienze = esperienze;
	}
	public int getOreValidate() {
		return oreValidate;
	}
	public void setOreValidate(int oreValidate) {
		this.oreValidate = oreValidate;
	}
	public int getOreTotali() {
		return oreTotali;
	}
	public void setOreTotali(int oreTotali) {
		this.oreTotali = oreTotali;
	}
	public List<Competenza> getCompetenze() {
		return competenze;
	}
	public void setCompetenze(List<Competenza> competenze) {
		this.competenze = competenze;
	}	
	
}
