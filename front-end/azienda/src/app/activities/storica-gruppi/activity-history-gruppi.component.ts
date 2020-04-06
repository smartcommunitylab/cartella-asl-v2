import { Component, OnInit, ViewChild } from '@angular/core';

import { ActivatedRoute, Params } from '@angular/router';

import { IPagedResults, EsperienzaSvolta, TipologiaAzienda, Stato, IPagedAA, AttivitaAlternanza } from '../../shared/interfaces';
import { DataService } from '../../core/services/data.service';
import { TrackByService } from '../../core/services/trackby.service';
import { startOfDay, startOfISOYear, addISOYears, startOfMonth } from 'date-fns';

import * as moment from 'moment';
import { Moment } from 'moment';

import { PaginationComponent } from '../../shared/pagination/pagination.component';

@Component({
  selector: 'activity-history-gruppi',
  templateUrl: './activity-history-gruppi.component.html',
  styleUrls: ['./activity-history-gruppi.component.scss']
})
export class HistoryGruppiComponent implements OnInit {

  @ViewChild('cmPagination') cmPagination: PaginationComponent;

  attivitaAlternanzaList: AttivitaAlternanza[] = [];
  totalRecords: number = 0;
  pageSize: number = 10;

  tipologiaAzienda: TipologiaAzienda[];

  title: string;
  filterStart;
  filterEnd;
  closeResult: string;
  filterTipology: number = 0;
  allTipology: any = [];

  filterDatePickerConfig = {
    locale: 'it',
    firstDayOfWeek: 'mo'
  };



  constructor(private route: ActivatedRoute, private dataService: DataService) {

  }


  ngOnInit() {

    console.log('HistoryGruppiComponent');

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

      this.getAttivitaAlternanza(1);

    });
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
    this.getAttivitaAlternanza(1);
  }

  pageChanged(page: number) {
    this.getAttivitaAlternanza(page);
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

  getAttivitaAlternanza(page: number) {
    console.log("activity-history.component " + this.filterStart.toDate() + " - " + this.filterEnd.toDate());
    // this.dataService.getEsperienzaSvoltaAPI
    this.dataService.getAttivitaAlternanzaForAziendaAPI(this.dataService.aziendaId, null,
      this.filterStart.toDate().getTime(),
      this.filterEnd.toDate().getTime(),
      (this.filterTipology == null || this.filterTipology == 0) ? this.allTipology : [this.filterTipology],
      null,
      (page == null) ? 0 : page - 1,
      this.pageSize)
      .subscribe((response: IPagedAA) => {
        this.totalRecords = response.totalElements;
        this.attivitaAlternanzaList = response.content;
      });
  }


}