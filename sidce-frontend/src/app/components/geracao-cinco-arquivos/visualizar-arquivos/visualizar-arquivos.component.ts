import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { MessageService } from 'primeng/components/common/messageservice';
import { ConsultarArquivosService } from 'src/app/services/consultarArquivos/consultar-arquivos.service';

@Component({
  selector: 'app-visualizar-arquivos',
  templateUrl: './visualizar-arquivos.component.html',
  styleUrls: ['./visualizar-arquivos.component.css'],
  providers: [MessageService]
})
export class VisualizarArquivosComponent implements OnInit, AfterViewInit {

  @ViewChild('menuAbas') menuAbas: MenuItem[];
  @ViewChild('paginador') paginador;
  items: MenuItem[];
  listaItens = new Array();
  listaItensBck = new Array();
  detalhando: boolean;
  ldDetalhamento: boolean;
  ldPaginacao: boolean;
  itemAtivo: any;
  isEleitoral: any;
  codigoSolicitacao: string;
  
  totalRecords: number;

  private sub: any;
  // Filtros
  codigo: String;
  numeroAgencia: String;
	numeroConta: String;
  cnpj: String;
  abaAtiva: MenuItem;

  validarTabela: boolean;
  operacao: string;
  isValid: boolean = false;
  isObrigatoriedade: boolean = false;

  agencias = {};
  colsAgencias = [];
  colsContas = [];
  colsExtrato = [];
  colsOrigemDestino = [];
  colsTitulares = [];


  BASE_URL = "/consultaArquivos/detalhamentoCincoArquivos";
  base_temp = `&codigo=${this.codigo}`;
  
  constructor(
    private route: ActivatedRoute, 
    private router: Router, 
    private consultaService : ConsultarArquivosService,
    private message: MessageService
  ) {
    this.ldPaginacao = true;
    this.validarTabela = false;
  }

  ngOnInit() {
    // Define abas
    this.items = [
      {label: 'Agência', id:'banco'},
      {label: 'Conta'},
      {label: 'Extrato'},
      {label: 'Origem Destino', id: 'origemDestino'},
      {label: 'Titular'}
    ];

    // Define aba ativa por padrão
    this.abaAtiva = this.items[0];

    this.sub = this.route.params.subscribe(params => {
      this.codigo = params.codigo;
      this.base_temp = `&codigo=${this.codigo}`;
      if(params.isEleitoral){
        this.isEleitoral = params.isEleitoral;
      }else{
        this.isEleitoral = null;
      }
      if(params.codigoSolicitacao) {
        this.codigoSolicitacao = params.codigoSolicitacao;
      }
    });

    this.colsAgencias = [
      { field: 'banco', subfield: 'codigoBanco', header: 'Nº banco' },
      { field: 'banco', subfield: 'numeroAgencia', header: 'Nº Agência' },
      { field: 'banco', subfield: 'cidade', header: 'Endereço Cidade' },
      { field: 'banco', subfield: 'uf', header: 'Endereço UF' },
      { field: 'banco', subfield: 'endereco', header: 'Endereço logradouro' },
      { field: 'banco', subfield: 'telefone', header: 'Telefone agência' }
    ];
    this.colsContas = [
      { field: 'conta', subfield: 'numeroConta', header: 'Nº Conta' },
      { field: 'conta', subfield: 'codigoBanco', header: 'Nº banco' },
      { field: 'conta', subfield: 'numeroAgencia', header: 'Nº Agência' },
      { field: 'conta', subfield: 'tipo', header: 'Tipo' },
      { field: 'conta', subfield: 'dataAbertura', header: 'Data Abertura' },
      { field: 'conta', subfield: 'dataEncerramento', header: 'Data Encerramento' },
      { field: 'conta', subfield: 'movimentacao', header: 'Movimentação' }
    ];
    this.colsExtrato = [
      { field: 'extrato', subfield: 'chaveExtrato', header: 'Chave Extrato' },
      { field: 'extrato', subfield: 'dataLancamento', header: 'Data Lançamento' },
      { field: 'extrato', subfield: 'documento', header: 'Documento' },
      { field: 'extrato', subfield: 'descricaoLancamento', header: 'Descrição Lancamento' },
      { field: 'extrato', subfield: 'tipoLancamento', header: 'Tipo Lançamento' },
      { field: 'extrato', subfield: 'valorLancamento', header: 'Valor Lançamento' }
    ];
    this.colsOrigemDestino = [
      { field: 'origemDestino', subfield: 'valorTransacao', header: 'Valor Transação'},
      { field: 'origemDestino', subfield: 'numeroDocumentoTranscao', header: 'Nº Doc Transação'},
      { field: 'origemDestino', subfield: 'codigoBanco', header: 'Código Banco'},
      { field: 'origemDestino', subfield: 'numeroAgencia', header: 'Nº Agência'},
      { field: 'origemDestino', subfield: 'numeroConta', header: 'Nº Conta'},
      { field: 'origemDestino', subfield: 'cpfCnpj', header: 'CPF/CNPJ'}
    ];
    this.colsTitulares = [
      { field: 'origemDestino', subfield: 'tipoVinculo', header:'Tipo Vínculo'},
      { field: 'origemDestino', subfield: 'pessoaInvestigada', header:'Pessoa Investigada'},
      { field: 'origemDestino', subfield: 'tipoPessoaTitular', header:'Tipo Pessoa Titular'},
      { field: 'origemDestino', subfield: 'cpfCNPJ  ', header:'CPF/CNPJ'},
      { field: 'origemDestino', subfield: 'nomeCompleto', header:'Nome Completo'},
      { field: 'origemDestino', subfield: 'nomeDocumentoIdentificacao', header:'Nome Doc Identificação'}
    ];
    this.operacao = '003';

  }

  ngAfterViewInit() {
    setTimeout(() => this.mudaAtiva());
  }

limpar(evento){
  this.numeroAgencia = null;
	this.numeroConta = null;
  this.cnpj = null;
}

voltar() {
  if(this.isEleitoral == undefined || this.isEleitoral == null){ 
    this.voltarConsultaArquivo();
  }
  if(this.isEleitoral === 'true'){
    this.voltarConsultaAfastamentoEleitoral();
  } 
  if(this.isEleitoral === 'false'){
    this.voltarConsultaAfastamentoGeral();
  }
}

voltarConsultaArquivo() {
  this.router.navigate(['/consultarArquivos']);
}

voltarConsultaAfastamentoEleitoral() {
  this.router.navigate(['afastamento-geral/consultar/eleitoral']);
}

voltarConsultaAfastamentoGeral() {
  this.router.navigate(['afastamento-geral/consultar']);
}

paginar(evento, item?: MenuItem) {
  this.ldDetalhamento = true;
  this.abaAtiva = item ? item : this.menuAbas['activeItem'];
  const params = {
    codigo: this.codigo,
    cnpj: (this.cnpj  && this.cnpj != "") ? this.cnpj : null,
    numeroAgencia: (this.numeroAgencia && this.numeroAgencia != "") ? this.numeroAgencia : null,
    numeroConta : (this.numeroConta && this.numeroConta != "") ? this.numeroConta : null,
    arquivo: this.abaAtiva.id ? this.abaAtiva.id : this.abaAtiva.label.toLowerCase(),
    page: evento.page,
    limit: evento.size
  };
  this.consultaService.detalharArquivoPaginado(params)
    .subscribe(response => {
      if(response.content){
        this.ldDetalhamento = false;
        this.listaItens = response.content;
        this.totalRecords = response.totalElements;
      }else{
        this.ldDetalhamento = false;
        this.message.add({ severity: 'warn', detail: 'Nenhum registro foi encontrado' });
      }
    });
}

// Método usado para alterar abaAtiva
mudaAtiva(item?: MenuItem) {
  if (this.detalhando) {
    this.toggleDetalhamento();
  }
  this.ldDetalhamento = true;
  this.ldPaginacao = false;
  this.abaAtiva = item ? item : this.menuAbas['activeItem'];
  const params = {
    codigo: this.codigo,
    cnpj: (this.cnpj  && this.cnpj != "") ? this.cnpj : null,
    numeroAgencia: (this.numeroAgencia && this.numeroAgencia != "") ? this.numeroAgencia : null,
    numeroConta : (this.numeroConta && this.numeroConta != "") ? this.numeroConta : null,
    arquivo: item ? item.id ? item.id : item.label.toLowerCase() : this.abaAtiva.id ? this.abaAtiva.id : this.abaAtiva.label.toLowerCase(),
    page: 0,
    limit: 10
  };
  this.consultaService.detalharArquivoPaginado(params)
    .subscribe(response => {
      if(response.content){
        this.ldDetalhamento = false;
        this.ldPaginacao = true;
        this.listaItens = response.content;
        this.totalRecords = response.totalElements;
      }else{
        this.listaItens = undefined;
        this.ldDetalhamento = false;
        this.message.add({ severity: 'warn', detail: 'Nenhum registro foi encontrado' });
      }
    });
}

public toggleDetalhamento(item?) {
  if (this.detalhando) {
    this.listaItens = this.listaItensBck;
    this.listaItensBck = null;
  } else {
    this.listaItensBck = this.listaItens;
    this.listaItens = this.listaItens.filter(e => e.id === item.id);
    this.carregarDetalhes(item);
  }
  this.detalhando = !this.detalhando;
}

private carregarDetalhes(item) {
  this.itemAtivo = item;
  // this.ldDetalhamento = true;
  // setTimeout(() => this.ldDetalhamento = false, 2000); // Simular o carregamento de dados
}

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

  incluirZeroEsquerda(){
    if (this.numeroConta) {
      let ccFmt = ("0000000000" + this.numeroConta).slice(-10);
      this.numeroConta = ccFmt;
    }
  }

  validarCNPJ() {
    let cnpj: any = this.cnpj;

    this.isValid = false;
    this.isObrigatoriedade = false;

    if(cnpj == undefined || cnpj == ''){
      this.isObrigatoriedade = true;
    }else{       
      if (cnpj.trim().length != 14 && cnpj.trim().length > 0){
        this.cnpj = undefined;
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
        this.cnpj = undefined;
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
              this.cnpj = undefined;
      }
      return true;
    }  
  }

}
