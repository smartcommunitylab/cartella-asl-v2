import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'cm-pagination',
  templateUrl: './pagination.component.html',
  styleUrls: [ './pagination.component.css' ]
})

export class PaginationComponent implements OnInit {
  
  private pagerTotalItems: number;
  private pagerPageSize: number;
  
  totalPages: number;
  pages: number[] = [];
  currentPage: number = 1;
  isVisible: boolean = false;
  previousEnabled: boolean = false;
  nextEnabled: boolean = false;

  maxPagesInComponent:number = 10;
  truncatedLeft:boolean = false;
  truncatedRight:boolean = false;
  
  @Input() get maxPages():number {
    return this.maxPagesInComponent;
  }

  set maxPages(pages:number) {
    this.maxPagesInComponent = pages;
  }

  @Input() get pageSize():number {
    return this.pagerPageSize;
  }

  set pageSize(size:number) {
    this.pagerPageSize = size;
    this.update();
  }
  
  @Input() get totalItems():number {
    return this.pagerTotalItems;
  }

  set totalItems(itemCount:number) {
    this.pagerTotalItems = itemCount;
    this.update();
  }
  
  @Output() pageChanged: EventEmitter<number> = new EventEmitter();

  constructor() { }

  ngOnInit() { 

  }
  
  update() {
    /*this.pages = [];
    if (this.pagerTotalItems && this.pagerPageSize) {
      this.totalPages = Math.floor(this.pagerTotalItems/this.pageSize);
      this.isVisible = this.totalPages > 0;
      if (this.totalItems >= this.pageSize) {
        for (let i = 1;i < this.totalPages + 1;i++) {
          this.pages.push(i);
        }
      }
      return;
    }
    
    this.isVisible = false;*/
    this.pages = [];
    this.totalPages = Math.ceil(this.pagerTotalItems/this.pageSize);
    this.truncatedLeft = this.truncatedRight = false;
    if (this.totalPages > this.maxPagesInComponent) { //restricted pages
      let startPage = 1;
      if (this.currentPage > this.maxPagesInComponent) {
        startPage = this.currentPage - ((this.currentPage-1) % this.maxPagesInComponent);
        this.truncatedLeft = true;
      }
      for (let i = startPage; i < this.totalPages + 1 && i < startPage + this.maxPagesInComponent;i++) {
        this.pages.push(i);
      }
      if (startPage + this.maxPagesInComponent < this.totalPages) {
        this.truncatedRight = true;
      }
      //console.log("filtered pages, startPage: " + startPage + " totalPages: " + this.totalPages + " currentPage: " + this.currentPage);
    } else { //all pages
      for (let i = 1; i < this.totalPages + 1;i++) {
        this.pages.push(i);
      }
    }
    this.isVisible = true;
  }
  
  previousNext(direction: number, event?: MouseEvent) {
    let page: number = this.currentPage;
    if (direction == -1) {
        if (page > 1) page--;
    } else {
        if (page < this.totalPages) page++;
    }
    this.changePage(page, event);
  }

  previousNextTruncated(direction: number, event?: MouseEvent) {
    let page: number = this.currentPage;
    if (direction == -1) {
        page = page - ((this.currentPage-1) % this.maxPagesInComponent) - 1;
    } else {
        page = page + (this.maxPagesInComponent - (this.currentPage-1) % this.maxPagesInComponent);
    }
    this.changePage(page, event);
  }

  firstLastPage(direction: number, event?: MouseEvent) {
    let page: number = 1;
    if (direction == -1) {
        page = 1;
    } else {
        page = this.totalPages;
    }
    this.changePage(page, event);
  }
  
  changePage(page: number, event?: MouseEvent) {
    if (event) {
      event.preventDefault();
    }
    if (this.currentPage === page) return;
    let prevPage = this.currentPage;
    this.currentPage = page;
    this.previousEnabled = this.currentPage > 1;
    this.nextEnabled = this.currentPage < this.totalPages;

    if (Math.floor((this.currentPage-1) / this.maxPagesInComponent) != Math.floor((prevPage-1) / this.maxPagesInComponent)) {
      this.update();
    }

    this.pageChanged.emit(page);
  }

}