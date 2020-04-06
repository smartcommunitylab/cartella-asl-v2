import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './home.component'
import { LoginComponent } from './login/login.component';

const routes: Routes = [
  { path: 'login/:app', component: LoginComponent }, 
  { path: '**', pathMatch:'full', component: HomeComponent}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class HomeRoutingModule { }
