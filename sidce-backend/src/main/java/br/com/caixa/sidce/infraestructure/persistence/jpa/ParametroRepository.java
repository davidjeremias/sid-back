package br.com.caixa.sidce.infraestructure.persistence.jpa;

import org.springframework.data.repository.cdi.Eager;

import br.com.caixa.sidce.domain.model.Parametro;
import br.com.caixa.sidce.domain.model.enums.ParametroEnum;
import br.com.caixa.sidce.util.infraestructure.jpa.RepositoryBase;

@Eager
public interface ParametroRepository extends RepositoryBase<Parametro, Integer> {

	Parametro getByChave(ParametroEnum param);

}
