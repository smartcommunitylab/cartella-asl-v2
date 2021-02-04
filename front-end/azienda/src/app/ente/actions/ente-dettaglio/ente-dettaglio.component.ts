import { Component, OnInit, ViewChild } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DataService } from '../../../core/services/data.service';
import { Router, ActivatedRoute } from '@angular/router';
import * as Leaflet from 'leaflet';

@Component({
  selector: 'cm-ente-dettaglio',
  templateUrl: './ente-dettaglio.component.html',
  styleUrls: ['./ente-dettaglio.component.scss']
})
export class EnteDettaglioComponent implements OnInit {

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private dataService: DataService,
    private modalService: NgbModal) { }

  ente;
  map;
  esperienze;
  navTitle: string = "Dettaglio ente";
  cardTitle: string = "Vedi";
  individuale: boolean;
  agreegatedTipo = [];
  agreegatedTotal = {};
  corsiStudio;
  tipologie;
  yearsHours;
  yearsHoursTotal;
  tabDefaultSelectedId;
  noActivitySetted: boolean = true;
  order: string = 'titolo';
  documenti;
  painoTipologieTerza: any = [];
  painoTipologieQuarto: any = [];
  painoTipologieQuinto: any = [];
  totale = {};
  pianoTipologie = {};
  atttivitaCompetenze = [];
  tipoInterna: boolean = false;
  codiceAteco: string = '';
  descAteco: string = '';
  ruoli;
  menuContent = "In questa pagina trovi tutte le informazioni. Usa il tasto “modifica dati ente” per modificare i dati. Con il tasto “Aggiungi account” puoi dare l’accesso ad altre persone per la gestione quotidiana delle attività di alternanza.";
  showContent: boolean = false;
  attachedAteco = [];

  ngOnInit() {
    this.dataService.getAzienda().subscribe((res) => {
      this.ente = res;
      this.updateAtecoCodiceList();
      setTimeout(() => { //ensure that map div is rendered
        this.drawMap();
      }, 0);
    },
      (err: any) => console.log(err), 
      () => console.log('getAttivita'));
  }

  modifica() {
    this.router.navigate(['modifica/dati/'], { relativeTo: this.route });
  }

  menuContentShow() {
    this.showContent = !this.showContent;
  }

  drawMap(): void {
    this.map = Leaflet.map('map');
    Leaflet.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
      maxZoom: 18
    }).addTo(this.map);

    if (this.ente.coordinate && this.ente.coordinate.latitude && this.ente.coordinate.longitude) {
      let selectedLocationMarker = Leaflet.marker([this.ente.coordinate.latitude, this.ente.coordinate.longitude]).addTo(this.map);
      this.map.setView(selectedLocationMarker.getLatLng(), 14);
    }
  }

  hasCoordinate(): boolean {
    return (this.ente.latitude && this.ente.longitude);
  }
  
  aggiungiAccount() {
  }

  modificaAbilitati() {
    this.router.navigate(['modifica/abilitati/'], { relativeTo: this.route });
  }

  updateAtecoCodiceList() {
    this.attachedAteco = [];
    if (this.ente.atecoCode && this.ente.atecoDesc) {
      for (var i = 0; i < this.ente.atecoCode.length; i++) {
        let atecoEntry = {codice: this.ente.atecoCode[i], descrizione: this.ente.atecoDesc[i]};
        this.attachedAteco.push(atecoEntry);
      }
    }
  }

  

}
