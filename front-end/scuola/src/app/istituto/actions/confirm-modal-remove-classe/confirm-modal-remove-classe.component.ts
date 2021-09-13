import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal/modal-ref';

@Component({
  selector: 'cm-confirm-modal-remove-classe',
  templateUrl: './confirm-modal-remove-classe.component.html'
})
export class ConfirmModalRemoveClasseComponent implements OnInit {

  @Input() nomeAccount;
  @Output() actionListener = new EventEmitter<Object>();

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit() {
      
  }

  ok() {
    this.activeModal.close();
    this.actionListener.emit('ok')
  }

  

}
