package br.com.caixa.sidce.domain.model;

import java.util.Date;

import javax.persistence.*;

import lombok.*;
import lombok.experimental.Tolerate;

@Builder(toBuilder = true)
@Getter
@Setter
@Entity
@Table(name = "DCETB010_CONTA")
public class Conta extends DadosCompartilhados {

	private static final long serialVersionUID = 9138204436643031735L;

	/**
	 * Código identificador da tabela DCETB010_CONTA Auto incremento (AA) Chave
	 * primária (PK)
	 */
	@Id
	@Basic(optional = false)
	@Column(name = "NU_ID", nullable = false)
	private Integer id;

	/**
	 * Código COMPE da instituição financeira: Tipo: numérico Tamanho: 3 caracteres;
	 */
	@Column(name = "NU_BANCO")
	private Integer codigoBanco;

	/**
	 * Número da agência sem dígito verificador
	 */
	@Column(name = "NU_AGENCIA")
	private Integer numeroAgencia;

	/**
	 * Número da conta com o dígito verificador. Não usar separadores, tais como
	 * ponto, barra, traço ou outro caractere de formatação.
	 */
	@Column(name = "NU_OP_CONTA_DV")
	private String numeroConta;

	/**
	 * Utilizar os seguintes códigos: 1 - Conta corrente 2 - Conta de poupança 3 -
	 * Conta investimento 4 - Outros casos.
	 */
	@Column(name = "NU_TIPO")
	private Integer tipo;

	/**
	 * Data de abertura da conta
	 */
	@Column(name = "DT_ABERTURA")
	private String dataAbertura;

	/**
	 * Data de encerramento da conta
	 */
	@Column(name = "DT_ENCERRAMENTO")
	private String dataEncerramento;

	/**
	 * Utilizar os seguintes códigos: 1 - para conta investigada com movimentação
	 * bancária no período de afastamento do sigilo bancário 2 - para conta
	 * investigada sem movimentação no período 3 - quando tratar-se de conta da
	 * mesma instituição financeira que efetuou transação bancária com uma conta
	 * investigada.
	 */
	@Column(name = "NU_MOVIMENTACAO")
	private Integer movimentacao;

	/**
	 * Data e hora de realização do processamento da informação.
	 */
	@Column(name = "DH_PROCESSAMENTO")
	private Date dtHrProcessamento;

	@Tolerate
	public Conta() {
		super();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Conta teste = (Conta) o;

		return this.numeroAgencia.equals(teste.numeroAgencia) && this.numeroConta.equalsIgnoreCase(teste.numeroConta)
				&& this.tipo.equals(teste.tipo);
	}

	@Override
	public int hashCode() {
		return this.numeroAgencia.hashCode() + this.numeroConta.hashCode() + this.tipo.hashCode();
	}

}