import { Component, Output, Input, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'offerta-cancella-modal',
  templateUrl: './offerta-cancella-modal.html',
  styleUrls: ['./offerta-cancella-modal.scss']
})
export class OffertaCancellaModal {
  closeResult: string;

  @Input() offerta;
  @Output() onDelete = new EventEmitter<string>();

  constructor(public activeModal: NgbActiveModal) { }
  delete() {
    this.activeModal.close();
    this.onDelete.emit('DEL');
  }
}

