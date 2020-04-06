import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { NgbModal, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { IPagedResults, IOffer, TipologiaAzienda, Stato, IPagedOffers } from '../../shared/interfaces';
import { DataService } from '../../core/services/data.service';
import { TrackByService } from '../../core/services/trackby.service';
import { startOfDay, startOfISOYear, addISOYears, startOfMonth } from 'date-fns';
import * as moment from 'moment';
import { Moment } from 'moment';
import { PaginationComponent } from '../../shared/pagination/pagination.component';
import { AttivitaCancellaModal } from '../modal/cancella/attivita-cancella-modal.component';

@Component({
  selector: 'offer-history',
  templateUrl: './offer-history.component.html',
  styleUrls: ['./offer-history.component.scss']
})
export class HistoryComponent implements OnInit {

  @ViewChild('cmPagination') cmPagination: PaginationComponent;

  offers: IOffer[] = [];

  filteredOffers: IOffer[] = [];
  totalRecords: number = 0;
  pageSize: number = 10;


  tipologiaAzienda: TipologiaAzienda[];

  status: Stato[];

  title: string;
  filterStart;
  filterEnd;
  closeResult: string;
  filterTipology: number = 0;
  allTipology: any = [];
  filterText: string;


  filterDatePickerConfig = {
    locale: 'it',
    firstDayOfWeek: 'mo'
  };



  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private dataService: DataService,
    private modalService: NgbModal) { }

  ngOnInit() {
    let today: Date = new Date();

    let start: Date = addISOYears(today, -1);
    let end: Date = startOfMonth(today);


    this.filterStart = moment.unix(start.getTime() / 1000);
    this.filterEnd = moment.unix(end.getTime() / 1000);

    this.dataService.getData("tipologiaAzienda").subscribe((data) => {
      this.tipologiaAzienda = data;

      // default selectAll.
      for (let tipo of this.tipologiaAzienda) {
        this.allTipology.push(tipo.id);
      }

      // hack for adding additional option for selectALL.
      let allOption: TipologiaAzienda = { id: 0, titolo: "TUTTI", selected: true };
      this.tipologiaAzienda.push(allOption);

      this.getOffersPage(1);
    });

  }

  pageChanged(page: number) {
    this.getOffersPage(page);
  }

  getTipo(id: any) {
    for (let tipo of this.tipologiaAzienda) {
      if (tipo.id == id) {
        return tipo.titolo;
      }
    }

  }

  cerca() {

    // this.cmPagination.pages = [];
    this.cmPagination.changePage(1);
    this.getOffersPage(1);


  }

  getOffersPage(page: number) {

    console.log("offer-history.component " + this.filterStart.toDate() + " - " + this.filterEnd.toDate());

    this.dataService.getPagedOppurtunitaAPI(
      this.dataService.aziendaId,
      this.filterStart.toDate().getTime(),
      this.filterEnd.toDate().getTime(),
      (this.filterTipology == null || this.filterTipology == 0) ? this.allTipology : [this.filterTipology],
      (this.filterText == '') ? null : this.filterText,
      (page == null) ? 0 : page - 1,
      this.pageSize)

      .subscribe((response: IPagedOffers) => {
        this.totalRecords = response.totalElements;
        this.offers = this.filteredOffers = response.content;
      });

  }

  setSelectedTipo(selectElement) {

    for (var i = 0; i < selectElement.options.length; i++) {
      var optionElement = selectElement.options[i];
      var optionModel = this.tipologiaAzienda[i];

      if (optionElement.selected == true) {
        optionModel.selected = true;
        this.filterTipology = optionModel.id;
      } else {
        optionModel.selected = false;
      }
    }
  }

  openDelete(offer) {
    const modalRef = this.modalService.open(AttivitaCancellaModal);
    modalRef.componentInstance.offer = offer;
    modalRef.result.then((result) => {
      if (result == 'deleted') {
        this.cmPagination.pages = [];
        this.cmPagination.changePage(1);
        this.getOffersPage(1);
      }
    });
  }

}