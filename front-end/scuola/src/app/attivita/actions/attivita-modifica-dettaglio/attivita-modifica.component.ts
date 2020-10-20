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
import { GrowlerService, GrowlerMessageType } from '../../../core/growler/growler.service';

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
    private geoService: GeoService,
    private growler: GrowlerService) { }

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
  forceErrorDisplayOraInizio: boolean = false;
  forceErrorDisplayOraFine: boolean = false;
  menuContent = "In questa pagina trovi tutte le informazioni relative all’attività che stai visualizzando.";
  showContent: boolean = false;
  tipoInterna: boolean = false;
  attivitaTipologia;
  stati = [{ "name": "In attesa", "value": "in_attesa" }, { "name": "In corso", "value": "in_corso" }, { "name": "Revisionare", "value": "revisione" }, { "name": "Archiviata", "value": "archiviata" }];
  attivitaStato: string = "";
  evn = environment;
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
    this.evn.modificationFlag=true;
    this.date = {
      dataInizio: moment(),
      dataFine: moment(),
      prevDataInizio: moment()
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
          this.date.prevDataInizio = moment(dataInizio.getTime());
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

  ngOnDestroy(){
    this.evn.modificationFlag=false;
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

    if (this.attivita.oraInizio > 23) {
      this.forceDalle23ErrorDisplay = true;
    } else {
      this.forceDalle23ErrorDisplay = false;
    }

    if (!this.attivita.oraFine) {
      this.forceErrorDisplayOraFine = true;
    } else {
      this.forceErrorDisplayOraFine = false;
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

      if (this.attivita.referenteEsterno && this.attivita.referenteEsterno != '' && this.attivita.referenteEsterno.trim().length > 0) {
        this.attivita.referenteEsterno =  this.attivita.referenteEsterno.trim();
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
      && !this.forceDalle23ErrorDisplay && !this.forceAlle23ErrorDisplay && !this.forceDalleAlleErrorDisplay
      && !this.forceErrorDisplayOraInizio && !this.forceErrorDisplayOraFine) {
 
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
    if(type == 'titolo'){
      (event.target.value.trim().length == 0) ? this.forceTitoloErrorDisplay = true : this.forceTitoloErrorDisplay   = false;
    } else if(type == 'scolastico'){
      (event.target.value.trim().length == 0) ? this.forceReferenteScuolaErrorDisplay = true : this.forceReferenteScuolaErrorDisplay = false;
    }else if(type == 'esterno'){
      (event.target.value.trim().length == 0) ? this.forceReferenteEsternoErrorDisplay = true : this.forceReferenteEsternoErrorDisplay = false;
    }else if(type == 'trim'){
      event.target.value = event.target.value.trim(); 
    }
  }

  changeDate(event: any) {
    if(this.date.dataInizio) {
      if(!this.date.dataInizio.isSame(this.date.prevDataInizio)) {
        var nuovoAnnoScolastico = this.getAnnoScolastico(this.date.dataInizio);
        console.log(nuovoAnnoScolastico);  
        if(this.attivita.annoScolastico === nuovoAnnoScolastico) {
          this.date.prevDataInizio = moment(this.date.dataInizio);
        } else {
          this.growler.growl("Attenzione, non è possibile cambiare l'anno scolastico!", GrowlerMessageType.Warning, 3000);
          this.date.dataInizio = moment(this.date.prevDataInizio);
        }
      }
    }    
  }

  getAnnoScolastico(data: moment.Moment) {
    var year = data.year();
    var startAcademicYear = moment().set({'year': year, 'month': 8, 'date': 1});
    var isNewAcademicYear = data.isSameOrAfter(startAcademicYear);
    var startYear = isNewAcademicYear ? year : year - 1;
    return startYear + "-" + (startYear + 1).toString().substring(2, 4);
  }
}
