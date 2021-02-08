import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SharedModule } from '../shared/shared.module';
import { IstitutoRoutingModule } from './istituto-routing.module';
import { NgbModule, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DesignAngularKitModule } from 'design-angular-kit'
import { SubNavbarModule } from '../sub-navbar/sub-navbar.module';

@NgModule({
  imports: [
    SharedModule,
    IstitutoRoutingModule,
    NgbModule.forRoot(),
    CommonModule,
    FormsModule,
    DesignAngularKitModule,
    SubNavbarModule],
  providers: [
    NgbActiveModal,
  ],
  entryComponents: [],
  declarations: [IstitutoRoutingModule.components]
})
export class IstitutoModule { }