import { Component, OnInit, ViewChild, ElementRef} from '@angular/core';
import { DataService } from '../../core/services/data.service';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { PermissionService } from '../../core/services/permission.service';
import { Observable, of } from 'rxjs';
import { catchError, debounceTime, distinctUntilChanged, map, tap, switchMap } from 'rxjs/operators';

@Component({
  selector: 'cm-dashboard-registrazioni',
  templateUrl: './registrazioni.component.html',
  styleUrls: ['./registrazioni.component.css']
})

export class DashboardRegistrazioniComponent implements OnInit {

  constructor(
    private dataService: DataService,
    private route: ActivatedRoute,
    private router: Router,
    private modalService: NgbModal,
    private permissionService: PermissionService) { }

  profile;
  report = {};
  cf = '';
  registrazioni = [];
  title: string = "Lista iscrizioni";

    ngOnInit() {
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
    this.dataService.getReportRegistrazioni(this.istituto.id, this.cf)
      .subscribe(r => {
        if(r) {
          this.registrazioni = r;
        }
      });
  }

}
