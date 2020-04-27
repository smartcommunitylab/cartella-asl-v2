import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { PianoAlternanza } from '../../../shared/classes/PianoAlternanza.class';
import { DataService } from '../../../core/services/data.service';
import { Router, ActivatedRoute } from '@angular/router';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'cm-piano-modifica-dati',
  templateUrl: './piano-modifica-dati.component.html',
  styleUrls: ['./piano-modifica-dati.component.css']
})
export class PianoModificaDatiComponent implements OnInit {

  piano;
  breadcrumbItems = [
    {
      title: "Lista piani",
      location: "../../../"
    },
    {
      title: "Dettaglio piano",
      location: "../../"
    },
    {
      title: "Modifica dati piano"
    }
  ];
  menuContent = "In questa pagina trovi tutte le informazioni relative all’attività che stai visualizzando.";
  showContent: boolean = false;
  annoRiferimento: any;
  fieldsError: string;
  evn = environment;
  
  corsiStudio;
  @Output() editPianoListener = new EventEmitter<Object>();
  forceErrorDisplay: boolean;
  forceErrorDisplayTitolo: boolean = false;

  constructor(private dataService: DataService,
    private router: Router,
    private activeRoute: ActivatedRoute) { }

  ngOnInit() {
    this.evn.modificationFlag=true;
    this.activeRoute.params.subscribe(params => {
      let id = params['id'];
      this.dataService.getPianoById(id).subscribe((piano: PianoAlternanza) => {
        this.piano = piano;
      },
        (err: any) => console.log(err),
        () => console.log('get piano tipologie'));
    });
    this.dataService.getCorsiStudio().subscribe((response) => {
      this.corsiStudio = response;
    })
     
  }
  ngOnDestroy(){
    this.evn.modificationFlag=false;
  }
  update() { //update
    
    if (this.allValidated()) {
      this.piano.titolo = this.piano.titolo.trim();
      (this.piano.note)?this.piano.note=this.piano.note.trim(): null;
      this.piano.dataAttivazione = null;
      this.piano.dataCreazione = null;
      this.piano.dataDisattivazione = null;
      this.piano.dataScadenza = null;
      this.dataService.updatePianoDetails(this.piano).subscribe((pianoUpdated) => {
        this.router.navigate(['../../'], { relativeTo: this.activeRoute });
      });
    } else {
      this.forceErrorDisplay = true;
    }    
  }


  allValidated() {
    return (
      (this.piano.titolo && this.piano.titolo != '' && this.piano.titolo.trim().length > 0)
      && (this.piano.oreTerzoAnno && this.piano.oreTerzoAnno > 0)
      && (this.piano.oreQuartoAnno && this.piano.oreQuartoAnno > 0)
      && (this.piano.oreQuintoAnno && this.piano.oreQuintoAnno > 0)
      && (this.piano.corsoDiStudioId && this.piano.corsoDiStudioId != 'Corso di studio')    
      );
  }

  
  trimValue(event, type) {  
    if(type == 'titolo'){
      (event.target.value.trim().length == 0)? this.forceErrorDisplayTitolo = true : this.forceErrorDisplayTitolo = false;
    } else if(type == 'trim'){
      event.target.value = event.target.value.trim(); 
    }
  }

  menuContentShow() {
    this.showContent = !this.showContent;
  }

}
