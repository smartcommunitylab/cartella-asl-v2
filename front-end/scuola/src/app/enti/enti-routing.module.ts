import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { EntiComponent } from './enti.component';
import { EntiContainerComponent } from './enti-container.component';
import { CreaEnteModalComponent } from './actions/crea-ente-modal/crea-ente-modal.component';
import { EnteDettaglioComponent } from './actions/ente-dettaglio/ente-dettaglio.component';
import { EnteDettaglioModificaComponent } from './actions/ente-modifica-dettaglio/ente-modifica.component';
import { EnteCancellaModal } from './actions/cancella-ente-modal/ente-cancella-modal.component';
import { AbilitaEntePrimaModal } from './actions/abilita-ente-prima-modal/abilita-ente-prima-modal.component';
import { AbilitaEnteSecondaModal } from './actions/abilita-ente-secondo-modal/abilita-ente-seconda-modal.component';
import { AnnullaInvitoModal } from './actions/annulla-invito-modal/annulla-invito-modal.component';
import { CreaConvenzioneComponent } from './actions/crea-convenzione-component/crea-convenzione.component';
import { EnteConvenzioneModificaComponent } from './actions/ente-modifica-convenzione/ente-modifica-convenzione.component';
import { DocumentoCancellaModal } from './actions/documento-cancella-modal/documento-cancella-modal.component';
import { ConvenzioneCancellaModal } from './actions/cancella-convenzione-modal/conv-cancella-modal.component';

const routes: Routes = [
  {
    path: '', component: EntiContainerComponent,
    children: [
      { path: 'list', component: EntiComponent },
      { path: 'detail/:id', component: EnteDettaglioComponent},
      { path: 'detail/:id/modifica/dati', component: EnteDettaglioModificaComponent},
      { path: 'detail/:id/aggiungi/convenzione', component: CreaConvenzioneComponent},
      { path: 'detail/:id/modifica/convenzione/:idConv', component: EnteConvenzioneModificaComponent},
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
    EnteCancellaModal,
    AbilitaEntePrimaModal,
    AbilitaEnteSecondaModal,
    AnnullaInvitoModal,
    CreaConvenzioneComponent,
    EnteConvenzioneModificaComponent,
    ConvenzioneCancellaModal,
    DocumentoCancellaModal
  ];
}


