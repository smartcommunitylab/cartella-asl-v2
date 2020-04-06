import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UtentiRoutingModule } from './utenti-routing.module';
import { UtentiComponent } from './utenti.component';
import { SharedModule } from '../shared/shared.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SubNavbarModule } from '../sub-navbar/sub-navbar.module';
import { UtenteDetailComponent } from './utente-detail/utente-detail.component';
import { UtentiListComponent } from './utenti-list/utenti-list.component';
import { UtenteAddRoleComponent } from './utente-add-role/utente-add-role.component';
import { DeleteUserModalComponent } from './modals/delete-user-modal/delete-user-modal.component';
import { DeleteRoleSingleModalComponent } from './modals/delete-role-single-modal/delete-role-single-modal.component';
import { DeleteRoleModalComponent } from './modals/delete-role-modal/delete-role-modal.component';

@NgModule({
  imports: [
    SharedModule,
    CommonModule,
    NgbModule.forRoot(),
    SubNavbarModule,
    UtentiRoutingModule
  ],
  declarations: [UtentiComponent, UtenteDetailComponent, UtentiListComponent, UtenteAddRoleComponent, DeleteUserModalComponent, DeleteRoleSingleModalComponent, DeleteRoleModalComponent],
  entryComponents: [
    DeleteUserModalComponent,
    DeleteRoleSingleModalComponent,
    DeleteRoleModalComponent,
    UtenteAddRoleComponent
  ]
})
export class UtentiModule { }
