import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FormsModule } from '@angular/forms';
import { SharedModule }   from '../shared/shared.module';
import { NgbModule,NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { from } from 'rxjs/observable/from';
import { DesignAngularKitModule } from 'design-angular-kit' 
import { DpDatePickerModule } from 'ng2-date-picker';
import { SkillsSelectorModule } from '../shared/skills-selector/skills-selector.module';
import { SubNavbarModule } from '../sub-navbar/sub-navbar.module';
import { ReactiveFormsModule } from '@angular/forms';
import { StudentiRoutingModule } from './studenti-routing.module';
import { NgCircleProgressModule } from 'ng-circle-progress';

@NgModule({
  imports:      [ 
    SharedModule,
    StudentiRoutingModule,
    NgbModule.forRoot(),
    DpDatePickerModule,
    CommonModule,
    FormsModule,
    DesignAngularKitModule,
    SkillsSelectorModule,
    SubNavbarModule,
    ReactiveFormsModule,
    NgCircleProgressModule.forRoot({})
  ],
  providers: [
    NgbActiveModal,
  ],
  entryComponents: [],
  declarations: [StudentiRoutingModule.components]
})
export class StudentiModule { }
