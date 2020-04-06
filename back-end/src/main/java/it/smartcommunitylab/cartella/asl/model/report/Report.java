package it.smartcommunitylab.cartella.asl.model.report;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Report {

	protected String nome;
	
	protected int annoCorso;
	protected String annoScolastico;
	
	protected int oreProgrammate; // somma “ore” in Attivita’
	protected int oreTotali; // oreTotali Anno Alternanza
	protected int attivitaProgrammate; // n. Esperienze
	protected int attivitaTotali; // somma Tipologia Attivita’ dell’Anno Alternanza
	protected int competenzePiano; // N. (non duplicate) del Piano Alternanza
	protected int competenzeEsperienze;  // N. (non duplicate) delle Esperienze Svolte
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getAnnoCorso() {
		return annoCorso;
	}

	public void setAnnoCorso(int annoCorso) {
		this.annoCorso = annoCorso;
	}

	public String getAnnoScolastico() {
		return annoScolastico;
	}

	public void setAnnoScolastico(String annoScolastico) {
		this.annoScolastico = annoScolastico;
	}

	public int getOreProgrammate() {
		return oreProgrammate;
	}

	public void setOreProgrammate(int oreProgrammate) {
		this.oreProgrammate = oreProgrammate;
	}

	public int getOreTotali() {
		return oreTotali;
	}

	public void setOreTotali(int oreTotali) {
		this.oreTotali = oreTotali;
	}

	public int getAttivitaProgrammate() {
		return attivitaProgrammate;
	}

	public void setAttivitaProgrammate(int attivitaProgrammate) {
		this.attivitaProgrammate = attivitaProgrammate;
	}

	public int getAttivitaTotali() {
		return attivitaTotali;
	}

	public void setAttivitaTotali(int attivitaTotali) {
		this.attivitaTotali = attivitaTotali;
	}

	public int getCompetenzePiano() {
		return competenzePiano;
	}

	public void setCompetenzePiano(int competenze) {
		this.competenzePiano = competenze;
	}

	public int getCompetenzeEsperienze() {
		return competenzeEsperienze;
	}

	public void setCompetenzeEsperienze(int competenzeEsperienze) {
		this.competenzeEsperienze = competenzeEsperienze;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
