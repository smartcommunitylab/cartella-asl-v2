import { NgModule } from '@angular/core';
import { NgbModule, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SharedModule } from '../shared/shared.module';
import { CompetenzeRoutingModule } from './competenze-routing.module';
import { PaginationModule } from '../shared/pagination/pagination.module';
import { DpDatePickerModule } from 'ng2-date-picker';
import { DesignAngularKitModule } from 'design-angular-kit'
import { AppModule } from '../app.module';
import { CreaCompetenzaModalComponent } from './actions/create-competenza-modal/crea-competenza-modal.component'
import { ModificaCompetenzaModalComponent } from './actions/modifica-competenza-modal/modifica-competenza-modal.component';
import { CompetenzaCancellaModal } from './actions/cancella-competenza-modal/competenza-cancella-modal.component';
import { CompetenzaModificaDatiComponent } from './actions/competenza-modifica-dati/competenza-modifica-dati.component';

@NgModule({
  imports: [
    CompetenzeRoutingModule,
    SharedModule,
    NgbModule.forRoot(),
    DpDatePickerModule,
    DesignAngularKitModule,
    PaginationModule
  ],
  declarations: [
    CompetenzeRoutingModule.components,
    CompetenzaModificaDatiComponent
  ],
  entryComponents: [
    CreaCompetenzaModalComponent,
    ModificaCompetenzaModalComponent,
    CompetenzaCancellaModal
  ],
  providers: [
    NgbActiveModal,
    AppModule
  ]
})
export class CompetenzeModule { }