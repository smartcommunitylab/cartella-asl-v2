import { Component, Output, EventEmitter, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'ruolo-cancella-modal',
  templateUrl: './ruolo-cancella-modal.html',
  styleUrls: ['./ruolo-cancella-modal.scss']
})
export class RuoloCancellaModal {
  closeResult: string;
  @Input() registeredDocente;
  @Output() onDelete = new EventEmitter<string>();

  constructor(public activeModal: NgbActiveModal) { }

  delete() {
    this.onDelete.emit('DELETED');
    this.activeModal.close("DELETED");
  }

}

