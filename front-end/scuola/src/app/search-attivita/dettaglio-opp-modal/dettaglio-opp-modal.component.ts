import { Component, Output, Input, EventEmitter } from '@angular/core';
import { NgbModal, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Attivita } from '../../shared/classes/Attivita.class';
import { DataService } from '../../core/services/data.service';

@Component({
  selector: 'dettaglio-opportunita-modal',
  templateUrl: './dettaglio-opportunita-modal.html',
  styleUrls:['./dettaglio-opportunita-modal.scss']
})
export class ProgrammaOpenOpportunitaModal {
  closeResult: string;
  istituto: string;

  @Input() opportunita: any;
  @Input() filtro:any;
  @Input() oppTipologiaObj;

  constructor(public activeModal: NgbActiveModal, private dataService: DataService) {
    this.istituto = this.dataService.getIstitutoName();
   }
  getCompetenzaColor(competenza){
    var color = '#';
    if (this.filtro.competenze.map((competenza => {
      return competenza.idCompetenza      
    })).indexOf(competenza.idCompetenza)>=0){
      color=color+'1c8e00';
    }
    else {
      color=color+'ffffff';
      
    }
    return color;
  }
  getCompetenzaString(competenza) {
    var competenzaString = '(Competenza ';
    if (this.filtro.competenze.indexOf(competenza.id) >= 0){
      competenzaString=competenzaString + 'conforme alla ricerca';
    }
    else {
      competenzaString=competenzaString + 'non conforme alla ricerca'; 
    }
    competenzaString += ')';
    return competenzaString;
  }
}

