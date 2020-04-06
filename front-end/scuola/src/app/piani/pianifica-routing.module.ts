import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PianificaComponent } from './pianifica.component';
import { PianificaContainerComponent } from './pianifica-container.component';
import { PianoDettaglioComponent } from './actions/piano-dettaglio/piano-dettaglio.component';
import { PianoModificaCompetenzeComponent } from './actions/piano-modifica-competenze/piano-modifica-competenze.component';
import { EditPianoModalComponent } from './actions/edit-piano-modal/edit-piano-modal.component';
import { DuplicaPianoModal } from './actions/duplica-piano-modal/duplica-piano-modal.component';
import { NewPianoModalComponent } from './actions/new-piano-modal/new-piano-modal.component';
import { DocumentoCancellaModal } from './actions/documento-cancella-modal/documento-cancella-modal.component';
import { PianoModificaDatiComponent } from './actions/piano-modifica-dati/piano-modifica-dati.component';
import { PianoModificaTipologieComponent } from './actions/piano-modifica-tipologie/piano-modifica-tipologie.component';
import { PianificaCancellaModal } from './actions/cancella-piano-modal/pianifica-cancella-modal.component';
import { ActivatePianoModal } from './actions/activate-piano-modal/activate-piano-modal.component';
import { DeactivatePianoModal } from './actions/deactivate-piano-modal/deactivate-piano-modal.component';

const routes: Routes = [
  {
    path: '', component: PianificaContainerComponent,
    children: [
      { path: 'list', component: PianificaComponent },
      { path: 'detail/:id', component: PianoDettaglioComponent},
      { path: 'detail/:id/modifica/dati', component: PianoModificaDatiComponent},
      { path: 'detail/:id/modifica/tipologia', component: PianoModificaTipologieComponent},
      { path: 'detail/:id/modifica/competenze', component: PianoModificaCompetenzeComponent},      
      { path: '**', pathMatch:'full', redirectTo: 'list' }
    ]
  }
];

@NgModule({
  imports: [ RouterModule.forChild(routes) ],
  exports: [ RouterModule ]
})
export class PianificaRoutingModule { 
  static components = [ PianificaContainerComponent, PianificaComponent, PianoDettaglioComponent, EditPianoModalComponent, PianoModificaCompetenzeComponent, NewPianoModalComponent, DocumentoCancellaModal, PianoModificaDatiComponent, PianoModificaTipologieComponent, DuplicaPianoModal, PianificaCancellaModal, ActivatePianoModal, DeactivatePianoModal];
}


