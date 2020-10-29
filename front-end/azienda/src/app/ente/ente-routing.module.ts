import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { EnteContainerComponent } from './ente-container.component';
import { EnteDettaglioComponent } from './actions/ente-dettaglio/ente-dettaglio.component';
import { EnteDettaglioModificaComponent } from './actions/ente-modifica-dettaglio/ente-modifica.component';

const routes: Routes = [
  {
    path: '', component: EnteContainerComponent,
    children: [
      { path: 'scheda', component: EnteDettaglioComponent},
      { path: 'scheda/modifica/dati', component: EnteDettaglioModificaComponent},
    ]
  }
];

@NgModule({
  imports: [ RouterModule.forChild(routes) ],
  exports: [ RouterModule ]
})
export class EnteRoutingModule { 
  static components = [
    EnteContainerComponent,
    EnteDettaglioComponent,
    EnteDettaglioModificaComponent
  ];
}


