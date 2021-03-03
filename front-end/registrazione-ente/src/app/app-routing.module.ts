import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LocalStorageModule } from '@ngx-pwa/local-storage';
import { HashLocationStrategy, LocationStrategy, PathLocationStrategy } from '@angular/common';

const app_routes: Routes = [
  { path: 'home/account/activate', loadChildren: 'app/home/home.module#HomeModule' },
  { path: 'registrazione', loadChildren: 'app/registrazione/registrazione.module#RegistrazioneModule' },
  { path: '**', pathMatch: 'full', redirectTo: 'home' }
];

@NgModule({
  imports: [
    RouterModule.forRoot(app_routes),
    LocalStorageModule
  ],
  providers: [{ provide: LocationStrategy, useClass: HashLocationStrategy }]
})
export class AppRoutingModule { }
