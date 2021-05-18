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
  monte2anno: number;
  monte3anno: number;
  monte4anno: number;
  monte5anno: number;
  note: string = '';
  fieldsError: string;
  corsoStudioId: any = 'Corso di studio';
  quadriennali: boolean = false;
    
  @Input() corsiStudio?: any; //list of corsi studio
  @Output() newPianoListener = new EventEmitter<Object>();
  forceErrorDisplay: boolean;
  forceErrorDisplayTitolo: boolean = false;

  constructor(public activeModal: NgbActiveModal, public dataService: DataService) { }

  ngOnInit() {
    this.annoRiferimento = this.dataService.schoolYear;
    
  }

  onChange(corsoId) {
    console.log(this.corsiStudio.find(corso => corso.courseId == corsoId).nome);
    let corsoSperimentale = this.corsiStudio.find(corso => corso.courseId == corsoId).corsoSperimentale;
    if (corsoSperimentale) {
      this.quadriennali = true;
    } else {
      this.quadriennali = false;
    }
  }

  create() { //create or update
    let piano;
    let year = this.annoRiferimento.substring(0, 4);
    if (this.allValidated()) {
      console.log(this.corsoStudioId);
      if (!this.quadriennali) {
        piano = {
          titolo: this.titolo.trim(),
          corsoDiStudioId: this.corsoStudioId,
          corsoDiStudio: this.corsiStudio.find(corso => corso.courseId == this.corsoStudioId).nome,
          note: this.note,
          oreTerzoAnno: this.monte3anno,
          oreQuartoAnno: this.monte4anno,
          oreQuintoAnno: this.monte5anno,
        }
      } else {
        piano = {
          titolo: this.titolo.trim(),
          corsoDiStudioId: this.corsoStudioId,
          corsoDiStudio: this.corsiStudio.find(corso => corso.courseId == this.corsoStudioId).nome,
          note: this.note,
          oreSecondoAnno: this.monte2anno,
          oreTerzoAnno: this.monte3anno,
          oreQuartoAnno: this.monte4anno,
        }
      }
      
      this.newPianoListener.emit(piano);
      this.activeModal.dismiss('create')
    } else {
      this.forceErrorDisplay = true;
    }    
  }


  allValidated() {
    
    if (!this.quadriennali) {
      return (
        (this.titolo && this.titolo != '' && this.titolo.trim().length > 0)
        && (this.monte3anno && this.monte3anno > 0)
        && (this.monte4anno && this.monte4anno > 0)
        && (this.monte5anno && this.monte5anno > 0)
        && (this.annoRiferimento && this.annoRiferimento != '')
        && (this.corsoStudioId && this.corsoStudioId != 'Corso di studio')
        );
    } else {
      return (
        (this.titolo && this.titolo != '' && this.titolo.trim().length > 0)
        && (this.monte2anno && this.monte2anno > 0)
        && (this.monte3anno && this.monte3anno > 0)
        && (this.monte4anno && this.monte4anno > 0)
        && (this.annoRiferimento && this.annoRiferimento != '')
        && (this.corsoStudioId && this.corsoStudioId != 'Corso di studio')
        );
    }
    
  }
  
  trimValue(event, type) {
    if(type == 'titolo'){
      (event.target.value.trim().length == 0)? this.forceErrorDisplayTitolo = true : this.forceErrorDisplayTitolo = false;
    } else if(type == 'trim'){
      event.target.value = event.target.value.trim(); 
    }
  }

}
