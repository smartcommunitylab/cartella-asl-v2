import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal/modal-ref';

@Component({
  selector: 'cm-confirm-modal-add-classe',
  templateUrl: './confirm-modal-add-classe.component.html'
})
export class ConfirmModalAddClasseComponent implements OnInit {

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
