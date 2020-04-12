package it.smartcommunitylab.cartella.asl.model.report;

import java.util.ArrayList;
import java.util.List;

import it.smartcommunitylab.cartella.asl.model.Competenza;
import it.smartcommunitylab.cartella.asl.model.Studente;

public class ReportDettaglioStudente {
	private Studente studente;
	private List<ReportEsperienzaStudente> esperienze = new ArrayList<>();
	private List<Competenza> competenze = new ArrayList<>();
	private String titoloPiano;
	private Long pianoId;
	private int oreValidate;
	private int oreTotali;
	private OreSvolte oreSvolteTerza = new OreSvolte();
	private OreSvolte oreSvolteQuarta = new OreSvolte();
	private OreSvolte oreSvolteQuinta = new OreSvolte();

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
	public String getTitoloPiano() {
		return titoloPiano;
	}
	public void setTitoloPiano(String titoloPiano) {
		this.titoloPiano = titoloPiano;
	}
	public Long getPianoId() {
		return pianoId;
	}
	public void setPianoId(Long pianoId) {
		this.pianoId = pianoId;
	}
	public int getOreValidate() {
		return oreValidate;
	}
	public void setOreValidate(int oreValidate) {
		this.oreValidate = oreValidate;
	}
	public OreSvolte getOreSvolteTerza() {
		return oreSvolteTerza;
	}
	public void setOreSvolteTerza(OreSvolte oreSvolteTerza) {
		this.oreSvolteTerza = oreSvolteTerza;
	}
	public OreSvolte getOreSvolteQuarta() {
		return oreSvolteQuarta;
	}
	public void setOreSvolteQuarta(OreSvolte oreSvolteQuarta) {
		this.oreSvolteQuarta = oreSvolteQuarta;
	}
	public OreSvolte getOreSvolteQuinta() {
		return oreSvolteQuinta;
	}
	public void setOreSvolteQuinta(OreSvolte oreSvolteQuinta) {
		this.oreSvolteQuinta = oreSvolteQuinta;
	}	
	
}
