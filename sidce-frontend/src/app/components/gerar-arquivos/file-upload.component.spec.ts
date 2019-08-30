import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GerarArquivosComponent } from './file-upload.component';

describe('FileUploadComponent', () => {
  let component: GerarArquivosComponent;
  let fixture: ComponentFixture<GerarArquivosComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GerarArquivosComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GerarArquivosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
