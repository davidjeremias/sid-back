package br.com.caixa.sidce.domain.model.enums;

public enum SituacaoEnum {

	GERADO (1,"Gerado"),
	TRANSMITIDO (2,"Transmitido"),
	REJEITADO (3,"Rejeitado"),
	INICIADO (4,"Iniciado"),
	FALHA_NO_PROCESSAMENTO (5,"Falha no processamento");

	private Integer id;
    private String nome;

    private SituacaoEnum (Integer id, String nome) {
        this.nome = nome;
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

	public Integer getId() {
		return id;
	}    

}
