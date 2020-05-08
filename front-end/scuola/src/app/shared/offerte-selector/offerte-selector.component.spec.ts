import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { OfferteSelectorComponent } from './offerte-selector.component';

describe('OfferteSelectorComponent', () => {
  let component: OfferteSelectorComponent;
  let fixture: ComponentFixture<OfferteSelectorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OfferteSelectorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OfferteSelectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
