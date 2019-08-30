import { Injectable } from '@angular/core';
import { MainService } from './main.service';

@Injectable({
  providedIn: 'root'
})
export class ParametrosService extends MainService {
  findAll() { return this.mainGet('/parametros') }
  salvar(item) { return this.mainPut('/parametros', item)}
}
