import { SituacaoSolicitacao } from "./situacaoSolicitacao";
import { Conta } from "./conta";
import { CodigoSolicitacao } from "./codigoSolicitacao";
import { UnidadeDTO } from "./unidadeDTO";

export class Solicitacao{
    public id: number;
    public matricula: string;
    public tipoSolicitacao: string;
    public dtHoraCadastro: Date;
    public rascunho: boolean;
    public situacaoSolicitacao: SituacaoSolicitacao;
    public contas: Array<Conta>;
    public motivoRejeicao: string;
    public dtHoraAnalise: Date;
    public matriculaResponsavel: string;
    public unidadeSolicitante: number;
    public unidadeResponsavel: number;
    public isPreAprovado: boolean;
    public codigoSolicitacao: CodigoSolicitacao;
    public situacaoArquivo: string;
    public unidadeDTO: UnidadeDTO;
    public codigo: string;
    public isUsuarioCriador: boolean;

}

