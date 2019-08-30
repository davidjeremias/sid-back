import { HttpClientModule } from '@angular/common/http';
import { APP_INITIALIZER, NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { DataTableModule } from 'angular-6-datatable';
import { ToastrModule } from 'ngx-toastr';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { CalendarModule } from 'primeng/calendar';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { DialogModule } from 'primeng/dialog';
import { DropdownModule } from 'primeng/dropdown';
import { FileUploadModule } from 'primeng/fileupload';
import { InputMaskModule } from 'primeng/inputmask';
import { InputTextModule } from 'primeng/inputtext';
import { MessageModule } from 'primeng/message';
import { MessagesModule } from 'primeng/messages';
import { MultiSelectModule } from 'primeng/multiselect';
import { OverlayPanelModule } from 'primeng/overlaypanel';
import { PaginatorModule } from 'primeng/paginator';
import { RadioButtonModule } from 'primeng/radiobutton';
import { TableModule } from 'primeng/table';
import { TabMenuModule } from 'primeng/tabmenu';
import { ToastModule } from 'primeng/toast';
import { AppRoutingModule } from './/app-routing.module';
import { AppComponent } from './app.component';
import { KeycloakService } from './auth/keycloak.service';
import { AfastamentoSolicitacaoComponent } from './components/afastamento-solicitacao/afastamento-solicitacao.component';
import { ConsultarAfastamentosSolicitacoesComponent } from './components/afastamento-solicitacao/consultar/consultar-afastamentos-solicitacoes.component';
import { DetalharAfastamentoSolicitacaoComponent } from './components/afastamento-solicitacao/detalhar/detalhar.component';
import { TabelaAfastamentosComponent } from './components/afastamento/tabela-afastamentos/tabela-afastamentos.component';
import { ConsultarArquivosComponent } from './components/consultar-arquivos/consultar-arquivos.component';
import { RetornoSimbaComponent } from './components/consultar-arquivos/retorno-simba/retorno-simba.component';
import { DadosAuditoriaComponent } from './components/dados-auditoria/dados-auditoria.component';
import { TabelaDadosAuditoriaComponent } from './components/dados-auditoria/tabela-dados-auditoria/tabela-dados-auditoria.component';
import { DropdownButtonComponent } from './components/gar/dropdown-button/dropdown-button.component';
import { MainMenuComponent } from './components/gar/main-menu/main-menu.component';
import { VisualizarArquivosComponent } from './components/geracao-cinco-arquivos/visualizar-arquivos/visualizar-arquivos.component';
import { GerarArquivosComponent } from './components/gerar-arquivos/file-upload.component';
import { NotificationsComponent } from './components/notifications/notifications.component';
import { NotificationsService } from './components/notifications/notifications.service';
import { ParametrosSistemaComponent } from './components/parametros-sistema/parametros-sistema.component';
import { AnalisarSolicitacaoComponent } from './components/solicitacao/analisar/analisar-solicitacao.component';
import { TabelaSolicitacoesComponent } from './components/solicitacao/tabela/tabela-solicitacoes.component';
import { CpfCnpjValidatorDirective } from './directives/cpf-cnpj.directive';
import { Globals } from './entidade/parametros/globals';
import { MainService } from './services/main.service';
import { UtilsComponent } from './sidce/utils/utils.component';
import { FooterComponent } from './template/footer/footer.component';
import { HeaderComponent } from './template/header/header.component';
import { DetalharAfastamentoComponent } from './components/afastamento/detalhar-afastamento/detalhar-afastamento.component';
import { ParametrosIntegracaoTseComponent } from './components/parametros-integracao-tse/parametros-integracao-tse.component';
import { LogIntegracaoComponent } from './components/log-integracao/log-integracao.component';
import { LancamentosComponent } from './components/lancamentos/lancamentos.component';
import { UnidadeGestoraComponent } from './components/unidade-gestora/unidade-gestora.component';
import { DetalharLogIntegracaoComponent } from './components/log-integracao/detalhar-log-integracao/detalhar-log-integracao.component';
import { AlterarCodigoLancamentoComponent } from './components/lancamentos/alterar-codigo-lancamento/alterar-codigo-lancamento.component';
import { AtribuirCodigoLancamentoComponent } from './components/lancamentos/atribuir-codigo-lancamento/atribuir-codigo-lancamento.component';

export function kcFactory(): () => void {
  return () => KeycloakService.init();
}

@NgModule({
  declarations: [
    AppComponent,
    FooterComponent,
    HeaderComponent,
    GerarArquivosComponent,
    ConsultarArquivosComponent,
    UtilsComponent,
    VisualizarArquivosComponent,
    RetornoSimbaComponent,
    NotificationsComponent,
    DadosAuditoriaComponent,
    TabelaDadosAuditoriaComponent,
    MainMenuComponent,
    AfastamentoSolicitacaoComponent,
    CpfCnpjValidatorDirective,
    ParametrosSistemaComponent,
    DetalharAfastamentoSolicitacaoComponent,
    AnalisarSolicitacaoComponent,
    TabelaAfastamentosComponent,
    TabelaSolicitacoesComponent,
    ConsultarAfastamentosSolicitacoesComponent,
    DropdownButtonComponent,
    DetalharAfastamentoComponent,
    ParametrosIntegracaoTseComponent,
    LogIntegracaoComponent,
    LancamentosComponent,
    UnidadeGestoraComponent,
    DetalharLogIntegracaoComponent,
    AlterarCodigoLancamentoComponent,
    AtribuirCodigoLancamentoComponent
  ],
  imports: [
    BrowserModule,
    DataTableModule,
    AppRoutingModule,
    DropdownModule,
    FileUploadModule,
    DialogModule,
    HttpModule,
    HttpClientModule,
    InputMaskModule,
    InputTextModule,
    MultiSelectModule,
    CalendarModule,
    RadioButtonModule,
    PaginatorModule,
    ButtonModule,
    TabMenuModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot(),
    MessageModule,
    MessagesModule,
    FormsModule,
    ReactiveFormsModule,
    ToastModule,
    TableModule,
    OverlayPanelModule,
    ConfirmDialogModule
  ],
  providers: [HttpClientModule, HttpModule, ConfirmationService, MessageService, MainService, NotificationsService, Globals,
    {
      provide: APP_INITIALIZER,
      useFactory: kcFactory,
      multi: true
    }],
  bootstrap: [AppComponent]
})
export class AppModule { }
