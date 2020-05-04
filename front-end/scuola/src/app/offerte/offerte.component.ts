import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../core/services/data.service'
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Location } from '@angular/common';
import { PaginationComponent } from '../shared/pagination/pagination.component';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap';


@Component({
    selector: 'offerte',
    templateUrl: './offerte.component.html',
    styleUrls: ['./offerte.component.scss']
})

export class OfferteComponent implements OnInit {
    title: string;
    offerte = [];
    filtro;
    totalRecords: number = 0;
    pageSize: number = 10;
    currentpage: number = 0;
    tipologie;
    tipologia = "Tipologie";
    stato;
    owner;
    filterText;
    menuContent = "In questa pagina trovi tutte le offerte accessibili al tuo istituto. Puoi crearne di nuove, o cercare nella lista. Per creare un’attività a partire da un’offerta, clicca sulla riga e vai alla sua pagina.";
    showContent: boolean = false;
    stati = [{ "name": "Disponibile", "value": "disponibile" }, { "name": "Scaduta", "value": "scaduta" }];
    sources = [{ "name": "Istituto", "value": "istituto" }, { "name": "Ente", "value": "ente" }];
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
            stato: '',
            ownerIstituto: null
        }

    }

    ngOnInit(): void {

        this.title = 'Lista offerte';
        console.log(this.route);
        this.dataService.getAttivitaTipologie().subscribe((res) => {
            this.tipologie = res;
            if (this.tipologia && this.tipologia != 'Tipologie') {
                this.filtro.tipologia = this.tipologia;
            }
            this.getOffertePage(1);
        },
            (err: any) => console.log(err),
            () => console.log('getAttivitaTipologie'));

    }

    openDetail(aa) {
        // this.router.navigate(['../detail', aa.id], { relativeTo: this.route });
    }

    creaOfferta() {
        // const modalRef = this.modalService.open(CreaAttivitaModalComponent, { windowClass: "creaAttivitaModalClass" });
        // modalRef.componentInstance.tipologie = this.tipologie;
        // modalRef.componentInstance.newAttivitaListener.subscribe((attivita) => {
        //      this.dataService.createAttivitaAlternanza(attivita).subscribe((response) => {
        //         this.router.navigate(['../detail', response.id], { relativeTo: this.route });
        //      },
        //          (err: any) => console.log(err),
        //          () => console.log('createAttivitaAlternanza'));

        // });
    }

    getOffertePage(page: number) {
        this.dataService.getOffeteForIstitutoAPI(this.filtro, (page - 1), this.pageSize)
            .subscribe((response) => {
                this.totalRecords = response.totalElements;
                this.offerte = response.content;
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
        if (this.cmPagination)
            this.cmPagination.changePage(1);
        if (this.filterText) {
            this.filtro.titolo = this.filterText;
        } else {
            this.filtro.titolo = null;
        }
        this.getOffertePage(1);
    }

    selectTipologiaFilter() {
        if (this.cmPagination)
            this.cmPagination.changePage(1);
        if (this.tipologia && this.tipologia != 'Tipologie') {
            this.filtro.tipologia = this.tipologia;
        } else {
            this.filtro.tipologia = null;
        }
        this.getOffertePage(1);
    }

    selectOwnerFilter() {
        if (this.cmPagination)
            this.cmPagination.changePage(1);
        if (this.owner) {
            if (this.owner == 'istituto') {
                this.filtro.ownerIstituto = true;
            } else {
                this.filtro.ownerIstituto = false;
            }
        } else {
            this.filtro.ownerIstituto = null;
        }
        this.getOffertePage(1);
    }

    selectStatoFilter() {
        this.cmPagination.changePage(1);
        if (this.stato) {
            this.filtro.stato = this.stato;
        } else {
            this.filtro.stato = null;
        }
        this.getOffertePage(1);
    }

    pageChanged(page: number) {
        this.currentpage = page;
        this.getOffertePage(page);
    }

    showTipStatoRiga(ev, off, tp) {
        if (!off.toolTipoStatoRiga) {
            off.toolTipoStatoRiga = '(' + off.postiRimanenti + '/' + off.postiDisponibili + ') posti liberi';
        }
    }

    setNomeEnte(off) {
        let label = '';
        if (off.nomeEnte) {
            label = off.nomeEnte;
        } else {
            label = 'Istituto';
        }
        return label;
    }

    setSource(off) {
        let label = '';
        if (off.istitutoId) {
            label = 'Istituto';
        } else {
            label = 'Ente';
        }        
        return label;
    }

    getStatoNome(statoValue) {
        if (this.stati) {
            let rtn = this.stati.find(data => data.value == statoValue);
            if (rtn) return rtn.name;
            return statoValue;
        }
    }

}