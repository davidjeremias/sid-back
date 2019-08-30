import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '../../../../../node_modules/@angular/router';
import { ParametrosIntegracaoService } from '../../../services/parametros/parametros-integracao.service';
import * as moment from 'moment';

@Component({
  selector: 'app-detalhar-log-integracao',
  templateUrl: './detalhar-log-integracao.component.html',
  styleUrls: ['./detalhar-log-integracao.component.css']
})
export class DetalharLogIntegracaoComponent implements OnInit {

  dataExecucao: Date;
  status: any;
  url: any;
  diaUtil: any;
  hora: Date;
  matricula: any;
  private id: any;
  dtTeste: Date = new Date();

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private service: ParametrosIntegracaoService
  ) {
   }

  ngOnInit() {
    this.route.params.subscribe(e => {
      this.id = e.id;
    });
    this.service.buscaParametrosAtual(this.id).subscribe(response => {
      let dt = response.data.dataHoraProcessamento;
      this.dataExecucao = moment(dt).toDate();
      this.status = this.montaStatus(response.data.status);
      this.url = response.data.parametrosIntegracaoTSE.url;
      this.matricula = response.data.parametrosIntegracaoTSE.matricula;
      this.diaUtil = this.montaDiaUtil(response.data.parametrosIntegracaoTSE.dia);
      let hr = new Date('7/26/2019 '+response.data.parametrosIntegracaoTSE.hora);
      this.hora =  moment(hr).toDate();
    });
  }

  montaStatus(status): string{
    if(status === 0){
      return "Erro"
    }else{
      return "Sucesso"
    }
  }

  montaDiaUtil(dia): string{
    return dia+"º dia útil";
  }

  voltar(){
    this.router.navigate(['/log-integracao']);
  }
}
