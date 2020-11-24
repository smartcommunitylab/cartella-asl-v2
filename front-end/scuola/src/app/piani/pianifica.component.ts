import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../core/services/data.service'
import { PianoAlternanza } from '../shared/classes/PianoAlternanza.class'
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Location } from '@angular/common';
import { PaginationComponent } from '../shared/pagination/pagination.component';
import { NewPianoModalComponent } from './actions/new-piano-modal/new-piano-modal.component';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap';
import { environment } from '../../environments/environment';

@Component({
    selector: 'pianifica',
    templateUrl: './pianifica.component.html',
    styleUrls: ['./pianifica.component.scss']
})

export class PianificaComponent implements OnInit {

    title: string;
    stati;
    pianiAlternanza: PianoAlternanza[];
    filtro;
    filterSearch = false;
    closeResult: string;
    totalRecords: number = 0;
    pageSize: number = 10;
    currentpage: number = 0;
    corsodistudio;
    corsiStudio;
    stato;
    menuContent = "In questa sezione trovi tutti i piani dell’Istituto, attivi e non attivi. Puoi creare nuovi piani, mentre i piani già in uso possono essere duplicati o modificati.";
    showContent: boolean = false;
    timeoutTooltip = 250;
    env = environment;
    filterDatePickerConfig = {
        locale: 'it',
        firstDayOfWeek: 'mo'
    };
    @ViewChild('cmPagination') private cmPagination: PaginationComponent;
    @ViewChild('tooltip') tooltip: NgbTooltip;

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
            inUso: '',
            corsoStudio: '',
            titolo: '',
            stato: ''
        }
        this.stati = [{ "name": "Attivo", "value": "attivo" }, { "name": "Bozza", "value": "bozza" }, { "name": "In scadenza", "value": "in_scadenza" }, { "name": "Scaduto", "value": "scaduto" }, { "name": "In attesa", "value": "in_attesa" }];
    }

    ngOnInit(): void {
        this.title = 'Lista piani';
        console.log(this.route);
        //Restore filter states from reload
        var inUsoRestore = this.route.snapshot.queryParamMap.get('inuso');
        this.filtro.inUso = inUsoRestore;
        var corsoStudioId = this.route.snapshot.queryParamMap.get('corsostudio');
        if (!corsoStudioId) {
            this.getPianiPage(1);
        }
        this.dataService.getCorsiStudio().subscribe((response) => {
            this.corsiStudio = response;
            if (corsoStudioId) {
                this.filtro.corsoStudio = corsoStudioId;
                this.getPianiPage(1);
            }
        },
            (err: any) => console.log(err),
            () => console.log('get corsi studio'));
    }

    openDetail(piano: PianoAlternanza) {
        this.router.navigate(['../detail', piano.id], { relativeTo: this.route });
    }

    openCreate() {
        const modalRef = this.modalService.open(NewPianoModalComponent, { windowClass: "myCustomModalClass" });
        modalRef.componentInstance.corsiStudio = this.corsiStudio;
        modalRef.componentInstance.newPianoListener.subscribe((piano: PianoAlternanza) => {
            this.dataService.createPiano(piano).subscribe((piano) => {
                this.router.navigate(['../detail', piano.id], { relativeTo: this.route });
            },
                (err: any) => console.log(err),
                () => console.log('get piani attivi'));
        });
    }

    getPianiPage(page: number) {
        this.dataService.getPianiPage((page - 1), this.pageSize, this.filtro)
            .subscribe((response) => {
                this.totalRecords = response.totalElements;
                this.pianiAlternanza = response.content;
            },
                (err: any) => console.log(err),
                () => console.log('get piani attivi'));
    }

    getCorsoDiStudioString(corsoStudioId) {
        if (this.corsiStudio) {
            let rtn = this.corsiStudio.find(data => data.courseId == corsoStudioId);
            if (rtn) return rtn.titolo;
            return corsoStudioId;
        }
    }

    recreateFilterUrl() {
        let queryParams: any = {};
        if (this.filtro.corsoStudio) {
            queryParams.corsostudio = this.filtro.corsoStudio;
        }
        if (this.filtro.inUso) {
            queryParams.inuso = this.filtro.inUso;
        }
        const url = this.router
            .createUrlTree([], { relativeTo: this.route, queryParams: queryParams })
            .toString();
        this.location.go(url);
    }

    menuContentShow() {
        this.showContent = !this.showContent;
    }

    cerca() {
        this.cmPagination.changePage(1);
        this.filterSearch = true;
        this.getPianiPage(1);
    }

    selectCorsoFilter() {
        this.cmPagination.changePage(1);
        if (this.corsodistudio) {
            this.filtro.corsoStudioId = this.corsodistudio;
        } else {
            this.filtro.corsoStudioId = null;
        }
        this.filterSearch = true;
        this.getPianiPage(1);
    }

    selectStatoFilter() {
        this.cmPagination.changePage(1);
        if (this.stato) {
            this.filtro.stato = this.stato;
        } else {
            this.filtro.stato = null;
        }
        this.filterSearch = true;
        this.getPianiPage(1);
    }

    pageChanged(page: number) {
        this.currentpage = page;
        this.getPianiPage(page);
    }

    getStatoNome(statoValue) {
        if (this.stati) {
            let rtn = this.stati.find(data => data.value == statoValue);
            if (rtn) return rtn.name;
            return statoValue;
        }
    }

    showTip(ev, piano, tp) {
        console.log(ev.target.title);
        if (!piano.toolTip) {
            if (piano.stato == 'attivo') {
                let msg = 'Piano attivo dal gg/mm/aaaa';
                var date = new Date(piano.dataAttivazione);
                piano.toolTip = msg.replace("gg/mm/aaaa", date.getDate() + '/' + (date.getMonth() + 1) + '/' + date.getFullYear())
            } else if (piano.stato == 'bozza') {
                piano.toolTip = 'Piano non attivo';
            } else if (piano.stato == 'scaduto') {
                let msg = 'Piano scaduto il gg/mm/aaaa';
                var date = new Date(piano.dataScadenza);
                piano.toolTip = msg.replace("gg/mm/aaaa", date.getDate() + '/' + (date.getMonth() + 1) + '/' + date.getFullYear())
            } else {
                if (!piano.duplicaPiano) {
                    piano.startTimeoutTipStato = true;
                    setTimeout(() => {
                        if (piano.startTimeoutTipStato == true) {
                            this.env.globalSpinner = false;
                            piano.fetchingToolTip = true;
                            this.dataService.getDulicaPiano(piano.id).subscribe((duplicaPiano) => {
                                piano.duplicaPiano = duplicaPiano;
                                this.setTooltip(piano);
                                piano.fetchingToolTip = false;
                                this.env.globalSpinner = true;
                                if (piano.startTimeoutTipStato == true) {
                                    tp.ngbTooltip = piano.toolTip;
                                    tp.open();
                                }
                                piano.startTimeoutTipStato = false;
                            });
                        }
                    }, this.timeoutTooltip);
                } else {
                    this.setTooltip(piano);
                }
            }
        }
    }

    hideTooltip(ev, piano, tp) {
        piano.startTimeoutTipStato = false;
        piano.fetchingToolTip = false;
    }

    setTooltip(piano) {
        let in_scadenza_msg = 'Il gg/mm/aaaa questo piano è stato sostituito da *nomepiano*';
        let in_attesa_msg = 'Il gg/mm/aaaa questo piano sostituirà il piano *nomepiano*';
        var dateActual = new Date(piano.duplicaPiano.dataAttivazione);
        if (piano.stato == 'in_scadenza') {
            in_scadenza_msg = in_scadenza_msg.replace("*nomepiano*", piano.duplicaPiano.titolo);
            in_scadenza_msg = in_scadenza_msg.replace("gg/mm/aaaa", dateActual.getDate() + '/' + (dateActual.getMonth() + 1) + '/' + dateActual.getFullYear());
            piano.toolTip = in_scadenza_msg;
        } else if (piano.stato == 'in_attesa') {
            in_attesa_msg = in_attesa_msg.replace("*nomepiano*", piano.duplicaPiano.titolo);
            in_attesa_msg = in_attesa_msg.replace("gg/mm/aaaa", dateActual.getDate() + '/' + (dateActual.getMonth() + 1) + '/' + dateActual.getFullYear());
            piano.toolTip = in_attesa_msg;
        }
    }

    onUnovering(event) {
        console.log(event);
    }

    refreshPiani() {
        this.filtro = {
            inUso: '',
            corsoStudio: '',
            titolo: '',
            stato: ''
        }
        this.corsodistudio = undefined;
        this.stato = undefined;
        this.filterSearch = false;
        this.getPianiPage(1);
    }

}