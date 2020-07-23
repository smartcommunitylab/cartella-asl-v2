import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DashboardRoutingModule } from './dashboard-routing.module';
import { DashboardComponent } from './dashboard.component';
import { DashboardSistemaComponent } from './sistema/sistema.component';
import { DashboardAttivitaComponent } from './attivita/attivita.component';
import { DashboardEsperienzeComponent } from './esperienze/esperienze.component';
import { SharedModule } from '../shared/shared.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SubNavbarModule } from '../sub-navbar/sub-navbar.module';

@NgModule({
  imports: [
    SharedModule,
    CommonModule,
    NgbModule.forRoot(),
    SubNavbarModule,
    DashboardRoutingModule
  ],
  declarations: [DashboardComponent, DashboardSistemaComponent, DashboardAttivitaComponent, DashboardEsperienzeComponent],
  entryComponents: []
})
export class DashboardModule { }
