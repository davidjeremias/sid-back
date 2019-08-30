import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DetalharLogIntegracaoComponent } from './detalhar-log-integracao.component';

describe('DetalharLogIntegracaoComponent', () => {
  let component: DetalharLogIntegracaoComponent;
  let fixture: ComponentFixture<DetalharLogIntegracaoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DetalharLogIntegracaoComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DetalharLogIntegracaoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
