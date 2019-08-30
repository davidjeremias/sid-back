import { Observable, throwError } from 'rxjs';
import { Injectable } from '@angular/core';
import axios from 'axios';
import { NotificationsComponent } from 'src/app/components/notifications/notifications.component';
import { NotificationsService } from 'src/app/components/notifications/notifications.service';
import { MainService } from '../main.service';


@Injectable({
  providedIn: 'root'
})
export class ConsultarArquivosService extends MainService {

  pesquisarArquivos(params?): Observable<any> {
    return this.mainGet('/consultaArquivos/', params);
  }

  isUltimaGeracao(): Observable<boolean> {
    return this.mainGet('/arquivos/isETLDisponivel');
  }

  downloadSimbaFile(codigo): Observable<any> {
    return this.mainGet(`/arquivos/downloadSimbaFile/${codigo}`);
  }

  buscaCodigoArquivo(item): Observable<string> {
    return this.mainGet(`/arquivos/${item.id}/codigoArquivo`);
  }

  downloadArquivos(codigo) {
    axios.post("/consultaArquivos/downloadArquivosConsulta", { "codigo": codigo }, { responseType: 'arraybuffer' })
      .then(function (response) {
        var blob = new Blob([response.data], { type: 'application/zip' });
        var link = document.createElement('a');
        document.body.appendChild(link);
        link.href = window.URL.createObjectURL(blob);
        link.download = "ArquivosGerados.zip";
        link.click();
        document.body.removeChild(link);
      })
      .catch(error => this.trataErro(error));
  }

  

  solicitaGeracaoConsulta(codigo) {
    axios.get("/consultaArquivos/solicitaGeracaoConsulta", { params: { "codigo": codigo } })
      .catch(error => this.trataErro(error))
  }

  //verifica se tem arquivos disponivel para download
  verificaArquivosDisponivel() {
    return Observable.create(observer => {
      axios.get("/consultaArquivos/processosDisponíveisParaDownload")
        .then(function (response) {
          observer.next(response);
          observer.complete();
        })
        .catch(error => this.trataErro(error))
    });
  }

  buscaPeriodosInformacaoGerados() {
    return Observable.create(observer => {
      axios.get('/consultaArquivos/periodosInformacaoGerados')
        .then(function (response) {
          observer.next(response.data);
          observer.complete();
        })
        .catch(error => this.trataErro(error))
    });

  }

  inserirRetornoSimba(formData: FormData): Observable<any> {
    return Observable.create(observer => {
      axios.post('/arquivos/inserirRetornoSimba', formData)
        .then(response => {
          observer.next(response.data);
          this.messageService.add({severity:'success', summary: '', detail:'Operação realizada com sucesso!'});
        })
        .catch(error => this.trataErro(error))
    });
  }

  alterarRetornoSimba(formData: FormData): Observable<any> {
    return Observable.create(observer => {
      axios.post('/arquivos/alterarRetornoSimba', formData)
        .then(response => {
          observer.next(response.data);
          this.messageService.add({severity:'success', summary: '', detail:'Operação realizada com sucesso!'});
        })
        .catch(error => this.trataErro(error))
    });
  }

  removerRetornoSimba(retornoSimba) {
    return Observable.create(observer => {
      axios.delete(`/arquivos/${retornoSimba.id}/removerRetornoSimba`)
        .then(response => {
          observer.next(response.data);
          this.messageService.add({severity:'success', summary: '', detail:'Exclusão realizada com sucesso!'});
        })
        .catch(error => this.trataErro(error))
    });
  }

  pesquisarListaPeriodo() {
    return Observable.create(observer => {
      axios.post('/arquivos/consultaAgendamentosComFiltro')
        .then(function (response) {
          observer.next(response.data);
          observer.complete();
        })
        .catch(error => this.trataErro(error))
    });
  }

  buscarSituacoes(): Observable<any> {
    return this.generico("/arquivos/situacoes");
  }

  private generico(endpoint): Observable<any> {
    return Observable.create(observer => {
      axios.get(endpoint)
        .then(function (response) {
          observer.next(response.data);
          observer.complete();
        })
        .catch(error => this.trataErro(error))
    });
  }
  detalharCincoArquivos(params) {
    return Observable.create(observer => {
      axios.get('/consultaArquivos/detalhamentoCincoArquivosPaginado', { params })
        .then(function (response) {
          observer.next(response.data);
          observer.complete();
        })
        .catch(error => this.trataErro(error))
    });

  }

  detalharArquivoPaginado(params) {
    return Observable.create(observer => {
      axios.get('/consultaArquivos/detalhamentoArquivoPaginado', { params })
        .then(function (response) {
          observer.next(response.data);
          observer.complete();
        })
        .catch(error => this.trataErro(error))
    });
  }

  filaConsultaArquivos() {
    return Observable.create(observer => {
      axios.get('/consultaArquivos/processosGeracaoConsultaNaFila')
        .then(function (response) {
          observer.next(response.data);
          observer.complete();
        })
        .catch(error => this.trataErro(error))
    });
  }

  buscaAgendamentoPorSolicitacao(id){
    return Observable.create(observer => {
      axios.get(`/arquivos/${id}/buscaAgendamento`)
        .then(function (response) {
          observer.next(response.data);
          observer.complete();
        })
        .catch(error => this.trataErro(error))
    });
  }

  buscaAgendamentoRetornoSimba(id){
    return Observable.create(observer => {
      axios.get(`/arquivos/${id}/buscaAgendamentoRetornoSimba`)
        .then(function (response) {
          observer.next(response.data);
          observer.complete();
        })
        .catch(error => this.trataErro(error))
    });
  }

  downloadArquivosETL(id) {
    return Observable.create(observer => {
      axios.get(`/arquivos/${id}/downloadArquivoAfastamento`)
        .then(response => {
          observer.next(response.data);
          if(response.data == null || response.data == "")
            this.messageService.add({severity:'success', summary: '', detail:'Enviado para fila de geração de arquivos'});
        })
        .catch(error => this.trataErro(error))
    });
  }

  verificaSituacaoDownload(id){
    return Observable.create(observer => {
      axios.get(`/arquivos/${id}/situacaoDownload`)
        .then(response => {
          observer.next(response.data);
        })
        .catch(error => this.trataErro(error))
    });
  }

}
