import { Component, OnInit, ViewChild, Input, Output, EventEmitter } from '@angular/core';
import { DataService } from '../../core/services/data.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { PaginationComponent } from '../../shared/pagination/pagination.component';
import { PermissionService } from '../../core/services/permission.service';

@Component({
  selector: 'cm-utente-add-role',
  templateUrl: './utente-add-role.component.html',
  styleUrls: ['./utente-add-role.component.css']
})
export class UtenteAddRoleComponent implements OnInit {

  constructor(
    private dataService: DataService,
    public activeModal: NgbActiveModal,
    public permissionService: PermissionService) { }

  @ViewChild('istitutiPagination') istitutiPagination: PaginationComponent;
  @ViewChild('aziendePagination') aziendePagination: PaginationComponent;
  @ViewChild('studentiPagination') studentiPagination: PaginationComponent;

  @Input() user;
  @Output() onRoleAdded = new EventEmitter();

  profile;
  roleId = '';

  filter = {
    istituto: {
      text: ''
    },
    azienda: {
      text: '',
      pIva: ''
    },
    studente: {
      text: '',
      cf: ''
    }
  }
  pageSize = 10;
  istitutiResult = {
    list: [],
    totalRecords: 0,
    selectedIstituto: undefined
  }
  aziendeResult = {
    list: [],
    totalRecords: 0,
    selectedAzienda: undefined
  }
  studentiResult = {
    list: [],
    totalRecords: 0,
    selectedStudente: undefined
  }
  usingProfileIds: boolean = false;

  ngOnInit() {
    this.dataService.getProfile().subscribe(profile => {
      console.log(profile);
      this.profile = profile;
      this.onRuoloSelect();
    }, err => {
    });
  }

  getIstitutiPage(page: number) {
    if (this.permissionService.getMyPermissions().addRole.from_all_entities) {
      this.usingProfileIds = false;
      this.dataService.getIstituti((page - 1), this.pageSize, this.filter.istituto)
        .subscribe((response) => {
          this.istitutiResult.totalRecords = response.totalElements;
          this.istitutiResult.list = response.content;
        },
          (err: any) => console.log(err),
          () => console.log('get istituti list'));
    } else {
      this.istitutiResult.totalRecords = this.profile.istituti.lenght;
      let profile = this.profile;
      this.istitutiResult.list = Object.keys(this.profile.istituti).map(function(istitutoId){
        let istituto = profile.istituti[istitutoId];
        return istituto;
      });
      this.usingProfileIds = true;
      console.log('get istituti list, using profile istituti')
    }
  }
  getAziendePage(page: number) {
    if (this.permissionService.getMyPermissions().addRole.from_all_entities) {
      this.usingProfileIds = false;
      this.dataService.getAziende((page - 1), this.pageSize, this.filter.azienda)
        .subscribe((response) => {
          this.aziendeResult.totalRecords = response.totalElements;
          this.aziendeResult.list = response.content;
        },
          (err: any) => console.log(err),
          () => console.log('get aziende list'));
    } else {
      this.aziendeResult.totalRecords = this.profile.aziende.lenght;
      let profile = this.profile;
      this.aziendeResult.list = Object.keys(this.profile.aziende).map(function(aziendaId){
        let azienda = profile.aziende[aziendaId];
        return azienda;
      });
      this.usingProfileIds = true;
      console.log('get aziende list, using profile istituti')
    }
  }
  getStudentiPage(page: number) {
    this.dataService.getStudenti((page - 1), this.pageSize, this.filter.studente)
      .subscribe((response) => {
        this.studentiResult.totalRecords = response.totalElements;
        this.studentiResult.list = response.content;
      },
        (err: any) => console.log(err),
        () => console.log('get studenti list'));
  }

  searchIstituti() {
    if (this.istitutiPagination.currentPage == 1) {
      this.getIstitutiPage(1);
    } else {
      this.istitutiPagination.changePage(1);
    }
  }
  searchAziende() {
    if (this.aziendePagination.currentPage == 1) {
      this.getAziendePage(1);
    } else {
      this.aziendePagination.changePage(1);
    }
  }
  searchStudenti() {
    if (this.studentiPagination.currentPage == 1) {
      this.getStudentiPage(1);
    } else {
      this.studentiPagination.changePage(1);
    }
  }

  addRole() {
    let tmpRoleIds = '';
    if (this.roleId == 'STUDENTE') {
      // tmpRoleIds = this.user.studentiId;
      tmpRoleIds = this.studentiResult.selectedStudente.id;
      this.user.roles.push({ 'role': this.roleId, "domainId": this.studentiResult.selectedStudente.id });
    } else if (this.roleId == 'DIRIGENTE_SCOLASTICO' || this.roleId == 'FUNZIONE_STRUMENTALE') {
      // tmpRoleIds = this.user.istitutiId;
      tmpRoleIds = this.istitutiResult.selectedIstituto.id;
      this.user.roles.push({ 'role': this.roleId, "domainId": this.istitutiResult.selectedIstituto.id });
      
    } else if (this.roleId == 'LEGALE_RAPPRESENTANTE_AZIENDA' || this.roleId == 'REFERENTE_AZIENDA') {
      // tmpRoleIds = this.user.aziendeId;
      tmpRoleIds = this.aziendeResult.selectedAzienda.id;
      this.user.roles.push({ 'role': this.roleId, "domainId": this.aziendeResult.selectedAzienda.id });
    }
    this.dataService.updateRole(this.user.id, this.roleId, tmpRoleIds)
      .subscribe((response) => {
        this.activeModal.close('');
        this.onRoleAdded.emit();
      },
        (err: any) => console.log(err),
        () => console.log('add role'));
  }

  addDisabled() {
    if (this.roleId == 'STUDENTE') {
      return !this.studentiResult.selectedStudente;
    } else if (this.roleId == 'DIRIGENTE_SCOLASTICO' || this.roleId == 'FUNZIONE_STRUMENTALE') {
      return !this.istitutiResult.selectedIstituto;
    } else if (this.roleId == 'LEGALE_RAPPRESENTANTE_AZIENDA' || this.roleId == 'REFERENTE_AZIENDA') {
      return !this.aziendeResult.selectedAzienda;
    } else if (this.roleId == 'ADMIN') {
      return false;
    }
    return true;
  }

  onRuoloSelect() {
    if (this.roleId == 'STUDENTE') {
      this.getStudentiPage(1);
    } else if (this.roleId == 'DIRIGENTE_SCOLASTICO' || this.roleId == 'FUNZIONE_STRUMENTALE') {
      this.getIstitutiPage(1);
    } else if (this.roleId == 'REFERENTE_AZIENDA' || this.roleId == 'LEGALE_RAPPRESENTANTE_AZIENDA') {
      this.getAziendePage(1);
    }
  }

}
