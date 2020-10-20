import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'sub-navbar',
  templateUrl: './sub-navbar.component.html',
  styleUrls: ['./sub-navbar.component.scss']
})
export class SubNavbarComponent implements OnInit {

  @Input() titolo;
  @Input() menuText;
  @Input() breadcrumbItems;
  @Output() customActionCallback = new EventEmitter();

  backBtnDestination:string;

  constructor(
    private router: Router,
    private route: ActivatedRoute) { }

  ngOnInit() {
    
  }

  customAction(item) {
    if (item.customAction && this.customActionCallback) this.customActionCallback.emit(item);
  }

}
