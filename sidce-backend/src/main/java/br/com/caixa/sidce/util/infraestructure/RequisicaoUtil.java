package br.com.caixa.sidce.util.infraestructure;

import java.util.Map;

public class RequisicaoUtil {

	/**
	 * Retorna o primeiro valor [0] da chave passada caso exista.
	 * 
	 * @param params - Map com os parametros da requisição
	 * @param nomeParametro - nome do parametro à ser retornado
	 * @return
	 */
	public static String extrairParametro(Map<String, String[]> params, String nomeParametro) {
		return params.get(nomeParametro) != null ? params.get(nomeParametro)[0] : null;
	}
	
	/**
	 * Retorna o array de valores da chave passada caso exista.
	 * 
	 * @param params - Map com os parametros da requisição
	 * @param nomeParametro - nome do parametro à ser retornado
	 * @return
	 */
	public static String[] extrairParametros(Map<String, String[]> params, String nomeParametro) {
		return params.get(nomeParametro) != null ? params.get(nomeParametro) : null;
	}
}
