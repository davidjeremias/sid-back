import { Component, OnInit } from '@angular/core';
import { VERSION } from '@angular/core';
import { FooterService } from 'src/app/services/footer/footer.service';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent implements OnInit {

  title = 'Modelo Sso';
  version: string;
  ng_version = VERSION.full;

  constructor( private footer: FooterService) { }

  ngOnInit() {
    this.findVersionSystem();
  }

  findVersionSystem(){
    this.footer.findVersionSystem().subscribe( response => {
      this.version = response.build.version;
    });
  }

}
