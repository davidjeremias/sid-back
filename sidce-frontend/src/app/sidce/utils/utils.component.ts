import { Component, OnInit } from '@angular/core';
import * as moment from 'moment';


@Component({
  selector: 'app-utils',
  templateUrl: './utils.component.html',
  styleUrls: ['./utils.component.css']
})
export class UtilsComponent implements OnInit {

  ano: any;
  mes: any;
  dia: any;

  constructor() { }

  ngOnInit() {
    this.dataAtual();
  }

  formatardata(data) {
    data = moment(data, "YYYY-MM-DD").format("DD/MM/YYYY");
    return data;
  }

  dataAtual() {
    let data = new Date();
    this.dia = data.getDate();
    this.mes = data.getMonth() + 1;
    this.ano = data.getFullYear();

    return this.dia + "/" + this.mes + "/" + this.ano;
  }

  pegaAno() {
    let data = new Date();
    let ano = data.getFullYear();

    return [ano].join('/');
  }

  pegaMesAnterior() {
    let data = new Date();
    let mes = data.getMonth();
    //quando o mês for JANEIRO 
    if (mes == 0) {
      this.ano = data.getFullYear() - 1;
    }
    return this.nome_Mes([mes].join('/'));
  }

  // exibe o nome do mês
  nome_Mes(mes: any) {
    if (mes == 1) {
      mes = 'JANEIRO';
    }
    else if (mes == 2) {
      mes = 'FEVEREIRO';
    }
    else if (mes == 3) {
      mes = 'MARÇO';
    }
    else if (mes == 4) {
      mes = 'ABRIL';
    }
    else if (mes == 5) {
      mes = 'MAIO';
    }
    else if (mes == 6) {
      mes = 'JUNHO';
    }
    else if (mes == 7) {
      mes = 'JULHO';
    }
    else if (mes == 8) {
      mes = 'AGOSTO';
    }
    else if (mes == 9) {
      mes = 'SETEMBRO';
    }
    else if (mes == 10) {
      mes = 'OUTUBRO';
    }
    else if (mes == 11) {
      mes = 'NOVEMBRO';
    }
    else if (mes == 12) {
      mes = 'DEZEMBRO';
    }
    else if (mes == 0) {
      mes = 'DEZEMBRO';
    }
    return mes;
  }

}
