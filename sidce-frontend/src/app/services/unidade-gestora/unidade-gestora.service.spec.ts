import { TestBed } from '@angular/core/testing';

import { UnidadeGestoraService } from './unidade-gestora.service';

describe('UnidadeGestoraService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: UnidadeGestoraService = TestBed.get(UnidadeGestoraService);
    expect(service).toBeTruthy();
  });
});
