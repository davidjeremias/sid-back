package br.com.caixa.sidce.infraestructure.persistence.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.com.caixa.sidce.domain.model.Extrato;
import br.com.caixa.sidce.infraestructure.persistence.jpa.util.HelperQuery;
import br.com.caixa.sidce.interfaces.web.dto.VisualizacaoArquivosDTO;

@Repository
public class ExtratoCustomRepositoryImpl implements ExtratoCustomRepository {

	@PersistenceContext
	private EntityManager em;

	@Override
	public PageImpl<Extrato> detalhamentoArquivoExtrato(Pageable pageable, VisualizacaoArquivosDTO dto) {
		
		HelperQuery hq = new HelperQuery("e", "DISTINCT count(*)", "Extrato", "numeroAgencia", dto);
		hq.setAnd("numeroConta");
		hq.setAnd("codigo");

		Query query = em.createQuery(hq.getConsulta(), Extrato.class);
		Query count = em.createQuery(hq.getMaximo());

		HelperQuery.customSetaFiltro(query,count,dto);
		
		int start = pageable.getPageSize() * (pageable.getPageNumber());

		query.setFirstResult(start);
		query.setMaxResults(pageable.getPageSize());
		Long total = (Long) count.getSingleResult();

		return new PageImpl<>(query.getResultList(), pageable, total);
	}

}
