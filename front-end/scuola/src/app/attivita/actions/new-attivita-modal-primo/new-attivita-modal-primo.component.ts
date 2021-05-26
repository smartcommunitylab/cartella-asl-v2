import { Component, Output, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'cm-attivita-modal-primo',
  templateUrl: './new-attivita-modal-primo.component.html',
  styleUrls: ['./new-attivita-modal-primo.component.scss']
})
export class NewAttivtaModalPrimo {

  @Output() modalitaListener = new EventEmitter<Object>();
  
  constructor(public activeModal: NgbActiveModal) { }

  create(option) {
    
    if (option == 1) {
      this.activeModal.dismiss(1);
      //rendicontazione corpo
      this.modalitaListener.emit(1);  
    } else {
      this.activeModal.dismiss(2);
      //rendicontazione ore giornaliera
      this.modalitaListener.emit(2);
    }

  }

}
