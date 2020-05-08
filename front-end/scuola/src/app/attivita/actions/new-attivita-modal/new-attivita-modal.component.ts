import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'cm-attivita-modal',
  templateUrl: './new-attivita-modal.component.html',
  styleUrls: ['./new-attivita-modal.component.scss']
})
export class NewAttivtaModal implements OnInit {

  titolo: string;
  annoRiferimento: any;
  monte3anno: number;
  monte4anno: number;
  monte5anno: number;
  note: string = '';
  fieldsError: string;
  corsoStudioId: any = 'Corso di studio';
    
  @Input() corsiStudio?: any; //list of corsi studio
  @Output() newPianoListener = new EventEmitter<Object>();
  forceErrorDisplay: boolean;

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit() {
  }

  create(option) {
    
    if (option == 1) {
      this.activeModal.dismiss('Crea nuova attività a partire da un’offerta');;
      this.newPianoListener.emit(1);  
    } else {
      this.activeModal.dismiss('Crea nuova attività');
      this.newPianoListener.emit(2);
    }

  }

}
