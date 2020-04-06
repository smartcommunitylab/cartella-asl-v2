import { Component, OnInit, ViewChild, Input } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { NgForm } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DataService } from '../../core/services/data.service';
import { ModalService } from '../../core/modal/modal.service';
import { IOffer, Competenza, Azienda} from '../../shared/interfaces';
import { GrowlerService, GrowlerMessageType } from '../../core/growler/growler.service';
import { startOfDay } from 'date-fns';
import * as moment from 'moment';
import { DatePickerComponent } from 'ng2-date-picker';
import { CompetenzaDetailModalComponent } from '../skills-selector/modals/competenza-detail-modal/competenza-detail-modal.component';
import { Location } from '@angular/common';
import { GeoService } from '../../core/services/geo.service';
import * as Leaflet from 'leaflet';

@Component({
  selector: 'oppurtunity-edit',
  templateUrl: './oppurtunity-edit.component.html',
  styleUrls: ['./oppurtunity-edit.component.scss']
})
export class OppurtunityEditComponent implements OnInit {

  @Input() offer: IOffer;

  // to be read from profile.
  tipologiaAzienda = [];
  errorMessage: string;
  deleteMessageEnabled: boolean;
  operationText: string = 'Insert';
  competenze: Competenza[] = []; //list of all competenze
  savedCompetenze: Competenza[] = [] //saved competenze di offerta
  updatedCompetenze = [];
  oraInizio;
  oraFine;
  minuteStep: number = 5;
  map;
  selectedLocationMarker;
  indirizzo;
  addressesItems = [];
  showAddressesList;
  forceErrorDisplay: boolean = false;
  oldPD: number; // old poste disponibile

  @ViewChild('customerForm') customerForm: NgForm;
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

  datePickerConfig = {
    locale: 'it',
    firstDayOfWeek: 'mo'
  };
  timePickerConfig = {
    locale: 'it',
    format: 'H:mm',
    hours24Format: 'H',
    showTwentyFourHours: true
  };


  navTitle: string = "Crea offerta";
  breadcrumbItems = [
    {
      title: "Lista offerte",
      location: "../../"
    },
    {
      title: "Modifica offerta",
      location: "./"
    }
  ];

  constructor(
    public activeModal: NgbModal,
    private router: Router,
    private route: ActivatedRoute,
    private dataService: DataService,
    private growler: GrowlerService,
    private modalService: ModalService,
    private GeoService: GeoService,
    private location: Location) { }

  ngOnInit() {

    this.route.params.subscribe((params: Params) => {
      let id = +params['id'];


      this.dataService.getData("tipologiaAzienda").subscribe((data) => {

        this.tipologiaAzienda = data;

        if (id == 0) { //new
          this.operationText = 'Crea';
          let today = startOfDay(new Date());

          let tmpAzienda: Azienda = { "id": this.dataService.aziendaId };
          this.offer = { 
            id: null, 
            titolo: '', 
            descrizione: '',
            tipologia: undefined,
            dataInizio: 0,
            dataFine: 0,
            oraInizio: '',
            oraFine: '',
            ore: null,
            postiDisponibili: null,
            postiRimanenti: null,
            prerequisiti: '',
            referenteAzienda: { id: '0' },
            competenze: [],
            azienda: tmpAzienda,
            start: moment.unix(today.getTime() / 1000),
            end: moment.unix(today.getTime() / 1000),
            referente: '',
            referenteCF: '',
            coordinate:  this.dataService.getAziendaPosition(),
          };

          setTimeout(() => {
            this.drawMap();
          }, 0);
        } else {
          this.dataService.getOppurtunitaDetailAPI(id)
            .subscribe((offer: IOffer) => {

              this.offer = offer;

              this.navTitle = this.offer.titolo;
              this.operationText = 'Modifica';
              this.savedCompetenze = offer.competenze;
              this.offer.start = moment.unix(this.offer.dataInizio / 1000);
              this.offer.end = moment.unix(this.offer.dataFine / 1000);
              if (this.offer.oraInizio) {
                let oraInizoHoursMins = this.offer.oraInizio.split(':');
                this.oraInizio = {
                  hour: Number(oraInizoHoursMins[0]),
                  minute: Number(oraInizoHoursMins[1]),
                  second: 0
                };
              }
              if (this.offer.oraFine) {
                let oraFineHoursMins = this.offer.oraFine.split(':');
                this.oraFine = {
                  hour: Number(oraFineHoursMins[0]),
                  minute: Number(oraFineHoursMins[1]),
                  second: 0
                };
              }

               // old rimanenti logic.
               this.oldPD = offer.postiDisponibili;

              setTimeout(() => {
                this.drawMap();
              }, 0);

            });
        }

      })



    });


  }

  back() {
    this.location.back();
  }


  getOffer(id: number) {
    this.dataService.getOffer(id).subscribe((offer: IOffer) => {
      this.offer = offer;

      this.offer.start = moment.unix(offer.dataInizio / 1000);
      this.offer.end = moment.unix(offer.dataFine / 1000);

    });
  }

  formatDate(dateToChange: Date) {

    var d = (dateToChange.getDate() < 10 ? '0' : '') + dateToChange.getDate();
    var m = ((dateToChange.getMonth() + 1) < 10 ? '0' : '') + (dateToChange.getMonth() + 1);
    var y = dateToChange.getFullYear();
    var x = String(d + "-" + m + "-" + y);
    return x;
  }

  submit() {
    this.offer.dataInizio = this.offer.start.toDate().getTime();
    this.offer.dataFine = this.offer.end.toDate().getTime();

    // if (this.oraInizio) {
    //   let tmpOra = new Date(this.offer.dataInizio);
    //   tmpOra.setHours(this.oraInizio.hour, this.oraInizio.minute);      
    //   this.offer.dataInizio = tmpOra.getTime();
    // }
    // if (this.oraFine && this.oraFine.hour && this.oraFine.minute) {      
    //   let tmpOra = new Date(this.offer.dataFine);
    //   tmpOra.setHours(this.oraFine.hour, this.oraFine.minute);      
    //   this.offer.dataFine = tmpOra.getTime();
    // }   

    if (this.oraInizio) {
      this.offer.oraInizio = this.formatTwoDigit(this.oraInizio.hour) + ":" + this.formatTwoDigit(this.oraInizio.minute);
    }
    if (this.oraFine) {
      this.offer.oraFine = this.formatTwoDigit(this.oraFine.hour) + ":" + this.formatTwoDigit(this.oraFine.minute);
    }
    console.log(JSON.stringify(this.offer));

    //posti rimanenti logic.
    let save = true;
    let displayMsg = 'Compila i campi necessari';
    if (this.oldPD != null && this.offer.postiDisponibili !== this.oldPD) {
      if (this.offer.postiDisponibili > this.oldPD) {
        // PR = PR + (new -old)
        this.offer.postiRimanenti = this.offer.postiRimanenti + (this.offer.postiDisponibili - this.oldPD);
      } else if ((this.offer.postiDisponibili < this.oldPD) && (this.offer.postiDisponibili >= (this.oldPD - this.offer.postiRimanenti))) {
        //PR = new - (old - PR)
        this.offer.postiRimanenti = this.offer.postiDisponibili - (this.oldPD - this.offer.postiRimanenti);
      } else {
        save = false;
        displayMsg = 'Attenzione: numero posti disponibile minori di quella attualmente assignati';
      }
    }
    
    if (this.allValidated() && save) {
      this.offer.competenze = [];

      if (this.offer.coordinate && !this.offer.coordinate.latitude) { //if latitude field is not present means that position is edited
        this.offer.coordinate = {
          'latitude': this.offer.coordinate[0],
          'longitude': this.offer.coordinate[1]
        };
      }

      if (this.offer.id === null) {

        this.dataService.insertOppurtunitaAPI(this.offer)
          .subscribe((inserted: IOffer) => {
            if (inserted) {

              this.growler.growl('Successo.', GrowlerMessageType.Success);
              this.router.navigateByUrl('/oppurtunita/list/details/' + inserted.id);
            }
            else {
              this.growler.growl("Errore", GrowlerMessageType.Danger);

            }
          },
            (err: any) => console.log(err));
      } else {


        this.dataService.updateOppurtunita(this.offer)
          .subscribe(res => {

            this.growler.growl('Successo.', GrowlerMessageType.Success);
            this.router.navigateByUrl('/oppurtunita/list/details/' + this.offer.id);

          },
            (err: any) => console.log(err));
      }

    } else {
      this.forceErrorDisplay = true;
      this.growler.growl(displayMsg, GrowlerMessageType.Warning);
    }


  }

  formatTwoDigit(n) {
    return (n < 10 ? '0' : '') + n;
  }


  allValidated() {
    return this.offer.start && this.offer.end &&
      this.offer.start <= this.offer.end &&
      this.offer.titolo && this.offer.tipologia && this.offer.ore && this.offer.postiDisponibili && this.offer.referente;
  }

  cancel(event: Event) {
    event.preventDefault();
    if (this.offer.id) {
      this.router.navigateByUrl('/oppurtunita/list/details/' + this.offer.id);
    } else {
      this.router.navigateByUrl('/oppurtunita/list');
    }

  }


  openDetailCompetenza(competenza, $event) {
    if ($event) $event.stopPropagation();
    const modalRef = this.activeModal.open(CompetenzaDetailModalComponent, { size: "lg" });
    modalRef.componentInstance.competenza = competenza;
  }

  drawMap(): void {
    this.map = Leaflet.map('map',  {
      center: [46.1025748,10.927261],
      zoom: 8
    });
    Leaflet.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
      maxZoom: 18
    }).addTo(this.map);
    /*if (this.selectedLocationMarker) {
      this.selectedLocationMarker.addTo(map);
    }*/

    if (this.offer.coordinate) {
      this.selectedLocationMarker = Leaflet.marker([this.offer.coordinate.latitude, this.offer.coordinate.longitude]).addTo(this.map);
      this.map.setView(this.selectedLocationMarker.getLatLng(), 14);
    } else {
      this.map.locate({ setView: true });
    }

    function onLocationFound(e) {
      var radius = e.accuracy / 2;
      Leaflet.marker(e.latlng).addTo(this.map);
      Leaflet.circle(e.latlng, radius).addTo(this.map);
      this.map.setZoom(12);
    }

    this.map.addEventListener('click', (e) => {
      this.GeoService.getAddressFromCoordinates(e).then(location => {
        this.offer.coordinate = [e.latlng.lat, e.latlng.lng];
        this.indirizzo = location;

        if (!this.selectedLocationMarker) {
          this.selectedLocationMarker = Leaflet.marker(e.latlng).addTo(this.map);
        } else {
          this.selectedLocationMarker.setLatLng(e.latlng).update();
        }
        this.map.setView(this.selectedLocationMarker.getLatLng());
      });
    });
  }
  getItems(ev: any) {
    this.GeoService.getAddressFromString(ev.target.value).then(locations => {
      let val = ev.target.value;
      if (locations instanceof Array) {
        this.addressesItems = locations;
      }
      if (val && val.trim() != '') {
        this.addressesItems = this.addressesItems.filter((item) => {
          return (item.name.toLowerCase().indexOf(val.toLowerCase()) > -1);
        });
        this.showAddressesList = true;
      } else {
        this.showAddressesList = false;
      }
    });
  }

  selectPlace(item) {
    this.offer.coordinate = [item.location[0], item.location[1]];
    this.indirizzo = item.name;
    if (!this.selectedLocationMarker) {
      this.selectedLocationMarker = Leaflet.marker(this.offer.coordinate).addTo(this.map);
    } else {
      this.selectedLocationMarker.setLatLng(this.offer.coordinate).update();
    }
    this.map.setView(this.selectedLocationMarker.getLatLng());

    this.showAddressesList = false;
  }

}