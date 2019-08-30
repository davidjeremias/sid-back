import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UnidadeGestoraComponent } from './unidade-gestora.component';

describe('UnidadeGestoraComponent', () => {
  let component: UnidadeGestoraComponent;
  let fixture: ComponentFixture<UnidadeGestoraComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UnidadeGestoraComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UnidadeGestoraComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
