import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VisualizarArquivosComponent } from './visualizar-arquivos.component';

describe('VisualizarArquivosComponent', () => {
  let component: VisualizarArquivosComponent;
  let fixture: ComponentFixture<VisualizarArquivosComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VisualizarArquivosComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VisualizarArquivosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
