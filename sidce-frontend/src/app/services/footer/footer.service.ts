import { Injectable } from '@angular/core';
import { MainService } from '../main.service';

@Injectable({
  providedIn: 'root'
})
export class FooterService extends MainService {

  BASE_URL = "/info";

  findVersionSystem() { return this.mainGet(this.BASE_URL) }

}
