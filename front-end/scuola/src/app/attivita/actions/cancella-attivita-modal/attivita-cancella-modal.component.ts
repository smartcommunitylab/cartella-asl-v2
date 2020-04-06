import { Component, Output, Input, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AttivitaAlternanza } from '../../../shared/classes/AttivitaAlternanza.class';

@Component({
  selector: 'attivita-cancella-modal',
  templateUrl: './attivita-cancella-modal.html',
  styleUrls: ['./attivita-cancella-modal.scss']
})
export class AttivitaCancellaModal {
  closeResult: string;

  @Input() attivita: AttivitaAlternanza;
  @Output() onDelete = new EventEmitter<string>();

  constructor(public activeModal: NgbActiveModal) { }
  delete() {
    this.activeModal.close();
    this.onDelete.emit('DEL');
  }
}

