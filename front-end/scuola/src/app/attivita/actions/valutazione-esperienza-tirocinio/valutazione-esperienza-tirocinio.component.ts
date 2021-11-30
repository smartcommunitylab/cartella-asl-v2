import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DataService } from '../../../core/services/data.service';

@Component({
  selector: 'valutazione-esperienza-tirocinio-selector',
  templateUrl: './valutazione-esperienza-tirocinio.component.html',
  styleUrls: ['./valutazione-esperienza-tirocinio.component.scss']
})
export class ValutazioneEsperienzaTirocinioComponent implements OnInit {

  attivita;
  esperienze;
  valutazionEsperienza;
  domande;
  menuContent = "In questa pagina trovi tutte le informazioni relative all’attività che stai visualizzando. Utilizza i tasti blu per modificare ciascuna sezione.";
  showContent: boolean = false;

  valutazioni = [
    { titolo: 'Moltissimo', punteggio: 5 },
    { titolo: 'Molto', punteggio: 4 },
    { titolo: 'Abbastanze', punteggio: 3 },
    { titolo: 'Poco', punteggio: 2 },
    { titolo: 'Per nulla', punteggio: 1 },
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
      title: "Valutazione esperienza",
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
        this.dataService.getAttivitaValutazione(this.esperienze[0].esperienzaSvoltaId).subscribe((valutazione) => {
          this.valutazionEsperienza = valutazione;
          this.domande = valutazione.valutazioni;
        });
      });
    });

  }

  menuContentShow() {
    this.showContent = !this.showContent;
  }

  setLabel(punteggio) {
    let rtn = this.valutazioni.find(data => data.punteggio == punteggio);
    if (rtn) return rtn.titolo + ' / ' + punteggio;
    return '-';
  }

  setRisposta(risposta) {
    if (risposta) {
      return risposta
    }
    return '-';
  }

}
