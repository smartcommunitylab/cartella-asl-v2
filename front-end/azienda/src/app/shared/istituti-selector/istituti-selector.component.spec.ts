import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { IstitutiSelectorComponent } from './istituti-selector.component';

describe('SkillsSelectorComponent', () => {
  let component: IstitutiSelectorComponent;
  let fixture: ComponentFixture<IstitutiSelectorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ IstitutiSelectorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(IstitutiSelectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
