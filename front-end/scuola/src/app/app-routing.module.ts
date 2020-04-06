import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LocalStorageModule } from '@ngx-pwa/local-storage';
import { AuthGuard } from './core/services/auth.guard';

const app_routes: Routes = [
  { path: 'piani', loadChildren: 'app/piani/pianifica.module#PianificaModule' },
  { path: 'attivita', loadChildren: 'app/attivita/attivita.module#AttivitaModule' },
  { path: 'competenze', loadChildren: 'app/competenze/competenze.module#CompetenzeModule' },
  { path: 'studenti', loadChildren: 'app/studenti/studenti.module#StudentiModule' },
  { path: 'enti', loadChildren: 'app/enti/enti.module#EntiModule' },
  { path: 'terms/:authorized', loadChildren: 'app/terms/terms.module#TermsModule'},
  { path: '**', pathMatch: 'full', redirectTo: '/attivita/list' }
];

@NgModule({
  imports: [RouterModule.forRoot(app_routes, {  useHash: true }), LocalStorageModule],
  exports: [RouterModule]
})
export class AppRoutingModule { }
