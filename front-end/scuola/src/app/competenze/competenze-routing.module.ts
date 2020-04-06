import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { CompetenzeComponent } from './competenze.component';
import { CompetenzeContainerComponent } from './competenze-container.component'
import { ModificaCompetenzaModalComponent } from './actions/modifica-competenza-modal/modifica-competenza-modal.component';
import { CompetenzaDettaglioComponent } from './actions/competenza-dettaglio/competenza-dettaglio.component';
import { CreaCompetenzaModalComponent } from './actions/create-competenza-modal/crea-competenza-modal.component'
import { CompetenzaModificaDatiComponent } from './actions/competenza-modifica-dati/competenza-modifica-dati.component'
import { CompetenzaModificaAbilitaComponent } from './actions/competenza-modifica-abilita/competenza-modifica-abilita.component'
import { CompetenzaModificaConoscenzeComponent } from './actions/competenza-modifica-conoscenze/competenza-modifica-conoscenze.component'
import { CompetenzaCancellaModal } from './actions/cancella-competenza-modal/competenza-cancella-modal.component'

const routes: Routes = [
  {
    path: '', component: CompetenzeContainerComponent,
    children: [
      { path: 'list', component: CompetenzeComponent },
      { path: 'list/detail/:id', component: CompetenzaDettaglioComponent },
      { path: 'list/detail/:id/modifica/dati', component: CompetenzaModificaDatiComponent},
      { path: 'list/detail/:id/modifica/abilita', component: CompetenzaModificaAbilitaComponent},
      { path: 'list/detail/:id/modifica/conoscenze', component: CompetenzaModificaConoscenzeComponent},
      { path: '**', pathMatch: 'full', redirectTo: 'list' }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CompetenzeRoutingModule {
  static components = [
    CompetenzeContainerComponent,
    CompetenzeComponent,
    CompetenzaDettaglioComponent,
    CreaCompetenzaModalComponent,
    ModificaCompetenzaModalComponent,
    CompetenzaModificaDatiComponent,
    CompetenzaModificaAbilitaComponent,
    CompetenzaModificaConoscenzeComponent,
    CompetenzaCancellaModal
  ];
}




