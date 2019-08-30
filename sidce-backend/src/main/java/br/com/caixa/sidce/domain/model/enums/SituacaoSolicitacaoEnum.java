package br.com.caixa.sidce.domain.model.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SituacaoSolicitacaoEnum implements Labeled  {

	@JsonProperty("Em criação") 
	EM_CRIACAO ("Em criação"),
	@JsonProperty("Enviado para aprovação") 
	ENVIADO_PARA_APROVACAO ("Enviado para aprovação"),
	@JsonProperty("Confirmado") 
	CONFIRMADO ("Confirmado"),
	@JsonProperty("Recusado") 
	RECUSADO ("Recusado"),
	@JsonProperty("Afastamento em criação") 
	AFASTAMENTO_EM_CRIACAO ("Afastamento em criação"),
	@JsonProperty("Gerado") 
	GERADO  ("Gerado"),
	@JsonProperty("Transmitido") 
	TRANSMITIDO ("Transmitido"),
	@JsonProperty("Rejeitado") 
	REJEITADO ("Rejeitado");

    private String label;

    private SituacaoSolicitacaoEnum (String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
    
    public static SituacaoSolicitacaoEnum getEnumByName(String nome){
    	for (SituacaoSolicitacaoEnum chave : SituacaoSolicitacaoEnum.values()) {
			if (chave.getLabel().equalsIgnoreCase(nome)) {
				return chave;
			}
		}
    	 throw new IllegalArgumentException("Nome [" + nome
                 + "] not supported.");
    }
    
    @Override
    public String label() {
        return label;
    }
}
