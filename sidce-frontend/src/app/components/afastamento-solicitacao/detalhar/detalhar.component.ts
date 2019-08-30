import { Component, Input } from '@angular/core';
import { System } from 'src/app/entidade/parametros/system';
import { SolicitacaoService } from 'src/app/services/solicitacao/solicitacao.service';
import { Solicitacao } from 'src/app/entidade/solicitacao';

/**
 * Componente de detalhamento de solicitação {R}
 * @author allef.garug
 * @version 1.0.1
*/
@Component({
  selector: 'app-detalhar-solicitacao',
  templateUrl: './detalhar.component.html',
  styleUrls: ['./detalhar.component.css'],
})
export class DetalharAfastamentoSolicitacaoComponent {

  /**
   * Solicitação atualmente sendo detalhada pelo componente
  */
  @Input() solicitacao: any;

  /**
   * Solicitação eleitoral?
   * 
   * @return {boolean} true: eleitoral; false: geral
  */
  @Input() isEleitoral: boolean;

  /**
   * É um Afatamento?
   * 
   * @return {boolean} true: afastamento; false: solicitacao
  */
  @Input() isAfastamento: boolean;

  /**
   * Solicitação Pendente?
   * 
   * @return {boolean} true: sim; false: não
  */
  @Input() isPendentes: boolean = false;

  /**
   * Requisição de download atualmente em andamento?
   * 
   * @return {boolean} Valor boleano
  */
  isDownloadAndamento = false;

  /**
   * Solicitação rejeitada?
   * 
   * @return {boolean} Valor boleano
  */
  @Input() isRejeitado = false;

  titulo: string;
  isConcluido: boolean = false;
  isEnviadoParaAprovacao: boolean = false;

  constructor(
    private service: SolicitacaoService
  ) { }

  ngOnInit() {
    this.titulo = this.gerarTitulo();
  }

  /**
   * Download do arquivo de ofício de solicitação
   * 
   * @param solicitacao Solicitação que será realizada o download
  */
  download(solicitacao) {
    this.isDownloadAndamento = true;
    this.service.busca(solicitacao.id).subscribe(solicitacao => {
      System.downloadFile(solicitacao.arquivoBlob, solicitacao.arquivo.name);
      this.isDownloadAndamento = false;
    });
  }

  gerarTitulo() {
    var titulo = "Detalhar Solicitação - Contas ";
    if (this.isPendentes) {
      return "";
    }
    return this.isEleitoral ? titulo + "eleitorais" : titulo + "gerais";
  }

  iniciarVerificacoes(solicitacao: Solicitacao){
    if(solicitacao){
      this.isEnviadoParaAprovacao = this.verificaSituacao(solicitacao.situacaoSolicitacao.nomeSituacao);
      this.isConcluido = this.verificarConclusao(solicitacao);
    }
    return true;
  }

  verificarConclusao(solicitacao: Solicitacao){
      return !this.isPendentes && !solicitacao.rascunho && !this.isEnviadoParaAprovacao;
  }

  verificaSituacao(status: string) {
    return status == 'Enviado para aprovação' ? true : false;
  }

  gerarNomeData(solicitacao: any) {
    if (solicitacao) {
      if (!this.isConcluido) return 'Inclusão';
      return solicitacao.situacaoSolicitacao.nomeSituacao == 'Recusado' ? 'Recusa' : 'Aprovação';
    }
  }

  gerarData(solicitacao: any) {
    if (solicitacao) {
      return this.isConcluido ? solicitacao.dtHoraAnalise : solicitacao.dtHoraCadastro;
    }
  }

}
