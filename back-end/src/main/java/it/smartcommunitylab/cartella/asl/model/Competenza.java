package it.smartcommunitylab.cartella.asl.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "competenza")
public class Competenza {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String classificationCode;
	private String uri;
	private String titolo;
	private String[] conoscenze;
	private String[] abilita;
	private int livelloEQF;
	private Boolean attiva;
	private String ownerId;
	private String ownerName;
	private String source; 

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getClassificationCode() {
		return classificationCode;
	}

	public void setClassificationCode(String classificationCode) {
		this.classificationCode = classificationCode;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public String[] getConoscenze() {
		return conoscenze;
	}

	public void setConoscenze(String[] conoscenze) {
		this.conoscenze = conoscenze;
	}

	public String[] getAbilita() {
		return abilita;
	}

	public void setAbilita(String[] abilita) {
		this.abilita = abilita;
	}

	public int getLivelloEQF() {
		return livelloEQF;
	}

	public void setLivelloEQF(int livelloEQF) {
		this.livelloEQF = livelloEQF;
	}

	public Boolean getAttiva() {
		return attiva;
	}

	public void setAttiva(Boolean attiva) {
		this.attiva = attiva;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

}
