import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../../core/services/data.service';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { PaginationComponent } from '../../shared/pagination/pagination.component';
import { PermissionService } from '../../core/services/permission.service';

@Component({
  selector: 'cm-dashboard-sistema',
  templateUrl: './sistema.component.html',
  styleUrls: ['./sistema.component.css']
})
export class DashboardSistemaComponent implements OnInit {

  constructor(
    private dataService: DataService,
    private route: ActivatedRoute,
    private router: Router,
    private modalService: NgbModal,
    private permissionService: PermissionService) { }

  @ViewChild('cmPagination') cmPagination: PaginationComponent;

  filtro = {
    page: 1,
    cf: '',
    nome: '',
    role: ''
  }
  totalRecords: number = 0;
  pageSize: number = 10;
  profile;
  report = {};
  istitutoId = "";
  annoScolastico = '2019-20';

  title: string = "Statistiche generali";

  ngOnInit() {
    this.dataService.getProfile().subscribe(profile => {
      console.log(profile)
      if (profile) {
          this.profile = profile;          
      }
    }, err => {
      console.log('error, no institute')
    });
  }

  getIstituti() {
    if(this.profile) {
      if(this.profile.istituti) {
        return Object.values(this.profile.istituti);
      }
    }
    return [];
  }

  getReport() {
    this.dataService.getUtilizzoSistema(this.istitutoId, this.annoScolastico)
      .subscribe(r => {
        if(r) {
          this.report = r;
        }
      });
  }

}
