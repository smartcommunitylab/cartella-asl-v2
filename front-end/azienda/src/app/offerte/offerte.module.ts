import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SharedModule }   from '../shared/shared.module';
import { OfferteRoutingModule } from './offerte-routing.module';
import { NgbModule,NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { from } from 'rxjs/observable/from';
import { DesignAngularKitModule } from 'design-angular-kit' 
import { DpDatePickerModule } from 'ng2-date-picker';
import { IstitutiSelectorModule } from '../shared/istituti-selector/istituti-selector.module';
import { SubNavbarModule } from '../sub-navbar/sub-navbar.module';
import { ReactiveFormsModule } from '@angular/forms';
import { CreaOffertaModalComponent } from './actions/crea-offerta-modal/crea-offerta-modal.component';
import { OffertaCancellaModal } from './actions/cancella-offerta-modal/offerta-cancella-modal.component';

@NgModule({
  imports:      [ 
    SharedModule,
    OfferteRoutingModule,
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
    CreaOffertaModalComponent,
    OffertaCancellaModal
   ],
declarations: [OfferteRoutingModule.components]
})
export class OfferteModule { }