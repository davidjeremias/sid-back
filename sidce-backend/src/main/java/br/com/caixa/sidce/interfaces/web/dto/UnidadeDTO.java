package br.com.caixa.sidce.interfaces.web.dto;

import java.io.Serializable;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Builder
@Getter
@Setter
@EqualsAndHashCode
public class UnidadeDTO implements Serializable {

	private static final long serialVersionUID = -4878346859298008604L;

	private Integer unidade;
	private String nomeUnidade;
	private String siglaUnidade;
	private String emailUnidade;

	@Tolerate
	public UnidadeDTO() {
		super();
	}

}
