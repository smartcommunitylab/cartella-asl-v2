import { Component, OnInit, ViewChild } from '@angular/core';

import { DataService } from '../../core/services/data.service';
import { EsperienzaSvolta, IPagedResults, IPagedES } from '../../shared/interfaces';
import { TrackByService } from '../../core/services/trackby.service';
import { startOfDay, addDays, endOfISOYear, startOfMonth, endOfMonth } from 'date-fns';
import { NgbTabChangeEvent, NgbTabset } from '@ng-bootstrap/ng-bootstrap';
import { LocalStorage } from '@ngx-pwa/local-storage'


@Component({
    selector: 'prossima-tabs-component',
    templateUrl: './prossima-tabs.component.html',
    styleUrls: ['./prossima-tabs-component.css']
})
export class ProssimaTabsComponent implements OnInit {

    @ViewChild('subTabsProssima') subTabsProssima: NgbTabset;

    constructor(private localStorage: LocalStorage) {}

    public beforeChange($event: NgbTabChangeEvent) {
        this.localStorage.setItem('L2Tab', $event.nextId).subscribe(() => { });
    };

    ngOnInit() {
        this.localStorage.getItem<any>('L2Tab').subscribe((selectedTab) => {
            if (selectedTab) {
                this.subTabsProssima.activeId = selectedTab;
            }
        });
    }

}
