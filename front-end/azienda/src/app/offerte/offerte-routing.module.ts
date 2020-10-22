import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { OfferteComponent } from './offerte.component';
import { OfferteContainerComponent } from './offerte-container.component';
import { CreaOffertaModalComponent } from './actions/crea-offerta-modal/crea-offerta-modal.component';
import { OfferteDettaglioComponent } from './actions/offerte-dettaglio/offerte-dettaglio.component';

const routes: Routes = [
  {
    path: '', component: OfferteContainerComponent,
    children: [
      { path: 'list', component: OfferteComponent },
      { path: 'detail/:id', component: OfferteDettaglioComponent },
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
  ];
}


