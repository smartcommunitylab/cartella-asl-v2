import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../../../core/services/data.service';
import { Router, ActivatedRoute } from '@angular/router';
import { AttivitaAlternanza } from '../../../shared/classes/AttivitaAlternanza.class';
import { FormGroup, FormControl } from '@angular/forms';
import * as moment from 'moment';
import { DatePickerComponent } from 'ng2-date-picker';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'cm-modifica-dettaglio',
  templateUrl: './attivita-modifica.component.html',
  styleUrls: ['./attivita-modifica.component.scss']
})
export class AttivitaDettaglioModificaComponent implements OnInit {

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private dataService: DataService) { }

  attivita: AttivitaAlternanza;
  esperienze;
  titolo;
  descrizione;
  formatore;
  referenteEsterno;
  referenteEsternoCF;
  myForm: FormGroup;
  tipologie;
  date;
  start: moment.Moment;
  end: moment.Moment;
  forceTitoloErrorDisplay: boolean = false;
  forceReferenteEsternoErrorDisplay: boolean = false;
  forceOreErrorDisplay: boolean = false;
  menuContent = "In questa pagina trovi tutte le informazioni relative all’attività che stai visualizzando.";
  showContent: boolean = false;
  attivitaTipologia;
  stati = [{ "name": "In attesa", "value": "in_attesa" }, { "name": "In corso", "value": "in_corso" }, { "name": "Revisionare", "value": "revisione" }, { "name": "Archiviata", "value": "archiviata" }];
  attivitaStato: string = "";
  evn = environment;
  breadcrumbItems = [
    {
      title: "Dettaglio attività",
      location: "../../",
    },
    {
      title: "Modifica dati attività"
    }
  ];

  datePickerConfig = {
    locale: 'it',
    firstDayOfWeek: 'mo',
  };

  @ViewChild('calendarStart') calendarStart: DatePickerComponent;
  @ViewChild('calendarEnd') calendarEnd: DatePickerComponent;

  openStart() {
    this.calendarStart.api.open();
  }

  closeStart() {
    this.calendarStart.api.close();
  }

  openEnd() {
    this.calendarEnd.api.open();
  }

  closeEnd() {
    this.calendarEnd.api.close();
  }

  ngOnInit() {
    this.evn.modificationFlag=true;
    this.date = {
      dataInizio: moment(),
      dataFine: moment()
    }

    this.route.params.subscribe(params => {
      let id = params['id'];
      this.dataService.getAttivitaTipologie().subscribe((res) => {
        this.tipologie = res;
        this.dataService.getAttivita(id).subscribe((res) => {
          this.attivita = res.attivitaAlternanza;
          this.attivita.nomeIstituto = res.nomeIstituto;
          this.attivitaStato = this.getStatoNome(this.attivita.stato);
          this.esperienze = res.esperienze;
          this.tipologie.filter(tipo => {
            if (tipo.id == this.attivita.tipologia) {
              this.attivitaTipologia = tipo.titolo;
            }
          })

          var dataInizio = new Date(this.attivita.dataInizio);
          this.date.dataInizio = moment(dataInizio.getTime());
          var dataFine = new Date(this.attivita.dataFine);
          this.date.dataFine = moment(dataFine);

          this.titolo = this.attivita.titolo;

          this.myForm = new FormGroup({
            titolo: new FormControl(),
            descrizione: new FormControl(),
            formatore: new FormControl(),
            referenteScuola: new FormControl(),
            referenteEsterno: new FormControl(),
            ore: new FormControl(),
            luogoSvolgimento: new FormControl(),
            formatoreCF: new FormControl(),
            referenteScuolaCF: new FormControl(),
            referenteEsternoCF: new FormControl()
          });
        }, (err: any) => console.log(err),
          () => console.log('getAttivitaTipologie'));
      });
    });
  }
  ngOnDestroy(){
    this.evn.modificationFlag=false;
  }

  getTipologiaString(tipologiaId) {
    if (this.tipologie) {
      let rtn = this.tipologie.find(data => data.id == tipologiaId);
      if (rtn) return rtn.titolo;
      return tipologiaId;
    } else {
      return tipologiaId;
    }
  }

  getTipologia(tipologiaId) {
    if (this.tipologie) {
      return this.tipologie.find(data => data.id == tipologiaId);
    } else {
      return tipologiaId;
    }
  }

  cancel() {
    this.router.navigate(['../../'], { relativeTo: this.route });
  }

  save() {

      if (this.attivita.referenteEsterno && this.attivita.referenteEsterno != '' && this.attivita.referenteEsterno.trim().length > 0) {
        this.attivita.referenteEsterno =  this.attivita.referenteEsterno.trim();
        this.forceReferenteEsternoErrorDisplay = false;
      } else {
        this.forceReferenteEsternoErrorDisplay = true;
      }

    this.attivita.dataInizio = moment(this.date.dataInizio, 'YYYY-MM-DD').valueOf();
    this.attivita.dataFine = moment(this.date.dataFine, 'YYYY-MM-DD').valueOf();

    if (!this.forceReferenteEsternoErrorDisplay && !this.forceOreErrorDisplay) {
      (this.attivita.referenteEsternoCF) ? this.attivita.referenteEsternoCF = this.attivita.referenteEsternoCF.trim() : this.attivita.referenteEsternoCF = null;
      (this.attivita.referenteEsternoTelefono) ? this.attivita.referenteEsternoTelefono = this.attivita.referenteEsternoTelefono.trim() : this.attivita.referenteEsternoTelefono = null;
  
      this.dataService.updateAttivitaAlternanza(this.attivita).subscribe((res => {
        this.router.navigate(['../../'], { relativeTo: this.route });
      }));
    }

  }

  menuContentShow() {
    this.showContent = !this.showContent;
  }
  getStatoNome(statoValue) {
    if (this.stati) {
      let rtn = this.stati.find(data => data.value == statoValue);
      if (rtn) return rtn.name;
      return statoValue;
    }
  }

  trimValue(event, type) { 
    if(type == 'titolo'){
      (event.target.value.trim().length == 0) ? this.forceTitoloErrorDisplay = true : this.forceTitoloErrorDisplay   = false;
    } else if(type == 'esterno'){
      (event.target.value.trim().length == 0) ? this.forceReferenteEsternoErrorDisplay = true : this.forceReferenteEsternoErrorDisplay = false;
    }else if(type == 'trim'){
      event.target.value = event.target.value.trim(); 
    }
  }
}
