import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Giornate } from '../../../shared/interfaces';
import { DataService } from '../../../core/services/data.service';
import { Component, ViewChild, OnInit } from '@angular/core';
import { addDays } from 'date-fns';
import { IModalContent } from '../../../core/modal/modal.service'
import { registerLocaleData } from '@angular/common';
import { GrowlerService, GrowlerMessageType } from '../../../core/growler/growler.service';
import localeIT from '@angular/common/locales/it'
import { DatePickerComponent } from 'ng2-date-picker';
import { ArchiaviazioneAttivitaModal } from '../archiaviazione-attivita-modal/archiaviazione-attivita.component';

registerLocaleData(localeIT);

declare var moment: any;
moment['locale']('it');


@Component({
  selector: 'gestione-preseneze-gruppo',
  templateUrl: './gestione-preseneze-gruppo.component.html',
  styleUrls: ['./gestione-preseneze-gruppo.component.scss']
})

export class GestionePresenzeGruppoComponent implements OnInit {

  breadcrumbItems = [
    {
      title: "Lista attività",
      location: "../../../../../../"
    },
    {
      title: "Dettaglio attività",
      location: "../../../../"
    },
    {
      title: "Gestione presenze"
    }
  ];

  report: any;
  presenze = [];
  attivita: any;
  date;
  limitMin: any;
  limitMax: any;
  studenti: any;
  studentiIds: string[];
  presenzaStudenti=[];
  calendar: any;
  paginationIndex: number = 0;
  defaultHour = 8;
  isArchivio: boolean;
  toolTipSave;
  isModifiedState: boolean;

  datePickerConfig = {
    locale: 'it',
    firstDayOfWeek: 'mo'  
  };

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

  refresh() {
    if (this.presenzaStudenti.length > 0) {
      if (this.date.dataInizio >= this.limitMin && this.date.dataInizio <= this.limitMax) {
        this.isModifiedState = false;
        var start = moment(this.calendar[0]);
        var end = moment(this.calendar[6]);
        let pageNumber = this.paginationIndex;
        while (start < end) {
          if (this.date.dataInizio >= start && this.date.dataInizio <= end) {
            break;
          }

          if (this.date.dataInizio < start && this.paginationIndex > 0) {
            start = addDays(start, -7);
            end = addDays(start, 7);
            pageNumber--;
            
          } else {
            start = addDays(start, 7);
            end = addDays(start, 7);
            pageNumber++;
          }
        }
  
        this.paginationIndex = pageNumber;
        this.optimizeCheckboxesStates();
        this.setCalendar(this.date.dataInizio);
        this.setHoursOfStudents();
      }
    }    
  }


  constructor(
    private activeRoute: ActivatedRoute,
    private router: Router,
    private dataService: DataService,
    private growler: GrowlerService,
    private modalService: NgbModal) {
    this.date = {
      dataInizio: moment()
    }
  }


  modalPopup: IModalContent = {
    header: 'Salva giorno',
    body: 'Sei sicuro di voler salvare il giorno?',
    cancelButtonText: 'Annulla',
    OKButtonText: 'Salva'
  };

  ngOnInit() {
    this.isModifiedState = false;
    this.activeRoute.params.subscribe(params => {
      let id = params['id'];
      this.paginationIndex = 0;
      this.dataService.getAttivita(id).subscribe((res) => {
        this.attivita = res.attivitaAlternanza;
        this.setCalendar(moment(this.attivita.dataInizio));
        this.dataService.getAttivitaPresenzeGruppoReport(id).subscribe((res => {
          this.report = res;

          if (this.report.giornateDaValidare == 0) {
            if ((this.report.oreTotali - this.report.oreValidate) > 0) {
              this.toolTipSave = 'Valida ore studenti';
            } else {
              this.toolTipSave = 'Non ci sono ore da validare';
            }
          } else {
            this.toolTipSave = 'Valida ore studenti';
          }
          
          this.attivita.stato == 'archiviata' ? (this.isArchivio = true): (this.isArchivio = false);
          this.limitMin = moment(this.report.dataInizio);
          this.limitMax = moment(this.report.dataFine);
          this.date.dataInizio = this.limitMin;

          this.dataService.getAttivitaPresenzeGruppoListaGiorni(id, moment(this.attivita.dataInizio).format('YYYY-MM-DD'), moment(this.attivita.dataFine).format('YYYY-MM-DD')).subscribe((studenti => {
            this.studenti = studenti;
            this.initDays();
            this.optimizeCheckboxesStates();
            this.setHoursOfStudents();
          }));
        }));
      });
    });
  }

  initDays() {
    var a = moment(this.attivita.dataInizio).startOf('day');
    var b = moment(this.attivita.dataFine).endOf('day');
    var tot = b.diff(a, 'days');   // =1
    this.studentiIds = Object.keys(this.studenti);
    for (var i = 0; i < this.studentiIds.length; i++) {
      if (this.studenti[this.studentiIds[i]].presenze.length < (tot + 1))
        this.initDaysStudent(this.studentiIds[i]);
    }
  }

  initDaysStudent(key) {
    // put objecty
    var startDate = moment(this.attivita.dataInizio);
    var endDate = moment(this.attivita.dataFine);
    var now = startDate.clone();
    while (now.diff(endDate, 'days') <= 0) {
      // while (now.isSameOrBefore(endDate)) {
      this.studenti[key].presenze.push({
        "attivitaSvolta": "",
        "esperienzaSvoltaId": this.studenti[key].esperienzaSvoltaId,
        "giornata": moment(now).format('YYYY-MM-DD'),
        "istitutoId": this.studenti[key].istitutoId,
        "oreSvolte": null,
        "verificata": false
      });     
      now.add(1, 'days');
    }
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
      days = studente.presenze;
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
      var tmpMap = days.map(day => day.giornata);
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

  setCalendar(initMomentDate) {
    this.calendar = [];
    var startDate = initMomentDate.startOf('isoWeek').valueOf();
    this.calendar.push(startDate);
    for (var i = 0; i < 6; i++) {
      let newDay = moment(this.calendar[this.calendar.length - 1]);
      this.calendar.push(newDay.add(1, 'days').valueOf());
    }
    this.calendar;
  }

  isDayBetween(giornata) {
    if (!giornata)
      return false;
    var inizio = moment(this.attivita.dataInizio);
    var fine = moment(this.attivita.dataFine);
    var day = moment(giornata);
    if (day >= inizio && day <= fine) {
      return true;
    } else {
      return false;
    }  
    // return day.isBetween(inizio, fine, null, "[]");
  }

  isDaysBackAvailable() {
    return this.paginationIndex > 0;
  }

  isDaysForwardAvailable() {
    var inizio = moment(this.attivita.dataInizio).startOf('isoWeek');
    var fine = moment(this.attivita.dataFine).endOf('isoWeek');
    return fine.diff(inizio, 'week') > this.paginationIndex
  }

  alldaysOfStudentChecked: boolean[] = [];
  allStudentOfDayChecked: boolean[] = [];

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
        if (!days[i].verificata) {
          this.alldaysOfStudentChecked[this.alldaysOfStudentChecked.length - 1] = false;
          this.allStudentOfDayChecked[i] = false;
        }
      }
    }
  }

  getPaginatedStudentePresenze(studente, pageNumber) {
    if (!studente) return [];
    return studente.presenze.slice(pageNumber * 7, pageNumber * 7 + 7);
  }

  setDefaultHourToAllStudents(dayIndex) {
    var tmp = [];
    for (var key in this.presenzaStudenti) {
      this.presenzaStudenti[key][dayIndex].oreSvolte = this.defaultHour;
      this.presenzaStudenti[key][dayIndex].verificata = false;
    }

  }

  daydiff(first, second) {
    return Math.round((second - first) / (1000 * 60 * 60 * 24));
  }

  edit(event: Giornate, newHour) {
    if(event.oreSvolte > 12) {
      event.oreSvolte = new Number(12);
    }
    event.verificata = false;
    event.isModifiedState = true;
    this.toolTipSave = 'Valida ore studenti';
    this.isModifiedState = true;
  }

  savePresences() {
    var toBeSaved = this.convertPresencesIntoStudenti();
    this.dataService.validaPresenzeAttivitaGruppo(this.attivita.id, toBeSaved).subscribe((res) => {
      var start = moment(this.calendar[0]);
      var end = moment(this.calendar[6]);
      let message = "Presenze comprese tra il " + start.format('DD/MM/YYYY') + " ed il " + end.format('DD/MM/YYYY') + " validate con successo";
      this.growler.growl(message, GrowlerMessageType.Success);
     
      this.dataService.getAttivitaPresenzeGruppoReport(this.attivita.id).subscribe((res => {
        this.report = res;
      
        this.dataService.getAttivitaPresenzeGruppoListaGiorni(this.attivita.id, moment(this.attivita.dataInizio).format('YYYY-MM-DD'), moment(this.attivita.dataFine).format('YYYY-MM-DD')).subscribe((studenti => {
          this.studenti = studenti;
          this.initDays();
          this.optimizeCheckboxesStates();
          this.setHoursOfStudents();
          this.refresh();
        }));      
      
      }));

    },
      (err: any) => {
        console.log(err)
      },
      () => console.log('valida presenze individuale'));
  }
  convertPresencesIntoStudenti() {
    var toBeSaved = []
    //save into this.studenti this presenzaStudenti
    //per ogni elemento della mappa presenzaStudenti
    for (var key in this.presenzaStudenti) {
      //merge presenza with giornate
      for (var i = 0; i < this.presenzaStudenti[key].length; i++) {
        var index = this.studenti[key].presenze.map(day => day.giornata).indexOf(this.presenzaStudenti[key][i].giornata)
        if (index >= 0) {
          var save = JSON.parse(JSON.stringify(this.presenzaStudenti[key][i]))
          save.verificata = true;
          save.giornata = moment(this.presenzaStudenti[key][i].giornata, 'YYYY-MM-DD').valueOf();
          toBeSaved.push(save);
        }        
      }
    }
    return toBeSaved;
  }

  navigateDaysForward() {
    if (this.isDaysForwardAvailable()) {
      this.date.dataInizio = this.date.dataInizio.clone().add(7, 'days');
      this.paginationIndex++;
      this.optimizeCheckboxesStates();
      this.setCalendar(this.date.dataInizio);
      this.setHoursOfStudents();
      this.isModifiedState = false;
    }
  }

  navigateDaysBack() {
    if (this.isDaysBackAvailable()) {
      this.date.dataInizio = this.date.dataInizio.clone().add(-7, 'days');
      this.paginationIndex--;
      this.optimizeCheckboxesStates();
      this.setCalendar(this.date.dataInizio);
      this.setHoursOfStudents();
      this.isModifiedState = false;
    }
  }

  showTipRiga(ev, studente) {
    console.log(ev.target.title);
    var numOreDaValidare = 0;
    var numOreValidate = 0;

    if (!studente.toolTipRiga) {
      studente.fetchingToolTipRiga = true;
      for (var i = 0; i < studente.presenze.length; i++) {
        if (!studente.presenze[0].verificata && studente.presenze[i].oreSvolte) {
          numOreDaValidare = numOreDaValidare + studente.presenze[i].oreSvolte;
        } else {
          numOreValidate = numOreValidate + studente.presenze[i].oreSvolte;
        }
      }
      studente.fetchingToolTipRiga = false;
      studente.toolTipRiga = "Ore da validare: " + numOreDaValidare + "   Ore validate: " + numOreValidate + " / " + this.attivita.ore;
    }
  }

  cancel() {
    this.router.navigate(['../../../../'], { relativeTo: this.activeRoute });
  }

  archivia() {
    this.dataService.attivitaAlternanzaEsperienzeReport(this.attivita.id)
      .subscribe((response) => {
        const modalRef = this.modalService.open(ArchiaviazioneAttivitaModal, { windowClass: "archiviazioneModalClass" });
        modalRef.componentInstance.esperienze = response;
        modalRef.componentInstance.titolo = this.attivita.titolo;
        modalRef.componentInstance.onArchivia.subscribe((esperienze) => {
          this.dataService.archiviaAttivita(this.attivita.id, esperienze).subscribe((res) => {
            this.router.navigate(['../../'], { relativeTo: this.activeRoute });
          })
        })
      }, (err: any) => console.log(err),
        () => console.log('attivitaAlternanzaEsperienzeReport'));
  }

}