import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BrowserModule } from '@angular/platform-browser/src/browser';
import { FormsModule } from '@angular/forms';
import { SearchAttivitaComponent } from './search-attivita.component'
import { RouterModule } from '@angular/router';
import { ProgrammaAddAttivitaModal } from './dettaglio-attivita-add/dettaglio-attivita-add.component';
import { ProgrammaOpenOpportunitaModal } from './dettaglio-opp-modal/dettaglio-opp-modal.component';
import { DpDatePickerModule } from 'ng2-date-picker';
import { SubNavbarModule } from '../sub-navbar/sub-navbar.module';
import { SharedModule } from '../shared/shared.module';
import { NgbModule,NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
// import { BootstrapSwitchModule } from 'angular2-bootstrap-switch';
import { ProgrammaRicercaAddressModal } from './ricerca-address-modal/ricerca-address-modal.component';
import { ProgrammaRoutingModule } from './search-attivita-routing.module';


@NgModule({
  imports: [
    FormsModule,
    ProgrammaRoutingModule,
    RouterModule,
    CommonModule,
    DpDatePickerModule,
    SubNavbarModule,
    SharedModule,
    NgbModule.forRoot(),
    // BootstrapSwitchModule.forRoot(),
  ],
  exports: [SearchAttivitaComponent],
  providers: [
    NgbActiveModal,
  ],
  entryComponents: [SearchAttivitaComponent,
    ProgrammaOpenOpportunitaModal,
    ProgrammaAddAttivitaModal,
    ProgrammaRicercaAddressModal],
  declarations: [SearchAttivitaComponent,
    ProgrammaOpenOpportunitaModal,
    ProgrammaAddAttivitaModal,
    ProgrammaRicercaAddressModal,
    ProgrammaRoutingModule.components]
})
export class SearchAttivitaModule { }