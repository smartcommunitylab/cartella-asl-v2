package it.smartcommunitylab.cartella.asl.model.report;

public class OreStudente {
  private long oreInterne = 0;
  private long oreEsterne = 0;
  private String studenteId;
  private String nominativo;
  
	public long getOreInterne() {
		return oreInterne;
	}
	public void setOreInterne(long oreInterne) {
		this.oreInterne = oreInterne;
	}
	public long getOreEsterne() {
		return oreEsterne;
	}
	public void setOreEsterne(long oreEsterne) {
		this.oreEsterne = oreEsterne;
	}
	public String getStudenteId() {
		return studenteId;
	}
	public void setStudenteId(String studenteId) {
		this.studenteId = studenteId;
	}
	public String getNominativo() {
		return nominativo;
	}
	public void setNominativo(String nominativo) {
		this.nominativo = nominativo;
	}
  
}
