import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ConfirmationService } from 'primeng/api';
import { SolicitacaoService } from 'src/app/services/solicitacao/solicitacao.service';
import { Router } from '@angular/router';
import { Conta } from 'src/app/entidade/conta';

@Component({
  selector: 'app-tabela-solicitacoes',
  templateUrl: './tabela-solicitacoes.component.html',
  styleUrls: ['./tabela-solicitacoes.component.css'],
})
export class TabelaSolicitacoesComponent {

  @Input() lista: Array<any>;
  @Input() totalRecords: number;
  @Input() isEleitoral: boolean;
  @Input() isLoading: boolean;
  @Input() isPendentes: boolean;
  @Output() paginado: EventEmitter<any> = new EventEmitter();
  detalhando: any;
  isRejeitado: boolean = false;
  isDetalhando: boolean;
  isAfastamento: boolean = false;

  constructor(
    private service: SolicitacaoService,
    private confirm: ConfirmationService,
    private router: Router
  ) { }

  paginar(event) {
    this.paginado.emit(event);
  }

  editar(item) {
    this.router.navigate([`/${this.isEleitoral ? 'solicitacao':'solicitacao-geral'}/${item.id}`]);
  }

  excluir(item) {
    this.confirm.confirm({
      message: 'Deseja realmente excluir a solicitação?',
      accept: () => this.exclusaoConfirmada(item)
    })
  }

  detalhar(item) {
    this.isDetalhando = true;
    if(item){
      item.contas.forEach(solicitacao => {
        solicitacao = this.realizaTratamento(solicitacao);
        console.log(item);
      });
    }
    this.detalhando = item;
    if(item.situacaoSolicitacao.nomeSituacao == 'Rejeitado') this.isRejeitado = true;
  }

  getDisplay() {
    return  {
      display: this.isLoading || this.lista.length == 0 ? 'none' : 'block'
    }
  }

  analisar(item) {
    this.router.navigate([`/${this.isEleitoral ? 'solicitacao':'solicitacao-geral'}/pendentes/${item.id}`]);
  }

  private exclusaoConfirmada(item) {
    this.service.excluir(item.id).subscribe(
      data => console.log(data),
      err => console.log(err),
      () => this.paginado.emit(true)
    )
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
