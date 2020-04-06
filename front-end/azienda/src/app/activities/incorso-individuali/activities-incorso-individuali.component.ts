import { Component, OnInit, Input } from '@angular/core';
import { ActivatedRoute, Params, } from '@angular/router';

import { IPagedResults, EsperienzaSvolta, IPagedES, TipologiaAzienda, Stato } from '../../shared/interfaces';
import { DataService } from '../../core/services/data.service';
import { startOfDay, addDays, endOfISOYear, startOfMonth, endOfMonth } from 'date-fns';
import { SorterService } from '../../core/services/sorter.service';

@Component({
  selector: 'activities-incorso-individuali',
  templateUrl: './activities-incorso-individuali.component.html',
  styleUrls: ['./activities-incorso-individuali.component.scss']
})
export class AttivitaIncorsoIndividualiComponent implements OnInit {

  @Input() esperienzaSvolta: EsperienzaSvolta[] = [];

  totalRecords: number = 0;
  pageSize: number = 10;
  tipologiaAzienda: TipologiaAzienda[] = [];
  status: Stato[] = [];
  order: string = "primo";

  constructor(private sorterService: SorterService, private route: ActivatedRoute, private dataService: DataService) {
  }

  ngOnInit() {

    console.log('AttivitaIncorsoIndividualiComponent');
     
    this.dataService.getData("tipologiaAzienda").subscribe((data) => {
      this.tipologiaAzienda = data;
      this.dataService.getData("statoAttivita").subscribe((sData) => {
         
        this.status = sData;
        this.getActivitiesInCorso(1);
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

  pageChanged(page: number) {
    this.getActivitiesInCorso(page);
  }

  getActivitiesInCorso(page: number) {
    let today: Date = new Date();

    let start: Date = startOfMonth(today);
    // let end: Date = endOfMonth(today);

    console.log('activities-incorso.component.ts' + start + " - ");

    this.dataService.getEsperienzaSvoltaAPI(this.dataService.aziendaId, null, 
      '',//start.getTime(),
      null, null, null, null, page - 1, this.pageSize)
      .subscribe((response: IPagedES) => {
        this.totalRecords = response.totalElements;
        this.esperienzaSvolta = response.content;
      });
  }

  sort(prop: string) {
    this.sorterService.sort(this.esperienzaSvolta, prop);
  }

  onSelectChange(ev) {
    //alert(this.order);
  }


}


