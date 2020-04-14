import { Component, OnInit } from '@angular/core';
import { PianoAlternanza } from '../../../shared/classes/PianoAlternanza.class';
import { Competenza } from '../../../shared/classes/Competenza.class';
import { DataService } from '../../../core/services/data.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal/modal';
import { Router, ActivatedRoute } from '@angular/router';
import { GrowlerService, GrowlerMessageType } from '../../../core/growler/growler.service';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'cm-piano-modifica-tipologie',
  templateUrl: './piano-modifica-tipologie.component.html',
  styleUrls: ['./piano-modifica-tipologie.component.scss']
})
export class PianoModificaTipologieComponent implements OnInit {

  piano: PianoAlternanza;
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
      title: "Modifica tipologie attività"
    }
  ];


  attachedCompetenze: Competenza[]; //competenze already added to the piano
  tipologie;
  tipologia = "Tipologie";
  annoRiferimento: number = 3;
  monteOre: number;
  pianoTipologie;
  painoTipologieTerza: any = [];
  painoTipologieQuarto: any = [];
  painoTipologieQuinto: any = [];
  forceErrorDisplay: boolean;
  totale = {};
  removeTipologieUuidList = [];
  anni = [3, 4, 5];
  menuContent = "Qui puoi modificare le tipologie di attività associate al piano. Per aggiungerne una, seleziona la tipologia, l’anno di riferimento (3° 4° o 5°) e il monte ore, quindi usa il tasto blu per associarla. Per cancellarla premi sulla “x” rossa cerchiata.";
  showContent: boolean = false;
  evn = environment;

  constructor(private dataService: DataService,
    private modalService: NgbModal,
    private router: Router,
    private activeRoute: ActivatedRoute,
    private growler: GrowlerService) { }

  ngOnInit() {
    this.evn.modificationFlag=true;
    this.activeRoute.params.subscribe(params => {
      let id = params['id'];

      this.resetTotale();

      this.dataService.getPianoById(id).subscribe((piano: PianoAlternanza) => {
        this.piano = piano;
        this.attachedCompetenze = piano.competenze;
        this.dataService.getAttivitaTipologie().subscribe((res) => {
          this.tipologie = res;
          this.dataService.getPianoTipologie(id).subscribe((res) => {
            this.pianoTipologie = res;
            if (this.pianoTipologie[3])
              this.painoTipologieTerza = this.pianoTipologie[3];
            if (this.pianoTipologie[4])
              this.painoTipologieQuarto = this.pianoTipologie[4];
            if (this.pianoTipologie[5])
              this.painoTipologieQuinto = this.pianoTipologie[5];
            this.updateTotale();
          });

        });
      },
        (err: any) => console.log(err),
        () => console.log('get piano tipologie'));
    });
  }
  ngOnDestroy(){
    this.evn.modificationFlag=false;
  }

  getTipologia(tipologiaId) {
    if (this.tipologie) {
      return this.tipologie.find(data => data.id == tipologiaId);
    } else {
      return tipologiaId;
    }
  }

  updateTotale() {
    this.resetTotale();
    if (this.painoTipologieTerza != null) {
      for (let pt of this.painoTipologieTerza) {
        this.totale[3] = this.totale[3] + pt.monteOre;
      }
    }
    if (this.painoTipologieQuarto != null) {
      for (let pt of this.painoTipologieQuarto) {
        this.totale[4] = this.totale[4] + pt.monteOre;
      }
    }
    if (this.painoTipologieQuinto != null) {
      for (let pt of this.painoTipologieQuinto) {
        this.totale[5] = this.totale[5] + pt.monteOre;
      }
    } 

  }

  addNewTipologie() {
    let present = false;
    let oreSuperato = false;
    if (this.allValidated()) {
      this.forceErrorDisplay = false;
      switch (this.annoRiferimento + '') {
        case '3':
          this.painoTipologieTerza.forEach(element => {
            if (element.tipologia == this.tipologia) {
              present = true;
            }            
          })
          if ((this.totale[3] + this.monteOre) > this.piano.oreTerzoAnno) {
            oreSuperato = true;
          }
          if (!present && !oreSuperato) {
            this.painoTipologieTerza.push({ pianoAlternanzaId: this.piano.id, tipologia: this.tipologia, annoRiferimento: 3, monteOre: this.monteOre });
            this.updateTotale();
          } else if (present) {
            this.growler.growl("Tipologia già esistente nell'anno in corso", GrowlerMessageType.Warning);
          } else if (oreSuperato) {
            this.growler.growl("Ore superato monte ore terza", GrowlerMessageType.Warning);
          }
          break;
        case '4':
          this.painoTipologieQuarto.forEach(element => {
            if (element.tipologia == this.tipologia) {
              present = true;
            }            
          })
          if ((this.totale[4] + this.monteOre) > this.piano.oreQuartoAnno) {
            oreSuperato = true;            
          }
          if (!present && !oreSuperato) {
            this.painoTipologieQuarto.push({ pianoAlternanzaId: this.piano.id, tipologia: this.tipologia, annoRiferimento: 4, monteOre: this.monteOre });
            this.updateTotale();
          } else if (present) {
            this.growler.growl("Tipologia già esistente nell'anno in corso", GrowlerMessageType.Warning);
          } else if (oreSuperato) {
            this.growler.growl("Ore superato monte ore quarta", GrowlerMessageType.Warning);
          }         
          break;
        case '5':
          this.painoTipologieQuinto.forEach(element => {
            if (element.tipologia == this.tipologia) {
              present = true;
            }            
          })
          if ((this.totale[5] + this.monteOre) > this.piano.oreQuintoAnno) {
            oreSuperato = true;            
          }
          if (!present && !oreSuperato) {
            this.painoTipologieQuinto.push({ pianoAlternanzaId: this.piano.id, tipologia: this.tipologia, annoRiferimento: 5, monteOre: this.monteOre });
            this.updateTotale();
          } else if (present) {
            this.growler.growl("Tipologia già esistente nell'anno in corso", GrowlerMessageType.Warning);
          } else if (oreSuperato) {
            this.growler.growl("Ore superato monte ore quinta", GrowlerMessageType.Warning);
          }          
          break;
      }
      this.tipologia="Tipologie";
      this.monteOre=null;
    } else {
      this.forceErrorDisplay = true;
    }
  }

  removeTipologie(tipo) {
    switch (tipo.annoRiferimento + '') {
      case '3':
        this.painoTipologieTerza.splice(this.painoTipologieTerza.indexOf(tipo), 1);
        break;
      case '4':
        this.painoTipologieQuarto.splice(this.painoTipologieQuarto.indexOf(tipo), 1);
        break;
      case '5':
        this.painoTipologieQuinto.splice(this.painoTipologieQuinto.indexOf(tipo), 1);
        break;
    }

    if (tipo.uuid && this.removeTipologieUuidList.indexOf(tipo.uuid) == -1) {
      this.removeTipologieUuidList.push(tipo.uuid);
    }

    this.updateTotale();
  }

  allValidated() {
    return (
      ((this.monteOre && this.monteOre > 0)
        && (this.tipologia && this.tipologia != 'Tipologie')
      ));
  }

  save() {
    let saveTipo = {};
    saveTipo[3] = this.painoTipologieTerza;
    saveTipo[4] = this.painoTipologieQuarto;
    saveTipo[5] = this.painoTipologieQuinto;
    this.dataService.assignTipologieToPiano(saveTipo, this.piano.id).subscribe((res) => {
      this.piano = res;
      this.router.navigate(['../../'], { relativeTo: this.activeRoute });
    },
      (err: any) => console.log(err),
      () => console.log('get piano tipologie'));

  }

  resetTotale() {
    this.totale[3] = 0;
    this.totale[4] = 0;
    this.totale[5] = 0;
  }

  menuContentShow() {
    this.showContent = !this.showContent;
  }

}
