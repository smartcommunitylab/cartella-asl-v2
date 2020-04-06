package it.smartcommunitylab.cartella.asl.model.report;

import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;

public class ReportDettaglioAttivitaEsperienza {
	private AttivitaAlternanza aa;
	private EsperienzaSvolta es;

	public ReportDettaglioAttivitaEsperienza() {
	}

	public ReportDettaglioAttivitaEsperienza(AttivitaAlternanza aa, EsperienzaSvolta es) {
		this.aa = aa;
		this.es = es;
	}

	public AttivitaAlternanza getAa() {
		return aa;
	}

	public void setAa(AttivitaAlternanza aa) {
		this.aa = aa;
	}

	public EsperienzaSvolta getEs() {
		return es;
	}

	public void setEs(EsperienzaSvolta es) {
		this.es = es;
	}

}
