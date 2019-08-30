package br.com.caixa.sidce.infraestructure.persistence.jpa;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.com.caixa.sidce.domain.model.SituacaoSolicitacao;
import br.com.caixa.sidce.domain.model.Solicitacao;
import br.com.caixa.sidce.interfaces.web.dto.SolicitacaoDTO;

@Repository
public class SolicitacaoCustomRepositoryImpl implements SolicitacaoCustomRepository {
	
	private static final String ELEITORAL = "ELEITORAL";

	@PersistenceContext
	private EntityManager em;

	@Override
	public PageImpl<Solicitacao> buscaSolicitacoesPaginado(Pageable pageable, SolicitacaoDTO dto) {

		StringBuilder queryHQL = new StringBuilder();
		StringBuilder countHQL = new StringBuilder();
		StringBuilder joinHQL = new StringBuilder();
		StringBuilder where = new StringBuilder();

		queryHQL.append("select distinct s from Solicitacao s ");
		countHQL.append("select count(s) from Solicitacao s ");
		where.append("where 1=1");

		if (dto.getCpfCNPJ() != null) {
			joinHQL.append(" INNER JOIN s.contas sc ");
			where.append(" and sc.cpfCNPJ = :cpfCNPJ ");
		}
		if (!dto.getSituacoes().isEmpty()) {
			joinHQL.append(" INNER JOIN s.situacaoSolicitacao ss ");
			where.append(" and ss.id in (:situacaoSolicitacao) ");
		}
		if (dto.getCodigo() != null) {
			joinHQL.append(" INNER JOIN s.codigoSolicitacao cs ");
			where.append(" and cs.codigo = :codigo ");
		}
		if (dto.getNumeroUnidade() != null && !dto.getPendente()) {
			where.append(" and s.unidadeSolicitante = :numeroUnidade");
		}
		// Quando a requisição é de uma consulta de Solicitações, remove as solicitações
		// que foram geradas através de Solicitações pré aprovadas (AFASTAMENTO)
		if (!dto.getAfastamento()) {
			where.append(" and s.isPreAprovado = :afastamento");
		}
		if (dto.getMatricula() != null) {
			where.append(" and s.matricula = :matricula");
		}
		if (dto.getTipoSolicitacao() != null) {
			where.append(" and s.tipoSolicitacao = :tipoSolicitacao");
		}
		if (dto.getDtInicio() != null)
			where.append(" AND s.dtHoraCadastro >= :dtInicio ");
		if (dto.getDtFim() != null)
			where.append(" AND s.dtHoraCadastro <= :dtFim ");

		queryHQL.append(joinHQL).append(where);
		countHQL.append(joinHQL).append(where);

		if(dto.getAfastamento()) {
			queryHQL.append(" order by s.rascunho desc, s.dtHoraAnalise desc, s.dtHoraCadastro desc");
		}else {
			queryHQL.append(" order by s.dtHoraCadastro desc");
		}

		Query query = em.createQuery(queryHQL.toString(), Solicitacao.class);
		Query count = em.createQuery(countHQL.toString());

		if (dto.getCpfCNPJ() != null) {
			query.setParameter("cpfCNPJ", dto.getCpfCNPJ());
			count.setParameter("cpfCNPJ", dto.getCpfCNPJ());
		}

		if (dto.getMatricula() != null) {
			query.setParameter("matricula", dto.getMatricula());
			count.setParameter("matricula", dto.getMatricula());
		}

		if (dto.getNumeroUnidade() != null && !dto.getPendente()) {
			query.setParameter("numeroUnidade", dto.getNumeroUnidade());
			count.setParameter("numeroUnidade", dto.getNumeroUnidade());
		}

		if (!dto.getAfastamento()) {
			query.setParameter("afastamento", false);
			count.setParameter("afastamento", false);
		}

		if (dto.getCodigo() != null) {
			query.setParameter("codigo", dto.getCodigo());
			count.setParameter("codigo", dto.getCodigo());
		}

		if (dto.getTipoSolicitacao() != null) {
			query.setParameter("tipoSolicitacao", dto.getTipoSolicitacao());
			count.setParameter("tipoSolicitacao", dto.getTipoSolicitacao());
		}

		if (dto.getDtInicio() != null) {
			query.setParameter("dtInicio", dto.getDtInicio().withHour(0).withMinute(0).withSecond(0));
			count.setParameter("dtInicio", dto.getDtInicio().withHour(0).withMinute(0).withSecond(0));
		}
		if (dto.getDtFim() != null) {
			query.setParameter("dtFim", dto.getDtFim().withHour(23).withMinute(59).withSecond(59));
			count.setParameter("dtFim", dto.getDtFim().withHour(23).withMinute(59).withSecond(59));
		}

		if (!dto.getSituacoes().isEmpty()) {
			List<Integer> b = dto.getSituacoes().stream().map(SituacaoSolicitacao::getId)
					.collect(Collectors.toList());
			query.setParameter("situacaoSolicitacao", b);
			count.setParameter("situacaoSolicitacao", b);
		}

		int start = pageable.getPageSize() * (pageable.getPageNumber());

		query.setFirstResult(start);
		query.setMaxResults(pageable.getPageSize());
		Long total = (Long) count.getSingleResult();

		return new PageImpl<>(query.getResultList(), pageable, total);

	}

	@Override
	public Integer gerarNumeroCodigoSolicitacao(String string) {
		StringBuilder sql = new StringBuilder();

		sql.append(" ;WITH codigo AS ( " + 
					" SELECT " +
					" UPPER(CO_PREFIXO) AS CO_PREFIXO, " + 
					" MAX(NU_SOLICITACAO) + 1 AS NU_SOLICITACAO " + 
					" FROM DCETB038_CODIGO_SOLICITACAO " + 
					" GROUP BY CO_PREFIXO " + 
					" UNION " + 
					" SELECT UPPER('TSE') AS CO_PREFIXO, 1 AS NU_SOLICITACAO UNION " + 
					" SELECT UPPER('EL')AS CO_PREFIXO, 1 AS NU_SOLICITACAO UNION " + 
					" SELECT UPPER('GE')AS CO_PREFIXO, 1 AS NU_SOLICITACAO " + 
					" EXCEPT " + 
					" SELECT CO_PREFIXO, NU_SOLICITACAO " + 
					" FROM DCETB038_CODIGO_SOLICITACAO " + 
					" ) " + 
					" SELECT " + 
					" NU_SOLICITACAO " + 
					" FROM codigo AS co " );
		if (string.equals(ELEITORAL)) {
			sql.append(" where co.CO_PREFIXO = 'EL';");
		} else {
			sql.append(" where co.CO_PREFIXO = 'GE';");
		}

		Query query = em.createNativeQuery(sql.toString());
		return (Integer) query.getSingleResult();
	}

}
