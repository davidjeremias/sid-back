package br.com.caixa.sidce.util.infraestructure.auditoria;

public enum TipoEventoAuditoriaEnum {

    BUSCA ("BUSCA"),
	ALTERACAO ("ALTERACAO"),
	INSERCAO ("INSERCAO"),
	EXCLUSAO ("EXCLUSAO"),
	UPLOAD ("UPLOAD"),
	DOWNLOAD ("DOWNLOAD"),
	SOLICITA_GERACAO("SOLICITAR GERACAO"),
	ENVIO_PARA_ANALISE("ENVIO PARA ANALISE"),
	ANALISE("ANALISE");

    private String nome;

    private TipoEventoAuditoriaEnum (String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
    
    public static TipoEventoAuditoriaEnum getEnumByName(String nome){
    	for (TipoEventoAuditoriaEnum chave : TipoEventoAuditoriaEnum.values()) {
			if (chave.getNome().equals(nome)) {
				return chave;
			}
		}
    	 throw new IllegalArgumentException("Nome [" + nome
                 + "] not supported.");
    }

}
