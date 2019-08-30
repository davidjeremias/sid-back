package br.com.caixa.sidce.domain.model.enums;

public enum StatusSolicitacaoEnum {
	EM_CRIACAO("Em Criação"),
	ENVIADO("Enviado para Aprovação");
	
	private String nome;

    private StatusSolicitacaoEnum (String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
