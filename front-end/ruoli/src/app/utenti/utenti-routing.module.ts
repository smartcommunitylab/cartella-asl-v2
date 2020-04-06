import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { UtentiComponent } from './utenti.component';
import { UtenteDetailComponent } from './utente-detail/utente-detail.component';
import { UtentiListComponent } from './utenti-list/utenti-list.component';
import { UtenteAddRoleComponent } from './utente-add-role/utente-add-role.component';

const routes: Routes = [
  { path: '', component: UtentiComponent,
    children: [
      { path: '', component: UtentiListComponent },
      { path: 'detail', component: UtenteDetailComponent },
      { path: 'detail/:userid', component: UtenteDetailComponent }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UtentiRoutingModule { }
