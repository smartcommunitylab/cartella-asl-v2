import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../core/services/data.service'
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Location } from '@angular/common';
import { PaginationComponent } from '../shared/pagination/pagination.component';
import { CreaEnteModalComponent } from './actions/crea-ente-modal/crea-ente-modal.component';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap';
import { environment } from '../../environments/environment';

@Component({
    selector: 'enti',
    templateUrl: './enti.component.html',
    styleUrls: ['./enti.component.scss']
})

export class EntiComponent implements OnInit {
    filtro;
    filterSearch = false;
    closeResult: string;
    totalRecords: number = 0;
    pageSize: number = 10;
    currentpage: number = 0;
    menuContent = "In questa pagina trovi una lista degli enti disponibili per lo svolgimento di attività. Puoi crearne di nuovi con il tasto verde “Aggiungi ente”. Per visualizzare i dettagli di un singolo ente, clicca sulla riga corrispondente.";
    showContent: boolean = false;
    timeoutTooltip = 250;
    env = environment;
    enti;
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
    }

    ngOnInit(): void {
        this.getEntiPaged(1);
    }

    openDetail(ente) {
        this.router.navigate(['../detail', ente.id], { relativeTo: this.route });
    }

    openCreate() {
        const modalRef = this.modalService.open(CreaEnteModalComponent, { windowClass: "myCustomModalClass" });
        modalRef.componentInstance.newEnteListener.subscribe((res) => {
            this.router.navigate(['../detail', res.id], { relativeTo: this.route });
        });
    }

    getEntiPaged(page: number) {
        this.dataService.getListaAziende(this.filtro, (page - 1), this.pageSize)
            .subscribe((response) => {
                this.totalRecords = response.totalElements;
                this.enti = response.content;
            },
                (err: any) => console.log(err),
                () => console.log('get piani attivi'));
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
        this.getEntiPaged(1);
    }

    pageChanged(page: number) {
        this.currentpage = page;
        this.getEntiPaged(page);
    }

    refreshEnti() {
        this.filtro = null;
        this.filterSearch = false;
        this.getEntiPaged(1);
    }

    setStatus(ente) {
        if (ente.origin == 'CONSOLE') {
            if (ente.registrazioneEnte && ente.registrazioneEnte.stato == 'inviato') {
                return 'In attivazione';
            } else if (ente.registrazioneEnte && ente.registrazioneEnte.stato == 'confermato') {
                return 'Con account';
            } else {
                return 'Disponibile';
            }            
        } else {
            return 'Con account';
        }
    }

    styleOption(ente) {
        var style = {
            'color': '#00CF86', //green
        };
        if (ente.origin == 'CONSOLE') {
            if (ente.registrazioneEnte && ente.registrazioneEnte.stato == 'inviato') {
                style['color'] = '#7FB2E5';
            } else if (ente.registrazioneEnte && ente.registrazioneEnte.stato == 'confermato') {
                style['color'] = '#00CF86';
            } else {
                style['color'] = '#FFB54C';
            }
        }
        return style;
    }
}