package br.com.caixa.sidce.domain.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import br.com.caixa.sidce.domain.model.Afastamento;
import br.com.caixa.sidce.domain.model.Solicitacao;
import br.com.caixa.sidce.util.infraestructure.jpa.RepositoryBase;
import br.com.caixa.sidce.util.infraestructure.service.RestFullService;

@Service
public class AfastamentoService extends RestFullService<Afastamento, Integer> {

	protected AfastamentoService(RepositoryBase<Afastamento, Integer> repository) {
		super(repository);
	}

	public void criaAfastamento(String matricula, Integer unidade, Solicitacao solicitacaoSalva) {
		add(Afastamento.builder().matricula(matricula).solicitacao(solicitacaoSalva).unidade(unidade)
				.dtHoraCadastro(LocalDateTime.now()).build());
	}
}
