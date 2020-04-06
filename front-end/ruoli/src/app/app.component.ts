import { Component, OnInit, ViewChild } from '@angular/core';

@Component({ 
  selector: 'cm-app-component',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
  @ViewChild('topNavbar') private navbar;

  isSticky:boolean;

  ngOnInit() { 
    this.navbar.isStickyListener.subscribe((isSticky) => {
      this.isSticky = isSticky;
    });
  }

}
