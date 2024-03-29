import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../core/services/data.service'
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Location } from '@angular/common';
import { PaginationComponent } from '../shared/pagination/pagination.component';
import { CreaEnteModalComponent } from './actions/crea-ente-modal/crea-ente-modal.component';
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

    constructor(
        private dataService: DataService,
        private route: ActivatedRoute,
        private router: Router,
        private location: Location,
        private modalService: NgbModal
    ) {}

    ngOnInit(): void {
        this.filtro = '';
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
        this.filtro = '';
        this.filterSearch = false;
        this.getEntiPaged(1);
    }

    setStatus(ente) {
        let stato = 'Disponibile';
        if (ente.registrazioneEnte && ente.registrazioneEnte.stato == 'inviato') {
            stato = 'In attivazione';
        } else if (ente.registrazioneEnte && ente.registrazioneEnte.stato == 'confermato') {
            stato = 'Con account';
        }
        return stato;
    }

    styleOption(ente) {
        var style = {
            'color': '#FFB54C', //orange
        };

        if (ente.registrazioneEnte && ente.registrazioneEnte.stato == 'inviato') {
            style['color'] = '#7FB2E5'; // grey
        } else if (ente.registrazioneEnte && ente.registrazioneEnte.stato == 'confermato') {
            style['color'] = '#00CF86'; // green
        }

        return style;
    }

    showTipStatoRiga(ente) {
        if (!ente.toolTipoStatoRiga) {
            if (ente.registrazioneEnte && ente.registrazioneEnte.stato == 'inviato') {
                ente.toolTipoStatoRiga = 'Email di attivazione inviata, in attesa di risposta.';
            } else if (ente.registrazioneEnte && ente.registrazioneEnte.stato == 'confermato') {
                ente.toolTipoStatoRiga = 'Accesso ente attivato.';
            } else {
                ente.toolTipoStatoRiga = 'Accesso ente non attivato.';
            }

        }
    }

    setConvenzioneStato(ente) {
        let stato = 'Assente';
        if (ente.convenzione && ente.convenzione.stato == 'attiva') {
            stato = 'Attiva';
        } else if (ente.convenzione && ente.convenzione.stato == 'non_attiva') {
            stato = 'Non attiva';
        }
        return stato;
    }

    styleOptionConvenzione(ente) {
        var style = {
            'color': '#707070', //grey
        };

        if (ente.convenzione && ente.convenzione.stato == 'non_attiva') {
            style['color'] = '#F83E5A'; // red
        } else if (ente.convenzione && ente.convenzione.stato == 'attiva') {
            style['color'] = '#00CF86'; // green
        }

        return style;
    }

    showTipStatoRigaConvenzione(ente) {
        if (!ente.toolTipoConvRiga) {
            if (ente.convenzione && ente.convenzione.stato == 'non_attiva') {
                ente.toolTipoConvRiga = 'La convenzione non è attiva. L’ente non può gestire i tirocini tramite la sua interfaccia EDIT.  Vai alla pagina profilo ente per aggiungere una convenzione valida.';
            } else if (ente.convenzione && ente.convenzione.stato == 'attiva') {
                ente.toolTipoConvRiga = 'La convenzione è attiva, e l’ente può collaborare tramite EDIT alla gestione dei tirocini.';
            } else {
                ente.toolTipoConvRiga = 'Questo ente non ha nessuna convenzione attiva con questo istituto. Vai al profilo ente per caricare una convenzione valida.';
            }

        }
    }

    customSearchOption() {
        var style = {
            'border-bottom': '2px solid #06c',
            'font-weight': 'bold'
        };
        if (this.filtro != '') {
            return style;
        }
    }


}