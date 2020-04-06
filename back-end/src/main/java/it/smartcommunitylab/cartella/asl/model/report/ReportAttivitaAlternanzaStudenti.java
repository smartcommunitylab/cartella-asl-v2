package it.smartcommunitylab.cartella.asl.model.report;

import java.util.ArrayList;
import java.util.List;

import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;

public class ReportAttivitaAlternanzaStudenti {
	private Long id;
	private String uuid;
	private String istitutoId;
	private List<String> studenti = new ArrayList<String>();
	private int numeroStudentiDaValidare;
	private int numeroOreDaValidare;
	private int numeroEsperienzeCompletate; 
	
	public ReportAttivitaAlternanzaStudenti() {}
	
	public ReportAttivitaAlternanzaStudenti(AttivitaAlternanza attivita) {
		this.setId(attivita.getId());
		this.setUuid(attivita.getUuid());
		this.setIstitutoId(attivita.getIstitutoId());
	}
	
	public void addStudenteDaValidare() {
		numeroStudentiDaValidare++;
	}
	
	public void addOreDaValidare(long ore) {
		numeroOreDaValidare += ore;
	}
	
	public void subOreDaValidare(long ore) {
		numeroOreDaValidare -= ore;
	}
	
	public void addEsperienzaCompletata() {
		numeroEsperienzeCompletate++;
	}
	
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

	public List<String> getStudenti() {
		return studenti;
	}

	public void setStudenti(List<String> studenti) {
		this.studenti = studenti;
	}

	public int getNumeroOreDaValidare() {
		return numeroOreDaValidare;
	}

	public void setNumeroOreDaValidare(int numeroOreDaValidare) {
		this.numeroOreDaValidare = numeroOreDaValidare;
	}

	public int getNumeroStudentiDaValidare() {
		return numeroStudentiDaValidare;
	}

	public void setNumeroStudentiDaValidare(int numeroStudentiDaValidare) {
		this.numeroStudentiDaValidare = numeroStudentiDaValidare;
	}

	public int getNumeroEsperienzeCompletate() {
		return numeroEsperienzeCompletate;
	}

	public void setNumeroEsperienzeCompletate(int numeroEsperienzeCompletate) {
		this.numeroEsperienzeCompletate = numeroEsperienzeCompletate;
	}



}
