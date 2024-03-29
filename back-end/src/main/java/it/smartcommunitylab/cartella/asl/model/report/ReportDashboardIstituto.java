package it.smartcommunitylab.cartella.asl.model.report;

import java.util.Map;
import java.util.TreeMap;

public class ReportDashboardIstituto {
	private long numeroOreTotali;
	private long numeroAttivitaInAttesa;
	private long numeroAttivitaInCorso;
	private long numeroAttivitaInRevisione;
	Map<Integer, Long> oreTipologiaMap = new TreeMap<>();
	Map<String, MediaOreClasse> oreClassiMap = new TreeMap<>();
	Map<String, OreStudente> oreStudentiMap = new TreeMap<>();
	
	public long getNumeroOreTotali() {
		return numeroOreTotali;
	}
	public void setNumeroOreTotali(long numeroOreTotali) {
		this.numeroOreTotali = numeroOreTotali;
	}
	public long getNumeroAttivitaInAttesa() {
		return numeroAttivitaInAttesa;
	}
	public void setNumeroAttivitaInAttesa(long numeroAttivitaInAttesa) {
		this.numeroAttivitaInAttesa = numeroAttivitaInAttesa;
	}
	public long getNumeroAttivitaInCorso() {
		return numeroAttivitaInCorso;
	}
	public void setNumeroAttivitaInCorso(long numeroAttivitaInCorso) {
		this.numeroAttivitaInCorso = numeroAttivitaInCorso;
	}
	public long getNumeroAttivitaInRevisione() {
		return numeroAttivitaInRevisione;
	}
	public void setNumeroAttivitaInRevisione(long numeroAttivitaInRevisione) {
		this.numeroAttivitaInRevisione = numeroAttivitaInRevisione;
	}
	public Map<Integer, Long> getOreTipologiaMap() {
		return oreTipologiaMap;
	}
	public void setOreTipologiaMap(Map<Integer, Long> oreTipologiaMap) {
		this.oreTipologiaMap = oreTipologiaMap;
	}
	public Map<String, MediaOreClasse> getOreClassiMap() {
		return oreClassiMap;
	}
	public void setOreClassiMap(Map<String, MediaOreClasse> oreClassiMap) {
		this.oreClassiMap = oreClassiMap;
	}
	public Map<String, OreStudente> getOreStudentiMap() {
		return oreStudentiMap;
	}
	public void setOreStudentiMap(Map<String, OreStudente> oreStudentiMap) {
		this.oreStudentiMap = oreStudentiMap;
	}
	
}
