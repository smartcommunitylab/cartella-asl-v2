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
  classeDataset;
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
          multistringText.push(Math.round(Number(data.datasets[0].data[tooltipItems[0].index])/oreTotali * 100) + ' % del totali')

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

  // Classe stack bar graph.
  public barChartClasseOptions: ChartOptions = {
    responsive: true,
  };
  public barChartClasseLabels: Label[] =[];
  public barChartClasseType: ChartType = 'bar';
  public barChartClasseLegend = true;
  public barChartClassePlugins = [];
  public barChartClasseData: ChartDataSets[] = [];

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
  ) { }

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
          this.initClasseDashboard();
          this.initIstitutoDashboard();
        },
          (err: any) => console.log(err),
          () => console.log('get dashboard istituto classi api'));
    },
      (err: any) => console.log(err),
      () => console.log('getAttivitaTipologie'));
  }

  initClasseDashboard() {
    // GET classes report.
    this.dataService.getDashboardIstitutoClasseReport(this.classe)
      .subscribe((response) => {
        this.classeDataset = response;
        if (Object.keys(this.classeDataset.oreTipologiaMap).length === 0) {
          this.showDashboard = false;
        } else {
          this.initPieChart(this.classeDataset);
          this.initGraphClasse(this.classeDataset);
          this.showDashboard = true;
        }
      },
        (err: any) => console.log(err),
        () => console.log('get dashboard classi report api'));
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

  initGraphClasse(report) {
    this.barChartClasseLabels = [];
    this.barChartClasseData = [];

    // this.sampleClasseData();
    let data1 = [];
    var data2 = [];
    Object.keys(report.oreStudentiMap).map(key => {
      var value = report.oreStudentiMap[key];
      data1.push(value.oreEsterne);
      data2.push(value.oreInterne);
      this.barChartClasseLabels.push(value.nominativo);
    });

    this.barChartClasseData = [
      { data: data1, label: 'ore esterne', stack: 'a', backgroundColor: '#0066CC', hoverBackgroundColor: '#0066CC', barPercentage: 0.5 },
      { data: data2, label: 'ore interne', stack: 'a', backgroundColor: '#00CF86', hoverBackgroundColor: '#00CF86', barPercentage: 0.5 },
    ];

    this.barChartClasseOptions = {
      responsive: true,
      maintainAspectRatio: true,
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
        }],
        xAxes: [{
          scaleLabel: {
            display: false,
            padding: 50,
          },
          ticks: {
            display: false,
            // autoSkip: false,
            // stepSize: 20,
            // maxRotation: 90,
            // minRotation: 90,
          }
        }]
      },
      legend: {
        display: true
      },
      tooltips: {
        enabled: true,
        backgroundColor: '#FAFBF0',
        bodyFontColor: '#707070',
        titleFontColor: '#707070',
        mode: 'single',
        callbacks: {
          afterBody: this.createTooltipClasseCallback(this.classeDataset),
        }
      },
    };

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

  initGraphIsituto(report) {
    this.barChartIstitutoLabels = [];
    this.barChartIstitutoData = [];

    // this.sampleSistemaData();
    Object.keys(report.oreClassiMap).map(key => {
      var value = report.oreClassiMap[key];
      var data = {};
      data['data'] = value.media;
      data['hoverBorderColor'] = '#00CF86';
      data['backgroundColor'] = '#00CF86';
      data['hoverBackgroundColor'] = '#00CF86';
      data['label'] = 'media ore svolte';
      this.barChartIstitutoLabels.push(value.classe);
      this.barChartIstitutoData.push(data);
    });

    this.barChartIstitutoOptions = {
      responsive: true,
      tooltips: {
        backgroundColor: '#FAFBF0',
        bodyFontColor: '#707070',
        titleFontColor: '#707070',
        mode: 'single',
        enabled: true,
        callbacks: {
          label: function (t, d) {
            var multistringText = [];
            return multistringText;
          },
          afterBody: this.createTooltipSistemaCallback(this.sistemaDataset),
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
          }
        }],
        xAxes: [{
          scaleLabel: {
            display: false,
            padding: 50,
          },
          ticks: {
            display: false,
            autoSkip: false,
            stepSize: 20,
            maxRotation: 90,
            minRotation: 90,
          }
        }]
      }
    };
  }

  samplePieChartData() {
    this.tipologie.forEach(element => {
      this.pieChartLabels.push(element.titolo);
      this.pieChartData.push(Math.floor(Math.random() * 8) + 1);
      console.log(element.titolo);
    });
  }

  sampleClasseData() {
    this.classeDataset = {};
    this.classeDataset.oreStudentiMap = {};
    this.classeDataset.numeroOreTotali = 84;
    this.classeDataset.numeroAttivitaInAttesa = 0;
    this.classeDataset.numeroAttivitaInCorso = 2;
    this.classeDataset.numeroAttivitaInRevisione = 3;
    let data1 = [];
    var data2 = [];
    for (var i = 1; i <= 15; i++) {
      let temp1 = Math.floor(Math.random() * 200) + 1;
      let temp2 = Math.floor(Math.random() * 200) + 1;
      data1.push(temp1);
      data2.push(temp2);
      this.barChartClasseLabels.push('Studente-' + i);
      var entry = {};
      entry['oreEsterne'] = temp1;
      entry['oreInterne'] = temp2;
      entry['oreDaValidare'] = (temp1 + temp2) / 2;
      entry['nominativo'] = 'ERCERC CERFE';
      entry['studenteId'] = 'eeee-fff-gggg-hhhh-iii';
      entry['label'] = 'Studente-' + i;
      this.classeDataset.oreStudentiMap[entry['label']] = entry;
    }

    this.barChartClasseData = [
      { data: data1, label: 'ore esterne', stack: 'a', backgroundColor: '#0066CC', hoverBackgroundColor: '#0066CC', barPercentage: 0.5 },
      { data: data2, label: 'ore interne', stack: 'a', backgroundColor: '#00CF86', hoverBackgroundColor: '#00CF86', barPercentage: 0.5 },
    ];

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
      data['data'] = Math.floor(Math.random() * 80) + 1;
      data['hoverBorderColor'] = '#00CF86';
      data['backgroundColor'] = '#00CF86';
      data['hoverBackgroundColor'] = '#00CF86';
      data['label'] = 'ore svolte';
      this.barChartIstitutoLabels.push(i + '° INFC');
      this.barChartIstitutoData.push(data);
      var entry = {};
      entry['media'] = data['data'];
      entry['oreSvolte'] = Math.floor(Math.random() * 50) + 1;
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

  createTooltipSistemaCallback(sistemaDataset) {
    return function (tooltipItem, data) {
      var tooltipItemHovered = tooltipItem[0];
      var info = sistemaDataset.oreClassiMap[tooltipItemHovered.label];
      var multistringText = [];
      multistringText.push('\n');
      multistringText.push('Media ' + info.media + ' ore');
      multistringText.push(info.numStudenti + ' alunni');
      return multistringText;
    }
  }

  createTooltipClasseCallback(classeDataset) {
    return  function(tooltipItem, data) {
      var tooltipItemHovered = tooltipItem[0];
      var info = classeDataset.oreStudentiMap[tooltipItemHovered.label];
      var multistringText = [];
      multistringText.push('\n');
      var oreTotali = info.oreEsterne + info.oreInterne;
      multistringText.push(oreTotali + ' ore totali');
      multistringText.push(info.oreDaValidare + ' ore da validare');
      // multistringText.push(info.oreInterne + ' ore interne');
      // multistringText.push(info.oreEsterne + ' ore esterne');
      return multistringText;
    }
  }

}