import { Component, OnInit, Input, AfterContentInit } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import { NgbModal, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ModalService, IModalContent } from '../../core/modal/modal.service'
import { IPagedResults, EsperienzaSvolta, TipologiaAzienda } from '../../shared/interfaces';
import { DataService } from '../../core/services/data.service';
import { CompetenzaDetailModalComponent } from '../../oppurtunities/skills-selector/modals/competenza-detail-modal/competenza-detail-modal.component';

@Component({
  selector: 'activity-details',
  templateUrl: './activity-details.component.html',
  styleUrls: ['./activity-details.component.scss']
})
export class ActivityDetailComponent implements OnInit {


  titolo: string;
  tipologiaAzienda: TipologiaAzienda[] = [];
  id: string;
  name: string;
  surname: string;

  @Input() esperienzaSvolta: EsperienzaSvolta;

  constructor(private route: ActivatedRoute, private dataService: DataService, private modalPopupService: ModalService, private modalService: NgbModal) {

  }

  ngOnInit() {
     
    this.dataService.getData("tipologiaAzienda").subscribe((data) => {
      this.tipologiaAzienda = data;
    });
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
}


