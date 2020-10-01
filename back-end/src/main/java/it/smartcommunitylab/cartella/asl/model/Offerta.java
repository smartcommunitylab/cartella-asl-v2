package it.smartcommunitylab.cartella.asl.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import it.smartcommunitylab.cartella.asl.beans.LocalDateDeserializer;
import it.smartcommunitylab.cartella.asl.beans.OffertaIstitutoStub;

@Entity
@Table(name = "offerta")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Offerta {

	public static enum Stati {
		scaduta, disponibile
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String uuid;
	private String istitutoId;
	private String titolo;
	private String descrizione;
	private int tipologia;
	@Transient
	@Enumerated(EnumType.STRING)
	private Stati stato;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataInizio;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataFine;
	private String oraInizio;
	private String oraFine;
	private int ore;
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
	private String prerequisiti;
	private int postiDisponibili;
	private int postiRimanenti;
	@Transient
	private int numeroAttivita;
	@Transient
	List<OffertaIstitutoStub> istitutiAssociati = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getIstitutoId() {
		return istitutoId;
	}

	public void setIstitutoId(String istitutoId) {
		this.istitutoId = istitutoId;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public int getTipologia() {
		return tipologia;
	}

	public void setTipologia(int tipologia) {
		this.tipologia = tipologia;
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

	public int getOre() {
		return ore;
	}

	public void setOre(int ore) {
		this.ore = ore;
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

	public String getPrerequisiti() {
		return prerequisiti;
	}

	public void setPrerequisiti(String prerequisiti) {
		this.prerequisiti = prerequisiti;
	}

	public int getPostiDisponibili() {
		return postiDisponibili;
	}

	public void setPostiDisponibili(int postiDisponibili) {
		this.postiDisponibili = postiDisponibili;
	}

	public int getPostiRimanenti() {
		return postiRimanenti;
	}

	public void setPostiRimanenti(int postiRimanenti) {
		this.postiRimanenti = postiRimanenti;
	}

	public Stati getStato() {
		return stato;
	}

	public void setStato(Stati stato) {
		this.stato = stato;
	}

	public int getNumeroAttivita() {
		return numeroAttivita;
	}

	public void setNumeroAttivita(int numeroAttivita) {
		this.numeroAttivita = numeroAttivita;
	}

	public String getReferenteEsternoTelefono() {
		return referenteEsternoTelefono;
	}

	public void setReferenteEsternoTelefono(String referenteEsternoTelefono) {
		this.referenteEsternoTelefono = referenteEsternoTelefono;
	}

	public String getReferenteScuolaTelefono() {
		return referenteScuolaTelefono;
	}

	public void setReferenteScuolaTelefono(String referenteScuolaTelefono) {
		this.referenteScuolaTelefono = referenteScuolaTelefono;
	}

	public List<OffertaIstitutoStub> getIstitutiAssociati() {
		return istitutiAssociati;
	}

	public void setIstitutiAssociati(List<OffertaIstitutoStub> istitutiAssociati) {
		this.istitutiAssociati = istitutiAssociati;
	}

}
