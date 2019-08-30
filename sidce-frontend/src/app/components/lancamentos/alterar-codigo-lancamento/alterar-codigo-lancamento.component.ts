import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { LancamentosService } from '../../../services/lancamentos/lancamentos.service';
import { NotificationsService } from '../../notifications/notifications.service';
import { Globals } from '../../../entidade/parametros/globals';

@Component({
  selector: 'app-alterar-codigo-lancamento',
  templateUrl: './alterar-codigo-lancamento.component.html',
  styleUrls: ['./alterar-codigo-lancamento.component.css']
})
export class AlterarCodigoLancamentoComponent implements OnInit {

  codigosDebito = new Array();
  codigosCredito = new Array();
  selectedCodigo: any;
  selectedNatureza: any;
  comboCredito: boolean = false;
  comboDebito: boolean = false;
  descricaoLancamento: any;
  codigoLancamento: any;
  natureza: any;
  naturezaLabel: any;
  id: any;
  @Input() lancamento: any;
  @Output() fechar = new EventEmitter();

  constructor(
    private service: LancamentosService,
    private message: NotificationsService,
    private globals: Globals
  ) {
    this.codigosDebito = this.globals.codigosDebito;
    this.codigosCredito = this.globals.codigosCredito;
   }

  ngOnInit() {
    this.onChangeCombo(this.lancamento.natureza);
    this.preparaUpdate(this.lancamento);
  }

  onChangeCombo(natureza){
    if(natureza == 'C'){
      this.comboCredito= true;
      this.comboDebito = false;
    }
    if(natureza == 'D'){
      this.comboCredito= false;
      this.comboDebito = true;
    }
  }

  preparaUpdate(lancamento){
    this.id = lancamento.id;
    this.descricaoLancamento = lancamento.descricaoHistorico;
    this.codigoLancamento = lancamento.codigoLancamento;
    this.natureza = lancamento.natureza;
    if(lancamento.natureza == 'C'){
      this.naturezaLabel = 'Crédito';
    }else{
      this.naturezaLabel = 'Débito';
    }
  }

  salvar(){
    const obj = {
      id: this.id ? this.id : null,
      codigoLancamento: this.selectedCodigo ? this.selectedCodigo.id : null,
    };
    this.service.salvar(obj).subscribe(response => {
      if(response.data.id != undefined){
        this.message.add({ severity: 'success', detail: 'Código alterado com sucesso' });
        this.fechar.emit(false);
      }
    });
  }

  voltar(){
    this.fechar.emit(false);
  }

}
