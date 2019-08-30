package br.com.caixa.sidce.infraestructure.persistence.jpa;

import org.springframework.data.repository.cdi.Eager;

import br.com.caixa.sidce.domain.model.SituacaoSolicitacao;
import br.com.caixa.sidce.domain.model.enums.SituacaoSolicitacaoEnum;
import br.com.caixa.sidce.util.infraestructure.jpa.RepositoryBase;

@Eager
public interface SituacaoSolicitacaoRepository  extends RepositoryBase<SituacaoSolicitacao, Integer> {

	SituacaoSolicitacao getByNomeSituacao(SituacaoSolicitacaoEnum param);
}
