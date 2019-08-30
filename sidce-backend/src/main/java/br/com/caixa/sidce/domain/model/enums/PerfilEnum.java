package br.com.caixa.sidce.domain.model.enums;

public enum PerfilEnum {

	REPRESENTANTEGIPES("REPRESENTANTEGIPES"),
	ADMINISTRADORPCMSO("ADMINISTRADORPCMSO"),
	MEDICOCAIXA("MEDICOCAIXA"),
	MEDICOCORDENADOR("MEDICOCORDENADOR"),
	MEDICOEXTERNO("MEDICOEXTERNO");
	
	// ****************************************************************

	/**
	 * Nome do tipo de situação.
	 */
	private String descricao;

	// ****************************************************************

	/**
	 * Construtor padrão.
	 * @param codigo String
	 * @param descricao String
	 */
	private PerfilEnum(String descricao) {
		this.descricao = descricao;
	}

	/**
	 * @return the descricao
	 */
	public String getDescricao() {
		return descricao;
	}
}
