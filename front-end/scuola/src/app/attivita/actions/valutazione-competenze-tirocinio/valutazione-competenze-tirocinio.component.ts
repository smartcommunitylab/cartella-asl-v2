import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
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
  menuContent = "In questa pagina trovi tutte le informazioni relative all’attività che stai visualizzando. Utilizza i tasti blu per modificare ciascuna sezione.";
  showContent: boolean = false;

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
    private dataService: DataService) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      let id = params['id'];
      this.dataService.getAttivita(id).subscribe((res) => {
        this.attivita = res.attivitaAlternanza;
        this.esperienze = res.esperienze;
        this.dataService.getValutazioneCompetenze(this.esperienze[0].esperienzaSvoltaId).subscribe((res) => {
          this.valutazioneCompetenze = res.valutazioni;
        });
      });
    });
    // this.valutazioneCompetenze = [];
    // this.valutazioneCompetenze[0] = {
    //   id: 1,
    //   competenzaTitolo: 'Valutare le caratteristiche contestuali dell’ambiente per svolgere le proprie mansioni in maniera corretta e non fuori luogo ',
    //   punteggio: 2,
    //   competenzaOwnerName: 'ISFOL'
    // }

    // this.valutazioneCompetenze[1] = {
    //   id: 2,
    //   competenzaTitolo: 'Valutare le caratteristiche contestuali dell’ambiente per svolgere le proprie mansioni in maniera corretta e non fuori luogo',
    //   punteggio: 4,
    //   competenzaOwnerName: 'ISTITUTO'
    // }

    // this.valutazioneCompetenze[2] = {
    //   id: 3,
    //   competenzaTitolo: 'Valutare le caratteristiche contestuali dell’ambiente per svolgere le proprie mansioni in maniera corretta e non fuori luogo',
    //   punteggio: 3,
    //   competenzaOwnerName: 'ESCO'
    // }

    // this.valutazioneCompetenze[3] = {
    //   id: 4,
    //   competenzaTitolo: 'Valutare le caratteristiche contestuali dell’ambiente per svolgere le proprie mansioni in maniera corretta e non fuori luogo. Valutare le caratteristiche contestuali dell’ambiente per svolgere le proprie mansioni in maniera corretta e non fuori luogo',
    //   risposta: 2,
    //   competenzaOwnerName: 'ESCO'
    // }

    // this.valutazioneCompetenze[4] = {
    //   id: 5,
    //   competenzaTitolo: 'Valutare le caratteristiche contestuali dell’ambiente per svolgere le proprie mansioni in maniera corretta e non fuori luogo',
    //   punteggio: 1,
    //   competenzaOwnerName: 'ISFOL'
    // }

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

}
