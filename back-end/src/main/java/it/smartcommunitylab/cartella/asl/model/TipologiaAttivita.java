package it.smartcommunitylab.cartella.asl.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "tipologia_attivita",
	indexes = { @Index(name = "pianoAlternanzaId_idx", columnList = "pianoAlternanzaId", unique = false) })
public class TipologiaAttivita {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String uuid;
	private String istitutoId;
	private long pianoAlternanzaId;
	private String titolo;
	private int tipologia;
	private int annoRiferimento;
	private int monteOre;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public int getTipologia() {
		return tipologia;
	}

	public void setTipologia(int tipologia) {
		this.tipologia = tipologia;
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

	public long getPianoAlternanzaId() {
		return pianoAlternanzaId;
	}

	public void setPianoAlternanzaId(long pianoAlternanzaId) {
		this.pianoAlternanzaId = pianoAlternanzaId;
	}

	public int getAnnoRiferimento() {
		return annoRiferimento;
	}

	public void setAnnoRiferimento(int annoRiferimento) {
		this.annoRiferimento = annoRiferimento;
	}

	public int getMonteOre() {
		return monteOre;
	}

	public void setMonteOre(int monteOre) {
		this.monteOre = monteOre;
	}

}
