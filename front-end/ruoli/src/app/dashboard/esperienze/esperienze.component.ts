import { Component, OnInit, ViewChild, ElementRef} from '@angular/core';
import { DataService } from '../../core/services/data.service';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { PermissionService } from '../../core/services/permission.service';
import { DeleteEsperienzaModalComponent } from '../modals/delete-esperienza-modal/delete-esperienza-modal.component';
import { ngCopy } from 'angular-6-clipboard';
import { Observable, of } from 'rxjs';
import { catchError, debounceTime, distinctUntilChanged, map, tap, switchMap } from 'rxjs/operators';
import * as moment from 'moment';

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
  annoScolastico = '';
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
      this.getAnnoScolstico();
    }

  getAnnoScolstico() {
    var now = moment();
    var lastDay = moment().month(8).date(1);
    if(now.isBefore(lastDay)) {
      this.annoScolastico = moment().year(now.year()-1).format('YYYY') + '-' + now.format('YY');
    } else {
      this.annoScolastico = now.format('YYYY') + '-' + moment().year(now.year()+1).format('YY');
    }
  }  

  istituto: any;
  searchingIstituto = false;
  searchIstitutoFailed = false;

  getIstituti = (text$: Observable<string>) => 
    text$.pipe(
      debounceTime(500),
      distinctUntilChanged(),
      tap(() => this.searchingIstituto = true),
      switchMap(term => this.dataService.searchIstituti(0, 10, term).pipe(
        map(result => {
          let entries = [];
          if(result.content) {            
            result.content.forEach(element => {
              entries.push(element);
            });
          }
          return entries;
        }),
        tap(() => this.searchIstitutoFailed = false),
        catchError((error) => {
          console.log(error);
          this.searchIstitutoFailed = true;
          return of([]);
        })
        )
      ),
      tap(() => this.searchingIstituto = false)
  )  

  formatterIstituto = (x: {name: string}) => x.name;

  getReport() {
    this.dataService.getReportEsperienze(this.istituto.id, this.annoScolastico, this.text)
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
    this.dataService.getEsperienzeCsv(this.istituto.id, this.annoScolastico, this.text).subscribe((doc) => {
      const downloadLink = document.createElement("a");
      downloadLink.href = doc.url;
      downloadLink.download = doc.filename;
      document.body.appendChild(downloadLink);
      downloadLink.click();
      document.body.removeChild(downloadLink);  
    });
  }

  deleteEsperienza(esp: any) {
    const modalRef = this.modalService.open(DeleteEsperienzaModalComponent);
    modalRef.componentInstance.titoloEsperienza = esp.titolo;
    modalRef.componentInstance.nominativoStudente = esp.nominativoStudente;
    modalRef.componentInstance.onDelete.subscribe(res => {
      console.log('deleteEsperienza');
      this.dataService.deleteEsperienza(esp.esperienzaId).subscribe(r => {
        if(r) {
          this.getReport();
        }
      });
    });
  }

}
