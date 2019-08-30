package br.com.caixa.sidce.interfaces.web.dto;

import java.io.Serializable;

import br.com.caixa.sidce.domain.model.AgendamentoETL;
import br.com.caixa.sidce.domain.model.Arquivo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Builder(toBuilder=true)
@Getter
@Setter
public class RetornoSimbaDTO implements Serializable{
	
	private static final long serialVersionUID = -1126770630380412650L;
	
	private AgendamentoETL agendamentoETL;
	private Arquivo arquivo;
	
	@Tolerate
	public RetornoSimbaDTO() {
		super();
	}

}
