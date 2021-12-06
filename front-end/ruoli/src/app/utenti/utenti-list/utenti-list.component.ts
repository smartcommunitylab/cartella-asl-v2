import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../../core/services/data.service';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { PaginationComponent } from '../../shared/pagination/pagination.component';
import { DeleteUserModalComponent } from '../modals/delete-user-modal/delete-user-modal.component';
import { PermissionService } from '../../core/services/permission.service';
import { Observable, of } from 'rxjs';
import { catchError, debounceTime, distinctUntilChanged, map, tap, switchMap } from 'rxjs/operators';

@Component({
  selector: 'cm-utenti-list',
  templateUrl: './utenti-list.component.html',
  styleUrls: ['./utenti-list.component.css']
})
export class UtentiListComponent implements OnInit {

  constructor(
    private dataService: DataService,
    private route: ActivatedRoute,
    private router: Router,
    private modalService: NgbModal,
    private permissionService: PermissionService) { }

  @ViewChild('cmPagination') cmPagination: PaginationComponent;

  filtro = {
    page: 1,
    text: '',
    role: '',
    userDomainId: ''
  }
  totalRecords: number = 0;
  pageSize: number = 10;
  utenti;
  istituto: any;
  searchingIstituto = false;
  searchIstitutoFailed = false;
  ruoli = ['STUDENTE', 'DIRIGENTE_SCOLASTICO', 'FUNZIONE_STRUMENTALE', 'REFERENTE_AZIENDA', 'LEGALE_RAPPRESENTANTE_AZIENDA', 'ADMIN'];

  title: string = "Utenti";

  ngOnInit() {
    this.route.queryParamMap.subscribe(params => {
      console.log(params);
      let roleId = params.get('role');
      if (!(roleId == 'STUDENTE' || roleId == 'DIRIGENTE_SCOLASTICO' || roleId == 'LEGALE_RAPPRESENTANTE_AZIENDA' ||
        roleId == 'REFERENTE_AZIENDA' || roleId == 'FUNZIONE_STRUMENTALE' || roleId == '')) {
        this.router.navigate(['./'], { relativeTo: this.route, queryParams: { role: '' } });
        return;
      }
      this.filtro.role = roleId;
      this.getUtentiPage(1);
    });
  }
  pageChanged(page: number) {
    this.getUtentiPage(page);
    //this.recreateFilterUrl();
  }
  getUtentiPage(page: number) {
    this.filtro.page = page;
    if(this.istituto) {
      this.filtro.userDomainId = this.istituto.id;
    } else {
      this.filtro.userDomainId = '';
    }
    this.dataService.getUsersList((page - 1), this.pageSize, this.filtro)
      .subscribe((response) => {
        this.totalRecords = response.totalElements;
        this.utenti = response.content;
      }
        ,
        (err: any) => console.log(err),
        () => console.log('get users list'));
  }
  searchUser() {
    if (this.cmPagination.currentPage == 1) {
      this.getUtentiPage(1);
    } else {
      this.cmPagination.changePage(1);
    }
  }

  deleteUser(userId) {
    const modalRef = this.modalService.open(DeleteUserModalComponent);
    modalRef.componentInstance.onDelete.subscribe(res => {
      this.dataService.deleteUser(userId).subscribe(updated => {
        this.getUtentiPage(this.filtro.page);
      });
    });
  }

  getIstituti = (text$: Observable<string>) => 
  text$.pipe(
    debounceTime(500),
    distinctUntilChanged(),
    tap(() => this.searchingIstituto = true),
    switchMap(term => this.dataService.searchIstituti(0, 10, term).pipe(
      map(result => {
        let entries = [];
        if(result.content) {            
          result.content.forEach(element => {
            entries.push(element);
          });
        }
        return entries;
      }),
      tap(() => this.searchIstitutoFailed = false),
      catchError((error) => {
        console.log(error);
        this.searchIstitutoFailed = true;
        return of([]);
      })
      )
    ),
    tap(() => this.searchingIstituto = false)
  ); 

  formatterIstituto = (x: {name: string}) => x.name;

  resetIstituto() {
    this.istituto = null;
  }
}
