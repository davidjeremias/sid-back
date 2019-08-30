package br.com.caixa.sidce.domain.model.enums;

public enum ParametroEnum {
	ETL_DIR_INPUT("ETL_DIR_INPUT"),
	ETL_DIR_OUTPUT("ELT_DIR_OUTPUT"),
	ETL_DIR_REPORT("ETL_DIR_REPORT"),
	DOMINIO_SMB("DOMINIO_SMB"),
	USR_SMB("USR_SMB"),
	SENHA_SMB("SENHA_SMB"),
	API_KEY("API_KEY"),
	CONTA_DEPOSITO_ENDPOINT("CONTA_DEPOSITO_ENDPOINT"),
	HOST_MAIL("HOST_MAIL"),
	PORT_MAIL("PORT_MAIL"),
	USERNAME_MAIL("USERNAME_MAIL"),
	PASSWORD_MAIL("PASSWORD_MAIL");
	
	private String nome;

    private ParametroEnum (String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
