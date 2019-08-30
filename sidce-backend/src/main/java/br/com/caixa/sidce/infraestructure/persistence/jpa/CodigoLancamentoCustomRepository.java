package br.com.caixa.sidce.infraestructure.persistence.jpa;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import br.com.caixa.sidce.domain.model.CodigoLancamento;

public interface CodigoLancamentoCustomRepository {
	
	PageImpl<CodigoLancamento> buscaLancamentos(Pageable pageable, String descricao, String natureza, String codigo, Boolean isCodigo);
}
