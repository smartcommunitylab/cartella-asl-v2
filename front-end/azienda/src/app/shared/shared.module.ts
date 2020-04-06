import { NgModule } from '@angular/core';
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
import { SubNavbarModule } from './sub-navbar/sub-navbar.module';

@NgModule({
  imports: [CommonModule, MapModule, FilterTextboxModule, PaginationModule, SubNavbarModule ],
  exports: [ CommonModule, FormsModule, CapitalizePipe, TrimPipe, LocalizedDatePipe, CalendarDatePipe, SortByDirective,
             MapModule, FilterTextboxModule, PaginationModule, SubNavbarModule ],
  declarations: [ CapitalizePipe, TrimPipe, LocalizedDatePipe, CalendarDatePipe, SortByDirective ]
})
export class SharedModule { }
