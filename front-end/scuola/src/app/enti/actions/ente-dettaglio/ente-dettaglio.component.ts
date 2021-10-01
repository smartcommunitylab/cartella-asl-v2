import { Component, OnInit, ViewChild } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DataService } from '../../../core/services/data.service';
import { Router, ActivatedRoute } from '@angular/router';
import * as Leaflet from 'leaflet';
import { EnteCancellaModal } from '../cancella-ente-modal/ente-cancella-modal.component';
import { AbilitaEntePrimaModal } from '../abilita-ente-prima-modal/abilita-ente-prima-modal.component';
import { AbilitaEnteSecondaModal } from '../abilita-ente-secondo-modal/abilita-ente-seconda-modal.component';
import { AnnullaInvitoModal } from '../annulla-invito-modal/annulla-invito-modal.component';
import { GrowlerMessageType, GrowlerService } from '../../../core/growler/growler.service';

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
    private modalService: NgbModal,
    private growler: GrowlerService) { }

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
  menuContent = 'In questa pagina trovi tutte le informazioni su un singolo ente. Usa il tasto “modifica dati ente” per modificare i dati. Con il tasto “Attiva accesso” puoi invitare un ente a crearsi un account in EDIT per gestire presenze, offerte e documentazione';
  tipoAzienda = [{ "id": 1, "value": "Associazione" }, { "id": 5, "value": "Cooperativa" }, { "id": 10, "value": "Impresa" }, { "id": 15, "value": "Libero professionista" }, { "id": 20, "value": "Pubblica amministrazione" }, { "id": 25, "value": "Ente privato/Fondazione" }];
  showContent: boolean = false;
  convenzioni = [];

  breadcrumbItems = [
    {
      title: "Lista enti",
      location: "../../",
    },
    {
      title: "Dettaglio ente"
    }
  ];


  ngOnInit() {

    this.route.params.subscribe(params => {
      let id = params['id'];

      this.dataService.getAzienda(id).subscribe((res) => {
        this.ente = res;
        this.dataService.getEnteConvenzione(id).subscribe((res) => {
          this.convenzioni = res;
        },
        (err: any) => console.log(err),
        () => console.log('getEnteConvenzioni'));

        setTimeout(() => { //ensure that map div is rendered
          this.drawMap();
        }, 0);
      },
        (err: any) => console.log(err),
        () => console.log('getEnte'));
    });

  }

  openDetailCompetenza(competenza, $event) {
    // if ($event) $event.stopPropagation();
    // const modalRef = this.modalService.open(CompetenzaDetailModalComponent, { size: "lg" });
    // modalRef.componentInstance.competenza = competenza;
  }

  openDetail(ente) {
    this.router.navigate([ente.id], { relativeTo: this.route });
  }

  modifica() {
    this.router.navigate(['modifica/dati/'], { relativeTo: this.route });
  }

   delete() {
     const modalRef = this.modalService.open(EnteCancellaModal, { windowClass: "cancellaModalClass" });
     modalRef.componentInstance.ente = this.ente;
     modalRef.componentInstance.onDelete.subscribe((res) => {
       if (res == 'deleted') {
         this.dataService.deleteAzienda(this.ente.id).subscribe((res) => {
           this.router.navigate(['../../'], { relativeTo: this.route });
         })
       }
     });
  }

  menuContentShow() {
    if (this.ente.registrazioneEnte && this.ente.registrazioneEnte.stato == 'inviato') {
      this.menuContent = 'In questa pagina trovi tutte le informazioni su un singolo ente. Questo ente è già stato invitato ad attivare un profilo. Attendi una risposta o, se pensi che ci sia stato un errore, annulla l’invito.'
    } else if (this.ente.registrazioneEnte && this.ente.registrazioneEnte.stato == 'confermato') {
      this.menuContent = "In questa pagina trovi tutte le informazioni su un singolo ente. Questo ente ha un profilo attivo, quindi per modificare il suo profilo devi rivolgerti al responsabile dell’ente.";
    }
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
  
  setStatus(ente) {
    let stato = 'Disponibile all’attivazione';
    if (ente.registrazioneEnte && ente.registrazioneEnte.stato == 'inviato') {
      stato = 'In attivazione';
    } else if (ente.registrazioneEnte && ente.registrazioneEnte.stato == 'confermato') {
      stato = 'Con account';
    }
    return stato;
  }

  styleOption(ente) {
    var style = {
      'color': '#FFB54C', //orange
      'font-weight': 'bold'
    };

    if (ente.registrazioneEnte && ente.registrazioneEnte.stato == 'inviato') {
      style['color'] = '#7FB2E5'; // grey
    } else if (ente.registrazioneEnte && ente.registrazioneEnte.stato == 'confermato') {
      style['color'] = '#00CF86'; // green
    }

    return style;
  }

  abilitaEnte() {
    if (!this.ente.email) {
      this.growler.growl('<b>Errore: nessun indirizzo email associato all\'ente.</b><br/>Inserire l\'indirizzo e riprovare. Si consiglia di contattare direttamente l\'ente per ottenere un indirizzo aggiornato e attivo.', GrowlerMessageType.Danger);
    } else {
      const modalRef = this.modalService.open(AbilitaEntePrimaModal, { windowClass: "abilitaEnteModalClass" });
      modalRef.componentInstance.ente = this.ente;
      modalRef.componentInstance.onAbilita.subscribe((res) => {
        const modalRef = this.modalService.open(AbilitaEnteSecondaModal, { windowClass: "abilitaEnteModalClass" });
        modalRef.componentInstance.ente = this.ente;
        modalRef.componentInstance.onAbilita.subscribe((res) => {
          //api call.
          this.dataService.creaRichiestaRegistrazione(this.ente).subscribe((res) => {
            this.router.navigate(['../../'], { relativeTo: this.route });
          })
        });
      })
    }

  }

  annullaInvitoEnte() {
    const modalRef = this.modalService.open(AnnullaInvitoModal, { windowClass: "abilitaEnteModalClass" });
    modalRef.componentInstance.ente = this.ente;
    modalRef.componentInstance.onAnnulla.subscribe((res) => {
      this.dataService.annullaRichiestaRegistrazione(this.ente).subscribe((res) => {
        this.router.navigate(['../../'], { relativeTo: this.route });
      })
    });
  }
  
  isModificable(ente) {
    var isModificable = true;
    if (ente.registrazioneEnte && ente.registrazioneEnte.stato == 'inviato') {
      isModificable = false;
    } else if (ente.registrazioneEnte && ente.registrazioneEnte.stato == 'confermato') {
      isModificable = false;
    }
    return isModificable;
  }

  getTipologiaString(tipologiaId) {
    if (this.tipoAzienda) {
      let rtn = this.tipoAzienda.find(data => data.id == tipologiaId);
      if (rtn) return rtn.value;
      return tipologiaId;
    } else {
      return tipologiaId;
    }
  }

  aggiungiConvenzione() {
      this.router.navigate(['aggiungi/convenzione'], { relativeTo: this.route });
  }

  setStatoConvenzione(conv) {
    let label = 'Scaduta';
    if (conv.stato == 'attiva') {
      label = 'Valida';
    }
    return label;
  }

  styleOptionConvenzione(conv) {
    var style = {
        'color': '#707070', //grey
    };

    if (conv && conv.stato == 'attiva') {
        style['color'] = '#00CF86'; // green
    }
    return style;
  }

  modificaConvenzione(conv, e) {
    var target = e.target;
    while (target) { // Iterate through elements
      if (target.tagName === 'TD') { // TD found, stop iteration
        break;
      }
      target = target.parentElement; // Set target as a parent element of the current element
    }
    if (target === null) { return; } // Check that we really have a TD

    if (target.cellIndex == 3) {
      this.downloadDoc(conv);
    } else {
      this.router.navigate(['modifica/convenzione', conv.id], { relativeTo: this.route });
    }
  }

  downloadDoc(doc) {
    this.dataService.downloadDocumentConvenzioneBlob(doc).subscribe((url) => {
      const downloadLink = document.createElement("a");
      downloadLink.href = url;
      downloadLink.download = doc.nomeFile;
      document.body.appendChild(downloadLink);
      downloadLink.click();
      document.body.removeChild(downloadLink);
    });
  }

}
