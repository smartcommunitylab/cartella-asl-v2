package it.smartcommunitylab.cartella.asl.model.report;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ReportAttivitaGiornaliera implements Comparable<ReportAttivitaGiornaliera> {

	private String studenteId;
	private String classe;
	private String nome;

	private int oreEffettuate; // somma “ore” di attivita completate
	private int oreTotali; // oreTotali del CorsoDiStudio
	private int esperienzeEffettuate; // n. Esperienze completate
	private int esperienzeTotali; // Esperienze totali
	private int competenzeEffettuate; // N. (non duplicate) delle Esperienze completate
	private int competenzeTotali; // N. (non duplicate) delle competenze in PianoAlternanza

	
	public String getStudenteId() {
		return studenteId;
	}

	public void setStudenteId(String studenteId) {
		this.studenteId = studenteId;
	}

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getOreEffettuate() {
		return oreEffettuate;
	}

	public void setOreEffettuate(int oreEffettuate) {
		this.oreEffettuate = oreEffettuate;
	}

	public int getOreTotali() {
		return oreTotali;
	}

	public void setOreTotali(int oreTotali) {
		this.oreTotali = oreTotali;
	}

	public int getEsperienzeEffettuate() {
		return esperienzeEffettuate;
	}

	public void setEsperienzeEffettuate(int esperienzeProgrammate) {
		this.esperienzeEffettuate = esperienzeProgrammate;
	}

	public int getEsperienzeTotali() {
		return esperienzeTotali;
	}

	public void setEsperienzeTotali(int esperienzeTotali) {
		this.esperienzeTotali = esperienzeTotali;
	}

	public int getCompetenzeEffettuate() {
		return competenzeEffettuate;
	}

	public void setCompetenzeEffettuate(int competenzeEsperienze) {
		this.competenzeEffettuate = competenzeEsperienze;
	}

	public int getCompetenzeTotali() {
		return competenzeTotali;
	}

	public void setCompetenzeTotali(int competenzePiano) {
		this.competenzeTotali = competenzePiano;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public int compareTo(ReportAttivitaGiornaliera o) {
		return this.getNome().compareTo(o.getNome());
	}
}
