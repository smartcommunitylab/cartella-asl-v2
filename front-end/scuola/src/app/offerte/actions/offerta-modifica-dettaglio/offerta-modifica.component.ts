import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../../../core/services/data.service';
import { GeoService } from '../../../core/services/geo.service';
import { Router, ActivatedRoute } from '@angular/router';
import { GrowlerService, GrowlerMessageType } from '../../../core/growler/growler.service';
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
    private geoService: GeoService,
    private growler: GrowlerService) { }

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
  
  forceTitoloErrorDisplay: boolean = false;
  forceDalle23ErrorDisplay: boolean = false;
  forceAlle23ErrorDisplay: boolean = false;
  forceDalleAlleErrorDisplay: boolean = false;
  forcePostiErrorDisplay: boolean = false;
   forceReferenteScuolaErrorDisplay: boolean = false;
  forceReferenteEsternoErrorDisplay: boolean = false;
  forceOreErrorDisplay: boolean = false;
  forceEnteDisplay: boolean = false;
 
  menuContent = "In questa pagina trovi tutti i dati dell’offerta. Puoi modificare ogni sezione utilizzando i tasti blu “Modifica”. Puoi creare una nuova attività con i dati di questa offerta con il tasto “crea attività da offerta”. Puoi eliminare definitivamente questa offerta con il tasto elimina: in questo caso, le attività che hai già creato non saranno influenzate dall’eliminazione dell’offerta.";
  showContent: boolean = false;
  tipoInterna: boolean = false;
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
              this.tipoInterna = tipo.interna;
              this.offertaTipologia = tipo.titolo;
            }
          })
          if (this.offerta.enteId && !this.tipoInterna) {
            this.dataService.getAzienda(this.offerta.enteId).subscribe((az => {
              this.azienda = az;
            }))
          }
          var dataInizio = new Date(this.offerta.dataInizio);
          this.date.dataInizio = moment(dataInizio.getTime());
          var dataFine = new Date(this.offerta.dataFine);
          this.date.dataFine = moment(dataFine);
          this.titolo = this.offerta.titolo;
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
    
    if (this.offerta.referenteScuola && this.offerta.referenteScuola != '' && this.offerta.referenteScuola.trim().length > 0) {
      this.offerta.referenteScuola = this.offerta.referenteScuola.trim();
      this.forceReferenteScuolaErrorDisplay = false;
    } else {
      this.forceReferenteScuolaErrorDisplay = true;
    }

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
    
    if (this.offerta.oraInizio > 23) {
      this.forceDalle23ErrorDisplay = true;
    } else {
      this.forceDalle23ErrorDisplay = false;
    }
    
    if (this.offerta.oraFine > 23) {
      this.forceAlle23ErrorDisplay = true;
    } else {
      this.forceAlle23ErrorDisplay = false;
    }
    
    if (this.offerta.oraInizio > this.offerta.oraFine) {
      this.forceDalleAlleErrorDisplay = true;
    } else {
      this.forceDalleAlleErrorDisplay = false;
    }

    if (!this.tipoInterna) {
     
      if (this.azienda && this.azienda.id != '') {
        this.offerta.nomeEnte = this.azienda.nome;
        this.offerta.enteId = this.azienda.id;
        this.forceEnteDisplay = false;
      } else {
        this.forceEnteDisplay = true;
      }

      if (this.offerta.referenteEsterno && this.offerta.referenteEsterno != '' && this.offerta.referenteEsterno.trim().length > 0) {
        this.offerta.referenteEsterno = this.offerta.referenteEsterno.trim();
        this.forceReferenteEsternoErrorDisplay = false;
      } else {
        this.forceReferenteEsternoErrorDisplay = true;
      }

    }

    if (this.place) {
      this.offerta.luogoSvolgimento = this.place.name;
      this.offerta.latitude = this.place.location[0];
      this.offerta.longitude = this.place.location[1];
    }
    
    this.offerta.dataInizio = moment(this.date.dataInizio, 'YYYY-MM-DD').valueOf();
    this.offerta.dataFine = moment(this.date.dataFine, 'YYYY-MM-DD').valueOf();

    if (!this.forceEnteDisplay && !this.forceTitoloErrorDisplay && !this.forceReferenteScuolaErrorDisplay
      && !this.forceReferenteEsternoErrorDisplay && !this.forceOreErrorDisplay
      && !this.forceDalle23ErrorDisplay && !this.forceAlle23ErrorDisplay && !this.forceDalleAlleErrorDisplay) {
      
      (this.offerta.descrizione) ? this.offerta.descrizione = this.offerta.descrizione.trim() : this.offerta.descrizione = null;
      (this.offerta.formatore) ? this.offerta.formatore = this.offerta.formatore.trim() : this.offerta.formatore = null;
      (this.offerta.formatoreCF) ? this.offerta.formatoreCF = this.offerta.formatoreCF.trim() : this.offerta.formatoreCF = null;
      (this.offerta.referenteScuolaCF) ? this.offerta.referenteScuolaCF = this.offerta.referenteScuolaCF.trim() : this.offerta.referenteScuolaCF = null;
      (this.offerta.referenteEsternoCF) ? this.offerta.referenteEsternoCF = this.offerta.referenteEsternoCF.trim() : this.offerta.referenteEsternoCF = null;

      this.dataService.createOfferta(this.offerta).subscribe(() => {
        this.router.navigate(['../../'], { relativeTo: this.route });
      },err => {
          console.log(err);
          if (err.indexOf("non ha abbastanza posti disponibili") != -1) {
            // this.growler.growl(err.error.message, GrowlerMessageType.Danger, 5000);
            this.forcePostiErrorDisplay = true;
          }
      });
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
}
