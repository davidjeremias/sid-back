package br.com.caixa.sidce.infraestructure.persistence.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.com.caixa.sidce.domain.model.CodigoLancamento;

@Repository
public class CodigoLancamentoCustomRepositoryImpl implements CodigoLancamentoCustomRepository{

	@PersistenceContext
	private EntityManager em;
	
	private static final String SQL_INICIAL = "SELECT c FROM CodigoLancamento c WHERE 1=1 ";
	
	public PageImpl<CodigoLancamento> buscaLancamentos(Pageable pageable, String descricao, String natureza, String codigo, Boolean isCodigo) {
		StringBuilder queryHQL = new StringBuilder();
		StringBuilder countHQL = new StringBuilder();
		StringBuilder where = new StringBuilder();
		
		queryHQL.append(SQL_INICIAL);
		countHQL.append("SELECT COUNT(c) FROM CodigoLancamento c WHERE 1=1 ");
		
		if(descricao != null) {
			where.append("AND c.descricaoHistorico LIKE '%"+descricao+"%' ");
		}
		if(natureza != null) {
			where.append("AND c.natureza = :natureza ");
		}
		if(codigo != null) {
			where.append("AND c.codigoLancamento = :codigo ");
		}
		if(!isCodigo) {
			where.append("AND c.codigoLancamento IS NULL ");
			where.append("AND (c.natureza IS NOT NULL AND c.natureza != '')");
		}
		if(isCodigo) {
			where.append("AND c.codigoLancamento IS NOT NULL ");
			where.append("AND (c.natureza IS NOT NULL AND c.natureza != '')");
		}
		
		queryHQL.append(where);
		countHQL.append(where);
		
		queryHQL.append("ORDER BY c.descricaoHistorico ");
		
		Query query = em.createQuery(queryHQL.toString(), CodigoLancamento.class);
		Query count = em.createQuery(countHQL.toString());
		
		if(natureza != null) {
			query.setParameter("natureza", natureza);
			count.setParameter("natureza", natureza);
		}
		if(codigo != null) {
			query.setParameter("codigo", Integer.parseInt(codigo));
			count.setParameter("codigo", Integer.parseInt(codigo));
		}
		
		int start = pageable.getPageSize() * (pageable.getPageNumber());
		
		query.setFirstResult(start);
		query.setMaxResults(pageable.getPageSize());
		Long total = (Long) count.getSingleResult();
		return new PageImpl<>(query.getResultList(), pageable, total);
	}

}
