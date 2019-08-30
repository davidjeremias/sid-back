package br.com.caixa.sidce.domain.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.caixa.sidce.util.infraestructure.domain.model.EntidadeBase;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Builder(toBuilder=true)
@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper=false)
@Table(name = "DCETB030_SITUACAO_SIGILO")
public class SituacaoSigilo extends EntidadeBase<Integer> {

	private static final long serialVersionUID = 6250995919300056137L;

	@Id
	@Column(name = "NU_ID", nullable = false)
	private Integer id;

	@Column(name = "NO_SITUACAO_SIGILO")	// [varchar]
	private String nomeSituacao;
	
	@Column(name = "DE_SITUACAO_SIGILO")	// [varchar]
	private String descricaoSituacao;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DH_CADASTRO")	// [datetime]
	private Date dtHrCadastro;

	@Tolerate
	public SituacaoSigilo() {
		super();
	}
}
