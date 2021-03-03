import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { HomeComponent } from './home.component';
import { HomeContainerComponent } from './home-container.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: '**', pathMatch: 'full', component: HomeComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class HomeRoutingModule {
  static components = [
    HomeContainerComponent,
    HomeComponent
  ];
}


