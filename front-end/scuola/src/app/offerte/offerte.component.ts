import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../core/services/data.service'
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { PaginationComponent } from '../shared/pagination/pagination.component';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap';
import { CreaOffertaModalComponent } from './actions/crea-offerta-modal/crea-offerta-modal.component';
import { StateStorageService } from '../core/auth/state-storage.service';

@Component({
    selector: 'offerte',
    templateUrl: './offerte.component.html',
    styleUrls: ['./offerte.component.scss']
})

export class OfferteComponent implements OnInit {
    title: string;
    offerte = [];
    filtro;
    filterSearch = false;
    totalRecords: number = 0;
    pageSize: number = 10;
    currentpage: number = 0;
    tipologie;
    tipologia = "Tipologia";
    stato;
    menuContent = "In questa pagina trovi tutte le offerte accessibili al tuo istituto. Puoi crearne di nuove, o cercare nella lista. Per creare un’attività a partire da un’offerta, clicca sulla riga e vai alla sua pagina.";
    showContent: boolean = false;
    stati = [{ "name": "Disponibile", "value": "disponibile" }, { "name": "Scaduta", "value": "scaduta" }];
    sources = [{ "name": "Istituto", "value": "istituto" }, { "name": "Ente", "value": "ente" }];
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
        private storageService: StateStorageService,
        private modalService: NgbModal
    ) { }

    ngOnInit(): void {
        this.title = 'Lista offerte';
        this.tipologia = 'Tipologia';
        this.filtro = {
            tipologia: '',
            titolo: '',
            stato: '',
            ownerIstituto: null
        };
        this.dataService.getAttivitaTipologie().subscribe((res) => {
            this.tipologie = res;
            this.getOffertePage(1);
        },
            (err: any) => console.log(err),
            () => console.log('getAttivitaTipologie'));

    }

    openDetail(off) {
        this.router.navigate(['../detail', off.id], { relativeTo: this.route });
    }

    creaOfferta() {
        const modalRef = this.modalService.open(CreaOffertaModalComponent, { windowClass: "creaAttivitaModalClass" });
        modalRef.componentInstance.tipologie = this.tipologie;
        modalRef.componentInstance.newOffertaListener.subscribe((offerta) => {
            this.dataService.createOfferta(offerta).subscribe((response) => {
                this.router.navigate(['../detail', response.id], { relativeTo: this.route });
            },
                (err: any) => console.log(err),
                () => console.log('createAttivitaAlternanza'));
        });
    }

    getOffertePage(page: number) {
        this.dataService.getOffeteForIstitutoAPI(this.filtro, (page - 1), this.pageSize)
            .subscribe((response) => {
                this.totalRecords = response.totalElements;
                this.offerte = response.content;
            },
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
        this.filterSearch = true;
        this.getOffertePage(1);
    }

    selectTipologiaFilter() {
        if (this.cmPagination)
            this.cmPagination.changePage(1);
        if (this.tipologia && this.tipologia != 'Tipologia') {
            this.filtro.tipologia = this.tipologia;
        } else {
            this.filtro.tipologia = null;
        }
        this.filterSearch = true;
        this.getOffertePage(1);
    }

    selectStatoFilter() {
        this.cmPagination.changePage(1);
        if (this.stato) {
            this.filtro.stato = this.stato;
        } else {
            this.filtro.stato = null;
        }
        this.filterSearch = true;
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

    refreshOfferte() {
        this.filtro = {
            tipologia: '',
            titolo: '',
            stato: '',
            ownerIstituto: null
        }
        this.tipologia = "Tipologia"
        this.stato = undefined;
        this.filterSearch = false;
        this.getOffertePage(1);
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

    customTipologiaOption() {
        var style = {
            'border-bottom': '2px solid #06c',
            'font-weight': 'bold'
        };
        if (this.tipologia != 'Tipologia') {
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

}