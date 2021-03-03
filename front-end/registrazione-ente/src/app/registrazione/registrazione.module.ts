import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { RegistrazioneRoutingModule } from './registrazione-routing.module';
import { NgbModule,NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DesignAngularKitModule } from 'design-angular-kit' 
import { DpDatePickerModule } from 'ng2-date-picker';

import { ReactiveFormsModule } from '@angular/forms';
import { ModificaAccountModalComponent } from './actions/modifica-account-modal/modifica-account-modal.component';
import { ModificaEnteModalComponent } from './actions/modifica-ente-modal/modifica-ente-modal.component';

@NgModule({
  imports:      [ 
    RegistrazioneRoutingModule,
    NgbModule.forRoot(),
    DpDatePickerModule,
    CommonModule,
    FormsModule,
    DesignAngularKitModule,
    ReactiveFormsModule
  ],
  providers: [
    NgbActiveModal,
  ],
  entryComponents: [
    ModificaAccountModalComponent,
    ModificaEnteModalComponent
  ],
declarations: [RegistrazioneRoutingModule.components]
})
export class RegistrazioneModule { }