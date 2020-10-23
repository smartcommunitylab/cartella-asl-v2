import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SharedModule }   from '../shared/shared.module';
import { EnteRoutingModule } from './ente-routing.module';
import { NgbModule,NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DesignAngularKitModule } from 'design-angular-kit' 
import { DpDatePickerModule } from 'ng2-date-picker';
import { IstitutiSelectorModule } from '../shared/istituti-selector/istituti-selector.module';
import { SubNavbarModule } from '../sub-navbar/sub-navbar.module';
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
  imports:      [ 
    SharedModule,
    EnteRoutingModule,
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
  entryComponents: [],
declarations: [EnteRoutingModule.components]
})
export class EnteModule { }