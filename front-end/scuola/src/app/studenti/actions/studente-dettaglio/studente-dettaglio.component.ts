import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../../../core/services/data.service';
import { Router, ActivatedRoute } from '@angular/router';
import { environment } from '../../../../../src/environments/environment';

@Component({
  selector: 'cm-studente-dettaglio',
  templateUrl: './studente-dettaglio.component.html',
  styleUrls: ['./studente-dettaglio.component.scss']
})
export class StudenteDettaglioComponent implements OnInit {
  oreValidate: any;
  oreTotali: any;
  studente;
  esperienze;
  navTitle: string = "Dettaglio studente";
  tipologie;
  percentage;
  showContent: boolean = false;
  menuContent = 'Qui trovi le attività svolte dal singolo studente, e puoi verificare lo stato di completamento del piano. Puoi andare alla pagina di ciascuna attività associata allo studente cliccando sulla riga corrispondente, oppure direttamente alla gestione delle presenze con il tasto blu sulla riga desiderata.';
  stati = [{ "name": "In attesa", "value": "in_attesa" }, { "name": "In corso", "value": "in_corso" }, { "name": "Revisione", "value": "revisione" }, { "name": "Archiviata", "value": "archiviata" }];  individuale: boolean;
  timeoutTooltip = 250;
  env = environment;

  breadcrumbItems = [
    {
      title: "Lista studenti",
      location: "../../",
    },
    {
      title: "Dettaglio studente"
    }
  ];

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private dataService: DataService) { }


  ngOnInit() {

    this.route.params.subscribe(params => {
      let id = params['id'];
      this.dataService.getStudenteDettaglio(id).subscribe((res) => {
        this.studente = res.studente;
        this.esperienze = res.esperienze;
        this.oreValidate = res.oreValidate;
        this.oreTotali = res.oreTotali;
        this.percentage = ((this.oreValidate / this.oreTotali) * 100).toFixed(0);
        this.breadcrumbItems[1].title = this.studente.name + ' ' + this.studente.surname;

        this.dataService.getAttivitaTipologie().subscribe((res) => {
          this.tipologie = res;
        });
      },
        (err: any) => console.log(err),
        () => console.log('getAttivita'));
    });

  }

  openDetail(attivita) {
    this.router.navigate([attivita.id], { relativeTo: this.route });
  }

  getTipologia(tipologiaId) {
    if (this.tipologie) {
      return this.tipologie.find(data => data.id == tipologiaId);
    } else {
      return tipologiaId;
    }
  }

  menuContentShow() {
   this.showContent = !this.showContent;
  }

  showTipButton(ev, esp, tp) {
    console.log(ev.target.title);
    if (!esp.toolTipButton) {
      if(!esp.report) {
        esp.startTimeoutTip = true;
        setTimeout(() => {
          if(esp.startTimeoutTip == true) {
            this.env.globalSpinner=false;
            esp.fetchingToolTipButton = true;
            this.dataService.getAttivitaReportStudenti(esp.attivitaAlternanzaId).subscribe((report) => {
              esp.report = report;
              this.setTipButton(esp);
              esp.fetchingToolTipButton = false;
              this.env.globalSpinner=true;
              if(esp.startTimeoutTip == true) {
                tp.ngbTooltip = esp.toolTipButton;
                tp.open();                 
              }
            });            
          }
        }, this.timeoutTooltip);
      }
      else {
        this.setTipButton(esp);
      }
    }
  }

  hideTipButton(ev, esp, tp) {
    esp.startTimeoutTip = false; 
    esp.fetchingToolTipButton = false;   
  }  

  setTipButton(esp) {
    esp.toolTipButton = 'Ci sono *nO* ore da validare per *nS* studenti'
    esp.toolTipButton = esp.toolTipButton.replace("*nO*", esp.report.numeroOreDaValidare);
    esp.toolTipButton = esp.toolTipButton.replace("*nS*", esp.report.numeroStudentiDaValidare);    
  } 

  onUnovering(event) {
    console.log(event);
  }

  gestionePresenze(esp) {
    this.tipologie.filter(tipo => {
      if (tipo.id == esp.tipologia) {
        esp.individuale = tipo.individuale;
      }
    })
    if (esp.individuale) {
      this.router.navigateByUrl('/attivita/detail/' + esp.attivitaAlternanzaId + '/modifica/studenti/presenze/individuale');
    } else {
      this.router.navigateByUrl('/attivita/detail/' + esp.attivitaAlternanzaId + '/modifica/studenti/presenze/gruppo');
    }
  }

  goAttivitaDetail(esp){
    this.router.navigateByUrl('/attivita/detail/' + esp.attivitaAlternanzaId );
  }
  getStatoNome(statoValue) {
    if (this.stati) {
      let rtn = this.stati.find(data => data.value == statoValue);
      if (rtn) return rtn.name;
      return statoValue;
    }
  }

  getSubTitle() {
    return this.oreValidate + '/' + this.oreTotali + 'h';
  }

}