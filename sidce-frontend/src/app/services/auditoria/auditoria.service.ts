import { Injectable } from '@angular/core';
import { MainService } from '../main.service';

@Injectable({
  providedIn: 'root',
})

export class AuditoriaService extends MainService {
  pesquisar(params) { return this.mainGet('/auditoria', params) }
  getFuncionalidades() { return this.mainGet('/auditoria/funcionalidades') }
  getEvento(evento) { return this.mainGet(`/auditoria/eventos`, { funcionalidade: evento }) }
}
