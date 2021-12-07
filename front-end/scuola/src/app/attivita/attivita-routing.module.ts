import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AttivitaComponent } from './attivita.component';
import { AttivitaContainerComponent } from './attivita-container.component';
import { AttivitaDettaglioComponent } from './actions/attivita-dettaglio/attivita-dettaglio.component';
import { AttivitaModificaCompetenzeComponent } from './actions/attivita-modifica-competenze/attivita-modifica-competenze.component';
import { AttivitaDettaglioModificaComponent } from './actions/attivita-modifica-dettaglio/attivita-modifica.component';
import { NewAttivtaModalPrimo } from './actions/new-attivita-modal-primo/new-attivita-modal-primo.component';
import { NewAttivtaModal } from './actions/new-attivita-modal/new-attivita-modal.component';
import { CreaAttivitaModalComponent } from './actions/crea-attivita-modal/crea-attivita-modal.component';
import { DocumentUploadModalComponent } from './actions/documento-upload-modal/document-upload-modal.component';
import { DocumentoCancellaModal } from './actions/documento-cancella-modal/documento-cancella-modal.component';
import { AttivitaModificaStudentiComponent } from './actions/attivita-modifica-studenti/attivita-modifica-studenti.component';
import { AttivitaCancellaModal } from './actions/cancella-attivita-modal/attivita-cancella-modal.component';
import { GestionePresenzeIndividualeComponent } from './actions/gestione-presenze-individuale/gestione-preseneze-individuale.component';
import { GestionePresenzeGruppoComponent } from './actions/gestione-presenze-gruppo/gestione-preseneze-gruppo.component';
import { ArchiaviazioneAttivitaModal } from './actions/archiaviazione-attivita-modal/archiaviazione-attivita.component';
import { AssociaOffertaComponent } from './actions/associa-offerta/associa-offerta.component';
import { ModificaOreStudentiComponent } from'./actions/modifica-ore-studenti/modifica-ore-studenti';
import { AvvisoEnteConvenzioneModal } from './actions/avviso-ente-convenzione-modal/avviso-ente-convenzione-modal.component';
import { ValutazioneEsperienzaTirocinioComponent } from './actions/valutazione-esperienza-tirocinio/valutazione-esperienza-tirocinio.component';
import { ValutazioneCompetenzeTirocinioComponent } from './actions/valutazione-competenze-tirocinio/valutazione-competenze-tirocinio.component';
import { AvvisoArchiviaTiroModal } from './actions/avviso-archivia-tiro-modal/avviso-archivia-tiro-modal.component';

const routes: Routes = [
  {
    path: '', component: AttivitaContainerComponent,
    children: [
      { path: 'list', component: AttivitaComponent },
      { path: 'list/:refresh', component: AttivitaComponent },
      { path: 'list/associa/offerta/:modalita', component: AssociaOffertaComponent },
      { path: 'detail/:id', component: AttivitaDettaglioComponent },
      { path: 'detail/:id/modifica/attivita', component: AttivitaDettaglioModificaComponent },
      { path: 'detail/:id/modifica/studenti', component: AttivitaModificaStudentiComponent},
      { path: 'detail/:id/modifica/competenze', component: AttivitaModificaCompetenzeComponent },
      { path: 'detail/:id/modifica/studenti/presenze/individuale', component: GestionePresenzeIndividualeComponent },
      { path: 'detail/:id/modifica/studenti/presenze/gruppo', component: GestionePresenzeGruppoComponent },
      { path: 'detail/:id/modifica/studenti/ore', component: ModificaOreStudentiComponent },
      { path: 'detail/:id/valutazione/esperienza', component: ValutazioneEsperienzaTirocinioComponent },
      { path: 'detail/:id/valutazione/competenze', component: ValutazioneCompetenzeTirocinioComponent },
      { path: '**', pathMatch:'full', redirectTo: 'list' }
    ]
  }
];

@NgModule({
  imports: [ RouterModule.forChild(routes) ],
  exports: [ RouterModule ]
})
export class AttivitaRoutingModule { 
  static components = [
    AttivitaContainerComponent,
    AttivitaComponent,
    NewAttivtaModalPrimo,
    NewAttivtaModal,
    CreaAttivitaModalComponent,
    AttivitaDettaglioComponent,
    DocumentUploadModalComponent,
    DocumentoCancellaModal,
    AttivitaModificaCompetenzeComponent,
    AttivitaModificaStudentiComponent,
    AttivitaCancellaModal,
    AttivitaDettaglioModificaComponent,
    GestionePresenzeIndividualeComponent,
    GestionePresenzeGruppoComponent,
    ArchiaviazioneAttivitaModal,
    AssociaOffertaComponent,
    ModificaOreStudentiComponent,
    AvvisoEnteConvenzioneModal,
    ValutazioneEsperienzaTirocinioComponent,
    ValutazioneCompetenzeTirocinioComponent,
    AvvisoArchiviaTiroModal
  ];
}


