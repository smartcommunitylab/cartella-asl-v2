import { NgModule }      from '@angular/core';
import { CommonModule } from '@angular/common';
import { BrowserModule } from '@angular/platform-browser/src/browser';
import { FormsModule } from '@angular/forms';
import { SubNavbarComponent } from './sub-navbar.component'
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [ FormsModule, RouterModule, CommonModule ],
  exports: [ SubNavbarComponent ],
  declarations: [ SubNavbarComponent ]
})
export class SubNavbarModule { }