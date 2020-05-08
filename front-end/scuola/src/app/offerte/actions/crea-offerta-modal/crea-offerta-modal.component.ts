import { Component, OnInit, Input, Output, EventEmitter, ViewChild } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import * as moment from 'moment';
import { DatePickerComponent } from 'ng2-date-picker';
import { DataService } from '../../../core/services/data.service';
import { Azienda } from '../../../shared/classes/Azienda.class';
import { Observable, of } from 'rxjs';
import { catchError, debounceTime, distinctUntilChanged, map, tap, switchMap } from 'rxjs/operators';

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
  referenteScuola: string;
  tipologia: any = 'Tipologie';
  ore;
  referenteEsterno: string;
  date;
  azienda: any;
  fieldsError: string;
  showSelect: boolean = true;
  aziende: Azienda[];
  forceEnteDisplay: boolean = false;
  pageSize = 20;
  tipoInterna: boolean = true;
  forceErrorDisplay: boolean;
  forceErrorDisplayTitolo: boolean = false;
  forceErrorDisplayOre: boolean = false;
  forceErrorDisplayNumeroPosti: boolean = false;
  forceErrorDisplayRS: boolean = false;
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

  onChange(tipoId) {
    this.tipologie.filter(tipo => {
      if (tipo.id == tipoId) {
        this.tipoInterna = tipo.interna;
      }
    })
  }

  create() { //create or update
    let offerta;
    if (this.allValidated()) {
      offerta = {
        titolo: this.titolo,
        postiDisponibili: this.numeroPosti,
        referenteScuola: this.referenteScuola,
        tipologia: this.tipologia,
        ore: this.ore,
        referenteEsterno: this.tipoInterna ? null : this.referenteEsterno,
        dataInizio: moment(this.date.dataInizio, 'YYYY-MM-DD').valueOf(),
        dataFine: moment(this.date.dataFine, 'YYYY-MM-DD').valueOf(),
        nomeEnte: this.tipoInterna ? null : this.azienda.nome,
        enteId: this.tipoInterna ? null : this.azienda.id
      }
      this.newOffertaListener.emit(offerta);
      this.activeModal.dismiss('create')
    } else {
      this.forceErrorDisplay = true;
      if (this.azienda) {
        this.forceEnteDisplay = false;
      } else {
        this.forceEnteDisplay = true;
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

    if (this.tipoInterna) {
      return (
        (this.titolo && this.titolo != '' && this.titolo.trim().length > 0)
        && (this.numeroPosti && this.numeroPosti > 0)
        && (this.referenteScuola && this.referenteScuola != '' && this.referenteScuola.trim().length > 0)
        && (this.ore && this.ore > 0)
        && (this.tipologia && this.tipologia != 'Tipologie')
        && (this.date.dataInizio && this.date.dataFine && this.date.dataInizio <= this.date.dataFine)
      );
    } else {
      return (
        (this.titolo && this.titolo != '' && this.titolo.trim().length > 0)
        && (this.numeroPosti && this.numeroPosti > 0)
        && (this.referenteScuola && this.referenteScuola != '' && this.referenteScuola.trim().length > 0)
        && (this.referenteEsterno && this.referenteEsterno != '' && this.referenteEsterno.trim().length > 0)
        && (this.ore && this.ore > 0)
        && (this.azienda && this.azienda.id != '')
        && (this.tipologia && this.tipologia != 'Tipologie')
        && (this.date.dataInizio && this.date.dataFine && this.date.dataInizio <= this.date.dataFine)
      );
    }
  }

  trimValue(event, type) {
    if (type == 'titolo') {
      (event.target.value.trim().length == 0) ? this.forceErrorDisplayTitolo = true : this.forceErrorDisplayTitolo = false;
    } else if (type == 'scolastico') {
      (event.target.value.trim().length == 0) ? this.forceErrorDisplayRS = true : this.forceErrorDisplayRS = false;
    } else if (type == 'esterno') {
      (event.target.value.trim().length == 0) ? this.forceErrorDisplayRE = true : this.forceErrorDisplayRE = false;
    }
    event.target.value = event.target.value.trim();
  }

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
          }),
          catchError(() => {
            this.searchFailedAZ = true;
            return of([]);
          }))
      ),
      tap(() => {
        this.searchingAZ = false
      })
    )

  formatter = (x: { nome: string }) => x.nome;

}
