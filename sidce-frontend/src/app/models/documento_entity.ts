import { BaseEntity } from './base_entity';

export class DocumentoEntity extends BaseEntity {
    public nome: string = null;
    public paginas: number = null;
    public ultimaModificacao: string = null;
}
