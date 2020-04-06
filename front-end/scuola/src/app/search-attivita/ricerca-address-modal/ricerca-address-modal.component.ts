import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { GeoService } from '../../core/services/geo.service';
import { NgbModal, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import * as Leaflet from 'leaflet';


@Component({
  selector: 'ricerca-address-modal',
  templateUrl: './ricerca-address-modal.html',
  styleUrls: ['./ricerca-address-modal.css']
})
export class ProgrammaRicercaAddressModal implements OnInit {

  @Output() onSelectAddress = new EventEmitter<string>();
  @Input() actualLocation?;
  location: { name: "", coordinates: [0,0] };
  constructor(
    public activeModal: NgbActiveModal,
    public GeoService: GeoService) {
    //this.viewCtrl=viewCtrl;
  }

  selectedLocationMarker;

  ngOnInit(): void {
    if (this.actualLocation) {
      this.location = JSON.parse(this.actualLocation);
      this.selectedLocationMarker = Leaflet.marker(this.location.coordinates);
    }
    this.drawMap();
  }
  select() {
    this.activeModal.close();
    this.onSelectAddress.emit(JSON.stringify(this.location));
  }
  drawMap(): void {
    let map;
    map = Leaflet.map('map');
    Leaflet.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
      maxZoom: 18
    }).addTo(map);
    if (this.selectedLocationMarker) {
      this.selectedLocationMarker.addTo(map);
    }

    //web location
    map.locate({ setView: true });

    //when we have a location draw a marker and accuracy circle
    function onLocationFound(e) {
      var radius = e.accuracy / 2;

      Leaflet.marker(e.latlng).addTo(map);
      // .bindPopup("You are within " + radius + " meters from this point").openPopup();

      Leaflet.circle(e.latlng, radius).addTo(map);

      map.setZoom(12);
    }
    map.addEventListener('locationfound', onLocationFound);
    //alert on location error
    function onLocationError(e) {
      console.log(e.message);
    }
    map.addEventListener('click', (e) => {
      //get address and then open popup
      //open confirm popUp
      this.GeoService.getAddressFromCoordinates(e).then(location => {
        if (!this.selectedLocationMarker) {
          this.selectedLocationMarker = Leaflet.marker(e.latlng).addTo(map);
        } else {
          this.selectedLocationMarker.setLatLng(e.latlng).update();
        }
        map.setView(this.selectedLocationMarker.getLatLng(), 13);
        console.log(e.latlng);
        this.location = {
          name: location,
          coordinates: [e.latlng.lat, e.latlng.lng]
        };
      });
    });
    map.on('locationerror', onLocationError);
  }
}