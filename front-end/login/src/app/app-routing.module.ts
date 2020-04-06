import { NgModule } from '@angular/core';
import { RouterModule, Routes, PreloadAllModules, NoPreloading } from '@angular/router';
import { PreloadModulesStrategy } from './core/strategies/preload-modules.strategy';
import { HashLocationStrategy, LocationStrategy } from '@angular/common';

const app_routes: Routes = [
    { path: 'home', loadChildren: 'app/home/home.module#HomeModule' },
    { path: '**', pathMatch: 'full', redirectTo: '/home' }
];

@NgModule({
  imports: [RouterModule.forRoot(app_routes, { useHash: true, preloadingStrategy: PreloadAllModules })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
