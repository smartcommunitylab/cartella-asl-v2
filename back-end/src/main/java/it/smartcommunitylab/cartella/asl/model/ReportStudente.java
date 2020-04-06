package it.smartcommunitylab.cartella.asl.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "report_studente")
public class ReportStudente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String istitutoId;
	private String studenteId;
	private String nominativoStudente;
	private String classeStudente;
	private String annoScolastico;
	private String corsoDiStudioId;
	private int competenzeAcquisite;
	private int oreSvolte3Anno;
	private int oreSvolte4Anno;
	private int oreSvolte5Anno;
	private int attivitaSvolte3Anno;
	private int attivitaSvolte4Anno;
	private int attivitaSvolte5Anno;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIstitutoId() {
		return istitutoId;
	}

	public void setIstitutoId(String istitutoId) {
		this.istitutoId = istitutoId;
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

	public String getAnnoScolastico() {
		return annoScolastico;
	}

	public void setAnnoScolastico(String annoScolastico) {
		this.annoScolastico = annoScolastico;
	}

	public String getCorsoDiStudioId() {
		return corsoDiStudioId;
	}

	public void setCorsoDiStudioId(String corsoDiStudioId) {
		this.corsoDiStudioId = corsoDiStudioId;
	}

	public int getCompetenzeAcquisite() {
		return competenzeAcquisite;
	}

	public void setCompetenzeAcquisite(int competenzeAcquisite) {
		this.competenzeAcquisite = competenzeAcquisite;
	}

	public int getOreSvolte3Anno() {
		return oreSvolte3Anno;
	}

	public void setOreSvolte3Anno(int oreSvolte3Anno) {
		this.oreSvolte3Anno = oreSvolte3Anno;
	}

	public int getOreSvolte4Anno() {
		return oreSvolte4Anno;
	}

	public void setOreSvolte4Anno(int oreSvolte4Anno) {
		this.oreSvolte4Anno = oreSvolte4Anno;
	}

	public int getOreSvolte5Anno() {
		return oreSvolte5Anno;
	}

	public void setOreSvolte5Anno(int oreSvolte5Anno) {
		this.oreSvolte5Anno = oreSvolte5Anno;
	}

	public int getAttivitaSvolte3Anno() {
		return attivitaSvolte3Anno;
	}

	public void setAttivitaSvolte3Anno(int attivitaSvolte3Anno) {
		this.attivitaSvolte3Anno = attivitaSvolte3Anno;
	}

	public int getAttivitaSvolte4Anno() {
		return attivitaSvolte4Anno;
	}

	public void setAttivitaSvolte4Anno(int attivitaSvolte4Anno) {
		this.attivitaSvolte4Anno = attivitaSvolte4Anno;
	}

	public int getAttivitaSvolte5Anno() {
		return attivitaSvolte5Anno;
	}

	public void setAttivitaSvolte5Anno(int attivitaSvolte5Anno) {
		this.attivitaSvolte5Anno = attivitaSvolte5Anno;
	}

}
