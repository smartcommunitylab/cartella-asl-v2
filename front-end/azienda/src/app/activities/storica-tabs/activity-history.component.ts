import { Component, OnInit, ViewChild } from '@angular/core';

import { ActivatedRoute, Params } from '@angular/router';

import { IPagedResults, EsperienzaSvolta, TipologiaAzienda, Stato, IPagedES } from '../../shared/interfaces';
import { DataService } from '../../core/services/data.service';
import { TrackByService } from '../../core/services/trackby.service';
import { startOfDay, startOfISOYear, addISOYears, startOfMonth } from 'date-fns';

import * as moment from 'moment';
import { Moment } from 'moment';

import { NgbTabChangeEvent, NgbTabset } from '@ng-bootstrap/ng-bootstrap';
import { LocalStorage } from '@ngx-pwa/local-storage'


@Component({
  selector: 'activity-history',
  templateUrl: './activity-history.component.html',
  styleUrls: ['./activity-history.component.scss']
})
export class HistoryComponent implements OnInit {

  @ViewChild('subTabsStorica') subTabsStorica: NgbTabset;

  constructor(private localStorage: LocalStorage) {}

  public beforeChange($event: NgbTabChangeEvent) {
    this.localStorage.setItem('historyTab', $event.nextId).subscribe(() => { });
  };

  ngOnInit() {
    this.localStorage.getItem<any>('historyTab').subscribe((selectedTab) => {
      if (selectedTab) {
        this.subTabsStorica.activeId = selectedTab;
      }
    });
  }

}