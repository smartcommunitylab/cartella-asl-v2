package it.smartcommunitylab.cartella.asl.model.report;

import java.util.Map;
import java.util.TreeMap;

public class ReportDashboardIstituto {
	private long numeroAttivitaInAttesa;
	private long numeroAttivitaInCorso;
	private long numeroAttivitaInRevisione;
	Map<Integer, Long> tipologiaMap = new TreeMap<>();
	Map<String, Double> classiMap = new TreeMap<>();
	
	public Map<Integer, Long> getTipologiaMap() {
		return tipologiaMap;
	}
	public void setTipologiaMap(Map<Integer, Long> tipologiaMap) {
		this.tipologiaMap = tipologiaMap;
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
	public Map<String, Double> getClassiMap() {
		return classiMap;
	}
	public void setClassiMap(Map<String, Double> classiMap) {
		this.classiMap = classiMap;
	}
	
}
