import { Component, OnInit } from '@angular/core';
import { Globals } from 'src/app/entidade/parametros/globals';
import { AuditoriaService } from 'src/app/services/auditoria/auditoria.service';
import { SelectItem } from 'primeng/api';
import * as moment from 'moment-timezone';

@Component({
  selector: 'app-dados-auditoria',
  templateUrl: './dados-auditoria.component.html'
})
export class DadosAuditoriaComponent implements OnInit {

  listaFuncionalidades: Array<SelectItem>;
  listaEventos: Array<SelectItem>;
  lista: Array<any>;

  totalRecords: number;

  // Atributos a serem pesquisados
  params: any;
  matricula: string;
  iniPeriodo: Date;
  fimPeriodo: Date;
  funcionalidade: string;
  codigoSolicitacao: string;
  evento: string;
  isCodigoEleitoral: boolean = false;
  isCodigoGeral: boolean = false;
  isCodigoTSE: boolean = false;

  constructor(
    public globals: Globals,
    private service: AuditoriaService
  ) { }

  ngOnInit() {
    this.getFuncionalidades();
    this.pesquisar();
  }

  private getFuncionalidades() {
    this.service.getFuncionalidades()
      .subscribe(response => {
        this.listaFuncionalidades = new Array();
        response.forEach(element => this.listaFuncionalidades.push({ label: element, value: element }));
      });
  }

  getEvento(event) {
    this.evento = undefined;
    this.service.getEvento(event.value)
      .subscribe(response => {
        this.listaEventos = new Array();
        response.forEach(element => this.listaEventos.push({ label: element, value: element }));
      });
  }

  pesquisar() {
    this.params = this.montarParametros();
    this.preSearch(this.params);
  }

  private preSearch(params?) {
    params = params ? params : this.montarParametros();
    this.service.pesquisar(params)
      .subscribe(response => {
        this.lista = response.content;
        console.log(this.lista);
        this.totalRecords = response.totalElements;
      });
  }

  private montarParametros() {
    const params = {
      matricula: this.matricula ? this.matricula : null,
      iniPeriodo: this.iniPeriodo ? moment.tz(this.iniPeriodo, 'America/Sao_Paulo').utc(true).set({ hour: 0, minute: 0, second: 0, millisecond: 0 }).toDate() : null,
      fimPeriodo: this.fimPeriodo ? moment.tz(this.fimPeriodo, 'America/Sao_Paulo').utc(true).set({ hour: 23, minute: 59, second: 59, millisecond: 59 }).toDate() : null,
      funcionalidade: this.funcionalidade ? this.funcionalidade : null,
      codigoSolicitacao: this.codigoSolicitacao ? this.trataCodigo() : null,
      evento: this.evento ? this.evento : null
    };
    return params;
  }

  paginar(event) {
    const params = this.params;
    params.page = event.page;
    params.limit = event.size;
    this.preSearch(params);
  }

  removerCodigo() {
    this.isCodigoEleitoral = false;
    this.isCodigoGeral = false;
    this.isCodigoTSE = false;
  }

  trataCodigo() {
    if (this.codigoSolicitacao && this.codigoSolicitacao != "") {
      return this.codigoSolicitacao = this.codigoSolicitacao.toLocaleUpperCase();
    }
  }

  verificaFuncionalidade() {
    this.codigoSolicitacao = undefined;
    this.funcionalidade.trim() == "Afastamento de sigilo - Contas eleitorais" || this.funcionalidade.trim() == "Solicitação de Afastamento de sigilo - Contas eleitorais" ? this.isCodigoEleitoral = true : this.isCodigoEleitoral = false;
    this.funcionalidade.trim() == "Afastamento de sigilo - Contas gerais" || this.funcionalidade.trim() == "Solicitação de Afastamento de sigilo - Contas gerais" ? this.isCodigoGeral = true : this.isCodigoGeral = false;
    this.funcionalidade.trim() == "Geração de Solicitação de afastamento via arquivo TSE" || this.funcionalidade.trim() == "Geração de Solicitação de afastamento via arquivo TSE(Rotina)" ? this.isCodigoTSE = true : this.isCodigoTSE = false;
  }
}
