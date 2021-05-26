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
      this.activeModal.dismiss('Crea nuova attività a partire da un’offerta');;
      this.modalitaListener.emit(1);  
    } else {
      this.activeModal.dismiss('Crea nuova attività');
      this.modalitaListener.emit(2);
    }

  }

}
