import { Injectable } from '@angular/core';
import { authConfig } from './auth.config';
import * as Keycloak from 'keycloak-js';
import axios from 'axios';
import { environment } from './../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class KeycloakService {

  public static keycloakAuth: any;

  static init(): Promise<any> {
    return new Promise((resolve, reject) => {
      const config = {
        'url': authConfig.issuer,
        'realm': 'intranet',
        'ssl-required': 'external',
        'bearer-only': true,
        'clientId': authConfig.clientId
      };
      this.keycloakAuth = Keycloak(config);
      this.keycloakAuth.init({ onLoad: 'login-required' })
        .success(authenticated => {
          if (authenticated) {
            axios.defaults.baseURL = environment.backendAddress;
            axios.defaults.headers.common['Authorization'] = 'Bearer ' + this.getToken();
            axios.interceptors.request.use((config) => {
              let originalRequest = config;
              if (this.keycloakAuth.isTokenExpired()) {
                return this.updateToken().then((token) => {
                  originalRequest.headers['Authorization'] = 'Bearer ' + token;
                  axios.defaults.headers.common['Authorization'] = 'Bearer ' + token;
                  return Promise.resolve(originalRequest);
                });
              }
              return config;
            }, (err) => {
              return Promise.reject(err);
            });
          }
          else {
            this.keycloakAuth.login();
          }
          resolve();
        })
        .error(() => {
          reject();
        });
    });
  }

  static updateToken() {
    return new Promise((resolve, reject) => {
      this.keycloakAuth.updateToken(5)
        .success(() => {
          resolve(this.keycloakAuth.token);
        })
        .error(reject);
    });
  }

  public static get keyCloak() {
    return this.keycloakAuth;
  }

  static getToken(): string {
    return this.keycloakAuth.token;
  }
}