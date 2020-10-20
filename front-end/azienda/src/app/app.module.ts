import { NgModule, APP_INITIALIZER }      from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AppComponent }  from './app.component';
import { AppRoutingModule } from './app-routing.module';

import { LoginModule } from './login/login.module';
import { CoreModule }   from './core/core.module';
import { SharedModule } from './shared/shared.module';

import { CompetenzaDetailModalComponent } from './oppurtunities/skills-selector/modals/competenza-detail-modal/competenza-detail-modal.component';
import { AppLoadService } from './app-load.service';
 
export function init_app(appLoadService: AppLoadService) {
    return () => appLoadService.initializeApp();
}
 

@NgModule({
  providers: [
    AppLoadService,
    { provide: APP_INITIALIZER, useFactory: init_app, deps: [AppLoadService], multi: true }
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    LoginModule,          //Eager loaded since we may need to go here right away as browser loads based on route user enters
    AppRoutingModule,     //Main routes for application
    CoreModule,           //Singleton objects (services, components that are loaded only once, etc.)
    SharedModule,          //Shared (multi-instance) objects
    NgbModule.forRoot(),
  ],
  declarations: [AppComponent, CompetenzaDetailModalComponent],
  entryComponents: [CompetenzaDetailModalComponent],
  exports: [ AppComponent ],
  bootstrap: [AppComponent]
  
})
export class AppModule { }