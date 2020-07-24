import { Component, OnInit, ViewChild, ElementRef} from '@angular/core';
import { DataService } from '../../core/services/data.service';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { PermissionService } from '../../core/services/permission.service';
import { ngCopy } from 'angular-6-clipboard';

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
  tipologieMap = {
    1: {color: 'rgba(255, 0, 0, 0.9)', label: 'Testimonianza'},
    2: {color: 'rgba(255, 128, 0, 0.9)', label: 'Formazione'},
    3: {color: 'rgba(255, 255, 0, 0.9)', label: 'Elaborazione Esperienza / Project work'},
    4: {color: 'rgba(64, 255, 0, 0.9)', label: 'Anno all\'estero'},
    5: {color: 'rgba(0, 255, 191, 0.9)', label: 'Impresa formativa simulata / Cooperativa formativa scolastica'},
    6: {color: 'rgba(0, 191, 255, 0.9)', label: 'Visita aziendale'},
    7: {color: 'rgba(0, 64, 255, 0.9)', label: 'Tirocinio Curriculare'},
    8: {color: 'rgba(128, 0, 255, 0.9)', label: 'Commessa esterna'},
    9: {color: 'rgba(255, 0, 255, 0.9)', label: 'Attività sportiva'},
    10: {color: 'rgba(128, 64, 64, 0.9)', label: 'Tirocinio estivo retribuito'},
    11: {color: 'rgba(0, 77, 0, 0.9)', label: 'Lavoro retribuito'},
    12: {color: 'rgba(64, 128, 128, 0.9)', label: 'Volontariato'}
  }
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

  getTipologia(id) {
    return this.tipologieMap[id]['label'];
  }

  copyText(text) {
    ngCopy(text);
  }

  getEsperienzeCsv() {
    this.dataService.getEsperienzeCsv(this.istitutoId, this.annoScolastico, this.text).subscribe((doc) => {
      const downloadLink = document.createElement("a");
      downloadLink.href = doc.url;
      downloadLink.download = doc.filename;
      document.body.appendChild(downloadLink);
      downloadLink.click();
      document.body.removeChild(downloadLink);  
    });
  }
}
