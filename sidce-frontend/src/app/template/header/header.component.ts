import { HeaderService } from './../../services/header/header.service';
import { Component, OnInit } from '@angular/core';
import {Location} from '@angular/common';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/filter';

import { DomSanitizer } from '@angular/platform-browser';
import { KeycloakService } from '../../auth/keycloak.service';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';


declare var require: any;

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
  userName: String;
  userMatricula: String;

  title = 'SIGILO BANCÁRIO';
  public ativaMenu: Boolean = false;

  constructor(
    public sanitizer: DomSanitizer,
  ) {

  }

  ngOnInit() {
    this.nomeUsuario();
    this.usuarioMatricula();
  }

  public logoff() {
    KeycloakService.keyCloak.logout({"redirectUri": window.location.origin });
  }

  nomeUsuario() {
    this.userName = KeycloakService.keyCloak.tokenParsed.given_name;
  }

  usuarioMatricula() {
    this.userMatricula = KeycloakService.keyCloak.tokenParsed.preferred_username;
  }

}
