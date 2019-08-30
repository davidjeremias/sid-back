import axios from 'axios';
import { Injectable } from '@angular/core';
import 'rxjs/add/operator/toPromise';
import { Observable } from 'rxjs';
import { MainService } from './main.service';

@Injectable({
  providedIn: 'root'
})

export class FileService extends MainService {

  BASE_URL = "/uploadFile";
  MODULE_CONSTANT = "usuario";

  baixarArquivos(url, nomeArquivo) {
    axios.get(url)
      .then(function (response) {

        //abre o arquivo para download
        var blob = new Blob([response.data]);
        var link = document.createElement('a');
        link.href = window.URL.createObjectURL(blob);
        link.download = nomeArquivo + ".txt";
        link.click();

      }).catch(err => {

      })
  }


  retornoStatus: any;
  resposta: any;

  adicionar(files: any): Observable<any> {

    var formData = new FormData();

    for (var index = 0; index < files.length; index++) {
      formData.append("file", files[index]);
    }

    return Observable.create(observer => {
      axios.post('/arquivos/uploadArquivosTSE', formData)
        .then(response => {
          this.retornoStatus = response.status;
          this.resposta = response.data;
          observer.next(response.data);
          observer.complete();
        })
        .catch()
    });

  }

  ultimoConcluido() {
    return this.generico('/arquivos/ultimoProcessamentoConcluido');
  }

  downloadUltimo() {
    return this.mainGet('/arquivos/downloadUltimoProcessamento');
  }

  isUltimoProcessamentoConcluido(): Observable<boolean> {
    return this.generico('/arquivos/isUltimoProcessamentoConcluido');
  }

  downloadArquivosSimba(): Observable<Array<any>> {
    return this.generico('/arquivos/downloadArquivosSimba');
  }

  private generico(endpoint): Observable<any> {
    return Observable.create(observer => {
      axios.get(endpoint)
        .then(function (response) {
          observer.next(response.data);
          observer.complete();
        })
        .catch(error => console.log(error))
    });
  }

}
