package br.com.caixa.sidce.infraestructure.persistence.jpa;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.com.caixa.sidce.domain.model.AgendamentoETL;
import br.com.caixa.sidce.domain.model.enums.EventoEnum;
import br.com.caixa.sidce.infraestructure.persistence.jpa.util.HelperQuery;
import br.com.caixa.sidce.interfaces.web.dto.VisualizacaoArquivosDTO;

@Repository
public class AgendamentoETLCustomRepositoryImpl implements AgendamentoETLCustomRepository {

	private static final String SELECT_HQL_INITIAL = "SELECT a FROM AgendamentoETL a WHERE 1=1 ";

	@PersistenceContext
	private EntityManager em;
	
	private static final String MATRICULA = "matricula";
	private static final String PERIODO_INFORMACAO = "periodoInformacao";
	private static final String INICIO_PERIODO_GERACAO = "inicioPeriodoGeracao";
	private static final String FIM_PERIODO_GERACAO = "fimPeriodoGeracao";
	private static final String CODIGO_AFASTAMENTO = "codigoAfastamento";
	
	private LocalDate dataValidaArquivos = LocalDate.now().minusDays(30);

	public PageImpl<AgendamentoETL> consultaAgendamentosComFiltro(Pageable pageable, Map<String, String[]> filter) {

		StringBuilder queryHQL = new StringBuilder();
		StringBuilder countHQL = new StringBuilder();
		StringBuilder where = new StringBuilder();

		queryHQL.append(SELECT_HQL_INITIAL);
		countHQL.append(" select count(a) from AgendamentoETL a where 1=1 ");

		where.append(" and a.evento = :descEvento");
		where.append(" and a.dtHrProcessamento IS NOT NULL");
		where.append(" and a.situacao.nomeSituacao != :situacao");

		if (filter.get(MATRICULA) != null) {
			where.append(" and a.matricula = :matricula");
		}
		if (filter.get(CODIGO_AFASTAMENTO) != null) {
			where.append(" and a.codigoSolicitacao.codigo = :codigoAfastamento");
		}
		if (filter.get(PERIODO_INFORMACAO) != null) {
			where.append(" and a.periodo = :periodoInformacao");
		}
		if (filter.get(INICIO_PERIODO_GERACAO) != null) {
			where.append(" and a.dtHrProcessamento >= '" + filter.get(INICIO_PERIODO_GERACAO)[0] + "'");
		}
		if (filter.get(FIM_PERIODO_GERACAO) != null) {
			where.append(" and a.dtHrProcessamento <= '" + filter.get(FIM_PERIODO_GERACAO)[0] + "'");
		}

		queryHQL.append(where);
		countHQL.append(where);

		queryHQL.append(" order by id desc");

		Query query = em.createQuery(queryHQL.toString(), AgendamentoETL.class);
		Query count = em.createQuery(countHQL.toString());
		
		query.setParameter("descEvento", EventoEnum.UPLOAD.getNome());
		count.setParameter("descEvento", EventoEnum.UPLOAD.getNome());
		
		query.setParameter("situacao", "Iniciado");
		count.setParameter("situacao", "Iniciado");

		if (filter.get(MATRICULA) != null) {
			query.setParameter(MATRICULA, filter.get(MATRICULA)[0]);
			count.setParameter(MATRICULA, filter.get(MATRICULA)[0]);
		}
		if (filter.get(CODIGO_AFASTAMENTO) != null) {
			query.setParameter(CODIGO_AFASTAMENTO, filter.get(CODIGO_AFASTAMENTO)[0]);
			count.setParameter(CODIGO_AFASTAMENTO, filter.get(CODIGO_AFASTAMENTO)[0]);
		}
		if (filter.get(PERIODO_INFORMACAO) != null) {
			int periodo = Integer.parseInt(filter.get(PERIODO_INFORMACAO)[0]);
			query.setParameter(PERIODO_INFORMACAO, periodo);
			count.setParameter(PERIODO_INFORMACAO, periodo);
		}

		int start = pageable.getPageSize() * (pageable.getPageNumber());

		query.setFirstResult(start);
		query.setMaxResults(pageable.getPageSize());
		Long total = (Long) count.getSingleResult();

		return new PageImpl<>(query.getResultList(), pageable, total);

	}

	/**
	 * Verifica se os arquivos do processamento foram gerados nos ultimos 30 dias
	 * 
	 * @param codigo
	 * @return
	 */
	public boolean isArquivosGeradoUltimosTrintaDias(String codigo) {

		StringBuilder hql = new StringBuilder();

		hql.append("SELECT COUNT(a.id) FROM AgendamentoETL a WHERE 1=1 ");
		hql.append(" AND a.codigo = :codigo");
		hql.append(" AND a.dtHrProcessamento IS NOT NULL");
		hql.append(" AND a.dtHrProcessamento > '" + dataValidaArquivos + "'");

		Query query = em.createQuery(hql.toString());
		query.setParameter("codigo", codigo);

		return (!query.getResultList().isEmpty());
	}

	public List<AgendamentoETL> processosDisponiveisParaDownload() {
		StringBuilder hql = new StringBuilder();

		hql.append(SELECT_HQL_INITIAL);
		hql.append(" AND a.dtHrProcessamento IS NOT NULL");
		hql.append(" AND a.dtHrProcessamento > '" + dataValidaArquivos + "'");

		Query query = em.createQuery(hql.toString(), AgendamentoETL.class);
		return (List<AgendamentoETL>) query.getResultList();
	}

	public PageImpl<AgendamentoETL> processosGeracaoConsultaNaFila(Pageable pageable) {

		StringBuilder queryHQL = new StringBuilder();
		StringBuilder countHQL = new StringBuilder();
		StringBuilder where = new StringBuilder();
		StringBuilder sortHQL = new StringBuilder();

		queryHQL.append(SELECT_HQL_INITIAL);
		countHQL.append(
				" SELECT count(*) FROM AgendamentoETL a where 1=1 ");
		where.append(
				" and a.dtHrProcessamento is null ");
		
		sortHQL.append(" ORDER BY a.dtHoraCadastro ASC ");

		queryHQL.append(where);
		queryHQL.append(sortHQL);
		countHQL.append(where);

		Query query = em.createQuery(queryHQL.toString(), AgendamentoETL.class);
		Query count = em.createQuery(countHQL.toString());

		int start = pageable.getPageSize() * (pageable.getPageNumber() - 1);

		query.setFirstResult(start);
		query.setMaxResults(pageable.getPageSize());
		Long total = (Long) count.getSingleResult();

		return new PageImpl<>(query.getResultList(), pageable, total);

	}

	public PageImpl<VisualizacaoArquivosDTO> detalhamentoCincoArquivos(Pageable pageable, VisualizacaoArquivosDTO dto) {

		StringBuilder select = new StringBuilder();
		StringBuilder join = new StringBuilder();
		StringBuilder where = new StringBuilder();
		StringBuilder selectCount = new StringBuilder();

		select.append(
				" SELECT new br.com.caixa.sidce.interfaces.web.dto.VisualizacaoArquivosDTO(b,c,e,od,t) FROM   Titular AS t ");
		
		selectCount.append(" SELECT count(*) FROM   Titular AS t ");

		join.append(" INNER JOIN Banco AS b ");
		join.append(" ON t.numeroAgencia = b.numeroAgencia ");
		join.append(" AND t.codigo = b.codigo ");
		join.append(" INNER JOIN Conta AS c ");
		join.append(" ON t.numeroConta = c.numeroConta ");
		join.append(" AND t.codigo = c.codigo ");
		join.append(" INNER JOIN Extrato AS e ");
		join.append(" ON t.numeroAgencia = e.numeroAgencia ");
		join.append(" AND t.numeroConta = e.numeroConta ");
		join.append(" AND t.codigo = e.codigo ");
		join.append(" INNER JOIN OrigemDestino AS od ");
		join.append(" ON e.chaveExtrato = od.chaveExtrato ");
		join.append(" AND t.codigo = od.codigo ");

		where.append(" WHERE  1=1");
		
		//	Filtros
		HelperQuery.customFiltro(where, dto);

		String consulta = select.toString() + join.toString() + where.toString();
		String maximo = selectCount.toString() + join.toString() + where.toString();

		Query query = em.createQuery(consulta, VisualizacaoArquivosDTO.class);
		Query count = em.createQuery(maximo);

		HelperQuery.customSetaFiltro(query,count,dto);
		
		int start = pageable.getPageSize() * (pageable.getPageNumber());

		query.setFirstResult(start);
		query.setMaxResults(pageable.getPageSize());
		Long total = (Long) count.getSingleResult();

		return new PageImpl<>(query.getResultList(), pageable, total);

	}

	@Override
	public List<AgendamentoETL> ultimoUploadGeracaoCadastrado() {
		StringBuilder hql = new StringBuilder();
		hql.append(SELECT_HQL_INITIAL);
		hql.append(" and a.evento =  '" + EventoEnum.UPLOAD.getNome() +"'");
		hql.append(" order by a.id desc ");

		Query query = em.createQuery(hql.toString(), AgendamentoETL.class);
		query.setMaxResults(1);
		return (List<AgendamentoETL>) query.getResultList();
	}
	
	@Override
	public List<AgendamentoETL> ultimoUploadGeracaoCadastradoGerado() {
		StringBuilder hql = new StringBuilder();
		hql.append(SELECT_HQL_INITIAL);
		hql.append(" and a.evento = '"+EventoEnum.UPLOAD.getNome()+"'");
		hql.append(" and a.dtHrProcessamento is not null");
		hql.append(" order by a.id desc ");

		Query query = em.createQuery(hql.toString(), AgendamentoETL.class);
		query.setMaxResults(1);
		return (List<AgendamentoETL>) query.getResultList();
	}
	
	@Override
	public Integer gerarNumeroCodigoAfastamento() {
		StringBuilder sql = new StringBuilder();
		
		sql.append(" ;WITH codigo AS ( " + 
					" SELECT " +
					" upper(CO_PREFIXO) as CO_PREFIXO, " + 
					" max(NU_SOLICITACAO) + 1 as NU_SOLICITACAO " + 
					" from DCETB038_CODIGO_SOLICITACAO " + 
					" group by CO_PREFIXO " + 
					" union " + 
					" select upper('TSE') as CO_PREFIXO, 1 as NU_SOLICITACAO union " + 
					" select upper('EL')as CO_PREFIXO, 1 as NU_SOLICITACAO union " + 
					" select upper('GE')as CO_PREFIXO, 1 as NU_SOLICITACAO " + 
					" except " + 
					" select CO_PREFIXO, NU_SOLICITACAO " + 
					" from DCETB038_CODIGO_SOLICITACAO " + 
					" ) " + 
					" select " + 
					" NU_SOLICITACAO " + 
					" from codigo as co " );
		sql.append(" where co.CO_PREFIXO = 'TSE';");
	
		Query query = em.createNativeQuery(sql.toString());
		return (Integer) query.getSingleResult();
	}
	
}
