import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../../../core/services/data.service';
import { GeoService } from '../../../core/services/geo.service';
import { Router, ActivatedRoute } from '@angular/router';
import { AttivitaAlternanza } from '../../../shared/classes/AttivitaAlternanza.class';
import { FormGroup, FormControl } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { catchError, debounceTime, distinctUntilChanged, map, tap, switchMap } from 'rxjs/operators';
import * as Leaflet from 'leaflet';

@Component({
  selector: 'cm-ente-dettaglio',
  templateUrl: './ente-modifica.component.html',
  styleUrls: ['./ente-modifica.component.scss']
})
export class EnteDettaglioModificaComponent implements OnInit {

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private dataService: DataService,
    private geoService: GeoService) { }

  ente;
  attivita: AttivitaAlternanza;
  map;
  selectedLocationMarker;
  titolo;
  myForm:  FormGroup;
  pageSize = 20;
  azienda: any;
  place: any;
  forceErrorDisplay: boolean = false;
  forceTitoloErrorDisplay: boolean = false;
  forceAddressDisplay: boolean = false;;
  showContent: boolean = false;
  tipoInterna: boolean = false;
  ateco: {codice: string, descrizione: string};
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

  ngOnInit() {
    this.ateco = {codice: '', descrizione: ''};
    this.atecoEntry = {codice: '', descrizione: ''};
    this.dataService.getAzienda().subscribe((res) => {
      this.ente = res;
      if(this.ente.address) {
        this.ente.address = this.ente.address.trim();
      }
      if (this.ente.atecoCode) {
        for (var i = 0; i < this.ente.atecoCode.length; i++) {
          let atecoEntry = {codice: this.ente.atecoCode[i], descrizione: this.ente.atecoDesc[i]};
          this.attachedAteco.push(atecoEntry);
        }
      }  
      setTimeout(() => { //ensure that map div is rendered
        this.drawMap();
      }, 0);
    },
      (err: any) => console.log(err),
      () => console.log('getAzienda'));
  }

  cancel() {
    this.router.navigate(['../../'], { relativeTo: this.route });
  }

  save() {

    if (this.allValidated()) {

      this.ente.coordinate = null;
      if (this.place.location) {
        this.ente.address = this.place.name;
        this.ente.latitude = this.place.location[0];
        this.ente.longitude = this.place.location[1];
      } else {
        this.ente.address = this.place;
        this.ente.latitude = null;
        this.ente.longitude = null;
      }

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
      
      this.dataService.addAzienda(this.ente).subscribe((res) => {
        this.router.navigate(['../../'], { relativeTo: this.route });
      },
        (err: any) => console.log(err),
        () => console.log('getAttivita'));

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

  onEnteEstero() {
    if(this.ente.estera) {
      this.enteForm.controls['partitaIva'].markAsTouched();
      this.enteForm.controls['partitaIva'].markAsDirty();
    } else {
      this.enteForm.controls['partitaIvaLocale'].markAsTouched();
      this.enteForm.controls['partitaIvaLocale'].markAsDirty();
    }
  }

  allValidated() {
    var partita_iva = false;
    if(this.ente.estera) {
      partita_iva = this.enteForm.controls['partitaIva'].valid.valueOf();
    } else {
      partita_iva = this.enteForm.controls['partitaIvaLocale'].valid.valueOf();
    }

    return (partita_iva
      && (this.ente.nome && this.ente.nome != '' && this.ente.nome.trim().length > 0)
      && (this.ente.partita_iva && this.ente.partita_iva != '')
      && this.checkPlace()
      && (this.ente.idTipoAzienda && this.ente.idTipoAzienda != '' && this.ente.idTipoAzienda != 'Tipo'));
  }

  checkPlace() {
    if(this.place && this.place.location) {
      return true;
    }
    if(typeof this.place === 'string') {
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
          if(result.Entries && result.Entries.Entry) {            
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

  formatterAteco = (x: {codice: string, descrizione: string}) => x.codice;

  menuContentShow() {
    this.showContent = !this.showContent;
  }

  drawMap(): void {
    this.map = Leaflet.map('map');
    Leaflet.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
      center: [46.1025748, 10.927261],
      zoom: 8
    }).addTo(this.map);

    if (this.ente.coordinate && this.ente.coordinate.latitude && this.ente.coordinate.longitude) {
      this.place = {};
      this.place.name = this.ente.address;
      this.place.location = [this.ente.coordinate.latitude, this.ente.coordinate.longitude];
      this.selectedLocationMarker = Leaflet.marker([this.ente.coordinate.latitude, this.ente.coordinate.longitude]).addTo(this.map);
      this.map.setView(this.selectedLocationMarker.getLatLng(), 14);
    } else {
      this.selectedLocationMarker = Leaflet.marker([46.067407, 11.121414]).addTo(this.map);
      this.map.setView(this.selectedLocationMarker.getLatLng(), 14);
    }

    function onLocationFound(e) {
      var radius = e.accuracy / 2;
      Leaflet.marker(e.latlng).addTo(this.map);
      Leaflet.circle(e.latlng, radius).addTo(this.map);
      this.map.setZoom(12);
    }

    this.map.addEventListener('click', (e) => {
      this.geoService.getAddressFromCoordinates(e).then(location => {
        // this.ente.coordinate = [e.latlng.lat, e.latlng.lng];
        var coordinate = [e.latlng.lat, e.latlng.lng];
        this.ente.address = location;
        this.ente.latitude = e.latlng.lat;
        this.ente.longitude = e.latlng.lng;

        if (coordinate) {
          this.place = {};
          this.place.name = this.ente.address;
          this.place.location = coordinate; //[this.ente.coordinate.latitude, this.ente.coordinate.longitude];
          this.selectedLocationMarker.setLatLng(e.latlng).update();
        }
        // this.map.setView(this.selectedLocationMarker.getLatLng());
      });
    });

  }

  selectPlace(placeObj) {
    if (placeObj.location) {
      this.forceAddressDisplay = false;
      this.ente.address = placeObj.name;
      // this.ente.coordinate.latitude = placeObj.location[0];
      // this.ente.coordinate.longitude = placeObj.location[1];
      this.ente.latitude = placeObj.location[0];
      this.ente.longitude = placeObj.location[1];
      this.selectedLocationMarker.setLatLng(placeObj.location).update();
      this.map.setView(this.selectedLocationMarker.getLatLng());
    }
  }

  trimValue(event, type) {
    if (type == 'titolo') {
      (event.target.value.trim().length == 0) ? this.forceTitoloErrorDisplay = true : this.forceTitoloErrorDisplay = false;
    }
  }

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

}
