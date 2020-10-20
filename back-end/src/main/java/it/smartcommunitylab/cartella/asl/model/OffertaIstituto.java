package it.smartcommunitylab.cartella.asl.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "offerta_istituto", indexes = { 
		@Index(name = "offertaId_idx", columnList = "offertaId", unique = false),
		@Index(name = "istitutoId_idx", columnList = "istitutoId", unique = false) 
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OffertaIstituto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long offertaId;
	private String istitutoId;
	private String nomeIstituto;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getOffertaId() {
		return offertaId;
	}
	public void setOffertaId(Long offertaId) {
		this.offertaId = offertaId;
	}
	public String getIstitutoId() {
		return istitutoId;
	}
	public void setIstitutoId(String istitutoId) {
		this.istitutoId = istitutoId;
	}
	public String getNomeIstituto() {
		return nomeIstituto;
	}
	public void setNomeIstituto(String nomeIstituto) {
		this.nomeIstituto = nomeIstituto;
	}

}
