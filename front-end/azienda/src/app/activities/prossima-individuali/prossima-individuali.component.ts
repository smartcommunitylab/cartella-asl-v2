import { Component, OnInit, Input } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';

import { IPagedResults, IOffer, EsperienzaSvolta, IPagedES, TipologiaAzienda, Stato } from '../../shared/interfaces';
import { DataService } from '../../core/services/data.service';
import { SorterService } from '../../core/services/sorter.service';
import { startOfDay, addDays, endOfISOYear } from 'date-fns';


@Component({
  selector: 'activities-prossima-individuali',
  templateUrl: './prossima-individuali.component.html',
  styleUrls: ['./prossima-individuali.component.scss']
})

export class AttivitaProssimaIndividualiComponent implements OnInit {

  @Input() esperienzaSvolta: EsperienzaSvolta[] = [];
  totalRecords: number = 0;
  pageSize: number = 10;
  tipologiaAzienda: TipologiaAzienda[] = [];
  status: Stato[] = [];
  order: string = "primo";

  constructor(private sorterService: SorterService, private route: ActivatedRoute, private dataService: DataService) {
  }

  ngOnInit() {
    console.log('AttivitaProssimaIndividualiComponent');
    this.dataService.getData("tipologiaAzienda").subscribe((data) => {
      this.tipologiaAzienda = data;
      this.dataService.getData("statoAttivita").subscribe((sData) => {
        this.status = sData;
        this.getActivitiesProssima(1);
      });
    });
  }

  pageChanged(page: number) {
    this.getActivitiesProssima(page);
  }

  getActivitiesProssima(page: number) {
    // call with time for from next month to the end of year.
    let tomorrow: Date = addDays(startOfDay(new Date()), 1);
    // let endOfYear: Date = endOfISOYear(tomorrow);

    this.dataService.getEsperienzaSvoltaAPI(this.dataService.aziendaId, null, tomorrow.getTime(), null, null, null, null, (page - 1), this.pageSize)
      .subscribe((response: IPagedES) => {
        this.totalRecords = response.totalElements;
        this.esperienzaSvolta = response.content;
      });
  }

  getStato(id: any) {
    for (let stato of this.status) {
      if (stato.id == id) {
        return stato.titolo;
      }
    }

  }

  getTipo(id: any) {
    for (let tipo of this.tipologiaAzienda) {
      if (tipo.id == id) {
        return tipo.titolo;
      }
    }

  }

  sort(prop: string) {
    this.sorterService.sort(this.esperienzaSvolta, prop);
  }

  onSelectChange(ev) {
    //alert(this.order);
  }

}