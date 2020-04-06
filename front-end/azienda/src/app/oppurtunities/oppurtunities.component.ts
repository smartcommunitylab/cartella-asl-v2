import { Component, OnInit, ViewChild } from '@angular/core';
import { LocalStorage } from '@ngx-pwa/local-storage'

@Component({
  selector: 'oppurtunities',
  templateUrl: './oppurtunities.component.html',
  styleUrls: ['./oppurtunities.component.scss'],
})
export class OppurtunitiesComponent {
 
  constructor(private localStorage: LocalStorage) {
    this.localStorage.clear().subscribe(() => { });
 }

}