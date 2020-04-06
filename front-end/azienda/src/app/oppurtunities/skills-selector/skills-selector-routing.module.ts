import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { SkillsSelectorComponent } from './skills-selector.component';

const routes: Routes = [
  // { path: '', component: SkillsSelectorComponent }
];

@NgModule({
  imports: [ RouterModule.forChild(routes) ],
  exports: [ RouterModule ]
})
export class SkillSetRoutingModule { 
  // static components = [ SkillsSelectorComponent ];
}