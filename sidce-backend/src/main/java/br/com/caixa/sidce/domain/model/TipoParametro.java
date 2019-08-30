package br.com.caixa.sidce.domain.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import br.com.caixa.sidce.util.infraestructure.domain.model.EntidadeBase;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Builder
@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper=false)
@Table(name = "DCETB032_FUNCIONALIDADE")
public class TipoParametro extends EntidadeBase<Integer> {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@Column(name = "NU_ID", nullable = false)
	private Integer id;
	
	@Column(name = "NO_FUNCIONALIDADE")
	private String funcionalidade;
	
	@Tolerate
	public TipoParametro() {
		super();
	}
}
