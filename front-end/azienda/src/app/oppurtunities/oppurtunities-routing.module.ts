import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { OppurtunitiesComponent } from './oppurtunities.component';
import { OppurtunitiesGridComponent } from './list/oppurtunities-grid.component';
import { OppurtunityDetailsComponent } from './details/oppurtunity-details.component';
import { OppurtunityEditComponent } from './edit/oppurtunity-edit.component';
import { HistoryComponent } from './storica/offer-history.component';
import { OpportunitaGuideComponent } from './guida/pianifica-guide.component';
import { SkillsSelectorComponent } from './skills-selector/skills-selector.component';
import { AttivitaCancellaModal } from './modal/cancella/attivita-cancella-modal.component';
import { SubNavbarModule } from '../shared/sub-navbar/sub-navbar.module';

const routes: Routes = [
  {
    path: '', component: OppurtunitiesComponent,
    children: [
      { path: 'list', component: OppurtunitiesGridComponent },
      { path: 'archivo', component: HistoryComponent },
      { path: 'list/details/:id', component: OppurtunityDetailsComponent },
      { path: 'list/details/:id/skills', component: SkillsSelectorComponent },
      { path: 'list/edit/:id', component: OppurtunityEditComponent },
      { path: 'guida', component: OpportunitaGuideComponent },
    ],
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class OppurtunitiesRoutingModule {
  static components = [OppurtunitiesComponent, OppurtunitiesGridComponent, HistoryComponent, OppurtunityDetailsComponent, OppurtunityEditComponent,
    OpportunitaGuideComponent, SkillsSelectorComponent, AttivitaCancellaModal];
}




