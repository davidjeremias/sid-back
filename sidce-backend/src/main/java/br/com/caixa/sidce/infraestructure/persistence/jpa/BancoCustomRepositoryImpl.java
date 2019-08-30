package br.com.caixa.sidce.infraestructure.persistence.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.com.caixa.sidce.domain.model.Banco;
import br.com.caixa.sidce.infraestructure.persistence.jpa.util.HelperQuery;
import br.com.caixa.sidce.interfaces.web.dto.VisualizacaoArquivosDTO;

@Repository
public class BancoCustomRepositoryImpl implements BancoCustomRepository {

	@PersistenceContext
	private EntityManager em;

	@Override
	public PageImpl<Banco> detalhamentoArquivoBanco(Pageable pageable, VisualizacaoArquivosDTO dto) {
		
		HelperQuery hq = new HelperQuery("b", "count(DISTINCT b.id)", "Banco", "numeroAgencia", dto);
		hq.setAnd("codigo");
		
		Query query = em.createQuery(hq.getConsulta().replaceAll(" e ", " b ").replaceAll("e\\.", "b."), Banco.class);
		Query count = em.createQuery(hq.getMaximo().replaceAll(" e ", " b ").replaceAll("e\\.", "b."));

		HelperQuery.customSetaFiltro(query,count,dto);
		
		int start = pageable.getPageSize() * (pageable.getPageNumber());

		query.setFirstResult(start);
		query.setMaxResults(pageable.getPageSize());
		Long total = (Long) count.getSingleResult();

		return new PageImpl<>(query.getResultList(), pageable, total);
	}

	

}
