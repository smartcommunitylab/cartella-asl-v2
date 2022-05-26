import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DataService } from '../../../core/services/data.service';
import { Router, ActivatedRoute } from '@angular/router';
import * as Leaflet from 'leaflet';
import { CreaNuovaUtenteModalComponent } from '../crea-nuova-utente-modal/crea-nuova-utente-modal.component';
import { RuoloCancellaModal } from '../ruolo-cancella-modal/ruolo-cancella-modal.component';
import { GrowlerService, GrowlerMessageType } from '../../../core/growler/growler.service';

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
    private growler: GrowlerService,
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
  menuContent = "In questa pagina trovi tutte le informazioni relative al profilo EDIT di questo ente. Usa il tasto “modifica dati ente” per modificare i dati. Con il tasto “Aggiungi account” puoi dare l’accesso ad altre persone per la gestione quotidiana delle attività di alternanza";
  showContent: boolean = false;
  attachedAteco = [];

  ngOnInit() {
    this.dataService.getAzienda().subscribe((res) => {
      this.ente = res;
      this.updateAtecoCodiceList();
      this.dataService.getRuoliByEnte().subscribe((roles) => {
        this.initializeRole(roles);
      });
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
    const modalRef = this.modalService.open(CreaNuovaUtenteModalComponent, { windowClass: "creaAttivitaModalClass" });
    modalRef.componentInstance.newUtenteListener.subscribe((role) => {
      this.dataService.aggiungiRuoloReferenteAzienda(role).subscribe(res => {
        this.dataService.getRuoliByEnte().subscribe((roles) => {
          let message = "Invio inviato con successo. Il delegato riceverà una mail con le istruzioni per attivare il proprio profilo EDIT ed iniziare a gestire i tirocini sull’interfaccia di " + this.ente.nome ;
          this.growler.growl(message, GrowlerMessageType.Success);
          this.initializeRole(roles);
        });
      });
    });
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

  deleteRuolo(ruolo) {
    const modalRef = this.modalService.open(RuoloCancellaModal);
    modalRef.componentInstance.onDelete.subscribe((res) => {
      this.dataService.cancellaRuoloReferenteAzienda(ruolo.id).subscribe(res => {
        this.dataService.getRuoliByEnte().subscribe((roles) => {
          this.initializeRole(roles);
        },
          (err: any) => console.log(err),
          () => console.log('getRuoliByEnte'));
      });
    });
  }

  checkDeleteRole(ruolo) {
    if (ruolo.userId == this.dataService.ownerId) {
      return false;
    }
    if (ruolo.role == "LEGALE_RAPPRESENTANTE_AZIENDA") {
      return false;
    }
    return true;
  }

  initializeRole(roles) {
    this.ruoli = roles.filter(role => {
      if (role.userId == this.dataService.ownerId) {
        return true;
      }
      return true;
    });
  }

  setRolelabel(role) {
    let label = "Referente Azienda";
    if (role == "LEGALE_RAPPRESENTANTE_AZIENDA") {
      label = "Responsabile ASL Azienda";
    }
    return label;
  }

}
