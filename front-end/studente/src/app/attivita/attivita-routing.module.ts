import { NgModule } from '@angular/core';
import { RouterModule, Routes} from '@angular/router';
import { AttivitaComponent } from './attivita.component';
import { AttivitaGuideComponent } from './attivita-guide/attivita-guide.component'
import { AttivitaDetailComponent } from './attivita-detail/attivita-detail.component';
import { AttivitaListComponent } from './attivita-list/attivita-list.component';

const app_routes: Routes = [
  { path: '', component: AttivitaComponent,
    children: [
      { path: '', component: AttivitaListComponent },
      { path: 'detail/:id', component: AttivitaDetailComponent },
      { path: 'guide', component: AttivitaGuideComponent },
      { path: '**', pathMatch:'full', redirectTo: '/attivita/' }
    ]
  }
  
];

@NgModule({
  imports: [ RouterModule.forChild(app_routes) ],
  exports: [ RouterModule ]
})
export class AttivitaRoutingModule { }
