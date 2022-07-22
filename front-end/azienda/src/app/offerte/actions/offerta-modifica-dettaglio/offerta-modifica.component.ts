import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../../../core/services/data.service';
import { GeoService } from '../../../core/services/geo.service';
import { Router, ActivatedRoute } from '@angular/router';
import { Azienda } from '../../../shared/interfaces';
import { Observable, of } from 'rxjs';
import { catchError, debounceTime, distinctUntilChanged, map, tap, switchMap } from 'rxjs/operators';
import * as moment from 'moment';
import { DatePickerComponent } from 'ng2-date-picker';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'cm-modifica-offerta',
  templateUrl: './offerta-modifica.component.html',
  styleUrls: ['./offerta-modifica.component.scss']
})
export class OffertaDettaglioModificaComponent implements OnInit {

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private dataService: DataService,
    private geoService: GeoService) { }

  offerta;
  titolo;
  date;
  start: moment.Moment;
  end: moment.Moment;
  descrizione;
  tipologie;
  offertaTipologia;
  azienda: any;
  aziende: Azienda[];
  place: any;
  pageSize = 20;
  orari = ['00', '01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23']
  forceTitoloErrorDisplay: boolean = false;
  forceErrorDisplayOraInizio: boolean = false;
  forceDalleAlleErrorDisplay: boolean = false;
  forceErrorDisplayOraFine: boolean = false;
  forcePostiErrorDisplay: boolean = false;
  forceReferenteEsternoErrorDisplay: boolean = false;
  forceOreErrorDisplay: boolean = false;
  forceEnteDisplay: boolean = false;
  forceErrorInvalidInizioData: boolean = false;
  forceErrorInvalidFineData: boolean = false;
  menuContent = "In questa pagina trovi tutti i dati dell’offerta. Puoi modificare ogni sezione utilizzando i tasti blu “Modifica”. Puoi creare una nuova attività con i dati di questa offerta con il tasto “crea attività da offerta”. Puoi eliminare definitivamente questa offerta con il tasto elimina: in questo caso, le attività che hai già creato non saranno influenzate dall’eliminazione dell’offerta.";
  showContent: boolean = false;
  evn = environment;
  breadcrumbItems = [
    {
      title: "Dettaglio offerta",
      location: "../../",
    },
    {
      title: "Modifica dati offerta"
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
    this.date = {
      dataInizio: moment(),
      dataFine: moment()
    }

    this.route.params.subscribe(params => {
      let id = params['id'];
      this.dataService.getAttivitaTipologie().subscribe((res) => {
        this.tipologie = res;
        this.dataService.getOfferta(id).subscribe((off) => {
          this.offerta = off;
          this.place = {};
          this.place.name = this.offerta.luogoSvolgimento;
          this.place.location = [this.offerta.latitude, this.offerta.longitude];
          this.tipologie.filter(tipo => {
            if (tipo.id == this.offerta.tipologia) {
              this.offertaTipologia = tipo.titolo;
            }
          })
          var dataInizio = new Date(this.offerta.dataInizio);
          this.date.dataInizio = moment(dataInizio.getTime());
          var dataFine = new Date(this.offerta.dataFine);
          this.date.dataFine = moment(dataFine);
          this.titolo = this.offerta.titolo;
          // tackle incase saved with 1 digit in past.
          this.offerta.oraInizio = this.dataService.formatTwoDigit(this.offerta.oraInizio);
          this.offerta.oraFine = this.dataService.formatTwoDigit(this.offerta.oraFine);
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

  cancel() {
    this.router.navigate(['../../'], { relativeTo: this.route });
  }

  save() {
    if (this.offerta.ore && this.offerta.ore > 0) {
      this.forceOreErrorDisplay = false;
    } else {
      this.forceOreErrorDisplay = true;
    }
    if (this.offerta.postiDisponibili > 0) {
      this.forcePostiErrorDisplay = false;
    } else {
      this.forcePostiErrorDisplay = true;
    }
    if (this.titolo && this.titolo != '' && this.titolo.trim().length > 0) {
      this.offerta.titolo = this.titolo.trim();
      this.forceTitoloErrorDisplay = false;
    } else {
      this.forceTitoloErrorDisplay = true;
    }
    if (!this.offerta.oraInizio) {
      this.forceErrorDisplayOraInizio = true;
    } else {
      this.forceErrorDisplayOraInizio = false;
    }
    if (!this.offerta.oraFine) {
      this.forceErrorDisplayOraFine = true;
    } else {
      this.forceErrorDisplayOraFine = false;
    }
    if (this.offerta.oraInizio > this.offerta.oraFine) {
      this.forceDalleAlleErrorDisplay = true;
    } else {
      this.forceDalleAlleErrorDisplay = false;
    }
    if (this.offerta.referenteEsterno && this.offerta.referenteEsterno != '' && this.offerta.referenteEsterno.trim().length > 0) {
      this.offerta.referenteEsterno = this.offerta.referenteEsterno.trim();
      this.forceReferenteEsternoErrorDisplay = false;
    } else {
      this.forceReferenteEsternoErrorDisplay = true;
    }
    if (this.place) {
      if (this.place.location) {
        this.offerta.luogoSvolgimento = this.place.name;
        this.offerta.latitude = this.place.location[0];
        this.offerta.longitude = this.place.location[1];
      } else {
        this.offerta.luogoSvolgimento = this.place;
        this.offerta.latitude = null;
        this.offerta.longitude = null;
      }
    }

    this.offerta.dataInizio = moment(this.date.dataInizio, 'YYYY-MM-DD').valueOf();
    this.offerta.dataFine = moment(this.date.dataFine, 'YYYY-MM-DD').valueOf();

    if (!this.forceEnteDisplay && !this.forceTitoloErrorDisplay
      && !this.forceReferenteEsternoErrorDisplay && !this.forceOreErrorDisplay
      && !this.forceDalleAlleErrorDisplay && !this.forceErrorDisplayOraInizio && !this.forceErrorDisplayOraFine
      && !this.forceErrorInvalidFineData && !this.forceErrorInvalidInizioData) {
      (this.offerta.descrizione) ? this.offerta.descrizione = this.offerta.descrizione.trim() : this.offerta.descrizione = null;
      (this.offerta.formatore) ? this.offerta.formatore = this.offerta.formatore.trim() : this.offerta.formatore = null;
      (this.offerta.formatoreCF) ? this.offerta.formatoreCF = this.offerta.formatoreCF.trim() : this.offerta.formatoreCF = null;
      (this.offerta.referenteScuolaCF) ? this.offerta.referenteScuolaCF = this.offerta.referenteScuolaCF.trim() : this.offerta.referenteScuolaCF = null;
      (this.offerta.referenteEsternoCF) ? this.offerta.referenteEsternoCF = this.offerta.referenteEsternoCF.trim() : this.offerta.referenteEsternoCF = null;

      this.dataService.createOfferta(this.offerta).subscribe(() => {
        this.router.navigate(['../../'], { relativeTo: this.route });
      }, err => {
        console.log(err);
        if (err.indexOf("non ha abbastanza posti disponibili") != -1) {
          this.forcePostiErrorDisplay = true;
        }
      });
    }

  }

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

  trimValue(event, type) {
    if (type == 'titolo') {
      (event.target.value.trim().length == 0) ? this.forceTitoloErrorDisplay = true : this.forceTitoloErrorDisplay = false;
    }
    else if (type == 'esterno') {
      (event.target.value.trim().length == 0) ? this.forceReferenteEsternoErrorDisplay = true : this.forceReferenteEsternoErrorDisplay = false;
    } else if (type == 'trim') {
      event.target.value = event.target.value.trim();
    }
  }

  validateFormat() {
    if (!moment(this.date.dataInizio).isValid()) {
      this.forceErrorInvalidInizioData = true;
    } else {
      this.forceErrorInvalidInizioData = false;
    }

    if (!moment(this.date.dataFine).isValid()) {
      this.forceErrorInvalidFineData = true;
    } else {
      this.forceErrorInvalidFineData = false;
    }
  }

}
