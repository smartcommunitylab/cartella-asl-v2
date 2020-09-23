import { Component, OnInit } from '@angular/core';
import { DataService } from '../../core/services/data.service';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Location } from '@angular/common';
import { DeleteRoleSingleModalComponent } from '../modals/delete-role-single-modal/delete-role-single-modal.component';
import { DeleteRoleModalComponent } from '../modals/delete-role-modal/delete-role-modal.component';
import { UtenteAddRoleComponent } from '../utente-add-role/utente-add-role.component';
import { PermissionService } from '../../core/services/permission.service';

@Component({
  selector: 'cm-utente-detail',
  templateUrl: './utente-detail.component.html',
  styleUrls: ['./utente-detail.component.css']
})
export class UtenteDetailComponent implements OnInit {

  constructor(
    private dataService: DataService,
    private route: ActivatedRoute,
    private router: Router,
    private modalService: NgbModal,
    private permissionService: PermissionService) { }

  navTitle: string = "Dettaglio utente";
  breadcrumbItems = [
    {
      title: "Utenti",
      location: "../../",
      customAction: true
    },
    {
      title: "Dettaglio utente",
      location: "./"
    }
  ];

  objectKeys = Object.keys;
  userId;
  user;
  isNewUser: boolean = false;
  forceInvalidFeedback: boolean = false;


  ngOnInit() {
    this.route.params.subscribe(params => {
      this.userId = +params['userid'];
      if (this.userId) {
        this.isNewUser = false;
      } else {
        this.isNewUser = true;
      }
      if (!this.isNewUser) {
        this.dataService.getUser(this.userId)
          .subscribe((response) => {
            this.user = response;
          },
            (err: any) => console.log(err),
            () => console.log('get user by id'));
      } else {
        this.user = {};
        //this.breadcrumbItems[0].location = "../";
        this.navTitle = "Nuovo utente";
      }
    });

  }

  isRolePresent(role) {
    if (!this.user || !this.user.roles) return false;
    for (var key in this.user.roles) {
      let value = this.user.roles[key];
      if (value.role == role) {
        return true;
      }
    }
    return false;
    // return this.user.roles.find(el => el == role);
  }

  saveUser() {
    this.forceInvalidFeedback = true;
    if (!(this.user.name && this.user.surname && this.user.cf && this.user.email)) {
      return;
    }

    if (this.isNewUser) {
      this.dataService.createUser(this.user)
        .subscribe((response: any) => {
          this.router.navigate(['./', response.id], { relativeTo: this.route });
        },
          (err: any) => console.log(err),
          () => console.log('save user'));
    } else {
      this.dataService.updateUser(this.user)
        .subscribe((response) => {
          this.navigateBack();
        },
          (err: any) => console.log(err),
          () => console.log('save user'));
    }
  }

  deleteRoleSingle(role, roleReferredToId) {
    let tmpRoleIds, tmpName;
    if (role == 'STUDENTE') {
      tmpName = this.user.studenti[roleReferredToId].name + " " + this.user.studenti[roleReferredToId].surname;
    } else if (role == 'DIRIGENTE_SCOLASTICO' || role == 'FUNZIONE_STRUMENTALE') {
      tmpName = this.user.istituti[roleReferredToId].name;
    } else if (role == 'LEGALE_RAPPRESENTANTE_AZIENDA' || role == 'REFERENTE_AZIENDA') {
      tmpName = this.user.aziende[roleReferredToId].name;
    }
    const modalRef = this.modalService.open(DeleteRoleSingleModalComponent);
    modalRef.componentInstance.roleAssociatedName = tmpName;
    modalRef.componentInstance.onDelete.subscribe(res => {
      // if (role == 'STUDENTE') {
      //   tmpRoleIds = this.user.studentiId;
      //   tmpRoleIds = tmpRoleIds.filter(id => id != roleReferredToId);
      // } else if (role == 'DIRIGENTE_SCOLASTICO' || role == 'FUNZIONE_STRUMENTALE') {
      //   tmpRoleIds = this.user.istitutiId;
      //   tmpRoleIds = tmpRoleIds.filter(id => id != roleReferredToId);
      // } else if (role == 'LEGALE_RAPPRESENTANTE_AZIENDA' || role == 'REFERENTE_AZIENDA') {
      //   tmpRoleIds = this.user.aziendeId;
      //   tmpRoleIds = tmpRoleIds.filter(id => id != roleReferredToId);
      // }
      // this.dataService.updateRole(this.userId, role, tmpRoleIds).subscribe(updated => {
      this.dataService.deleteRole(this.userId, role, roleReferredToId).subscribe(updated => {
        this.ngOnInit();
      });
    });
  }
  deleteRoleAll(role) {
    let tmpName;
    if (role == 'STUDENTE') {
      tmpName = "studente";
    } else if (role == 'DIRIGENTE_SCOLASTICO') {
      tmpName = "dirigente scolastico";
    } else if (role == 'FUNZIONE_STRUMENTALE') {
      tmpName = "funzione strumentale";
    } else if (role == 'LEGALE_RAPPRESENTANTE_AZIENDA') {
      tmpName = "legale rappresentante azienda";
    } else if (role == 'REFERENTE_AZIENDA') {
      tmpName = "referente azienda";
    } else if (role == 'ADMIN') {
      tmpName = "admin";
    }
    const modalRef = this.modalService.open(DeleteRoleModalComponent);
    modalRef.componentInstance.roleName = tmpName;
    modalRef.componentInstance.onDelete.subscribe(res => {
      this.dataService.deleteRole(this.userId, role, null).subscribe(updated => {
        this.ngOnInit();
      });
    });
  }
  addRole() {
    const modalRef = this.modalService.open(UtenteAddRoleComponent, { size: 'lg' });
    modalRef.componentInstance.user = this.user;
    modalRef.componentInstance.onRoleAdded.subscribe(res => {
      this.ngOnInit();
    });

  }
  navigateBack(event = null) {
    this.route.queryParamMap.subscribe(params => {
      this.router.navigate(['../../'], { relativeTo: this.route, queryParams: { role: params.get("roleback") } });
    });
  }

}
