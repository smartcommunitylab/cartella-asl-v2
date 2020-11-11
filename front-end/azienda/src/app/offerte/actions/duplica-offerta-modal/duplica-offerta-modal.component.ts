import { Component, OnInit, Input, Output, EventEmitter, ViewChild } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import * as moment from 'moment';
import { DatePickerComponent } from 'ng2-date-picker';
import { DataService } from '../../../core/services/data.service';

@Component({
  selector: 'duplica-offerta-modal',
  templateUrl: './duplica-offerta-modal.component.html',
  styleUrls: ['./duplica-offerta-modal.component.scss']
})
export class DuplicaOffertaModalComponent implements OnInit {

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

  // titolo: string;
  // numeroPosti;
  // oraInizio;
  // oraFine;
  tipologia: any = 'Tipologia';
  // ore;
  // referenteEsterno: string;
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

  @Input() offerta?: any;
  @Input() tipologie?: any;
  @Output() onDupicateConfirm = new EventEmitter<Object>();
  
  constructor(public activeModal: NgbActiveModal, private dataService: DataService) { }

  ngOnInit() {
    this.tipologia = this.offerta.tipologia;
    this.date = {
      dataInizio: moment(this.offerta.dataInizio),
      dataFine: moment(this.offerta.dataFine)
    }
  }

  create() { 
    if (this.allValidated()) {
      let duplicaOfferta = {
        titolo: this.offerta.titolo,
        postiDisponibili: this.offerta.postiDisponibili,
        tipologia: this.tipologia,
        ore: this.offerta.ore,
        oraInizio: this.offerta.oraInizio,
        oraFine: this.offerta.oraFine,
        referenteEsterno: this.offerta.referenteEsterno,
        dataInizio: moment(this.date.dataInizio, 'YYYY-MM-DD').valueOf(),
        dataFine: moment(this.date.dataFine, 'YYYY-MM-DD').valueOf(),
        nomeEnte: this.dataService.aziendaName,
        enteId: this.dataService.aziendaId,
        annoScolastico: this.dataService.getAnnoScolstico(moment(this.date.dataInizio))
      }
      this.onDupicateConfirm.emit(duplicaOfferta);
      this.activeModal.dismiss('duplicate')
    } else {
      this.forceErrorDisplay = true;
      if (!this.offerta.oraInizio) {
        this.forceErrorDisplayOraInizio = true;
      } else {
        this.forceErrorDisplayOraInizio = false;
      }
      if (this.offerta.oraInizio > 23) {
        this.forceDalle23ErrorDisplay = true;
      } else {
        this.forceDalle23ErrorDisplay = false;
      }
      if (!this.offerta.oraFine) {
        this.forceErrorDisplayOraFine = true;
      } else {
        this.forceErrorDisplayOraFine = false;
      }
      if (this.offerta.oraFine > 23) {
        this.forceAlle23ErrorDisplay = true;
      } else {
        this.forceAlle23ErrorDisplay = false;
      }
      if (this.offerta.ore < 1) {
        this.forceErrorDisplayOre = true;
      } else {
        this.forceErrorDisplayOre = false;
      }
      if (this.offerta.postiDisponibili < 1) {
        this.forceErrorDisplayNumeroPosti = true;
      } else {
        this.forceErrorDisplayNumeroPosti = false;
      }
    }
  }

  allValidated() {
      return (
        (this.offerta.titolo && this.offerta.titolo != '' && this.offerta.titolo.trim().length > 0)
        && (this.offerta.postiDisponibili && this.offerta.postiDisponibili > 0)
        && (this.offerta.referenteEsterno && this.offerta.referenteEsterno != '' && this.offerta.referenteEsterno.trim().length > 0)
        && (this.offerta.ore && this.offerta.ore > 0)
        && (this.offerta.oraInizio && this.offerta.oraInizio>0 && this.offerta.oraFine && this.offerta.oraFine>0)
        && (this.tipologia && this.tipologia != 'Tipologia')
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
