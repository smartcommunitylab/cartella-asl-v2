import { Component, OnInit, ViewChild } from '@angular/core';

import { DataService } from '../../core/services/data.service';
import { EsperienzaSvolta, IPagedResults, IPagedES } from '../../shared/interfaces';
import { TrackByService } from '../../core/services/trackby.service';
import { startOfDay, addDays, endOfISOYear, startOfMonth, endOfMonth } from 'date-fns';
import { LocalStorage } from '@ngx-pwa/local-storage'
import { NgbTabChangeEvent, NgbTabset } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
    selector: 'activities-component',
    templateUrl: './activities.component.html',
    styleUrls: ['./activities-component.css']
})
export class ActivitiesComponent implements OnInit {
    //@ViewChild('L1Tabs') L1Tabs: NgbTabset;

    constructor(private localStorage: LocalStorage,
        private route: ActivatedRoute,
        private router: Router) { }
/*
    public beforeChange($event: NgbTabChangeEvent) {
        // this.localStorage.clear().subscribe(() => { //trick to forgot selections
            this.localStorage.setItem('L1Tab', $event.nextId).subscribe(() => {
            });
        // });
    };*/

    showIndividuale;

    ngOnInit() {
        this.showIndividuale = this.route.snapshot.url[2].path.indexOf('individuali') >= 0; 

        /*this.localStorage.getItem<any>('L1Tab').subscribe((selectedTab) => {
            if (selectedTab) {
                this.L1Tabs.activeId = selectedTab;
            }
        });*/


    }
}
