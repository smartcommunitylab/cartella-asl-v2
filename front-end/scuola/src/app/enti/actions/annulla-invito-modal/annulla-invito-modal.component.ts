import { Component, Output, Input, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'annulla-invito-modal',
  templateUrl: './annulla-invito-modal.html',
  styleUrls: ['./annulla-invito-modal.scss']
})
export class AnnullaInvitoModal {
  closeResult: string;

  @Input() ente;
  @Output() onAnnulla = new EventEmitter<string>();

  constructor(public activeModal: NgbActiveModal) { }

  annullaInvito() {
    this.activeModal.close();
    this.onAnnulla.emit('ANNULLA');
  }

}

