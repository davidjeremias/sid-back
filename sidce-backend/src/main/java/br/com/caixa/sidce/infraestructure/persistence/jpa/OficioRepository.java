package br.com.caixa.sidce.infraestructure.persistence.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.cdi.Eager;

import br.com.caixa.sidce.domain.model.Oficio;
import br.com.caixa.sidce.util.infraestructure.jpa.RepositoryBase;

@Eager
public interface OficioRepository  extends RepositoryBase<Oficio, Integer> {
	
	@Query("SELECT o FROM Oficio o WHERE o.solicitacao.id = ?1")
	List<Oficio> buscaOficioPorSolicitacao(Integer idSolicitacao);

}
