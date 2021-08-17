package it.smartcommunitylab.cartella.asl.model.report;

import java.util.ArrayList;
import java.util.List;

import it.smartcommunitylab.cartella.asl.model.ProfessoriClassi;
import it.smartcommunitylab.cartella.asl.model.ReferenteAlternanza;

public class ReportDocenteClasse {
  private ReferenteAlternanza referenteAlternanza;
  private List<ProfessoriClassi> associazioneClassi = new ArrayList<>();
  private boolean attivo;

  public ReferenteAlternanza getReferenteAlternanza() {
    return referenteAlternanza;
  }
  public void setReferenteAlternanza(ReferenteAlternanza referenteAlternanza) {
    this.referenteAlternanza = referenteAlternanza;
  }
  public List<ProfessoriClassi> getAssociazioneClassi() {
    return associazioneClassi;
  }
  public void setAssociazioneClassi(List<ProfessoriClassi> associazioneClassi) {
    this.associazioneClassi = associazioneClassi;
  }
  
  public boolean isAttivo() {
    return attivo;
  }
  public void setAttivo(boolean attivo) {
    this.attivo = attivo;
  }

  
}
