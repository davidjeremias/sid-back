package br.com.caixa.sidce.infraestructure.persistence.jpa.util;

import javax.persistence.Query;

import br.com.caixa.sidce.interfaces.web.dto.VisualizacaoArquivosDTO;

public class HelperQuery {

	private StringBuilder select = new StringBuilder();
	private StringBuilder join = new StringBuilder();
	private StringBuilder where = new StringBuilder();
	private StringBuilder selectCount = new StringBuilder();
	
	public static final String TABELA_PRINCIPAL = "t";
	public static final String TABELA_JOIN = "e";
	public static final String CONDICAO_FIXA = " = e.";
		
	public HelperQuery(String coluna, String contador, String objeto, String compareParam, VisualizacaoArquivosDTO dto, String... conditions) {
		select.append("SELECT DISTINCT " + coluna + " FROM   Titular AS t ");
		selectCount.append("SELECT " + contador  + " FROM   Titular AS t ");
		join.append("INNER JOIN " + objeto + " AS e");
		join.append(" ON t." + compareParam + CONDICAO_FIXA + compareParam);
		
		for (String condition : conditions) {
			setCondition(condition);
		}
		
		where.append(" WHERE  1=1");
		customFiltro(where, dto);
	}
	
	public String getConsulta() {
		return customMontaQuery(select.toString(), join.toString(), where.toString());
	}
	
	public String getMaximo() {
		return customMontaQuery(selectCount.toString(), join.toString(), where.toString());
	}
	
	public void setCondition(String condition) {
		join.append(condition);
	}
	
	public StringBuilder setAnd(String param) {
		return join.append(" AND t." + param + CONDICAO_FIXA + param);
	}
	
	public StringBuilder setAnd(String tabela1, String tabela2, String param) {
		return join.append(" AND " + tabela1 + "." + param + " = " + tabela2 + "." + param);
	}
	
	public StringBuilder setOr(String param) {
		return join.append(" OR t." + param + CONDICAO_FIXA + param);
	}
	
	public StringBuilder setOr(String tabela1, String tabela2, String param) {
		return join.append(" OR " + tabela1 + "." + param + " = " + tabela2 + "." + param);
	}
	
	public StringBuilder newJoin(String newTable, String alias, String tabelaJoin, String coluna) {
		join.append(" INNER JOIN " + newTable + " AS " + alias);
		join.append(" ON " + tabelaJoin + "." + coluna + " = " + alias + "." + coluna);
		return join;
	}
	
	public static void customFiltro(StringBuilder where, VisualizacaoArquivosDTO dto) {
		if (dto.getCodigo() != null)
			where.append(" AND  t.codigo = :codigo");
		if (dto.getCnpj() != null)
			where.append(" AND t.cpfCNPJ = :cnpj ");
		if (dto.getNumeroAgencia() != null)
			where.append(" AND t.numeroAgencia = :numeroAgencia ");
		if (dto.getNumeroConta() != null)
			where.append(" AND t.numeroConta = :numeroConta ");
	}

	public static void customSetaFiltro(Query query, Query count, VisualizacaoArquivosDTO dto) {
		if (dto.getCodigo() != null) {
			query.setParameter("codigo", dto.getCodigo());
			count.setParameter("codigo", dto.getCodigo());
		}
		if (dto.getCnpj() != null) {
			query.setParameter("cnpj", dto.getCnpj());
			count.setParameter("cnpj", dto.getCnpj());
		}
		if (dto.getNumeroConta() != null) {
			query.setParameter("numeroConta", dto.getNumeroConta());
			count.setParameter("numeroConta", dto.getNumeroConta());
		}
		if (dto.getNumeroAgencia() != null) {
			query.getParameters();
			count.getParameters();
			query.setParameter("numeroAgencia", Integer.parseInt(dto.getNumeroAgencia()));
			count.setParameter("numeroAgencia", Integer.parseInt(dto.getNumeroAgencia()));
		}
	}
	
	public static String customMontaQuery(String select, String join, String where) {
		return new StringBuilder().append(select).append(join).append(where).toString();
	}
}
