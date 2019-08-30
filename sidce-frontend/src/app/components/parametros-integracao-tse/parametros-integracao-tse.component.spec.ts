import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ParametrosIntegracaoTseComponent } from './parametros-integracao-tse.component';

describe('ParametrosIntegracaoTseComponent', () => {
  let component: ParametrosIntegracaoTseComponent;
  let fixture: ComponentFixture<ParametrosIntegracaoTseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ParametrosIntegracaoTseComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ParametrosIntegracaoTseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
