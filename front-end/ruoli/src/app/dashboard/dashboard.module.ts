import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DashboardRoutingModule } from './dashboard-routing.module';
import { DashboardComponent } from './dashboard.component';
import { DashboardSistemaComponent } from './sistema/sistema.component';
import { DashboardAttivitaComponent } from './attivita/attivita.component';
import { DashboardEsperienzeComponent } from './esperienze/esperienze.component';
import { DashboardRegistrazioniComponent } from './registrazioni/registrazioni.component';
import { DashboardRuoliEnteComponent } from './ruoli-ente/ruoli-ente.component';
import { DeleteAttivitaModalModule } from './modals/delete-attivita-modal/delete-attivita-modal.module';
import { ActivateAttivitaModalModule } from './modals/activate-attivita-modal/activate-attivita-modal.module';
import { DetailAttivitaModalModule } from './modals/detail-attivita-modal/detail-attivita-modal.module';
import { DeleteEsperienzaModalModule } from './modals/delete-esperienza-modal/delete-esperienza-modal.module';
import { DeleteRuoloEnteModalModule } from './modals/delete-ente-ruolo-modal/delete-ente-ruolo-modal.module';
import { SharedModule } from '../shared/shared.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SubNavbarModule } from '../sub-navbar/sub-navbar.module';
import { DeleteAttivitaModalComponent } from './modals/delete-attivita-modal/delete-attivita-modal.component';
import { ActivateAttivitaModalComponent } from './modals/activate-attivita-modal/activate-attivita-modal.component';
import { DetailAttivitaModalComponent } from './modals/detail-attivita-modal/detail-attivita-modal.component';
import { DeleteEsperienzaModalComponent } from './modals/delete-esperienza-modal/delete-esperienza-modal.component';
import { DeleteRuoloEnteModalComponent } from './modals/delete-ente-ruolo-modal/delete-ente-ruolo-modal.component';

@NgModule({
  imports: [
    SharedModule,
    CommonModule,
    NgbModule.forRoot(),
    SubNavbarModule,
    DashboardRoutingModule,
    DeleteAttivitaModalModule,
    ActivateAttivitaModalModule,
    DetailAttivitaModalModule,
    DeleteEsperienzaModalModule,
    DeleteRuoloEnteModalModule
  ],
  declarations: [DashboardComponent, DashboardSistemaComponent, DashboardAttivitaComponent, 
    DashboardEsperienzeComponent, DashboardRegistrazioniComponent, DashboardRuoliEnteComponent],
  entryComponents: [DeleteAttivitaModalComponent, DeleteEsperienzaModalComponent, 
    ActivateAttivitaModalComponent, DetailAttivitaModalComponent, DeleteRuoloEnteModalComponent]
})
export class DashboardModule { }
