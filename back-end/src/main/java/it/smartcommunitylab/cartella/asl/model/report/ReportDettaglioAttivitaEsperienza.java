package it.smartcommunitylab.cartella.asl.model.report;

import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;

public class ReportDettaglioAttivitaEsperienza {
	private AttivitaAlternanza aa;
	private EsperienzaSvolta es;
	private int oreValidate;
	private int oreDaValidare;
	private int oreTotali;


	public ReportDettaglioAttivitaEsperienza() {
	}

	public ReportDettaglioAttivitaEsperienza(AttivitaAlternanza aa, EsperienzaSvolta es,
			int oreValidate, int oreDaValidare, int oreTotali) {
		this.aa = aa;
		this.es = es;
		this.oreTotali = oreTotali;
		this.oreValidate = oreValidate;
		this.oreDaValidare = oreDaValidare;
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

	public int getOreDaValidare() {
		return oreDaValidare;
	}

	public void setOreDaValidare(int oreDaValidare) {
		this.oreDaValidare = oreDaValidare;
	}

}
