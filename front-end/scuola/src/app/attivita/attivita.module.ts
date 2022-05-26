import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SharedModule }   from '../shared/shared.module';
import { AttivitaRoutingModule } from './attivita-routing.module';
import { NgbModule,NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { from } from 'rxjs/observable/from';
import { DesignAngularKitModule } from 'design-angular-kit' 
import { AttivitaCancellaModal } from './actions/cancella-attivita-modal/attivita-cancella-modal.component'
import { DocumentUploadModalComponent } from './actions/documento-upload-modal/document-upload-modal.component';
import { DocumentoCancellaModal } from './actions/documento-cancella-modal/documento-cancella-modal.component';
import { ArchiaviazioneAttivitaModal } from './actions/archiaviazione-attivita-modal/archiaviazione-attivita.component';
import { DpDatePickerModule } from 'ng2-date-picker';
import { SkillsSelectorModule } from '../shared/skills-selector/skills-selector.module';
import { SubNavbarModule } from '../sub-navbar/sub-navbar.module';
import { NewAttivtaModalPrimo } from './actions/new-attivita-modal-primo/new-attivita-modal-primo.component';
import { NewAttivtaModal } from './actions/new-attivita-modal/new-attivita-modal.component';
import { CreaAttivitaModalComponent } from './actions/crea-attivita-modal/crea-attivita-modal.component';
import { ReactiveFormsModule } from '@angular/forms';
import { AvvisoEnteConvenzioneModal } from './actions/avviso-ente-convenzione-modal/avviso-ente-convenzione-modal.component';
import { AvvisoArchiviaTiroModal } from './actions/avviso-archivia-tiro-modal/avviso-archivia-tiro-modal.component';
import { LocalizedDatePipe } from '../shared/pipes/localizedDatePipe';


@NgModule({
  imports:      [ 
    SharedModule,
    AttivitaRoutingModule,
    NgbModule.forRoot(),
    DpDatePickerModule,
    CommonModule,
    FormsModule,
    DesignAngularKitModule,
    SkillsSelectorModule,
    SubNavbarModule,
    ReactiveFormsModule
  ],
  providers: [
    NgbActiveModal,
    LocalizedDatePipe
  ],
  entryComponents: [
    NewAttivtaModalPrimo,
    NewAttivtaModal,
    CreaAttivitaModalComponent,
    DocumentUploadModalComponent,
    DocumentoCancellaModal,
    AttivitaCancellaModal,
    ArchiaviazioneAttivitaModal,
    AvvisoEnteConvenzioneModal,
    AvvisoArchiviaTiroModal
  ],
declarations: [AttivitaRoutingModule.components]
})
export class AttivitaModule { }