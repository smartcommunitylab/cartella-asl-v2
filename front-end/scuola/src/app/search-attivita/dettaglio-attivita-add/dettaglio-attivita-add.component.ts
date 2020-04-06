import { Component, Output, Input, EventEmitter } from '@angular/core';
import { NgbModal, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DataService } from '../../core/services/data.service';
import * as moment from 'moment';
import { OnInit } from '@angular/core/src/metadata/lifecycle_hooks';
import { PianoAlternanza } from '../../shared/classes/PianoAlternanza.class';
import { Competenza } from '../../shared/classes/Competenza.class';
import { GrowlerService, GrowlerMessageType } from '../../core/growler/growler.service';

@Component({
  selector: 'dettaglio-attivita-add-modal',
  templateUrl: './dettaglio-attivita-add.html',
  styleUrls: ['./dettaglio-attivita-add.scss']
})
export class ProgrammaAddAttivitaModal implements OnInit {
  
  
  

  ngOnInit(): void {
    this.dataInizio = moment(this.opportunita.dataInizio).format('DD-MM-YYYY');
    this.dataFine = moment(this.opportunita.dataFine).format('DD-MM-YYYY');
    this.oreTotali = this.opportunita.ore;
    this.referenteFormazione = this.opportunita.referenteFormazione;
    this.titolo = this.opportunita.titolo;
    //TODO: Uncomment this to enable date checks, at the moment useless for testing purpose
    /*this.datePickerConfig.min = moment(this.opportunita.dataInizio);
    this.datePickerConfig.max = moment(this.opportunita.dataFine);*/
    if (this.piano && this.piano.competenze)
      this.competenzePiano = this.piano.competenze;
    // console.log(this.competenzePiano);
    this.competenzeAttivita = this.opportunita.competenze;
    this.checkedCompetenze = this.checkedCompetenze.concat(this.competenzeAttivita);

    let tmp;

    this.competenzeAttivita.forEach(competenza => {
      tmp = this.competenzePiano.findIndex((comp) => comp.id == competenza.id);
      if(tmp != -1) {
        this.competenzePiano.splice(tmp, 1)
      }
    });

    this.listCompetenze = this.competenzeAttivita.concat(this.competenzePiano);


    /*this.competenzeAttivita = this.opportunita.competenze;

    let temp = [];

    this.competenzeAttivita.forEach(competenza => {
      this.competenzePiano.forEach(comp => {
        if(comp.id == competenza.id) {
          temp.push(comp);
        }
      })
    })

    temp.forEach(competenza => {
      for (var i = 0; i < this.competenzePiano.length; i++) {
        if (this.competenzePiano[i].id == competenza.id) {
          this.competenzePiano.splice(i, 1);
        }
      }
    })*/

  
    
    // console.log(this.competenzePiano);
    // console.log(this.competenzeAttivita);
    // console.log(this.listCompetenze);
  }
  closeResult: string;
  datePickerConfig = {
    locale: 'it',
    firstDayOfWeek: 'mo',
    min: undefined,
    max: undefined
  };
  @Input() opportunita: any;
  @Input() piano: PianoAlternanza
  @Input() tipologia;
  @Output() onAdd = new EventEmitter();
  @Input() oppTipologiaObj;
  dataInizio;
  dataFine;
  oreTotali;
  titolo;
  competenzeToggle = false;
  competenzePiano : Array<Competenza> = [];
  competenzeAttivita = [];
  listCompetenze = [];
  checkedCompetenze = [];
  referenteFormazione;
  referenteFormazioneCF: string;
  forceErrorDisplay: boolean = false;
  constructor(public activeModal: NgbActiveModal, private modalService: NgbModal, private dataService: DataService,  private growler: GrowlerService) {
  }

  add() {
    
    if (this.allValidated()) {
      this.opportunita.dataInizio = moment(this.dataInizio, 'DD-MM-YYYY').valueOf();
      this.opportunita.dataFine = moment(this.dataFine, 'DD-MM-YYYY').valueOf();
      this.opportunita.ore = this.oreTotali; 
      this.opportunita.referenteFormazione = this.referenteFormazione;
      this.opportunita.referenteFormazioneCF = this.referenteFormazioneCF;
      this.opportunita.titolo = this.titolo;
      this.opportunita.competenze = this.checkedCompetenze;
      this.onAdd.emit(this.opportunita);
      this.activeModal.close();
    } else {
      //probably never used: allValidated decides disabled status of save
      this.forceErrorDisplay = true;
      this.growler.growl('Compila i campi necessari', GrowlerMessageType.Warning);
    }
    
  }

  toggleChange() {
    this.competenzeToggle = !this.competenzeToggle;
  }

  allValidated() {
    return this.referenteFormazione;
  }

  competenzaChange(competenza) :void {
    if (this.checkedCompetenze.length == 0) {
      this.checkedCompetenze.push(competenza);
      console.log(this.checkedCompetenze);
      return;
    }
    var check = null;
    this.checkedCompetenze.forEach(comp => {
      if(competenza.id == comp.id) {
        check = comp;
      }
    })
    if (check == null) {
      this.checkedCompetenze.push(competenza);
      console.log(this.checkedCompetenze);
      return;
    }
    this.checkedCompetenze.splice(this.checkedCompetenze.indexOf(check), 1);
    console.log(this.checkedCompetenze);
  }

  openDetailCompetenza(competenza, $event) {
    // $event.stopPropagation();
    // const modalRef = this.modalService.open(CompetenzaDetailModalComponent, { size: "lg" });
    // modalRef.componentInstance.competenza = competenza;
  }
}

