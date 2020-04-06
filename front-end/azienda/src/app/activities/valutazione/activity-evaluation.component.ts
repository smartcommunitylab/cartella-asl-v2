import { Component, OnInit, Input } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';

import { IPagedResults, EsperienzaSvolta, Valutazione } from '../../shared/interfaces';
import { NgbModal, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ModalService, IModalContent } from '../../core/modal/modal.service'
import { GrowlerService, GrowlerMessageType } from '../../core/growler/growler.service';
import { DataService } from '../../core/services/data.service';
import { ActivityEvalutationEditComponent } from './activity-evaluation-edit.component';
import { AttivitaCancellaModal } from '../modal/cancella/attivita-cancella-modal.component';
import { CompetenzaDetailModalComponent } from '../../oppurtunities/skills-selector/modals/competenza-detail-modal/competenza-detail-modal.component';
import { NgbRatingConfig } from '@ng-bootstrap/ng-bootstrap';
import { serverAPIConfig } from '../../core/serverAPIConfig'

@Component({
  selector: 'activity-evaluation',
  templateUrl: './activity-evaluation.component.html',
  styleUrls: ['./activity-evaluation.component.scss']
})
export class ActivityEvalutationComponent implements OnInit {

  @Input() esperienzaSvolta: EsperienzaSvolta;

  eval: Valutazione;

  name: string;

  currentRate: number = 4;

  modalPopup: IModalContent = {
    header: 'Avviso',
    body: 'Sei sicuro di voler eliminare?',
    cancelButtonText: 'Annulla',
    OKButtonText: 'Cancella'
  };
  tipologie;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private modalService: NgbModal,
    private dataService: DataService,
    private modalPopupService: ModalService,
    private growler: GrowlerService,
    config: NgbRatingConfig) {
    config.max = 5;

  }

  ngOnInit() {
     
    if (this.esperienzaSvolta.attivitaAlternanza.individuale) {
      this.name = this.esperienzaSvolta.studente.name + " " + this.esperienzaSvolta.studente.surname;
    } else {
      this.name = "Classe " + this.esperienzaSvolta.attivitaAlternanza.classe;
    }

    if (this.esperienzaSvolta && this.esperienzaSvolta.schedaValutazioneAzienda)
    this.dataService.downloadschedaValutazione(this.esperienzaSvolta.id).subscribe((val: Valutazione) => {
      this.eval = val;
      this.eval.url = serverAPIConfig.host + '/' + val.url;
    });

    this.dataService.getAttivitaTipologie().subscribe((res) => {
      this.tipologie = res;
    });

  }

  getTipologiaAttivita(tipologiaId) {
    if (this.tipologie) {
      return this.tipologie.find(data => data.id == tipologiaId);
    } else {
      return tipologiaId;
    }
  }

  uploadSchedaValutazione(fileInput) {
    if (fileInput.target.files && fileInput.target.files[0]) {

      this.dataService.uploadDocument(fileInput.target.files[0], this.esperienzaSvolta.id+'').subscribe((val: Valutazione) => {
        this.eval = val;
        this.dataService.downloadschedaValutazione(this.esperienzaSvolta.id).subscribe((val: Valutazione) => {
          this.eval = val;
          this.eval.url = serverAPIConfig.host + '/' + val.url;
        });
      });
    }    
  }

  openModify(valutazione: Valutazione) {

    const modalRef = this.modalService.open(ActivityEvalutationEditComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.espId = this.esperienzaSvolta.id;

    modalRef.result.then((result) => {
       
      if (result == 'update') {
        this.dataService.downloadschedaValutazione(this.esperienzaSvolta.id).subscribe((val: Valutazione) => {
          this.eval = val;
          this.eval.url = serverAPIConfig.host + '/' + val.url;
        });
      }

    });
  }

  openCreate(valutazione: Valutazione) {
    const modalRef = this.modalService.open(ActivityEvalutationEditComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.espId = this.esperienzaSvolta.id;
    modalRef.result.then((result) => {
      if (result == 'update') {
        this.dataService.downloadschedaValutazione(this.esperienzaSvolta.id).subscribe((val: Valutazione) => {
          this.eval = val;
          this.eval.url = serverAPIConfig.host + '/' + val.url;
        });
      }
    });
  }


  download() {
    this.dataService.downloadschedaValutazione(this.esperienzaSvolta.id).subscribe((val: Valutazione) => {
      window.open(val.url, '_blank');
    });

  }

  openDelete() {
    const modalRef = this.modalService.open(AttivitaCancellaModal);
    modalRef.componentInstance.esp = this.esperienzaSvolta;
    modalRef.result.then((result) => {
      if (result == 'deleted') {
        this.eval = null;
      }
    });
  }

}


