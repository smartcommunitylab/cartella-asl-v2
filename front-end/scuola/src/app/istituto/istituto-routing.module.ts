import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ConfirmModalAddClasseComponent } from './actions/confirm-modal-add-classe/confirm-modal--add-classe.component';
import { ConfirmModalClasseComponent } from './actions/confirm-modal-classe/confirm-modal-classe.component';
import { ConfirmModalRemoveClasseComponent } from './actions/confirm-modal-remove-classe/confirm-modal-remove-classe.component';
import { DocenteModificaStudentiComponent } from './actions/docente-modifica-studenti/docente-modifica-studenti.component';
import { IstitutoDettaglioModificaComponent } from './actions/istituto-modifica-dettaglio/istituto-modifica.component';
import { RuoloCancellaModal } from './actions/ruolo-cancella-modal/ruolo-cancella-modal.component';
import { UpdateDocenteModalComponent } from './actions/update-docente-modal/update-docente-modal.component';
import { IstitutoContainerComponent } from './istituto-container.component';
import { IstitutoComponent } from './istituto.component';

const routes: Routes = [
  {
    path: '', component: IstitutoContainerComponent,
    children: [
      { path: 'scheda', component: IstitutoComponent },
      { path: 'modifica/:id', component: IstitutoDettaglioModificaComponent},
      { path: 'modificaStudenti/:id', component: DocenteModificaStudentiComponent},
      { path: '**', pathMatch:'full', redirectTo: 'scheda' }
    ]
  }
];

@NgModule({
  imports: [ RouterModule.forChild(routes) ],
  exports: [ RouterModule ]
})
export class IstitutoRoutingModule { 
  static components = [
    IstitutoContainerComponent,
    IstitutoComponent,
    IstitutoDettaglioModificaComponent,
    DocenteModificaStudentiComponent,
    UpdateDocenteModalComponent,
    RuoloCancellaModal,
    ConfirmModalClasseComponent,
    ConfirmModalAddClasseComponent,
    ConfirmModalRemoveClasseComponent
  ];
}


