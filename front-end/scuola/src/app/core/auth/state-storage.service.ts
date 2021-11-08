import { Injectable } from '@angular/core';

@Injectable(
  { providedIn: 'root' }
  )
export class StateStorageService {
  private previousUrlKey = 'previousUrl';
  private filtroAttivitaKey = 'filtroAttivita';
  private filtroOffertaKey = 'filtroOfferte';

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
  
  storefiltroOfferta(localValue: string): void {
    sessionStorage.setItem(this.filtroOffertaKey, localValue);
  }

  getfiltroOfferta(): string | null | undefined {
    return sessionStorage.getItem(this.filtroOffertaKey);
  }

  clearAll() {
    sessionStorage.removeItem(this.previousUrlKey);
    sessionStorage.removeItem(this.filtroAttivitaKey);
    sessionStorage.removeItem(this.filtroOffertaKey);
  }
  
}
