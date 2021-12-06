import { Component, Output, Input, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'abilita-ente-prima-modal',
  templateUrl: './abilita-ente-prima-modal.html',
  styleUrls: ['./abilita-ente-prima-modal.scss']
})
export class AbilitaEntePrimaModal {
  closeResult: string;

  @Input() ente;
  @Input() enteResponsabile;
  @Output() onAbilita = new EventEmitter<string>();

  constructor(public activeModal: NgbActiveModal) { }
 
  abilita() {
    this.activeModal.close();
    this.onAbilita.emit('ABILITA');
  }
}

