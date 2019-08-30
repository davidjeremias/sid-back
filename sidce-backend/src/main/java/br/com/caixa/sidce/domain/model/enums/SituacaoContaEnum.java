package br.com.caixa.sidce.domain.model.enums;

public enum SituacaoContaEnum {

	ATIVO(1, "Ativo/Reativado"), INATIVO(2, "Inativo por Elo"), ENCERRADO(3, "Encerrado/Inativo/Remanejado"),
	LIQUIDADO(4, "Liquidado"), SUSPENSO(5, "Suspenso/Pendente"), CANCELADO(6, "Cancelado/Excluído"),
	LANCADO(7, "Lançado a Prejuízo"), NAO_PERTENCE(8, "Não pertence ao cliente"),
	PARA_CORRECAO(9, "Para correção/complemento dos dados do contrato");

	private Integer id;
	private String nome;

	private SituacaoContaEnum(Integer id, String nome) {
		this.nome = nome;
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public Integer getId() {
		return id;
	}

	public static String getEnumByName(Integer id) {
		for (SituacaoContaEnum chave : SituacaoContaEnum.values()) {
			if (chave.getId().equals(id)) {
				return chave.getNome();
			}
		}
		return null;
	}
}
