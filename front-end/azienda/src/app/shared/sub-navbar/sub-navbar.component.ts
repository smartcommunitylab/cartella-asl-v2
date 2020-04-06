import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'sub-navbar',
  templateUrl: './sub-navbar.component.html',
  styleUrls: ['./sub-navbar.component.css']
})
export class SubNavbarComponent implements OnInit {

  @Input() titolo;
  @Input() breadcrumbItems;
  @Output() customActionCallback = new EventEmitter();

  backBtnDestination:string;

  constructor(
    private router: Router,
    private route: ActivatedRoute) { }

  ngOnInit() {
    
  }

  backBtnClick() {
    if (this.breadcrumbItems.length > 1) {
      if (this.breadcrumbItems[this.breadcrumbItems.length - 2].customAction) {
        if (this.customActionCallback) this.customActionCallback.emit(this.breadcrumbItems[this.breadcrumbItems.length - 2]);
      } else {
        this.router.navigate([this.breadcrumbItems[this.breadcrumbItems.length - 2].location], {relativeTo: this.route, queryParams: this.breadcrumbItems[this.breadcrumbItems.length - 2].queryParams});
      }
    } else {
      this.router.navigate(['../'], {relativeTo: this.route});
    }
  }
  customAction(item) {
    if (item.customAction && this.customActionCallback) this.customActionCallback.emit(item);
  }

}
