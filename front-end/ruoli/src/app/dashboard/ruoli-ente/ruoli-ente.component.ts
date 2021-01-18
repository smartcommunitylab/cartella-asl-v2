import { Component, OnInit, ViewChild, ElementRef} from '@angular/core';
import { DataService } from '../../core/services/data.service';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { PermissionService } from '../../core/services/permission.service';
import { DeleteRuoloEnteModalComponent } from '../modals/delete-ente-ruolo-modal/delete-ente-ruolo-modal.component';
import { Observable, of } from 'rxjs';
import { catchError, debounceTime, distinctUntilChanged, map, tap, switchMap } from 'rxjs/operators';

@Component({
  selector: 'cm-dashboard-ruoli-ente',
  templateUrl: './ruoli-ente.component.html',
  styleUrls: ['./ruoli-ente.component.css']
})

export class DashboardRuoliEnteComponent implements OnInit {

  constructor(
    private dataService: DataService,
    private route: ActivatedRoute,
    private router: Router,
    private modalService: NgbModal,
    private permissionService: PermissionService) { }

  profile;
  report = {};
  registrazioni = [];
  title: string = "Ruoli Ente";

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

    ente: any;
    searchingEnte = false;
    searchEnteFailed = false;
  
    getEnti = (text$: Observable<string>) => 
      text$.pipe(
        debounceTime(500),
        distinctUntilChanged(),
        tap(() => this.searchingEnte = true),
        switchMap(term => this.dataService.searchEnti(0, 10, term).pipe(
          map(result => {
            let entries = [];
            if(result.content) {            
              result.content.forEach(element => {
                entries.push(element);
              });
            }
            return entries;
          }),
          tap(() => this.searchEnteFailed = false),
          catchError((error) => {
            console.log(error);
            this.searchEnteFailed = true;
            return of([]);
          })
          )
        ),
        tap(() => this.searchingEnte = false)
    )  
  
    formatterEnte = (x: {nome: string, partita_iva: string}) => x.nome;
  
  getReport() {
    this.dataService.getReportRegistrazioneEnte(this.ente.id)
      .subscribe(r => {
        if(r) {
          this.registrazioni = r;
        }
      });
  }

  deleteEnteRole(reg) {
    const modalRef = this.modalService.open(DeleteRuoloEnteModalComponent);
    modalRef.componentInstance.role = reg.role;
    modalRef.componentInstance.email = reg.email;
    modalRef.componentInstance.onDelete.subscribe(res => {
      console.log('deleteEnteRole');
      this.dataService.deleteRegistrazioneEnte(reg.id).subscribe(r => {
        if(r) {
          this.getReport();
        }
      });
    });
  }

}
