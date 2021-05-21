package it.smartcommunitylab.cartella.asl.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
//@Indexed
@Table(name = "corso_di_studio")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CorsoDiStudio {

	@Id
	// @GeneratedValue(strategy = GenerationType.IDENTITY)
	private String offertaId;

	private String nome;

	private String courseId;

	private String istitutoId;

	private String annoScolastico;

	private String extId;
	private String origin;

	// TODO
	private int oreAlternanza = 200;

	@Transient
	private Boolean corsoSperimentale;


	public String getOffertaId() {
		return offertaId;
	}

	public void setOffertaId(String offertaId) {
		this.offertaId = offertaId;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getIstitutoId() {
		return istitutoId;
	}

	public void setIstitutoId(String istitutoId) {
		this.istitutoId = istitutoId;
	}

	public String getAnnoScolastico() {
		return annoScolastico;
	}

	public void setAnnoScolastico(String annoScolastico) {
		this.annoScolastico = annoScolastico;
	}

	public int getOreAlternanza() {
		return oreAlternanza;
	}

	public void setOreAlternanza(int oreAlternanza) {
		this.oreAlternanza = oreAlternanza;
	}

	public String getExtId() {
		return extId;
	}

	public void setExtId(String extId) {
		this.extId = extId;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public Boolean getCorsoSperimentale() {
		return corsoSperimentale;
	}

	public void setCorsoSperimentale(Boolean corsoSperimentale) {
		this.corsoSperimentale = corsoSperimentale;
	}

}
