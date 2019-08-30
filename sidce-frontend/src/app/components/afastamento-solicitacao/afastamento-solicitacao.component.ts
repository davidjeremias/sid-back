import { Component, OnInit } from '@angular/core';
import { System } from 'src/app/entidade/parametros/system';
import { SolicitacaoService } from 'src/app/services/solicitacao/solicitacao.service';
import { Globals } from 'src/app/entidade/parametros/globals';
import { Message, ConfirmationService } from 'primeng/api';
import { ActivatedRoute, Router } from '@angular/router';
import * as moment from 'moment';
import { Conta } from 'src/app/entidade/conta';
import { CodigoSolicitacao } from 'src/app/entidade/codigoSolicitacao';

/**
 * Componente para adição de alteração de solicitação {C}{U}
 * @author allef.garug
 * @version 1.2.0
*/
@Component({
  selector: 'app-solicitacao',
  templateUrl: './afastamento-solicitacao.component.html',
  styleUrls: ['./afastamento-solicitacao.component.css'],
})
export class AfastamentoSolicitacaoComponent implements OnInit {

  globals: Globals;

  /**
   * Nome ou razão social do objeto recuperado por cpf/cnpj
  */
  nome;

  /**
   * Label a ser exibida dependendo da pesquisa cpf ou cnpj
   * 
   * @return {string} "Nome" ou "Razão Social"
  */
  labelNome: string;

  /**
   * Label a ser exibida dependendo da pesquisa cpf ou cnpj
   * 
   * @return {string} "CPF" ou "CNPJ"
  */
  cpfCnpj: string;

  /**
   * Valor de cpf/cnpj a ser pesquisado
  */
  valuePesquisa: string;

  /**
   * Lista a ser populada assim que pesquisa de contas for executada
  */
  lista1: Array<Conta>;

  /**
   * Lista de contas adicionadas após pesquisa, essa lista que possui os
   * elementos que terão período informado
  */
  lista2: Array<any>;

  /**
   * Lista de seleção contendo todos os elementos selecionados em tela de lista1
  */
  lista1Select: Array<any>;

  /**
   * Lista de seleção contendo todos os elementos selecionados em tela de lista2
  */
  lista2Select: Array<any>;

  /**
   * Arquivo em formato File do ofício da solicitação
  */
  arquivo: File;

  /**
   * Arquivo em formato base64 do ofício da solicitação
  */
  arquivo64: any;

  /**
   * Arquivo em formato Blob do ofício da solicitação
  */
  arquivoBlob: Blob;

  // ************************************************
  //
  // Daqui para baixo são dumps, componentes para funcionamento da tela e não de fato relacionados a solicitação
  //
  // ************************************************

  /**
   * Mensagens de alerta exibidas em tela
  */
  msgs: Array<Message>;

  /**
   * Solicitação eleitoral?
   * 
   * @return {boolean} true: eleitoral; false: geral
  */
  isEleitoral: boolean;

  /**
   * Realizando um afastamento?
   * 
   * @return {boolean} true: afastamento; false: solicitacao
  */
  isAfastamento: boolean;

  // ************************************************
  //
  // Parâmetros carregados para edição de solicitação
  //
  // ************************************************
  id: number;
  dtHrCadastro: Date;
  situacao: any;
  isAlteracao: Boolean;
  isNome: Boolean;
  isRazao: Boolean;
  nomeResponsavel: string;
  codigoSolicitacao: CodigoSolicitacao;
  dataAtual: Date = new Date();
  ano: number = this.dataAtual.getFullYear();
  isSalvando: boolean = false;

  constructor(
    private service: SolicitacaoService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private confirmationService: ConfirmationService
  ) {
    this.globals = System.getInstance();
    this.msgs = new Array<Message>();
    this.activatedRoute.data.subscribe(r => {this.isEleitoral = r.eleitoral; this.isAfastamento = r.afastamento});
    this.cpfCnpj = this.isEleitoral ? "cnpj" : "cpf";
    this.lista1 = new Array();
    this.lista1Select = new Array();
    this.lista2 = new Array();
    this.lista2Select = new Array();
  }

  ngOnInit() {
    this.carregarSolicitacao();
  }

  /**
    * Busca contas de acordo com cpf ou cnpj e carrega a lista1 com os dados recebidos
    * 
    * @param {string} cpfOuCnpj Valor a ser pesquisado
   */
  carregarContas(valor) {
    this.lista1 = new Array();
    this.isNome = false;
    this.isRazao = false;
    this.labelNome = '';
    const param = { "cpfCNPJ": valor };
    switch (this.cpfCnpj) {
      case 'cpf':
        this.service.buscaContas(param)
          .subscribe(response => {
            this.isNome = true;
            this.labelNome = "Nome";
            response.forEach(item => {
              this.lista1.push(this.realizaTratamento(item));
            });
          });
        break;
      case 'cnpj':
        this.service.buscaContas(param)
          .subscribe(response => {
            this.isRazao = true;
            this.labelNome = 'Razão Social';
            response.forEach(item => {
              this.lista1.push(this.realizaTratamento(item));
            });
          });
        break;
    }
  }

  realizaTratamento(conta: Conta) {
    conta.agencia = ("0000" + conta.numeroAgencia.toString()).slice(-4);
    this.nomeResponsavel = conta.nomeResponsavel;
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

  retirarFormatacao(item) {
    return item.replace(/(\.|\/|\-)/g,"");
}

  /**
   * Carrega solicitação em caso de edição
  */
  private carregarSolicitacao() {
    this.activatedRoute.params.subscribe(params => {
      if (params.id) {
        this.service.busca(params.id).subscribe(r => {
          this.id = params.id;
          this.isAlteracao = true;
          this.arquivoBlob = r.arquivoBlob;
          this.arquivo = r.arquivo;
          this.arquivo64 = r.arquivo64;
          this.dtHrCadastro = r.dtHoraCadastro;
          this.situacao = r.situacaoSolicitacao;
          this.codigoSolicitacao = r.codigoSolicitacao;
          this.lista2 = r.contas;
          this.lista2.forEach(e => {
            e = this.realizaTratamento(e); 
            e.periodo = new Array();
            e.periodo.push(moment(e.inicioPeriodo).toDate());
            e.periodo.push(moment(e.fimPeriodo).toDate());
          })
        });
      }
    });
  }

  /**
   * Adiciona todos os elementos de lista1Select a lista2
   * 
   * Após inserir, limpa a lista1Select
  */
  adicionarSelecionados() {
    var listaContas = new Array();
    var existe: boolean;
    this.lista1Select.forEach(conta => {
      var teste = this.verificaContaExistente(conta);
      if(teste){
        existe = true;
      }else{
        listaContas.push(conta);
      }
    });
    if(existe){
      this.msgs = new Array();
      this.msgs.push({
        severity: 'warn',
        summary: '',
        detail: 'Uma ou mais contas já haviam sido adicionadas, as contas ainda não adicionadas foram inseridas na lista.'
      })
    }
    this.lista1Select = new Array();
    listaContas.forEach(conta => {
      this.lista2.push(conta);
    });
  }

  verificaContaExistente(conta: Conta){
    let retorno: boolean = false;
    this.lista2.forEach(contaSalva => {
      if(contaSalva.agencia == conta.agencia &&
        contaSalva.operacao == conta.operacao &&
        contaSalva.conta == conta.conta &&
        contaSalva.digitoConta == conta.digitoConta &&
        contaSalva.situacao == conta.situacao){
          retorno = true;
      }
    });
    return retorno;
  }

  /**
   * Insere período em todas as solicitações de lista2Select
   * 
   * Após inserir, limpa a lista2Select
   * 
   * @param periodo Periódo a ser inserido nas solicitações
  */
  datasEmLote(periodo) {
    this.lista2Select.forEach(e => {
      e.periodo = periodo.value;
      this.validaPeriodo(e);
    });
    this.lista2Select = new Array();
  }

  /**
   * Valida o perído inserido na solicitação
   * Remove período caso seja inválido e apresenta em tela erro
   * 
   * @param item Solicitação a ter período validado
  */
  validaPeriodo(item): boolean {
    if (item.periodo) {
      const test = moment(item.dataAbertura).isAfter(moment(item.periodo[0]));
      if (test) {
        delete item.periodo;
        this.msgs = new Array();
        this.msgs.push({
          severity: 'error',
          summary: '',
          detail: 'O período da informação deve ser posterior ou igual a data de criação da conta!'
        });
        return false;
      }
      return true;
    }
    return false;
  }

  /**
   * Ação para remover individualmente uma solicitação da lista2
   * 
   * @param element Solicição a ser removida
  */
  removerIndividual(element) {
    const index = this.lista2.findIndex(indexx => indexx.id === element.id);
    this.lista2.splice(index, 1);
  }

  /**
   * Remove todos os elementos de lista2Select de lista2
   * 
   * Após remover, limpa a lista2Select
  */
  removerSelecionados() {
    this.lista2Select.forEach(e => this.removerIndividual(e));
    this.lista2Select = new Array();
  }

  /**
   * Verifica se é uma edição que vai sobrepor um ofício já salvo em banco,
   * após isso chama método de tratarUpload()
   * 
   * @param event Evento contendo arquivo
   * @param fileUpload Componente que realizou o upload do arquivo
  */
  uploadArquivo(event, fileUpload) {
    if (this.id) {
      this.confirmationService.confirm({
        message: 'O novo arquivo irá substituir o arquivo atual, deseja continuar?',
        accept: () => this.tratarUpload(event, fileUpload)
      })
    } else {
      this.tratarUpload(event, fileUpload);
    }
  }

  /**
   * Insere o arquivo a solicitação, convertendo para base64 e mantendo o arquivo em
   * formato file para trabalho em tela
   * 
   * @param event Evento contendo arquivo
   * @param fileUpload Componente que realizou o upload do arquivo
  */
  private tratarUpload(event, fileUpload) {
    this.arquivo = event.files[0];
    fileUpload.clear();

    let fr = new FileReader();
    fr.onload = (e) => {
      this.arquivo64 = fr.result;
    };
    fr.readAsDataURL(this.arquivo);
  }

  /**
   * Validações para o upload de arquivo, realizado antes do envio para uploadArquivo()
   * 
   * @param event Evento contendo arquivo
  */
  validaArquivo(event): boolean {
    const arquivo: File = event.files[0];
    this.msgs = new Array<Message>();
    const isPdf = arquivo.type === "application/pdf";
    const isTamanhoValido = arquivo.size <= 10000000;

    if (!isTamanhoValido) this.msgs.push({
      severity: 'error',
      summary: '',
      detail: 'Tamanho máximo de arquivo 10mb'
    });

    if (!isPdf) this.msgs.push({
      severity: 'error',
      summary: '',
      detail: 'O arquivo fornecido deve ser do tipo PDF'
    });

    return isPdf && isTamanhoValido ? true : false;
  }

  /**
   * Solicita confirmação para envio para orgão responsável após validação, caso usuário confirme, realiza de fato o salvamento
  */
  confirmarEnvio() {
    if (this.isValido()) {
      this.confirmationService.confirm({
        message: 'Ao enviar a solicitação para aprovação não poderá realizar alteração ou exclusão, deseja continuar?',
        accept: () => {
          this.isSalvando = true;
          this.salvar(false)
        }
      })
    }
  }

  /**
   * Salva solicitação após validação, identificando endpoint correto para salvar/editar
  */
  salvar(rascunho: boolean) {
    this.isSalvando = true;
    if (this.isValido()) {
      const obj = this.montaObjeto(rascunho);
      const promise = this.id ? this.service.editar(obj) : this.service.salvar(obj, obj.rascunho);
      return promise.subscribe(() => this.router.navigate([`/${this.gerarRouter()}/consultar`]));
    } else {
      this.isSalvando = false;
      return;
    }
  }

  gerarRouter(){
    if(this.isEleitoral){
      return this.isAfastamento ? 'afastamento' : 'solicitacao';
    }else{
      return this.isAfastamento ? 'afastamento-geral' : 'solicitacao-geral';
    }
  }

  /**
   * Monta solicitação a ser salva
   * 
   * @param {boolean} rascunho Se solicitação será um rascunho ou não
   * @return Objeto pronto para ser enviado
  */
  private montaObjeto(rascunho: boolean) {
    const obj = {
      "id": this.id ? this.id : null,
      "numeroUnidade": System.unidade,
      "tipoSolicitacao": this.isEleitoral ? "E" : "G",
      "rascunho": rascunho,
      "oficio": this.arquivo64,
      "afastamento": this.isAfastamento,
      "nomeArquivo": this.arquivo ? this.arquivo.name : null,
      "contas": this.lista2,
      "codigoSolicitacao": this.codigoSolicitacao
    };
    obj.contas.forEach(e => {
      e.cpfCNPJ = this.retirarFormatacao(e.cpfCNPJ);
      if (e.periodo) {
        e.inicioPeriodo = e.periodo[0];
        e.fimPeriodo = e.periodo[1];
      }
    });
    return obj;
  }

  /**
   * Validações de solicitação atual
   * 
   * @return {boolean} Retorna true caso todas as validações sejam atendidas
  */
  private isValido(): boolean {
    const listaValidacoes = new Array();
    this.msgs = new Array<Message>();

    const contaPreenchida = this.lista2.length > 0;
    if (!contaPreenchida) this.msgs.push({
      severity: 'error',
      summary: '',
      detail: 'Necessário informar ao menos uma conta para afastamento'
    });

    const listaValida = this.lista2.every(e => e.periodo);
    if (!listaValida && contaPreenchida) this.msgs.push({
      severity: 'error',
      summary: '',
      detail: 'Existem contas sem período informado'
    });

    const oficioValido = this.arquivo ? true : false;
    if (!oficioValido) this.msgs.push({
      severity: 'error',
      summary: '',
      detail: 'Necessário anexar o arquivo de ofício'
    });

    listaValidacoes.push(contaPreenchida, listaValida, oficioValido);

    return listaValidacoes.every(e => e === true);
  }

  /**
   * Download do ofício atual
  */
  download() {
    System.downloadFile(this.arquivoBlob, this.arquivo.name);
  }
}
