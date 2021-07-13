package it.smartcommunitylab.cartella.asl.model.report;

public class DocentiClassiReport {
  private String corsoId;
  private String corso;
  private String annoScolastico;
  private String classe;
  private Long studenti;

  public String getCorsoId() {
    return corsoId;
  }
  public void setCorsoId(String corsoId) {
    this.corsoId = corsoId;
  }
  public String getCorso() {
    return corso;
  }
  public void setCorso(String corso) {
    this.corso = corso;
  }
  public String getAnnoScolastico() {
    return annoScolastico;
  }
  public void setAnnoScolastico(String annoScolastico) {
    this.annoScolastico = annoScolastico;
  }
  public String getClasse() {
    return classe;
  }
  public void setClasse(String classe) {
    this.classe = classe;
  }
  public Long getStudenti() {
    return studenti;
  }
  public void setStudenti(Long studenti) {
    this.studenti = studenti;
  }
  
}
