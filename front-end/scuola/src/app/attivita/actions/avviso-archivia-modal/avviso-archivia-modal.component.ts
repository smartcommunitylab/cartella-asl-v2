import { Component, Output, Input, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'avviso-archivia-modal',
  templateUrl: './avviso-archivia-modal.html',
  styleUrls: ['./avviso-archivia-modal.scss']
})
export class AvvisoArchiviaModal {
  closeResult: string;

  @Input() nrStudentiNonCompletato;
  @Input() nrTotale;
  @Output() onArchivia = new EventEmitter<string>();

  constructor(public activeModal: NgbActiveModal) { }

  archivia() {
    this.activeModal.close();
    this.onArchivia.emit('ARCHIVIA');
  }
}

