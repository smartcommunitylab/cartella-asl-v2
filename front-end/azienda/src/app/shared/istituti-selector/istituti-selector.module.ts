import { NgModule }      from '@angular/core';
import { CommonModule } from '@angular/common';
import { IstitutiSelectorComponent } from './istituti-selector.component'
import { FormsModule } from '@angular/forms';
import { PaginationModule } from '../pagination/pagination.module';

@NgModule({
  imports: [ FormsModule, CommonModule, PaginationModule ],
  exports: [ IstitutiSelectorComponent ],
  declarations: [ IstitutiSelectorComponent ]
})
export class IstitutiSelectorModule { }