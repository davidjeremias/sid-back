import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';
import { MenuItem, ConfirmationService } from 'primeng/api';
import { SolicitacaoService } from 'src/app/services/solicitacao/solicitacao.service';
import { Conta } from 'src/app/entidade/conta';
import { ConsultarArquivosService } from '../../../services/consultarArquivos/consultar-arquivos.service';
import { Globals } from 'src/app/entidade/parametros/globals';
import { System } from 'src/app/entidade/parametros/system';
import base64Object from 'base64-js';
import { Solicitacao } from 'src/app/entidade/solicitacao';

@Component({
  selector: 'app-tabela-afastamentos',
  templateUrl: './tabela-afastamentos.component.html',
  styleUrls: ['./tabela-afastamentos.component.css'],
})
export class TabelaAfastamentosComponent implements OnInit {

  @Input() lista: Array<any>;
  @Input() totalRecords: number;
  @Input() isEleitoral: boolean;
  @Input() isLoading: boolean;
  @Output() paginado: EventEmitter<any> = new EventEmitter();
  @Output() emitComponente: EventEmitter<any> = new EventEmitter();
  detalhando: any;
  isDetalhando: boolean;
  isSimbaVisivel: boolean;
  isAfastamento: boolean = true;
  alteracaoPermitida: boolean = false;
  exclusaoPermitida: boolean = false;
  @Output() isBotaoFila = new EventEmitter();;
  
  listaAcoesSimba: Array<MenuItem>;
  acoesSimbaOutros: Array<MenuItem>;
  acoesSimbaGerado: Array<MenuItem>;
  labelSimba = '<i class="fas fa-handshake"></i>';

  solicitacao: any;
  simbaVisivel: any;
  acaoSimba: any;
  afastamento: any;
  temp: any;
  url: string = 'afastamento-geral/consultar';

  constructor(
    private service: SolicitacaoService,
    private consultaArquivo: ConsultarArquivosService,
    private confirm: ConfirmationService,
    private router: Router
  ) {
    this.acoesSimbaGerado = [
      {
        "label": "Incluir",
        "title": "Incluir Informações de Retorno de Transmissão para o SIMBA",
        "id": "1",
        "command": () => this.iniSimba(this.solicitacao, "incluir")
      }
    ];

    this.acoesSimbaOutros= [
      {
        "label": "Detalhar",
        "title": "Visualizar Informações de Retorno de Transmissão para o SIMBA",
        "id": "2",
        "command": () => this.iniSimba(this.solicitacao, "detalhar")
      },
      {
        "label": "Alterar",
        "title": "Alterar Informações de Retorno de Transmissão para o SIMBA",
        "id": "3",
        "command": () => this.iniSimba(this.solicitacao, "alterar")
      },
      {
        "label": "Excluir",
        "title": "Excluir Informações de Retorno de Transmissão para o SIMBA",
        "id": "4",
        "command": () => this.iniSimba(this.solicitacao, "excluir")
      }
    ];
  }

  ngOnInit() {
    this.verificaPermissoes();
  }

  detalhar(item): void {
    this.isDetalhando = true;
    if(item){
      item.contas.forEach(solicitacao => {
        solicitacao = this.realizaTratamento(solicitacao);
        console.log(item);
      });
    }
    this.detalhando = item;
  }

  getDisplay() {
    return  {
      display: this.isLoading || this.lista.length == 0 ? 'none' : 'block'
    }
  }

  paginar(event) {
    this.paginado.emit(event);
  }

  editar(item) {
    this.router.navigate([`/${this.gerarRouter()}/${item.id}`]);
  }

  gerarRouter(){
    if(this.isEleitoral){
      return this.isAfastamento ? 'afastamento' : 'solicitacao';
    }else{
      return this.isAfastamento ? 'afastamento-geral' : 'solicitacao-geral';
    }
  }

  gerarData(solicitacao: Solicitacao) {
    if (solicitacao) {
      return solicitacao.dtHoraAnalise ? solicitacao.dtHoraAnalise : solicitacao.dtHoraCadastro;
    }
  }

  excluir(item) {
    this.confirm.confirm({
      message: 'Deseja realmente excluir o Afastamento em criação?',
      accept: () => this.exclusaoConfirmada(item)
    })
  }

  private exclusaoConfirmada(item) {
    this.service.excluirAfastamento(item.id).subscribe(
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

  verificaPermissoes(){
    this.alteracaoPermitida = Globals.verificaPermissao(["DCE_MATRIZ", "DCE_OPERADOR"]);
    this.exclusaoPermitida = Globals.verificaPermissao(["DCE_MATRIZ", "DCE_OPERADOR"]);
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

  iniSimba(item, acao) {
    if(acao == 'incluir'){
      this.simbaVisivel = item;
      this.acaoSimba = acao;
      this.isSimbaVisivel = true;
      this.afastamento = undefined;
    }else if(acao == 'excluir'){
      this.consultaArquivo.buscaAgendamentoPorSolicitacao(item.id).subscribe(data => {
        this.afastamento = data;
        this.acaoSimba = acao;
        this.isSimbaVisivel = false;
      });
    }else{
      this.consultaArquivo.buscaAgendamentoPorSolicitacao(item.id).subscribe(data => {
        this.afastamento = data;
        this.acaoSimba = acao;
        this.isSimbaVisivel = true;
      });
    }
  }

  lidarComRetornoSimba(event){
    this.isSimbaVisivel = false;
    this.paginado.emit(event);
  }

  downloadArquivosETL(item){
    this.temp = item;
    this.consultaArquivo.downloadArquivosETL(item.id).subscribe(data => {
      if(data != ""){
        const arquivoBlob = new Blob([base64Object.toByteArray(data)], { type: 'application/zip' });
        System.downloadFile(arquivoBlob, `arquivo_tse`);
      }else{
        let index = this.lista.indexOf(this.temp);
        let novaSituacao = 'Fila';
        this.temp.situacaoArquivo = novaSituacao;
        this.lista.splice(index, 1, this.temp);
      }
    });
  }

  visualizaFilaArquivo(item){
    this.isBotaoFila.emit(true);
  }

  visualizarCincoArquivos(solicitacao) {
    this.router.navigate(['/visualizarArquivos', solicitacao.codigo, this.isEleitoral, solicitacao.codigoSolicitacao ? solicitacao.codigoSolicitacao.codigo : undefined]);
  }
}
