package it.smartcommunitylab.cartella.asl.beans;

import it.smartcommunitylab.cartella.asl.model.OffertaIstituto;

public class OffertaIstitutoStub {
	private String istitutoId;
	private String nomeIstituto;
	
	public OffertaIstitutoStub() {};
	
	public OffertaIstitutoStub(OffertaIstituto o) {
		this.istitutoId = o.getIstitutoId();
		this.nomeIstituto = o.getNomeIstituto();
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
