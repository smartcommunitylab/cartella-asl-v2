import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { Location } from '@angular/common';
import { DataService } from '../../core/services/data.service';
import { ICustomer, IPagedResults, EsperienzaSvolta, Stato, TipologiaAzienda, AttivitaAlternanza } from '../../shared/interfaces';
import { TrackByService } from '../../core/services/trackby.service';
import * as moment from 'moment';
import { GrowlerService, GrowlerMessageType } from '../../core/growler/growler.service';

@Component({
    selector: 'activityAlternanza-component',
    templateUrl: './activityAlternanza.component.html',
    styleUrls: ['./activityAlternanza.scss']
})
export class ActivityAlternanzaComponent implements OnInit {

    id: string = "";
    attivitaAlternanza: AttivitaAlternanza;
    tipologiaAzienda: TipologiaAzienda[] = [];
    status: Stato[] = [];



    studenti;
    studentiIds;
    paginationIndex: number = 0;
    isArchivio: boolean;
    calendar;
    presenzaStudenti = [];
    defaultHour = 8;

    navTitle: string = "Dettaglio attività";
    breadcrumbItems = [
        {
            title: "Lista Attività",
            location: "../../"
        },
        {
            title: "Dettaglio attività",
            location: "./"
        }
    ];


    constructor(private location: Location, private router: Router, private route: ActivatedRoute, private dataService: DataService, private trackbyService: TrackByService, private growler: GrowlerService) { }

    back() {
        this.location.back();
    }

    ngOnInit() {
        console.log('ActivityAlternanzaComponent');
        this.dataService.getData("tipologiaAzienda").subscribe((data) => {
            this.tipologiaAzienda = data;
            this.route.params.subscribe((params: Params) => {
                this.id = params['id'];
                this.dataService.getAttivitaAlternanzaByIdAPI(this.id)
                    .subscribe((activity: AttivitaAlternanza) => {
                        this.attivitaAlternanza = activity;
                        this.loadCalendar();
                    });
            });

        });
    }
    getTipo(id: any) {
        for (let tipo of this.tipologiaAzienda) {
            if (tipo.id == id) {
                return tipo.titolo;
            }
        }
    }

    loadCalendar() {
        this.setCalendar(this.attivitaAlternanza, true);
        this.dataService.getAttivitaGiornalieraCalendario(this.id).subscribe((studenti: any) => {
          this.studenti = studenti;
          this.initDays();
          /*this.isArchivio = this.corso.completata;
          if (this.isArchivio) {
            this.breadcrumbItems[0].title = "Archivio corsi";
            this.breadcrumbItems[0].location = "../../";
          }*/
          //this.navTitle = corso.titolo + " - Presenze";
          this.optimizeCheckboxesStates();
          this.setHoursOfStudents();
        },
          (err: any) => console.log(err),
          () => console.log('get attivita giornaliera calendario by id'));
    }


  setCalendar(attivita, init) {
    this.calendar = [];
    var startDate;
    if (init)
      startDate = moment(attivita.dataInizio).startOf('isoWeek').valueOf();
    else startDate = moment(attivita.dataInizio).startOf('isoWeek').add((this.paginationIndex), 'week').valueOf();
    this.calendar.push(startDate);
    for (var i = 0; i < 6; i++) {
      let newDay = moment(this.calendar[this.calendar.length - 1]);
      this.calendar.push(newDay.add(1, 'days').valueOf());
    }
    this.calendar;
  }
  setHoursOfStudents() {
    for (var k = 0; k < this.studentiIds.length; k++)
      this.presenzaStudenti[this.studentiIds[k]] = this.getPresenzaStudenti(this.studenti[this.studentiIds[k]], this.paginationIndex)
  }
  getPresenzaStudenti(studente, pageNumber) {
    var days = [];
    if (!studente)
      days = [];
    else
      days = studente.giornate;
    //filll the gap
    days = this.fillDaysGap(days, pageNumber);
    return days
  }
  fillDaysGap(days, pageNumber) {
    var tmp = [];
    var k = 0;
    var data1
    var data2
    for (var i = 0; i < 7; i++) {
      var tmpMap = days.map(day => day.data);
      var found = tmpMap.indexOf(moment(this.calendar[i]).format('YYYY-MM-DD'))
      if (found >= 0) {
        // if (days[k] && data1 === data2) {
        tmp.push(days[found])
        k++;
      }
      else {
        tmp.push({
          oreSvolte: 0,
          data: moment(this.calendar[i]).format('YYYY-MM-DD')
        })
      }
    }
    return tmp;
  }

  getPaginatedStudentePresenze(studente, pageNumber) {
    if (!studente) return [];
    return studente.giornate.slice(pageNumber * 7, pageNumber * 7 + 7);
  }

  selectAllPresenze(event) {
    this.studentiIds.forEach(studenteId => {
      this.getPaginatedStudentePresenze(this.studenti[studenteId], this.paginationIndex).forEach(giornata => {
        giornata.presente = event.target.checked;
      });
    });
    this.optimizeCheckboxesStates();
  }

  isAllChecked() {
    if (!this.allStudentOfDayChecked) return false;
    return this.allStudentOfDayChecked.every(day => day);
  }

  selectAllDaysOfStudent(studente, state) {
    this.getPaginatedStudentePresenze(studente, this.paginationIndex).forEach(giornata => {
      giornata.presente = state;
    });
    this.optimizeCheckboxesStates();
  }

  selectAllStudentsOfDay(giornata, dayIndex, state) {
    this.studentiIds.forEach(studenteId => {
      this.studenti[studenteId].giornate[this.paginationIndex * 7 + dayIndex].presente = state;
    });
    this.optimizeCheckboxesStates();
  }

  alldaysOfStudentChecked: boolean[] = [];
  allStudentOfDayChecked: boolean[] = [];

  //set the states of "header" checkboxes reading only one time the matrix
  optimizeCheckboxesStates() {
    this.alldaysOfStudentChecked = [];
    this.allStudentOfDayChecked = [];
    for (var studenteId in this.studenti) {
      this.getPaginatedStudentePresenze(this.studenti[studenteId], this.paginationIndex).forEach(element => {
        this.allStudentOfDayChecked.push(true);
      });
      break;
    }
    for (var studenteId in this.studenti) {
      this.alldaysOfStudentChecked.push(true);
      let days = this.getPaginatedStudentePresenze(this.studenti[studenteId], this.paginationIndex);
      for (let i = 0; i < days.length; i++) {
        if (!days[i].presente) {
          this.alldaysOfStudentChecked[this.alldaysOfStudentChecked.length - 1] = false;
          this.allStudentOfDayChecked[i] = false;
        }
      }
    }
  }

  getAllDaysOfStudentChecked(studentIndex) {
    if (!this.alldaysOfStudentChecked || this.alldaysOfStudentChecked.length < studentIndex) {
      return false;
    }
    return this.alldaysOfStudentChecked[studentIndex];
  }

  setDefaultHourToAllStudents(dayIndex) {
    //set for all Students at that day, defaultHour
    var tmp = [];
    for (var key in this.presenzaStudenti) {
      this.presenzaStudenti[key][dayIndex].oreSvolte = this.defaultHour;
    }


  }
  getAllStudentsOfDayChecked(dayIndex) {
    if (!this.allStudentOfDayChecked || this.allStudentOfDayChecked.length < dayIndex) {
      return false;
    }
    return this.allStudentOfDayChecked[dayIndex];
  }

  isDaysBackAvailable() {
    return this.paginationIndex > 0;
  }
  isDaysForwardAvailable() {
    var inizio = moment(this.attivitaAlternanza.dataInizio).startOf('isoWeek');
    var fine = moment(this.attivitaAlternanza.dataFine).endOf('isoWeek');
    return fine.diff(inizio, 'week') > this.paginationIndex
  }
  isDayBetween(giornata) {
    var inizio = moment(this.attivitaAlternanza.dataInizio);
    var fine = moment(this.attivitaAlternanza.dataFine);
    var day = moment(giornata);
    return day.isBetween(inizio, fine, null, "[]");
  }

  navigateDaysBack() {
    if (this.isDaysBackAvailable()) {
      if (!this.isArchivio) {
        this.convertPresencesIntoStudenti();
        this.dataService.saveAttivitaGiornaliereStudentiPresenze(this.studenti).subscribe((studenti: any) => {
          this.growler.growl("Presenze aggiornate con successo", GrowlerMessageType.Success);
          this.paginationIndex--;
          this.optimizeCheckboxesStates();
          this.setCalendar(this.attivitaAlternanza, false);
          this.setHoursOfStudents();
        },
          (err: any) => console.log(err),
          () => console.log('save attivita giornaliera presenze'));
      } else {
        this.paginationIndex--;
        this.optimizeCheckboxesStates();
        this.setCalendar(this.attivitaAlternanza, false);
        this.setHoursOfStudents();
      }
    }

  }
  navigateDaysForward() {
    if (this.isDaysForwardAvailable()) {
      if (!this.isArchivio) {
        this.convertPresencesIntoStudenti();
        this.dataService.saveAttivitaGiornaliereStudentiPresenze(this.studenti).subscribe((studenti: any) => {
          this.growler.growl("Presenze aggiornate con successo", GrowlerMessageType.Success);
          this.paginationIndex++;
          this.optimizeCheckboxesStates();
          this.setCalendar(this.attivitaAlternanza, false);
          this.setHoursOfStudents();
        },
          (err: any) => console.log(err),
          () => console.log('save attivita giornaliera presenze'));
      } else {
        this.paginationIndex++;
        this.optimizeCheckboxesStates();
        this.setCalendar(this.attivitaAlternanza, false);
        this.setHoursOfStudents();
      }
      
    }
  }
  savePresences() {
    this.convertPresencesIntoStudenti();
    this.dataService.saveAttivitaGiornaliereStudentiPresenze(this.studenti).subscribe((studenti: any) => {
      this.growler.growl("Presenze aggiornate con successo", GrowlerMessageType.Success);
      //this.router.navigate(['../'], { relativeTo: this.route });
      this.ngOnInit();
    },
      (err: any) => console.log(err),
      () => console.log('save attivita giornaliera presenze'));
  }
  convertPresencesIntoStudenti() {
    //save into this.studenti this presenzaStudenti
    //per ogni elemento della mappa presenzaStudenti
    for (var key in this.presenzaStudenti) {
      //merge presenza with giornate
      for (var i = 0; i < this.presenzaStudenti[key].length; i++) {
        var index = this.studenti[key].giornate.map(day => day.data).indexOf(this.presenzaStudenti[key][i].data)
        if (index >= 0) {
          this.studenti[key].giornate[index] = this.presenzaStudenti[key][i];
        }
        if (this.studenti[key].giornate[index]) {
          this.studenti[key].giornate[index].verificata = true;
        }
      }
    }


  }

  initDays() {
    var a = moment(this.attivitaAlternanza.dataInizio).startOf('day');
    var b = moment(this.attivitaAlternanza.dataFine).endOf('day');
    var tot = b.diff(a, 'days');   // =1
    this.studentiIds = Object.keys(this.studenti);
    for (var i = 0; i < this.studentiIds.length; i++) {
      if (this.studenti[this.studentiIds[i]].giornate.length < (tot + 1))
        this.initDaysStudent(this.studentiIds[i]);
    }
  }
  initDaysStudent(key) {
    // put objecty
    var startDate = moment(this.attivitaAlternanza.dataInizio);
    var endDate = moment(this.attivitaAlternanza.dataFine);
    var now = startDate.clone();
    while (now.diff(endDate, 'days') <= 0) {
      // while (now.isSameOrBefore(endDate)) {
      this.studenti[key].giornate.push({
        "data": now.format('YYYY-MM-DD'),
        "presente": false,
        "oreSvolte": 0,
        "attivitaSvolta": ""
      });
      now.add(1, 'days');
    }
  }
}


