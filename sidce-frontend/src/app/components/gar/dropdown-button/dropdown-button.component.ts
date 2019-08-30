import { Component, Input } from '@angular/core';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'gar-dropdown-button',
  templateUrl: './dropdown-button.component.html',
  styleUrls: ['./dropdown-button.component.css']
})
export class DropdownButtonComponent {

  @Input() itens;
  @Input() label: string;
  @Input() title: string;
  visivel = false;

  constructor() { }

  ngOnInit() {
  }

  toggle() {
    this.visivel = !this.visivel;
  }

}
