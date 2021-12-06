import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../core/services/data.service'
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { PaginationComponent } from '../shared/pagination/pagination.component';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap';
import { CreaOffertaModalComponent } from './actions/crea-offerta-modal/crea-offerta-modal.component';

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
    filterSearch = false;
    stato;
    owner;
    menuContent = "In questa pagina trovi tutte le offerte di attività presso il tuo ente. Puoi creare una nuova offerta utilizzando il tasto verde “crea offerta”, oppure puoi consultare i dettagli di ciascuna offerta cliccando sulla riga corrispondente”. La creazione di attività, anche a partire dalle offerte, è compito esclusivo degli istituti scolastici";
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
        let enteTipologie = this.tipologie.filter(f => !f.interna);
        modalRef.componentInstance.tipologie = enteTipologie;
        modalRef.componentInstance.newOffertaListener.subscribe((offerta) => {
            this.dataService.createOfferta(offerta).subscribe((response) => {
                this.router.navigateByUrl('/offerte/detail/' + response.id + '/modifica/istituti');
                // this.router.navigate(['../detail', response.id + '/modifica/istituti'], { relativeTo: this.route });
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
        this.filterSearch = true;
        if (this.cmPagination)
            this.cmPagination.changePage(1);
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
            titolo: '',
            stato: '',
        }
        this.stato = undefined;
        this.filterSearch = false;
        this.getOffertePage(1);
    }

    setIstitutiLabel(off) {
        let label = '';
        if (off.istitutiAssociati.length == 1) {
            label = off.istitutiAssociati[0].nomeIstituto;
        } else if (off.istitutiAssociati.length > 1) {
            label = off.istitutiAssociati.length + ' istituti';
        } else if (off.stato == 'bozza') {
            label = 'Nessun istituto selezionato'
        }
        return label;
    }

    showTipRiga(ev, off, tp) {
        if (!off.toolTipRiga) {
            off.toolTipRiga = this.setTipRiga(off);
        }
    }

    setTipRiga(off) {
        let tip = '';
        if (off.istitutiAssociati.length > 0) {
            for (let i = 0; i < off.istitutiAssociati.length; i++) {
                tip = tip + off.istitutiAssociati[i].nomeIstituto + '\n';// + "-" + aa.classi[i] + '\n';
                if (i == 5) {
                    break;
                }
            }
        } else if (off.stato == 'bozza') {
            tip = 'Seleziona almeno un istituto per rendere attiva l’offerta';
        }
        return tip;
    }

    setIndirizzo(off) {
        let label = '';
        if (off.luogoSvolgimento) {
            if (off.luogoSvolgimento.length > 10) {
                label = off.luogoSvolgimento.substring(0, 10) + '...';
            } else {
                label = off.off.luogoSvolgimento;
            }
        }
        return label;
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

}