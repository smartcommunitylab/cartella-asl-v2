import { NgModule }      from '@angular/core';
import { CommonModule } from '@angular/common';
import { OfferteSelectorComponent } from './offerte-selector.component'
import { FormsModule } from '@angular/forms';
import { PaginationModule } from '../../shared/pagination/pagination.module';
import { DesignAngularKitModule } from 'design-angular-kit';
import { NgbModule,NgbActiveModal } from '@ng-bootstrap/ng-bootstrap'; 

@NgModule({
  imports: [FormsModule, CommonModule, PaginationModule, DesignAngularKitModule, NgbModule.forRoot()],
  providers: [
    NgbActiveModal,
  ],
  exports: [ OfferteSelectorComponent ],
  declarations: [ OfferteSelectorComponent ]
})
export class OfferteSelectorModule { }