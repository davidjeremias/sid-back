import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AfastamentoSolicitacaoComponent } from './components/afastamento-solicitacao/afastamento-solicitacao.component';
import { ConsultarAfastamentosSolicitacoesComponent } from './components/afastamento-solicitacao/consultar/consultar-afastamentos-solicitacoes.component';
import { ConsultarArquivosComponent } from './components/consultar-arquivos/consultar-arquivos.component';
import { DadosAuditoriaComponent } from './components/dados-auditoria/dados-auditoria.component';
import { VisualizarArquivosComponent } from './components/geracao-cinco-arquivos/visualizar-arquivos/visualizar-arquivos.component';
import { GerarArquivosComponent } from './components/gerar-arquivos/file-upload.component';
import { ParametrosSistemaComponent } from './components/parametros-sistema/parametros-sistema.component';
import { AnalisarSolicitacaoComponent } from './components/solicitacao/analisar/analisar-solicitacao.component';
import { ParametrosIntegracaoTseComponent } from './components/parametros-integracao-tse/parametros-integracao-tse.component';
import { UnidadeGestoraComponent } from './components/unidade-gestora/unidade-gestora.component';
import { LogIntegracaoComponent } from './components/log-integracao/log-integracao.component';
import { LancamentosComponent } from './components/lancamentos/lancamentos.component';
import { DetalharLogIntegracaoComponent } from './components/log-integracao/detalhar-log-integracao/detalhar-log-integracao.component';
import { AtribuirCodigoLancamentoComponent } from './components/lancamentos/atribuir-codigo-lancamento/atribuir-codigo-lancamento.component';
import { AlterarCodigoLancamentoComponent } from './components/lancamentos/alterar-codigo-lancamento/alterar-codigo-lancamento.component';

const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'upload', component: GerarArquivosComponent },
  { path: 'consultarArquivos', component: ConsultarArquivosComponent },
  { path: 'visualizarArquivos/:codigo/:codigoSolicitacao', component: VisualizarArquivosComponent },
  { path: 'visualizarArquivos/:codigo/:isEleitoral/:codigoSolicitacao', component: VisualizarArquivosComponent },
  { path: 'auditoria', component: DadosAuditoriaComponent },
  { path: 'solicitacao', component: AfastamentoSolicitacaoComponent, data: { "eleitoral": true, "afastamento": false } },
  { path: 'solicitacao/consultar', component: ConsultarAfastamentosSolicitacoesComponent, data: { "eleitoral": true, "afastamento": false } },
  { path: 'solicitacao/pendentes', component: ConsultarAfastamentosSolicitacoesComponent, data: { "eleitoral": true, "pendentes": true } },
  { path: 'solicitacao/pendentes/:id', component: AnalisarSolicitacaoComponent, data: { "eleitoral": true } },
  { path: 'solicitacao/:id', component: AfastamentoSolicitacaoComponent, data: { "eleitoral": true, "afastamento": false } },
  { path: 'solicitacao-geral', component: AfastamentoSolicitacaoComponent, data: { "eleitoral": false, "afastamento": false } },
  { path: 'solicitacao-geral/consultar', component: ConsultarAfastamentosSolicitacoesComponent, data: { "eleitoral": false, "afastamento": false  } },
  { path: 'solicitacao-geral/pendentes', component: ConsultarAfastamentosSolicitacoesComponent, data: { "eleitoral": false, "pendentes": true } },
  { path: 'solicitacao-geral/pendentes/:id', component: AnalisarSolicitacaoComponent },
  { path: 'solicitacao-geral/:id', component: AfastamentoSolicitacaoComponent, data: { "eleitoral": false, "afastamento": false } },
  { path: 'afastamento', component: AfastamentoSolicitacaoComponent, data: { "eleitoral": true, "afastamento": true } },
  { path: 'afastamento/consultar', component: ConsultarAfastamentosSolicitacoesComponent, data: { "eleitoral": true, "afastamento": true } },
  { path: 'afastamento/:id', component: AfastamentoSolicitacaoComponent, data: { "eleitoral": true, "afastamento": true } },
  { path: 'afastamento-geral', component: AfastamentoSolicitacaoComponent, data: { "eleitoral": false, "afastamento": true } },
  { path: 'afastamento-geral/consultar', component: ConsultarAfastamentosSolicitacoesComponent, data: { "eleitoral": false, "afastamento": true } },
  { path: 'afastamento-geral/consultar/eleitoral', component: ConsultarAfastamentosSolicitacoesComponent, data: { "eleitoral": true, "afastamento": true } },
  { path: 'afastamento-geral/:id', component: AfastamentoSolicitacaoComponent, data: { "eleitoral": false, "afastamento": true } },
  { path: 'params', component: ParametrosSistemaComponent},
  { path: 'parametros-integracao', component: ParametrosIntegracaoTseComponent},
  { path: 'unidade-gestora', component: UnidadeGestoraComponent},
  { path: 'log-integracao', component: LogIntegracaoComponent},
  { path: 'lancamentos', component: LancamentosComponent},
  { path: 'detalhar-log-integracao/:id', component: DetalharLogIntegracaoComponent},
  { path: 'atribuir-codigo-lancamento', component: AtribuirCodigoLancamentoComponent},
  { path: 'alterar-codigo-lancamento', component: AlterarCodigoLancamentoComponent},
  { path: 'consulta-lancamento', component: LancamentosComponent}
];

@NgModule({
  exports: [RouterModule],
  imports: [RouterModule.forRoot(routes, { useHash: false, scrollPositionRestoration: 'enabled'})],
})
export class AppRoutingModule { }
