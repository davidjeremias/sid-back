package br.com.caixa.sidce.interfaces.web.dto;

import java.io.Serializable;
import java.util.Date;

import br.com.caixa.sidce.domain.model.CodigoSolicitacao;
import br.com.caixa.sidce.util.infraestructure.auditoria.TipoEventoAuditoriaEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaDTO implements Serializable {

	private static final long serialVersionUID = 1523417624529102968L;

	// Filtros
	private String evento;
	private String codigo;
	private Date dtInicio;
	private Date dtFim;

	// Objeto
	private Integer id;
	private TipoEventoAuditoriaEnum tipoEvento;
	private String host;
	private String matricula;
	private String funcionalidade;
	private Date dtHrAuditoria;
	private CodigoSolicitacao codigoSolicitacao;

}
