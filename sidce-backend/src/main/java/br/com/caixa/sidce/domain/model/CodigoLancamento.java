package br.com.caixa.sidce.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@EqualsAndHashCode(callSuper = false)
@Table(name = "DCETB008_TIPO_LANCAMENTO")
public class CodigoLancamento extends EntidadeBase<Integer>{

	private static final long serialVersionUID = -154887716792495431L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "NU_ID")
	private Integer id;
	
	@Column(name = "DE_TIPO")
	private String descricaoLancamento;
	
	@Column(name = "DE_HISTORICO")
	private String descricaoHistorico;
	
	@Column(name = "NU_TIPO")
	private Integer codigoLancamento;
	
	@Column(name = "IC_NATUREZA")
	private String natureza;
	
	@Tolerate
	public CodigoLancamento() {
		super();
	}
}
