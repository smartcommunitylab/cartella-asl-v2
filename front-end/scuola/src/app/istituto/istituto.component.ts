import { Component, OnInit } from '@angular/core';
import { DataService } from '../core/services/data.service'
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Location } from '@angular/common';
import { environment } from '../../environments/environment';

@Component({
    selector: 'istituto',
    templateUrl: './istituto.component.html',
    styleUrls: ['./istituto.component.scss']
})

export class IstitutoComponent implements OnInit {
    filtro;
    filterSearch = false;
    closeResult: string;
    menuContent = "In questa sezione trovi una dashboard che mostra un riassunto visivo delle attività di tutti gli studenti. Inoltre, da qui puoi gestire gli account abilitati sul Portale EDIT Istituto di questo istituto e i dati principali dell’istituto.";
    showContent: boolean = false;
    timeoutTooltip = 250;
    env = environment;
    istituto;

    constructor(
        private dataService: DataService,
        private route: ActivatedRoute,
        private router: Router,
        private location: Location,
        private modalService: NgbModal
    ) {

    }

    ngOnInit(): void {
        this.getIstituto();
    }

    getIstituto() {
        this.dataService.getIstitutoById(this.dataService.istitutoId)
            .subscribe((response) => {
                this.istituto = response;
            },
                (err: any) => console.log(err),
                () => console.log('get istituto api'));
    }

    menuContentShow() {
        this.showContent = !this.showContent;
    }

    modifica() {
        this.router.navigate(['../modifica', this.dataService.istitutoId], { relativeTo: this.route });
      }

}