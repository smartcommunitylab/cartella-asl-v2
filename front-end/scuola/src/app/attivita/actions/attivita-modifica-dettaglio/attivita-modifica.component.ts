import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../../../core/services/data.service';
import { GeoService } from '../../../core/services/geo.service';
import { Router, ActivatedRoute } from '@angular/router';
import { AttivitaAlternanza } from '../../../shared/classes/AttivitaAlternanza.class';
import { FormGroup, FormControl } from '@angular/forms';
import { Azienda } from '../../../shared/interfaces';
import { Observable, of } from 'rxjs';
import { catchError, debounceTime, distinctUntilChanged, map, tap, switchMap } from 'rxjs/operators';
import * as moment from 'moment';
import { DatePickerComponent } from 'ng2-date-picker';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'cm-modifica-dettaglio',
  templateUrl: './attivita-modifica.component.html',
  styleUrls: ['./attivita-modifica.component.scss']
})
export class AttivitaDettaglioModificaComponent implements OnInit {

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private dataService: DataService,
    private geoService: GeoService) { }

  attivita: AttivitaAlternanza;
  esperienze;
  titolo;
  descrizione;
  formatore;
  referenteScuola;
  referenteEsterno;
  luogoSvolgimento;
  formatoreCF;
  referenteScuolaCF;
  referenteEsternoCF;
  myForm: FormGroup;
  aziende: Azienda[];
  pageSize = 20;
  tipologie;
  azienda: any;
  place: any;
  riferente: any;
  date: {
    dataInizio: moment.Moment,
    dataFine: moment.Moment,
    maxFine: moment.Moment,
    minInizio: moment.Moment,
    prevDataInizio: moment.Moment
  } = {
    dataInizio: null,
    dataFine: null,
    maxFine: null,
    minInizio: null,
    prevDataInizio: null
  };
  forceEnteDisplay: boolean = false;
  start: moment.Moment;
  end: moment.Moment;
  schoolYear: string;
  schoolYears: string[] = [];
  forceTitoloErrorDisplay: boolean = false;
  forceReferenteScuolaErrorDisplay: boolean = false;
  forceReferenteEsternoErrorDisplay: boolean = false;
  forceOreErrorDisplay: boolean = false;
  forceDalleAlleErrorDisplay: boolean = false;
  forceErrorDisplayOraInizio: boolean = false;
  forceErrorDisplayOraFine: boolean = false;
  forceAnnoScolasticoErrorDisplay: boolean = false;
  forceSelectionMsg: boolean = false;
  menuContent = "In questa pagina trovi tutte le informazioni relative all’attività che stai visualizzando.";
  showContent: boolean = false;
  tipoInterna: boolean = false;
  zeroStudent: boolean = true;
  attivitaTipologia;
  stati = [{ "name": "In attesa", "value": "in_attesa" }, { "name": "In corso", "value": "in_corso" }, { "name": "Revisionare", "value": "revisione" }, { "name": "Archiviata", "value": "archiviata" }];
  orari = ['00', '01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23']
  attivitaStato: string = "";
  evn = environment;
  modalita;

  breadcrumbItems = [
    {
      title: "Dettaglio attività",
      location: "../../",
    },
    {
      title: "Modifica dati attività"
    }
  ];

  datePickerConfig = {
    locale: 'it',
    firstDayOfWeek: 'mo',
    min: moment().subtract(60, 'months'),
    max: moment().add(36,'months')
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

  ngOnInit() {
    this.evn.modificationFlag = true;
    this.date.dataInizio = moment().startOf('day');
    this.date.dataFine = moment().startOf('day');;
    this.date.prevDataInizio = moment().startOf('day');;
    this.date.minInizio = moment([2018, 8, 1]);

    this.route.params.subscribe(params => {
      let id = params['id'];
      this.dataService.getAttivitaTipologie().subscribe((res) => {
        this.tipologie = res;
        this.dataService.getAttivita(id).subscribe((res) => {
          this.attivita = res.attivitaAlternanza;
          this.attivitaStato = this.getStatoNome(this.attivita.stato);
          this.place = {};
          this.place.name = this.attivita.luogoSvolgimento;
          this.place.location = [this.attivita.latitude, this.attivita.longitude];
          this.riferente = {};
          this.riferente.nominativoDocente = this.attivita.referenteScuola;
          this.esperienze = res.esperienze;
          this.esperienze.length == 0 ? this.zeroStudent = true : this.zeroStudent = false;
          this.tipologie.filter(tipo => {
            if (tipo.id == this.attivita.tipologia) {
              this.tipoInterna = tipo.interna;
              this.attivitaTipologia = tipo.titolo;
            }
          })

          if (this.attivita.enteId && !this.tipoInterna) {
            this.dataService.getAzienda(this.attivita.enteId).subscribe((az => {
              this.azienda = az;
            }))
          }
          var dataInizio = new Date(this.attivita.dataInizio);
          this.date.dataInizio = moment(dataInizio).startOf('day');;
          this.date.prevDataInizio = moment(dataInizio).startOf('day');;
          var dataFine = new Date(this.attivita.dataFine);
          this.date.dataFine = moment(dataFine).startOf('day');;
          this.schoolYear = this.attivita.annoScolastico.slice();
          this.checkDate();
      
          this.titolo = this.attivita.titolo;
          // tackle incase saved with 1 digit in past.
          this.attivita.oraInizio = this.formatTwoDigit(this.attivita.oraInizio);
          this.attivita.oraFine = this.formatTwoDigit(this.attivita.oraFine);

          this.myForm = new FormGroup({
            titolo: new FormControl(),
            descrizione: new FormControl(),
            formatore: new FormControl(),
            referenteScuola: new FormControl(),
            referenteEsterno: new FormControl(),
            ore: new FormControl(),
            luogoSvolgimento: new FormControl(),
            formatoreCF: new FormControl(),
            referenteScuolaCF: new FormControl(),
            referenteEsternoCF: new FormControl()
          });

          this.isRendicontazioneOre(this.attivita);

        }, (err: any) => console.log(err),
          () => console.log('getAttivitaTipologie'));
      });
    });
  }

  ngOnDestroy() {
    this.evn.modificationFlag = false;
  }

  getTipologiaString(tipologiaId) {
    if (this.tipologie) {
      let rtn = this.tipologie.find(data => data.id == tipologiaId);
      if (rtn) return rtn.titolo;
      return tipologiaId;
    } else {
      return tipologiaId;
    }
  }

  getTipologia(tipologiaId) {
    if (this.tipologie) {
      return this.tipologie.find(data => data.id == tipologiaId);
    } else {
      return tipologiaId;
    }
  }

  checkDate() {
    if(this.zeroStudent) {
      var anno = this.getAnnoScolasticoNum(this.date.dataInizio);
      this.date.maxFine = moment([anno + 1, 8, 30]);
      this.date.minInizio = moment([2018, 8, 1]);
    } else {
      var annoFisso = parseInt(this.attivita.annoScolastico.substring(0, 4), 10);
      this.date.maxFine = moment([annoFisso + 1, 8, 30]);
      this.date.minInizio = moment([annoFisso, 8, 1]);
    }
  }

  getAnnoScolasticoNum(now) {
    var lastDay = moment(now).month(8).date(1);
    if (now.isBefore(lastDay)) {
      return (now.get('year') - 1);
    }
    return now.get('year');
  }

  getAnnoScolastico(data: moment.Moment) {
    var year = data.year();
    var startAcademicYear = moment().set({ 'year': year, 'month': 8, 'date': 1 });
    var isNewAcademicYear = data.isSameOrAfter(startAcademicYear);
    var startYear = isNewAcademicYear ? year : year - 1;
    return startYear + "-" + (startYear + 1).toString().substring(2, 4);
  }

  cancel() {
    this.router.navigate(['../../'], { relativeTo: this.route });
  }

  save(myForm) {

    if (this.attivita.referenteScuola && this.attivita.referenteScuola != '' && this.attivita.referenteScuola.trim().length > 0) {
      this.attivita.referenteScuola = this.attivita.referenteScuola.trim();
      this.forceReferenteScuolaErrorDisplay = false;
    } else {
      this.forceReferenteScuolaErrorDisplay = true;
    }

    if (this.attivita.ore && this.attivita.ore > 0) {
      this.forceOreErrorDisplay = false;
    } else {
      this.forceOreErrorDisplay = true;
    }

    if (this.titolo && this.titolo != '' && this.titolo.trim().length > 0) {
      this.attivita.titolo = this.titolo.trim();
      this.forceTitoloErrorDisplay = false;
    } else {
      this.forceTitoloErrorDisplay = true;
    }

    if (!this.attivita.oraInizio) {
      this.forceErrorDisplayOraInizio = true;
    } else {
      this.forceErrorDisplayOraInizio = false;
    }

    if (!this.attivita.oraFine) {
      this.forceErrorDisplayOraFine = true;
    } else {
      this.forceErrorDisplayOraFine = false;
    }

    if (this.attivita.oraInizio > this.attivita.oraFine) {
      this.forceDalleAlleErrorDisplay = true;
    } else {
      this.forceDalleAlleErrorDisplay = false;
    }

    if (!this.tipoInterna) {
      if (this.azienda && this.azienda.id != undefined) {
        this.attivita.nomeEnte = this.azienda.nome;
        this.attivita.enteId = this.azienda.id;
        this.forceEnteDisplay = false;
      } else {
        this.forceEnteDisplay = true;
      }

      if (this.attivita.referenteEsterno && this.attivita.referenteEsterno != '' && this.attivita.referenteEsterno.trim().length > 0) {
        this.attivita.referenteEsterno = this.attivita.referenteEsterno.trim();
        this.forceReferenteEsternoErrorDisplay = false;
      } else {
        this.forceReferenteEsternoErrorDisplay = true;
      }
    }

    if ((this.date.dataInizio < this.date.minInizio) || (this.date.dataFine > this.date.maxFine)
      || (this.date.dataInizio > this.date.dataFine)) {
      this.forceAnnoScolasticoErrorDisplay = true;
    } else {
      this.forceAnnoScolasticoErrorDisplay = false;
    }

    if (this.place) {
      this.attivita.luogoSvolgimento = this.place.name;
      this.attivita.latitude = this.place.location[0];
      this.attivita.longitude = this.place.location[1];
    }

    this.attivita.dataInizio = moment(this.date.dataInizio, 'YYYY-MM-DD').valueOf();
    this.attivita.dataFine = moment(this.date.dataFine, 'YYYY-MM-DD').valueOf();
    this.attivita.annoScolastico = this.schoolYear;

    if (!this.forceEnteDisplay && !this.forceTitoloErrorDisplay && !this.forceReferenteScuolaErrorDisplay
      && !this.forceReferenteEsternoErrorDisplay && !this.forceOreErrorDisplay && !this.forceAnnoScolasticoErrorDisplay
      && !this.forceDalleAlleErrorDisplay && !this.forceErrorDisplayOraInizio && !this.forceErrorDisplayOraFine) {

      (this.attivita.descrizione) ? this.attivita.descrizione = this.attivita.descrizione.trim() : this.attivita.descrizione = null;
      (this.attivita.formatore) ? this.attivita.formatore = this.attivita.formatore.trim() : this.attivita.formatore = null;
      (this.attivita.formatoreCF) ? this.attivita.formatoreCF = this.attivita.formatoreCF.trim() : this.attivita.formatoreCF = null;
      (this.attivita.referenteScuolaCF) ? this.attivita.referenteScuolaCF = this.attivita.referenteScuolaCF.trim() : this.attivita.referenteScuolaCF = null;
      (this.attivita.referenteEsternoCF) ? this.attivita.referenteEsternoCF = this.attivita.referenteEsternoCF.trim() : this.attivita.referenteEsternoCF = null;

      this.dataService.createAttivitaAlternanza(this.attivita).subscribe((res => {
        this.router.navigate(['../../'], { relativeTo: this.route });
      }));
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

  searching = false;
  searchFailed = false;
  getAddresses = (text$: Observable<string>) =>
    text$.pipe(
      debounceTime(500),
      distinctUntilChanged(),
      tap(() => this.searching = true),
      switchMap(term => this.geoService.getLocations(term).pipe(
        map(places => {
          return this.geoService.createPlaces(places);
        }),
        tap(() => this.searchFailed = false),
        catchError((error) => {
          console.log(error);
          this.searchFailed = true;
          return of([]);
        })
      )),
      tap(() => this.searching = false)
    )

  formatterAddress = (x: { name: string }) => x.name;

  menuContentShow() {
    this.showContent = !this.showContent;
  }

  getStatoNome(statoValue) {
    if (this.stati) {
      let rtn = this.stati.find(data => data.value == statoValue);
      if (rtn) return rtn.name;
      return statoValue;
    }
  }

  trimValue(event, type) {
    if (type == 'titolo') {
      (event.target.value.trim().length == 0) ? this.forceTitoloErrorDisplay = true : this.forceTitoloErrorDisplay = false;
    } else if (type == 'scolastico') {
      (event.target.value.trim().length == 0) ? this.forceReferenteScuolaErrorDisplay = true : this.forceReferenteScuolaErrorDisplay = false;
    } else if (type == 'esterno') {
      (event.target.value.trim().length == 0) ? this.forceReferenteEsternoErrorDisplay = true : this.forceReferenteEsternoErrorDisplay = false;
    } else if (type == 'trim') {
      event.target.value = event.target.value.trim();
    }
  }

  changeDate(event: any) {
    this.checkDate();
    this.schoolYears = [];
    if(this.zeroStudent) {
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
    } else {
      this.schoolYear = this.attivita.annoScolastico.slice();
      this.schoolYears.push(this.schoolYear.slice());
    }
  }

  formatTwoDigit(n){
    n = parseInt(n); //ex. if already passed '05' it will be converted to number 5
    var ret = n > 9 ? "" + n: "0" + n;
    return ret;
  }

  isRendicontazioneOre(aa) {
    if (aa.rendicontazioneCorpo) {
      this.modalita = 'Rendicontazione a corpo';
    } else {
      this.modalita = 'Rendicontazione ore giornaliera';
    }
  }

  searchingRIF = false;
  searchFailedRIF = false;
  
  getRiferente = (text$: Observable<string>) =>
    text$.pipe(
      debounceTime(500),
      distinctUntilChanged(),
      tap(() => this.searchingRIF = true),
      switchMap(term => this.dataService.getRiferente(term).pipe(
        map(items => {
          // non existing riferente hack
          if (items.length < 1) {
            this.attivita.referenteScuolaCF = null;
            this.attivita.referenteScuolaEmail = null;
            this.attivita.referenteScuola = this.riferente;
          }
          return items;
        }),
        tap(() => {
          this.searchFailedRIF = false
        }),
        catchError((error) => {
          console.log(error);
          this.searchFailedRIF = true;
          return of([]);
        })
      )),
      tap(() => this.searching = false)
    )

  formatterReferente = (x: { nominativoDocente: string }) => x.nominativoDocente;

  selectedRiferente($event) {
    this.attivita.referenteScuolaCF = null;
    this.attivita.referenteScuolaEmail = null;
    if ($event.item.cfDocente) {
      this.forceSelectionMsg = true;
      this.attivita.referenteScuolaCF = $event.item.cfDocente;
    }
    if ($event.item.nominativoDocente) {
      this.attivita.referenteScuola = $event.item.nominativoDocente;
    }
    if ($event.item.emailDocente) {
      this.attivita.referenteScuolaEmail = $event.item.emailDocente;
    }
  }

  
  
}
