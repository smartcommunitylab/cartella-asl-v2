import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DashboardComponent } from './dashboard.component';
import { DashboardSistemaComponent } from './sistema/sistema.component';

const routes: Routes = [
  { path: '', component: DashboardComponent,
    children: [
      { path: 'esperienze', component: DashboardSistemaComponent },
      { path: 'attivita', component: DashboardSistemaComponent },
      { path: 'sistema', component: DashboardSistemaComponent },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DashboardRoutingModule { }
