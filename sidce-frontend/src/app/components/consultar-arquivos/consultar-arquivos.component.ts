import { SituacaoSolicitacao } from './../../entidade/situacaoSolicitacao';
import { ConsultarArquivosService } from './../../services/consultarArquivos/consultar-arquivos.service';
import { UtilsComponent } from './../../sidce/utils/utils.component';
import { Component, OnInit } from '@angular/core';
import { MessageService } from 'primeng/components/common/messageservice';
import { Router } from '@angular/router';
import { Globals } from 'src/app/entidade/parametros/globals';
import { NotificationsService } from '../notifications/notifications.service';

@Component({
  selector: 'app-consultar-arquivos',
  templateUrl: './consultar-arquivos.component.html',
  styleUrls: ['./consultar-arquivos.component.css'],
  providers: [MessageService]
})
export class ConsultarArquivosComponent implements OnInit {
  listaPeriodo = [];
  inicioPeriodoGeracao: any;
  fimPeriodoGeracao: any;
  data = [];
  utils = new UtilsComponent();
  tabelaConsulta: boolean;
  mesInformacao = [];
  mesValor = [];
  anoInformacao: Array<any>;
  listaArquivos: Array<any>;
  listaTemp: Array<any>;
  totalRecords: number;
  disponiveisDownload = [];
  mes: any;
  ano: any;
  matriculainput: any;
  cols = [];
  colsFila = [];
  BASE_URL = "/consultaArquivos";
  FILA_URL = "/consultaArquivos/processosGeracaoConsultaNaFila";
  modalFila = false;
  processosEmfila = [];
  periodoInformacao = [];
  isSimbaVisivel: boolean;
  simbaVisivel: any;
  isArquivo: boolean;
  codigoAfastamento: any;
  acaoSimba: any;
  afastamento: any;
  solic: any;
  btnRetornoSimba: boolean = false;
  ldConsulta: boolean = true;

  constructor(
    private messageService: NotificationsService,
    private router: Router,
    private globals: Globals,
    private consultaArquivo: ConsultarArquivosService
  ) {
    this.anoInformacao = new Array();
  }

  ngOnInit() {
    this.pesquisarPeriodoInformacao();
    this.menuTabela();
    this.buscaFilaProcessamentoConsulta();
    this.fila_de_Arquivos();
    this.pesquisarArquivos();
  }

  iniSimba(item, acao) {
    if(acao == 'Incluir'){
      this.simbaVisivel = item;
      this.acaoSimba = acao;
      this.isSimbaVisivel = true;
      this.afastamento = undefined;
      this.solic = true;
    }
    if(acao == 'Alterar'){
      this.simbaVisivel = item;
      this.acaoSimba = acao;
      this.isSimbaVisivel = true;
      this.afastamento = undefined;
      this.solic = true;
    }
  }

  pesquisarArquivos(params?) {
    this.ldConsulta = true;
    this.consultaArquivo.verificaArquivosDisponivel()
      .subscribe(response => {
        if (response.data.length > 0) {
          this.disponiveisDownload = response.data;
        }
        params = params ? params : this.montarParametros();
    this.consultaArquivo.pesquisarArquivos(params)
      .subscribe(response => {
        if(response.content){
          this.listaArquivos = response.content;
          this.listaArquivos.forEach(e =>{
          if(e.situacao.nomeSituacao == 'Transmitido' || e.situacao.nomeSituacao == 'Rejeitado'){
            this.btnRetornoSimba = true;
          }
        });
        this.totalRecords = response.totalElements;
        this.ldConsulta = false;
        }else{
          this.ldConsulta = false;
        }
      });
      });
  }

  private montarParametros() {
    const params = {
      matricula: this.matriculainput ? this.matriculainput : null,
      periodoInformacao : this.ano && this.mes ? `${this.ano}${this.mes}` : null,
      inicioPeriodoGeracao : this.inicioPeriodoGeracao ? this.inicioPeriodoGeracao : null,
      fimPeriodoGeracao : this.fimPeriodoGeracao ? this.fimPeriodoGeracao : null,
      codigoAfastamento : this.codigoAfastamento ? this.codigoAfastamento : null
    };
    return params;
  }

  isDisponivelDownload(codigo) {
    return (this.disponiveisDownload.some(e => e.codigo === codigo)) ? true : false
  }

  baixarArquivos(codigo) {
    this.consultaArquivo.downloadArquivos(codigo);
  }

  isNaFila(codigo) {
    return (this.processosEmfila.some(e => e.codigo === codigo)) ? true : false
  }

  lidarComRetornoSimba(retorno) {
    if(retorno.response) {
      const index = this.listaArquivos.findIndex(e => e.codigo === retorno.processo.codigo);
      this.listaArquivos[index] = retorno.processo;
      this.isSimbaVisivel = false;
      this.btnRetornoSimba = true;
    }
  }

  // solicita o ETL o dowload dos arquivos
  solicitarGeracaoArquivos(codigo) {
    if (this.processosEmfila.some(e => e.codigo === codigo)) {
      this.messageService.add({ severity: 'warn', detail: 'Processo já se encontra na fila' });
    } else {
      this.processosEmfila.push(codigo);
      this.consultaArquivo.solicitaGeracaoConsulta(codigo);
      this.messageService.add({ severity: 'success', detail: 'Processo adicionado na fila de geração' });
    }
  }

  visualizarCincoArquivos(item) {
    this.router.navigate(['/visualizarArquivos', item.codigo, item.codigoSolicitacao ? item.codigoSolicitacao.codigo : undefined]);
  }

  paginar(evento) {
    const params = {
      page: evento.page,
      limit: evento.size
    };
    this.consultaArquivo.pesquisarArquivos(params)
      .subscribe(response => {
        this.listaArquivos = response.content;
        this.totalRecords = response.totalElements;
      });
  }

  changeMes(event) {
    const ano = event.value;
    this.mesInformacao = this.periodoInformacao
      .filter(e => e.ano === ano)
      .map(e => ({ label: this.utils.nome_Mes(e.mes), value: e.mes }));
  }

  menuTabela() {
    return this.cols = [
      { field: 'matricula', header: 'Solicitante' },
      { field: 'periodo', header: ' Período da informação' },
      { field: 'dtHoraCadastro', header: 'Data do upload' },
      { field: 'dtHrProcessamento', header: 'Data da geração dos arquivos' }

    ];
  }

  fila_de_Arquivos() {
    return this.colsFila = [
      { field: 'matricula', header: 'Solicitante do download' },
      { field: 'dtHoraCadastro', header: 'Download', type: 'date' },
      { field: 'matricula', header: 'Solicitante' },
      { field: 'periodo', header: 'Período a informação' },
      { field: 'dtHoraCadastro', header: 'Upload', type: 'date' }

    ];
  }

  limpar() {
    this.matriculainput = null;
    this.inicioPeriodoGeracao = null;
    this.fimPeriodoGeracao = null;
    this.ano = null;
    this.mes = null;
    this.codigoAfastamento = null;
    this.menuTabela();

  }

  formatarData(date) {
    return date.getDate();
  }

  validardata(dataInicio, dataFim) {

    if (dataInicio == null && dataFim != null) {
      dataInicio = this.utils.dataAtual();
    } else if (dataInicio != null && dataFim == null) {
      dataFim = this.utils.dataAtual();
    }
    else if (dataInicio > dataFim) {
      this.mensagem("error", "Data inicial maior que a data Final");
    }
  }

  varificaDataInvalida(data) {

    let dia = data.slice(0, 2);
    let mes = data.slice(3, 5);

    if (dia < 1 || dia > 31) {
      this.mensagem("error", "Data Inválida!");
    }

    if (mes < 1 || mes > 12) {
      this.mensagem("error", "Data Inválida!");
    }

    if (dia > 29 && mes == 2) {
      this.mensagem("error", "Data Inválida!");
    }

    if (data.toString().length < 10 || data.toString().length > 10) {
      this.mensagem("error", "Data Inválida!");
    }
  }

  mensagem(tipoMSG, texto) {
    this.messageService.add({ severity: tipoMSG, summary: texto });
  }

  //busca o período disponivel para consulta
  pesquisarPeriodoInformacao() {
    this.consultaArquivo.buscaPeriodosInformacaoGerados()
      .subscribe(response => {
        if(response){
          response.forEach(value => {
            if(value){
              const ano = value.toString().slice(0, 4);
            const mes = value.toString().slice(4, 6);
            const obj = {
              "ano": ano,
              "mes": mes,
              "label": ano,
              "value": ano
            }
            this.periodoInformacao.push(obj);
  
            if (!this.anoInformacao.some(e => e.ano === ano))
              this.anoInformacao.push(Object.assign({}, obj))
            }
          });
        } 
      });
  }

  buscaFilaProcessamentoConsulta() {
    this.consultaArquivo.filaConsultaArquivos()
      .subscribe(response => this.processosEmfila = response.content);
  }

  isFilaVazia() {
    return (this.processosEmfila.length > 0) ? false : true;
  }

  exibirFila() {
    this.modalFila = (this.modalFila) ? false : true;
  }

}
