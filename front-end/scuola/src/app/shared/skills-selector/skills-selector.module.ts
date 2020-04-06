import { NgModule }      from '@angular/core';
import { CommonModule } from '@angular/common';
import { SkillsSelectorComponent } from './skills-selector.component'
import { FormsModule } from '@angular/forms';
import { PaginationModule } from '../../shared/pagination/pagination.module';

@NgModule({
  imports: [ FormsModule, CommonModule, PaginationModule ],
  exports: [ SkillsSelectorComponent ],
  declarations: [ SkillsSelectorComponent ]
})
export class SkillsSelectorModule { }