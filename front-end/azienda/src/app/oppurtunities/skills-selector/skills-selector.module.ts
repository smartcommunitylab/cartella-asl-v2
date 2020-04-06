import { NgModule } from '@angular/core';
import { NgbModule, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SkillSetRoutingModule } from './skills-selector-routing.module';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { SharedModule }   from '../../shared/shared.module';

@NgModule({
  imports: [SkillSetRoutingModule, NgbModule.forRoot(), FormsModule, CommonModule, SharedModule],
  declarations: [],
  providers: [
    NgbActiveModal,
  ]
})


export class SkillSetModule { }