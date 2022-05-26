import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../../../core/services/data.service';
import { Router, ActivatedRoute } from '@angular/router';
import { environment } from '../../../../../src/environments/environment';
import { AttivitaAlternanza } from '../../../shared/classes/AttivitaAlternanza.class';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap';
import { PaginationComponent } from '../../../shared/pagination/pagination.component';
import * as moment from 'moment';

@Component({
  selector: 'cm-istituto-dettaglio',
  templateUrl: './istituto-dettaglio.component.html',
  styleUrls: ['./istituto-dettaglio.component.scss']
})
export class IstitutoDettaglioComponent implements OnInit {
  istituto;
  filtro;
  filterSearch;
  attivitaAAs: AttivitaAlternanza[] = [];
  navTitle: string = "Dettaglio istituto";
  currentpage: number = 0;
  tipologie;
  totalRecords: number = 0;
  pageSize: number = 10;
  showContent: boolean = false;
  menuContent = 'In questa pagina trovi tutte le attività degli studenti di questo istituto, ed alcuni dati per metterti in contatto.';
  stati = [{ "name": "In attesa", "value": "in_attesa" }, { "name": "In corso", "value": "in_corso" }, { "name": "Revisionare", "value": "revisione" }, { "name": "Archiviata", "value": "archiviata" }];
  env = environment;
  timeoutTooltip = 250;
  @ViewChild('tooltip') tooltip: NgbTooltip;
  @ViewChild('cmPagination') private cmPagination: PaginationComponent;
  breadcrumbItems = [
    {
      title: "Lista istituti",
      location: "../../",
    },
    {
      title: "Dettaglio istituto"
    }
  ];

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private dataService: DataService) {
    this.filtro = {
      titolo: '',
      stato: undefined
    }
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      let id = params['id'];
      this.dataService.getistitutoDettaglio(id).subscribe((res) => {
        this.istituto = res;
        this.filtro.istitutoId = this.istituto.id;
        this.breadcrumbItems[1].title = this.istituto.name;

        this.dataService.getAttivitaTipologie().subscribe((res) => {
          this.tipologie = res;
          this.getAttivitaAltPage(1);
        });
      },
        (err: any) => console.log(err),
        () => console.log('getAttivita'));
    });

  }

  getAttivitaAltPage(page: number) {
    this.dataService.getAttivitaAlternanzaForEnteAPI(this.filtro, (page - 1), this.pageSize)
      .subscribe((response) => {
        this.totalRecords = response.totalElements;
        this.attivitaAAs = response.content;
        for (let aa of this.attivitaAAs) {
          this.tipologie.filter(tipo => {
            if (tipo.id == aa.tipologia) {
              aa.individuale = tipo.individuale;
            }
          })
        }
      }, (err: any) => console.log(err),
        () => console.log('getAttivitaAlternanzaForIstitutoAPI'));
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

  cerca() {
    this.cmPagination.changePage(1);
    this.filterSearch = true;
    this.getAttivitaAltPage(1);
  }

  selectStatoFilter() {
    this.cmPagination.changePage(1);
    this.filterSearch = true;
    this.getAttivitaAltPage(1);
  }

  pageChanged(page: number) {
    this.currentpage = page;
    this.getAttivitaAltPage(page);
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

  setAssegnatoLabel(aa) {
    let label = '';
    if (aa.studenti.length == 1) {
      label = aa.studenti[0];
    } else if (aa.studenti.length > 1) {
      label = aa.studenti.length + ' studenti';
    }
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

  refreshAttivita() {
    this.filtro.titolo = '';
    this.filtro.stato = undefined;
    this.filterSearch = false;
    this.getAttivitaAltPage(1);
  }

  annoScolastico(aa) {
    return this.dataService.getAnnoScolstico(moment(aa.dataInizio));
  }

  getEsperienzeistitutoCsv() {
    this.dataService.getEsperienzeistitutoCsv(this.istituto.id).subscribe((doc) => {
      const downloadLink = document.createElement("a");
      downloadLink.href = doc.url;
      downloadLink.download = doc.filename;
      document.body.appendChild(downloadLink);
      downloadLink.click();
      document.body.removeChild(downloadLink);  
    });
  }

  styleOptionConvenzione(conv) {
    var style = {};

    if (conv.stato == 'non_attiva') {
      style['color'] = '#F83E5A'; // red
    }
    return style;
  }

  downloadConvenzioneDoc(doc) {
    this.dataService.downloadDocumentConvenzioneBlob(doc).subscribe((url) => {
      const downloadLink = document.createElement("a");
      downloadLink.href = url;
      downloadLink.download = doc.nomeFile;
      document.body.appendChild(downloadLink);
      downloadLink.click();
      document.body.removeChild(downloadLink);
    });
  }

}