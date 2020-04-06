import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'cm-edit-piano-modal',
  templateUrl: './edit-piano-modal.component.html',
  styleUrls: ['./edit-piano-modal.component.scss']
})
export class EditPianoModalComponent implements OnInit {

  annoRiferimento: any;
  fieldsError: string;
  
  @Input() corsiStudio?: any;
  @Input() piano?: any;
  @Output() editPianoListener = new EventEmitter<Object>();
  forceErrorDisplay: boolean;

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit() {
    var d = new Date(this.piano.dataAttivazione);
    this.annoRiferimento = d.getFullYear();  
  }

  update() { //update
    
    if (this.allValidated()) {
      this.piano.dataAttivazione = new Date(this.annoRiferimento + "-09").getTime();
      this.editPianoListener.emit(this.piano);
      this.activeModal.dismiss('edit')
    } else {
      this.forceErrorDisplay = true;
    }    
  }


  allValidated() {
    return (
      (this.piano.titolo && this.piano.titolo != '')
      && (this.piano.oreTerzoAnno && this.piano.oreTerzoAnno > 0)
      && (this.piano.oreQuartoAnno && this.piano.oreQuartoAnno > 0)
      && (this.piano.oreQuintoAnno && this.piano.oreQuintoAnno > 0)
      && (this.annoRiferimento && this.annoRiferimento != '')
      && (this.piano.corsoDiStudioId && this.piano.corsoDiStudioId != 'Corso di studio')    
      );
  }

}
