import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../../core/services/data.service';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { PaginationComponent } from '../../shared/pagination/pagination.component';
import { DeleteUserModalComponent } from '../modals/delete-user-modal/delete-user-modal.component';
import { PermissionService } from '../../core/services/permission.service';

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
    private permissionService: PermissionService) { }

  title: string = "Import utenti";

  ngOnInit() {

  }


}
