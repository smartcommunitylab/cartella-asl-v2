package it.smartcommunitylab.cartella.asl.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "studente", indexes = { @Index(name = "cf_idx", columnList = "cf", unique = false) })
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Studente implements Comparable<Studente> {

	@Id
	private String id;
	private String name;
	private String surname;
	private String birthdate;
	private String cf;

	@Transient
	private String classroom;
	@Transient
	private int annoCorso;
	@Transient
	private String annoScolastico;
	@Transient
	private String istitutoId;

    @Transient
    private CorsoDiStudioBean corsoDiStudio;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCf() {
		return cf;
	}

	public void setCf(String cf) {
		this.cf = cf;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getClassroom() {
		return classroom;
	}

	public void setClassroom(String classroom) {
		this.classroom = classroom;
	}

	public int getAnnoCorso() {
		return annoCorso;
	}

	public void setAnnoCorso(int annoCorso) {
		this.annoCorso = annoCorso;
	}

	public String getIstitutoId() {
		return istitutoId;
	}

	public void setIstitutoId(String istitutoId) {
		this.istitutoId = istitutoId;
	}
	
	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public int compareTo(Studente o) {
		return (surname + " " + name).compareTo(o.surname + " " + o.name);
	}

    public CorsoDiStudioBean getCorsoDiStudio() {
        return corsoDiStudio;
    }

    public void setCorsoDiStudio(CorsoDiStudioBean corsoDiStudio) {
        this.corsoDiStudio = corsoDiStudio;
    }

		public String getAnnoScolastico() {
			return annoScolastico;
		}

		public void setAnnoScolastico(String annoScolastico) {
			this.annoScolastico = annoScolastico;
		}

}
