import { Component, OnInit } from '@angular/core';
import { DataService } from '../core/services/data.service'
import { ActivatedRoute, Router } from '@angular/router';
import { ChartDataSets, ChartOptions, ChartType, Chart } from 'chart.js';
import { Label } from 'ng2-charts';
import { UpdateDocenteModalComponent } from './actions/update-docente-modal/update-docente-modal.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { RuoloCancellaModal } from './actions/ruolo-cancella-modal/ruolo-cancella-modal.component';

@Component({
  selector: 'istituto',
  templateUrl: './istituto.component.html',
  styleUrls: ['./istituto.component.scss']
})

export class IstitutoComponent implements OnInit {
  filtro;
  menuContent = "In questa sezione trovi una dashboard che mostra un riassunto visivo delle attività di tutti gli studenti. Inoltre, da qui puoi gestire gli account abilitati sul Portale EDIT Istituto di questo istituto e i dati principali dell’istituto.";
  showContent: boolean = false;
  istituto;
  showDashboard: boolean = false;
  classeReport: boolean = true;
  tipologie;
  classe = null;
  classi;
  classeDataset;
  sistemaDataset;
  numeroAttivitaInAttesa;
  numeroAttivitaInCorso;
  numeroAttivitaInRevisione;
  oreTotali;
  registeredDocenti;

  // PIE chart.
  pieChartOptions: ChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    legend: {
      position: 'left',
      labels: {
        fontSize: 14,
        fontFamily: "Titillium Web"
      }
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
  public barChartIstitutoType: ChartType = 'bar';
  public barChartIstitutoLegend = true;
  public barChartIstitutoPlugins = [];
  public barChartIstitutoData: ChartDataSets[] = [];
  additionalDataSistema = [];
  public barChartIstitutoOptions: ChartOptions = {};

  constructor(
    private dataService: DataService,
    private route: ActivatedRoute,
    private router: Router,
    private modalService: NgbModal
  ) {}

  ngOnInit(): void {
    this.getIstituto();
    // GET classes.
    this.dataService.getAttivitaTipologie().subscribe((res) => {
      this.tipologie = res;
      this.dataService.getDashboardIstitutoClasse()
        .subscribe((response) => {
          this.classi = response;
          this.dataService.getRegistrazioneDocente().subscribe((response) => {
            this.registeredDocenti = response;
          },
          (err: any) => console.log(err),
          () => console.log('get registrazione docente api'));
        },
          (err: any) => console.log(err),
          () => console.log('get dashboard istituto classi api'));
    },
      (err: any) => console.log(err),
      () => console.log('getAttivitaTipologie'));
  }

  getIstituto() {
    this.dataService.getIstitutoById(this.dataService.istitutoId)
      .subscribe((response) => {
        this.istituto = response;
      },
        (err: any) => console.log(err),
        () => console.log('get istituto api'));
  }

  initClasseDashboard() {
    // GET classes report.
    if (this.classe) {
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

  initGraphClasse(report) {
    this.barChartClasseLabels=[];
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
      responsive: false,
      maintainAspectRatio: false,
      scales: {
        yAxes: [{
          position: 'left',
          scaleLabel: {
            display: true,
            labelString: 'Ore',
          },
          ticks: {
            beginAtZero: true,
            // stepSize: 40,
            // suggestedMax: 400
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
        display: true,
        labels: {
          fontSize: 14,
          fontFamily: "Titillium Web"
        }
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
    this.barChartIstitutoData =[];
    
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
      responsive: false,
      maintainAspectRatio: false,
      scales: {
        yAxes: [{
          position: 'left',
          scaleLabel: {
              display: true,
              labelString: 'Ore',
          },
          ticks: {
            beginAtZero: true,
            // stepSize: 40,
            // suggestedMax: 400,
        }
      }],
        xAxes: [{
          ticks: {
            maxRotation: 60,
            minRotation: 50,
          }
        }],
      },
      legend: {
        display: true,
        labels: {
          fontSize: 14,
          fontFamily: "Titillium Web"
        }        
      },
      tooltips: {
        enabled: true,
        backgroundColor: '#FAFBF0',
        bodyFontColor: '#707070',
        titleFontColor: '#707070',
        mode: 'single',
        callbacks: {
          label: function(t, d) {
            var multistringText = [];
            return multistringText;
          },
          afterBody: this.createTooltipSistemaCallback(this.sistemaDataset),
         }
      },
    };
  }

  menuContentShow() {
    this.showContent = !this.showContent;
  }

  modifica() {
    this.router.navigate(['../modifica', this.dataService.istitutoId], { relativeTo: this.route });
  }

  samplePieChartData() {
    this.tipologie.forEach(element => {
      this.pieChartLabels.push(element.titolo);
      this.pieChartData.push(Math.floor(Math.random() * 8) + 1  );
      console.log(element.titolo);
    });
  }

  sampleSistemaData() {
    this.sistemaDataset = {};
    this.sistemaDataset.oreClassiMap = {};
    this.sistemaDataset.numeroOreTotali = 84;
    this.sistemaDataset.numeroAttivitaInAttesa = 0;
    this.sistemaDataset.numeroAttivitaInCorso = 2;
    this.sistemaDataset.numeroAttivitaInRevisione = 3;
    for (var i=1; i<=24; i++) {
      var data = {};
      data['data'] = Math.floor(Math.random() * 80) + 1;
      data['hoverBorderColor'] = '#00CF86';
      data['backgroundColor'] = '#00CF86';
      data['hoverBackgroundColor'] = '#00CF86';
      data['label'] = 'media ore svolte';
      this.barChartIstitutoLabels.push(i+'° INFC');    
      this.barChartIstitutoData.push(data);
      var entry = {};
      entry['oreSvolte'] = data['data'];
      entry['media'] = Math.floor(Math.random() * 80) + 1;
      entry['numStudenti'] = Math.floor(Math.random() * 5) + 1;
      entry['label'] = i+'° INFC';
      this.sistemaDataset.oreClassiMap[entry['label']] = entry;
    }
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
    for (var i=1; i<=15; i++) {
      let temp1 = Math.floor(Math.random() * 200) + 1;
      let temp2 = Math.floor(Math.random() * 200) + 1;
      data1.push(temp1);
      data2.push(temp2);      
      this.barChartClasseLabels.push('Studente-' + i);
      var entry = {};
      entry['oreEsterne'] = temp1;
      entry['oreInterne'] = temp2;
      entry['oreDaValidare'] = (temp1+temp2)/2;
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
  
  createTooltipSistemaCallback(sistemaDataset) {
    return  function(tooltipItem, data) {
      var tooltipItemHovered = tooltipItem[0];
      var info = sistemaDataset.oreClassiMap[tooltipItemHovered.label];
      var multistringText = [];
      multistringText.push('\n');
      multistringText.push('Media ' + info.media + ' ore');
      multistringText.push(info.numStudenti + ' alunni');
      // multistringText.push(info.oreSvolte + ' ore');
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

  roundedBorder() {
    Chart['elements'].Rectangle.prototype.draw = function () {
      let ctx = this._chart.ctx;
      let view = this._view;
      //////////////////// edit this to change how rounded the edges are /////////////////////
      let borderRadius = 20;
      let left = view.x - view.width / 2;
      let right = view.x + view.width / 2;
      let top = view.y;
      let bottom = view.base;
      ctx.beginPath();
      ctx.fillStyle = view.backgroundColor;
      ctx.strokeStyle = view.borderColor;
      ctx.lineWidth = view.borderWidth;

      let barCorners = [
        [left, bottom],
        [left, top],
        [right, top],
        [right, bottom]
      ];

      //start in bottom-left
      ctx.moveTo(barCorners[0][0], barCorners[0][1]);

      for (let i = 1; i < 4; i++) {
        let x = barCorners[1][0];
        let y = barCorners[1][1];
        let width = barCorners[2][0] - barCorners[1][0];
        let height = barCorners[0][1] - barCorners[1][1];

        //Fix radius being too big for small values
        if (borderRadius > width / 2) {
          borderRadius = width / 2;
        }
        if (borderRadius > height / 2) {
          borderRadius = height / 2;
        }

        // DRAW THE LINES THAT WILL BE FILLED - REPLACING lineTo with quadraticCurveTo CAUSES MORE EDGES TO BECOME ROUNDED
        ctx.moveTo(x + borderRadius, y);
        ctx.lineTo(x + width - borderRadius, y);
        ctx.quadraticCurveTo(x + width, y, x + width, y + borderRadius);
        ctx.lineTo(x + width, y + height - borderRadius);
        ctx.quadraticCurveTo(x + width, y + height, x + width - borderRadius, y + height);
        ctx.lineTo(x + borderRadius, y + height);
        ctx.quadraticCurveTo(x, y + height, x, y + height - borderRadius);
        ctx.lineTo(x, y + borderRadius);
        ctx.quadraticCurveTo(x, y, x + borderRadius, y);
      }
      //FILL THE LINES
      ctx.fill();
    };
  }

  listAttivita(stato) {
    this.filtro = {
      tipologia: '',
      titolo: '',
      stato: stato
    };
    localStorage.setItem('filtroAttivita', JSON.stringify(this.filtro));
    this.router.navigateByUrl('/attivita/list');
  }

  onFilterChange(ev) {
    this.classeReport = !this.classeReport;
    this.showDashboard = true;
    if (this.classeReport) {
      this.classe = null;
      this.initClasseDashboard();
    } else {
      this.initIstitutoDashboard();
    }
  }

  selectClasseFilter() {
    // console.log(this.classe);
    this.initClasseDashboard();
  }

}