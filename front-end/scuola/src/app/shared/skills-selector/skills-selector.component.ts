import { Component, Input, Output, EventEmitter, OnInit, ViewChild } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal/modal';
import { DataService } from '../../core/services/data.service'
import { Competenza } from '../../shared/classes/Competenza.class';
import { PaginationComponent } from '../../shared/pagination/pagination.component';
import { IPagedCompetenze } from '../../shared/classes/IPagedCompetenze.class';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'cm-skills-selector',
  templateUrl: './skills-selector.component.html',
  styleUrls: ['./skills-selector.component.scss']
})
export class SkillsSelectorComponent implements OnInit {

  @Input() attachedCompetenze: Competenza[];
  @Input() addButtonText: string;
  @Output() onNewCompetenzeAddedListener = new EventEmitter<Competenza[]>();
  
  @ViewChild('cmPagination') cmPagination: PaginationComponent;
  totalRecords: number = 0;
  @ViewChild('cmPaginationAssociate') cmPaginationAssociate: PaginationComponent;
  totalRecordsAssociate: number = 0;
 
  pageSize: number = 10;
  competenze: Competenza[];
  competenzeAssociateWindow: Competenza[];
  searchCompetenzaTxt: string;
  searchCompetenzaAssociateTxt: string;
  evn = environment;

  constructor(private dataService: DataService) {
  }

  ngOnInit() {
    this.evn.modificationFlag=true;
    this.getCompetenze(1);
    this.getCompetenzeAssociate(1);
  }

  ngOnChanges(changes) {
    this.mergeCompetenze();
  }

  ngOnDestroy(){
    this.evn.modificationFlag=false;
  }

  mergeCompetenze() {
    if (this.competenze && this.attachedCompetenze) {
      this.competenze.forEach(element => {
        if (this.attachedCompetenze.find(function (el) { return el.id == element.id; })) {
          element['disabled'] = true;
        }
      });
    }
  }

  pageChanged(page: number) {
    this.getCompetenze(page);
  }
  pageChangedAssociate(page: number) {
    this.getCompetenzeAssociate(page);
  }

  getCompetenze(page) {
    this.dataService.getCompetenzeAPI(this.searchCompetenzaTxt, (page - 1), this.pageSize).subscribe((response: IPagedCompetenze) => {
      this.competenze = response.content;
      this.totalRecords = response.totalElements;
      this.mergeCompetenze();
    });
  }

  getCompetenzeAssociate(page: number) {
    
    this.competenzeAssociateWindow = this.attachedCompetenze.slice((page - 1) * this.pageSize, page * this.pageSize);
    this.totalRecordsAssociate = this.attachedCompetenze.length;

    if (this.searchCompetenzaAssociateTxt) {
      var txt = this.searchCompetenzaAssociateTxt;
      this.competenzeAssociateWindow = this.attachedCompetenze.filter(function (tag) {
        return tag.titolo.indexOf(txt) >= 0;
      });
    }
    
  }

  searchCompetenza() {
    this.cmPagination.changePage(1);
    this.getCompetenze(1);
  }

  searchCompetenzaAssociate() {
    this.cmPaginationAssociate.changePage(1);
    this.getCompetenzeAssociate(1);
  }

  toggleCompetenza(competenza) {    
    if (this.attachedCompetenze.indexOf(competenza) < 0) {
      this.attachedCompetenze.push(competenza);
      this.getCompetenzeAssociate(1);
    }
    this.competenze.forEach(element => {
      if (element.id == competenza.id) {
        element['disabled'] = true;
      }
    });

  }

  addCompetenze() {
    this.onNewCompetenzeAddedListener.emit(this.attachedCompetenze);
    // this.getCompetenzeAssociate(1);
  }

  deleteCompetenza(competenza) {
    this.attachedCompetenze.splice(this.attachedCompetenze.indexOf(competenza), 1);
    this.competenze.forEach(element => {
      if (element.id == competenza.id) {
        element['disabled'] = false;
      }
    })
    this.getCompetenzeAssociate(1);
  }

 
}
