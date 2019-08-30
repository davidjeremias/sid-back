package br.com.caixa.sidce.infraestructure.persistence.jpa;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.cdi.Eager;

import br.com.caixa.sidce.domain.model.Solicitacao;
import br.com.caixa.sidce.interfaces.web.dto.SolicitacaoDTO;

@Eager
public interface SolicitacaoCustomRepository {

	PageImpl<Solicitacao> buscaSolicitacoesPaginado(Pageable pageable, SolicitacaoDTO dto);

	Integer gerarNumeroCodigoSolicitacao(String tipo);

}
