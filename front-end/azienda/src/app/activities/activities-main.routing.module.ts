import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LocalStorageModule } from '@ngx-pwa/local-storage';

import { ActivitiesMainComponent } from './activities-main.component';
import { ActivitiesComponent } from './tabs/activities.component';
import { ActivityComponent } from './activity/activity.component';
import { ActivityDetailComponent } from './detail/activity-details.component';
import { ActivityEvalutationComponent } from './valutazione/activity-evaluation.component';
import { ActivityEvalutationEditComponent } from './valutazione/activity-evaluation-edit.component';
import { AttivitaCancellaModal } from './modal/cancella/attivita-cancella-modal.component';
import { ActivitySimpleDiaryComponent } from './diary/activity-simple-diary.component';
import { GuideComponent } from './guida/attivita-guide.component';
import { IncorsoTabsComponent } from './incorso-tabs/incorso-tabs.component'
import { AttivitaIncorsoIndividualiComponent } from './incorso-individuali/activities-incorso-individuali.component'
import { AttivitaIncorsoGruppiComponent } from './incorso-gruppi/activities-incorso-gruppi.component'
import { ProssimaTabsComponent } from './prossima-tabs/prossima-tabs.component'
import { AttivitaProssimaIndividualiComponent } from './prossima-individuali/prossima-individuali.component'
import { AttivitaProssimaGruppiComponent } from './prossima-gruppi/activities-prossima-gruppi.component'
import { HistoryComponent } from './storica-tabs/activity-history.component';
import { HistoryIndividualiComponent } from './storica-individuali/activity-history-individuali.component';
import { HistoryGruppiComponent } from './storica-gruppi/activity-history-gruppi.component';
import { ActivityAlternanzaComponent } from './attivitaAlternanza/activityAlternanza.component';
import { NoteAziendaEditModalComponent } from './activity/modals/note-azienda-edit-modal/note-azienda-edit-modal.component';

const routes: Routes = [
    {
        path: '', component: ActivitiesMainComponent,
        children: [
            { path: 'tabs/incorso/individuali', component: ActivitiesComponent },
            { path: 'tabs/incorso/individuali/view/:id', component: ActivityComponent },
            { path: 'tabs/incorso/individuali/viewAA/:id', component: ActivityAlternanzaComponent },

            { path: 'tabs/incorso/gruppo', component: ActivitiesComponent},
            { path: 'tabs/incorso/gruppo/view/:id', component: ActivityComponent },
            { path: 'tabs/incorso/gruppo/viewAA/:id', component: ActivityAlternanzaComponent },

            { path: 'tabs/incorso/guida', component: GuideComponent },

        ]
    },
    { path: 'tabs/archivo', component: HistoryComponent },
    // { path: 'tabs/guida', component: GuideComponent },
    { path: '**', pathMatch: 'full', redirectTo: 'tabs/incorso/individuali' }
];

@NgModule({
    imports: [RouterModule.forChild(routes), LocalStorageModule],
    exports: [RouterModule],
    entryComponents: [NoteAziendaEditModalComponent]
})
export class ActivitiesMainRoutingModule {
    static components = [ActivitiesMainComponent, ActivitiesComponent, ActivityComponent, ActivityDetailComponent, ActivitySimpleDiaryComponent, ActivityEvalutationComponent, ActivityEvalutationEditComponent,
        GuideComponent, AttivitaCancellaModal, IncorsoTabsComponent, AttivitaIncorsoIndividualiComponent, AttivitaIncorsoGruppiComponent,
        ProssimaTabsComponent, AttivitaProssimaIndividualiComponent, AttivitaProssimaGruppiComponent,
        HistoryComponent, HistoryIndividualiComponent, HistoryGruppiComponent,
        ActivityAlternanzaComponent, NoteAziendaEditModalComponent];
}