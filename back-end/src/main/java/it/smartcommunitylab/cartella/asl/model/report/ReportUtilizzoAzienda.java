package it.smartcommunitylab.cartella.asl.model.report;

public class ReportUtilizzoAzienda {
	private long offerte = 0;
	private long attivitaAlternanza = 0;
	
	public boolean isUsed() {
		return (offerte > 0) || (attivitaAlternanza > 0);
	}
	
	public long getOfferte() {
		return offerte;
	}
	public void setOfferte(long offerte) {
		this.offerte = offerte;
	}
	public long getAttivitaAlternanza() {
		return attivitaAlternanza;
	}
	public void setAttivitaAlternanza(long attivitaAlternanza) {
		this.attivitaAlternanza = attivitaAlternanza;
	}
	
}
