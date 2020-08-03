import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DashboardComponent } from './dashboard.component';
import { DashboardSistemaComponent } from './sistema/sistema.component';
import { DashboardAttivitaComponent } from './attivita/attivita.component';
import { DashboardEsperienzeComponent } from './esperienze/esperienze.component';
import { DashboardRegistrazioniComponent } from './registrazioni/registrazioni.component';

const routes: Routes = [
  { path: '', component: DashboardComponent,
    children: [
      { path: 'esperienze', component: DashboardEsperienzeComponent },
      { path: 'registrazioni', component: DashboardRegistrazioniComponent },
      { path: 'attivita', component: DashboardAttivitaComponent },
      { path: 'sistema', component: DashboardSistemaComponent }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DashboardRoutingModule { }
