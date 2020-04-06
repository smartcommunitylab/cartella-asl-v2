package it.smartcommunitylab.cartella.asl.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "esperienza_svolta_allineamento")
public class EsperienzaSvoltaAllineamento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long espSvoltaId;

	private Long ultimoAllineamento;

	private boolean allineato;

	private boolean daAllineare = true;

	@Column(columnDefinition="VARCHAR(1024)")
	private String errore;

	private int numeroTentativi;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getEspSvoltaId() {
		return espSvoltaId;
	}

	public void setEspSvoltaId(Long espSvoltaId) {
		this.espSvoltaId = espSvoltaId;
	}

	public Long getUltimoAllineamento() {
		return ultimoAllineamento;
	}

	public void setUltimoAllineamento(Long ultimoAllineamento) {
		this.ultimoAllineamento = ultimoAllineamento;
	}

	public boolean isAllineato() {
		return allineato;
	}

	public void setAllineato(boolean allineato) {
		this.allineato = allineato;
	}

	public boolean isDaAllineare() {
		return daAllineare;
	}

	public void setDaAllineare(boolean daAllineare) {
		this.daAllineare = daAllineare;
	}

	public String getErrore() {
		return errore;
	}

	public void setErrore(String errore) {
		this.errore = errore;
	}

	public int getNumeroTentativi() {
		return numeroTentativi;
	}

	public void setNumeroTentativi(int numeroTentativi) {
		this.numeroTentativi = numeroTentativi;
	}

}
