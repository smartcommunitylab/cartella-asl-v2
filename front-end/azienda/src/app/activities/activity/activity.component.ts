import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { Location } from '@angular/common';
import { DataService } from '../../core/services/data.service';
import { ICustomer, IPagedResults, EsperienzaSvolta, Stato, TipologiaAzienda } from '../../shared/interfaces';
import { TrackByService } from '../../core/services/trackby.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { CompetenzaDetailModalComponent } from '../../oppurtunities/skills-selector/modals/competenza-detail-modal/competenza-detail-modal.component';
import { NoteAziendaEditModalComponent } from './modals/note-azienda-edit-modal/note-azienda-edit-modal.component';

@Component({
    selector: 'acitivty-component',
    templateUrl: './activity.component.html',
    styleUrls: ['./activity.scss']
})
export class ActivityComponent implements OnInit {

    customers: ICustomer[];
    totalRecords: number = 0;
    pageSize: number = 5;
    displayMode: DisplayModeEnum;
    titolo: string;
    id: string = "";
    name: string;
    surname: string;
    calendarView: boolean = false;
    evaluationView: boolean = false;
    detailsView: boolean = false;
    periodStartDate: number;
    periodEndDate: number;

    esperienzaSvolta: EsperienzaSvolta;
    tipologiaAzienda: TipologiaAzienda[] = [];
    status: Stato[] = [];

    navTitle: string = "Dettaglio attività";

    // try to use location.back here;
    breadcrumbItems = [
        {
            title: "Lista attività",
            location: "../../"
        },
        {
            title: "Dettaglio attività",
            location: "./"
        }
    ];


    constructor(private location: Location, private router: Router, private route: ActivatedRoute, private dataService: DataService, private trackbyService: TrackByService, private modalService: NgbModal) { }

    back() {
        this.location.back();
    }

    ngOnInit() {

        this.route.params.subscribe((params: Params) => {

            this.id = params['id'];
            this.dataService.getEsperienzaSvoltaByIdAPI(this.id)
                .subscribe((activity: EsperienzaSvolta) => {
                    this.esperienzaSvolta = activity;
                });
        });

        this.dataService.getData("tipologiaAzienda").subscribe((data) => {
            this.tipologiaAzienda = data;
        });
    }


    changeDisplayMode(mode: DisplayModeEnum) {
        alert("clicked");
        this.displayMode = mode;
    }

    openDetailPage() {

        if (this.esperienzaSvolta) {
            this.router.navigate(['/attivita', this.id, 'details']);
        }
    }

    openDiaryPage() {
        if (this.esperienzaSvolta) {
            this.router.navigate(['/attivita', this.id, 'diary']);
        }
    }

    openEvalPage() {
        if (this.esperienzaSvolta) {
            this.router.navigate(['/attivita', this.id, 'evaluation']);
        }

    }

    getTipo(id: any) {
        for (let tipo of this.tipologiaAzienda) {
            if (tipo.id == id) {
                return tipo.titolo;
            }
        }
    }

    openDetailCompetenza(competenza, $event) {
      if ($event) $event.stopPropagation();
      const modalRef = this.modalService.open(CompetenzaDetailModalComponent, { size: "lg" });
      modalRef.componentInstance.competenza = competenza;
    }

    editAziendaNote() {
        const modalRef = this.modalService.open(NoteAziendaEditModalComponent, { size: "lg" });
        modalRef.componentInstance.note = this.esperienzaSvolta.noteAzienda;
        modalRef.componentInstance.titolo = this.esperienzaSvolta.attivitaAlternanza.titolo;
        modalRef.componentInstance.esperienzaId = this.esperienzaSvolta.id;
        modalRef.componentInstance.noteSaved.subscribe(nota => {
            this.esperienzaSvolta.noteAzienda = nota;
        });
    }

}

enum DisplayModeEnum {
    Incorso = 0,
    Prossima = 1
}

// enum TipologiaAzienda {
//     TIROCINIO = 1,
//     VISITAAZIENDA = 2,
//     COMMESSAESTERNA = 3
// }