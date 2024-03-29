import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { StudentiComponent } from './studenti.component';
import { StudentiContainerComponent } from './studenti-container.component'
import { StudenteDettaglioComponent } from './actions/studente-dettaglio/studente-dettaglio.component'
const routes: Routes = [

  {
    path: '', component: StudentiContainerComponent,
    children: [
      { path: 'list', component: StudentiComponent },
      { path: 'list/detail/:id', component: StudenteDettaglioComponent },
      { path: '**', pathMatch:'full', redirectTo: 'list' }
    ]
  }

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class StudentiRoutingModule { 
  static components = [
    StudentiContainerComponent,
    StudentiComponent,
    StudenteDettaglioComponent
  ];
}
