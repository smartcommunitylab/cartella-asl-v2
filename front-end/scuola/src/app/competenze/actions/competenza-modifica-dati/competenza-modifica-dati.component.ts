import { Component, OnInit } from '@angular/core';
import { Competenza } from '../../../shared/classes/Competenza.class';
import { DataService } from '../../../core/services/data.service';
import { Router, ActivatedRoute } from '@angular/router';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'cm-competenza-modifica-dati',
  templateUrl: './competenza-modifica-dati.component.html',
  styleUrls: ['./competenza-modifica-dati.component.css']
})
export class CompetenzaModificaDatiComponent implements OnInit {

  breadcrumbItems = [
    {
      title: "Lista competenze",
      location: "../../../"
    },
    {
      title: "Dettaglio competenza",
      location: "../../"
    },
    {
      title: "Modifica dati competenza"
    }
  ];

  forceErrorDisplay: boolean;
  forceErrorDisplayTitolo: boolean = false;
  competenza: Competenza;
  numeroAttivitaAltAssociate = 0;
  menuContent = "Raccolta di tutte le competenze sia ministeriali che di quelle inserite dalla scuola stessa. Le competenze inserite dalla scuola sono visibili solo ad essa e non condivise con altri istituti.";
  showContent: boolean = false;
  arrEQF=[1,2,3,4,5,6,7,8];
  evn = environment;

  constructor(private dataService: DataService,
    private router: Router,
    private activeRoute: ActivatedRoute) { }

  ngOnInit() {
    this.evn.modificationFlag=true;
    this.activeRoute.params.subscribe(params => {
      let id = params['id'];

      this.dataService.getIstiutoCompetenzaDetail(id).subscribe(response => {
        this.competenza = response.competenza;
        this.numeroAttivitaAltAssociate = response.numeroAttivitaAltAssociate;
      },
        (err: any) => console.log(err),
        () => console.log('get Istiuto competenza detail'));
    });
  }

  ngOnDestroy(){
    this.evn.modificationFlag=false;
  }

  // modify competenza
  saveEdit() { //create or update
    if (this.allValidated()) {
      this.competenza.titolo = this.competenza.titolo.trim();
      this.dataService.updateIstitutoCompetenza(this.competenza).subscribe((res) => {
        this.router.navigate(['../../'], { relativeTo: this.activeRoute });
      });
    } else {
      this.forceErrorDisplay = true;
    }
  }

  allValidated() {
    return (
      (this.competenza.titolo && this.competenza.titolo != '' && this.competenza.titolo.trim().length > 0)
      && (this.competenza.livelloEQF || this.competenza.livelloEQF != null)
    );
  }
  trimValue(event, type) { 
    if(type == 'titolo'){
      (event.target.value.trim().length == 0)? this.forceErrorDisplayTitolo = true : this.forceErrorDisplayTitolo = false;
    } else if(type == 'trim'){
      event.target.value = event.target.value.trim(); 
    }
  }
  onChange(eqf){
    this.competenza.livelloEQF= eqf;
  }
  menuContentShow() {
    this.showContent = !this.showContent;
  }
}
