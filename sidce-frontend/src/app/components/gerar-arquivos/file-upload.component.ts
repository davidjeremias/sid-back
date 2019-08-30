import { UtilsComponent } from './../../sidce/utils/utils.component';
import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { Router } from '@angular/router';
import { FileService } from 'src/app/services/file.service';
import { ConsultarArquivosService } from 'src/app/services/consultarArquivos/consultar-arquivos.service';
import { Message } from 'primeng/api';
import base64Object from 'base64-js';


const cnpj_partido: string = 'cnpj_partido_';
const cnpj_candidatos: string = 'cnpj_candidatos_';
const partido: number = 1;
const partido_candidatos: number = 2;
const txt: string = '.txt';

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.css']
})


export class GerarArquivosComponent implements OnInit {

  constructor(private fileService: FileService, private consultaArquivo: ConsultarArquivosService, private router: Router) { }

  utils = new UtilsComponent();
  dadosArquivo: any;
  dadosArquivoArray = new Array;
  validarArquivo: boolean = false;
  qtdArquivo: number;
  mostraUpload = false;
  ano: any;
  mes: any;
  validacao_Tabela: any;
  bloqueiaGerar: boolean = false;
  usuario: String;
  dataAtual: number;
  dataProcessamento: any;
  arquivos: any;
  gerandoArquivo: boolean = false;
  msgError: any;
  erroCampo: boolean = false;
  msgErro = new Array;
  isValido: boolean;
  ldUpload: boolean = false;

  ngOnInit() {
    this.getUltimo();
    this.mes = this.pegaMesAnterior();
    this.ano = this.pegaAno();
    this.consultaArquivo.isUltimaGeracao()
      .subscribe(retorno => {
        this.isValido = retorno;
        if (!this.isValido) {
          this.msgs.push({
            severity: 'warn',
            summary: '',
            detail: 'Geração bloqueada por estar aguardando retorno simba ou último processamento do ETL.'
          })
        }
      });
  }

  msgs: Array<Message> = new Array<Message>();

  clear() {
    this.msgs = [];
  }

  onFileChanged(event, component) {
    event.files.forEach(file => {
      if (this.valida_documento(file.name, false, false)) {
        this.dadosArquivo = file;
        this.dadosArquivoArray.push(this.dadosArquivo);
      }
      component.clear();

      if (this.dadosArquivoArray.length >= this.qtdArquivo) {
        this.validarArquivo = true;
      }
    });

  }

  private getUltimo() {
    this.fileService.ultimoConcluido()
      .subscribe(response => {
        this.usuario = response.matricula;
        this.dataProcessamento = response.dtHrProcessamento;
      });
  }

  downloadUltimo() {
    this.fileService.downloadUltimo()
      .subscribe(response => {
        const blob = new Blob([base64Object.toByteArray(response)], { type: 'application/zip' });
        // window.open(URL.createObjectURL(blob), "batatinha quando nasce");
        const a = document.createElement('a');
        a.href = window.URL.createObjectURL(blob);
        a.download = `arquivo_tse_${this.dataProcessamento}`;
        document.body.appendChild(a);
        a.click()
        document.body.removeChild(a);
      });
  }

  // chama o service para realizar o upload do(s) arquivo(s) 
  gerarArquivo() {
    let validaPartido = false;
    let validarCandidato = false;
    let cont: number = 1;


    while (cont <= this.qtdArquivo) {

      // percorre o arquivo (File)
      this.dadosArquivoArray.forEach(element => {

        // verifica se a quantidade de arquivo é a mesma do array 
        // caso sucesso ele valida os documentos caso contrário lança
        // o erro informando o usuário para inserir mais 1 documennto
        if (this.qtdArquivo == this.dadosArquivoArray.length) {
          // this.valida_documento(element.name, validarCandidato, validaPartido);
        } else {
          this.mensagens(`É necessário submeter o arquivo cnpj_candidatos_${this.ano}.txt e cnpj_partido_${this.ano}.txt`);
          this.erroCampo = true;
        }

        this.verifica_Conteudo_Arquivo(element.size, element.name);

        this.verifica_Extencao_Arquivo(element.name);
      });

      cont++;
    }

    if (!this.erroCampo) {
      // exibe a modal de geração de arquivo
      this.gerandoArquivo = true;

      // pega a data atual para exibir na tabela
      this.dataAtual = Date.now();

      //bloqueia o botão gerar enquanto o arquivo esta sendo gerado
      this.bloqueiaGerar = true;

      this.ldUpload = true;

      this.fileService.adicionar(this.dadosArquivoArray)
        .subscribe(() => {
          this.limpar();
          this.limparMensagens()
          this.isValido = false;
          this.msgs.push({ severity: "success", summary: "Operação realizada com sucesso" });
          this.ldUpload = false;
        });

      setTimeout(() => {
        this.validacao_Tabela = this.fileService.retornoStatus;
        this.arquivos = this.fileService.resposta;

        //cancela a modal (caregar arquivos) após gerar os arquivos 
        this.gerandoArquivo = false;
      }, 3000);
    }

  }


  baixarArquivo() {
    this.arquivos.forEach(element => {
      this.fileService.baixarArquivos(element.fileDownloadUri, element.fileName);
    });
  }

  //verifica se os 5 arquivos foram gerados
  verifica_geracao_arquivos() {
    this.erroCampo = true;
    this.mensagens("Ocorreu um erro ao gerar os arquivos, por favor, entre em contato com o administrador do sistema ");
  }

  //verifica se o arquivo tem algum conteudo
  verifica_Conteudo_Arquivo(arquivo, nome_Arquivo) {
    if (arquivo <= 3) {
      this.erroCampo = true;
      this.mensagens("Não foram encontradas informações para o(s) arquivo(s) " + nome_Arquivo);
    }
  }

  validaTipo(event) {
    const lista: Array<File> = Array.from(event.files);
    lista.forEach(element => this.verifica_Extencao_Arquivo(element.name));
  }

  // verifica se o nome do arquivo esta correto
  valida_documento(nome_arquivo: string, validarCandidato: boolean, validaPartido: boolean): boolean {
    const arquivoPartido = cnpj_partido + this.ano + txt;
    const arquivoCandida = cnpj_candidatos + this.ano + txt;
    const arquivosIguais = this.dadosArquivoArray.some(e => e.name === nome_arquivo);
    let retorno;

    if (arquivosIguais) {
      this.mensagens("Arquivo já adicionado"); 
      return false;
    }

    /*switch (this.qtdArquivo) {
      case 1:
        retorno = nome_arquivo !== arquivoPartido;
        if (retorno)
      this.mensagens(`É necessário submeter o arquivo cnpj_partido_${this.ano}.txt`);
        else
          this.limparMensagens();
        break;
      case 2:
        retorno = (nome_arquivo !== arquivoCandida && nome_arquivo !== arquivoPartido) || arquivosIguais;
        if (retorno)
          this.mensagens(`É necessário submeter o arquivo cnpj_candidatos_${this.ano}.txt e cnpj_partido_${this.ano}.txt`);
        else 
          this.limparMensagens();
        break;
    }*/

    return true;
  }

  private limparMensagens() {
    this.msgs = new Array();
  }

  // valia se a extenção é .TXT
  verifica_Extencao_Arquivo(arquivo: string) {
    if (arquivo.substr(-4) != txt) {
      this.erroCampo = true;
      this.mensagens("Os arquivos fornecidos pelo TSE devem ser do tipo txt");
    }
  }

  pegaAno() {
    let data = new Date();
    let ano = data.getFullYear();

    return [ano].join('/');
  }

  pegaMesAnterior() {
    let data = new Date();
    let mes = data.getMonth();
    //quando o mês for JANEIRO 
    if (mes == 0) {
      this.ano = data.getFullYear() - 1;
    }
    return this.utils.nome_Mes([mes].join('/'));
  }

  // pega o valor do radio button e seta na variável
  // qtd arquivo para saber a quantidade de upload que será feito
  validarUpload(event: number) {
    this.limparMensagens();
    this.limpar();
    if (event != 0) {
      this.mostraUpload = true;
      this.qtdArquivo = event;
    } else {
      this.limpar();
    }
  }

  @ViewChild('inputFile') inputVariable: ElementRef;

  limpar() {

    this.validarArquivo = false;
    this.dadosArquivo = null;
    this.dadosArquivoArray = [];
    this.validacao_Tabela = null;
    this.bloqueiaGerar = false;
    this.gerandoArquivo = false;
    this.msgError = null;
    this.erroCampo = false;
    this.inputVariable.nativeElement.value = '';

    this.router.navigate['/upload'];
  }

  mensagens(codigoMsg: String) {
    this.limparMensagens();
    this.msgs.push({ severity: "error", summary: "Atenção", detail: codigoMsg.toString() });
  }

}
