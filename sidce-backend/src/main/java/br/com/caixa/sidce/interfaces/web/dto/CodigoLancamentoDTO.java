package br.com.caixa.sidce.interfaces.web.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Getter
@Setter
public class CodigoLancamentoDTO implements Serializable {

	private static final long serialVersionUID = -6574842420313528407L;

	private Integer id;
	private String codigoLancamento;

	@Tolerate
	public CodigoLancamentoDTO() {
		super();
	}

}
