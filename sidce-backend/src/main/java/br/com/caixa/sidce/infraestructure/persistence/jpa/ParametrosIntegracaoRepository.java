package br.com.caixa.sidce.infraestructure.persistence.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.cdi.Eager;

import br.com.caixa.sidce.domain.model.ParametrosIntegracaoTSE;
import br.com.caixa.sidce.util.infraestructure.jpa.RepositoryBase;

@Eager
public interface ParametrosIntegracaoRepository extends RepositoryBase<ParametrosIntegracaoTSE, Integer>{
	
	@Query("SELECT p FROM ParametrosIntegracaoTSE p WHERE p.dataExclusao IS NULL")
	ParametrosIntegracaoTSE buscaUltimo();
}
