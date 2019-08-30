import { Component, OnInit } from '@angular/core';
import { LancamentosService } from '../../../services/lancamentos/lancamentos.service';
import { MessageService, ConfirmationService } from '../../../../../node_modules/primeng/api';
import { Router } from '../../../../../node_modules/@angular/router';
import { Globals } from '../../../entidade/parametros/globals';
import { reject } from '../../../../../node_modules/@types/q';
import { NotificationsService } from '../../notifications/notifications.service';

@Component({
  selector: 'app-atribuir-codigo-lancamento',
  templateUrl: './atribuir-codigo-lancamento.component.html',
  styleUrls: ['./atribuir-codigo-lancamento.component.css']
})
export class AtribuirCodigoLancamentoComponent implements OnInit {

  descricaoLancamento: any;
  naturezaLancamento: any;
  codigoLancamento: any;
  selectedNatureza: any;
  selectedCodigo: any;
  naturezas = new Array();
  codigosDebito = new Array();
  codigosCredito = new Array();
  listaLancamento: Array<any>;
  ldConsulta: any;
  totalRecords: any;
  listaNova: Array<any>;
  listaSemCodigo: Array<any> = new Array();
  listaTemp: Array<any> = new Array();

  constructor(
    private service: LancamentosService,
    private messageService: NotificationsService,
    private router: Router,
    private globals: Globals,
    private confirm: ConfirmationService
  ) {
    this.naturezas = this.globals.naturezas;
    this.codigosDebito = this.globals.codigosDebito;
    this.codigosCredito = this.globals.codigosCredito;

    this.ldConsulta = false;
   }

  ngOnInit() {
    this.pesquisaComFiltro();
  }

  pesquisaComFiltro(){
    this.ldConsulta = true;
    const obj = {
      descricaoLancamento: this.descricaoLancamento ? this.descricaoLancamento : null,
      natureza: this.selectedNatureza ? this.selectedNatureza.id : null,
      codigoLancamento: this.selectedCodigo ? this.selectedCodigo.id : null,
      isCodigo: false
    };
    this.service.buscar(obj).subscribe(response => {
      if(response.data.content){
        this.listaLancamento = response.data.content;
        this.totalRecords = response.data.totalElements;
        this.ldConsulta = false;
      }else{
        this.listaLancamento = undefined;
        this.ldConsulta = false;
        this.messageService.add({ severity: 'warn', detail: 'Não foi encontrado nenhum lançamento sem código' });
      }
    });
  }

  paginar(evento){
    this.ldConsulta = true;
    const params = {
      page: evento.page,
      limit: evento.size,
      descricaoLancamento: this.descricaoLancamento ? this.descricaoLancamento : null,
      natureza: this.selectedNatureza ? this.selectedNatureza.id : null,
      codigoLancamento: this.selectedCodigo ? this.selectedCodigo.id : null,
      isCodigo: false
    };
    this.service.buscar(params).subscribe(response => {
      if(response.data.content){
        this.listaLancamento = response.data.content;
        this.totalRecords = response.data.totalElements;
        this.ldConsulta = false;
      }else{
        this.listaLancamento = undefined;
        this.ldConsulta = false;
        this.messageService.add({ severity: 'warn', detail: 'Não foi encontrado nenhum lançamento sem código' });
      }
    });
  }

  voltar(){
    this.router.navigate(['/consulta-lancamento']);
  }

  salvar(lista){
    this.listaNova = new Array();
    this.trataListaNova(lista);
    this.listaSemCodigo = lista;
    console.log(this.listaSemCodigo);
    this.listaTemp = this.listaLancamento.filter(this.filterListaSemCodigo);
    if(this.listaTemp.length > 0){
      this.confirm.confirm({
        message: 'Os lançamentos sem código atribuído continuarão pendentes. Deseja continuar?',
        accept: () => {
          this.service.salvarAll(this.listaNova).subscribe(() => {
            this.pesquisaComFiltro();
            this.mensagemSucesso();
          });
        }
      });
    }else{
      this.service.salvarAll(this.listaNova).subscribe(() => {
        this.pesquisaComFiltro();
        this.mensagemSucesso();
      });
    }
  }

  mensagemSucesso(){
    this.messageService.add({ severity: 'success', detail: 'Códigos atribuídos com sucesso' });
  }

  filterListaSemCodigo(value){
    return value.codigoLancamento == null || value.codigoLancamento == undefined;
  }

  trataListaNova(lista){
    lista.forEach(e => {
      if(e.codigoLancamento){
        e.codigoLancamento = e.codigoLancamento.id;
        this.listaNova.push(e);
      }
    });
  }

}
