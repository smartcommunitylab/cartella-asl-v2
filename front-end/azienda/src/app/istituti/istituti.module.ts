import { NgModule } from '@angular/core';
import { NgbModule, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SharedModule } from '../shared/shared.module';
import { IstitutiRoutingModule } from './istituti-routing.module';
import { PaginationModule } from '../shared/pagination/pagination.module';
import { DpDatePickerModule } from 'ng2-date-picker';
import { DesignAngularKitModule } from 'design-angular-kit'
import { AppModule } from '../app.module';

@NgModule({
  imports: [
    IstitutiRoutingModule,
    SharedModule,
    NgbModule.forRoot(),
    DpDatePickerModule,
    DesignAngularKitModule,
    PaginationModule
  ],
  declarations: [
    IstitutiRoutingModule.components,
  ],
  entryComponents: [
  ],
  providers: [
    NgbActiveModal,
    AppModule
  ]
})
export class IstitutiModule { }