package br.com.caixa.sidce.infraestructure.persistence.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.cdi.Eager;
import org.springframework.data.repository.query.Param;

import br.com.caixa.sidce.domain.model.Banco;
import br.com.caixa.sidce.util.infraestructure.jpa.RepositoryBase;

@Eager
public interface BancoRepository extends RepositoryBase<Banco, Integer> {

	@Query("SELECT b FROM Banco b INNER JOIN Titular t ON b.numeroAgencia = t.numeroAgencia WHERE b.codigo = :codigo")
	List<Banco> buscaBancoPorCodigo(@Param("codigo") String codigo);
}
