package br.com.caixa.sidce.domain.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Builder(toBuilder = true)
@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "DCETB014_ORIGEM_DESTINO")
public class OrigemDestino extends DadosCompartilhados {

	private static final long serialVersionUID = 7572925979335555590L;

	@Id
	@Basic(optional = false)
	@Column(name = "NU_ID", nullable = false)
	private Integer id; // Sequencial da tabela, auto incremento - chave primária.

	@Column(name = "CO_CHAVE_OD") // [int] Número sequencial gerado pela instituição financeira para identificação
									// dos registros de ORIGEM_DESTINO. Este código não pode ser repetido no
									// arquivo.
	private Integer codigoOrigemDestino;

	@Column(name = "CO_CHAVE_EXTRATO") // [int] Código relacionado ao campo "CODIGO_CHAVE_EXTRATO" no arquivo EXTRATO.
										// Para cada registro no arquivo "EXTRATO" sempre haverá um ou mais registros
										// correspondentes no arquivo "ORIGEM_DESTINO".
	private Integer chaveExtrato;

	@Column(name = "VR_TRANSACAO") // [numeric] Valor individual de cada documento da transação.
	private Long valorTransacao;

	@Column(name = "NU_DOCUMENTO_TRANSACAO") // [varchar] Número do documento usado pela instituição financeira para
												// identificar a transação.
	private String numeroDocumentoTranscao;

	@Column(name = "NU_BANCO_OD") // [int] Código COMPE da instituição financeira que enviou ou recebeu recursos
									// da conta investigada.
	private Integer codigoBanco;

	@Column(name = "NU_AGENCIA_OD") // [int] Número da agência, sem dígito verificador, que enviou ou recebeu
									// recursos da conta investigada.
	private Integer numeroAgencia;

	@Column(name = "NU_CONTA_OD") // [varchar] Número, com o dígito verificador, da conta que enviou ou recebeu
									// recursos da conta investigada.
	private String numeroConta;

	@Column(name = "NU_TIPO_CONTA_OD") // [smallint] Utilizar os seguintes códigos: "1" para conta corrente, "2" para
										// conta de poupança, "3" para conta investimento, "4" para outros casos.
	private Integer tipoConta;

	@Column(name = "NU_TIPO_PESSOA") // [smallint] Utilizar os seguintes códigos para o tipo de pessoa que participou
										// da transação como ordenante ou como beneficiária de recursos: "1" para
										// Natural, "2" para Jurídica.
	private Integer tipoPessoa;

	@Column(name = "NU_CPF_CNPJ_OD") // [varchar] Número do CPF ou CNPJ da pessoa que efetuou a transação como
										// ordenante ou como beneficiária de recursos. Nos casos de endosso, preencher o
										// CPF/CNPJ do beneficiário final que recebeu o cheque endossado (endossatário),
										// constante no verso do cheque.
	private String cpfCnpj;

	@Column(name = "NO_PESSOA_OD") // [varchar] Nome da pessoa que efetuou a transação com o investigado, como
									// ordenante ou como beneficiária final de recursos. Nos casos de endosso,
									// preencher o nome do beneficiário final que recebeu o cheque endossado
									// (endossatário), constante no verso do cheque.
	private String nomePessoa;

	@Column(name = "NO_DOC_IDENTIFICACAO_OD") // [varchar] Nome do documento de identificação do ordenante ou
												// beneficiário final usado na transação, que não seja CPF. Exemplos:
												// RG, Carteira de Trabalho, Identidade Funcional, entre outros.
	private String nomeDocumentoIdentificacao;

	@Column(name = "NU_DOC_IDENTIFICACAO_OD") // [varchar] Número e complemento do documento de identificação do
												// ordenante ou beneficiário final conforme registrado pela instituição
												// financeira, podendo conter caractere de formatação. Exemplos: RG
												// 123456 SSP/DF, OAB 1234-DF.
	private String numeroDocumentoIdentificacao;

	@Column(name = "CO_CODIGO_BARRAS") // [varchar] Números que compõem o código de barras (incluindo o DV) de um
										// documento de compensação (boleto). Devem ser usados caracteres de separação,
										// tais como ponto, barra, traço ou outro caractere de formatação.
	private String codigoBarras;

	@Column(name = "NO_ENDOSSANTE_CHEQUE") // [varchar] Nos casos de endosso, neste campo deve constar o nome da pessoa
											// natural ou jurídica para quem o cheque foi nominado, localizado no
											// anverso do documento, ou seja, o emissor do endosso (endossante).
	private String nomeEndossante;

	@Column(name = "DE_DOC_ENDOSSANTE_CHEQUE") // [varchar] Nome, número e complemento do documento de identificação da
												// pessoa natural ou jurídica para quem o cheque foi nominado,localizado
												// no anverso do documento, ou seja, o emissor do endosso (endossante)
												// usado na transação. Exemplos: CPF 99999999999, RG 123456 SSP/DF, OAB
												// 1234-DF, Identidade Funcional 123456, etc.
	private String descricaoDocumentoEndossante;

	@Column(name = "NU_SITUACAO_IDENTIFICACAO") // [smallint] Informar o valor fixo "0" (zero).
	private Integer situacao;

	@Column(name = "DE_OBSERVACAO") // [varchar] Outras informações importantes, como por exemplo, "saque em
									// espécie", "saque na boca do caixa", "distribuição de depósito em contas
									// distintas", entre outras.
	private String observacao;

	@Tolerate
	public OrigemDestino() {
		super();
	}

}