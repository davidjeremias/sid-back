import { Component, OnInit } from '@angular/core';
import { Router } from '../../../../node_modules/@angular/router';
import { ParametrosIntegracaoService } from '../../services/parametros/parametros-integracao.service';
import { MessageService } from '../../../../node_modules/primeng/api';

@Component({
  selector: 'app-log-integracao',
  templateUrl: './log-integracao.component.html',
  styleUrls: ['./log-integracao.component.css']
})
export class LogIntegracaoComponent implements OnInit {

  inicio: Date;
  fim: Date;
  status = new Array();
  selectedStatus: any;
  listaLog: Array<any>;
  totalRecords: any;
  ldConsulta: boolean;

  constructor(
    private router: Router,
    private service: ParametrosIntegracaoService,
    private messageService: MessageService,
  ) {
    this.status = [
      {label: 'Todos', id: null},
      {label: 'Erro', id: '0'},
      {label: 'Sucesso', id: '1'}
    ];
   }

  ngOnInit() {
  }

  ngOnChanges() { 
  }

  pesquisar(){
    this.ldConsulta = true;
    if(!this.selectedStatus){
      this.selectedStatus = this.status.filter(e => e.label === 'Todos');
    }
    const obj = {
      inicio: this.inicio ? this.inicio : null,
      fim: this.fim ? this.fim : null,
      status: this.selectedStatus.id ? this.selectedStatus.id : null
    };
    this.service.pesquisarlogIntegracao(obj).subscribe(response => {
      if(response.data.content){
        this.listaLog = response.data.content;
        this.totalRecords = response.data.totalElements;
        this.ldConsulta = false;
      }else{
        this.listaLog = undefined;
        this.ldConsulta = false;
        this.messageService.add({ severity: 'warn', detail: 'Nenhum registro encontrado' });
      }
    });
  }

  paginar(evento) {
    const params = {
      page: evento.page,
      limit: evento.size
    };
    this.service.pesquisarlogIntegracao(params)
      .subscribe(response => {
        this.listaLog = response.data.content;
        this.totalRecords = response.data.totalElements;
      });
  }

  detalhar(id){
    this.router.navigate(['/detalhar-log-integracao', id]);
  }

  limpar(){
    this.inicio = undefined;
    this.fim = undefined;
    this.selectedStatus = undefined;
    this.listaLog = undefined;
  }
}
