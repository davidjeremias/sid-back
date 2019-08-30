package br.com.caixa.sidce.interfaces.web.dto;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Builder
@Getter
@Setter
public class EmailDTO implements Serializable{
	
	@Tolerate
	public EmailDTO() {
		super();
	}

	private static final long serialVersionUID = -5302958962715173472L;
	
	private String matricula;
	private String email;

}
