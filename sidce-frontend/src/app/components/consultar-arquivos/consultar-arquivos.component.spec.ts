import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsultarArquivosComponent } from './consultar-arquivos.component';

describe('ConsultarArquivosComponent', () => {
  let component: ConsultarArquivosComponent;
  let fixture: ComponentFixture<ConsultarArquivosComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConsultarArquivosComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConsultarArquivosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
