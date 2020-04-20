import { Component, OnInit, Input, Output, EventEmitter, ViewChild } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DataService } from '../../../core/services/data.service';
import { GeoService } from '../../../core/services/geo.service';
import { Observable, of } from 'rxjs';
import { catchError, debounceTime, distinctUntilChanged, map, tap, switchMap } from 'rxjs/operators';

@Component({
  selector: 'crea-ente-modal',
  templateUrl: './crea-ente-modal.component.html',
  styleUrls: ['./crea-ente-modal.component.scss']
})
  
export class CreaEnteModalComponent implements OnInit {

  nome;
  email;
  address;
  partitaIva;
  pec;
  place;
  phone;
  keyword = 'nome';
  fieldsError: string;
  pageSize = 20;
  forceErrorDisplay: boolean;
  forceAddressDisplay;
  idTipoAzienda: any = 'Tipo';
  tipoAzienda = [{ "id": 1, "value": "Associazione" }, { "id": 5, "value": "Cooperativa" }, { "id": 10, "value": "Impresa" }, { "id": 15, "value": "Libero professionista" }, { "id": 20, "value": "Pubblica amministrazione" }, { "id": 25, "value": "Ente privato/Fondazione" }];

  @Output() newEnteListener = new EventEmitter<Object>();


  constructor(public activeModal: NgbActiveModal, private dataService: DataService, private geoService: GeoService) { }

  ngOnInit() { }

  create() { //create or update
    let ente;
    if (this.allValidated()) {
      ente = {
        nome: this.nome,
        email: this.email,
        partita_iva: this.partitaIva,
        pec: this.pec,
        address: this.place.name,
        latitude: this.place.location[0],
        longitude: this.place.location[1],
        phone: this.phone,
        idTipoAzienda: this.idTipoAzienda
      }
      this.newEnteListener.emit(ente);
      this.activeModal.dismiss('create')
    } else {
      this.forceErrorDisplay = true;
      if (this.place && this.place.location) {
        this.forceAddressDisplay = false;
      } else {
        this.forceAddressDisplay = true;
      }
    }
  }


  allValidated() {
    return ((this.nome && this.nome != '')
      && (this.partitaIva && this.partitaIva != '')
      && (this.place && this.place.location)
      && (this.idTipoAzienda && this.idTipoAzienda !='' && this.idTipoAzienda != 'Tipo'));
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

}
