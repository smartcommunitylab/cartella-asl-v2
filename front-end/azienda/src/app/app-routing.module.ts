import { NgModule } from '@angular/core';
import { RouterModule, Routes} from '@angular/router';
import { HashLocationStrategy, LocationStrategy } from '@angular/common';
import { LocalStorageModule } from '@ngx-pwa/local-storage';

const app_routes: Routes = [
  { path: 'home', loadChildren: 'app/home/home.module#HomeModule' },
  { path: 'attivita', loadChildren: 'app/activities/activities-main.module#ActivitiesMainModule' },
  { path: 'oppurtunita', loadChildren: 'app/oppurtunities/oppurtunities.module#OppurtunitiesModule' },
  { path: 'terms/:authorized', loadChildren: 'app/terms/terms.module#TermsModule'},
  { path: '**', pathMatch:'full', redirectTo: '/home' } //catch any unfound routes and redirect to home page
];

@NgModule({
  imports: [ RouterModule.forRoot(app_routes, { useHash: true }), LocalStorageModule ],
  exports: [RouterModule],  
})
export class AppRoutingModule { }
