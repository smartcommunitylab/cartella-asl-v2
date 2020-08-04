package it.smartcommunitylab.cartella.asl.model.report;

import java.util.HashMap;
import java.util.Map;

public class ReportDashboardUsoSistema {
	private long numeroStudentiIscritti;
	private long numeroStudentiConEsperienze;
	private long numeroAttivita;
	Map<Integer, Long> tipologiaMap = new HashMap<>();
	
	public long getNumeroStudentiIscritti() {
		return numeroStudentiIscritti;
	}
	public void setNumeroStudentiIscritti(long numeroStudentiIscritti) {
		this.numeroStudentiIscritti = numeroStudentiIscritti;
	}
	public long getNumeroStudentiConEsperienze() {
		return numeroStudentiConEsperienze;
	}
	public void setNumeroStudentiConEsperienze(long numeroStudentiConEsperienze) {
		this.numeroStudentiConEsperienze = numeroStudentiConEsperienze;
	}
	public long getNumeroAttivita() {
		return numeroAttivita;
	}
	public void setNumeroAttivita(long numeroAttivita) {
		this.numeroAttivita = numeroAttivita;
	}
	public Map<Integer, Long> getTipologiaMap() {
		return tipologiaMap;
	}
	public void setTipologiaMap(Map<Integer, Long> tipologiaMap) {
		this.tipologiaMap = tipologiaMap;
	}
	
}
