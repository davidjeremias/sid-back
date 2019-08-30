import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import axios, {AxiosRequestConfig} from 'axios';
import { NotificationsService } from '../../components/notifications/notifications.service';

@Injectable({
  providedIn: 'root'
})
export class LancamentosService {

  constructor(private messageService: NotificationsService) { }

  buscar(parametros?: Object): Observable<any> {
    const params: AxiosRequestConfig = parametros;
    return Observable.create(observer => {
      axios.get('/codigoLancamento/buscar', {params})
        .then(function (response) {
          observer.next(response);
          observer.complete();
        })
        .catch(error => {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Algum erro inesperado aconteceu!' });
        });
    });
  }

  buscaLancamentosSemCodigo(): Observable<any> {
    return Observable.create(observer => {
      axios.get('/codigoLancamento/buscar')
        .then(function (response) {
          observer.next(response);
          observer.complete();
        })
        .catch(error => {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Algum erro inesperado aconteceu!' });
        });
    });
  }

  salvar(parametros?: Object): Observable<any> {
    return Observable.create(observer => {
      axios.post('/codigoLancamento/salvar', parametros)
        .then(function (response) {
          observer.next(response);
          observer.complete();
        })
        .catch(error => {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Algum erro inesperado aconteceu!' });
        });
    });
  }

  salvarAll(parametros?: Object): Observable<any> {
    return Observable.create(observer => {
      axios.post('/codigoLancamento/salvarAll', parametros)
        .then(function (response) {
          observer.next(response);
          observer.complete();
        })
        .catch(error => {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Algum erro inesperado aconteceu!' });
        });
    });
  }
}
