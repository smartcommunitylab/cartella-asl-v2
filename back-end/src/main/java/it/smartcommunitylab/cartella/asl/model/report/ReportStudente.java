package it.smartcommunitylab.cartella.asl.model.report;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ReportStudente extends Report {

	private String idStudente;
	private String classe;
	private String cognomeNome;

	public String getIdStudente() {
		return idStudente;
	}

	public void setIdStudente(String idStudente) {
		this.idStudente = idStudente;
	}

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

	public String getCognomeNome() {
		return cognomeNome;
	}

	public void setCognomeNome(String cognomeNome) {
		this.cognomeNome = cognomeNome;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
