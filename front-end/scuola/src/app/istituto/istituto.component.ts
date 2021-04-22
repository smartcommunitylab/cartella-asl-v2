import { Component, OnInit } from '@angular/core';
import { DataService } from '../core/services/data.service'
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Location } from '@angular/common';
import { environment } from '../../environments/environment';
import { ChartDataSets, ChartOptions, ChartType } from 'chart.js';
import { Label } from 'ng2-charts';

@Component({
  selector: 'istituto',
  templateUrl: './istituto.component.html',
  styleUrls: ['./istituto.component.scss']
})

export class IstitutoComponent implements OnInit {
  filtro;
  filterSearch = false;
  closeResult: string;
  menuContent = "In questa sezione trovi una dashboard che mostra un riassunto visivo delle attività di tutti gli studenti. Inoltre, da qui puoi gestire gli account abilitati sul Portale EDIT Istituto di questo istituto e i dati principali dell’istituto.";
  showContent: boolean = false;
  timeoutTooltip = 250;
  env = environment;
  istituto;
  showDashboard: boolean = false;
  tipologie;
  classe;
  classi;
  sistemaDataset;
  numeroAttivitaInAttesa;
  numeroAttivitaInCorso;
  numeroAttivitaInRevisione;
  oreTotali;
 
  // PIE chart.
  pieChartOptions: ChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    legend: {
      position: 'left',
    },
    tooltips: {
      enabled: true,
      mode: 'single',
      callbacks: {
        label: function (tooltipItems, data) {
          var multistringText = [];
          multistringText.push(data.labels[tooltipItems.index]);

          return multistringText;
        },
        footer: function (tooltipItems, data) {
          var multistringText = [];
          // calculate ore totali.
          var oreTotali = 0;
          Object.keys(data.datasets[0].data).map(key => {
            oreTotali = oreTotali + data.datasets[0].data[key];
          });
          multistringText.push(data.datasets[0].data[tooltipItems[0].index] + ' ore');
          multistringText.push(Math.round(Number(data.datasets[0].data[tooltipItems[0].index]) / oreTotali * 100) + ' % del totali')

          return multistringText;
        }
      }
    },
  };
  pieChartLabels: Label[] = [];
  pieChartData: number[] = [];
  pieChartType: ChartType = 'pie';
  pieChartLegend = true;
  pieChartPlugins = [];
  pieChartColors = [
    {
      backgroundColor: ['#FF9700', '#00CF86', '#0066CC', '#F83E5A', '#7FB2E5', '#5A6772', '#E5E5E5', '#5A41F3', '#05FF00', '#FF6AA0', '#00FFFF', '#AD00FF', '#04111C'],
    },
  ];

  constructor(
    private dataService: DataService,
    private route: ActivatedRoute,
    private router: Router,
    private location: Location,
    private modalService: NgbModal
  ) {

  }

  ngOnInit(): void {
    this.getIstituto();
    // GET classes.
    this.dataService.getAttivitaTipologie().subscribe((res) => {
      this.tipologie = res;
      this.dataService.getDashboardIstitutoClasse()
        .subscribe((response) => {
          this.classi = response;
          // default selection.
          this.classe = this.classi[0];
          this.initIstitutoDashboard();
        },
          (err: any) => console.log(err),
          () => console.log('get dashboard istituto classi api'));
    },
      (err: any) => console.log(err),
      () => console.log('getAttivitaTipologie'));

  }

  initIstitutoDashboard() {
    this.showDashboard = false;
    // GET sistema report.
    this.dataService.getDashboardIstitutoSistemaReport()
      .subscribe((response) => {
        this.sistemaDataset = response;
        this.initPieChart(this.sistemaDataset);
        this.showDashboard = true;
      },
        (err: any) => console.log(err),
        () => console.log('get dashboard istituto report api'));
  }

  initPieChart(report) {
    this.numeroAttivitaInAttesa = report.numeroAttivitaInAttesa;
    this.numeroAttivitaInCorso = report.numeroAttivitaInCorso; 
    this.numeroAttivitaInRevisione = report.numeroAttivitaInRevisione;
    this.oreTotali = report.numeroOreTotali;

    this.pieChartLabels =[];
    this.pieChartData = [];
 
    // this.samplePieChartData();
    
    Object.keys(report.oreTipologiaMap).map(key => {
      this.tipologie.forEach(tipo => {
        if (tipo.id == Number(key)) {
          this.pieChartLabels.push(tipo.titolo);
          this.pieChartData.push(report.oreTipologiaMap[key]);
        }
      })
    });
   
  }

  samplePieChartData() {
    this.tipologie.forEach(element => {
      this.pieChartLabels.push(element.titolo);
      this.pieChartData.push(Math.floor(Math.random() * 8) + 1  );
      console.log(element.titolo);
    });
  }

  getIstituto() {
    this.dataService.getIstitutoById(this.dataService.istitutoId)
      .subscribe((response) => {
        this.istituto = response;
      },
        (err: any) => console.log(err),
        () => console.log('get istituto api'));
  }

  menuContentShow() {
    this.showContent = !this.showContent;
  }

  modifica() {
    this.router.navigate(['../modifica', this.dataService.istitutoId], { relativeTo: this.route });
  }

}