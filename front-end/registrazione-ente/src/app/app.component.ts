import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { DataService } from './core/services/data.service';
import { environment } from '../environments/environment';

@Component({
  selector: 'cm-app-component',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
  @ViewChild('topNavbar') private navbar;

  isSticky: boolean;
  hide: boolean = true;
  env = environment;
  constructor(private router: Router, private dataService: DataService) { }

  ngOnInit() {}

}
