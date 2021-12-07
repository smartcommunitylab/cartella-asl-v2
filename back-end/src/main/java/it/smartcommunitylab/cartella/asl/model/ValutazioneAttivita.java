package it.smartcommunitylab.cartella.asl.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import it.smartcommunitylab.cartella.asl.beans.LocalDateDeserializer;

@Entity
@Table(name = "valutazione_attivita", indexes = { 
		@Index(name = "esperienzaId_idx", columnList = "esperienzaSvoltaId", unique = false)})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ValutazioneAttivita {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long attivitaAlternanzaId;
	private Long esperienzaSvoltaId;
	private String studenteId;
	private String istitutoId;
	private String codiceDomanda;
	private Integer posizione;
	private Boolean rispostaChiusa;
	private Integer punteggio = 0;
	@Column(columnDefinition="VARCHAR(2048)")
	private String descrizione;
	@Column(columnDefinition="VARCHAR(2048)")
	private String risposta;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate ultimaModifica;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getAttivitaAlternanzaId() {
		return attivitaAlternanzaId;
	}
	public void setAttivitaAlternanzaId(Long attivitaAlternanzaId) {
		this.attivitaAlternanzaId = attivitaAlternanzaId;
	}
	public Long getEsperienzaSvoltaId() {
		return esperienzaSvoltaId;
	}
	public void setEsperienzaSvoltaId(Long esperienzaSvoltaId) {
		this.esperienzaSvoltaId = esperienzaSvoltaId;
	}
	public String getStudenteId() {
		return studenteId;
	}
	public void setStudenteId(String studenteId) {
		this.studenteId = studenteId;
	}
	public String getIstitutoId() {
		return istitutoId;
	}
	public void setIstitutoId(String istitutoId) {
		this.istitutoId = istitutoId;
	}
	public String getCodiceDomanda() {
		return codiceDomanda;
	}
	public void setCodiceDomanda(String codiceDomanda) {
		this.codiceDomanda = codiceDomanda;
	}
	public Integer getPosizione() {
		return posizione;
	}
	public void setPosizione(Integer posizione) {
		this.posizione = posizione;
	}
	public Boolean getRispostaChiusa() {
		return rispostaChiusa;
	}
	public void setRispostaChiusa(Boolean rispostaChiusa) {
		this.rispostaChiusa = rispostaChiusa;
	}
	public Integer getPunteggio() {
		return punteggio;
	}
	public void setPunteggio(Integer punteggio) {
		this.punteggio = punteggio;
	}
	public String getRisposta() {
		return risposta;
	}
	public void setRisposta(String risposta) {
		this.risposta = risposta;
	}
	public LocalDate getUltimaModifica() {
		return ultimaModifica;
	}
	public void setUltimaModifica(LocalDate ultimaModifica) {
		this.ultimaModifica = ultimaModifica;
	}
	public String getDescrizione() {
		return descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}


}
