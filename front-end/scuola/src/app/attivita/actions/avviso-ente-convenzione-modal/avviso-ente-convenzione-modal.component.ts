import { Component, Output, Input, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'avviso-ente-convenzione-modal',
  templateUrl: './avviso-ente-convenzione-modal.html',
  styleUrls: ['./avviso-ente-convenzione-modal.scss']
})
export class AvvisoEnteConvenzioneModal {
  closeResult: string;

  @Input() attivita;
  @Input() convAttiva;
  @Output() onClose = new EventEmitter<Object>();;

  constructor(public activeModal: NgbActiveModal) { }

  close() {
    this.activeModal.close();
    this.onClose.emit('VIEWED');
  }
 
}

