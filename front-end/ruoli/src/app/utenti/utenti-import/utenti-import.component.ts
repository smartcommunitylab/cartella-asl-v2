import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../../core/services/data.service';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { PermissionService } from '../../core/services/permission.service';
import { GrowlerService, GrowlerMessageType } from '../../core/growler/growler.service';
import { ngCopy } from 'angular-6-clipboard';
import { Observable, of } from 'rxjs';
import { catchError, debounceTime, distinctUntilChanged, map, tap, switchMap } from 'rxjs/operators';

@Component({
  selector: 'cm-utenti-import',
  templateUrl: './utenti-import.component.html',
  styleUrls: ['./utenti-import.component.css']
})
export class UtentiImportComponent implements OnInit {

  constructor(
    private dataService: DataService,
    private route: ActivatedRoute,
    private router: Router,
    private modalService: NgbModal,
    private growler: GrowlerService,
    private permissionService: PermissionService) { }

  profile: any;
  title: string = "Import utenti da csv";
  importResult: any;
  showErrors = false;

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

  uploadStudenti(fileInput) {
    if (fileInput.target.files && fileInput.target.files[0]) {
      this.dataService.uploadStudenti(fileInput.target.files[0]).subscribe((result) => {
        this.importResult = result;
        if(this.importResult && this.importResult.errors && (this.importResult.errors.length > 0)) {
          this.showErrors = true;
        } else {
          this.showErrors = false;
        }
        console.log("uploadStudenti OK");
        this.growler.growl('invio file andato a buon fine', GrowlerMessageType.Success, 3000);
      });
    }
  }

  uploadFunzioniStrumentali(fileInput) {
    if (fileInput.target.files && fileInput.target.files[0]) {
      this.dataService.uploadFunzioniStrumentali(fileInput.target.files[0], this.istituto.id).subscribe((result) => {
        this.importResult = result;
        if(this.importResult && this.importResult.errors && (this.importResult.errors.length > 0)) {
          this.showErrors = true;
        } else {
          this.showErrors = false;
        }
        console.log("uploadFunzioniStrumentali OK");
        this.growler.growl('invio file andato a buon fine', GrowlerMessageType.Success, 3000);
      });
    }
  }

  copyText() {
    if(this.importResult) {
      var myJSON = JSON.stringify(this.importResult);
      ngCopy(myJSON);
      this.growler.growl('dati copiati nella clipboard', GrowlerMessageType.Success, 3000);
    }
  }

  resetErrors() {
    if(this.importResult) {
      this.importResult.errors = null;
    }
  }

}
