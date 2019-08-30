package br.com.caixa.sidce.domain.model.enums;

public enum EventoEnum {

	DOWNLOAD ("Download"),
	ENVIO_INTERFACE_GRANDE_PORTE ("Envio de arquivo de interface para o grande porte."),
	IMPORTACAO_GRANDE_PORTE ("Importação de arquivo de contrapartida do grande porte."),
	IMPORTACAO_INTERFACE_ARQUIVO ("Importação de arquivo de interface."),
	IMPORTACAO_INTERFACE_REST ("Importação de dados via interface REST API."),
	UPLOAD ("Upload e geração dos 5 arquivos"),
	SOB_DEMANDA ("Geração sob demanda");
	

    private String nome;

    private EventoEnum (String nome) {
        this.nome = nome;
    }
    
	public String getNome() {
		return nome;
	}
}
