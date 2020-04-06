package it.smartcommunitylab.cartella.asl.model.eccezioni;

public class DettaglioEccezione {

	private String studenteId;
	private String studente;
	private String classe;
	private Long esperienzaSvoltaId;

	public DettaglioEccezione(String studenteId, String studente, String classe, Long esperienzaSvoltaId) {
		super();
		this.studenteId = studenteId;
		this.studente = studente;
		this.classe = classe;
		this.esperienzaSvoltaId = esperienzaSvoltaId;
	}

	public String getStudenteId() {
		return studenteId;
	}

	public void setStudenteId(String studenteId) {
		this.studenteId = studenteId;
	}

	public String getStudente() {
		return studente;
	}

	public void setStudente(String studente) {
		this.studente = studente;
	}

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

	public Long getEsperienzaSvoltaId() {
		return esperienzaSvoltaId;
	}

	public void setEsperienzaSvoltaId(Long esperienzaSvoltaId) {
		this.esperienzaSvoltaId = esperienzaSvoltaId;
	}

}
