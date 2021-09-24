import { Component, OnInit, OnDestroy, ElementRef, ViewChild, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';
import { DataService } from '../services/data.service';
import { AuthService } from '../../core/auth/auth.service';
import { environment } from '../../../environments/environment';

@Component({
    selector: 'cm-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit, OnDestroy {

    isCollapsed: boolean;
    loginLogoutText: string = 'Login';
    sub: Subscription;
    isSticky: boolean = false;
    profile;
    aziende;
    actualAzienda;
    profileDropdownVisible;
    env = environment;
  
    @Output() isStickyListener = new EventEmitter<boolean>();

    constructor(private router: Router, public dataService: DataService, private authService: AuthService, private _eref: ElementRef) { }

    ngOnInit() {}
    
    initProfile() {
        this.dataService.getProfile().subscribe(profile => {
            if (profile) {
                this.profile = profile;
                if (profile && profile.aziende) {
                    this.dataService.setAziendaId('Fondazione Bruno Kessler');
                    this.dataService.setAziendaName('Fondazione Bruno Kessler');
                }
            }
        })
    }

    ngOnDestroy() {
        this.sub.unsubscribe();
    }

    loginOrOut() {}

    logout() {
        this.authService.logout();
    }

    redirectToLogin() {
        this.router.navigate(['/login']);
    }

    setLoginLogoutText() {
    }

    isActive(location) {
        if (this.router.url.includes(location)) {
            return true;
        }
    }

}