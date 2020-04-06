import { NgModule, Pipe, PipeTransform  } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TermsComponentRoutingModule } from './terms-routing.module';
import { TermsComponent } from './terms.component';
import { DomSanitizer } from '@angular/platform-browser'

@Pipe({ name: 'safeHtml'})
export class SafeHtmlPipe implements PipeTransform  {
  constructor(private sanitized: DomSanitizer) {}
  transform(value) {
    return this.sanitized.bypassSecurityTrustHtml(value);
  }
}

@NgModule({
  imports: [
    CommonModule,
    TermsComponentRoutingModule
  ],
  declarations: [TermsComponent, SafeHtmlPipe]
})
export class TermsModule { }
