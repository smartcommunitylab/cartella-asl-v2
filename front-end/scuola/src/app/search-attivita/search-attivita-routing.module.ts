import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { SearchAttivitaComponent } from './search-attivita.component';

const routes: Routes = [
  {
    path: '', component: SearchAttivitaComponent,
    children: [
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ProgrammaRoutingModule {
  static components = [SearchAttivitaComponent];
}


