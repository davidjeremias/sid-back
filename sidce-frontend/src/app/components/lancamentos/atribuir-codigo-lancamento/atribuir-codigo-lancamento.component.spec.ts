import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AtribuirCodigoLancamentoComponent } from './atribuir-codigo-lancamento.component';

describe('AtribuirCodigoLancamentoComponent', () => {
  let component: AtribuirCodigoLancamentoComponent;
  let fixture: ComponentFixture<AtribuirCodigoLancamentoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AtribuirCodigoLancamentoComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AtribuirCodigoLancamentoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
