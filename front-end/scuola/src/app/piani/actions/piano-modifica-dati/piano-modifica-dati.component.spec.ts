import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PianoModificaDatiComponent } from './piano-modifica-dati.component';

describe('PianoModificaDatiComponent', () => {
  let component: PianoModificaDatiComponent;
  let fixture: ComponentFixture<PianoModificaDatiComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PianoModificaDatiComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PianoModificaDatiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
