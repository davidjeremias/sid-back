import { Injectable, Optional } from "@angular/core";
import Axios, { AxiosRequestConfig, AxiosResponse } from "axios";
import { Observable } from "rxjs";
import { NotificationsService } from "../components/notifications/notifications.service";
import { Globals } from "../entidade/parametros/globals";
import { System } from "../entidade/parametros/system";

/**
 * Service a ser extendida nas diversas services utilizadas pelo sistema,
 * necessitando apenas de implementações de endpoint para funcionamento padrão das aplicações
 * desenvolvidas pela Spread.
 * @author allef.garug
 * @version 2.2.5
 */
@Injectable()
export class MainService {

    constructor(
        @Optional() public messageService: NotificationsService,
        @Optional() private globals: Globals
    ) {
        this.globals = System.getInstance();
    }

    /**
     * RRequest padrão get
     * Recupera recursos do servidor baseado ou não em parâmetros
     * 
     * @param {string} endpoint Endpoint da request
     * @param {object} parametros json contendo parâmetros da requisição do tipo get
     * @return {Observable} Observable contendo requisição após processamento
     */
    protected mainGet(endpoint: string, parametros?: object): Observable<any> {
        const params: AxiosRequestConfig = parametros;
        return this.request(Axios.get(endpoint, { params }));
    }

    /**
     * Request padrão post
     * Insere um novo recurso
     * 
     * @param {string} endpoint Endpoint da request
     * @param data Objeto ou lista de objetos enviados juntos a requisição
     * @return {Observable} Observable contendo requisição após processamento
     */
    protected mainPost(endpoint: string, data, customMessage?: string): Observable<any> {
        if (data.length === 1) {
            return this.request(Axios.post(endpoint, data[0]), customMessage);
        } else {
            return this.request(Axios.post(endpoint, data), customMessage);
        }
    }

    /**
     * Request padrão put
     * Altera um recurso já existente
     * 
     * @param {string} endpoint Endpoint da request
     * @param data Objeto ou lista de objetos enviados juntos a requisição
     * @return {Observable} Observable contendo requisição após processamento
     */
    protected mainPut(endpoint: string, data, customMessage?: string): Observable<any> {
        if (data.length === 1) { 
            return this.request(Axios.put(endpoint, data[0]), customMessage);
        } else {
            return this.request(Axios.put(endpoint, data), customMessage);
        }
    }

    /**
     * Request padrão delete
     * Realiza exclusão física ou lógica de recurso
     * 
     * @param {string} endpoint Endpoint da request
     * @param data Objeto ou lista de objetos enviados juntos a requisição
     * @return {Observable} Observable contendo requisição após processamento
     */
    protected mainDelete(endpoint: string, customMessage?: string): Observable<any> {
        // const params: AxiosRequestConfig = parametros;
        return this.request(Axios.delete(endpoint), customMessage);
    }

    protected mainPatch(endpoint: string, data, customMessage?: string): Observable<any> {
        if (data.length === 1) { 
            return this.request(Axios.put(endpoint, data[0]), customMessage);
        } else {
            return this.request(Axios.put(endpoint, data), customMessage);
        }
    }


    /**
     * Request mockada para testes
     * 
     * @param obj Retorno mockado
     * @return {Observable} Observable contendo obj recebido como parâmetro
     */
    protected mainMock(obj, customMessage?: string): Observable<any> {
        this.messageService.add({ severity: "success", summary: "RETORNO_MOCK", detail: customMessage });
        return Observable.create(observer => observer.next(obj));
    }

    /**
     * Implementação de requisição utilizada por todas as requests realizadas na aplicação
     * 
     * @param {Promise} promise Promise utilizada para devidos tratamentos da requisição
     * @return {Observable} Observable contendo requisição após processamento
     */
    private request(promise: Promise<AxiosResponse>, customMessage?: string): Observable<any> {
        return Observable.create(observer => {
            promise
                .then(response => {
                    this.trataResponse(response, customMessage);
                    response.data || response.config.method === 'get' ? observer.next(response.data) : null;
                    observer.complete();
                })
                .catch(error => this.trataErro(error))
        });
    }

    /**
     * Tratamento de response em casos de sucesso mas não necessáriamente o esperado
     * Exibe mensagem contendo mensagem tratada
     * 
     * @param response Response a ser analizada
     */
    protected trataResponse(response, customMessage?:string) {
        const status = response.status;
        let esperado;
        let message;
        let summary;

        if (customMessage) {
            esperado = "success";
            message = customMessage;
        } else {
            switch (status) {
                case 200:
                    esperado = "success";
                    switch (response.config.method) {
                        case "delete":
                            message = "Exclusão realizada com sucesso!"
                            break;
                        case "put":
                            message = "Alteração realizada com sucesso!"
                            break;
                        default:
                            message = this.globals.getMsg(1).valor;
                    }
                    break;
                case 201:
                    esperado = "success";
                    message = this.globals.getMsg(5).valor;
                    break;
                case 204:
                    esperado = "warn";
                    message = this.globals.getMsg(2).valor;
                    break;
                default:
                    message = "Algo não saiu como esperado";
            }
        }

        switch (esperado) {
            case "warn":
                summary = "Oops...";
                break;
            case "success":
                summary = "Sucesso";
                break;
            default:
                summary = "Oops...";
                break;
        }

        if (response.config.method !== 'get' || status !== 200) this.messageService.add({ severity: esperado, summary: summary, detail: message });
    }

    /**
     * Tratamento de erro de requests
     * Exibe mensagem contendo mensagem tratada ou Erro desconhecido caso não consiga recuperar motivo para erro
     * 
     * Erros 422 são tratados nas mensagens das opções globais do sistema (this.globals)
     * 
     * @param erro Erro a ser analizado
     */
    protected trataErro(erro) {
        const tipoErro = erro.response.status;
        let message;
        switch (tipoErro) {
            case 404:
                message = "Endpoint não encontrado";
                break;
            case 422:
                message = this.globals.getMsg(erro.response.data.message).valor;
                break;
            default:
                message = "Erro desconhecido";
                break;
        }
        this.messageService.add({ severity: 'error', summary: `Atenção`, detail: message });
    }
}