import { Component, OnInit, Input, Output, EventEmitter, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DataService } from '../../../core/services/data.service';
import { GeoService } from '../../../core/services/geo.service';
import { FormGroup } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { catchError, debounceTime, distinctUntilChanged, map, tap, switchMap } from 'rxjs/operators';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'modifica-ente-modal',
  templateUrl: './modifica-ente-modal.component.html',
  styleUrls: ['./modifica-ente-modal.component.scss']
})
export class ModificaEnteModalComponent implements OnInit {

  constructor(
    public activeModal: NgbActiveModal,
    private dataService: DataService,
    private geoService: GeoService) { }

  selectedLocationMarker;
  titolo;
  myForm: FormGroup;
  pageSize = 20;
  azienda: any;
  place: any;
  forceErrorDisplay: boolean = false;
  forceTitoloErrorDisplay: boolean = false;
  forceAddressDisplay: boolean = false;;
  showContent: boolean = false;
  tipoInterna: boolean = false;
  ateco: { codice: string, descrizione: string };
  atecoEntry: any;
  attachedAteco = [];
  menuContent = "In questa pagina trovi tutte le informazioni relative all’attività che stai visualizzando.";
  tipoAzienda = [{ "id": 1, "value": "Associazione" }, { "id": 5, "value": "Cooperativa" }, { "id": 10, "value": "Impresa" }, { "id": 15, "value": "Libero professionista" }, { "id": 20, "value": "Pubblica amministrazione" }, { "id": 25, "value": "Ente privato/Fondazione" }];

  breadcrumbItems = [
    {
      title: "Dettaglio ente",
      location: "../../",
    },
    {
      title: "Modifica dati ente"
    }
  ];

  @ViewChild('enteForm') enteForm: FormGroup;

  @Input() ente?: any;
  @Output() updatedEnteListener = new EventEmitter<Object>();


  ngOnInit() {
    this.ateco = { codice: '', descrizione: '' };
    this.atecoEntry = { codice: '', descrizione: '' };
    if (this.ente.address) {
      this.ente.address = this.ente.address.trim();
      // this.place = {};
      // this.place.name = this.ente.address;
      // this.place.location = [this.ente.coordinate.latitude, this.ente.coordinate.longitude];
    }
    
    if (this.ente.atecoCode) {
      for (var i = 0; i < this.ente.atecoCode.length; i++) {
        let atecoEntry = {codice: this.ente.atecoCode[i], descrizione: this.ente.atecoDesc[i]};
        this.attachedAteco.push(atecoEntry);
      }
    }   
  
  }

  salva() {

    if (this.allValidated()) {

      // this.ente.coordinate = null;
      // if (this.place.location) {
      //   this.ente.address = this.place.name;
      //   this.ente.latitude = this.place.location[0];
      //   this.ente.longitude = this.place.location[1];
      // } else {
      //   this.ente.address = this.place;
      //   this.ente.latitude = null;
      //   this.ente.longitude = null;
      // }

      (this.ente.nome) ? this.ente.nome = this.ente.nome.trim() : this.ente.nome = null;
      (this.ente.email) ? this.ente.email = this.ente.email.trim() : this.ente.email = null;
      (this.ente.pec) ? this.ente.pec = this.ente.pec.trim() : this.ente.pec = null;
      (this.ente.phone) ? this.ente.phone = this.ente.phone.trim() : this.ente.phone = null;


      this.ente.atecoCode = [];
      this.ente.atecoDesc = [];

      if (this.attachedAteco && this.attachedAteco.length > 0) {
        this.attachedAteco.forEach(ateco => {
          this.ente.atecoCode.push(ateco.codice);
          this.ente.atecoDesc.push(ateco.descrizione)
        })
      } 

      delete this.ente['registrazioneEnte'];
      
      this.updatedEnteListener.emit(this.ente);
      this.activeModal.dismiss('create')


    } else {
      this.forceErrorDisplay = true;
      if (this.checkPlace()) {
        this.forceAddressDisplay = false;
      } else {
        this.forceAddressDisplay = true;
      }
      if (this.ente.nome && this.ente.nome != '' && this.ente.nome.trim().length > 0) {
        this.forceTitoloErrorDisplay = false;
      } else {
        this.forceTitoloErrorDisplay = true;
      }
    }
  }

  allValidated() {
    return (
      (this.ente.nome && this.ente.nome != '' && this.ente.nome.trim().length > 0)
      && (this.ente.partita_iva && this.ente.partita_iva != '')
      // && this.checkPlace()
      && (this.ente.idTipoAzienda && this.ente.idTipoAzienda != '' && this.ente.idTipoAzienda != 'Tipo'));
  }

  checkPlace() {
    if (this.place && this.place.location) {
      return true;
    }
    if (typeof this.place === 'string') {
      return this.place.trim() != ''
    }
    return false;
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

  searchingAteco = false;
  searchAtecoFailed = false;

  getAteco = (text$: Observable<string>) =>
    text$.pipe(
      debounceTime(500),
      distinctUntilChanged(),
      tap(() => this.searchingAteco = true),
      switchMap(term => this.dataService.getAteco(term).pipe(
        map(result => {
          let entries = [];
          if (result.Entries && result.Entries.Entry) {
            result.Entries.Entry.forEach(element => {
              entries.push(element);
            });
          }
          return entries;
        }),
        tap(() => this.searchAtecoFailed = false),
        catchError((error) => {
          console.log(error);
          this.searchAtecoFailed = true;
          return of([]);
        })
      )),
      tap(() => this.searchingAteco = false)
    )

  formatterAteco = (x: { codice: string, descrizione: string }) => x.codice;

  selectAteco(entry: any) {
    if (entry && entry.codice && entry.descrizione) {
      let index = this.attachedAteco.findIndex(x => x.codice === entry.codice);
      if (index < 0) {
        this.attachedAteco.push(entry);
      }
    }
  }

  deleteAteco(entry: any) {
    let index = this.attachedAteco.findIndex(x => x.codice === entry.codice);
    if (index > -1) {
      this.attachedAteco.splice(index, 1);
    }
  }

  menuContentShow() {
    this.showContent = !this.showContent;
  }

  selectPlace(placeObj) {
    if (placeObj.location) {
      this.forceAddressDisplay = false;
      this.ente.address = placeObj.name;
      this.ente.latitude = placeObj.location[0];
      this.ente.longitude = placeObj.location[1];
    }
  }

  trimValue(event, type) {
    if (type == 'titolo') {
      (event.target.value.trim().length == 0) ? this.forceTitoloErrorDisplay = true : this.forceTitoloErrorDisplay = false;
    }
  }

}
