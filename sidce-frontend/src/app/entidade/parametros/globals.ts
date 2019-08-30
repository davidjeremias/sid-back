import { Injectable } from "@angular/core";
import { FormGroup, NgForm } from "@angular/forms";
import { KeycloakService } from "src/app/auth/keycloak.service";
import { Mensagem } from "../Mensagem";

@Injectable()
export class Globals {

    matricula: string;
    static matricula;
    static unidade: string;
    static instance: Globals;

    constructor() {
        this.matricula = Globals.matricula = KeycloakService.keyCloak.tokenParsed.preferred_username;
        Globals.unidade = KeycloakService.keyCloak.tokenParsed.unidade;
    }

    ptbr = {
        firstDayOfWeek: 0,
        dayNames: ["Domingo", "Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira", "Sábado"],
        dayNamesShort: ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab"],
        dayNamesMin: ["D", "S", "T", "Q", "Q", "S", "S"],
        monthNames: ["Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"],
        monthNamesShort: ["Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"],
        today: 'Hoje',
        clear: 'Limpar',
        dateFormat: 'dd/mm/yy'
    }

    codigosDebito = [
        {label: "101 para cheques", id: '101'},
        {label: "102 para encargos", id: '102'},
        {label: "103 para estornos", id: '103'},
        {label: "104 para lançamento avisado", id: '104'},
        {label: "105 para tarifas", id: '105'},
        {label: "106 para aplicação", id: '106'},
        {label: "107 para empréstimo/financiamento", id: '107'},
        {label: "108 para câmbio", id: '108'},
        {label: "109 para CPMF", id: '109'},
        {label: "110 para IOF", id: '110'},
        {label: "111 para imposto de renda", id: '111'},
        {label: "112 para pagamento fornecedores", id: '112'},
        {label: "113 para pagamento salário", id: '113'},
        {label: "114 para saque eletrônico", id: '114'},  
        {label: "115 para ações", id: '115'},
        {label: "117 para transferência entre contas", id: '117'},
        {label: "118 para devolução da compensação", id: '118'},
        {label: "119 para devolução de cheque depositado", id: '119'},
        {label: "120 para transferência interbancária (DOC, TED)", id: '120'},
        {label: "121 para antecipação a fornecedores", id: '121'},
        {label: "122 para OC/AEROPS", id: '122'}
      ];
  
      codigosCredito = [
        {label: "201 para cheques", id: '201'},
        {label: "202 para lìquido de cobrança", id: '202'},
        {label: "203 para devolução de cheques", id: '203'},
        {label: "204 para estornos", id: '204'},
        {label: "205 para lançamento avisado", id: '205'},
        {label: "206 para resgate e aplicação", id: '206'},
        {label: "207 para empréstimo/financiamento", id: '207'},
        {label: "208 para câmbio", id: '208'},
        {label: "209 para transferência interbancária (DOC, TED)", id: '209'},
        {label: "210 para ações", id: '210'},
        {label: "211 para dividendos", id: '211'},
        {label: "212 para seguro", id: '212'},
        {label: "213 para transferência entre contas", id: '213'},
        {label: "214 para depósitos especiais", id: '214'},
        {label: "215 para devolução para compensação", id: '215'},
        {label: "216 para OCT", id: '216'},
        {label: "217 para pagamento de fornecedores", id: '217'},
        {label: "218 para pagamentos diversos", id: '218'},
        {label: "219 para pagamentos salariais", id: '219'}
      ];

      naturezas = [
        {label: "Todos", id: null},
        {label: "Crédito", id: 'C'},
        {label: "Débito", id: 'D'}
      ];

    protected _menu = [];

    get menu() {
        return this.copyList(this._menu);
    }

    protected _mensagens: Array<Mensagem> = [
        new Mensagem(1, "Operação realizada com sucesso"),
        new Mensagem(2, "Nenhum Registro Encontrado"),
        new Mensagem(3, "Data Inválida"),
        new Mensagem(4, "Não foi possível realizar a operação, por favor, entre em contato com o administrador do sistema"),
        new Mensagem(5, "Inclusão realizada com sucesso")
    ];

    getMsg(param: String | number): Mensagem {
        let identificador;
        if (param instanceof String || typeof param == "string") {
            const codigo = param;
            switch (codigo) {
                case "sucesso":
                    identificador = 1;
                    break;
                case "vazio":
                    identificador = 2;
                    break;
                case "data":
                    identificador = 3;
                    break;
                default:
                    identificador = codigo;
            }
        } else {
            identificador = param;
        }
        const retorno = this._mensagens.find(e => e.identificador === identificador);
        return retorno ? retorno : new Mensagem(null, param.toString());
    }

    copy(object) {
        return Object.assign({}, object);
    }

    copyList(list) {
        return JSON.parse(JSON.stringify(list));
    }

    limpar(elemento) {
        if (elemento instanceof FormGroup) {
            elemento.reset();
            return;
        }

        if (elemento instanceof NgForm) {
            elemento.resetForm();
            return;
        }

        console.error("Ainda não é possível limpar esse elemento, necessitando implementação adequada.");
    }

    static getInstance(): Globals {
        if (this.instance) {
            return this.instance;
        } else {
            this.instance = new Globals();
        }
    }

    static downloadFile(blobFile, name) {
        const a = document.createElement('a');
        a.href = window.URL.createObjectURL(blobFile);
        a.download = name;
        document.body.appendChild(a);
        a.click()
        document.body.removeChild(a);
    }

    static verificaPermissao(permissoes: Array<string>){
        var isPermitido = false;
         var roles = KeycloakService.keyCloak.realmAccess.roles;
         isPermitido = permissoes.some(permissao => roles.includes(permissao));
         return isPermitido;
    }
}