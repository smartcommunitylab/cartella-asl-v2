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
@Table(name = "associazione_docenti_classi", indexes = { 
		@Index(name = "registrazioneDocenteId_idx", columnList = "registrazioneDocenteId", unique = false)
  }
)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AssociazioneDocentiClassi {
  @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

  private Long registrazioneDocenteId;
  private String classe;
  private String annoScolastico;

  @Transient  
  private Long studenti;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getRegistrazioneDocenteId() {
    return registrazioneDocenteId;
  }

  public void setRegistrazioneDocenteId(Long registrazioneDocenteId) {
    this.registrazioneDocenteId = registrazioneDocenteId;
  }

  public String getClasse() {
    return classe;
  }

  public void setClasse(String classe) {
    this.classe = classe;
  }

  public String getAnnoScolastico() {
    return annoScolastico;
  }

  public void setAnnoScolastico(String annoScolastico) {
    this.annoScolastico = annoScolastico;
  }

  public Long getStudenti() {
    return studenti;
  }

  public void setStudenti(Long studenti) {
    this.studenti = studenti;
  }

}

