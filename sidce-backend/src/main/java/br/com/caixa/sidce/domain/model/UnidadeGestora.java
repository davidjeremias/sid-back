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

@Entity
@Builder
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Table(name = "DCETB046_UNIDADE_GESTORA")
public class UnidadeGestora extends EntidadeBase<Integer>{
	
	private static final long serialVersionUID = 2435531690687175254L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "NU_ID")
	private Integer id;
	
	@Column(name = "NU_UNIDADE")
	private Integer numeroUnidade;
	
	@Column(name = "NO_UNIDADE")
	private String descricaoUnidade;
	
	@Column(name = "NO_EMAIL")
	private String emailUnidade;
	
	@Tolerate
	public UnidadeGestora() {
		super();
	}
}
