import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SharedModule }   from '../shared/shared.module';
import { PianificaRoutingModule } from './pianifica-routing.module';
import { NgbModule,NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DesignAngularKitModule } from 'design-angular-kit' 
import { PianificaCancellaModal } from './actions/cancella-piano-modal/pianifica-cancella-modal.component';
import { DocumentoCancellaModal } from './actions/documento-cancella-modal/documento-cancella-modal.component';
import { DuplicaPianoModal } from './actions/duplica-piano-modal/duplica-piano-modal.component';
import { ActivatePianoModal } from './actions/activate-piano-modal/activate-piano-modal.component';
import { DeactivatePianoModal } from './actions/deactivate-piano-modal/deactivate-piano-modal.component';
import { SkillsSelectorModule } from '../shared/skills-selector/skills-selector.module';
import { SubNavbarModule } from '../sub-navbar/sub-navbar.module';
import { NewPianoModalComponent } from './actions/new-piano-modal/new-piano-modal.component';
import { EditPianoModalComponent } from './actions/edit-piano-modal/edit-piano-modal.component';
import { PianoModificaDatiComponent } from './actions/piano-modifica-dati/piano-modifica-dati.component';


@NgModule({
  imports:      [ 
    SharedModule,
    PianificaRoutingModule,
    NgbModule.forRoot(),
    CommonModule,
    FormsModule,
    DesignAngularKitModule,
    SkillsSelectorModule,
    SubNavbarModule  ],
  providers: [
    NgbActiveModal,
  ],
  entryComponents: [
    PianificaCancellaModal,
    DuplicaPianoModal,
    ActivatePianoModal,
    DeactivatePianoModal,
    NewPianoModalComponent,
    DocumentoCancellaModal,
    EditPianoModalComponent
  ],
declarations: [PianificaRoutingModule.components, PianoModificaDatiComponent]
})
export class PianificaModule { }