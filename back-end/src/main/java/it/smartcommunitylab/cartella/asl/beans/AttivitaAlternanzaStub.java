package it.smartcommunitylab.cartella.asl.beans;

import java.util.List;

public class AttivitaAlternanzaStub {

	private String annoScolastico;
	private Long dataInizio;
	private Long dataFine;
	private List<Long> competenzeId;
	private Integer ore;
	private String referenteScuola;
	private String referenteScuolaCF;
	private String titolo;

	private List<String> studentiId;
	private List<String> tags;

	public String getAnnoScolastico() {
		return annoScolastico;
	}

	public void setAnnoScolastico(String annoScolastico) {
		this.annoScolastico = annoScolastico;
	}

	public Long getDataInizio() {
		return dataInizio;
	}

	public void setDataInizio(Long dataInizio) {
		this.dataInizio = dataInizio;
	}

	public Long getDataFine() {
		return dataFine;
	}

	public void setDataFine(Long dataFine) {
		this.dataFine = dataFine;
	}

	public List<Long> getCompetenzeId() {
		return competenzeId;
	}

	public void setCompetenzeId(List<Long> competenzeId) {
		this.competenzeId = competenzeId;
	}

	public Integer getOre() {
		return ore;
	}

	public void setOre(Integer ore) {
		this.ore = ore;
	}

	public String getReferenteScuola() {
		return referenteScuola;
	}

	public void setReferenteScuola(String referenteScuola) {
		this.referenteScuola = referenteScuola;
	}

	public String getReferenteScuolaCF() {
		return referenteScuolaCF;
	}

	public void setReferenteScuolaCF(String referenteScuolaCF) {
		this.referenteScuolaCF = referenteScuolaCF;
	}

	public List<String> getStudentiId() {
		return studentiId;
	}

	public void setStudentiId(List<String> studentiId) {
		this.studentiId = studentiId;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

}
