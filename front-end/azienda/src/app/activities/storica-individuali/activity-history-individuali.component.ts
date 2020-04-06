import { Component, OnInit, ViewChild } from '@angular/core';

import { ActivatedRoute, Params } from '@angular/router';

import { IPagedResults, EsperienzaSvolta, TipologiaAzienda, Stato, IPagedES } from '../../shared/interfaces';
import { DataService } from '../../core/services/data.service';
import { TrackByService } from '../../core/services/trackby.service';
import { startOfDay, startOfISOYear, addISOYears, startOfMonth } from 'date-fns';

import * as moment from 'moment';
import { Moment } from 'moment';

import { PaginationComponent } from '../../shared/pagination/pagination.component';

@Component({
  selector: 'activity-history-individuali',
  templateUrl: './activity-history-individuali.component.html',
  styleUrls: ['./activity-history-individuali.component.scss']
})
export class HistoryIndividualiComponent implements OnInit {

  @ViewChild('cmPagination') cmPagination: PaginationComponent;

  esperienzaSvolta: EsperienzaSvolta[] = [];
  totalRecords: number = 0;
  pageSize: number = 10;

  tipologiaAzienda: TipologiaAzienda[];

  status: Stato[];// = [{ id: 1, titolo: "Incorso", selected: false }, { id: 0, titolo: "Todo", selected: false }, { id: -1, titolo: "Fermato", selected: false }, { id: 2, titolo: "Completato", selected: false }]

  title: string;
  filterStart;
  filterEnd;
  closeResult: string;
  filterTipology: number = 0;
  filterStato: number = 5;
  allTipology: any = [];
  allStatus: any = [];


  filterDatePickerConfig = {
    locale: 'it',
    firstDayOfWeek: 'mo'
  };



  constructor(private route: ActivatedRoute, private dataService: DataService) {

  }


  ngOnInit() {
     
    console.log('HistoryIndividualiComponent');

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

      
      this.dataService.getData("statoAttivita").subscribe((sData) => {
        this.status = sData;
        // default selectAll.
        for (let state of this.status) {
          this.allStatus.push(state.id);
        }
        let allStates: Stato = { id: 5, titolo: "TUTTI", selected: true };
        this.status.push(allStates);

        this.getEsperienzaSvolta(1);
      });
    });
  }

  getTipo(id: any) {
    for (let tipo of this.tipologiaAzienda) {
      if (tipo.id == id) {
        return tipo.titolo;
      }
    }

  }

  getStato(id: any) {
    for (let stato of this.status) {
      if (stato.id == id) {
        return stato.titolo;
      }
    }

  }


  cerca() {
    // this.cmPagination.pages = [];
    this.cmPagination.changePage(1);
    this.getEsperienzaSvolta(1);   
  }

  pageChanged(page: number) {
    this.getEsperienzaSvolta(page);
  }

  setSelectedStato(selectElement) {
    for (var i = 0; i < selectElement.options.length; i++) {
      var optionElement = selectElement.options[i];
      var optionModel = this.status[i];

      if (optionElement.selected == true) {
        optionModel.selected = true;
        this.filterStato = optionModel.id
      } else {
        optionModel.selected = false;
      }
    }
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

  getEsperienzaSvolta(page: number) {
    console.log("activity-history.component " + this.filterStart.toDate() + " - " + this.filterEnd.toDate());
    this.dataService.getEsperienzaSvoltaAPI(this.dataService.aziendaId, null,
      this.filterStart.toDate().getTime(),
      this.filterEnd.toDate().getTime(),
      (this.filterStato == null || this.filterStato == 5) ? this.allStatus : [this.filterStato],
      (this.filterTipology == null || this.filterTipology == 0) ? this.allTipology : [this.filterTipology],
      null,
      (page == null) ? 0 : page - 1,
      this.pageSize)

      .subscribe((response: IPagedES) => {
        this.totalRecords = response.totalElements;
        this.esperienzaSvolta = response.content;
      });
  }


}