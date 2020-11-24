import { Component, OnInit, ViewChild } from '@angular/core';
import { PaginationComponent } from '../shared/pagination/pagination.component';
import { DataService } from '../core/services/data.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'cm-studenti',
  templateUrl: './studenti.component.html',
  styleUrls: ['./studenti.component.scss']
})
export class StudentiComponent implements OnInit {

  titolo = 'Lista studenti';
  menuContent = "In questa pagina trovi tutti gli studenti che hanno un’attività in corso presso questo ente, o che hanno svolto attività in passato presso questo ente. Per vedere il dettaglio di uno studente, clicca sulla riga corrispondente.";
  showContent: boolean = false;
  studenti;
  filtro;
  filterSearch = false;
  totalRecords: number = 0;
  pageSize: number = 10;
  currentpage: number = 0;
  stati = [{ "name": "In attesa", "value": "in_attesa" }, { "name": "Attivo", "value": "attivo" }, { "name": "Inattivo", "value": "inattivo" }];
  @ViewChild('cmPagination') private cmPagination: PaginationComponent;

  constructor(
    private dataService: DataService,
    private route: ActivatedRoute,
    private router: Router) {
    this.filtro = {
      text: null
    }
  }

  ngOnInit() {
    this.getStudenti(1);
  }

  menuContentShow() {
    this.showContent = !this.showContent;
  }

  cerca() {
    this.cmPagination.changePage(1);
    this.filterSearch = true;
    this.getStudenti(1);
  }

  pageChanged(page: number) {
    this.currentpage = page;
    this.getStudenti(page);
  }

  getStudenti(page) {
    this.dataService.getStudenteForEnte(this.filtro, (page - 1), this.pageSize)
      .subscribe((response) => {
        this.totalRecords = response.totalElements;
        this.studenti = response.content;
      },
        (err: any) => console.log(err),
        () => console.log('get lista studenti'));
  }

  openDetail(studenteObj) {
    this.router.navigate(['../list/detail', studenteObj.studente.id], { relativeTo: this.route });
  }

  showTipRiga(ev, studente) {
    if (studente.attivitaAlternanza) {
      studente.toolTipRiga = studente.attivitaAlternanza.titolo + ', Anno scolastico ' + studente.attivitaAlternanza.annoScolastico;
    }
  }

  refreshStudenti() {
    this.filtro = {
      text: null
    }
    this.filterSearch = false;
    this.getStudenti(1);
  }

  setIstitutoName(student) {
    let label = '';
    if (student.istituto.name) {
      if (student.istituto.name.length > 10) {
        label = student.istituto.name.substring(0, 10) + '...';
      } else {
        label = student.istituto.name;
      }
    }
    return label;
  }

  getStatoNome(statoValue) {
    if (this.stati) {
      let rtn = this.stati.find(data => data.value == statoValue);
      if (rtn) return rtn.name;
      return statoValue;
    }
  }

}