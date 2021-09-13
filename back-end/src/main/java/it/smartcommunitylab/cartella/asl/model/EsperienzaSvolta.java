package it.smartcommunitylab.cartella.asl.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "esperienza_svolta", indexes = { 
		@Index(name = "attivitaAlternanzaId_idx", columnList = "attivitaAlternanzaId", unique = false),
		@Index(name = "studenteId_idx", columnList = "studenteId", unique = false)})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EsperienzaSvolta {

	public static enum Stati {
		valida, annullata, da_definire
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String uuid;
	private String istitutoId;
	private Long attivitaAlternanzaId;
	private String studenteId;
	private String nominativoStudente;
	private String cfStudente;
	private String classeStudente;
	private String registrazioneId;
	@Enumerated(EnumType.STRING)
	private Stati stato;
	private String codiceMiur;
	private int oreRendicontate;
	
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

	public Long getAttivitaAlternanzaId() {
		return attivitaAlternanzaId;
	}

	public void setAttivitaAlternanzaId(Long attivitaAlternanzaId) {
		this.attivitaAlternanzaId = attivitaAlternanzaId;
	}

	public String getStudenteId() {
		return studenteId;
	}

	public void setStudenteId(String studenteId) {
		this.studenteId = studenteId;
	}

	public String getNominativoStudente() {
		return nominativoStudente;
	}

	public void setNominativoStudente(String nominativoStudente) {
		this.nominativoStudente = nominativoStudente;
	}

	public String getClasseStudente() {
		return classeStudente;
	}

	public void setClasseStudente(String classeStudente) {
		this.classeStudente = classeStudente;
	}

	public String getRegistrazioneId() {
		return registrazioneId;
	}

	public void setRegistrazioneId(String registrazioneId) {
		this.registrazioneId = registrazioneId;
	}

	public Stati getStato() {
		return stato;
	}

	public void setStato(Stati stato) {
		this.stato = stato;
	}

	public String getCodiceMiur() {
		return codiceMiur;
	}

	public void setCodiceMiur(String codiceMiur) {
		this.codiceMiur = codiceMiur;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public String getCfStudente() {
		return cfStudente;
	}

	public void setCfStudente(String cfStudente) {
		this.cfStudente = cfStudente;
	}

	public int getOreRendicontate() {
		return oreRendicontate;
	}

	public void setOreRendicontate(int oreRendicontate) {
		this.oreRendicontate = oreRendicontate;
	}

}
