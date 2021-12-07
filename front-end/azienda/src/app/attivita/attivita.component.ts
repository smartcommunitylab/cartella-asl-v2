import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../core/services/data.service'
import { ActivatedRoute, Router } from '@angular/router';
import { PaginationComponent } from '../shared/pagination/pagination.component';
import { AttivitaAlternanza } from '../shared/classes/AttivitaAlternanza.class';
import { environment } from '../../environments/environment';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap';

declare var moment: any;
moment['locale']('it');

@Component({
    selector: 'attivita',
    templateUrl: './attivita.component.html',
    styleUrls: ['./attivita.component.scss']
})

export class AttivitaComponent implements OnInit {
    title: string;
    attivitaAAs: AttivitaAlternanza[] = [];
    filtro;
    closeResult: string;
    totalRecords: number = 0;
    pageSize: number = 10;
    currentpage: number = 0;
    tipologie;
    filterSearch = false;
    stato;
    menuContent = "In questa pagina trovi tutte le attività svolte presso il tuo ente. Puoi cercare un istituto, uno studente o un titolo attività, oppure puoi filtrare per stato. Con il tasto blu sulla destra puoi andare direttamente alla gestione presenze. Per visualizzare un’attività, clicca sulla riga corrispondente.";
    showContent: boolean = false;
    stati = [{ "name": "In attesa", "value": "in_attesa" }, { "name": "In corso", "value": "in_corso" }, { "name": "Revisionare", "value": "revisione" }, { "name": "Archiviata", "value": "archiviata" }];
    env = environment;
    timeoutTooltip = 250;
    @ViewChild('tooltip') tooltip: NgbTooltip;

    filterDatePickerConfig = {
        locale: 'it',
        firstDayOfWeek: 'mo'
    };

    @ViewChild('cmPagination') private cmPagination: PaginationComponent;


    constructor(
        private dataService: DataService,
        private route: ActivatedRoute,
        private router: Router
    ) {
        // force route reload whenever params change;
        this.router.routeReuseStrategy.shouldReuseRoute = () => false;
        this.filtro = {
            titolo: '',
            stato: ''
        }
    }

    ngOnInit(): void {
        this.title = 'Lista attività';
        console.log(this.route);
        this.dataService.getAttivitaTipologie().subscribe((res) => {
            this.tipologie = res;
            this.getAttivitaAltPage(1);
        },
            (err: any) => console.log(err),
            () => console.log('getAttivitaTipologie'));

    }

    openDetail(aa) {
        this.router.navigate(['../detail', aa.id], { relativeTo: this.route });
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
        this.filterSearch = true;
        this.cmPagination.changePage(1);
        this.getAttivitaAltPage(1);
    }

    selectStatoFilter() {
        this.cmPagination.changePage(1);
        if (this.stato) {
            this.filtro.stato = this.stato;
        } else {
            this.filtro.stato = null;
        }
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
        this.filtro = {
            tipologia: '',
            titolo: '',
            stato: ''
        }
        this.filterSearch = false;
        this.stato = undefined;
        this.getAttivitaAltPage(1);
    }

    customSearchOption() {
        var style = {
            'border-bottom': '2px solid #06c',
            'font-weight': 'bold'
        };
        if (this.filtro.titolo != '') {
            return style;
        }
    }
    
    customStatoOption() {
        var style = {
            'border-bottom': '2px solid #06c',
            'font-weight': 'bold'
        };
        if (this.stato != undefined) {
            return style;
        }
    } 
    
    isValutazioneCompetenzeActive(aa:AttivitaAlternanza) {
        if((aa.tipologia == 7) && (aa.stato != "archiviata")) {
          var dataMinima = moment(aa.dataFine).subtract(1, 'days');
          var now = moment();
          if(dataMinima.isBefore(now)) {
            return true;
          }
        }
        return false;
    }

    valutaCompetenze(aa) {
        //this.router.navigateByUrl('/attivita/detail/' + aa.id + '/modifica/studenti/presenze/individuale');
        this.router.navigate(['/attivita/detail/' + aa.id + '/valuta/competenze'], { relativeTo: this.route });
    }
            

}