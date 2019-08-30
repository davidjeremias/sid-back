package br.com.caixa.sidce.infraestructure.persistence.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.com.caixa.sidce.domain.model.Titular;
import br.com.caixa.sidce.infraestructure.persistence.jpa.util.HelperQuery;
import br.com.caixa.sidce.interfaces.web.dto.VisualizacaoArquivosDTO;

@Repository
public class TitularCustomRepositoryImpl implements TitularCustomRepository {

	@PersistenceContext
	private EntityManager em;

	@Override
	public PageImpl<Titular> detalhamentoArquivoTitular(Pageable pageable, VisualizacaoArquivosDTO dto) {

		StringBuilder select = new StringBuilder();
		StringBuilder join = new StringBuilder();
		StringBuilder where = new StringBuilder();
		StringBuilder selectCount = new StringBuilder();

		select.append(
				" SELECT DISTINCT  t FROM   Titular AS t ");
		
		selectCount.append(" SELECT DISTINCT count(*) FROM   Titular AS t ");
		
		where.append(" WHERE  1=1");
		
		//	Filtros
		HelperQuery.customFiltro(where, dto);

		String consulta = HelperQuery.customMontaQuery(select.toString(), join.toString(), where.toString());
		String maximo = HelperQuery.customMontaQuery(selectCount.toString(), join.toString(), where.toString());
		Query query = em.createQuery(consulta, Titular.class);
		Query count = em.createQuery(maximo);

		HelperQuery.customSetaFiltro(query,count,dto);
		
		int start = pageable.getPageSize() * (pageable.getPageNumber());

		query.setFirstResult(start);
		query.setMaxResults(pageable.getPageSize());
		Long total = (Long) count.getSingleResult();

		return new PageImpl<>(query.getResultList(), pageable, total);
	}

	

}
