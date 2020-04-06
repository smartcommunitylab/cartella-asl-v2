import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgbModule} from '@ng-bootstrap/ng-bootstrap';
import { SharedModule }   from '../shared/shared.module';

import { AttivitaComponent } from './attivita.component';
import { AttivitaRoutingModule } from './attivita-routing.module';
import { AttivitaGuideComponent } from './attivita-guide/attivita-guide.component';
import { AttivitaListComponent } from './attivita-list/attivita-list.component';
import { AttivitaDetailComponent } from './attivita-detail/attivita-detail.component';
import { SubNavbarModule } from '../sub-navbar/sub-navbar.module';
import { AttivitaPresenzeComponent } from './attivita-presenze/attivita-presenze.component';
import { CalendarModule } from 'angular-calendar';

@NgModule({
  imports: [
    SharedModule,
    CommonModule,
    NgbModule.forRoot(),
    AttivitaRoutingModule,
    SubNavbarModule,
    CalendarModule.forRoot()
  ],
  declarations: [
    AttivitaComponent,
    AttivitaGuideComponent,
    AttivitaListComponent,
    AttivitaDetailComponent,
    AttivitaPresenzeComponent
  ],
  entryComponents: []
})
export class AttivitaModule { }
