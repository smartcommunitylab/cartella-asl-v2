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
  @Input() enteResponsabile;
  @Output() onAbilita = new EventEmitter<string>();

  constructor(public activeModal: NgbActiveModal) {
    console.log();
  }

  invia() {
    this.activeModal.close();
    this.onAbilita.emit('ABILITA');
  }
}

