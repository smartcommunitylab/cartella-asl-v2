package it.smartcommunitylab.cartella.asl.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "documento",
	indexes = { @Index(name = "risorsaId_idx", columnList = "risorsaId", unique = false) })
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Documento {
	
	public static enum TipoDoc {
		piano_formativo, convenzione, valutazione_studente, valutazione_ente, doc_generico, old_doc
	};

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String uuid;
	private String nomeFile;
	private LocalDate dataUpload;
	private String formatoDocumento;
	private String risorsaId;
	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "varchar(255) default 'old_doc'")
	private TipoDoc tipo;
	
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

	public TipoDoc getTipo() {
		return tipo;
	}

	public void setTipo(TipoDoc tipo) {
		this.tipo = tipo;
	}

}
