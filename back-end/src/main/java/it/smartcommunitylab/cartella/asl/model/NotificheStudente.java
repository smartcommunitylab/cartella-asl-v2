package it.smartcommunitylab.cartella.asl.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "notifiche_studente", indexes = { @Index(name = "studenteId_idx", columnList = "studenteId", unique = false) })
public class NotificheStudente {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String studenteId;
	
	@Column(columnDefinition = "VARCHAR(1024)")
	private String registrationToken;
	
	private LocalDate dataAttivazione;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getStudenteId() {
		return studenteId;
	}
	public void setStudenteId(String studenteId) {
		this.studenteId = studenteId;
	}
	public String getRegistrationToken() {
		return registrationToken;
	}
	public void setRegistrationToken(String registrationToken) {
		this.registrationToken = registrationToken;
	}
	public LocalDate getDataAttivazione() {
		return dataAttivazione;
	}
	public void setDataAttivazione(LocalDate dataAttivazione) {
		this.dataAttivazione = dataAttivazione;
	}
}
