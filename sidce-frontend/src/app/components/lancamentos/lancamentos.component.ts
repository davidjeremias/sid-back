import { Component, OnInit } from '@angular/core';
import { LancamentosService } from '../../services/lancamentos/lancamentos.service';
import { MessageService } from '../../../../node_modules/primeng/api';
import { Router } from '../../../../node_modules/@angular/router';
import { Globals } from '../../entidade/parametros/globals';

@Component({
  selector: 'app-lancamentos',
  templateUrl: './lancamentos.component.html',
  styleUrls: ['./lancamentos.component.css']
})
export class LancamentosComponent implements OnInit {

  descricaoLancamento: any;
  naturezaLancamento: any;
  codigoLancamento: any;
  selectedNatureza: any;
  selectedCodigo: any;
  naturezas = new Array();
  codigosDebito = new Array();
  codigosCredito = new Array();
  listaLancamento: Array<any>;
  comboCredito: boolean = false;
  comboDebito: boolean = false;
  ldConsulta: any;
  totalRecords: any;
  isAlteracao: boolean;
  lancamento: any;

  constructor(
    private service: LancamentosService,
    private messageService: MessageService,
    private router: Router,
    private globals: Globals
  ) {
    this.naturezas = this.globals.naturezas;
    this.codigosDebito = this.globals.codigosDebito;
    this.codigosCredito = this.globals.codigosCredito;

    this.ldConsulta = false;
   }

  ngOnInit() {
    this.pesquisar();
  }

  onChangeCombo(){
    if(this.selectedNatureza.label == 'Crédito'){
      this.comboCredito= true;
      this.comboDebito = false;
    }
    if(this.selectedNatureza.label == 'Débito'){
      this.comboCredito= false;
      this.comboDebito = true;
    }
    if(this.selectedNatureza.label == 'Todos'){
      this.comboCredito= false;
      this.comboDebito = false;
    }
  }

  pesquisar(){
    this.ldConsulta = true;
    const obj = {
      descricaoLancamento: this.descricaoLancamento ? this.descricaoLancamento : null,
      natureza: this.selectedNatureza ? this.selectedNatureza.id : null,
      codigo: this.selectedCodigo ? this.selectedCodigo.id : null,
      isCodigo: true
    };
    this.service.buscar(obj).subscribe(response => {
      if(response.data.content){
        this.listaLancamento = response.data.content;
        this.totalRecords = response.data.totalElements;
        this.ldConsulta = false;
      }else{
        this.listaLancamento = undefined;
        this.ldConsulta = false;
        this.messageService.add({ severity: 'warn', detail: 'Nenhum registro encontrado' });
      }
    });
  }

  limpar(){
    this.descricaoLancamento = undefined;
    this.selectedNatureza = undefined;
    this.selectedCodigo = undefined;
    this.comboCredito= false;
    this.comboDebito = false;
    this.listaLancamento = undefined;
  }

  paginar(evento){
    this.ldConsulta = true;
    const params = {
      page: evento.page,
      limit: evento.size,
      descricaoLancamento: this.descricaoLancamento ? this.descricaoLancamento : null,
      natureza: this.selectedNatureza ? this.selectedNatureza.id : null,
      codigo: this.selectedCodigo ? this.selectedCodigo.id : null,
      isCodigo: true
    };
    this.service.buscar(params).subscribe(response => {
      if(response.data.content){
        this.listaLancamento = response.data.content;
        this.totalRecords = response.data.totalElements;
        this.ldConsulta = false;
      }else{
        this.listaLancamento = undefined;
        this.ldConsulta = false;
        this.messageService.add({ severity: 'warn', detail: 'Nenhum registro encontrado' });
      }
    });
  }

  atribuirCodigo(){
    this.router.navigate(['/atribuir-codigo-lancamento']);
  }

  alterarCodigo(item){
    this.lancamento = item;
    this.isAlteracao = true;
  }

  fecharDialog(event){
    this.isAlteracao = event;
    this.pesquisar();
  }

}
