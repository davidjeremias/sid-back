import { DomSanitizer } from '@angular/platform-browser';
import { Injectable } from '@angular/core';
import { KeycloakService } from '../../auth/keycloak.service';


@Injectable({
  providedIn: 'root'
})
export class HeaderService {

  constructor(public sanitizer: DomSanitizer) {

  }

  public userName() {
    return KeycloakService.keyCloak.tokenParsed.given_name;
  }

  public userMatricula() {
    return KeycloakService.keyCloak.tokenParsed.preferred_username;
  }


}
