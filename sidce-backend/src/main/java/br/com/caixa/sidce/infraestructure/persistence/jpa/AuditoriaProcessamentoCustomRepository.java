package br.com.caixa.sidce.infraestructure.persistence.jpa;

import java.util.List;

import org.springframework.data.repository.cdi.Eager;

@Eager
public interface AuditoriaProcessamentoCustomRepository {

	public List<String> buscarTxtDisponiveis(String codigo);
}
