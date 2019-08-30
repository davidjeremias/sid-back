package br.com.caixa.sidce.infraestructure.persistence.jpa;

import org.springframework.data.repository.cdi.Eager;

import br.com.caixa.sidce.domain.model.AuditoriaProcessamento;
import br.com.caixa.sidce.util.infraestructure.jpa.RepositoryBase;

@Eager
public interface AuditoriaProcessamentoRepository extends RepositoryBase<AuditoriaProcessamento, Integer> {
	
}
