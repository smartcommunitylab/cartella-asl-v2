import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ConfirmModalClasseComponent } from './actions/confirm-modal-classe/confirm-modal-classe.component';
import { DocenteModificaStudentiComponent } from './actions/docente-modifica-studenti/docente-modifica-studenti.component';
import { IstitutoDettaglioModificaComponent } from './actions/istituto-modifica-dettaglio/istituto-modifica.component';
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
    ConfirmModalClasseComponent,
  ];
}


