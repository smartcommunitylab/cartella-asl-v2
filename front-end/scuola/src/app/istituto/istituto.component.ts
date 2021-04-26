import { Component, OnInit } from '@angular/core';
import { DataService } from '../core/services/data.service'
import { ActivatedRoute, Router } from '@angular/router';
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

  // Istituto bar graph.
  public barChartIstitutoLabels: Label[] = [];
  public barChartIstitutoData: ChartDataSets[] = [];
  public barChartIstitutoType: ChartType = 'bar';
  public barChartIstitutoLegend = true;
  public barChartIstitutoPlugins = [];
  barChartIstitutoOptions: ChartOptions = {};

  constructor(
    private dataService: DataService,
    private route: ActivatedRoute,
    private router: Router,
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
        this.initGraphIsituto(this.sistemaDataset);
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

    this.pieChartLabels = [];
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
      this.pieChartData.push(Math.floor(Math.random() * 8) + 1);
      console.log(element.titolo);
    });
  }

  initGraphIsituto(report) {
    this.barChartIstitutoLabels = [];
    this.barChartIstitutoData = [];

    // this.sampleSistemaData();

    Object.keys(report.oreClassiMap).map(key => {
      var value = report.oreClassiMap[key];
      var data = {};
      data['data'] = value.oreSvolte;
      data['hoverBorderColor'] = '#00CF86';
      data['backgroundColor'] = '#00CF86';
      data['hoverBackgroundColor'] = '#00CF86';
      data['label'] = 'ore svolte';
      this.barChartIstitutoLabels.push(value.classe);
      this.barChartIstitutoData.push(data);
    });

    this.barChartIstitutoOptions = {
      responsive: true,
      tooltips: {
        enabled: true,
        mode: 'single',
        callbacks: {
          afterBody: function (t, d) {
            console.log(this.additionalDataSistema[t[0].index]);
            return '';  // return a string that you wish to append
          },
        }
      },
      scales: {
        yAxes: [{
          position: 'left',
          scaleLabel: {
              display: true,
              labelString: 'Ore',
          },
          ticks: {
            beginAtZero: true,
            stepSize: 40,
            suggestedMax: 440
        }
      }]
      }
    };
  }

  sampleSistemaData() {
    this.sistemaDataset = {};
    this.sistemaDataset.oreClassiMap = {};
    this.sistemaDataset.numeroOreTotali = 84;
    this.sistemaDataset.numeroAttivitaInAttesa = 0;
    this.sistemaDataset.numeroAttivitaInCorso = 2;
    this.sistemaDataset.numeroAttivitaInRevisione = 3;
    for (var i = 1; i <= 24; i++) {
      var data = {};
      data['data'] = Math.floor(Math.random() * 400) + 1;
      data['hoverBorderColor'] = '#00CF86';
      data['backgroundColor'] = '#00CF86';
      data['hoverBackgroundColor'] = '#00CF86';
      data['label'] = 'ore svolte';
      this.barChartIstitutoLabels.push(i + '° INFC');
      this.barChartIstitutoData.push(data);
      var entry = {};
      entry['oreSvolte'] = data['data'];
      entry['media'] = Math.floor(Math.random() * 50) + 1;
      entry['numStudenti'] = Math.floor(Math.random() * 5) + 1;
      entry['label'] = i + '° INFC';
      this.sistemaDataset.oreClassiMap[entry['label']] = entry;
    }
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