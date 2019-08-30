import { Injectable } from '@angular/core';
import axios, {AxiosRequestConfig} from 'axios';
import { Observable } from 'rxjs';
import { NotificationsService } from '../../components/notifications/notifications.service';

@Injectable({
  providedIn: 'root'
})
export class UnidadeGestoraService {

  constructor(private messageService: NotificationsService) { }

  buscaUnidadeAtual(): Observable<any> {
    return Observable.create(observer => {
      axios.get('/unidadeGestora')
        .then(function (response) {
          observer.next(response);
          observer.complete();
        })
        .catch(error => {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Algum erro inesperado aconteceu!' });
        });
    });
  }

  buscaUnidades(parametros?: Object): Observable<any> {
    const params: AxiosRequestConfig = parametros;
    return Observable.create(observer => {
      axios.get('/unidadeGestora/unidadeSIICO', {params})
        .then(function (response) {
          observer.next(response);
          observer.complete();
        })
        .catch(error => {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Algum erro inesperado aconteceu!' });
        });
    });
  }

  alterarUnidade(unidade: Object): Observable<any> {
    return Observable.create(observer => {
      axios.put('/unidadeGestora/salvar', unidade)
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
