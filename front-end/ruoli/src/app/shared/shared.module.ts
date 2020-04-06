import { NgModule, LOCALE_ID } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { FilterTextboxModule } from './filter-textbox/filter-textbox.module';
import { MapModule } from './map/map.module';
import { PaginationModule } from './pagination/pagination.module';

import { CapitalizePipe } from './pipes/capitalize.pipe';
import { CalendarDatePipe } from './pipes/calendar-date-pipe';
import { LocalizedDatePipe } from './pipes/localizedDatePipe';
import { TrimPipe } from './pipes/trim.pipe';
import { SortByDirective } from './directives/sortby.directive';

import { registerLocaleData } from '@angular/common';
import localeIt from '@angular/common/locales/it';
import localeItExtra from '@angular/common/locales/extra/it';

registerLocaleData(localeIt, 'it-IT');

@NgModule({
  imports: [CommonModule, MapModule, FilterTextboxModule, PaginationModule ],
  exports: [ CommonModule, FormsModule, CapitalizePipe, TrimPipe, LocalizedDatePipe, CalendarDatePipe, SortByDirective,
             MapModule, FilterTextboxModule, PaginationModule ],
  declarations: [ CapitalizePipe, TrimPipe, LocalizedDatePipe, CalendarDatePipe, SortByDirective ],
  providers: [
    { provide: LOCALE_ID, useValue: "it-IT" }, //replace "en-US" with your locale
  ]
})
export class SharedModule { }
