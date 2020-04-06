import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CompetenzaModificaDatiComponent } from './competenza-modifica-dati.component';

describe('CompetenzaModificaDatiComponent', () => {
  let component: CompetenzaModificaDatiComponent;
  let fixture: ComponentFixture<CompetenzaModificaDatiComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CompetenzaModificaDatiComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CompetenzaModificaDatiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
