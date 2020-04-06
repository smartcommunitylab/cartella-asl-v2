import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../core/services/data.service'
import { Competenza } from '../shared/classes/Competenza.class';
import { GrowlerService, GrowlerMessageType } from '../core/growler/growler.service';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { IPagedCompetenze } from '../shared/classes/IPagedCompetenze.class';
import { PaginationComponent } from '../shared/pagination/pagination.component';
import { CreaCompetenzaModalComponent } from './actions/create-competenza-modal/crea-competenza-modal.component'

@Component({
  selector: 'competenze',
  templateUrl: './competenze.component.html',
  styleUrls: ['./competenze.component.scss']
})

export class CompetenzeComponent implements OnInit {

  @ViewChild('cmPagination') private cmPagination: PaginationComponent;

  competenze: Competenza[] = [];
  filterText
  title: string;
  filtro;
  closeResult: string;
  totalRecords: number = 0;
  pageSize: number = 10;
  currentpage: number = 0;
  stato;
  owner;
  menuContent = "In questa pagina trovi tutte le competenze disponibili. Puoi crearne di nuove con il tasto verde “Crea competenza”. Per vedere il dettaglio di una competenza, clicca sulla riga corrispondente.";
  showContent: boolean = false;
  stati = [{ "name": "Disponibile", "value": 1 }, { "name": "Non disponibile", "value": 2 }];
  sources = [{ "name": "Istituto", "value": "istituto" }, { "name": "ISFOL", "value": "ISFOL" }];
  

  constructor(
    private dataService: DataService,
    private route: ActivatedRoute,
    private router: Router,
    private modalService: NgbModal,
    private growler: GrowlerService
  ) {
    this.filtro = {
      owner: [this.dataService.istitutoId, 'ISFOL'],
      stato: null,
      filterText: null

    }
  }
  ngOnInit(): void {
    this.title = 'Lista competenze';
    this.getCompetenzePage(1);

  }

  getCompetenzePage(page: number) {
    this.dataService.getPagedCompetenzeOrderByIstitutoId(this.filtro,  (page - 1), this.pageSize)
      .subscribe((response: IPagedCompetenze) => {
        this.competenze = response.content;
        this.totalRecords = response.totalElements
      },
        (err: any) => console.log(err), () => console.log('getCompetenze for filtersearch: '));

  }

  filterChanged() {
    if (this.cmPagination)
      this.cmPagination.changePage(1);
    this.getCompetenzePage(1);
  }

  openDetail(competenza) {
    this.router.navigate(['../list/detail', competenza.id], { relativeTo: this.route });
  }
  openCreate() {
    const modalRef = this.modalService.open(CreaCompetenzaModalComponent, { windowClass: "myCustomModalClass" });
        modalRef.componentInstance.newCompetenzeListener.subscribe((competenza) => {
          this.dataService.addIstitutoCompetenza(competenza).subscribe((saved) => { 
            this.router.navigate(['../list/detail', saved.id], { relativeTo: this.route });
          });
        });
  }

  pageChanged(page: number) {
    this.getCompetenzePage(page);
  }

  selectStatoFilter() {
    if (this.cmPagination)
      this.cmPagination.changePage(1);
    if (this.stato=='1') {
      this.filtro.stato = true;
    } else if (this.stato == '2') {
      this.filtro.stato = false;
    }
    else {
      this.filtro.stato = null;
    }
    this.getCompetenzePage(1);
  }

  cerca() {
    if (this.cmPagination)
      this.cmPagination.changePage(1);
    if (this.filterText) {
      this.filtro.filterText = this.filterText;
    } else {
      this.filtro.filterText = null;
    }
    this.getCompetenzePage(1);
  }

  menuContentShow() {
    this.showContent = !this.showContent;
  }

  selectOwnerFilter() {
    if (this.cmPagination)
      this.cmPagination.changePage(1);
    if (this.owner) {
      if (this.owner == 'istituto') {
        this.filtro.owner = [this.dataService.istitutoId];
      } else {
        this.filtro.owner = ['ISFOL'];
      }
    } else {
      this.filtro.owner = [this.dataService.istitutoId, 'ISFOL'];
    }
    this.getCompetenzePage(1);
  }
  
  refreshCompetenza() {
    this.stato = undefined;
    this.filtro.stato = null;
    this.filterText=null;
    this.filtro.filterText = null;
    this.owner=undefined;
    this.filtro.owner = [this.dataService.istitutoId, 'ISFOL'];
    this.getCompetenzePage(1);
    // this.router.navigate(['/competenza/list'], { relativeTo: this.route });
  }
}
