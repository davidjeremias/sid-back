package br.com.caixa.sidce.infraestructure.persistence.jpa;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.com.caixa.sidce.domain.model.AuditoriaDownloadTSE;

@Repository
public class AuditoriaDownloadTSECustomRepositoryImpl implements AuditoriaDownloadTSECustomRepository{
	
	@PersistenceContext
	private EntityManager em;
	
	private static final String SELECT_HQL_INITIAL = "SELECT a FROM AuditoriaDownloadTSE a WHERE 1=1 ";
	
	@Override
	public PageImpl<AuditoriaDownloadTSE> buscaAuditoriaRotina(Pageable pageable, LocalDateTime inicio,
			LocalDateTime fim, Integer status) {
		StringBuilder queryHQL = new StringBuilder();
		StringBuilder countHQL = new StringBuilder();
		StringBuilder where = new StringBuilder();
		StringBuilder sort = new StringBuilder();

		queryHQL.append(SELECT_HQL_INITIAL);
		countHQL.append("select count(a) from AuditoriaDownloadTSE a where 1=1 ");

		if (inicio != null) {
			where.append(" and a.dataHoraProcessamento > :inicio");
		}
		if (fim != null) {
			where.append(" and a.dataHoraProcessamento < :fim");
		}
		if (status != null) {
			where.append(" and a.status = :status");
		}
		
		sort.append(" ORDER BY a.dataHoraProcessamento DESC ");

		queryHQL.append(where);
		queryHQL.append(sort);
		countHQL.append(where);

		Query query = em.createQuery(queryHQL.toString(), AuditoriaDownloadTSE.class);
		Query count = em.createQuery(countHQL.toString());

		if (inicio != null) {
			query.setParameter("inicio", inicio);
			count.setParameter("inicio", inicio);
		}
		if (fim != null) {
			query.setParameter("fim", fim);
			count.setParameter("fim", fim);
		}
		if (status != null) {
			int st = status.intValue();
			query.setParameter("status", st);
			count.setParameter("status", st);
		}

		int start = pageable.getPageSize() * (pageable.getPageNumber());

		query.setFirstResult(start);
		query.setMaxResults(pageable.getPageSize());
		Long total = (Long) count.getSingleResult();

		return new PageImpl<>(query.getResultList(), pageable, total);
	}

}
