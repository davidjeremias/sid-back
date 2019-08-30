package br.com.caixa.sidce.infraestructure.persistence.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.com.caixa.sidce.domain.model.Auditoria;
import br.com.caixa.sidce.infraestructure.persistence.jpa.util.HelperQuery;
import br.com.caixa.sidce.interfaces.web.dto.AuditoriaDTO;
import br.com.caixa.sidce.util.infraestructure.auditoria.TipoEventoAuditoriaEnum;

@Repository
public class AuditoriaCustomRepositoryImpl implements AuditoriaCustomRepository {

	private static final String DT_FIM = "dtFim";
	private static final String DT_INICIO = "dtInicio";
	private static final String EVENTO = "evento";
	private static final String MATRICULA = "matricula";
	private static final String FUNCIONALIDADE = "funcionalidade";
	private static final String CODIGO = "codigo";
	
	@PersistenceContext
	private EntityManager em;

	@Override
	public PageImpl<Auditoria> buscaAuditoriaPaginado(Pageable pageable, AuditoriaDTO dto) {

		StringBuilder queryHQL = new StringBuilder();
		StringBuilder where = new StringBuilder();
		StringBuilder countHQL = new StringBuilder();
		StringBuilder joinHQL = new StringBuilder();

		queryHQL.append(
				" SELECT a FROM   Auditoria AS a ");
		
		countHQL.append(" SELECT count(DISTINCT a.id) FROM   Auditoria AS a ");
		where.append(" WHERE  1=1");
		
		//	Filtros
		if (dto.getMatricula() != null)
			where.append(" AND  a.matricula = :matricula");
		if (dto.getCodigo() != null) {
			joinHQL.append(" INNER JOIN a.codigoSolicitacao cs ");
			where.append(" and cs.codigo = :codigo ");
		}
		if (dto.getFuncionalidade() != null)
			where.append(" AND a.funcionalidade = :funcionalidade ");
		if (dto.getEvento() != null)
			where.append(" AND a.tipoEvento = :evento ");
		if (dto.getDtInicio()!= null)
			where.append(" AND a.dtHrAuditoria >= :dtInicio ");
		if (dto.getDtFim() != null)
			where.append(" AND a.dtHrAuditoria <= :dtFim ");

		queryHQL.append(joinHQL).append(where);
		countHQL.append(joinHQL).append(where);
		queryHQL.append(" ORDER BY a.dtHrAuditoria DESC ");
		
		Query query = em.createQuery(queryHQL.toString(), Auditoria.class);
		Query count = em.createQuery(countHQL.toString());

		if (dto.getMatricula() != null) {
			query.setParameter(MATRICULA, dto.getMatricula());
			count.setParameter(MATRICULA, dto.getMatricula());
		}
		if (dto.getCodigo() != null) {
			query.setParameter(CODIGO, dto.getCodigo());
			count.setParameter(CODIGO, dto.getCodigo());
		}
		if (dto.getFuncionalidade() != null) {
			query.setParameter(FUNCIONALIDADE, dto.getFuncionalidade());
			count.setParameter(FUNCIONALIDADE, dto.getFuncionalidade());
		}
		if (dto.getEvento() != null) {
			TipoEventoAuditoriaEnum enumOpt = TipoEventoAuditoriaEnum.getEnumByName(dto.getEvento());
			query.setParameter(EVENTO, enumOpt);
			count.setParameter(EVENTO, enumOpt);
		}
		if (dto.getDtInicio() != null) {
			query.setParameter(DT_INICIO, dto.getDtInicio());
			count.setParameter(DT_INICIO, dto.getDtInicio());
		}
		if (dto.getDtFim() != null) {
			query.setParameter(DT_FIM, dto.getDtFim());
			count.setParameter(DT_FIM, dto.getDtFim());
		}
		
		int start = pageable.getPageSize() * (pageable.getPageNumber());

		query.setFirstResult(start);
		query.setMaxResults(pageable.getPageSize());
		Long total = (Long) count.getSingleResult();

		return new PageImpl<>(query.getResultList(), pageable, total);
	}

	@Override
	public List<TipoEventoAuditoriaEnum> buscaEventosDisponiveis(String funcionalidade) {
		
		StringBuilder select = new StringBuilder();
		StringBuilder where = new StringBuilder();
		
		select.append("SELECT DISTINCT a.tipoEvento FROM Auditoria AS a");
		where.append(" WHERE 1=1");
		if (funcionalidade != null)
			where.append(" AND a.funcionalidade = :funcionalidade ");
		
		String consulta = HelperQuery.customMontaQuery(select.toString(), "", where.toString());
		
		Query query = em.createQuery(consulta);    
		
		if (funcionalidade != null) {
			query.setParameter(FUNCIONALIDADE, funcionalidade);
		}
		
		return query.getResultList();
	}

	

}
