import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LocalStorageModule } from '@ngx-pwa/local-storage';

const app_routes: Routes = [
  { path: 'attivita', loadChildren: 'app/attivita/attivita.module#AttivitaModule' },
  { path: 'offerte', loadChildren: 'app/offerte/offerte.module#OfferteModule' },
  { path: 'enti', loadChildren: 'app/enti/enti.module#EntiModule' },
  { path: 'studenti', loadChildren: 'app/studenti/studenti.module#StudentiModule' },
  { path: 'istituti', loadChildren: 'app/istituti/istituti.module#IstitutiModule' },
  { path: 'terms/:authorized', loadChildren: 'app/terms/terms.module#TermsModule' },
  { path: '**', pathMatch: 'full', redirectTo: '/attivita/list' }
];

@NgModule({
  imports: [RouterModule.forRoot(app_routes, {  useHash: true }), LocalStorageModule],
  exports: [RouterModule]
})
export class AppRoutingModule { }
