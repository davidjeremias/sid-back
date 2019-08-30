import { Component, OnInit, Output, EventEmitter, OnChanges, Input } from '@angular/core';
import { FileUpload } from 'primeng/fileupload';
import { ConsultarArquivosService } from 'src/app/services/consultarArquivos/consultar-arquivos.service';
import { Message, ConfirmationService, MessageService } from 'primeng/api';
import { Observable } from 'rxjs';
import base64Object from 'base64-js';
import { ThrowStmt } from '../../../../../node_modules/@angular/compiler';
import { System } from 'src/app/entidade/parametros/system';

@Component({
  selector: 'app-retorno-simba',
  templateUrl: './retorno-simba.component.html'
})
export class RetornoSimbaComponent implements OnInit, OnChanges {

  @Output() fechar: EventEmitter<any> = new EventEmitter();
  @Input() processo: any;
  arquivo: File;
  situacoes: any;
  situacao: any;
  solicitacao: any;
  tamanhoArquivo: number;
  nomeArquivo: any;
  msgs: Array<Message>;
  status: any;
  codigo: any;
  isArquivo: boolean = false;
  isDetalhamento: boolean = false;
  isExclusao: boolean = false;
  @Input() recebeAcao: any;
  @Input() afastamento: any = undefined;
  @Input() solic: any = false;
  acao: any;

  constructor(
    private consultaArquivo: ConsultarArquivosService,
    private confirmationService: ConfirmationService
  ) {
    this.tamanhoArquivo = 1000000;
    this.acao = this.recebeAcao;
  }

  ngOnInit() {
    this.consultaArquivo.buscarSituacoes().subscribe(e => {
      this.situacoes = e;
    });
  }

  ngOnChanges() {
    this.isDetalhamento = false;
    this.acao = this.recebeAcao;
    if(!this.solic){
      if(this.afastamento){
        this.status = this.afastamento.agendamentoETL.situacao.id;
        this.nomeArquivo = this.afastamento.arquivo.nomeArquivo;
        this.isArquivo = true;
        this.processo = this.afastamento.agendamentoETL.solicitacao;
        let arquivoBlob = new Blob([base64Object.toByteArray(this.afastamento.arquivo.bytesArquivo)], { type: 'application/pdf' });
        let arrayOfBlob = new Array<Blob>();
        arrayOfBlob.push(arquivoBlob);
        let arquivo: File = new File(arrayOfBlob, this.afastamento.arquivo.nomeArquivo, { type: 'application/pdf' });
        this.arquivo = arquivo;
        if(this.acao === 'detalhar') this.isDetalhamento = true;
        if(this.acao === 'excluir'){
          this.excluir(this.processo);
          this.isArquivo = false;
        } 
      }else{
        this.status = !this.processo ||  this.processo.situacaoSolicitacao.id === 6 ? null : this.processo.situacaoSolicitacao.id;
        this.arquivo = null;
        this.isArquivo = false;
      }
    }
    if(this.solic){
      if(this.acao === 'Incluir'){
        this.status =  null;
        this.arquivo = null;
        this.isArquivo = false;
      }
      if(this.acao === 'Alterar'){
        this.consultaArquivo.buscaAgendamentoRetornoSimba(this.processo.id).subscribe(data => {
          this.afastamento = data;
          this.status = this.afastamento.agendamentoETL.situacao.id;
          this.nomeArquivo = this.afastamento.arquivo.nomeArquivo;
          let arquivoBlob = new Blob([base64Object.toByteArray(this.afastamento.arquivo.bytesArquivo)], { type: 'application/pdf' });
          let arrayOfBlob = new Array<Blob>();
          arrayOfBlob.push(arquivoBlob);
          let arquivo: File = new File(arrayOfBlob, this.afastamento.arquivo.nomeArquivo, { type: 'application/pdf' });
          this.arquivo = arquivo;
          this.isArquivo = true;
        });
      }
      
    }
    
    this.solicitacao = this.processo;
  }

  uploadImage(event, fileUpload: FileUpload) {
    this.arquivo = event.files[0];
    this.nomeArquivo = this.arquivo.name;
    if(this.arquivo) this.isArquivo = true;
    fileUpload.clear();
  }

  validaArquivo(event): boolean {
    const arquivo: File = event.files[0];
    this.msgs = new Array<Message>();
    const isPdf = arquivo.type === "application/pdf";
    const isTamanhoValido = arquivo.size <= this.tamanhoArquivo;

    if (!isTamanhoValido){
      this.msgs.push({
        severity: 'error',
        summary: '',
        detail: 'Tamanho máximo de arquivo 1mb',
        closable: true
      });
      this.nomeArquivo = null;
    } 

    if (!isPdf) {
      this.msgs.push({
        severity: 'error',
        summary: '',
        detail: 'O arquivo fornecido pelo SIMBA deve ser do tipo PDF',
        closable: true
      });
      this.nomeArquivo = null;
    }

    return isPdf && isTamanhoValido ? true : false;
  }

  private validaSalvar(): boolean {
    this.msgs = new Array<Message>();

    if (!this.status) this.msgs.push({
      severity: 'error',
      summary: '',
      detail: 'Selecione um tipo de arquivo',
      closable: true
    });

    if (!this.arquivo) this.msgs.push({
      severity: 'error',
      summary: '',
      detail: 'Selecione um arquivo',
      closable: true
    });

    return this.status && this.arquivo ? true : false;
  }

  salvar() {
    if (this.validaSalvar()) {
      const envio = new FormData();
      envio.append("id", this.processo.id);
      envio.append("codigo", this.processo.codigo);
      envio.append("situacao", this.status);
      envio.append("tipo", "solicitacao")
      if (this.arquivo)
        envio.append("file", this.arquivo, this.arquivo.name);
      else
        envio.append("file", new File([""], "filename"));
      const retorno: Observable<any> = this.processo.situacaoSolicitacao.id === 6 ?
        this.consultaArquivo.inserirRetornoSimba(envio) :
        this.consultaArquivo.alterarRetornoSimba(envio);
      retorno.subscribe(response => {
        this.processo = response;
        this.solicitacao = response.solicitacao;
        this.msgs = new Array<Message>();
        this.arquivo = null;
        this.fechar.emit({response: true, processo: response});
        this.acao = null;
      });
    }
  }

  salvarConsultaArquivo() {
    if (this.validaSalvar()) {
      const envio = new FormData();
      envio.append("id", this.processo.id);
      envio.append("codigo", this.processo.codigo);
      envio.append("situacao", this.status);
      envio.append("tipo", "agendamento")
      if (this.arquivo)
        envio.append("file", this.arquivo, this.arquivo.name);
      else
        envio.append("file", new File([""], "filename"));
      const retorno: Observable<any> = this.consultaArquivo.inserirRetornoSimba(envio);
      retorno.subscribe(response => {
        this.processo = response;
        this.solicitacao = response.solicitacao;
        this.msgs = new Array<Message>();
        this.arquivo = null;
        this.fechar.emit({response: true, processo: response});
        this.acao = null;
      });
    }
  }

  excluir(item) {
    this.confirmationService.confirm({
      message: 'Deseja realmente excluir?',
      header: 'Mensagem de confirmação:',
      icon: 'pi pi-info-circle',
      accept: () => {
        this.consultaArquivo.removerRetornoSimba(item)
      .subscribe(() => {
        this.afastamento = null;
        this.status = null;
        this.situacao = null;
        this.arquivo = null;
        this.nomeArquivo = null;
        this.acao = null;
        this.isDetalhamento = true;
        this.fechar.emit(true);
      });
      },
      reject: () => {
        return;
      }
  });
  }

  alterar(){
    this.isDetalhamento = false;
    this.acao = 'alterar';
  }

  alterarConsultaArquivo(){
    if (this.validaSalvar()) {
      const envio = new FormData();
      envio.append("id", this.processo.id);
      envio.append("codigo", this.processo.codigo);
      envio.append("situacao", this.status);
      envio.append("tipo", "agendamento")
      if (this.arquivo)
        envio.append("file", this.arquivo, this.arquivo.name);
      else
        envio.append("file", new File([""], "filename"));
      const retorno: Observable<any> = this.consultaArquivo.alterarRetornoSimba(envio);
      retorno.subscribe(response => {
        this.processo = response;
        this.solicitacao = response.solicitacao;
        this.msgs = new Array<Message>();
        this.arquivo = null;
        this.fechar.emit({response: true, processo: response});
        this.acao = null;
      });
    }
  }

  download(item) {
    const arquivoBlob = new Blob([base64Object.toByteArray(this.afastamento.arquivo.bytesArquivo)], { type: 'application/pdf' });
    System.downloadFile(arquivoBlob, this.afastamento.arquivo.nomeArquivo);
  }

}
