import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AlterarCodigoLancamentoComponent } from './alterar-codigo-lancamento.component';

describe('AlterarCodigoLancamentoComponent', () => {
  let component: AlterarCodigoLancamentoComponent;
  let fixture: ComponentFixture<AlterarCodigoLancamentoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AlterarCodigoLancamentoComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AlterarCodigoLancamentoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
