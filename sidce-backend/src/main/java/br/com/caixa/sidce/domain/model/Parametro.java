package br.com.caixa.sidce.domain.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.caixa.sidce.domain.model.enums.ParametroEnum;
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
@EqualsAndHashCode(callSuper = false)
@Table(name = "DCETB033_DETALHE_FUNCIONALIDADE")
public class Parametro extends EntidadeBase<Integer> {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@Column(name = "NU_ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "CO_FUNCIONALIDADE")
	@Enumerated(EnumType.STRING)
	private ParametroEnum chave;

	@Column(name = "DE_FUNCIONALIDADE")
	private String valor;

	@ManyToOne
	@JoinColumn(referencedColumnName = "NU_ID", name = "NU_ID_TB032")
	private TipoParametro tipo;

	@Tolerate
	public Parametro() {
		super();
	}
}
