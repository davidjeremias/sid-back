import { TestBed } from '@angular/core/testing';

import { ConsultarArquivosService } from './consultar-arquivos.service';

describe('ConsultarArquivosService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ConsultarArquivosService = TestBed.get(ConsultarArquivosService);
    expect(service).toBeTruthy();
  });
});
