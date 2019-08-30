import { Globals } from "./globals";
import { Mensagem } from "../Mensagem";

export class System extends Globals {
    constructor() {
        super();

        this._mensagens.push(...[
            new Mensagem("file-not-found", "Impossível realizar download de última geração"),
            new Mensagem("last-process-not-found", "Não há processamentos"),
            new Mensagem("dataFim-maior-dataInicio", "Data início não pode ser superior a data fim")
        ]);

        this._menu = [
            {
                "label": "Contas Eleitorais",
                "icon": "fas fa-list-ul",
                "heading": true,
                "items": [
                    {
                        "label": "Consultar Arquivos",
                        "icon": "fas fa-search",
                        "routerLink": "/consultarArquivos",
                        "roles": [
                            "DCE_MATRIZ",
                            "DCE_OPERADOR",
                            "DCE_AUDITORIA"
                        ]
                    },
                    {
                        "label": "Solicitar Afastamento",
                        "icon": "fas fa-ticket-alt",
                        "routerLink": "/solicitacao",
                        "roles": [
                            "DCE_MATRIZ",
                            "DCE_CONSULTA",                        ]
                    },
                    {
                        "label": "Consultar Solicitações",
                        "icon": "fas fa-search",
                        "routerLink": "/solicitacao/consultar",
                        "roles": [
                            "DCE_MATRIZ",
                            "DCE_CONSULTA",
                            "DCE_AUDITORIA"
                        ]
                    },
                    {
                        "label": "Gerar Arquivos",
                        "icon": "fas fa-upload",
                        "routerLink": "/upload",
                        "roles": [
                            "DCE_MATRIZ",
                            "DCE_OPERADOR"
                        ]
                    },
                    {
                        "label": "Solicitações Pendentes",
                        "icon": "fas fa-tasks",
                        "routerLink": "/solicitacao/pendentes",
                        "roles": [
                            "DCE_MATRIZ",
                            "DCE_OPERADOR",
                            "DCE_AUDITORIA"
                        ]
                    },
                    {
                        "label": "Realizar Afastamento",
                        "icon": "fas fa-door-open",
                        "routerLink": "/afastamento",
                        "roles": [
                            "DCE_MATRIZ",
                            "DCE_OPERADOR",
                        ]
                    },
                    {
                        "label": "Consultar Afastamentos",
                        "icon": "fas fa-search",
                        "routerLink": "/afastamento/consultar",
                        "roles": [
                            "DCE_MATRIZ",
                            "DCE_OPERADOR",
                            "DCE_AUDITORIA",
                        ]
                    }
                ]
            },
            {
                "label": "Contas Gerais",
                "icon": "fas fa-list-ul",
                "items": [
                    {
                        "label": "Solicitar Afastamento",
                        "icon": "fas fa-ticket-alt",
                        "routerLink": "/solicitacao-geral",
                        "roles": [
                            "DCE_MATRIZ",
                            "DCE_CONSULTA",
                        ],
                    },
                    {
                        "label": "Consultar Solicitações",
                        "icon": "fas fa-search",
                        "routerLink": "/solicitacao-geral/consultar",
                        "roles": [
                            "DCE_MATRIZ",
                            "DCE_CONSULTA",
                            "DCE_AUDITORIA"
                        ],
                    },
                    {
                        "label": "Solicitações Pendentes",
                        "icon": "fas fa-tasks",
                        "routerLink": "/solicitacao-geral/pendentes",
                        "roles": [
                            "DCE_MATRIZ",
                            "DCE_OPERADOR",
                            "DCE_AUDITORIA"
                        ]
                    },
                    {
                        "label": "Realizar Afastamento",
                        "icon": "fas fa-door-open",
                        "routerLink": "/afastamento-geral",
                        "roles": [
                            "DCE_MATRIZ",
                            "DCE_OPERADOR",
                        ]
                    },
                    {
                        "label": "Consultar Afastamentos",
                        "icon": "fas fa-search",
                        "routerLink": "/afastamento-geral/consultar",
                        "roles": [
                            "DCE_MATRIZ",
                            "DCE_OPERADOR",
                            "DCE_AUDITORIA",
                        ]
                    }
                ]
            },
            {
                "label": "Dados de Auditoria",
                "icon": "fas fa-shield-alt",
                "routerLink": "/auditoria",
                "roles": [
                    "DCE_MATRIZ",
                    "DCE_AUDITORIA"
                ]
            },
            {
                "label": "Parâmetros",
                "icon": "fas fa-database",
                "items": [
                    {
                        "label": "Integração TSE",
                        "icon": "fas fa-cogs",
                        "routerLink": "/parametros-integracao",
                        "roles": [
                            "DCE_MATRIZ"
                        ],
                    },
                    {
                        "label": "Log de Integração",
                        "icon": "fas fa-list-alt",
                        "routerLink": "/log-integracao",
                        "roles": [
                            "DCE_MATRIZ",
                            "DCE_AUDITORIA"
                        ],
                    },
                    {
                        "label": "Atualização de Lançamentos",
                        "icon": "fas fa-wrench",
                        "routerLink": "/lancamentos",
                        "roles": [
                            "DCE_MATRIZ",
                            "DCE_OPERADOR"
                        ],
                    },
                    {
                        "label": "Unidade Gestora",
                        "icon": "fas fa-users-cog",
                        "routerLink": "/unidade-gestora",
                        "roles": [
                            "DCE_MATRIZ"
                        ],
                    }
                ]
            }
        ];
    }

    static getInstance() {
        if (this.instance) {
            return this.instance;
        } else {
            return this.instance = new System();
        }
    }
}