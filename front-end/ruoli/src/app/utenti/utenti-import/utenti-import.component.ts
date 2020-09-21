import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../../core/services/data.service';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { PermissionService } from '../../core/services/permission.service';
import { GrowlerService, GrowlerMessageType } from '../../core/growler/growler.service';

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
  istitutoId = '';
  title: string = "Import utenti da csv";

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

  getIstituti() {
    if(this.profile) {
      if(this.profile.istituti) {
        return Object.values(this.profile.istituti);
      }
    }
    return [];
  }

  uploadStudenti(fileInput) {
    if (fileInput.target.files && fileInput.target.files[0]) {
      this.dataService.uploadStudenti(fileInput.target.files[0]).subscribe((users) => {
        console.log("uploadStudenti OK");
        this.growler.growl('invio file andato a buon fine', GrowlerMessageType.Success, 5000);
      });
    }
  }

  uploadFunzioniStrumentali(fileInput) {
    if (fileInput.target.files && fileInput.target.files[0]) {
      this.dataService.uploadFunzioniStrumentali(fileInput.target.files[0], this.istitutoId).subscribe((users) => {
        console.log("uploadFunzioniStrumentali OK");
        this.growler.growl('invio file andato a buon fine', GrowlerMessageType.Success, 5000);
      });
    }
  }

}
