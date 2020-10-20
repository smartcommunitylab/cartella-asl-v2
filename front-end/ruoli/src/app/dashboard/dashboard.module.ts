import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DashboardRoutingModule } from './dashboard-routing.module';
import { DashboardComponent } from './dashboard.component';
import { DashboardSistemaComponent } from './sistema/sistema.component';
import { DashboardAttivitaComponent } from './attivita/attivita.component';
import { DashboardEsperienzeComponent } from './esperienze/esperienze.component';
import { DashboardRegistrazioniComponent } from './registrazioni/registrazioni.component';
import { DeleteAttivitaModalModule } from './modals/delete-attivita-modal/delete-attivita-modal.module';
import { DeleteEsperienzaModalModule } from './modals/delete-esperienza-modal/delete-esperienza-modal.module';
import { SharedModule } from '../shared/shared.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SubNavbarModule } from '../sub-navbar/sub-navbar.module';
import { DeleteAttivitaModalComponent } from './modals/delete-attivita-modal/delete-attivita-modal.component';
import { DeleteEsperienzaModalComponent } from './modals/delete-esperienza-modal/delete-esperienza-modal.component';

@NgModule({
  imports: [
    SharedModule,
    CommonModule,
    NgbModule.forRoot(),
    SubNavbarModule,
    DashboardRoutingModule,
    DeleteAttivitaModalModule,
    DeleteEsperienzaModalModule
  ],
  declarations: [DashboardComponent, DashboardSistemaComponent, DashboardAttivitaComponent, 
    DashboardEsperienzeComponent, DashboardRegistrazioniComponent],
  entryComponents: [DeleteAttivitaModalComponent, DeleteEsperienzaModalComponent]
})
export class DashboardModule { }
