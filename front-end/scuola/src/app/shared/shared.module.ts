import { NgModule, LOCALE_ID } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { MapModule } from './map/map.module';
import { PaginationModule } from './pagination/pagination.module';

import { CapitalizePipe } from './pipes/capitalize.pipe';
import { CalendarDatePipe } from './pipes/calendar-date-pipe';
import { LocalizedDatePipe } from './pipes/localizedDatePipe';
import { TrimPipe } from './pipes/trim.pipe';
import { SortByDirective } from './directives/sortby.directive';

import { registerLocaleData } from '@angular/common';

import { StudentsSelectorModule } from './students-selector/students-selector.module';
import { OfferteSelectorModule } from './offerte-selector/offerte-selector.module';

import localeIt from '@angular/common/locales/it';
import localeItExtra from '@angular/common/locales/extra/it';

registerLocaleData(localeIt, 'it-IT', localeItExtra);
import { SubNavbarModule } from '../sub-navbar/sub-navbar.module';

@NgModule({
  imports: [CommonModule, MapModule, PaginationModule ,SubNavbarModule, StudentsSelectorModule, OfferteSelectorModule],
  exports: [ CommonModule, FormsModule, CapitalizePipe, TrimPipe, LocalizedDatePipe, CalendarDatePipe, SortByDirective, 
             MapModule, PaginationModule, SubNavbarModule, StudentsSelectorModule, OfferteSelectorModule],
  declarations: [ CapitalizePipe, TrimPipe, LocalizedDatePipe, CalendarDatePipe, SortByDirective],
  providers: [
    { provide: LOCALE_ID, useValue: "it-IT" }, //replace "en-US" with your locale
  ]
})
export class SharedModule { }
