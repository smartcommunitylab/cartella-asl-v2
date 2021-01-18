import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Registrazioneomponent } from './registrazione.component';

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
    Registrazioneomponent
  ];
}


