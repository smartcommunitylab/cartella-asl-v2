import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { LocalizedDatePipe } from '../../../shared/pipes/localizedDatePipe';
import { DataService } from '../../../core/services/data.service';

@Component({
  selector: 'valutazione-competenze-tirocinio-selector',
  templateUrl: './valutazione-competenze-tirocinio.component.html',
  styleUrls: ['./valutazione-competenze-tirocinio.component.scss']
})
export class ValutazioneCompetenzeTirocinioComponent implements OnInit {
  attivita;
  esperienze;
  valutazioneCompetenze;
  menuContent = "In questa sezione trovi i risultati della valutazione che l’ente ha compiuto dell’esperienza svolta dall’alunno/a presso la sua sede. L’ente ha valutato lo stato dell’acquisizione delle competenze associate all’attività.";
  showContent: boolean = false;
  competenze;
  competenzeTotale = 0;
  competenzeValutate = 0;
  competenzeAcquisite = 0;
  percentage;

  valutazioni = [
    { titolo: 'Avanzato', punteggio: 4 },
    { titolo: 'Intermedio', punteggio: 3 },
    { titolo: 'Base', punteggio: 2 },
    { titolo: 'Non acquisita', punteggio: 1 },
    { titolo: '-', punteggio: 0 }
  ];

  breadcrumbItems = [
    {
      title: "Lista attività",
      location: "../../../",
    },
    {
      title: "Dettaglio attività",
      location: "../../",
    },
    {
      title: "Valutazione competenze",
    }
  ];

  constructor(
    private route: ActivatedRoute,
    private dataService: DataService,
    private localizedDatePipe: LocalizedDatePipe) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      let id = params['id'];
      this.dataService.getAttivita(id).subscribe((res) => {
        this.attivita = res.attivitaAlternanza;
        this.esperienze = res.esperienze;
        this.dataService.getValutazioneCompetenze(this.esperienze[0].esperienzaSvoltaId).subscribe((valutazione) => {
          this.valutazioneCompetenze = valutazione;
          this.percentage = ((this.valutazioneCompetenze.oreInserite / this.valutazioneCompetenze.ore) * 100).toFixed(0);
          this.competenze = valutazione.valutazioni;
          this.competenze.forEach(d=> {
            this.competenzeTotale++;
            if (d.punteggio > 0) {
              this.competenzeValutate++;
            }
            if (d.punteggio > 1) {
              this.competenzeAcquisite++;
            }
          })
        });
      });
    });
  }

  menuContentShow() {
    this.showContent = !this.showContent;
  }

  setLabel(punteggio) {
    let titolo = '-'
    let rtn = this.valutazioni.find(data => data.punteggio == punteggio);
    if (rtn) titolo = rtn.titolo;
    return titolo;
  }

  styleLabel(punteggio) {
    var style = {};
    if (punteggio > 1) {
      style['font-weight'] = 600;      
    }
    return style;
  }

  initCounter() {
    this.competenzeTotale = 0;
    this.competenzeValutate = 0;
    this.competenzeAcquisite = 0;
  }

  setOreInserite() {
    var label = '';
    if (this.valutazioneCompetenze)
        label = this.valutazioneCompetenze.oreInserite + "/" + this.valutazioneCompetenze.ore + " (" + this.percentage + "%)";
    return label;
  }

  setDate(val) {
    let date = '-';
    if (val) {
      date = this.localizedDatePipe.transform(val, 'dd/MM/yyyy')
    }
    return date;
  }

}
