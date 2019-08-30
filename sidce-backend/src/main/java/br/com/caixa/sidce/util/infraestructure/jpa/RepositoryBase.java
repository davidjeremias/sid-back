package br.com.caixa.sidce.util.infraestructure.jpa;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.caixa.sidce.util.infraestructure.domain.model.EntidadeBase;

public interface RepositoryBase<E extends EntidadeBase<PK>, PK extends Serializable>
    extends JpaRepository<E, PK> {

}
