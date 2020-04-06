import { CalendarDateFormatter, DateFormatterParams } from 'angular-calendar';
import { DatePipe } from '@angular/common';

export class CustomDateFormatter extends CalendarDateFormatter {

   public monthViewColumnHeader({date, locale}: DateFormatterParams): string {
     return new DatePipe(locale).transform(date, 'EEE', locale); // use short week days
   }

}