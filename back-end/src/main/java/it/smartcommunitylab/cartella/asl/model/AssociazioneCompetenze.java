package it.smartcommunitylab.cartella.asl.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "associazione_competenze",
	indexes = { @Index(name = "risorsaId_idx", columnList = "risorsaId", unique = false) })
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AssociazioneCompetenze {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long competenzaId;
	private String risorsaId;
	private int ordine;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCompetenzaId() {
		return competenzaId;
	}

	public void setCompetenzaId(Long competenzaId) {
		this.competenzaId = competenzaId;
	}

	public String getRisorsaId() {
		return risorsaId;
	}

	public void setRisorsaId(String risorsaId) {
		this.risorsaId = risorsaId;
	}

	public int getOrdine() {
		return ordine;
	}

	public void setOrdine(int ordine) {
		this.ordine = ordine;
	}

}
