package it.smartcommunitylab.cartella.asl.model.report;

import it.smartcommunitylab.cartella.asl.model.Competenza;

public class ReportCompetenzaDettaglio {
	private Competenza competenza;
	private int numeroAttivitaAltAssociate;
	private int numeroPianoAltAssociate;
	private int numeroStudentiAssociate;
	private int numeroOfferteAssociate;

	public Competenza getCompetenza() {
		return competenza;
	}

	public void setCompetenza(Competenza competenza) {
		this.competenza = competenza;
	}

	public int getNumeroAttivitaAltAssociate() {
		return numeroAttivitaAltAssociate;
	}

	public void setNumeroAttivitaAltAssociate(int numeroAttivitaAltAssociate) {
		this.numeroAttivitaAltAssociate = numeroAttivitaAltAssociate;
	}

	public int getNumeroPianoAltAssociate() {
		return numeroPianoAltAssociate;
	}

	public void setNumeroPianoAltAssociate(int numeroPianoAltAssociate) {
		this.numeroPianoAltAssociate = numeroPianoAltAssociate;
	}

	public int getNumeroStudentiAssociate() {
		return numeroStudentiAssociate;
	}

	public void setNumeroStudentiAssociate(int numeroStudentiAssociate) {
		this.numeroStudentiAssociate = numeroStudentiAssociate;
	}

	public int getNumeroOfferteAssociate() {
		return numeroOfferteAssociate;
	}

	public void setNumeroOfferteAssociate(int numeroOfferteAssociate) {
		this.numeroOfferteAssociate = numeroOfferteAssociate;
	}

}
