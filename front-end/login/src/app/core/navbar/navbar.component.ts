import { Component, OnInit, OnDestroy, HostListener, ElementRef, ViewChild, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';

import { Subscription } from 'rxjs/Subscription';

// import { AuthService } from '../services/auth.service';
import { GrowlerService, GrowlerMessageType } from '../growler/growler.service';

@Component({
    selector: 'cm-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit, OnDestroy {

    isCollapsed: boolean = true;
    loginLogoutText: string = 'Login';
    sub: Subscription;

    isSticky:boolean = false;
    @ViewChild('navbar') private navbarToStick;
    @ViewChild('navbarHeaderTop') private navbarHeaderTop;

    @Output() isStickyListener = new EventEmitter<boolean>();

    constructor(private router: Router,  private growler: GrowlerService) { }

    ngOnInit() { 

    }

    ngOnDestroy() {
        this.sub.unsubscribe();
    }

    loginOrOut() {

    }
    
    redirectToLogin() {
        this.router.navigate(['/login']);
    }

    setLoginLogoutText() {
    }

    @HostListener("window:scroll", [])
    onWindowScroll() {        
        var sticky = this.navbarHeaderTop.nativeElement.clientHeight;
        if (this.isSticky != (window.pageYOffset >= sticky)) {
            this.isSticky = window.pageYOffset >= sticky;
            this.isStickyListener.emit(this.isSticky);
        }
    }

}