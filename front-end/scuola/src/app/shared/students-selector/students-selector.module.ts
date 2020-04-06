import { NgModule }      from '@angular/core';
import { CommonModule } from '@angular/common';
import { StudentsSelectorComponent } from './students-selector.component'
import { FormsModule } from '@angular/forms';
import { PaginationModule } from '../pagination/pagination.module';
import { ConfirmModalComponent } from './modals/confirm-modal/confirm-modal.component'

@NgModule({
  imports: [ FormsModule, CommonModule, PaginationModule ],
  exports: [ StudentsSelectorComponent ],
  entryComponents: [ ConfirmModalComponent ],
  declarations: [ StudentsSelectorComponent, ConfirmModalComponent ]
})
export class StudentsSelectorModule { }