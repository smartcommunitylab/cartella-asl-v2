import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { LocalizedDatePipe } from '../../../shared/pipes/localizedDatePipe';
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
  menuContent = "In questa sezione trovi i risultati della valutazione che l’alunno/a ha fatto rispetto alla conguenza dell’esperienza svolta presso l’ente con il proprio percorso di studi.";
  showContent: boolean = false;
  domanteTotale = 0;
  domandeCompilati = 0;
  percentage;

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
    private dataService: DataService,
    private localizedDatePipe: LocalizedDatePipe) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      let id = params['id'];
      this.initCounter();
      this.dataService.getAttivita(id).subscribe((res) => {
        this.attivita = res.attivitaAlternanza;
        this.esperienze = res.esperienze;
        this.dataService.getAttivitaValutazione(this.esperienze[0].esperienzaSvoltaId).subscribe((valutazione) => {
          this.valutazionEsperienza = valutazione;
          this.percentage = ((this.valutazionEsperienza.oreInserite / this.valutazionEsperienza.ore) * 100).toFixed(0);
          this.domande = valutazione.valutazioni;
          this.domande.forEach(d=> {
            if (d.rispostaChiusa) {
              this.domanteTotale++;
            }
            if (d.punteggio > 0) {
              this.domandeCompilati++;
            }
          })
        });
      });
    });
  }

  initCounter() {
    this.domanteTotale = 0;
    this.domandeCompilati = 0;
  }

  setOreInserite() {
    var label = '';
    if (this.valutazionEsperienza)
       label = this.valutazionEsperienza.oreInserite + "/" + this.valutazionEsperienza.ore + " (" + this.percentage + "%)";
    return label;
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

  setDate(val) {
    let date = '-';
    if (val) {
      date = this.localizedDatePipe.transform(val, 'dd/MM/yyyy')
    }
    return date;
  }

  setMedia(val) {
    if (val == 'NaN' || !val || val == 'undefined' || val == 0) {
      return '-';
    } else {
      return val;
    }
  }

}
