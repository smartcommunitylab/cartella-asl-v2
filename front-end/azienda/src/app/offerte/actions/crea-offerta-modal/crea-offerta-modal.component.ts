import { Component, OnInit, Input, Output, EventEmitter, ViewChild } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import * as moment from 'moment';
import { DatePickerComponent } from 'ng2-date-picker';
import { DataService } from '../../../core/services/data.service';

@Component({
  selector: 'crea-offerta-modal',
  templateUrl: './crea-offerta-modal.component.html',
  styleUrls: ['./crea-offerta-modal.component.scss']
})
export class CreaOffertaModalComponent implements OnInit {

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

  titolo: string;
  numeroPosti;
  oraInizio;
  oraFine;
  tipologia: any = 'Tipologie';
  ore;
  referenteEsterno: string;
  date;
  fieldsError: string;
  showSelect: boolean = true;
  forceEnteDisplay: boolean = false;
  pageSize = 20;
  forceErrorDisplay: boolean;
  forceErrorDisplayTitolo: boolean = false;
  forceErrorDisplayOre: boolean = false;
  forceErrorDisplayNumeroPosti: boolean = false;
  forceDalle23ErrorDisplay: boolean = false;
  forceErrorDisplayOraInizio: boolean = false;
  forceAlle23ErrorDisplay: boolean = false;
  forceErrorDisplayOraFine: boolean = false;
  forceDalleAlleErrorDisplay: boolean = false;
  forceErrorDisplayRE: boolean = false;

  datePickerConfig = {
    locale: 'it',
    firstDayOfWeek: 'mo'
  };

  @Input() tipologie?: any;
  @Output() newOffertaListener = new EventEmitter<Object>();
  
  constructor(public activeModal: NgbActiveModal, private dataService: DataService) { }

  ngOnInit() {
    this.date = {
      dataInizio: moment(),
      dataFine: moment()
    }
  }

  create() { //create or update
    let offerta;
    if (this.allValidated()) {
      offerta = {
        titolo: this.titolo,
        postiDisponibili: this.numeroPosti,
        tipologia: this.tipologia,
        ore: this.ore,
        oraInizio: this.oraInizio,
        oraFine: this.oraFine,
        referenteEsterno: this.referenteEsterno,
        dataInizio: moment(this.date.dataInizio, 'YYYY-MM-DD').valueOf(),
        dataFine: moment(this.date.dataFine, 'YYYY-MM-DD').valueOf(),
        nomeEnte: this.dataService.aziendaName,
        enteId: this.dataService.aziendaId
      }
      this.newOffertaListener.emit(offerta);
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
      if (this.ore < 1) {
        this.forceErrorDisplayOre = true;
      } else {
        this.forceErrorDisplayOre = false;
      }
      if (this.numeroPosti < 1) {
        this.forceErrorDisplayNumeroPosti = true;
      } else {
        this.forceErrorDisplayNumeroPosti = false;
      }
    }
  }

  allValidated() {
      return (
        (this.titolo && this.titolo != '' && this.titolo.trim().length > 0)
        && (this.numeroPosti && this.numeroPosti > 0)
        && (this.referenteEsterno && this.referenteEsterno != '' && this.referenteEsterno.trim().length > 0)
        && (this.ore && this.ore > 0)
        && (this.oraInizio && this.oraInizio>0 && this.oraFine && this.oraFine>0)
        && (this.tipologia && this.tipologia != 'Tipologie')
        && (this.date.dataInizio && this.date.dataFine && this.date.dataInizio <= this.date.dataFine)
      );
  }

  trimValue(event, type) {
    if (type == 'titolo') {
      (event.target.value.trim().length == 0) ? this.forceErrorDisplayTitolo = true : this.forceErrorDisplayTitolo = false;
    } else if (type == 'esterno') {
      (event.target.value.trim().length == 0) ? this.forceErrorDisplayRE = true : this.forceErrorDisplayRE = false;
    } else if(type == 'trim'){
      event.target.value = event.target.value.trim(); 
    } 
  }

}
