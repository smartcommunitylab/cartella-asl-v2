import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AttivitaComponent } from './attivita.component';
import { AttivitaContainerComponent } from './attivita-container.component';
import { AttivitaDettaglioComponent } from './actions/attivita-dettaglio/attivita-dettaglio.component';
import { AttivitaDettaglioModificaComponent } from './actions/attivita-modifica-dettaglio/attivita-modifica.component';
// import { DocumentoCancellaModal } from './actions/documento-cancella-modal/documento-cancella-modal.component';
import { GestionePresenzeIndividualeComponent } from './actions/gestione-presenze-individuale/gestione-preseneze-individuale.component';
// import { GestionePresenzeGruppoComponent } from './actions/gestione-presenze-gruppo/gestione-preseneze-gruppo.component';

const routes: Routes = [
  {
    path: '', component: AttivitaContainerComponent,
    children: [
      { path: 'list', component: AttivitaComponent },
      { path: 'detail/:id', component: AttivitaDettaglioComponent },
      { path: 'detail/:id/modifica/attivita', component: AttivitaDettaglioModificaComponent },
      // { path: 'detail/:id/modifica/studenti/presenze/individuale', component: GestionePresenzeIndividualeComponent },
      // { path: 'detail/:id/modifica/studenti/presenze/gruppo', component: GestionePresenzeGruppoComponent },
      { path: '**', pathMatch:'full', redirectTo: 'list' }
    ]
  }
];

@NgModule({
  imports: [ RouterModule.forChild(routes) ],
  exports: [ RouterModule ]
})
export class AttivitaRoutingModule { 
  static components = [
    AttivitaContainerComponent,
    AttivitaComponent,
    AttivitaDettaglioComponent,
    AttivitaDettaglioModificaComponent,
    GestionePresenzeIndividualeComponent,
    // DocumentoCancellaModal,
    // GestionePresenzeGruppoComponent
  ];
}


