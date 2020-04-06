import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../../core/services/data.service';
import { PaginationComponent } from '../../shared/pagination/pagination.component';
import { Router, ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';

@Component({
  selector: 'cm-attivita-list',
  templateUrl: './attivita-list.component.html',
  styleUrls: ['./attivita-list.component.css']
})
export class AttivitaListComponent implements OnInit {

  title: string;
  filtro;
  totalRecords: number = 0;
  pageSize: number = 10;
  attivitaStudente;
  stati = [ {"name": "In attesa", "value": "in_attesa"}, { "name": "In corso", "value": "in_corso" }, { "name": "Revisionare", "value": "revisione" }, {"name": "Archiviata", "value": "archiviata"}];
  filterDatePickerConfig = {
    locale: 'it',
    firstDayOfWeek: 'mo'
  };

  @ViewChild('cmPagination') private cmPagination: PaginationComponent;

  constructor(
    private dataService: DataService,
    private route: ActivatedRoute,
    private router: Router,
    private location: Location) {
    this.filtro = {
      page: 1
    }
  }
  ngOnInit(): void {
    this.title = 'Lista attività';
    console.log(this.route);

    if (this.route.snapshot.queryParamMap.get('page')) {
      this.filtro.page = this.route.snapshot.queryParamMap.get('page');
    }
    this.getAttivitaPage(this.filtro.page);
  }

  pageChanged(page: number) {
    this.getAttivitaPage(page);
    this.recreateFilterUrl();
  }
  getAttivitaPage(page: number) {
    this.filtro.page = page;
    this.dataService.getAttivitaStudenteList((page - 1), this.pageSize, this.dataService.studenteId, this.filtro)
      .subscribe((response) => {
        this.totalRecords = response.totalElements;
        this.attivitaStudente = response.content;
      }
        ,
        (err: any) => console.log(err),
        () => console.log('get attivita studente'));
  }

  recreateFilterUrl() {
    let queryParams: any = {};
    if (this.filtro.page) {
      queryParams.page = this.filtro.page;
    }
    const url = this.router
      .createUrlTree([], { relativeTo: this.route, queryParams: queryParams })
      .toString();
    this.location.go(url);
  }

  getStatoNome(statoValue) {
    if (this.stati) {
        let rtn = this.stati.find(data => data.value == statoValue);
        if (rtn) return rtn.name;
        return statoValue;
    }
}

}
