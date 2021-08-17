import { Component, OnInit, OnDestroy, HostListener, ElementRef, ViewChild, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';
import { GrowlerService, GrowlerMessageType } from '../growler/growler.service';
import { DataService } from '../services/data.service';
import { PermissionService } from '../services/permission.service';
import { AuthService } from '../auth/auth.service';

@Component({
    selector: 'cm-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit, OnDestroy {

    isCollapsed: boolean = true;
    loginLogoutText: string = 'Login';
    sub: Subscription;
    studenti = [];
    actualUser;
    isSticky: boolean = false;
    istitutoName = "";
    profile;
    profileDropdownVisible;
    @ViewChild('navbar') private navbarToStick;
    @ViewChild('navbarHeaderTop') private navbarHeaderTop;

    @Output() isStickyListener = new EventEmitter<boolean>();

    constructor(private router: Router, private growler: GrowlerService, private dataService: DataService, public permissionService: PermissionService, public authService: AuthService) { }

    ngOnInit() {
        this.dataService.getProfile().subscribe(profile => {
            console.log(profile)
            if (profile) {
                this.profile = profile;
            }
        }, err => {
            //todo
            console.log('error, no institute')
        });

    }

    getIstitutoName(id) {
        this.dataService.getIstitutoById(id).subscribe(istituto => {
            this.istitutoName = istituto.name;
        })
    }
    ngOnDestroy() {
        this.sub.unsubscribe();
    }

    loginOrOut() {

    }

    logout() {
        this.authService.logout();
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