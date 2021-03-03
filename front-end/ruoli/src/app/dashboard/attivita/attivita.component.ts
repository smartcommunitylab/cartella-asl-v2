import { Component, OnInit, ViewChild, ElementRef} from '@angular/core';
import { DataService } from '../../core/services/data.service';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { PermissionService } from '../../core/services/permission.service';
import { DeleteAttivitaModalComponent } from '../modals/delete-attivita-modal/delete-attivita-modal.component';
import { ActivateAttivitaModalComponent } from '../modals/activate-attivita-modal/activate-attivita-modal.component';
import { DetailAttivitaModalComponent } from '../modals/detail-attivita-modal/detail-attivita-modal.component';
import { Observable, of } from 'rxjs';
import { catchError, debounceTime, distinctUntilChanged, map, tap, switchMap } from 'rxjs/operators';
import * as moment from 'moment';

@Component({
  selector: 'cm-dashboard-attivita',
  templateUrl: './attivita.component.html',
  styleUrls: ['./attivita.component.css']
})

export class DashboardAttivitaComponent implements OnInit {

  constructor(
    private dataService: DataService,
    private route: ActivatedRoute,
    private router: Router,
    private modalService: NgbModal,
    private permissionService: PermissionService) { }

  profile;
  report = {};
  annoScolastico = '2019-20';
  text = "";
  attivitaList = [];
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
  title: string = "Statistiche delle attività";

    ngOnInit() {
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
    this.dataService.getReportAttivita(this.istituto.id, this.annoScolastico)
      .subscribe(r => {
        if(r) {
          this.report = r;
        }
      });
  }

  getAttivita() {
    this.dataService.getReportDettaglioAttivita(this.istituto.id, this.annoScolastico, this.text)
      .subscribe(r => {
        if(r) {
          this.attivitaList = r;
        }
      });
  }

  deleteAttivita(aa: any) {
    const modalRef = this.modalService.open(DeleteAttivitaModalComponent);
    modalRef.componentInstance.titolo = aa.titolo;
    modalRef.componentInstance.onDelete.subscribe(res => {
      console.log('deleteAttivita');
      this.dataService.deleteAttivita(aa.id).subscribe(r => {
        if(r) {
          this.getAttivita();
        }
      });
    });
  }

  activateAttivita(aa: any) {
    const modalRef = this.modalService.open(ActivateAttivitaModalComponent);
    modalRef.componentInstance.titolo = aa.titolo;
    modalRef.componentInstance.onActivate.subscribe(res => {
      console.log('activateAttivita');
      this.dataService.activateAttivita(aa.id).subscribe(r => {
        if(r) {
          this.getAttivita();
        }
      });
    });
  }

  detailAttivita(aa: any) {
    this.dataService.reportAttivita(aa.id).subscribe(r => {
      if(r) {
        const modalRef = this.modalService.open(DetailAttivitaModalComponent, {size:'lg'});
        modalRef.componentInstance.report = r;
        modalRef.componentInstance.onActivate.subscribe(res => {
        });
      }
    });
  }

  getTipologia(id) {
    return this.tipologieMap[id]['label'];
  }

  getNumStudentiTip(aa: any) {
    let classi = [];
    aa.classi.forEach(element => {
      if(!classi.includes(element)) {
          classi.push(element);
      }
    });
    let tip = 'studenti:' + aa.studenti.length + ' / classi:' + classi.length;
    return tip;
  }

  getStudentiTip(aa: any) {
    let tip = [];
    for (let i = 0; i < aa.studenti.length; i++) {
        let row = aa.studenti[i] + " - " + aa.classi[i];
        tip.push(row);
        if (i == 15) {
            break;
        }
    } 
    return tip;   
  }
}
