import { Component, Output, Input, EventEmitter, OnInit } from '@angular/core';
import { NgbModal, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { PianoAlternanza } from '../../../shared/classes/PianoAlternanza.class';
@Component({
  selector: 'cm-duplica-piano-modal',
  templateUrl: './duplica-piano-modal.component.html',
  styleUrls: ['./duplica-piano-modal.component.scss']
})
export class DuplicaPianoModal implements OnInit {

  annoRiferimento: any;
  fieldsError: string;

  @Input() corsiStudio?: any;
  @Input() piano?: any;
  @Output() onDupicateConfirm = new EventEmitter<Object>();
  forceErrorDisplay: boolean;
  
  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit() {
    var d = new Date(this.piano.dataAttivazione);
    this.annoRiferimento = d.getFullYear() + 1; 
  }

  update() { //update

    if (this.allValidated()) {
      this.piano.dataAttivazione = null;
      this.piano.dataCreazione = null;
      this.piano.dataDisattivazione = null;
      this.piano.dataScadenza = null;
      this.onDupicateConfirm.emit(this.piano);
      this.activeModal.dismiss('duplica')
    } else {
      this.forceErrorDisplay = true;
    }
  }


  allValidated() {
    if (this.piano.corsoSperimentale) {
      return (
        (this.piano.titolo || this.piano.titolo != '')
        && (this.piano.oreSecondoAnno || this.piano.oreSecondoAnno > 0)
        && (this.piano.oreTerzoAnno || this.piano.oreTerzoAnno > 0)
        && (this.piano.oreQuartoAnno || this.piano.oreQuartoAnno > 0)
        && (this.annoRiferimento || this.annoRiferimento != '')
        && (this.piano.corsoDiStudioId && this.piano.corsoDiStudioId != 'Corso di studio')
      );
    } else {
      return (
        (this.piano.titolo || this.piano.titolo != '')
        && (this.piano.oreTerzoAnno || this.piano.oreTerzoAnno > 0)
        && (this.piano.oreQuartoAnno || this.piano.oreQuartoAnno > 0)
        && (this.piano.oreQuintoAnno || this.piano.oreQuintoAnno > 0)
        && (this.annoRiferimento || this.annoRiferimento != '')
        && (this.piano.corsoDiStudioId && this.piano.corsoDiStudioId != 'Corso di studio')
      );
    }    
  }

}
