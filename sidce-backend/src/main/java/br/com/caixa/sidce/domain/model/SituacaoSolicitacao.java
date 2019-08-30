package br.com.caixa.sidce.domain.model;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import br.com.caixa.sidce.domain.model.enums.SituacaoSolicitacaoEnum;
import br.com.caixa.sidce.domain.model.enums.SituacaoSolicitacaoEnumConverter;
import br.com.caixa.sidce.util.infraestructure.domain.model.EntidadeBase;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Builder(toBuilder = true)
@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "[DCETB037_SITUACAO_SOLICITACAO]")
public class SituacaoSolicitacao extends EntidadeBase<Integer> {

	private static final long serialVersionUID = 6250995919300056137L;

	@Id
	@Column(name = "NU_ID", nullable = false)
	private Integer id;

	@Column(name = "NO_SITUACAO", nullable = false)
	@Convert(converter = SituacaoSolicitacaoEnumConverter.class)
	private SituacaoSolicitacaoEnum nomeSituacao;

	@Tolerate
	public SituacaoSolicitacao() {
		super();
	}

	public String getNomeSituacao() {
		return nomeSituacao.label();
	}

}
