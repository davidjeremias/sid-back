import { Component, isDevMode } from '@angular/core';
import { KeycloakService } from './auth/keycloak.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  usuario: boolean;
  roles = [
    "DCE_MATRIZ",
    "DCE_OPERADOR",
    "DCE_CONSULTA",
    "DCE_AUDITORIA"
  ];

  constructor() {
    const roles = KeycloakService.keyCloak.realmAccess.roles;
    this.usuario = roles.some(e => this.roles.includes(e)) || isDevMode ? true : false;
  }
}