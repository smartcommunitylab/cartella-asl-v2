import { Injectable } from '@angular/core';

@Injectable(
  { providedIn: 'root' }
  )
export class StateStorageService {
  private previousUrlKey = 'previousUrl';

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
}
