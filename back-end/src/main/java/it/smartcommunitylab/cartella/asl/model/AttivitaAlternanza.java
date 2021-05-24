package it.smartcommunitylab.cartella.asl.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import it.smartcommunitylab.cartella.asl.beans.LocalDateDeserializer;

@Entity
@Table(name = "attivita_alternanza", indexes = { 
		@Index(name = "istitutoId_idx", columnList = "istitutoId", unique = false),
		@Index(name = "tipologia_idx", columnList = "tipologia", unique = false)})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AttivitaAlternanza {

	public static enum Stati {
		attiva, in_attesa, in_corso, revisione, archiviata
	};

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String uuid;
	private String istitutoId;
	private String titolo;
	private int tipologia;
	private String annoScolastico;
	@Enumerated(EnumType.STRING)
	private Stati stato;
	private String descrizione;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataInizio;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataFine;
	private String oraInizio;
	private String oraFine;
	private int ore;
	private Long offertaId;
	private String titoloOfferta;
	private String enteId;
	private String nomeEnte;
	private String referenteScuola;
	private String referenteScuolaCF;
	private String referenteScuolaTelefono;
	private String referenteEsterno;
	private String referenteEsternoCF;
	private String referenteEsternoTelefono;
	private String formatore;
	private String formatoreCF;
	private String luogoSvolgimento;
	private Double latitude;
	private Double longitude;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataArchiviazione;
	
	@Column(columnDefinition = "bit default 0")
	private Boolean rendicontazioneCorpo;
	
	@Transient
	private int oreSvolte;
	
	public AttivitaAlternanza clona() {
		AttivitaAlternanza aa = new AttivitaAlternanza();
		aa.setAnnoScolastico(annoScolastico);
		aa.setDataArchiviazione(dataArchiviazione);
		aa.setDataFine(dataFine);
		aa.setDataInizio(dataInizio);
		aa.setDescrizione(descrizione);
		aa.setEnteId(enteId);
		aa.setFormatore(formatore);
		aa.setFormatoreCF(formatoreCF);
		aa.setId(id);
		aa.setIstitutoId(istitutoId);
		aa.setLatitude(latitude);
		aa.setLongitude(longitude);
		aa.setLuogoSvolgimento(luogoSvolgimento);
		aa.setNomeEnte(nomeEnte);
		aa.setOffertaId(offertaId);
		aa.setOraFine(oraFine);
		aa.setOraInizio(oraInizio);
		aa.setOre(ore);
		aa.setReferenteEsterno(referenteEsterno);
		aa.setReferenteEsternoCF(referenteEsternoCF);
		aa.setReferenteEsternoTelefono(referenteEsternoTelefono);
		aa.setReferenteScuola(referenteScuola);
		aa.setReferenteScuolaCF(referenteScuolaCF);
		aa.setReferenteScuolaTelefono(referenteScuolaTelefono);
		aa.setStato(stato);
		aa.setTipologia(tipologia);
		aa.setTitolo(titolo);
		aa.setTitoloOfferta(titoloOfferta);
		aa.setUuid(uuid);
		return aa;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAnnoScolastico() {
		return annoScolastico;
	}

	public void setAnnoScolastico(String annoScolastico) {
		this.annoScolastico = annoScolastico;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public String getOraInizio() {
		return oraInizio;
	}

	public void setOraInizio(String oraInizio) {
		this.oraInizio = oraInizio;
	}

	public String getOraFine() {
		return oraFine;
	}

	public void setOraFine(String oraFine) {
		this.oraFine = oraFine;
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

	public String getIstitutoId() {
		return istitutoId;
	}

	public void setIstitutoId(String istitutoId) {
		this.istitutoId = istitutoId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getTipologia() {
		return tipologia;
	}

	public void setTipologia(int tipologia) {
		this.tipologia = tipologia;
	}

	public Stati getStato() {
		return stato;
	}

	public void setStato(Stati stato) {
		this.stato = stato;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public LocalDate getDataInizio() {
		return dataInizio;
	}

	public void setDataInizio(LocalDate dataInizio) {
		this.dataInizio = dataInizio;
	}

	public LocalDate getDataFine() {
		return dataFine;
	}

	public void setDataFine(LocalDate dataFine) {
		this.dataFine = dataFine;
	}

	public int getOre() {
		return ore;
	}

	public void setOre(int ore) {
		this.ore = ore;
	}

	public Long getOffertaId() {
		return offertaId;
	}

	public void setOffertaId(Long offertaId) {
		this.offertaId = offertaId;
	}

	public String getTitoloOfferta() {
		return titoloOfferta;
	}

	public void setTitoloOfferta(String titoloOfferta) {
		this.titoloOfferta = titoloOfferta;
	}

	public String getEnteId() {
		return enteId;
	}

	public void setEnteId(String enteId) {
		this.enteId = enteId;
	}

	public String getNomeEnte() {
		return nomeEnte;
	}

	public void setNomeEnte(String nomeEnte) {
		this.nomeEnte = nomeEnte;
	}

	public String getReferenteEsterno() {
		return referenteEsterno;
	}

	public void setReferenteEsterno(String referenteEsterno) {
		this.referenteEsterno = referenteEsterno;
	}

	public String getReferenteEsternoCF() {
		return referenteEsternoCF;
	}

	public void setReferenteEsternoCF(String referenteEsternoCF) {
		this.referenteEsternoCF = referenteEsternoCF;
	}

	public String getFormatore() {
		return formatore;
	}

	public void setFormatore(String formatore) {
		this.formatore = formatore;
	}

	public String getFormatoreCF() {
		return formatoreCF;
	}

	public void setFormatoreCF(String formatoreCF) {
		this.formatoreCF = formatoreCF;
	}

	public String getLuogoSvolgimento() {
		return luogoSvolgimento;
	}

	public void setLuogoSvolgimento(String luogoSvolgimento) {
		this.luogoSvolgimento = luogoSvolgimento;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AttivitaAlternanza other = (AttivitaAlternanza) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public LocalDate getDataArchiviazione() {
		return dataArchiviazione;
	}

	public void setDataArchiviazione(LocalDate dataArchiviazione) {
		this.dataArchiviazione = dataArchiviazione;
	}

	public String getReferenteScuolaTelefono() {
		return referenteScuolaTelefono;
	}

	public void setReferenteScuolaTelefono(String referenteScuolaTelefono) {
		this.referenteScuolaTelefono = referenteScuolaTelefono;
	}

	public String getReferenteEsternoTelefono() {
		return referenteEsternoTelefono;
	}

	public void setReferenteEsternoTelefono(String referenteEsternoTelefono) {
		this.referenteEsternoTelefono = referenteEsternoTelefono;
	}

	public int getOreSvolte() {
		return oreSvolte;
	}

	public void setOreSvolte(int oreSvolte) {
		this.oreSvolte = oreSvolte;
	}

	public Boolean getRendicontazioneCorpo() {
		return rendicontazioneCorpo;
	}

	public void setRendicontazioneCorpo(Boolean rendicontazioneCorpo) {
		this.rendicontazioneCorpo = rendicontazioneCorpo;
	}
}
