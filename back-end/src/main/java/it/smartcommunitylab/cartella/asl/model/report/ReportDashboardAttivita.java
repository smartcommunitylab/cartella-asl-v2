package it.smartcommunitylab.cartella.asl.model.report;

public class ReportDashboardAttivita {
	private long numeroEsperienze;
	private long numeroEsperienzeAnnullate;
	private long numeroEsperienzeAllineate;
	private long numeroAttivita;
	private long numeroAttivitaArchiviate;
	private long numeroAttivitaSenzaStudenti;
	
	public long getNumeroEsperienze() {
		return numeroEsperienze;
	}
	public void setNumeroEsperienze(long numeroEsperienze) {
		this.numeroEsperienze = numeroEsperienze;
	}
	public long getNumeroEsperienzeAnnullate() {
		return numeroEsperienzeAnnullate;
	}
	public void setNumeroEsperienzeAnnullate(long numeroEsperienzeAnnullate) {
		this.numeroEsperienzeAnnullate = numeroEsperienzeAnnullate;
	}
	public long getNumeroEsperienzeAllineate() {
		return numeroEsperienzeAllineate;
	}
	public void setNumeroEsperienzeAllineate(long numeroEsperienzeAllineate) {
		this.numeroEsperienzeAllineate = numeroEsperienzeAllineate;
	}
	public long getNumeroAttivita() {
		return numeroAttivita;
	}
	public void setNumeroAttivita(long numeroAttivita) {
		this.numeroAttivita = numeroAttivita;
	}
	public long getNumeroAttivitaArchiviate() {
		return numeroAttivitaArchiviate;
	}
	public void setNumeroAttivitaArchiviate(long numeroAttivitaArchiviate) {
		this.numeroAttivitaArchiviate = numeroAttivitaArchiviate;
	}
	public long getNumeroAttivitaSenzaStudenti() {
		return numeroAttivitaSenzaStudenti;
	}
	public void setNumeroAttivitaSenzaStudenti(long numeroAttivitaSenzaStudenti) {
		this.numeroAttivitaSenzaStudenti = numeroAttivitaSenzaStudenti;
	}
}
