import { Component, OnInit, Input } from '@angular/core';
import { ActivatedRoute, Params, } from '@angular/router';

import { IPagedResults, IPagedAA, TipologiaAzienda, Stato, AttivitaAlternanza } from '../../shared/interfaces';
import { DataService } from '../../core/services/data.service';
import { startOfDay, addDays, endOfISOYear, startOfMonth, endOfMonth } from 'date-fns';
import { SorterService } from '../../core/services/sorter.service';

@Component({
  selector: 'activities-prossima-gruppi',
  templateUrl: './activities-prossima-gruppi.component.html',
  styleUrls: ['./activities-prossima-gruppi.component.scss']
})
export class AttivitaProssimaGruppiComponent implements OnInit {

  attivitaAlternanzaList: AttivitaAlternanza[] = [];

  totalRecords: number = 0;
  pageSize: number = 10;
  tipologiaAzienda: TipologiaAzienda[] = [];
  order: string = "primo";

  constructor(private sorterService: SorterService, private route: ActivatedRoute, private dataService: DataService) {
  }

  ngOnInit() {
    console.log('AttivitaProssimaGruppiComponent')

    this.dataService.getData("tipologiaAzienda").subscribe((data) => {
      this.tipologiaAzienda = data;
        this.getActivitiesProssima(1);
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
    this.getActivitiesProssima(page);
  }

  getActivitiesProssima(page: number) {
    // call with time for from next month to the end of year.
    let tomorrow: Date = addDays(startOfDay(new Date()), 1);
    // let endOfYear: Date = endOfISOYear(tomorrow);

    this.dataService.getAttivitaAlternanzaForAziendaAPI(this.dataService.aziendaId, null, tomorrow.getTime(), null, null, null, (page - 1), this.pageSize)
      .subscribe((response: IPagedAA) => {
        this.totalRecords = response.totalElements;
        this.attivitaAlternanzaList = response.content;
      });
  }


}