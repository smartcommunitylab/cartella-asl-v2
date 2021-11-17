package it.smartcommunitylab.cartella.asl.model;

import java.time.LocalDate;

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
@Table(name = "valutazione_competenza", indexes = { 
		@Index(name = "esperienzaId_idx", columnList = "esperienzaSvoltaId", unique = false)})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ValutazioneCompetenza {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long attivitaAlternanzaId;
	private Long esperienzaSvoltaId;
	private String studenteId;
	private String istitutoId;
	private Integer ordine;
	private Integer punteggio = 0;
	private String competenzaTitolo;
	private String competenzaOwnerName;
	private String competenzaUri;
	private String competenzaSource;
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
	public Integer getOrdine() {
		return ordine;
	}
	public void setOrdine(Integer ordine) {
		this.ordine = ordine;
	}
	public Integer getPunteggio() {
		return punteggio;
	}
	public void setPunteggio(Integer punteggio) {
		this.punteggio = punteggio;
	}
	public String getCompetenzaTitolo() {
		return competenzaTitolo;
	}
	public void setCompetenzaTitolo(String competenzaTitolo) {
		this.competenzaTitolo = competenzaTitolo;
	}
	public String getCompetenzaOwnerName() {
		return competenzaOwnerName;
	}
	public void setCompetenzaOwnerName(String competenzaOwnerName) {
		this.competenzaOwnerName = competenzaOwnerName;
	}
	public String getCompetenzaUri() {
		return competenzaUri;
	}
	public void setCompetenzaUri(String competenzaUri) {
		this.competenzaUri = competenzaUri;
	}
	public String getCompetenzaSource() {
		return competenzaSource;
	}
	public void setCompetenzaSource(String competenzaSource) {
		this.competenzaSource = competenzaSource;
	}
	public LocalDate getUltimaModifica() {
		return ultimaModifica;
	}
	public void setUltimaModifica(LocalDate ultimaModifica) {
		this.ultimaModifica = ultimaModifica;
	}
	


}
