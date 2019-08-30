import { Component, OnInit } from '@angular/core';
import { ParametrosService } from 'src/app/services/parametros.service';

@Component({
  selector: 'app-parametros-sistema',
  templateUrl: './parametros-sistema.component.html'
})
export class ParametrosSistemaComponent implements OnInit {
  
  listaParametros;

  listaCategorias = new Array();

  constructor(private service: ParametrosService) { }

  ngOnInit() {
    this.service.findAll().subscribe(r =>  {
      this.listaParametros = r.content;
      this.montaCategorias(r.content);
    });
  }

  montaCategorias(responseFindAll) {
    responseFindAll.forEach(e => {
      if(!this.listaCategorias.some(categoria => categoria.id === e.tipo.id)) {
        this.listaCategorias.push(e.tipo)
      }
    });
  }

  salvar(parametro) {
    this.service.salvar(parametro);
  }

  teste() {
    console.log(this)
  }

}
