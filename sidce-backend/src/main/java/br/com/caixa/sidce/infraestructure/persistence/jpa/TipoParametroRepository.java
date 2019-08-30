package br.com.caixa.sidce.infraestructure.persistence.jpa;

import org.springframework.data.repository.cdi.Eager;

import br.com.caixa.sidce.domain.model.TipoParametro;
import br.com.caixa.sidce.util.infraestructure.jpa.RepositoryBase;

@Eager
public interface TipoParametroRepository extends RepositoryBase<TipoParametro, Integer> {

}
