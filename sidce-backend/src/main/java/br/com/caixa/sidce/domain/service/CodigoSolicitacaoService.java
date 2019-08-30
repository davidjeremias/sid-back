package br.com.caixa.sidce.domain.service;

import org.springframework.stereotype.Service;

import br.com.caixa.sidce.domain.model.CodigoSolicitacao;
import br.com.caixa.sidce.util.infraestructure.jpa.RepositoryBase;
import br.com.caixa.sidce.util.infraestructure.service.RestFullService;

@Service
public class CodigoSolicitacaoService extends RestFullService<CodigoSolicitacao, Integer> {

	protected CodigoSolicitacaoService(RepositoryBase<CodigoSolicitacao, Integer> repository) {
		super(repository);
	}

}
