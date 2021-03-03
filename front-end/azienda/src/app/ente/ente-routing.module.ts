import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { EnteContainerComponent } from './ente-container.component';
import { EnteDettaglioComponent } from './actions/ente-dettaglio/ente-dettaglio.component';
import { EnteDettaglioModificaComponent } from './actions/ente-modifica-dettaglio/ente-modifica.component';
import { CreaNuovaUtenteModalComponent } from './actions/crea-nuova-utente-modal/crea-nuova-utente-modal.component';
import { EnteModificaAbilitatiComponent } from './actions/ente-modifica-abilitati/ente-modifica-abilitati';
import { RuoloCancellaModal } from './actions/ruolo-cancella-modal/ruolo-cancella-modal.component';

const routes: Routes = [
  {
    path: '', component: EnteContainerComponent,
    children: [
      { path: 'scheda', component: EnteDettaglioComponent},
      { path: 'scheda/modifica/dati', component: EnteDettaglioModificaComponent},
      { path: 'scheda/modifica/abilitati', component: EnteModificaAbilitatiComponent}
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
    EnteDettaglioModificaComponent,
    CreaNuovaUtenteModalComponent,
    RuoloCancellaModal,
    EnteModificaAbilitatiComponent
  ];
}


