import { Component, OnInit } from '@angular/core';
import { Unidade } from '../../entidade/unidade';
import { UnidadeGestoraService } from '../../services/unidade-gestora/unidade-gestora.service';
import { MessageService, ConfirmationService } from '../../../../node_modules/primeng/api';

@Component({
  selector: 'app-unidade-gestora',
  templateUrl: './unidade-gestora.component.html',
  styleUrls: ['./unidade-gestora.component.css']
})
export class UnidadeGestoraComponent implements OnInit {

  unidade: any;
  listaUnidades: Array<any>;
  selectedUnidade: Unidade;
  cols: any[];
  totalRecords: any;
  ldConsulta: boolean;
  numeroUnidade: any;
  descricaoUnidade: any;
  id: any;
  isPrimeiroRegistro: boolean;

  constructor(
    private service: UnidadeGestoraService,
    private messageService: MessageService,
    private confirm: ConfirmationService
  ) { }

  ngOnInit() {
    this.buscaUnidadeAtual();
    this.selectedUnidade = null;
    this.cols = [
      { field: 'numeroUnidade', header: 'Número Unidade' },
      { field: 'descricaoUnidade', header: 'Descrição Unidade' }
    ];
  }

  buscaUnidadeAtual(){
    this.service.buscaUnidadeAtual().subscribe(response => {
      if(response.data){
        response.data.forEach(e => {
        this.id = e.id;
        this.numeroUnidade = e.numeroUnidade;
        this.descricaoUnidade = e.descricaoUnidade;
        this.isPrimeiroRegistro = false;
        });
      }else{
        this.isPrimeiroRegistro = true;
      }
    });
  }

  pesquisar(){
    this.selectedUnidade = undefined;
    if(this.validaPesquisar()){
      this.ldConsulta = true;
    const obj = {
      unidade: this.unidade ? this.unidade : null
    };
    this.busca(obj);
    }
  }

  paginar(evento){
    this.selectedUnidade = undefined;
    this.ldConsulta = true;
    const params = {
      page: evento.page,
      limit: evento.size,
      unidade: this.unidade ? this.unidade : null
    };
    this.busca(params);
  }

  busca(obj){
    this.service.buscaUnidades(obj).subscribe(response => {
      if(response.data.content.length > 0){
        this.listaUnidades = response.data.content;
        this.totalRecords = response.data.totalElements;
        this.ldConsulta = false;
      }else{
        this.listaUnidades = undefined;
        this.ldConsulta = false;
        this.messageService.add({ severity: 'warn', detail: 'Nenhum registro encontrado' });
      }
    });
  }

  limpar(){
    this.unidade = undefined;
    this.listaUnidades = undefined;
    this.selectedUnidade = null;
  }

  alterarUnidade(item){
    this.service.alterarUnidade(item).subscribe(response => {
      if (response.status === 201) {
        this.numeroUnidade = response.data.numeroUnidade;
        this.descricaoUnidade = response.data.descricaoUnidade;
        this.messageService.add({ severity: 'success', detail: 'Unidade selecionada com sucesso!' });
        this.limpar();
        this.isPrimeiroRegistro = false;
      }
    });
  }

  salvar(item) {
    if(this.isPrimeiroRegistro){
      this.confirm.confirm({
        message: `Definir a unidade <${item.numeroUnidade} - ${item.descricaoUnidade}> como responsável pelo sistema?`,
        accept: () => this.alterarUnidade(item)
      });
    }else{
      this.confirm.confirm({
        message: `Deseja substituir a unidade atual pela a unidade <${item.numeroUnidade} - ${item.descricaoUnidade}> como responsável pelo sistema?`,
        accept: () => this.alterarUnidade(item)
      });
    }
    
  }

  validaPesquisar(): boolean{
    if(this.unidade == undefined){
      this.messageService.add({ severity: 'warn', detail: 'Digite o número da unidade!' });
      return false;
    }else{
      return true;
    }
  }
}
