package it.smartcommunitylab.cartella.asl.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "registrazione_docente", indexes = { 
		@Index(name = "istitutoId_idx", columnList = "istitutoId", unique = false),
		@Index(name = "emailDocente_idx", columnList = "emailDocente", unique = false)
  }
)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RegistrazioneDocente {
  @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

  private String istitutoId;

  private Long userId;

  private Long ownerId;

  private String emailDocente;	
	private String nominativoDocente;
  private String cfDocente;

  @Transient
  private String classi;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getIstitutoId() {
    return istitutoId;
  }

  public void setIstitutoId(String istitutoId) {
    this.istitutoId = istitutoId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
  }

  public String getEmailDocente() {
    return emailDocente;
  }

  public void setEmailDocente(String emailDocente) {
    this.emailDocente = emailDocente;
  }

  public String getNominativoDocente() {
    return nominativoDocente;
  }

  public void setNominativoDocente(String nominativoDocente) {
    this.nominativoDocente = nominativoDocente;
  }

  public String getCfDocente() {
    return cfDocente;
  }

  public void setCfDocente(String cfDocente) {
    this.cfDocente = cfDocente;
  }

  public String getClassi() {
    return classi;
  }

  public void setClassi(String classi) {
    this.classi = classi;
  }


  
}
