import { Component, OnInit, OnChanges } from '@angular/core';
import { System } from 'src/app/entidade/parametros/system';
import { SelectItem, MessageService } from 'primeng/api';
import { SolicitacaoService } from 'src/app/services/solicitacao/solicitacao.service';
import { Globals } from 'src/app/entidade/parametros/globals';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { ConsultarArquivosService } from '../../../services/consultarArquivos/consultar-arquivos.service';
import { Solicitacao } from 'src/app/entidade/solicitacao';

/**
 * Componete para consulta ordenada de solicitações {R} eleitorais e gerais
 * @author allef.garug
 * @version 1.1.2
*/
@Component({
  selector: 'app-consultar-solicitacoes',
  templateUrl: './consultar-afastamentos-solicitacoes.component.html'
})
export class ConsultarAfastamentosSolicitacoesComponent implements OnInit {
  globals: Globals = System.getInstance();

  /**
   * Anos aceitos na pesquisa de solicitações, iniciando em 2000 e indo até ano atual
  */
  yearRange: string = `2000:${(new Date).getFullYear()}`;

  /**
   * Lista de status possíveis de solicitação
  */
  listaStatus: Array<SelectItem>;

  /**
   * Parâmetros de pesquisa de solicitação
  */
  params: any;

  /**
   * Solicitação eleitoral?
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

  /**
   * Label a ser exibida dependendo da pesquisa cpf ou cnpj
   * @return {string} "CPF" ou "CNPJ"
  */
  cpfCnpj: any;

  /**
   * Lista de solicitações
  */
  lista: Array<Solicitacao> = new Array();

  /**
   * Total de solicitações existentes que podem ser exibidas
  */
  totalRecords;

  tituloPagina;

  isFila;

  processosEmfila;

  // Parametros
  matricula: any;
  codigo: any;
  valuePesquisa: string;
  iniPeriodo: any;
  fimPeriodo: any;
  status: any;
  isLoading: boolean = true;
  isTratando: boolean = false;
  isValid: boolean = false;
  isObrigatoriedade: boolean = false;

  constructor(
    private service: SolicitacaoService,
    private activeRoute: ActivatedRoute,
    private messageService: MessageService,
    private arquivoService: ConsultarArquivosService
  ) {
    this.activeRoute.data.subscribe(r => {
      this.isEleitoral = r.eleitoral;
      this.isPendentes = r.pendentes;
      this.isAfastamento = r.afastamento;
    });

    this.tituloPagina = `
      Consultar ${
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
    this.cargaInicial();
  }

  ngOnChanges() {
    this.preSearch();
  }

  /**
   * Carrega listaStaus
  */
  private getStatusSolicitacao(): Observable<any> {
    return this.service.getStatusSolicitacao(this.montarParametros())
      .map(response => this.popularListaStatus(response));
  }

  private getStatusAfastamento() {
    return this.service.getStatusAfastamento(this.montarParametros())
      .map(response => this.popularListaStatus(response));
  }

  private cargaInicial() {
    this.isLoading = true;
    let promise: Observable<any>;
    promise = this.isAfastamento ? this.getStatusAfastamento() : this.getStatusSolicitacao();
    promise.subscribe(() => this.pesquisar());
  }

  /**
   * Monta os parâmetros baseados nos filtros atuais e chama pesquisa
  */
  pesquisar() {
    this.lista = undefined;
    this.params = this.montarParametros();
    this.preSearch(this.params);
  }

  /**
   * Pesquisa solicitações e popula lista
   * 
   * @param params Parâmetros para pesquisa de solicitação
  */
  private preSearch(params?) {
    this.isLoading = true;
    params = params ? params : this.montarParametros();
    if(this.iniPeriodo && this.fimPeriodo && this.iniPeriodo > this.fimPeriodo){
      this.messageService.add({ severity: 'warn', summary: 'Mensagem de alerta:', detail: 'A data INICIAL deve ser anterior a data FINAL!' });
    }else{
      this.lista = new Array();
      this.service.pesquisar(params)
        .subscribe(response => {
          this.lista = response.content;
          if(this.lista){
            this.lista.forEach(solicitacao => {
              solicitacao.isUsuarioCriador = this.verificarUsuario(solicitacao.matricula);    
            });
            this.totalRecords = response.totalElements;
          }else{
            this.lista = new Array();
          }
          this.isLoading = false;
        }, error => this.isLoading = false);
    }
  }

  private verificarUsuario(matricula: string){
    let matriculaUsuarioLogado = Globals.matricula;
    return matricula == matriculaUsuarioLogado;
  }

  /**
   * Monta parâmetros de pesquisa de solicitação
   * 
   * @return Parâmetros prontos para pesquisa
  */
  private montarParametros() {
    const params = {
      "tipo": this.isEleitoral ? "E" : "G",
      "isAfastamento": this.isAfastamento,
      "isPendentes": this.isPendentes,
      "matricula": this.matricula ? this.matricula : null,
      "codigo": this.codigo ? this.trataCodigo() : null,
      "numeroUnidade": System.unidade,
      "cpfCNPJ": this.valuePesquisa && (this.valuePesquisa.length == 11 || this.valuePesquisa.length == 14) ? this.valuePesquisa : null,
      "iniPeriodo": this.iniPeriodo ? this.iniPeriodo : null,
      "fimPeriodo": this.fimPeriodo ? this.fimPeriodo : null,
      "situacao": this.status ? this.status : this.listaStatus ? this.listaStatus.filter(e => e.label !== "Todos").map(e => e.value) : null
    };
    return params;
  }

  private popularListaStatus(response) {
    this.listaStatus = new Array();
    // Remove status em criação caso esteja visualizando somente pendencias
    this.listaStatus.push({ label: 'Todos', value: response.filter(e => e.id !== 1 || !this.isPendentes).map(e => e.id) });
    return response.filter(e => e.id !== 1 || !this.isPendentes).forEach(element => this.listaStatus.push({ label: element.nomeSituacao, value: element.id }));
  }

  /**
   * Pagina e realiza nova pesquisa baseada na página
  */
  paginar(event) {
    const params = this.params;
    if (event) {
      params.page = event.page;
      params.limit = event.size;
    }
    this.preSearch(params);
  }

  preparaFila(event){
    this.isFila = event;
  }

  visualizaFila(){
    this.arquivoService.filaConsultaArquivos().subscribe(data => {
      console.log(data);
      this.processosEmfila = data.content;
    });
  }

  trataCodigo() {
    if (this.codigo && this.codigo != "") {
      return this.codigo = this.codigo.toLocaleUpperCase();
    }
  }

  validarCNPJ() {
    let cnpj: any = this.valuePesquisa;

    this.isValid = false;
    this.isObrigatoriedade = false;

    if(cnpj == undefined || cnpj == ''){
      this.isObrigatoriedade = true;
    }else{       
      if (cnpj.trim().length != 14 && cnpj.trim().length > 0){
        this.valuePesquisa = undefined;
        this.isValid = true;
    }
           
      // Valida DVs
      let tamanho = cnpj.trim().length - 2;
      let numeros = cnpj.substring(0,tamanho);
      let digitos = cnpj.substring(tamanho);
      let soma = 0;
      let pos = tamanho - 7;
      for (let i = tamanho; i >= 1; i--) {
        soma += numeros.charAt(tamanho - i) * pos--;
        if (pos < 2)
              pos = 9;
      }
      let resultado = soma % 11 < 2 ? 0 : 11 - soma % 11;

      tamanho = tamanho + 1;
      numeros = cnpj.substring(0,tamanho);
      soma = 0;
      pos = tamanho - 7;
      for (let i = tamanho; i >= 1; i--) {
        soma += numeros.charAt(tamanho - i) * pos--;
        if (pos < 2)
              pos = 9;
      }
      resultado = soma % 11 < 2 ? 0 : 11 - soma % 11;

      if(resultado != digitos.charAt(0) && resultado != digitos.charAt(1)){
        // this.valuePesquisa = undefined;
        this.isValid = true;
      }
      // Elimina CNPJs invalidos conhecidos
      if (cnpj == "00000000000000" || 
          cnpj == "11111111111111" || 
          cnpj == "22222222222222" || 
          cnpj == "33333333333333" || 
          cnpj == "44444444444444" || 
          cnpj == "55555555555555" || 
          cnpj == "66666666666666" || 
          cnpj == "77777777777777" || 
          cnpj == "88888888888888" || 
          cnpj == "99999999999999"){
              this.isValid = true;
              // this.valuePesquisa = undefined;
      }
      return true;
    }  
  }

}
