package br.com.caixa.sidce.interfaces.web.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Builder
@Getter
@Setter
@EqualsAndHashCode
public class ContratosDTO implements Serializable {

	private static final long serialVersionUID = 2291777693972547756L;
	
	private String coIdentificacao;
	private Date dtInicio;
	private String nuProduto;
	private String nuNaturalSistema;
	private String sgSistema;
	private Date dtFim;
	private String nuUnidade;
	private Date dtSituacao;
	private String nuCrto;
	private String nuConvenio;
	private String nuCanalCMRCO;
	private String noRdzdoContrato;
	private String icSituacao;
	private String noProduto;
	private String nuTitular;
	
	@Tolerate
	public ContratosDTO() {
		super();
	}
}
