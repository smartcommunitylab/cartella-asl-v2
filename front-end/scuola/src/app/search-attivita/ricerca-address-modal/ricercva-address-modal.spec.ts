import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProgrammaRicercaAddressModal } from './ricerca-address-modal.component';

describe('SearchAttivitaComponent', () => {
  let component: ProgrammaRicercaAddressModal;
  let fixture: ComponentFixture<ProgrammaRicercaAddressModal>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProgrammaRicercaAddressModal ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgrammaRicercaAddressModal);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
