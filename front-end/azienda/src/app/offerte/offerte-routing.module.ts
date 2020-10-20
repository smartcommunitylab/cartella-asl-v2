import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { OfferteComponent } from './offerte.component';
import { OfferteContainerComponent } from './offerte-container.component';

const routes: Routes = [
  {
    path: '', component: OfferteContainerComponent,
    children: [
      { path: 'list', component: OfferteComponent },
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
    OfferteComponent
  ];
}


