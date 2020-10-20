package it.smartcommunitylab.cartella.asl.model.report;

import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;

public class ReportAttivitaAlternanzaStudentiEnte {
	private Long id;
	private String uuid;
	private int numeroStudentiDaValidare = 0;
	private int numeroOreDaValidare = 0;
	
	public ReportAttivitaAlternanzaStudentiEnte() {}
	
	public ReportAttivitaAlternanzaStudentiEnte(AttivitaAlternanza attivita) {
		this.setId(attivita.getId());
		this.setUuid(attivita.getUuid());
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


}
