import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { SearchAttivitaComponent } from './search-attivita.component';
import { RicercaModificaCompetenzeComponent } from './ricerca-modifica-competenze/ricerca-modifica-competenze.component';

const routes: Routes = [
  {
    path: '', component: SearchAttivitaComponent,
    children: [
      { path: 'modificacompetenze', component: RicercaModificaCompetenzeComponent },

    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ProgrammaRoutingModule {
  static components = [SearchAttivitaComponent, RicercaModificaCompetenzeComponent];
}


