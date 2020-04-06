import { Component, Output, Input, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ReportCompetenza } from '../../../shared/classes/ReportCompetenza.class';

@Component({
  selector: 'competenza-cancella-modal',
  templateUrl: './competenza-cancella-modal.html',
  styleUrls: ['./competenza-cancella-modal.scss']
})
export class CompetenzaCancellaModal {
  closeResult: string;

  @Input() competenzaReport: ReportCompetenza;
  @Output() onDelete = new EventEmitter<string>();

  constructor(public activeModal: NgbActiveModal) { }
  delete() {
    this.activeModal.close();
    this.onDelete.emit('DEL');
  }
}

