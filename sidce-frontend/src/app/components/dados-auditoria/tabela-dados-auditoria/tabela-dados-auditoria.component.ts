import { Component } from '@angular/core';
import { Input } from '@angular/core';
import { EventEmitter } from '@angular/core';
import { Output } from '@angular/core';

@Component({
  selector: 'app-tabela-dados-auditoria',
  templateUrl: './tabela-dados-auditoria.component.html',
  styleUrls: ['./tabela-dados-auditoria.component.css'],
})
export class TabelaDadosAuditoriaComponent {

  @Input() lista: Array<any>;
  @Input() totalRecords: number;
  @Output() paginado: EventEmitter<any> = new EventEmitter();

  paginar(event) {
    this.paginado.emit(event);
  }

}
