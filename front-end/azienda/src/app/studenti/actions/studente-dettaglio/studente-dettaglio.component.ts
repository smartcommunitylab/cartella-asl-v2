import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../../../core/services/data.service';
import { Router, ActivatedRoute } from '@angular/router';
import { environment } from '../../../../../src/environments/environment';
import * as moment from 'moment';

@Component({
  selector: 'cm-studente-dettaglio',
  templateUrl: './studente-dettaglio.component.html',
  styleUrls: ['./studente-dettaglio.component.scss']
})
export class StudenteDettaglioComponent implements OnInit {
  istituto: any;
  studente;
  attivitaAAs;
  competenze;
  navTitle: string = "Dettaglio studente";
  tipologie;
  percentage;
  showContent: boolean = false;
  menuContent = 'In questa pagina trovi tutte le informazioni riguardanti uno studente in particolare. Se vuoi vedere il dettaglio di una delle sue attività, clicca sulla riga corrispondente. Per gestire le presenze dell’attività, clicca sul tasto blu della riga corrispondente.';
  stati = [{ "name": "In attesa", "value": "in_attesa" }, { "name": "In corso", "value": "in_corso" }, { "name": "Revisione", "value": "revisione" }, { "name": "Archiviata", "value": "archiviata" }];  individuale: boolean;
  timeoutTooltip = 250;
  env = environment;

  breadcrumbItems = [
    {
      title: "Lista studenti",
      location: "../../",
    },
    {
      title: "Dettaglio studente"
    }
  ];

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private dataService: DataService) { }


  ngOnInit() {

    this.route.params.subscribe(params => {
      let id = params['id'];
      this.dataService.getStudenteDettaglio(id).subscribe((res) => {
        this.studente = res.studente;
        this.attivitaAAs = res.attivitaList;
        this.istituto = res.istituto;
        this.breadcrumbItems[1].title = this.studente.name + ' ' + this.studente.surname;

        this.dataService.getAttivitaTipologie().subscribe((res) => {
          this.tipologie = res;
          for (let aa of this.attivitaAAs) {
            this.tipologie.filter(tipo => {
              if (tipo.id == aa.tipologia) {
                aa.individuale = tipo.individuale;
              }
            })
          }
          
        });
      },
        (err: any) => console.log(err),
        () => console.log('getAttivita'));
    });

  }

  getTipologia(tipologiaId) {
    if (this.tipologie) {
      return this.tipologie.find(data => data.id == tipologiaId);
    } else {
      return tipologiaId;
    }
  }

  menuContentShow() {
    this.showContent = !this.showContent;
  }

  showTipStatoRiga(ev, aa, tp) {
    if (!aa.toolTipoStatoRiga) {
      if (aa.stato == 'archiviata') {
        this.setTipStatoRiga(aa);
      } else if (aa.stato == 'revisione') {
        let labelRevisione = 'In questa attività ci sono ancora ore da validare!';
        aa.toolTipoStatoRiga = labelRevisione;
      } else if (aa.stato == 'in_attesa') {
        aa.toolTipoStatoRiga = 'L’attività non è ancora iniziata.';
      }
    }
  }

  hideTipStatoRiga(ev, aa, tp) {
    aa.startTimeoutTipStato = false;
    aa.fetchingToolTipRiga = false;
  }

  setTipStatoRiga(aa) {
    var dateArchivazione = new Date(aa.dataArchiviazione);
    let labelArchiviata = 'Questa attività si è conclusa il gg/mm/aaaa';
    labelArchiviata = labelArchiviata.replace("gg/mm/aaaa", dateArchivazione.getDate() + '/' + (dateArchivazione.getMonth() + 1) + '/' + dateArchivazione.getFullYear());
    aa.toolTipoStatoRiga = labelArchiviata;
  }

  showTipRiga(ev, aa, tp) {
    console.log(ev.target.title);
    if (!aa.toolTipRiga) {
      aa.toolTipRiga = this.setTipRiga(aa);
    }
  }

  setTipRiga(aa) {
    let tip = '';
    for (let i = 0; i < aa.studenti.length; i++) {
      tip = tip + aa.studenti[i] + '\n';
      if (i == 15) {
        break;
      }
    }
    return tip;
  }

  setOreSvolteLabel(aa) {
    let label = aa.oreSvolte +'/' + aa.ore;    
    return label;
  }

  showTipButton(ev, aa, tp) {
    if (!aa.toolTipButton) {
      if (!aa.report) {
        aa.startTimeoutTipButton = true;
        setTimeout(() => {
          if (aa.startTimeoutTipButton == true) {
            this.env.globalSpinner = false;
            aa.fetchingToolTipButton = true;
            this.dataService.getAttivitaReportStudenti(aa.id).subscribe((report) => {
              aa.report = report;
              this.setTipButton(aa);
              aa.fetchingToolTipButton = false;
              this.env.globalSpinner = true;
              if (aa.startTimeoutTipButton == true) {
                tp.ngbTooltip = aa.toolTipButton;
                tp.open();
              }
              aa.startTimeoutTipButton = false;
            });
          }
        }, this.timeoutTooltip);
      } else {
        this.setTipButton(aa);
      }
    }
  }

  setTipButton(aa) {
    aa.toolTipButton = 'Ci sono *nO* ore da validare per *nS* studenti'
    aa.toolTipButton = aa.toolTipButton.replace("*nO*", aa.report.numeroOreDaValidare);
    aa.toolTipButton = aa.toolTipButton.replace("*nS*", aa.report.numeroStudentiDaValidare);
  }

  hideTipButton(ev, aa, tp) {
    aa.startTimeoutTipButton = false;
    aa.fetchingToolTipButton = false;
  }

  onUnovering(event) {
    console.log(event);
  }

  openDetail(aa) {
    this.router.navigateByUrl('/attivita/detail/' + aa.id);
  }
  
  gestionePresenze(aa) {
    this.tipologie.filter(tipo => {
      if (tipo.id == aa.tipologia) {
        aa.individuale = tipo.individuale;
      }
    })
    if (aa.individuale) {
      this.router.navigateByUrl('/attivita/detail/' + aa.id + '/modifica/studenti/presenze/individuale');
    } else {
      // this.router.navigateByUrl('/attivita/detail/' + aa.id + '/modifica/studenti/presenze/gruppo');
      this.router.navigateByUrl('/attivita/detail/' + aa.id);
    }
  }

  getStatoNome(statoValue) {
    if (this.stati) {
      let rtn = this.stati.find(data => data.value == statoValue);
      if (rtn) return rtn.name;
      return statoValue;
    }
  }

  annoScolastico(aa) {
    return this.dataService.getAnnoScolstico(moment(aa.dataInizio));
  }

  getEsperienzeStudenteCsv() {
    this.dataService.getEnteStudenteCsv(this.studente.id).subscribe((doc) => {
      const downloadLink = document.createElement("a");
      downloadLink.href = doc.url;
      downloadLink.download = doc.filename;
      document.body.appendChild(downloadLink);
      downloadLink.click();
      document.body.removeChild(downloadLink);  
    });
  }

}