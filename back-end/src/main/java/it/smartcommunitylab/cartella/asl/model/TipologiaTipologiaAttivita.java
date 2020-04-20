package it.smartcommunitylab.cartella.asl.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "tipologia_tipologia_attivita")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TipologiaTipologiaAttivita {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String titolo;
	private int tipologia;
	private Boolean interna;
	private Boolean individuale;
	private Boolean convenzione;
	private Boolean progettoFormativo;
	private Boolean diarioDiBordo;
	private Boolean valutazioneTutorEsterno;
	private Boolean valutazioneStudente;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public Boolean getInterna() {
		return interna;
	}

	public void setInterna(Boolean interna) {
		this.interna = interna;
	}

	public Boolean getIndividuale() {
		return individuale;
	}

	public void setIndividuale(Boolean individuale) {
		this.individuale = individuale;
	}

	public int getTipologia() {
		return tipologia;
	}

	public void setTipologia(int tipologia) {
		this.tipologia = tipologia;
	}

	public Boolean getProgettoFormativo() {
		return progettoFormativo;
	}

	public void setProgettoFormativo(Boolean progettoFormativo) {
		this.progettoFormativo = progettoFormativo;
	}

	public Boolean getDiarioDiBordo() {
		return diarioDiBordo;
	}

	public void setDiarioDiBordo(Boolean diarioDiBordo) {
		this.diarioDiBordo = diarioDiBordo;
	}

	public Boolean getValutazioneTutorEsterno() {
		return valutazioneTutorEsterno;
	}

	public void setValutazioneTutorEsterno(Boolean valutazioneTutorEsterno) {
		this.valutazioneTutorEsterno = valutazioneTutorEsterno;
	}

	public Boolean getValutazioneStudente() {
		return valutazioneStudente;
	}

	public void setValutazioneStudente(Boolean valutazioneStudente) {
		this.valutazioneStudente = valutazioneStudente;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public Boolean getConvenzione() {
		return convenzione;
	}

	public void setConvenzione(Boolean convenzione) {
		this.convenzione = convenzione;
	}

}
