import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { IstitutiComponent } from './istituti.component';
import { IstitutiContainerComponent } from './istituti-container.component';

const routes: Routes = [
  {
    path: '', component: IstitutiContainerComponent,
    children: [
      { path: 'list', component: IstitutiComponent },
      { path: '**', pathMatch: 'full', redirectTo: 'list' }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class IstitutiRoutingModule {
  static components = [
    IstitutiContainerComponent,
    IstitutiComponent,
  ];
}




