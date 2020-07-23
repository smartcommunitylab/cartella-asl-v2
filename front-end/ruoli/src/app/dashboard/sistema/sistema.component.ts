import { Component, OnInit, ViewChild, ElementRef} from '@angular/core';
import { DataService } from '../../core/services/data.service';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { PermissionService } from '../../core/services/permission.service';
import { Chart } from 'chart.js';

@Component({
  selector: 'cm-dashboard-sistema',
  templateUrl: './sistema.component.html',
  styleUrls: ['./sistema.component.css']
})

export class DashboardSistemaComponent implements OnInit {

  constructor(
    private dataService: DataService,
    private route: ActivatedRoute,
    private router: Router,
    private modalService: NgbModal,
    private permissionService: PermissionService) { }

  profile;
  report = {};
  istitutoId = "";
  annoScolastico = '2019-20';
  doughnut;
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

  title: string = "Statistiche generali";

  @ViewChild('doughnutChartCanvas') chartCanvas: ElementRef<HTMLCanvasElement>;
  
    ngOnInit() {
      //var ctx = this.chartCanvas.nativeElement.getContext('2d');

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
    this.dataService.getUtilizzoSistema(this.istitutoId, this.annoScolastico)
      .subscribe(r => {
        if(r) {
          this.report = r;
          var datasets = [];
          var values = [];
          var labels = [];
          var backgroundColors = [];
          for (var i = 1; i < 13; i++) {
            if(r.tipologiaMap[i]) {
              values.push(r.tipologiaMap[i]);
              backgroundColors.push(this.tipologieMap[i].color);  
              labels.push(this.tipologieMap[i].label);
            }
          }
          datasets.push({
            data: values,
            backgroundColor: backgroundColors
          });
          var chartConfig = {
            type: 'doughnut',
            data: {
              datasets: datasets,
              labels: labels
            },
            options: {
              circumference: Math.PI,
              rotation: -Math.PI
            }
          };
          this.doughnut = new Chart('doughnutChartCanvas', chartConfig);
          //this.doughnut.update();
        }
      });
  }

}
