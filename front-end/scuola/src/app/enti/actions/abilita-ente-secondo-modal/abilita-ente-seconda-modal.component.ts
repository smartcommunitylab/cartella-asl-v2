import { Component, Output, Input, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'abilita-ente-seconda-modal',
  templateUrl: './abilita-ente-seconda-modal.html',
  styleUrls: ['./abilita-ente-seconda-modal.scss']
})
export class AbilitaEnteSecondaModal {
  closeResult: string;

  @Input() ente;
  @Output() onAbilita = new EventEmitter<string>();

  constructor(public activeModal: NgbActiveModal) { }
  invia() {
    this.activeModal.close();
    this.onAbilita.emit('ABILITA');
  }
}

