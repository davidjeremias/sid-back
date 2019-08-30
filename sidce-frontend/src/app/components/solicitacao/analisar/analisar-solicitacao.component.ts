import { Component, OnInit } from '@angular/core';
import { SolicitacaoService } from 'src/app/services/solicitacao/solicitacao.service';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';
import { ConfirmationService, Message } from 'primeng/api';
import { Conta } from 'src/app/entidade/conta';
import { System } from 'src/app/entidade/parametros/system';

@Component({
  selector: 'app-analisar-solicitacao',
  templateUrl: './analisar-solicitacao.component.html'
})
export class AnalisarSolicitacaoComponent implements OnInit {

  /**
    * Solicitação atualmente sendo detalhada pelo componente
  */
  solicitacao: any;

  tituloPagina;

  /**
    * Solicitação eleitoral?
    * 
    * @return {boolean} true: eleitoral; false: geral
  */
  isEleitoral: boolean;

  /**
   * Consultar somente solicitações pendentes de avaliação
   * @return {boolean} true: somente pendentes; false: todas as solicitações
  */
 isPendentes: boolean;

 /**
  * Atributo para consultar apenas afastamentos
  * Afastamentos são todas as solicitações com ic_preaprovado = true
  * @return {boolean} true: afastamento; false: solicitacao
 */
 isAfastamento;

 isRejeitado: boolean = false;
 isAprovado: boolean = false;

  recusando = false;
  msgs: Array<Message>;

  constructor(
    private activatedRoute: ActivatedRoute,
    private confirmationService: ConfirmationService,
    private location: Location,
    private service: SolicitacaoService,
  ) {
    this.activatedRoute.data.subscribe(r => {
      this.isEleitoral = r.eleitoral;
      this.isPendentes = r.pendentes;
      this.isAfastamento = r.afastamento;
    });

    this.tituloPagina = `
      Analisar ${
      this.isAfastamento ?
        'Afastamentos' :
        `Solicitações ${this.isPendentes ?
          'Pendentes' :
          'de Afastamento'
        }`
      } - Contas ${this.isEleitoral ?
        'Eleitorais' :
        'Gerais'
      }`;
   }

  ngOnInit() {
    this.activatedRoute.params.subscribe(params => {
      if (params.id) {
        this.service.busca(params.id).subscribe(r => {
          r.contas.forEach(solicitacao => {
            solicitacao = this.realizaTratamento(solicitacao);
          });
          this.solicitacao = r;
        });
      }
    });
    this.activatedRoute.data.subscribe(r => {
      this.isEleitoral = r.eleitoral;
    });
  }

  voltar() {
    this.location.back();
  }

  confirmar() {
    this.solicitacao.unidadeResponsavel = System.unidade;
    this.confirmationService.confirm({
      header: 'Aprovar Solicitação',
      message: 'Ao confirmar a solicitação, a lista de contas entrará em fila para geração do afastamento de sigilo. Deseja continuar?',
      accept: () => this.service.aprovar(this.solicitacao)
        .subscribe(response => {
          this.tratarSalvamento(response);
        })
    });
  }

  toogleRecusar() {
    this.recusando = !this.recusando;
    if (!this.recusando) {
      this.solicitacao.motivoRejeicao = '';
      this.msgs = new Array();
    }
  }

  recusar() {
    if (this.isValido()) {
      this.solicitacao.unidadeResponsavel = System.unidade;
      this.service.rejeitar(this.solicitacao)
        .subscribe(response => {
          this.tratarSalvamento(response);
        });
    }
  }

  private tratarSalvamento(response) {
    this.solicitacao = response;
    this.voltar();
  }

  private isValido(): boolean {
    this.msgs = new Array();
    if (this.solicitacao.motivoRejeicao) {
      return true;
    } else {
      this.msgs.push({
        severity: 'error',
        summary: '',
        detail: 'É necessário informar um motivo para cancelar uma solicitação'
      });
      return false;
    }
  }

  realizaTratamento(conta: Conta) {
    conta.agencia = ("0000" + conta.numeroAgencia.toString()).slice(-4);
    conta.conta = parseInt(conta.numeroConta);
    conta.operacao = this.trataOperacao(conta);
    conta.cpfCNPJ = this.trataCpfCNPJ(conta.cpfCNPJ);
    return conta;
  }

  trataOperacao(conta : Conta){
    if (conta.numeroConta.length == 8) {
      return ("000" + conta.numeroOperacao.toString()).slice(-3);
    } else {
      return ("0000" + conta.numeroOperacao.toString()).slice(-4);
    }
  }

  trataCpfCNPJ(cpfCNPJ){
    if (cpfCNPJ.length == 11){
      return cpfCNPJ.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/g,"\$1.\$2.\$3\-\$4");
    }else{
      return cpfCNPJ.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/g,"\$1.\$2.\$3\/\$4\-\$5");
    }
  }

}
