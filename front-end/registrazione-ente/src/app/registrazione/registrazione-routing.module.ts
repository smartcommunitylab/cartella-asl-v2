import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Registrazioneomponent } from './registrazione.component';
import { ModificaAccountModalComponent } from './actions/modifica-account-modal/modifica-account-modal.component';
import { ModificaEnteModalComponent } from './actions/modifica-ente-modal/modifica-ente-modal.component';

const routes: Routes = [
  { path: '', component: Registrazioneomponent },
  { path: '**', component: Registrazioneomponent },
];

@NgModule({
  imports: [ RouterModule.forChild(routes) ],
  exports: [ RouterModule ]
})
export class RegistrazioneRoutingModule { 
  static components = [
    Registrazioneomponent,
    ModificaAccountModalComponent,
    ModificaEnteModalComponent   
  ];
}


