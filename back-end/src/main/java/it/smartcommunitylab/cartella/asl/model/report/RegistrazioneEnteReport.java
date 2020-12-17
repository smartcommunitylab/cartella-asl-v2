package it.smartcommunitylab.cartella.asl.model.report;

import java.time.LocalDate;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import it.smartcommunitylab.cartella.asl.beans.LocalDateDeserializer;
import it.smartcommunitylab.cartella.asl.model.RegistrazioneEnte;
import it.smartcommunitylab.cartella.asl.model.RegistrazioneEnte.Stato;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;

public class RegistrazioneEnteReport {
	
	private Long id;
	
	private String aziendaId;
	
	private Long userId;
	private String cf;
	private String email;
	private String name;
	private String surname;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataInvito;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataAccettazione;
	
	private String nominativoInvito;
	
	private String token;
	
	@Enumerated(EnumType.STRING)
	private Stato stato;
	
	@Enumerated(EnumType.STRING)
	private ASLRole role;
	
	public RegistrazioneEnteReport() {}
	
	public RegistrazioneEnteReport(RegistrazioneEnte reg, ASLUser user) {
		this.userId = user.getId();
		this.cf = user.getCf();
		this.email = user.getEmail();
		this.name = user.getName();
		this.surname = user.getSurname();
		this.dataInvito = reg.getDataInvito();
		this.dataAccettazione = reg.getDataAccettazione();
		this.nominativoInvito = reg.getNominativoInvito();
		this.token = reg.getToken();
		this.stato = reg.getStato();
		this.role = reg.getRole();
	}

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

	public String getCf() {
		return cf;
	}

	public void setCf(String cf) {
		this.cf = cf;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
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

	public String getNominativoInvito() {
		return nominativoInvito;
	}

	public void setNominativoInvito(String nominativoInvito) {
		this.nominativoInvito = nominativoInvito;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Stato getStato() {
		return stato;
	}

	public void setStato(Stato stato) {
		this.stato = stato;
	}

	public ASLRole getRole() {
		return role;
	}

	public void setRole(ASLRole role) {
		this.role = role;
	}

}
