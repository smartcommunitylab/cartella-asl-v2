import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { NgbModal, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DataService } from '../../core/services/data.service';
import { OppurtunityEditComponent } from '../edit/oppurtunity-edit.component'
import { ICustomer, IOffer, Competenza, TipologiaAzienda } from '../../shared/interfaces';
import { CompetenzaDetailModalComponent } from '../skills-selector/modals/competenza-detail-modal/competenza-detail-modal.component';
import { GrowlerService, GrowlerMessageType } from '../../core/growler/growler.service';
import { ModalService, IModalContent } from '../../core/modal/modal.service'
import { Location } from '@angular/common';
import { AttivitaCancellaModal } from '../modal/cancella/attivita-cancella-modal.component';
import * as Leaflet from 'leaflet';

@Component({
  selector: 'oppurtunity-details',
  templateUrl: './oppurtunity-details.component.html',
  styleUrls: ['./oppurtunity-details.component.scss']
})
export class OppurtunityDetailsComponent implements OnInit {

  offer: IOffer;

  competenze: Competenza[];

  tipologiaAzienda: TipologiaAzienda[];

  modalPopup: IModalContent = {
    header: 'Avviso',
    body: 'Sei sicuro di voler eliminare?',
    cancelButtonText: 'Annulla',
    OKButtonText: 'Cancella'
  };

  navTitle: string = "Dettaglio Offerta";

  // try to use location.back here;
  breadcrumbItems = [
      {
          title: "List Offerte",
          location: "../../"
      },
      {
          title: "Dettaglio offerta",
          location: "./"
      }
  ];

  map;

  

  constructor(
    private location: Location,
    private modalPopupService: ModalService,
    private router: Router,
    private route: ActivatedRoute,
    private modalService: NgbModal,
    private dataService: DataService,
    private growler: GrowlerService) {
  }

  back() {
    this.location.back();
  }
  
  ngOnInit() {
     
    this.route.params.subscribe((params: Params) => {
      let id = +params['id'];

      this.dataService.getData("tipologiaAzienda").subscribe((data) => {

        this.tipologiaAzienda = data;

        this.dataService.getOppurtunitaDetailAPI(id)
          .subscribe((offer: IOffer) => {
            this.offer = offer;
            this.competenze = offer.competenze;

            this.navTitle = this.offer.titolo;

            setTimeout(() => { //ensure that map div is rendered
              this.drawMap();
            }, 0);
          });

      });

    });
  }

  openModify(attivita) {
    const modalRef = this.modalService.open(OppurtunityEditComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.offer = attivita;

  }

  openDetailCompetenza(competenza, $event) {
    if ($event) $event.stopPropagation();
    const modalRef = this.modalService.open(CompetenzaDetailModalComponent, { size: "lg" });
    modalRef.componentInstance.competenza = competenza;
  }

  addCompetenze() {
    // this.router.navigateByUrl('/skills/' + this.offer.id); // fully working example from parent.
    this.router.navigate(['skills'],  {relativeTo: this.route});// full working example from relative.
  }

  getTitolo(tipoId: any) {
    for (let tipo of this.tipologiaAzienda) {
      if (tipo.id == tipoId) {
        return tipo.titolo;
      }
    }
  }

  deleteCompetenza(deleted, $event) {

     
    if ($event) $event.stopPropagation();

    this.modalPopupService.show(this.modalPopup).then(ok => {

      if (ok) {

        let updatedCompetenze = [];
        for (let competenza of this.offer.competenze) {
          if (competenza.id != deleted.id) {
            updatedCompetenze.push(Number(competenza.id));
          }
        }

        this.dataService.updateCompetenze(this.offer.id, updatedCompetenze)
          .subscribe((updatedComptenze: boolean) => {
            if (updatedComptenze) {
              this.growler.growl('Successo.', GrowlerMessageType.Success);
              this.dataService.getOppurtunitaDetailAPI(this.offer.id)
              .subscribe((offer: IOffer) => {
                this.offer = offer;
                this.competenze = offer.competenze;
              });
             
            } else {
              this.growler.growl("Errore", GrowlerMessageType.Danger);
              this.dataService.getOppurtunitaDetailAPI(this.offer.id)
              .subscribe((offer: IOffer) => {
                this.offer = offer;
                this.competenze = offer.competenze;
              });
            }
          })
      }

    });

  }

  confirm() {

    this.modalPopupService.show(this.modalPopup).then(ok => {
      if (ok) {
        this.dataService.deleteOppurtunita(this.offer.id).subscribe(updated => {
            this.growler.growl('Successo', GrowlerMessageType.Success);
            this.router.navigateByUrl('/oppurtunita/list');
        });
      }
    })
  }

  openDelete(offer) {
    const modalRef = this.modalService.open(AttivitaCancellaModal);
    modalRef.componentInstance.offer = offer;
    modalRef.result.then((result) => {
      if (result == 'deleted') {
        this.router.navigateByUrl('/oppurtunita/list');
      } else {
        // this.router.navigateByUrl('/oppurtunita/list');
      }
    });
  }


  drawMap(): void {
    this.map = Leaflet.map('map');
    Leaflet.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
      maxZoom: 18
    }).addTo(this.map);

    if (this.offer.coordinate) {
      let selectedLocationMarker = Leaflet.marker([this.offer.coordinate.latitude, this.offer.coordinate.longitude]).addTo(this.map);
      this.map.setView(selectedLocationMarker.getLatLng(), 14);
    }
    
  }
}