import { Component, Input } from '@angular/core';
import { System } from 'src/app/entidade/parametros/system';
import { SolicitacaoService } from 'src/app/services/solicitacao/solicitacao.service';

@Component({
  selector: 'app-detalhar-afastamento',
  templateUrl: './detalhar-afastamento.component.html',
  styleUrls: ['./detalhar-afastamento.component.css']
})
export class DetalharAfastamentoComponent {

  /**
   * Solicitação atualmente sendo detalhada pelo componente
  */
  @Input() solicitacao: any;

  /**
   * Solicitação eleitoral?
   * 
   * @return {boolean} true: eleitoral; false: geral
  */
  @Input() isEleitoral: boolean;

  /**
   * Requisição de download atualmente em andamento?
   * 
   * @return {boolean} Valor boleano
  */
  isDownloadAndamento = false;

  titulo: string;

  @Input() isPendentes: boolean = false;

  constructor(
    private service: SolicitacaoService
  ) { }

  ngOnInit() {
    this.titulo = this.gerarTitulo();
  }

  /**
   * Download do arquivo de ofício de solicitação
   * 
   * @param solicitacao Solicitação que será realizada o download
  */
  download(solicitacao) {
    this.isDownloadAndamento = true;
    this.service.busca(solicitacao.id).subscribe(solicitacao => {
      System.downloadFile(solicitacao.arquivoBlob, solicitacao.arquivo.name);
      this.isDownloadAndamento = false;
    });
  }

  gerarTitulo() {
    var titulo = "Detalhar Afastamento - Contas ";
    return this.isEleitoral ? titulo + "eleitorais" : titulo + "gerais";
  }

  gerarData(solicitacao: any) {
    if (solicitacao) {
      return solicitacao.rascunho ? solicitacao.dtHoraCadastro : solicitacao.dtHoraAnalise;
    }
  }

}
