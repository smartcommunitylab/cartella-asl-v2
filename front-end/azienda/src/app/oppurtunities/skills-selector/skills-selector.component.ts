import { Component, Input, Output, OnInit, ViewChild } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal/modal';
import { Location } from '@angular/common';
import { DataService } from '../../core/services/data.service'
import { Competenza, IPagedCompetenze, ConoscenzaCompetenza, ProfiloCompetenza, IOffer } from '../../shared/interfaces';
import { CompetenzaDetailModalComponent } from './modals/competenza-detail-modal/competenza-detail-modal.component';
import { GrowlerService, GrowlerMessageType } from '../../core/growler/growler.service';
import { AttivitaAlternanza } from '../../shared/interfaces';
import { Router, ActivatedRoute } from '@angular/router';
import { PaginationComponent } from '../../shared/pagination/pagination.component';

@Component({
  selector: 'cm-skills-selector',
  templateUrl: './skills-selector.component.html',
  styleUrls: ['./skills-selector.component.css']
})
export class SkillsSelectorComponent implements OnInit {

  //@Input() profileType; //filter profiles

  @ViewChild('cmPagination') cmPagination: PaginationComponent;

  totalRecords: number = 0;
  pageSize: number = 10;

  id: any;

  attivita: IOffer;

  competenzeProfili: ProfiloCompetenza[];
  competenze: Competenza[];

  selectedNewCompetenze: Competenza[];
  attachedCompetenze: Competenza[]; //competenze already added to the activity

  searchCompetenzaTxt: string;


  navTitle: string = "Modifica competenze";
  breadcrumbItems = [
    {
      title: "Dettaglio offerta",
      location: "../"
    },
    {
      title: "Modifica Competenze",
      location: "./"
    }
  ];



  constructor(private location: Location,
    private dataService: DataService,
    private modalService: NgbModal,
    private router: Router,
    private activeRoute: ActivatedRoute,
    private growler: GrowlerService) {
    this.selectedNewCompetenze = [];
  }

  back() {
    this.location.back();
  }
  
  ngOnInit() {

    this.activeRoute.params.subscribe(params => {
      this.id = params['id'];

      this.dataService.getOppurtunitaDetailAPI(this.id).subscribe((offer: IOffer) => {
        this.attivita = offer;
        this.attachedCompetenze = offer.competenze;
        // GET all competenze for azienda.
        this.getCompetenze(1);

      });

    });

  }

  pageChanged(page: number) {
    this.getCompetenze(page);
  }

  getCompetenze(page) {
    this.dataService.getCompetenzeAPI(this.searchCompetenzaTxt, (page - 1), this.pageSize).subscribe((response: IPagedCompetenze) => {
      // this.attachedCompetenze = response.content;
      this.competenze = response.content;
      this.totalRecords = response.totalElements;
      this.mergeCompetenze();
    });
  }

  mergeCompetenze() {
    if (this.competenze && this.attachedCompetenze) {
      this.competenze.forEach(element => {
        if (this.attachedCompetenze.find(function (el) { return el.id == element.id; })) {
          element['disabled'] = true;
          element['selected'] = true;
        }
      });
    }
  }

  searchCompetenza(txt) {
    this.cmPagination.changePage(1);
    //this.cmPagination.pages = [];
    this.getCompetenze(0);
  }

  toggleCompetenza(competenza) {
    if (competenza.disabled) return
    if (competenza.hasOwnProperty('selected')) {
      competenza.selected = !competenza.selected;
    } else {
      competenza.selected = true;
    }
    if (competenza.selected) {
      this.selectedNewCompetenze.push(competenza);
    } else {
      this.selectedNewCompetenze.splice(this.selectedNewCompetenze.indexOf(competenza), 1);
    }
  }

  openDetailCompetenza(competenza, $event) {
    if ($event) $event.stopPropagation();
    const modalRef = this.modalService.open(CompetenzaDetailModalComponent, { size: "lg" });
    modalRef.componentInstance.competenza = competenza;
  }

  getProfiloName(idProfilo) {
    return this.competenzeProfili ? this.competenzeProfili[idProfilo].titolo : "";
  }

  addCompetenze() {

    let updatedCompetenze = [];

    for (let competenza of this.selectedNewCompetenze) {
      updatedCompetenze.push(Number(competenza.id));
    }

    for (let competenza of this.attachedCompetenze) {
      updatedCompetenze.push(Number(competenza.id));
    }
    

    // Update competenze. PUT /opportunita/{id}/competenze
    this.dataService.updateCompetenze(this.id, updatedCompetenze)
      .subscribe((status: boolean) => {
        if (status) {
          this.growler.growl('Successo.', GrowlerMessageType.Success);
          this.router.navigateByUrl('/oppurtunita/list/details/' + this.id);
        } else {
          this.growler.growl("Errore", GrowlerMessageType.Danger);
          this.router.navigateByUrl('/oppurtunita/list/details' + this.id);
        }
      })
  }

}
