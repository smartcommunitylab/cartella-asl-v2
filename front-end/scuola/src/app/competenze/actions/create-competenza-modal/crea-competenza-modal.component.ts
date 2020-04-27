import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'crea-competenza-modal',
  templateUrl: './crea-competenza-modal.component.html',
  styleUrls: ['./crea-competenza-modal.component.scss']
})
export class CreaCompetenzaModalComponent implements OnInit {

  definizione: string;
  livelloEQF: any = "livelloEQF";
  fieldsError: string;
  @Output() newCompetenzeListener = new EventEmitter<Object>();
  forceErrorDisplay: boolean;
  forceErrorDisplayTitolo: boolean = false;
  arrEQF=[1,2,3,4,5,6,7,8];

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit() {
  }

  create() { //create or update
    let competenza;
    if (this.allValidated()) {
      competenza = {
        titolo: this.definizione.trim(),
        livelloEQF: this.livelloEQF
      }
      this.newCompetenzeListener.emit(competenza);
      this.activeModal.dismiss('create')
    } else {
      this.forceErrorDisplay = true;
    }
  }


  allValidated() {
    return (
      (this.definizione && this.definizione != '' && this.definizione.trim().length > 0)
      && (this.livelloEQF && this.livelloEQF != 'livelloEQF')
    );
  }

  trimValue(event, type) { 
    if(type == 'titolo'){
      (event.target.value.trim().length == 0)? this.forceErrorDisplayTitolo = true : this.forceErrorDisplayTitolo = false;
    } else if(type == 'trim'){
      event.target.value = event.target.value.trim(); 
    }
  }

  onChange(eqf){
    this.livelloEQF= eqf;
  }

}
