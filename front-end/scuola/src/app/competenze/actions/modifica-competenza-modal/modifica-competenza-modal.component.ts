import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'modifica-competenza-modal',
  templateUrl: './modifica-competenza-modal.component.html',
  styleUrls: ['./modifica-competenza-modal.component.scss']
})
export class ModificaCompetenzaModalComponent implements OnInit {

  definizione: string;
  livelloEQF;
  fieldsError: string;
  @Input() competenza?: any;
  @Output() editCompetenzeListener = new EventEmitter<Object>();
  forceErrorDisplay: boolean;


  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit() {
  }

  edit() { //create or update
    if (this.allValidated()) {
      this.editCompetenzeListener.emit(this.competenza);
      this.activeModal.dismiss('edit')
    } else {
      this.forceErrorDisplay = true;
    }
  }


  allValidated() {
    return (
      (this.competenza.titolo || this.competenza.titolo != '')
      && (this.competenza.livelloEQF || this.competenza.livelloEQF != '')
    );
  }

}
