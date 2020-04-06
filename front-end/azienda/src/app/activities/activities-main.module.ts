import { NgModule } from '@angular/core';

import { SharedModule } from '../shared/shared.module';

import { DpDatePickerModule } from 'ng2-date-picker';
import { CalendarModule } from 'angular-calendar';
import { NgbModalModule, NgbDatepickerModule, NgbTimepickerModule, NgbTabsetModule, NgbRatingModule } from '@ng-bootstrap/ng-bootstrap';
// import { DateTimePickerComponent } from './date-time-picker.component';
// import { CalendarHeaderComponent } from './calendar-header.component';
import { Ng2DatetimePickerModule } from 'ng2-datetime-picker';
import { CustomDateFormatter } from '../shared/pipes/calendar-date-formatter';
import { CalendarDateFormatter, DateFormatterParams } from 'angular-calendar';

import { ActivitiesMainRoutingModule } from './activities-main.routing.module';
import { ActivityEvalutationEditComponent } from './valutazione/activity-evaluation-edit.component';
import { AttivitaCancellaModal } from './modal/cancella/attivita-cancella-modal.component';
import { FileUploadModule } from 'ng2-file-upload';



@NgModule({
  imports: [
    SharedModule,
    ActivitiesMainRoutingModule,
    NgbTabsetModule.forRoot(),
    DpDatePickerModule,
    Ng2DatetimePickerModule,
    NgbModalModule.forRoot(),
    NgbDatepickerModule.forRoot(),
    NgbTabsetModule.forRoot(),
    NgbTimepickerModule.forRoot(),
    CalendarModule.forRoot(),
    FileUploadModule,
    NgbRatingModule.forRoot()],
  declarations: [ActivitiesMainRoutingModule.components],
  entryComponents: [ActivityEvalutationEditComponent, AttivitaCancellaModal]
})
export class ActivitiesMainModule { }