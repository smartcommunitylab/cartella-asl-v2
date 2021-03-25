import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { IstitutoDettaglioModificaComponent } from './actions/ente-modifica-dettaglio/istituto-modifica.component';
import { IstitutoContainerComponent } from './istituto-container.component';
import { IstitutoComponent } from './istituto.component';

const routes: Routes = [
  {
    path: '', component: IstitutoContainerComponent,
    children: [
      { path: 'scheda', component: IstitutoComponent },
      { path: 'modifica/:id', component: IstitutoDettaglioModificaComponent},
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
    IstitutoDettaglioModificaComponent
  ];
}

