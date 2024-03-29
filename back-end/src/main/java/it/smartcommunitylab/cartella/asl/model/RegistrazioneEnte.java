package it.smartcommunitylab.cartella.asl.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import it.smartcommunitylab.cartella.asl.beans.LocalDateDeserializer;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;

@Entity
@Table(name = "registrazione_ente", indexes = { 
		@Index(name = "aziendaId_idx", columnList = "aziendaId", unique = false),
		@Index(name = "token_idx", columnList = "token", unique = false)})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RegistrazioneEnte {
	
	public static enum Stato {
		inattivo, inviato, confermato
	};

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String aziendaId;
	
	private String istitutoId;
	
	private Long userId;
	
	private Long ownerId;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataInvito;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataAccettazione;
	
	private String email;
	
	private String cf;
	
	private String nominativoInvito;
	
	private String nomeReferente;
	
	private String cognomeReferente;
	
	private String telefonoReferente;
	
	private String nomeIstituto;
	
	private String nomeEnte;
	
	private String token;
	
	@Enumerated(EnumType.STRING)
	private Stato stato;
	
	@Enumerated(EnumType.STRING)
	private ASLRole role;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAziendaId() {
		return aziendaId;
	}

	public void setAziendaId(String aziendaId) {
		this.aziendaId = aziendaId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public LocalDate getDataInvito() {
		return dataInvito;
	}

	public void setDataInvito(LocalDate dataInvito) {
		this.dataInvito = dataInvito;
	}

	public LocalDate getDataAccettazione() {
		return dataAccettazione;
	}

	public void setDataAccettazione(LocalDate dataAccettazione) {
		this.dataAccettazione = dataAccettazione;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNominativoInvito() {
		return nominativoInvito;
	}

	public void setNominativoInvito(String nominativoInvito) {
		this.nominativoInvito = nominativoInvito;
	}

	public Stato getStato() {
		return stato;
	}

	public void setStato(Stato stato) {
		this.stato = stato;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public ASLRole getRole() {
		return role;
	}

	public void setRole(ASLRole role) {
		this.role = role;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public String getIstitutoId() {
		return istitutoId;
	}

	public void setIstitutoId(String istitutoId) {
		this.istitutoId = istitutoId;
	}

	public String getNomeIstituto() {
		return nomeIstituto;
	}

	public void setNomeIstituto(String nomeIstituto) {
		this.nomeIstituto = nomeIstituto;
	}

	public String getNomeEnte() {
		return nomeEnte;
	}

	public void setNomeEnte(String nomeEnte) {
		this.nomeEnte = nomeEnte;
	}

	public String getCf() {
		return cf;
	}

	public void setCf(String cf) {
		this.cf = cf;
	}

	public String getNomeReferente() {
		return nomeReferente;
	}

	public void setNomeReferente(String nomeReferente) {
		this.nomeReferente = nomeReferente;
	}

	public String getCognomeReferente() {
		return cognomeReferente;
	}

	public void setCognomeReferente(String cognomeReferente) {
		this.cognomeReferente = cognomeReferente;
	}

	public String getTelefonoReferente() {
		return telefonoReferente;
	}

	public void setTelefonoReferente(String telefonoReferente) {
		this.telefonoReferente = telefonoReferente;
	}

}
