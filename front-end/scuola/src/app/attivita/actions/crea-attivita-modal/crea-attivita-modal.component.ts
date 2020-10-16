import { Component, OnInit, Input, Output, EventEmitter, ViewChild } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import * as moment from 'moment';
import { DatePickerComponent } from 'ng2-date-picker';
import { DataService } from '../../../core/services/data.service';
import { Azienda } from '../../../shared/classes/Azienda.class';
import { Observable, of} from 'rxjs';
import { catchError, debounceTime, distinctUntilChanged, map, tap, switchMap} from 'rxjs/operators';

@Component({
  selector: 'crea-attivita-modal',
  templateUrl: './crea-attivita-modal.component.html',
  styleUrls: ['./crea-attivita-modal.component.scss']
})
export class CreaAttivitaModalComponent implements OnInit {

  @ViewChild('calendarStart') calendarStart: DatePickerComponent;
  @ViewChild('calendarEnd') calendarEnd: DatePickerComponent;
 
  openStart() {
    this.calendarStart.api.open();
  }

  closeStart() {
    this.calendarStart.api.close();
  }

  openEnd() {
    this.calendarEnd.api.open();
  }

  closeEnd() {
    this.calendarEnd.api.close();
  }

  keyword = 'nome';
  titolo: string;
  referenteScuola: string;
  referenteEsterno: string;
  ore;
  oraInizio;
  oraFine;
  fieldsError: string;
  tipologia: any = 'Tipologie';
  date;
  showSelect: boolean = true;
  aziende: Azienda[];
  azienda: any;
  filteredAziende: Azienda[] = [];
  forceEnteDisplay: boolean = false;
  pageSize = 20;
  tipoInterna: boolean = true;
  schoolYear;

  @Input() tipologie?: any;
  @Output() newAttivitaListener = new EventEmitter<Object>();
  forceErrorDisplay: boolean;
  forceErrorDisplayTitolo: boolean = false;
  forceErrorDisplayRS: boolean = false;
  forceErrorDisplayRE: boolean = false;
  forceDalle23ErrorDisplay: boolean = false;
  forceErrorDisplayOraInizio: boolean = false;
  forceAlle23ErrorDisplay: boolean = false;
  forceErrorDisplayOraFine: boolean = false;

  datePickerConfig = {
    locale: 'it',
    firstDayOfWeek: 'mo'
  };

  constructor(public activeModal: NgbActiveModal, private dataService: DataService) { }

  ngOnInit() {
    this.date = {
      dataInizio: moment(),
      dataFine: moment()
    }
    this.schoolYear = this.dataService.schoolYear;
    // this.dataService.getListaAziende(null, 0, this.pageSize).subscribe((response) => {
    //   this.aziende = response.content;
    // });
    
  }

  onChange(tipoId) {
    this.tipologie.filter(tipo => {
      if (tipo.id == tipoId) {
        this.tipoInterna = tipo.interna;
      }
    })
  }

  myHandler() {
    this.dataService.getSchoolYear(this.dataService.istitutoId,
      moment(this.date.dataInizio, 'YYYY-MM-DD').valueOf()).subscribe((resp => { 
      this.schoolYear = resp.schoolYear;
    }))
  }

  create() { //create or update
    let attivita;
    if (this.allValidated()) {
      attivita = {
        titolo: this.titolo.trim(),
        referenteScuola: this.referenteScuola.trim(),
        referenteEsterno: this.tipoInterna?null:this.referenteEsterno.trim(),
        tipologia: this.tipologia,
        annoScolastico: this.schoolYear,
        ore: this.ore,
        oraInizio: this.oraInizio,
        oraFine: this.oraFine,
        nomeEnte: this.tipoInterna?null:this.azienda.nome,
        enteId: this.tipoInterna?null:this.azienda.id,
        dataInizio: moment(this.date.dataInizio, 'YYYY-MM-DD').valueOf(),
        dataFine: moment(this.date.dataFine, 'YYYY-MM-DD').valueOf()
      }
      this.newAttivitaListener.emit(attivita);
      this.activeModal.dismiss('create')
    } else {
      this.forceErrorDisplay = true;
      if (!this.oraInizio) {
        this.forceErrorDisplayOraInizio = true;
      } else {
        this.forceErrorDisplayOraInizio = false;
      }
      if (this.oraInizio > 23) {
        this.forceDalle23ErrorDisplay = true;
      } else {
        this.forceDalle23ErrorDisplay = false;
      }
      if (!this.oraFine) {
        this.forceErrorDisplayOraFine = true;
      } else {
        this.forceErrorDisplayOraFine = false;
      }
      if (this.oraFine > 23) {
        this.forceAlle23ErrorDisplay = true;
      } else {
        this.forceAlle23ErrorDisplay = false;
      }
      if (this.azienda) {
        this.forceEnteDisplay = false;
      } else {
        this.forceEnteDisplay = true;
      }
    }
  }


  allValidated() {

    if (this.tipoInterna) {
      return (
        (this.titolo && this.titolo != '' && this.titolo.trim().length > 0)
        && (this.referenteScuola && this.referenteScuola != '' && this.referenteScuola.trim().length > 0)
        && (this.ore && this.ore > 0)
        && (this.oraInizio && this.oraInizio>0 && this.oraFine && this.oraFine>0)
        && (this.tipologia && this.tipologia != 'Tipologie')
        && (this.date.dataInizio && this.date.dataFine && this.date.dataInizio <= this.date.dataFine)
      );  
    } else {
      return (
        (this.titolo && this.titolo != '' && this.titolo.trim().length > 0)
        && (this.referenteScuola && this.referenteScuola != '' && this.referenteScuola.trim().length > 0)
        && (this.referenteEsterno && this.referenteEsterno != '' && this.referenteEsterno.trim().length > 0)
        && (this.ore && this.ore > 0)
        && (this.oraInizio && this.oraInizio>0 && this.oraFine && this.oraFine>0)
        && (this.azienda.id != '')
        && (this.tipologia && this.tipologia != 'Tipologie')
        && (this.date.dataInizio && this.date.dataFine && this.date.dataInizio <= this.date.dataFine)
      );
    }
  }
  
  trimValue(event, type) { 
    if(type == 'titolo'){
      (event.target.value.trim().length == 0) ? this.forceErrorDisplayTitolo = true : this.forceErrorDisplayTitolo = false;
    } else if(type == 'scolastico'){
      (event.target.value.trim().length == 0) ? this.forceErrorDisplayRS = true : this.forceErrorDisplayRS = false;
    } else if(type == 'esterno'){
      (event.target.value.trim().length == 0) ? this.forceErrorDisplayRE = true : this.forceErrorDisplayRE = false;
    }else if(type == 'trim'){
      event.target.value = event.target.value.trim(); 
    } 
  }
  // search = (text$: Observable<string>) =>
  //   text$.pipe(
  //     debounceTime(200),
  //     distinctUntilChanged(),
  //     map(term => {
  //       if (parseInt("0" + term, 10) > 0) {
  //         return this.aziende.filter(az => az.partita_iva.indexOf(term) > -1).slice(0, 10);
  //       } else {
  //         return this.aziende.filter(az => az.nome.toUpperCase().indexOf(term.toUpperCase()) > -1).slice(0, 10);
  //       }
  //     })      
  //   )
  

  searchingAZ = false;
  searchFailedAZ = false;
  search = (text$: Observable<string>) =>
    text$.pipe(
      debounceTime(500),
      distinctUntilChanged(),
      tap(() => this.searchingAZ = true),
      switchMap(term =>
        this.dataService.searchEnte(term).pipe(
          tap(() => {
            this.searchFailedAZ = false
          } ),
          catchError(() => {
            this.searchFailedAZ = true;
            return of([]);
          }))
      ),
      tap(() => {
        this.searchingAZ = false
      })
    )
  
    formatter = (x: {nome: string}) => x.nome;

}
