import { Component, OnInit, ViewChild } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DataService } from '../../../core/services/data.service';
import { Router, ActivatedRoute } from '@angular/router';
import * as Leaflet from 'leaflet';
import { EnteCancellaModal } from '../cancella-ente-modal/ente-cancella-modal.component';
import { AnnullaInvitoModal } from '../annulla-invito-modal/annulla-invito-modal.component';

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
  menuContent = "In questa pagina trovi tutte le informazioni su un singolo ente. Usa il tasto “modifica dati ente” per modificare i dati.";
  showContent: boolean = false;

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

        setTimeout(() => { //ensure that map div is rendered
          this.drawMap();
        }, 0);
        // });

      },
        (err: any) => console.log(err),
        () => console.log('getAttivita'));
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
    if (ente.origin == 'CONSOLE') {
      if (ente.registrazioneEnte && ente.registrazioneEnte.stato == 'inviato') {
        return 'In attivazione';
      } else if (ente.registrazioneEnte && ente.registrazioneEnte.stato == 'confermato') {
        return 'Con account';
      } else {
        return 'Disponibile all’attivazione';
      }
    } else {
      return 'Con account';
    }
  }

  styleOption(ente) {
    var style = {
      'color': '#00CF86', //green
      'font-weight': 'bold'
    };
    if (ente.origin == 'CONSOLE') {
      if (ente.registrazioneEnte && ente.registrazioneEnte.stato == 'inviato') {
        style['color'] = '#7FB2E5';
      } else if (ente.registrazioneEnte && ente.registrazioneEnte.stato == 'confermato') {
        style['color'] = '#00CF86';
      } else {
        style['color'] = '#FFB54C';
      }
    }
    return style;
  }

  abilitaEnte() {
    //api call.
    this.dataService.creaRichiestaRegistrazione(this.ente).subscribe((res) => {
      this.router.navigate(['../../'], { relativeTo: this.route });
    })
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

}
