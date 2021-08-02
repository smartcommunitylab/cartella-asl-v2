import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SharedModule } from '../shared/shared.module';
import { IstitutoRoutingModule } from './istituto-routing.module';
import { NgbModule, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DesignAngularKitModule } from 'design-angular-kit'
import { SubNavbarModule } from '../sub-navbar/sub-navbar.module';
import { ChartsModule } from 'ng2-charts';
import { UpdateDocenteModalComponent } from './actions/update-docente-modal/update-docente-modal.component';
import { RuoloCancellaModal } from './actions/ruolo-cancella-modal/ruolo-cancella-modal.component';
import { ConfirmModalClasseComponent } from './actions/confirm-modal-classe/confirm-modal-classe.component';
import { ConfirmModalAddClasseComponent } from './actions/confirm-modal-add-classe/confirm-modal--add-classe.component';
import { ConfirmModalRemoveClasseComponent } from './actions/confirm-modal-remove-classe/confirm-modal-remove-classe.component';

@NgModule({
  imports: [
    SharedModule,
    IstitutoRoutingModule,
    NgbModule.forRoot(),
    CommonModule,
    FormsModule,
    DesignAngularKitModule,
    SubNavbarModule,
    ChartsModule
  ],
  providers: [
    NgbActiveModal,
  ],
  entryComponents: [
    UpdateDocenteModalComponent,
    RuoloCancellaModal,
    ConfirmModalClasseComponent,
    ConfirmModalAddClasseComponent,
    ConfirmModalRemoveClasseComponent
  ],
  declarations: [IstitutoRoutingModule.components]
})
export class IstitutoModule { }