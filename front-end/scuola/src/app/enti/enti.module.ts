import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SharedModule }   from '../shared/shared.module';
import { EntiRoutingModule } from './enti-routing.module';
import { NgbModule,NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DesignAngularKitModule } from 'design-angular-kit';
import { DpDatePickerModule } from 'ng2-date-picker';
import { EnteCancellaModal } from './actions/cancella-ente-modal/ente-cancella-modal.component';
import { SubNavbarModule } from '../sub-navbar/sub-navbar.module';
import { CreaEnteModalComponent } from './actions/crea-ente-modal/crea-ente-modal.component';
import { AbilitaEntePrimaModal } from './actions/abilita-ente-prima-modal/abilita-ente-prima-modal.component';
import { AbilitaEnteSecondaModal } from './actions/abilita-ente-secondo-modal/abilita-ente-seconda-modal.component';
import { AnnullaInvitoModal } from './actions/annulla-invito-modal/annulla-invito-modal.component';
import { DocumentoCancellaModal } from './actions/documento-cancella-modal/documento-cancella-modal.component';
import { ConvenzioneCancellaModal } from './actions/cancella-convenzione-modal/conv-cancella-modal.component';

@NgModule({
  imports:      [ 
    SharedModule,
    EntiRoutingModule,
    NgbModule.forRoot(),
    DpDatePickerModule,
    CommonModule,
    FormsModule,
    DesignAngularKitModule,
    SubNavbarModule  ],
  providers: [
    NgbActiveModal,
  ],
  entryComponents: [
    CreaEnteModalComponent,
    EnteCancellaModal,
    AbilitaEntePrimaModal,
    AbilitaEnteSecondaModal,
    AnnullaInvitoModal,
    ConvenzioneCancellaModal,
    DocumentoCancellaModal
  ],
declarations: [EntiRoutingModule.components]
})
export class EntiModule { }