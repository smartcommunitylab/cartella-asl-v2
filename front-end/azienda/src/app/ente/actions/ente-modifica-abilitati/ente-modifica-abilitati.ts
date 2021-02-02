import { Component, OnInit } from '@angular/core';
import { DataService } from '../../../core/services/data.service';
import { Router, ActivatedRoute } from '@angular/router';


@Component({
  selector: 'cm-ente-modifica-abilitati',
  templateUrl: './ente-modifica-abilitati.html',
  styleUrls: ['./ente-modifica-abilitati.scss']
})
export class EnteModificaAbilitatiComponent implements OnInit {

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private dataService: DataService) { }

  ruoli;
  ente;
  menuContent = "In questa pagina trovi tutte le informazioni relative all’attività che stai visualizzando.";
  showContent: boolean = false;

  breadcrumbItems = [
    {
      title: "Scheda ente",
      location: "../../",
    },
    {
      title: "Modifica account abilitati"
    }
  ];

  ngOnInit() {

  this.dataService.getAzienda().subscribe((res) => {
      this.ente = res;
      this.dataService.getRuoliByEnte().subscribe((res) => {
        this.ruoli = res;
      },
        (err: any) => console.log(err),
        () => console.log('getRuoliByEnte'));
    }, (err: any) => console.log(err),
      () => console.log('getAzienda'));
  }

  cancel() {
    this.router.navigate(['../../'], { relativeTo: this.route });
  }

  save() {
  }

 
  menuContentShow() {
    this.showContent = !this.showContent;
  }

  deleteRuolo(ruolo){
    this.dataService.cancellaRuoloReferenteAzienda(ruolo.id).subscribe(res=>{
      this.dataService.getRuoliByEnte().subscribe((res) => {
        this.ruoli = res;
      },
        (err: any) => console.log(err),
        () => console.log('getRuoliByEnte'));
    });
  }
 
}
