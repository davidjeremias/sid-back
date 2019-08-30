package br.com.caixa.sidce.domain.model.enums;

public enum TipoSolicitacaoEnum {
	E("ELEITORAL"),
	G("GERAL");
	
	private String nome;

    private TipoSolicitacaoEnum (String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
