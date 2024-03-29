import { AuthConfig } from 'angular-oauth2-oidc';
import { environment } from '../../environments/environment';

export const authConfig: AuthConfig = {
  issuer: environment.issuer,
  redirectUri: window.location.origin + '/',
  clientId: 'cli-web-dce',
};
