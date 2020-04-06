import { Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Giornate } from '../../shared/interfaces';
import { DataService } from '../../core/services/data.service';
import { Component, OnInit } from '@angular/core';
import { isBefore, addDays, startOfISOWeek, endOfISOWeek, isAfter, isSameISOWeek } from 'date-fns';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ModalService } from '../../core/modal/modal.service'
import { GrowlerService, GrowlerMessageType } from '../../core/growler/growler.service';
import { isNumber } from '@ng-bootstrap/ng-bootstrap/util/util';

declare var moment: any;
moment['locale']('it');

@Component({
  selector: 'cm-attivita-presenze',
  templateUrl: './attivita-presenze.component.html',
  styleUrls: ['./attivita-presenze.component.css']
})
export class AttivitaPresenzeComponent implements OnInit {

  @Input() attivita;
  @Input() tipologiaEsperienza;

  titolo: string;
  states = [{ "id": 1, "name": "Presente" }, { "id": 2, "name": "Assente" }];

  view: string = 'week';
  titleWeek: string;
  periodStartDate: number;
  periodEndDate: number;
  viewDate: Date; //set this date to starting point of period.
  viewEndDate: Date;
  presenzaStudente = [];
  events: any = [];
  studenti;
  calendar;
  eventWindow: any = []; // visible event per selected week.
  frontArrowDisabled: boolean = false;
  backArrowDisabled: boolean = false;

  presenze = [];

  constructor(
    private modalService: ModalService,
    private modal: NgbModal,
    private route: ActivatedRoute,
    private router: Router,
    private dataService: DataService,
    private growler: GrowlerService) {

  }

  ngOnInit() {
    this.presenze = [];
    this.events = [];
    this.viewDate = new Date(this.attivita.aa.dataInizio);
    this.viewEndDate = new Date(this.attivita.aa.dataFine);

    this.dataService.getStudenteAttivitaGiornalieraCalendario(this.attivita.es.id, this.attivita.es.studenteId, moment(this.viewDate).format('YYYY-MM-DD'), moment(this.viewEndDate).format('YYYY-MM-DD')).subscribe((resp: any) => {
      this.presenze = resp;
      this.initDays();
   
      for (let addedGiorno of this.presenze) {
        this.events.push(addedGiorno);
        this.viewDatechanged();
      }
   
      this.setCalendar(this.attivita.aa);
      this.setHoursOfStudent();
   
    },
      (err: any) => console.log(err),
      () => console.log('get attivita giornaliera calendario by id'));
  }

  initDays() {
    var startDate = moment(this.attivita.aa.dataInizio);
    var endDate = moment(this.attivita.aa.dataFine);
    var tot = endDate.diff(startDate, 'days');

    var now = startDate.clone();
    if (this.presenze.length < (tot + 1)) {
      while (now.diff(endDate, 'days') <= 0) {
        var index = this.presenze.findIndex(x => x.giornata === now.format('YYYY-MM-DD'));
        if (index < 0) {
          this.presenze.push({
            "attivitaSvolta": "",
            "esperienzaSvoltaId": this.attivita.es.id,
            "giornata": moment(now).format('YYYY-MM-DD'),
            "istitutoId": this.attivita.aa.istitutoId,
            "oreSvolte": null,
            "verificata": false
          });
        }
        now.add(1, 'days');
      }
    }

    // sort by giornata.
    this.presenze = this.presenze.sort((a, b) => {
      return moment(a.giornata).diff(moment(b.giornata));
    });

  }

  daydiff(first, second) {
    return Math.round((second - first) / (1000 * 60 * 60 * 24));
  }

  todayIsBetween() {
    var today = moment();
    var inizio = this.attivita.aa.dataInizio;
    var fine = this.attivita.aa.dataFine;
    return today.isBetween(inizio, fine, null, "[]")
  }

  viewDatechanged() {
    this.eventWindow = [];
    let windowStart = this.viewDate;
    let startOfWeek = startOfISOWeek(windowStart);
    let endOfWeek = endOfISOWeek(windowStart);
    for (var eventIndex = 0; eventIndex < this.events.length; eventIndex++) {
      if (isSameISOWeek(this.events[eventIndex].giornata, startOfWeek)) {
        this.eventWindow.push(this.events[eventIndex]);
      }
    }

    var s = moment(startOfWeek);
    var e = moment(endOfWeek);

    this.titleWeek = s.format('DD MMMM') + " - " + e.format('DD MMMM');

    // if -1 day from start of week is less than dataInizio of activity disable back arrow.
    if (isBefore(addDays(startOfWeek, -1), new Date(this.attivita.aa.dataInizio))) {
      this.backArrowDisabled = true;
    } else {
      this.backArrowDisabled = false;
    }
    // if +1 day from end of week is greater than dataFine of activity disable forward arrow.
    if (isAfter(addDays(endOfWeek, 1), new Date(this.attivita.aa.dataFine))) {
      this.frontArrowDisabled = true;
    } else {
      this.frontArrowDisabled = false;
    }

  }

  edit(event: Giornate, newHour) {
    event.verificata = false;
    event.isModifiedState = true;
    // update the right element 
    var foundIndex = this.presenzaStudente.map(giornata => giornata.giornata).indexOf(event.giornata);
    if (isNumber(newHour)) {
      this.presenzaStudente[foundIndex].oreSvolte = newHour
    }
  
  }

  savePresenze() {
    let toBeSaved = this.prepareSaveArray();

    this.dataService.saveAttivitaGiornaliereStudentiPresenze(toBeSaved, this.attivita.es.id).subscribe((studente: any) => {
      this.growler.growl("Presenze aggiornate con successo", GrowlerMessageType.Success);
      this.ngOnInit();
    },
      (err: any) => console.log(err),
      () => console.log('save attivita giornaliera presenze'));
  }

  prepareSaveArray() {
    var toBeSaved = [];

    this.presenze.forEach(ps => {
      if (ps.isModifiedState) {
        var save = JSON.parse(JSON.stringify(ps))
        save.giornata = moment(ps.giornata, 'YYYY-MM-DD').valueOf();
        toBeSaved.push(save);
      }
    });

    return toBeSaved;
  }

  getPresenzaStudente(event) {
    var foundPresenza = [];
    if (this.presenze)
      foundPresenza = this.presenze.filter(giorno => giorno.giornata == event.giornata);
    if (foundPresenza.length > 0)
      return foundPresenza[0].oreSvolte
    else return 0
  }

  setCalendar(attivita) {
    this.calendar = [];
    var startDate = moment(attivita.dataInizio);
    var endDate = moment(attivita.dataFine);
    this.calendar.push(startDate);
    // for (var i = 0; i < 6; i++) {
    let newDay = moment(this.calendar[this.calendar.length - 1]);
    while (newDay < endDate) {
      newDay = moment(this.calendar[this.calendar.length - 1]);
      this.calendar.push(newDay.add(1, 'days').valueOf());
    }
    this.calendar;
  }

  setHoursOfStudent() {
    this.presenzaStudente = this.getPresenzaStudenti()
  }

  getPresenzaStudenti() {
    var days = [];
    
    if (this.presenze)
      days = this.presenze;
    
    days = this.fillDaysGap(days);
    
    return days
  }

  fillDaysGap(days) {
    var tmpDays = [];
    for (var i = 0; i < this.calendar.length; i++) {
      var tmpMap = days.map(day => day.giornata);
      var found = tmpMap.indexOf(moment(this.calendar[i]).format('YYYY-MM-DD'));
      if (found >= 0) {
        tmpDays.push(days[found])
      }
      else {
        tmpDays.push({
          oreSvolte: 0,
          data: moment(this.calendar[i]).format('YYYY-MM-DD')
        })
      }
    }

    return tmpDays;
  }

}
