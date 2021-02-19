import { Component, OnInit, Input, Output, EventEmitter, ViewChild } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import * as moment from 'moment';
import { DatePickerComponent } from 'ng2-date-picker';
import { DataService } from '../../../core/services/data.service';
import { Azienda } from '../../../shared/classes/Azienda.class';
import { Observable, of } from 'rxjs';
import { catchError, debounceTime, distinctUntilChanged, tap, switchMap } from 'rxjs/operators';

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
  oraInizio = '07';
  oraFine = '18';
  fieldsError: string;
  tipologia: any = 'Tipologia';
  date: {
    dataInizio: moment.Moment,
    dataFine: moment.Moment,
    maxFine: moment.Moment
  } = {
    dataInizio: null,
    dataFine: null,
    maxFine: null
  };
  showSelect: boolean = true;
  aziende: Azienda[];
  azienda: any;
  filteredAziende: Azienda[] = [];
  forceEnteDisplay: boolean = false;
  pageSize = 20;
  tipoInterna: boolean = true;
  schoolYear: string;
  schoolYears: string[] = [];
  orari = ['00', '01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23']
  @Input() tipologie?: any;
  @Output() newAttivitaListener = new EventEmitter<Object>();
  forceErrorDisplay: boolean;
  forceErrorDisplayTitolo: boolean = false;
  forceErrorDisplayRS: boolean = false;
  forceErrorDisplayRE: boolean = false;
  forceErrorDisplayOraInizio: boolean = false;
  forceErrorDisplayOraFine: boolean = false;

  datePickerConfig = {
    locale: 'it',
    firstDayOfWeek: 'mo',
    min: moment().subtract(60, 'months'),
    max: moment().add(36,'months')
  };

  constructor(public activeModal: NgbActiveModal, private dataService: DataService) { }

  ngOnInit() {
    this.date.dataInizio = moment();
    this.date.dataFine = moment();
    this.checkDate();
    this.schoolYear = this.getAnnoScolastico(this.date.dataInizio);
  }

  onChange(tipoId) {
    this.tipologie.filter(tipo => {
      if (tipo.id == tipoId) {
        this.tipoInterna = tipo.interna;
      }
    })
  }

  checkDate() {
    var anno = this.getAnnoScolasticoNum(this.date.dataInizio);
    this.date.maxFine = moment([anno + 1, 8, 30]);
  }

  myHandler() {
    this.checkDate();
    this.schoolYears = [];

    var inizioSettembre = moment([this.date.dataInizio.year(), 8, 1]);
    var fineSettembre = moment([this.date.dataInizio.year(), 9, 1]);
    var annoScolasticoPrec = moment().year(this.date.dataInizio.year() - 1).format('YYYY') + '-' + this.date.dataInizio.format('YY');
    var annoScolasticoSucc = this.date.dataInizio.format('YYYY') + '-' + moment().year(this.date.dataInizio.year() + 1).format('YY');
    if(this.date.dataInizio.isBefore(inizioSettembre)) {
      this.schoolYears.push(annoScolasticoPrec);
    }
    if(this.date.dataFine.isAfter(fineSettembre)) {
      this.schoolYears.push(annoScolasticoSucc);
    }
    if(this.date.dataInizio.isSameOrAfter(inizioSettembre) && this.date.dataFine.isBefore(fineSettembre)) {
      this.schoolYears.push(annoScolasticoPrec);
      this.schoolYears.push(annoScolasticoSucc);
    }
    if(!this.schoolYears.includes(this.schoolYear)) {
      this.schoolYear = this.schoolYears[0].slice();
    }
  }

  getAnnoScolasticoNum(now) {
    var lastDay = moment(now).month(8).date(1);
    if (now.isBefore(lastDay)) {
      return (now.get('year') - 1);
    }
    return now.get('year');
  }

  getAnnoScolastico(now) {
    var annoScolastico;
    var lastDay = moment().month(8).date(1);
    if (now.isBefore(lastDay)) {
      annoScolastico = moment().year(now.year() - 1).format('YYYY') + '-' + now.format('YY');
    } else {
      annoScolastico = now.format('YYYY') + '-' + moment().year(now.year() + 1).format('YY');
    }
    return annoScolastico;
  }

  create() { //create or update
    let attivita;
    if (this.allValidated()) {
      attivita = {
        titolo: this.titolo.trim(),
        referenteScuola: this.referenteScuola.trim(),
        referenteEsterno: this.tipoInterna ? null : this.referenteEsterno.trim(),
        tipologia: this.tipologia,
        annoScolastico: this.schoolYear,
        ore: this.ore,
        oraInizio: this.oraInizio,
        oraFine: this.oraFine,
        nomeEnte: this.tipoInterna ? null : this.azienda.nome,
        enteId: this.tipoInterna ? null : this.azienda.id,
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
      if (!this.oraFine) {
        this.forceErrorDisplayOraFine = true;
      } else {
        this.forceErrorDisplayOraFine = false;
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
        && (this.oraInizio && this.oraFine && this.oraFine >= this.oraInizio)
        && (this.tipologia && this.tipologia != 'Tipologia')
        && (this.date.dataInizio && this.date.dataFine && (this.date.dataInizio <= this.date.dataFine) && (this.date.dataFine < this.date.maxFine))
      );
    } else {
      return (
        (this.titolo && this.titolo != '' && this.titolo.trim().length > 0)
        && (this.referenteScuola && this.referenteScuola != '' && this.referenteScuola.trim().length > 0)
        && (this.referenteEsterno && this.referenteEsterno != '' && this.referenteEsterno.trim().length > 0)
        && (this.ore && this.ore > 0)
        && (this.oraInizio && this.oraFine && this.oraFine >= this.oraInizio)
        && (this.azienda.id != '')
        && (this.tipologia && this.tipologia != 'Tipologia')
        && (this.date.dataInizio && this.date.dataFine && (this.date.dataInizio <= this.date.dataFine) && (this.date.dataFine < this.date.maxFine))
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
    } else if (type == 'trim') {
      event.target.value = event.target.value.trim();
    }
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
