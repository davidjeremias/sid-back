package br.com.caixa.sidce.domain.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;

import br.com.caixa.sidce.util.infraestructure.domain.model.EntidadeBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Getter
@Setter
@EqualsAndHashCode(callSuper=false)
@MappedSuperclass
public abstract class DadosCompartilhados extends EntidadeBase<Integer> implements Serializable {

	private static final long serialVersionUID = -8831824100098243637L;

	@Column(name = "CO_PERIODO") //	 Código do período que os dados se referem - AAAAMM.
	private Integer periodo;
	
	@GenericGenerator(name = "generator", strategy = "guid", parameters = {})
	@GeneratedValue(generator = "generator")
	@Column(name = "CO_IMPORTACAO", columnDefinition = "uniqueidentifier", nullable= true)
	private String codigo;
	
	@Column(name = "NU_VERSAO") 
	private Integer versao;
	
	@ManyToOne
    @JoinColumn(name = "NU_ID_TB001")
	private AuditoriaProcessamento auditoriaProcessamento;

	@Tolerate
	public DadosCompartilhados() {
		super();
	}
}
