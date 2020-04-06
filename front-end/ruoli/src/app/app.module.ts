import { NgModule, APP_INITIALIZER } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { CoreModule } from './core/core.module';
import { SharedModule } from './shared/shared.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AppLoadService } from './app-load.service';
import { AuthenticationService } from './core/services/authentication.service';
import { AuthGuard } from './core/services/auth.guard';
import { AuthInterceptor } from './core/interceptors/auth.interceptor';

export function init_app(appLoadService: AppLoadService) {
  return () => appLoadService.initializeApp();
}

@NgModule({
  providers: [
    AppLoadService,
    { provide: APP_INITIALIZER, useFactory: init_app, deps: [AppLoadService], multi: true },
    AuthenticationService,
    AuthGuard,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,     //Main routes for application
    CoreModule,           //Singleton objects (services, components that are loaded only once, etc.)
    SharedModule,          //Shared (multi-instance) objects
    NgbModule.forRoot()
  ],
  declarations: [AppComponent],
  exports: [AppComponent],
  entryComponents: [

  ],
  bootstrap: [AppComponent]

})
export class AppModule { }