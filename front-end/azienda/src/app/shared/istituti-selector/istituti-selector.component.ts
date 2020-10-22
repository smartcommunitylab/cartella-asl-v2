import { Component, Input, Output, EventEmitter, OnInit, ViewChild } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal/modal';
import { DataService } from '../../core/services/data.service'
import { PaginationComponent } from '../pagination/pagination.component';
import { IPagedIstituto } from '../classes/IPagedIstituto.class';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'cm-istituti-selector',
  templateUrl: './istituti-selector.component.html',
  styleUrls: ['./istituti-selector.component.scss']
})
export class IstitutiSelectorComponent implements OnInit {

  @Input() attachedIstituti;
  @Input() addButtonText: string;
  @Output() onNewIstitutiAddedListener = new EventEmitter<any[]>();
  
  @ViewChild('cmPagination') cmPagination: PaginationComponent;
  totalRecords: number = 0;
  @ViewChild('cmPaginationAssociate') cmPaginationAssociate: PaginationComponent;
  totalRecordsAssociate: number = 0;
 
  pageSize: number = 10;
  istitute;
  istituteAssociateWindow;
  searchistitutoTxt: string;
  searchistitutoAssociateTxt: string;
  evn = environment;

  constructor(private dataService: DataService) {
  }

  ngOnInit() {
    this.evn.modificationFlag=true;
    this.getIstituti(1);
    this.getIstitutiAssociate(1);
  }

  ngOnChanges(changes) {
    this.mergeIstituti();
  }

  ngOnDestroy(){
    this.evn.modificationFlag=false;
  }

  mergeIstituti() {
    if (this.istitute && this.attachedIstituti) {
      this.istitute.forEach(element => {
        if (this.attachedIstituti.find(function (el) { return el.id == element.id; })) {
          element['disabled'] = true;
        }
      });
    }
  }

  pageChanged(page: number) {
    this.getIstituti(page);
  }
  pageChangedAssociate(page: number) {
    this.getIstitutiAssociate(page);
  }

  getIstituti(page) {
    this.dataService.searchIstitutiAPI(this.searchistitutoTxt, (page - 1), this.pageSize).subscribe((response: IPagedIstituto) => {
      this.istitute = response.content;
      this.totalRecords = response.totalElements;
      this.mergeIstituti();
    });
  }

  getIstitutiAssociate(page: number) {
    
    this.istituteAssociateWindow = this.attachedIstituti.slice((page - 1) * this.pageSize, page * this.pageSize);
    this.totalRecordsAssociate = this.attachedIstituti.length;

    if (this.searchistitutoAssociateTxt) {
      var txt = this.searchistitutoAssociateTxt;
      this.istituteAssociateWindow = this.attachedIstituti.filter(function (tag) {
        return tag.titolo.indexOf(txt) >= 0;
      });
    }
    
  }

  addIstituti() {
    this.onNewIstitutiAddedListener.emit(this.attachedIstituti);
    // this.getCompetenzeAssociate(1);
  }

  
}