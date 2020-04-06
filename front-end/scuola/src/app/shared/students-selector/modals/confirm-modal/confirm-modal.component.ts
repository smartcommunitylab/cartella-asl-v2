import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal/modal-ref';

@Component({
  selector: 'cm-confirm-modal',
  templateUrl: './confirm-modal.component.html'
})
export class ConfirmModalComponent implements OnInit {

  @Input() popupText: string;
  @Input() showBody: boolean;
  
  @Output() actionListener = new EventEmitter<Object>();

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit() {
      
  }

  ok() {
    this.activeModal.close();
    this.actionListener.emit('ok')
  }

  

}
