import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SharedModule }   from '../shared/shared.module';
import { EntiRoutingModule } from './enti-routing.module';
import { NgbModule,NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DesignAngularKitModule } from 'design-angular-kit' 
import { EnteCancellaModal } from './actions/cancella-ente-modal/ente-cancella-modal.component';
import { SubNavbarModule } from '../sub-navbar/sub-navbar.module';
import { CreaEnteModalComponent } from './actions/crea-ente-modal/crea-ente-modal.component';
import { AnnullaInvitoModal } from './actions/annulla-invito-modal/annulla-invito-modal.component';

@NgModule({
  imports:      [ 
    SharedModule,
    EntiRoutingModule,
    NgbModule.forRoot(),
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
    AnnullaInvitoModal
  ],
declarations: [EntiRoutingModule.components]
})
export class EntiModule { }