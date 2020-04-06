import { Component, OnInit, Input } from '@angular/core';
import { ActivatedRoute, Params, } from '@angular/router';

import { IPagedResults, IPagedAA, TipologiaAzienda, Stato, AttivitaAlternanza } from '../../shared/interfaces';
import { DataService } from '../../core/services/data.service';
import { startOfDay, addDays, endOfISOYear, startOfMonth, endOfMonth } from 'date-fns';
import { SorterService } from '../../core/services/sorter.service';

@Component({
  selector: 'activities-incorso-gruppi',
  templateUrl: './activities-incorso-gruppi.component.html',
  styleUrls: ['./activities-incorso-gruppi.component.scss']
})
export class AttivitaIncorsoGruppiComponent implements OnInit {

  attivitaAlternanzaList: AttivitaAlternanza[] = [];

  totalRecords: number = 0;
  pageSize: number = 10;
  tipologiaAzienda: TipologiaAzienda[] = [];
  order: string = "primo";

  constructor(private sorterService: SorterService, private route: ActivatedRoute, private dataService: DataService) {
  }

  ngOnInit() {
    console.log('AttivitaIncorsoGruppiComponent')

    this.dataService.getData("tipologiaAzienda").subscribe((data) => {
      this.tipologiaAzienda = data;
      this.getActivitiesInCorso(1);
    });


  }

  getTipo(id: any) {
    for (let tipo of this.tipologiaAzienda) {
      if (tipo.id == id) {
        return tipo.titolo;
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

    this.dataService.getAttivitaAlternanzaForAziendaAPI(this.dataService.aziendaId, null,
      '',//start.getTime(),
      null, null, null, page - 1, this.pageSize)
      .subscribe((response: IPagedAA) => {
        this.totalRecords = response.totalElements;
        this.attivitaAlternanzaList = response.content;
      });
  }

  sort(prop: string) {
    this.sorterService.sort(this.attivitaAlternanzaList, prop);
  }

  onSelectChange(ev) {
    //alert(this.order);
  }


}


