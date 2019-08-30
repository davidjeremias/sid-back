package br.com.caixa.sidce.infraestructure.persistence.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import br.com.caixa.sidce.domain.model.enums.EventoEnum;

@Repository
public class AuditoriaProcessamentoCustomRepositoryImpl implements  AuditoriaProcessamentoCustomRepository {

	@PersistenceContext
	private EntityManager em;

	public List<String> buscarTxtDisponiveis(String codigo) {
		StringBuilder queryHQL = new StringBuilder();
		queryHQL.append("SELECT a.nome FROM AuditoriaProcessamento a INNER JOIN a.evento where e.descricaoEvento = :evento and a.codigo = ?1");
		
		Query query = em.createQuery(queryHQL.toString(), String.class);
		
		query.setParameter("evento", EventoEnum.DOWNLOAD);
		return query.getResultList();
	}

}