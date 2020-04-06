package it.smartcommunitylab.cartella.asl.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "documento",
	indexes = { @Index(name = "risorsaId_idx", columnList = "risorsaId", unique = false) })
public class Documento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String uuid;
	private String nomeFile;
	private LocalDate dataUpload;
	private String formatoDocumento;
	private String risorsaId;
	
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

	public String getNomeFile() {
		return nomeFile;
	}

	public void setNomeFile(String nomeFile) {
		this.nomeFile = nomeFile;
	}

	public LocalDate getDataUpload() {
		return dataUpload;
	}

	public void setDataUpload(LocalDate dataUpload) {
		this.dataUpload = dataUpload;
	}

	public String getFormatoDocumento() {
		return formatoDocumento;
	}

	public void setFormatoDocumento(String formatoDocumento) {
		this.formatoDocumento = formatoDocumento;
	}

	public String getRisorsaId() {
		return risorsaId;
	}

	public void setRisorsaId(String risorsaId) {
		this.risorsaId = risorsaId;
	}

}
