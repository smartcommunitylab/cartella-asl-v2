import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { DataService } from './core/services/data.service';

@Component({
  selector: 'cm-app-component',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
  @ViewChild('topNavbar') private navbar;

  isSticky: boolean;
  hide: boolean = true;

  constructor(
    private router: Router,
    private dataService: DataService
  ) { }


  ngOnInit() {

    if (this.navbar) {
      this.navbar.isStickyListener.subscribe((isSticky) => {
        this.isSticky = isSticky;
      });
    }
    
    this.dataService.getProfile().subscribe(profile => {
      if (!profile.authorized) {
        this.router.navigate(['/terms', profile.authorized]);
      } else {
        this.hide = false;
      }
    });


  }

}
