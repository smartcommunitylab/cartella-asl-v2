import { NgModule, Optional, SkipSelf } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { GrowlerModule } from './growler/growler.module';
import { ModalModule } from './modal/modal.module';
import { OverlayModule } from './overlay/overlay.module';

import { DataService } from './services/data.service';
import { NavbarComponent } from './navbar/navbar.component';
import { FilterService } from './services/filter.service';
import { SorterService } from './services/sorter.service';
import { TrackByService } from './services/trackby.service';
import { DialogService } from './services/dialog.service';
import { EnsureModuleLoadedOnceGuard } from './ensureModuleLoadedOnceGuard';
import { EventBusService } from './services/event-bus.service';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { GeoService } from './services/geo.service';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule } from '@angular/forms';
import { FooterComponent } from './footer/footer.component';
import { ReloginModal } from './overlay/modal/login/relogin-modal.component';

@NgModule({
  imports: [ CommonModule, RouterModule, HttpClientModule, GrowlerModule, ModalModule, OverlayModule,FormsModule, NgbModule.forRoot() ],
  exports: [ GrowlerModule, RouterModule, HttpClientModule, ModalModule, OverlayModule, NavbarComponent, FooterComponent ],
  declarations: [NavbarComponent, FooterComponent, ReloginModal],
  entryComponents: [
    ReloginModal
  ],
  providers: [
    SorterService, FilterService, DataService, TrackByService, 
               DialogService, EventBusService, GeoService,
               {
                 provide: HTTP_INTERCEPTORS,
                 useClass: AuthInterceptor,
                 multi: true,
               } 
              ] // these should be singleton
})
export class CoreModule extends EnsureModuleLoadedOnceGuard {    //Ensure that CoreModule is only loaded into AppModule

  //Looks for the module in the parent injector to see if it's already been loaded (only want it loaded once)
  constructor( @Optional() @SkipSelf() parentModule: CoreModule) {
    super(parentModule);
  }  

}



