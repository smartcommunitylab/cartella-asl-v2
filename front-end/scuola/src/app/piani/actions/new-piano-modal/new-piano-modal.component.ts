import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DataService } from '../../../core/services/data.service'
@Component({
  selector: 'cm-new-piano-modal',
  templateUrl: './new-piano-modal.component.html',
  styleUrls: ['./new-piano-modal.component.scss']
})
export class NewPianoModalComponent implements OnInit {

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

  constructor(public activeModal: NgbActiveModal, public dataService: DataService) { }

  ngOnInit() {
    this.annoRiferimento = this.dataService.schoolYear;
    
  }

  create() { //create or update
    let piano;
    let year = this.annoRiferimento.substring(0, 4);
    if (this.allValidated()) {
      console.log(this.corsoStudioId);
      piano = {
        titolo: this.titolo,
        corsoDiStudioId: this.corsoStudioId,
        corsoDiStudio: this.corsiStudio.find(corso => corso.courseId == this.corsoStudioId).nome,
        note: this.note,
        oreTerzoAnno: this.monte3anno,
        oreQuartoAnno: this.monte4anno,
        oreQuintoAnno: this.monte5anno,
        // dataAttivazione: new Date(year + "-09").getTime() //starts from september
      }
      this.newPianoListener.emit(piano);
      this.activeModal.dismiss('create')
    } else {
      this.forceErrorDisplay = true;
    }    
  }


  allValidated() {
    return (
      (this.titolo && this.titolo != '')
      && (this.monte3anno && this.monte3anno > 0)
      && (this.monte4anno && this.monte4anno > 0)
      && (this.monte5anno && this.monte5anno > 0)
      && (this.annoRiferimento && this.annoRiferimento != '')
      && (this.corsoStudioId && this.corsoStudioId != 'Corso di studio')
      );
  }

}
