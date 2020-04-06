import { Component, OnInit, OnDestroy, HostListener, ElementRef, ViewChild, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';
import { AuthenticationService } from '../services/authentication.service';
import { GrowlerService, GrowlerMessageType } from '../growler/growler.service';
import { DataService } from '../services/data.service';
import { Studente } from '../../shared/interfaces';

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
    actualStudente;
    isSticky: boolean = false;
    istitutoName = "";
    profile;
    profileDropdownVisible;
    @ViewChild('navbar') private navbarToStick;
    @ViewChild('navbarHeaderTop') private navbarHeaderTop;

    @Output() isStickyListener = new EventEmitter<boolean>();

    constructor(private router: Router, private growler: GrowlerService, private dataService: DataService, private authService: AuthenticationService) { }

    ngOnInit() {
        this.dataService.getProfile().subscribe(profile => {
            //set profile ID
            console.log(profile)
            if (profile) {
                this.profile = profile;
                // sessionStorage.setItem('access_token', profile.token)
                if (profile && profile.studenti) {
                    // get ids(keys of map).
                    var ids = [];
                    for (var k in profile.studenti) {
                        ids.push(k);
                    } 
                    this.dataService.getListaStudentiByIds(ids).subscribe((studenti: Array<Studente>) => {
                        if (studenti) {
                            this.studenti = studenti;
                            this.dataService.setStudenteId(this.studenti[0].id);
                            this.getIstitutoName(this.studenti[0].istitutoId);
                            this.actualStudente = this.studenti[0];
                            this.dataService.setListId(this.studenti);
                        }
                    }
                    )
                }
            }
        }, err => {
            //todo
            console.log('error, no institute')
        });

    }

    onStudenteChange(studente) {
        this.actualStudente = studente;
        this.dataService.setStudenteId(studente.id);
        this.getIstitutoName(studente.istitutoId);
        this.router.navigate(['/home']);

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