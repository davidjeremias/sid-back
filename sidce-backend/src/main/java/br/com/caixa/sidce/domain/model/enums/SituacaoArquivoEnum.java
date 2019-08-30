package br.com.caixa.sidce.domain.model.enums;

public enum SituacaoArquivoEnum {
	
	DOWNLOAD (1,"Download"),
	FILA (2,"Fila"),
	DELETADO (3,"Deletado");

	private Integer id;
    private String nome;

    private SituacaoArquivoEnum (Integer id, String nome) {
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
