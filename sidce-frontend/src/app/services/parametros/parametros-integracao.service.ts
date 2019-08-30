import { NotificationsService } from 'src/app/components/notifications/notifications.service';
import { Observable } from 'rxjs';
import { MainService } from './../main.service';
import { Injectable } from '@angular/core';
import axios, {AxiosRequestConfig} from 'axios';

@Injectable({
  providedIn: 'root'
})
export class ParametrosIntegracaoService{

  constructor(private messageService: NotificationsService) { }

  salvar(data: Object): Observable<any> {
    return Observable.create(observer => {
      axios.post('/parametrosIntegracao/salvar', data)
        .then(function (response) {
          observer.next(response);
          observer.complete();
        })
        .catch(error => {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Algum erro inesperado aconteceu!' });
        });
    });
  }

  alterar(data: Object): Observable<any> {
    return Observable.create(observer => {
      axios.put('/parametrosIntegracao/alterar', data)
        .then(function (response) {
          observer.next(response);
          observer.complete();
        })
        .catch(error => {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Algum erro inesperado aconteceu!' });
        });
    });
  }

  pesquisar(): Observable<any> {
    return Observable.create(observer => {
      axios.get('/parametrosIntegracao/buscar')
        .then(function (response) {
          observer.next(response);
          observer.complete();
        })
        .catch(error => {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Algum erro inesperado aconteceu!' });
        });
    });
  }

  verificaLink(url: Object): Observable<any> {
    const params: AxiosRequestConfig = url;
    return Observable.create(observer => {
      axios.get('/parametrosIntegracao/verificaLink', {params})
        .then(function (response) {
          observer.next(response);
          observer.complete();
        })
        .catch(error => {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Algum erro inesperado aconteceu!' });
        });
    });
  }

  pesquisarlogIntegracao(obj: Object): Observable<any> {
    const params: AxiosRequestConfig = obj;
    return Observable.create(observer => {
      axios.get('/auditoriaIntegracaoTSE/buscar', {params})
        .then(function (response) {
          observer.next(response);
          observer.complete();
        })
        .catch(error => {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Algum erro inesperado aconteceu!' });
        });
    });
  }

  buscaParametrosAtual(id: any): Observable<any> {
    return Observable.create(observer => {
      axios.get(`/auditoriaIntegracaoTSE/${id}/buscaParametrosAtual`)
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
