import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../core/services/data.service'
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Location } from '@angular/common';
import { PaginationComponent } from '../shared/pagination/pagination.component';
import { NewAttivtaModal } from './actions/new-attivita-modal/new-attivita-modal.component';
import { CreaAttivitaModalComponent } from './actions/crea-attivita-modal/crea-attivita-modal.component';
import { AttivitaAlternanza } from '../shared/classes/AttivitaAlternanza.class';
import { environment } from '../../environments/environment';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap';


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
    tipologia = "Tipologie";
    stato;
    menuContent = "In questa pagina trovi tutte le attività. Puoi cercarle o filtrarle per tipologia o stato. Con il tasto blu sulla destra puoi andare direttamente alla gestione presenze. Per visualizzare un’attività, clicca sulla riga corrispondente.";
    showContent: boolean = false;
    stati = [ {"name": "In attesa", "value": "in_attesa"}, { "name": "In corso", "value": "in_corso" }, { "name": "Revisionare", "value": "revisione" }, {"name": "Archiviata", "value": "archiviata"}];
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
        private router: Router,
        private location: Location,
        private modalService: NgbModal
    ) {
        
        // force route reload whenever params change;
        this.router.routeReuseStrategy.shouldReuseRoute = () => false;
        
        this.filtro = {
            tipologia: '',
            titolo: '',
            stato : ''
        }
       

     }

    ngOnInit(): void {

        this.title = 'Lista attività';
        console.log(this.route);
        this.dataService.getAttivitaTipologie().subscribe((res) => {
            this.tipologie = res;
            if (this.tipologia) {
                this.filtro.tiologia = this.tipologia;
            }
            this.getAttivitaAltPage(1);
        },
            (err: any) => console.log(err),
            () => console.log('getAttivitaTipologie'));

    }

    openDetail(aa) {
        this.router.navigate(['../detail', aa.id], { relativeTo: this.route });
    }
   
    openCreate() {
        const modalRef = this.modalService.open(NewAttivtaModal, { windowClass: "creaAttivitaModalClass" });
        modalRef.componentInstance.newPianoListener.subscribe((option) => {
            if (option == 2) {
                this.openCreateNewAttivita();            
            }

        });
    }
    
    openCreateNewAttivita() {
        const modalRef = this.modalService.open(CreaAttivitaModalComponent, { windowClass: "creaAttivitaModalClass" });
        modalRef.componentInstance.tipologie = this.tipologie;
        modalRef.componentInstance.newAttivitaListener.subscribe((attivita) => {
             this.dataService.createAttivitaAlternanza(attivita).subscribe((response) => {
                this.router.navigate(['../detail', response.id], { relativeTo: this.route });
             },
                 (err: any) => console.log(err),
                 () => console.log('createAttivitaAlternanza'));

        });
    }

    getAttivitaAltPage(page: number) {
        this.dataService.getAttivitaAlternanzaForIstitutoAPI(this.filtro, (page - 1), this.pageSize)
            .subscribe((response) => {
                this.totalRecords = response.totalElements;
                this.attivitaAAs = response.content;
                for (let aa of this.attivitaAAs) {
                    this.tipologie.filter(tipo => {
                        if (tipo.id == aa.tipologia) {
                            aa.individuale = tipo.individuale;
                            if (!aa.individuale) {
                                var classSet = [];
                                for (let cls of aa.classi) {
                                    if (classSet.indexOf(cls) < 0)
                                     classSet.push(cls);
                                }
                                aa.classSet = classSet;
                                if (aa.studenti.length == 1 && aa.classSet.length == 1) {
                                    aa.groupRigaTip = aa.studenti[0] + 'i - ' + aa.classSet[0];
                                } else if (aa.classSet.length == 1 && aa.studenti.length > 0) {
                                    aa.groupRigaTip = aa.studenti.length + ' studenti - ' + aa.classSet[0];
                                } else if (aa.classSet.length > 1 && aa.studenti.length > 0) {
                                    aa.groupRigaTip = aa.studenti.length + ' studenti - ' + aa.classSet.length + ' classi';
                                }
                            } 
                                
                        }
                    })
                }
                
            }
                ,
                (err: any) => console.log(err),
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
        this.getAttivitaAltPage(1);        
    }

    selectTipologiaFilter() {
        this.cmPagination.changePage(1);
        if (this.tipologia && this.tipologia != 'Tipologie') {
          this.filtro.tipologia = this.tipologia;
        } else {
          this.filtro.tipologia = null;
        }
        this.getAttivitaAltPage(1);
    }
    
    selectStatoFilter() {
        this.cmPagination.changePage(1);
        if (this.stato) {
          this.filtro.stato = this.stato;
        } else {
          this.filtro.stato = null;
        }
        this.getAttivitaAltPage(1);
    }
    
    pageChanged(page: number) {
        this.currentpage = page;
        this.getAttivitaAltPage(page);
    }

    showTipStatoRiga(ev, aa, tp) {
        if(!aa.toolTipoStatoRiga) {
            if (aa.stato == 'archiviata') {            
                if(!aa.report) {
                    aa.startTimeoutTipStato = true;
                    setTimeout(() => {
                        if(aa.startTimeoutTipStato == true) {
                            this.env.globalSpinner=false;
                            aa.fetchingToolTipRiga = true;
                            this.dataService.getAttivitaReportStudenti(aa.id).subscribe((report) => {
                                aa.report = report;
                                this.setTipStatoRiga(aa);
                                aa.fetchingToolTipRiga = false; 
                                this.env.globalSpinner=true;
                                if(aa.startTimeoutTipStato == true) {
                                    tp.ngbTooltip = aa.toolTipoStatoRiga;
                                    tp.open();    
                                }
                                aa.startTimeoutTipStato = false;               
                            }); 
                        }
                    }, this.timeoutTooltip);
                } else {
                    this.setTipStatoRiga(aa);
                }
            } else if (aa.stato == 'revisione') {
                let labelRevisione = 'Questa attività si è conclusa il gg/mm/aaaa. È necessario archiviare l’attività, altrimenti le ore non saranno considerate valide!';
                var dataFine = new Date(aa.dataFine);
                labelRevisione = labelRevisione.replace("gg/mm/aaaa",  dataFine.getDate() + '/' + (dataFine.getMonth()+1) + '/' + dataFine.getFullYear());
                aa.toolTipoStatoRiga = labelRevisione;
            } else if (aa.stato == 'in_attesa') {
                aa.toolTipoStatoRiga = 'Quest’attività non è ancora iniziata';
            }
        }
    }

    hideTipStatoRiga(ev, aa, tp) {
        aa.startTimeoutTipStato = false; 
        aa.fetchingToolTipRiga = false;   
    }

    setTipStatoRiga(aa) {
        var dateArchivazione = new Date(aa.dataArchiviazione);
        let labelArchiviata = 'Archiviata il gg/mm/aaaa, *nEs*/*n* studenti hanno completato l’attività';
        labelArchiviata = labelArchiviata.replace('*nEs*', aa.report.numeroEsperienzeCompletate);
        labelArchiviata = labelArchiviata.replace('*n*', aa.report.studenti.length);
        labelArchiviata = labelArchiviata.replace("gg/mm/aaaa",  dateArchivazione.getDate() + '/' + (dateArchivazione.getMonth()+1) + '/' + dateArchivazione.getFullYear());
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
            tip = tip + aa.studenti[i] + "-" + aa.classi[i] + '\n';
            if (i == 15) {
                break;
            }
        } 
        return tip;   
    }

    setAssegnatoLabel(aa) {
        let label = '';
        let classi = [];
        if (aa.classi.length > 0) {
            aa.classi.forEach(element => {
                if(!classi.includes(element)) {
                    classi.push(element);
                }
            });
        }
        if (aa.studenti.length == 1) {
            label = aa.studenti[0];    
        } else if (aa.studenti.length > 1) {
            label = aa.studenti.length + ' studenti';
        }
        if (classi.length == 1) {
            label = label + ' - ' + classi[0];
        } else if (classi.length > 1) {
            label = label + ' - ' + classi.length + ' classi';
        }
        return label;
    }

    showTipButton(ev, aa, tp) {
        // console.log("aa::",aa,"event", ev, "tooltip", tp);
        if (!aa.toolTipButton) {
            if(!aa.report) {
                aa.startTimeoutTipButton = true;
                setTimeout(() => {
                    if(aa.startTimeoutTipButton == true) {
                        this.env.globalSpinner=false;
                        aa.fetchingToolTipButton = true;
                        this.dataService.getAttivitaReportStudenti(aa.id).subscribe((report) => {
                            aa.report = report;
                            this.setTipButton(aa);
                            aa.fetchingToolTipButton = false;
                            this.env.globalSpinner=true;
                            if(aa.startTimeoutTipButton == true) {
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
        // this.tipologie.filter(tipo => {
        //     if (tipo.id == aa.tipologia) {
        //         aa.individuale = tipo.individuale;
        //     }
        // })
        if (aa.individuale) {
            this.router.navigateByUrl('/attivita/detail/' + aa.id + '/modifica/studenti/presenze/individuale');
        } else {
            this.router.navigateByUrl('/attivita/detail/' + aa.id + '/modifica/studenti/presenze/gruppo');
        }
    }

    getStatoNome(statoValue) {
        if (this.stati) {
            let rtn = this.stati.find(data => data.value == statoValue);
            if (rtn) return rtn.name;
            return statoValue;
        }
    }

    refreshAttivita(){
        this.filtro = {
            tipologia: '',
            titolo: '',
            stato : ''
        }
        this.tipologia = "Tipologie"
        this.stato = undefined;
        this.getAttivitaAltPage(1);
    }
}