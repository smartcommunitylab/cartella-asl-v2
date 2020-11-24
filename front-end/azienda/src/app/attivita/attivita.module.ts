import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SharedModule }   from '../shared/shared.module';
import { AttivitaRoutingModule } from './attivita-routing.module';
import { NgbModule,NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DesignAngularKitModule } from 'design-angular-kit' 
import { DocumentUploadModalComponent } from './actions/documento-upload-modal/document-upload-modal.component';
import { DpDatePickerModule } from 'ng2-date-picker';
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
    SubNavbarModule,
    ReactiveFormsModule
  ],
  providers: [
    NgbActiveModal,
  ],
  entryComponents: [
    DocumentUploadModalComponent
  ],
declarations: [AttivitaRoutingModule.components]
})
export class AttivitaModule { }