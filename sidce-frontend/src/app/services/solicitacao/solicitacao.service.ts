import { Injectable } from '@angular/core';
import base64Object from 'base64-js';
import * as moment from 'moment';
import { Observable } from 'rxjs';
import { MainService } from '../main.service';

@Injectable({
  providedIn: 'root',
})
export class SolicitacaoService extends MainService {
  private msgIncluida: string = "Solicitação enviada com sucesso para aprovação da área responsável!";

  private _busca(id) { return this.mainGet(`solicitacao/${id}`) }
  
  buscaContas(cpf) { return this.mainGet('solicitacao/buscaContas', cpf) }

  salvar(objeto, rascunho: boolean) {
    if (objeto.afastamento) {
      return this.mainPost('solicitacao/salvaAfastamento', objeto)
    } else {
      return this.mainPost('solicitacao/salvaSolicitacao', objeto)
    }
  }

  editar(objeto) {
    if (objeto.afastamento) {
      return this.mainPut('solicitacao/alterarAfastamento', objeto)
    } else {
      return this.mainPut('solicitacao/salvaSolicitacao', objeto)
    }
  }

  excluir(id) { return this.mainDelete(`solicitacao/${id}`) }
  excluirAfastamento(id) { return this.mainDelete(`solicitacao/${id}/excluirAfastamento`) }
  getStatusSolicitacao(params) { return this.mainGet('solicitacao/situacoes', params) }
  getStatusAfastamento(params) { return this.mainGet('solicitacao/situacoes', params) }
  pesquisar(params) { return this.mainGet('/solicitacao', params) }

  aprovar(solicitacao) { return this.mainPost('solicitacao/aprovar', solicitacao) }
  rejeitar(solicitacao) { return this.mainPost('solicitacao/rejeitar', solicitacao) }


  /**
   * Carrega solicitação em caso de edição
  */
  busca(id): Observable<any> {
    return this._busca(id).map(response => {

      const arquivoBlob = new Blob([base64Object.toByteArray(response.oficios[0].base64arquivo)], { type: 'application/pdf' });
      const arquivo = new File([arquivoBlob], `oficio-solicitacao-${id}.pdf`);

      const solicitacao = {
        "id": id,
        "isAlteracao": true,
        "arquivo": arquivo,
        "arquivoBlob": arquivoBlob,
        "arquivo64": response.oficios[0].base64arquivo,
        "dtHoraCadastro": response.dtHoraCadastro,
        "situacao": response.situacaoSolicitacao,
        "contas": response.contas,
        "matricula": response.matricula,
        "situacaoSolicitacao": response.situacaoSolicitacao,
        "codigoSolicitacao": response.codigoSolicitacao
      };

      solicitacao.contas.forEach(e => {
        e.periodo = new Array();
        e.periodo.push(moment(e.inicioPeriodo).toDate());
        e.periodo.push(moment(e.fimPeriodo).toDate());
      });

      return solicitacao;
    });
  }
}
