import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { OfferteComponent } from './offerte.component';
import { OfferteContainerComponent } from './offerte-container.component';
import { CreaOffertaModalComponent } from './actions/crea-offerta-modal/crea-offerta-modal.component';
import { OfferteDettaglioComponent } from './actions/offerte-dettaglio/offerte-dettaglio.component';
import { AssociaOffertaComponent } from './actions/associa-offerta/associa-offerta.component';
import { OffertaModificaCompetenzeComponent } from './actions/offerta-modifica-competenze/offerta-modifica-competenze.component';
import { OffertaCancellaModal } from './actions/cancella-offerta-modal/offerta-cancella-modal.component';
import { OffertaDettaglioModificaComponent } from './actions/offerta-modifica-dettaglio/offerta-modifica.component';

const routes: Routes = [
  {
    path: '', component: OfferteContainerComponent,
    children: [
      { path: 'list', component: OfferteComponent },
      { path: 'detail/:id', component: OfferteDettaglioComponent },
      { path: 'detail/:id/modifica/offerta', component: OffertaDettaglioModificaComponent },
      { path: 'detail/:id/associa/offerta', component: AssociaOffertaComponent },
      { path: 'detail/:id/modifica/competenze', component: OffertaModificaCompetenzeComponent },
      { path: '**', pathMatch:'full', redirectTo: 'list' }
    ]
  }
];

@NgModule({
  imports: [ RouterModule.forChild(routes) ],
  exports: [ RouterModule ]
})
export class OfferteRoutingModule { 
  static components = [
    OfferteContainerComponent,
    OfferteComponent,
    CreaOffertaModalComponent,
    OfferteDettaglioComponent,
    OffertaDettaglioModificaComponent,
    AssociaOffertaComponent,
    OffertaModificaCompetenzeComponent,
    OffertaCancellaModal
  ];
}


