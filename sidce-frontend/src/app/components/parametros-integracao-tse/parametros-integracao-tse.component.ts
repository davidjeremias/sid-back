import { NotificationsService } from './../notifications/notifications.service';
import { ParametrosIntegracaoService } from './../../services/parametros/parametros-integracao.service';
import { Component, OnInit } from '@angular/core';
import { MessageService } from 'primeng/components/common/messageservice';
import * as moment from 'moment';

@Component({
  selector: 'app-parametros-integracao-tse',
  templateUrl: './parametros-integracao-tse.component.html',
  styleUrls: ['./parametros-integracao-tse.component.css'],
  providers: [MessageService]
})
export class ParametrosIntegracaoTseComponent implements OnInit {

  url: any;
  hora: Date;
  dias = new Array();
  selectedDias: any;

  constructor(
    private service: ParametrosIntegracaoService, 
    private messageService: NotificationsService,
  ) {
    this.dias = [
      {label: '1º dia útil', id: 1},
      {label: '2º dia útil', id: 2},
      {label: '3º dia útil', id: 3},
      {label: '4º dia útil', id: 4},
      {label: '5º dia útil', id: 5},
      {label: '6º dia útil', id: 6},
      {label: '7º dia útil', id: 7},
      {label: '8º dia útil', id: 8},
      {label: '9º dia útil', id: 9},
      {label: '10º dia útil', id: 10}
    ];
   }

  ngOnInit() {
    this.pesquisaParametros();
  }

  salvar(){
    const obj = {
      url: this.url ? this.url : null,
      dia: this.selectedDias.id ? this.selectedDias.id : null,
      hora: this.hora ? this.hora : null
    };
    if(this.validaObrigatoriedade(obj)){
      this.service.salvar(obj).subscribe(response => {
        this.messageService.add({ severity: 'success', detail: 'Parâmetros de Integração TSE inseridos com sucesso' });
      });
    }
  }

  validaObrigatoriedade(obj): boolean{
    if(obj.url && obj.dia && obj.hora){
      return true;
    }else{
      this.messageService.add({ severity: 'error', detail: 'Campos obrigatórios não preenchidos' });
      return false;
    }
  }

  pesquisaParametros(){
    this.service.pesquisar().subscribe(response => {
      if(response.data){
        response.data.forEach(e => {
          this.url = e.url;
          this.selectedDias = this.dias[e.dia -1];
          let dt = e.hora
          this.hora = moment(dt).toDate();
        });  
      }
    });
  }

  verificaLink(){
    if(this.url){
      const url = {
        url: this.url ? this.url : null
      };
      this.service.verificaLink(url).subscribe(response => {
        if(response.status === 200)
          this.messageService.add({ severity: 'success', detail: 'Link válido' });
        if(response.status === 204)
          this.messageService.add({ severity: 'info', detail: 'Digite um Link válido' });
      });
    }
  }

}
