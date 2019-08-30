package br.com.caixa.sidce.config;

public enum DataSourceIntegracaoEnum {

	SIICO ("S", "spring.datasource.siico.jndi-name");

    private String sigla;
    private String springJNDI;

    private DataSourceIntegracaoEnum (String sigla, String springJNDI) {
        this.sigla = sigla;
        this.springJNDI = springJNDI;
    }

    public String getSigla() {
        return sigla;
    }

    public String getSpringJNDI() {
        return springJNDI;
    }
}
