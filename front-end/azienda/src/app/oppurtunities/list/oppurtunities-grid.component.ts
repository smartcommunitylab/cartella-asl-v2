import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { NgbModal, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DataService } from '../../core/services/data.service';
import { ICustomer, IPagedResults, IState, IOffer, IPagedOffers, TipologiaAzienda } from '../../shared/interfaces';
import { FilterService } from '../../core/services/filter.service';
import { forEach } from '@angular/router/src/utils/collection';
import { OppurtunityEditComponent } from '../edit/oppurtunity-edit.component'
import { startOfDay, addDays, endOfISOYear, startOfMonth, endOfMonth } from 'date-fns';
import { PaginationComponent } from '../../shared/pagination/pagination.component';
import { GrowlerService, GrowlerMessageType } from '../../core/growler/growler.service';
import { AttivitaCancellaModal } from '../modal/cancella/attivita-cancella-modal.component';

@Component({
  selector: 'oppurtunity-list',
  templateUrl: './oppurtunities-grid.component.html',
  styleUrls: ['./oppurtunities-grid.component.scss']
})
export class OppurtunitiesGridComponent implements OnInit {

  @ViewChild('cmPagination') cmPagination: PaginationComponent;
  title: string;
  filterText: string;
  offers: IOffer[] = [];
  filteredOffers: IOffer[] = [];
  displayMode: DisplayModeEnum;
  displayModeEnum = DisplayModeEnum;
  totalRecords: number = 0;
  pageSize: number = 10;
  start: Date;
  end: Date;
  tipologiaAzienda: TipologiaAzienda[];
  order: string = "primo";

  constructor(private modalService: NgbModal, private router: Router, private dataService: DataService, private filterService: FilterService, private growler: GrowlerService) { }

  ngOnInit() {
    this.title = 'Offerte';
    let today: Date = new Date();
    this.start = startOfMonth(today);
    // this.end = endOfMonth(today);

    this.dataService.getData("tipologiaAzienda").subscribe((data) => {

      this.tipologiaAzienda = data;

      this.getOffersPage(1);
    });


  }


  getTitolo(tipoId: any) {
    for (let tipo of this.tipologiaAzienda) {
      if (tipo.id == tipoId) {
        return tipo.titolo;
      }
    }
  }

  changeDisplayMode(mode: DisplayModeEnum) {
    this.displayMode = mode;
  }

  pageChanged(page: number) {
    this.getOffersPage(page);
  }

  getOffersPage(page: number) {

    // this.dataService.getOffersPage((page - 1) * this.pageSize, this.pageSize)
    this.dataService.getPagedOppurtunitaAPI(this.dataService.aziendaId,
      '',//this.start.getTime(),
      null,
      null,
      this.filterText,
      (page - 1),
      this.pageSize)
      .subscribe((response: IPagedOffers) => {
        this.offers = this.filteredOffers = response.content;
        this.totalRecords = response.totalElements
      },
        (err: any) => console.log(err), () => console.log('getOffersPage() retrieved offers for page: ' + page));

  }


  filterChanged() {
    // this.cmPagination.pages = [];
    this.cmPagination.changePage(1);
    this.getOffersPage(1);
  }

  openDelete(offer) {
    const modalRef = this.modalService.open(AttivitaCancellaModal);
    modalRef.componentInstance.offer = offer;
    modalRef.result.then((result) => {
      if (result == 'deleted') {
        this.cmPagination.pages = [];
        this.cmPagination.changePage(1);
        this.getOffersPage(1);
      } else {
        this.router.navigateByUrl('/oppurtunita/list');
      }
    });
  }

  onSelectChange(ev) {
    //alert(this.order);
  }

}

enum DisplayModeEnum {
  Card = 0,
  Grid = 1,
  Map = 2
}
