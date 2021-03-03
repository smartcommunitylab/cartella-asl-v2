import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../../../core/services/data.service';
import { GeoService } from '../../../core/services/geo.service';
import { Router, ActivatedRoute } from '@angular/router';
import { AttivitaAlternanza } from '../../../shared/classes/AttivitaAlternanza.class';
import { FormGroup } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { catchError, debounceTime, distinctUntilChanged, map, tap, switchMap } from 'rxjs/operators';
import * as Leaflet from 'leaflet';
import { ValidationService } from '../../../core/services/validation.service';

@Component({
  selector: 'cm-istituto-dettaglio',
  templateUrl: './istituto-modifica.component.html',
  styleUrls: ['./istituto-modifica.component.scss']
})
export class IstitutoDettaglioModificaComponent implements OnInit {

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private dataService: DataService,
    private validationService: ValidationService,
    private geoService: GeoService) { }

  istituto;
  attivita: AttivitaAlternanza;
  map;
  selectedLocationMarker;
  titolo;
  myForm: FormGroup;
  pageSize = 20;
  azienda: any;
  place: any;
  forceErrorDisplay: boolean = false;
  forceTitoloErrorDisplay: boolean = false;
  forceAddressDisplay: boolean = false;;
  forceErrorDisplayMail: boolean = false;
  showContent: boolean = false;
  tipoInterna: boolean = false;
  menuContent = "In questa pagina trovi tutte le informazioni relative all’attività che stai visualizzando.";
  tipoAzienda = [{ "id": 1, "value": "Associazione" }, { "id": 5, "value": "Cooperativa" }, { "id": 10, "value": "Impresa" }, { "id": 15, "value": "Libero professionista" }, { "id": 20, "value": "Pubblica amministrazione" }, { "id": 25, "value": "istituto privato/Fondazione" }];

  breadcrumbItems = [
    {
      title: "Scheda istituto",
      location: "../../",
    },
    {
      title: "Modifica dati istituto"
    }
  ];

  @ViewChild('istitutoForm') istitutoForm: FormGroup;

  ngOnInit() {

    this.route.params.subscribe(params => {
      let id = params['id'];
      this.dataService.getIstitutoById(this.dataService.istitutoId).subscribe((res) => {
        this.istituto = res;
        setTimeout(() => { //ensure that map div is rendered
          this.drawMap();
        }, 0);
      },
        (err: any) => console.log(err),
        () => console.log('get Istituto'));
    });

  }

  cancel() {
    this.router.navigate(['../../'], { relativeTo: this.route });
  }

  save() {

    if (this.allValidated()) {

      this.istituto.coordinate = null;
      if (this.place.location) {
        this.istituto.address = this.place.name;
        this.istituto.latitude = this.place.location[0];
        this.istituto.longitude = this.place.location[1];
      } else {
        this.istituto.address = this.place;
        this.istituto.latitude = null;
        this.istituto.longitude = null;
      }

      (this.istituto.name) ? this.istituto.name = this.istituto.name.trim() : this.istituto.name = null;
      (this.istituto.email) ? this.istituto.email = this.istituto.email.trim() : this.istituto.email = null;
      (this.istituto.pec) ? this.istituto.pec = this.istituto.pec.trim() : this.istituto.pec = null;
      (this.istituto.phone) ? this.istituto.phone = this.istituto.phone.trim() : this.istituto.phone = '';
      
      // if (!this.istituto.hoursThreshold) {
      //   this.istituto.hoursThreshold = 0;
      // }

      delete this.istituto['coordinate'];
      // delete this.istituto['pec'];
      this.dataService.updateIstituto(this.istituto).subscribe((res) => {
        this.router.navigate(['../../'], { relativeTo: this.route });
      },
        (err: any) => console.log(err),
        () => console.log('update istituto'));

    } else {
      this.forceErrorDisplay = true;
      if (this.checkPlace()) {
        this.forceAddressDisplay = false;
      } else {
        this.forceAddressDisplay = true;
      }
      if (this.istituto.name && this.istituto.name != '' && this.istituto.name.trim().length > 0) {
        this.forceTitoloErrorDisplay = false;
      } else {
        this.forceTitoloErrorDisplay = true;
      }
      if (!this.validationService.isValidEmail(this.istituto.email)) {
        this.forceErrorDisplayMail = true;
      } else {
        this.forceErrorDisplayMail = false;
      }
    }
  }

  allValidated() {
    var partita_iva = false;
    partita_iva = this.istitutoForm.controls['partitaIvaLocale'].valid.valueOf();

    return (partita_iva
      && (this.istituto.name && this.istituto.name != '' && this.istituto.name.trim().length > 0)
      && (this.istituto.cf && this.istituto.cf != '')
      && this.checkPlace()
      && (this.istituto.email && this.istituto.email != '' && this.istituto.email.trim().length > 0 && this.validationService.isValidEmail(this.istituto.email))
    );
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

  menuContentShow() {
    this.showContent = !this.showContent;
  }

  drawMap(): void {
    this.map = Leaflet.map('map');
    Leaflet.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
      cistitutor: [46.1025748, 10.927261],
      zoom: 8
    }).addTo(this.map);

    if (this.istituto.coordinate && this.istituto.coordinate.latitude && this.istituto.coordinate.longitude) {
      this.place = {};
      this.place.name = this.istituto.address;
      this.place.location = [this.istituto.coordinate.latitude, this.istituto.coordinate.longitude];
      this.selectedLocationMarker = Leaflet.marker([this.istituto.coordinate.latitude, this.istituto.coordinate.longitude]).addTo(this.map);
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
        // this.istituto.coordinate = [e.latlng.lat, e.latlng.lng];
        var coordinate = [e.latlng.lat, e.latlng.lng];
        this.istituto.address = location;
        this.istituto.latitude = e.latlng.lat;
        this.istituto.longitude = e.latlng.lng;

        if (coordinate) {
          this.place = {};
          this.place.name = this.istituto.address;
          this.place.location = coordinate; //[this.istituto.coordinate.latitude, this.istituto.coordinate.longitude];
          this.selectedLocationMarker.setLatLng(e.latlng).update();
        }
        // this.map.setView(this.selectedLocationMarker.getLatLng());
      });
    });

  }

  selectPlace(placeObj) {
    if (placeObj.location) {
      this.forceAddressDisplay = false;
      this.istituto.address = placeObj.name;
      // this.istituto.coordinate.latitude = placeObj.location[0];
      // this.istituto.coordinate.longitude = placeObj.location[1];
      this.istituto.latitude = placeObj.location[0];
      this.istituto.longitude = placeObj.location[1];
      this.selectedLocationMarker.setLatLng(placeObj.location).update();
      this.map.setView(this.selectedLocationMarker.getLatLng());
    }
  }

  trimValue(event, type) {
    if (type == 'titolo') {
      (event.target.value.trim().length == 0) ? this.forceTitoloErrorDisplay = true : this.forceTitoloErrorDisplay = false;
    }
  }

}
