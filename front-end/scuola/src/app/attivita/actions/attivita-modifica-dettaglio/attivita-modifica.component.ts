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
  date;
  forceEnteDisplay: boolean = false;
  start: moment.Moment;
  end: moment.Moment;
  forceTitoloErrorDisplay: boolean = false;
  forceReferenteScuolaErrorDisplay: boolean = false;
  forceReferenteEsternoErrorDisplay: boolean = false;
  forceOreErrorDisplay: boolean = false;
  forceDalle23ErrorDisplay: boolean = false;
  forceAlle23ErrorDisplay: boolean = false;
  forceDalleAlleErrorDisplay: boolean = false;
  menuContent = "In questa pagina trovi tutte le informazioni relative all’attività che stai visualizzando.";
  showContent: boolean = false;
  tipoInterna: boolean = false;
  attivitaTipologia;
  stati = [{ "name": "In attesa", "value": "in_attesa" }, { "name": "In corso", "value": "in_corso" }, { "name": "Revisionare", "value": "revisione" }, { "name": "Archiviata", "value": "archiviata" }];
  attivitaStato: string = "";
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

    this.date = {
      dataInizio: moment(),
      dataFine: moment()
    }

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
          this.esperienze = res.esperienze;
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
          this.date.dataInizio = moment(dataInizio.getTime());
          var dataFine = new Date(this.attivita.dataFine);
          this.date.dataFine = moment(dataFine);

          this.titolo = this.attivita.titolo;

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

          // this.dataService.getListaAziende(null,0, this.pageSize).subscribe((response) => {
          //   this.aziende = response.content;
          //   this.aziende.filter(az => {
          //     if (az.id == this.attivita.enteId) {
          //       this.azienda = az;
          //     }
          //   });

          // });

        }, (err: any) => console.log(err),
          () => console.log('getAttivitaTipologie'));
      });
    });
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

  save(myForm) {

    if (this.attivita.referenteScuola && this.attivita.referenteScuola != '') {
      this.forceReferenteScuolaErrorDisplay = false;
    } else {
      this.forceReferenteScuolaErrorDisplay = true;
    }

    if (this.attivita.ore && this.attivita.ore > 0) {
      this.forceOreErrorDisplay = false;
    } else {
      this.forceOreErrorDisplay = true;
    }

    if (this.titolo && this.titolo != '') {
      this.attivita.titolo = this.titolo;
      this.forceTitoloErrorDisplay = false;
    } else {
      this.forceTitoloErrorDisplay = true;
    }

    if (this.attivita.oraInizio > 23) {
      this.forceDalle23ErrorDisplay = true;
    } else {
      this.forceDalle23ErrorDisplay = false;
    }

    if (this.attivita.oraFine > 23) {
      this.forceAlle23ErrorDisplay = true;
    } else {
      this.forceAlle23ErrorDisplay = false;
    }

    if (this.attivita.oraInizio > this.attivita.oraFine) {
      this.forceDalleAlleErrorDisplay = true;
    } else {
      this.forceDalleAlleErrorDisplay = false;

    }

    if (!this.tipoInterna) {
      if (this.azienda && this.azienda.id != '') {
        this.attivita.nomeEnte = this.azienda.nome;
        this.attivita.enteId = this.azienda.id;
        this.forceEnteDisplay = false;
      } else {
        this.forceEnteDisplay = true;
      }

      if (this.attivita.referenteEsterno && this.attivita.referenteEsterno != '') {
        this.forceReferenteEsternoErrorDisplay = false;
      } else {
        this.forceReferenteEsternoErrorDisplay = true;
      }
    }

    if (this.place) {
      this.attivita.luogoSvolgimento = this.place.name;
      this.attivita.latitude = this.place.location[0];
      this.attivita.longitude = this.place.location[1];
    }

    this.attivita.dataInizio = moment(this.date.dataInizio, 'YYYY-MM-DD').valueOf();
    this.attivita.dataFine = moment(this.date.dataFine, 'YYYY-MM-DD').valueOf();

    if (!this.forceEnteDisplay && !this.forceTitoloErrorDisplay && !this.forceReferenteScuolaErrorDisplay
      && !this.forceReferenteEsternoErrorDisplay && !this.forceOreErrorDisplay
      && !this.forceDalle23ErrorDisplay && !this.forceAlle23ErrorDisplay && !this.forceDalleAlleErrorDisplay) {
      this.dataService.createAttivitaAlternanza(this.attivita).subscribe((res => {
        this.router.navigate(['../../'], { relativeTo: this.route });
      }));
    }

  }

  // search = (text$: Observable<string>) =>
  // text$.pipe(
  //   debounceTime(200),
  //   distinctUntilChanged(),
  //   map(term => {
  //     if (parseInt("0" + term, 10) > 0) {
  //       return this.aziende.filter(az => az.partita_iva.indexOf(term) > -1).slice(0, 10);
  //     } else {
  //       return this.aziende.filter(az => az.nome.toUpperCase().indexOf(term.toUpperCase()) > -1).slice(0, 10);
  //     }
  //   })      
  // )

  searching = false;
  searchFailed = false;

  search = (text$: Observable<string>) =>
    text$.pipe(
      debounceTime(500),
      distinctUntilChanged(),
      tap(() => this.searching = true),
      switchMap(term =>
        this.dataService.searchEnte(term).pipe(
          tap(() => {
            this.searchFailed = false
          }),
          catchError(() => {
            this.searchFailed = true;
            return of([]);
          }))
      ),
      tap(() => {
        this.searching = false
      })
    )

  formatter = (x: { nome: string }) => x.nome;

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

}
