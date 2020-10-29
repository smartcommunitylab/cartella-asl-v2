import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../core/services/data.service'
import { GrowlerService, GrowlerMessageType } from '../core/growler/growler.service';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { PaginationComponent } from '../shared/pagination/pagination.component';

@Component({
  selector: 'istituti',
  templateUrl: './Istituti.component.html',
  styleUrls: ['./Istituti.component.scss']
})

export class IstitutiComponent implements OnInit {

  @ViewChild('cmPagination') private cmPagination: PaginationComponent;

  istituti: any[] = [];
  title: string;
  filtro;
  totalRecords: number = 0;
  pageSize: number = 10;
  menuContent = "In questa pagina trovi tutti gli istituti disponibili nel sistema. Puoi vedere il dettaglio di un singolo istituto cliccando sulla riga corrispondente";
  showContent: boolean = false;
 
  constructor(
    private dataService: DataService,
    private route: ActivatedRoute,
    private router: Router,
    private modalService: NgbModal,
    private growler: GrowlerService
  ) {
    this.filtro = {
      filterText: null
    }
  }

  ngOnInit(): void {
    this.title = 'Lista Istituti';
    this.getIstitutiPage(1);

  }

  getIstitutiPage(page: number) {
    this.dataService.getPagedIstitutiOrderByIstitutoId(this.filtro, (page - 1), this.pageSize)
      .subscribe((response) => {
        this.istituti = response.content;
        this.totalRecords = response.totalElements
      },
        (err: any) => console.log(err), () => console.log('getIstituti for filtersearch: '));

  }

  filterChanged() {
    if (this.cmPagination)
      this.cmPagination.changePage(1);
    this.getIstitutiPage(1);
  }

  openDetail(istituto) {
    this.router.navigate(['../list/detail', istituto.id], { relativeTo: this.route });
  }
 
  pageChanged(page: number) {
    this.getIstitutiPage(page);
  }

  selectStatoFilter() {
    if (this.cmPagination)
      this.cmPagination.changePage(1);
    this.getIstitutiPage(1);
  }

  cerca() {
    if (this.cmPagination)
      this.cmPagination.changePage(1);
    this.getIstitutiPage(1);
  }

  menuContentShow() {
    this.showContent = !this.showContent;
  }

  refresh() {
    this.filtro.filterText = null;
    this.getIstitutiPage(1);
    // this.router.navigate(['/competenza/list'], { relativeTo: this.route });
  }

  styleOption(istObj) {
    var style = {
      'color': 'none',
    };
    if (istObj.attivitaInCorso > 0) 
      style['color'] = '#00CF86';
    
    return style;
  }

}
