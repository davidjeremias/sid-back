package br.com.caixa.sidce.interfaces.web.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Builder(toBuilder = true)
@Getter
@Setter
public class ParametrosIntegracaoDTO implements Serializable {

	private static final long serialVersionUID = -483287841706671981L;

	private Integer id;
	private String url;
	private Integer dia;
	private LocalDateTime hora;

	@Tolerate
	public ParametrosIntegracaoDTO() {
		super();
	}

}
