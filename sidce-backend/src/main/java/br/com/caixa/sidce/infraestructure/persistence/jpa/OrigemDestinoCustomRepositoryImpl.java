package br.com.caixa.sidce.infraestructure.persistence.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.com.caixa.sidce.domain.model.OrigemDestino;
import br.com.caixa.sidce.infraestructure.persistence.jpa.util.HelperQuery;
import br.com.caixa.sidce.interfaces.web.dto.VisualizacaoArquivosDTO;

@Repository
public class OrigemDestinoCustomRepositoryImpl implements OrigemDestinoCustomRepository {

	@PersistenceContext
	private EntityManager em;
	
	@Override
	public PageImpl<OrigemDestino> detalhamentoArquivoOrigemDestino(Pageable pageable, VisualizacaoArquivosDTO dto) {

		HelperQuery hq = new HelperQuery("od", "DISTINCT count(*)", "Extrato", "numeroAgencia", dto);
		hq.setAnd("numeroConta");
		hq.setAnd("codigo");
		hq.newJoin("OrigemDestino", "od", HelperQuery.TABELA_JOIN, "chaveExtrato");
		hq.setAnd(HelperQuery.TABELA_PRINCIPAL, "od", "codigo");
		
		Query query = em.createQuery(hq.getConsulta(), OrigemDestino.class);
		Query count = em.createQuery(hq.getMaximo());

		HelperQuery.customSetaFiltro(query,count,dto);
		
		int start = pageable.getPageSize() * (pageable.getPageNumber());

		query.setFirstResult(start);
		query.setMaxResults(pageable.getPageSize());
		Long total = (Long) count.getSingleResult();

		return new PageImpl<>(query.getResultList(), pageable, total);
	}
}
