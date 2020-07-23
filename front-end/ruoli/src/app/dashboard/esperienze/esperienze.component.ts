import { Component, OnInit, ViewChild, ElementRef} from '@angular/core';
import { DataService } from '../../core/services/data.service';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { PermissionService } from '../../core/services/permission.service';

@Component({
  selector: 'cm-dashboard-esperienze',
  templateUrl: './esperienze.component.html',
  styleUrls: ['./esperienze.component.css']
})

export class DashboardEsperienzeComponent implements OnInit {

  constructor(
    private dataService: DataService,
    private route: ActivatedRoute,
    private router: Router,
    private modalService: NgbModal,
    private permissionService: PermissionService) { }

  profile;
  report = {};
  istitutoId = '';
  annoScolastico = '2019-20';
  text = '';
  esperienze = [];

  title: string = "Lista esperienze";

    ngOnInit() {
      this.dataService.getProfile().subscribe(profile => {
        console.log(profile)
        if (profile) {
            this.profile = profile;          
        }
      }, err => {
        console.log('error, no institute')
      });
    }

  getIstituti() {
    if(this.profile) {
      if(this.profile.istituti) {
        return Object.values(this.profile.istituti);
      }
    }
    return [];
  }

  getReport() {
    this.dataService.getReportEsperienze(this.istitutoId, this.annoScolastico, this.text)
      .subscribe(r => {
        if(r) {
          this.esperienze = r;
        }
      });
  }

}
