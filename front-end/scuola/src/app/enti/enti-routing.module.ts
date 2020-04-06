import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { EntiComponent } from './enti.component';
import { EntiContainerComponent } from './enti-container.component';
import { CreaEnteModalComponent } from './actions/crea-ente-modal/crea-ente-modal.component';
import { EnteDettaglioComponent } from './actions/ente-dettaglio/ente-dettaglio.component';
import { EnteDettaglioModificaComponent } from './actions/ente-modifica-dettaglio/ente-modifica.component';
import { EnteCancellaModal } from './actions/cancella-ente-modal/ente-cancella-modal.component';

const routes: Routes = [
  {
    path: '', component: EntiContainerComponent,
    children: [
      { path: 'list', component: EntiComponent },
      { path: 'detail/:id', component: EnteDettaglioComponent},
      { path: 'detail/:id/modifica/dati', component: EnteDettaglioModificaComponent},
      { path: '**', pathMatch:'full', redirectTo: 'list' }
    ]
  }
];

@NgModule({
  imports: [ RouterModule.forChild(routes) ],
  exports: [ RouterModule ]
})
export class EntiRoutingModule { 
  static components = [
    EntiContainerComponent,
    EntiComponent,
    CreaEnteModalComponent,
    EnteDettaglioComponent,
    EnteDettaglioModificaComponent,
    EnteCancellaModal
  ];
}


