import { TestBed } from '@angular/core/testing';

import { ParametrosIntegracaoService } from './parametros-integracao.service';

describe('ParametrosIntegracaoService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ParametrosIntegracaoService = TestBed.get(ParametrosIntegracaoService);
    expect(service).toBeTruthy();
  });
});
