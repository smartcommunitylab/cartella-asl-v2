import { Injectable } from '@angular/core';

@Injectable(
  { providedIn: 'root' }
  )
export class StateStorageService {
  private previousUrlKey = 'previousUrl';
  private filtroAttivitaKey = 'filtroAttivita';
  private filtroStudenteKey = 'filtroStudente';

  constructor() {}

  storeUrl(url: string): void {
    sessionStorage.setItem(this.previousUrlKey, url);
  }

  getUrl(): string | null | undefined {
    return sessionStorage.getItem(this.previousUrlKey);
  }

  clearUrl(): void {
    sessionStorage.removeItem(this.previousUrlKey);
  }

  storefiltroAttivita(localValue: string): void {
    sessionStorage.setItem(this.filtroAttivitaKey, localValue);
  }

  getfiltroAttivita(): string | null | undefined {
    return sessionStorage.getItem(this.filtroAttivitaKey);
  }
  
  storefiltroStudente(localValue: string): void {
    sessionStorage.setItem(this.filtroStudenteKey, localValue);
  }

  getfiltroStudente(): string | null | undefined {
    return sessionStorage.getItem(this.filtroStudenteKey);
  }

  clearAll() {
    sessionStorage.removeItem(this.previousUrlKey);
    sessionStorage.removeItem(this.filtroAttivitaKey);
    sessionStorage.removeItem(this.filtroStudenteKey);
  }
  
}
