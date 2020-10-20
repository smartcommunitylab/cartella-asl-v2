import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SharedModule }   from '../shared/shared.module';
import { AttivitaRoutingModule } from './attivita-routing.module';
import { NgbModule,NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { from } from 'rxjs/observable/from';
import { DesignAngularKitModule } from 'design-angular-kit' 
// import { DocumentoCancellaModal } from './actions/documento-cancella-modal/documento-cancella-modal.component';
import { DpDatePickerModule } from 'ng2-date-picker';
import { IstitutiSelectorModule } from '../shared/istituti-selector/istituti-selector.module';
import { SubNavbarModule } from '../sub-navbar/sub-navbar.module';
import { ReactiveFormsModule } from '@angular/forms';


@NgModule({
  imports:      [ 
    SharedModule,
    AttivitaRoutingModule,
    NgbModule.forRoot(),
    DpDatePickerModule,
    CommonModule,
    FormsModule,
    DesignAngularKitModule,
    IstitutiSelectorModule,
    SubNavbarModule,
    ReactiveFormsModule
  ],
  providers: [
    NgbActiveModal,
  ],
  entryComponents: [
    // DocumentoCancellaModal
  ],
declarations: [AttivitaRoutingModule.components]
})
export class AttivitaModule { }