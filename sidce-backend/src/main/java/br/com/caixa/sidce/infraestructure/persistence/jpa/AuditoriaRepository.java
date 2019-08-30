package br.com.caixa.sidce.infraestructure.persistence.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.cdi.Eager;

import br.com.caixa.sidce.domain.model.Auditoria;
import br.com.caixa.sidce.util.infraestructure.jpa.RepositoryBase;

@Eager
public interface AuditoriaRepository extends RepositoryBase<Auditoria, Integer> {
	
	@Query("SELECT DISTINCT a.funcionalidade FROM Auditoria a ORDER BY a.funcionalidade ASC")
	List<String> buscaFuncionalidadesDisponiveis();
	
}
