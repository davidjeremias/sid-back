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
@Table(name = "DCETB011_TITULAR")
public class Titular extends DadosCompartilhados {

	private static final long serialVersionUID = 9092980340383033938L;

	@Id
	@Basic(optional = false)
	@Column(name = "NU_ID", nullable = false)
	private Integer id; // Sequencial da tabela, auto incremento - chave primária.

	@Column(name = "NU_BANCO") // Código COMPE da instituição financeira: Tipo: numérico; Tamanho: 3 caracteres
	private Integer codigoConta;

	@Column(name = "NU_AGENCIA") // Número da agência sem dígito verificador.
	private Integer numeroAgencia;

	@Column(name = "NU_CONTA") // Número da conta com o dígito verificador.
	private String numeroConta;

	@Column(name = "NU_TIPO_CONTA") // Utilizar os seguintes códigos: "1" para conta corrente, "2" para conta de
									// poupança, "3" para conta investimento, "4" para outros casos.
	private Integer tipoConta;

	@Column(name = "CO_TIPO_TITULAR") // Informa o tipo de vinculo na conta: Utilizar "T" para o Titular, "1" para o
										// primeiro cotitular, "2" para o segundo cotitular e assim consecutivamente; ou
										// "R" para Representante, "L" para representante legal, "P" para procurador "O"
										// para outros.
	private String tipoVinculo;

	@Column(name = "IC_PESSOA_INVESTIGADA") // 0 e 1 - "0" se não teve o sigilo afastado, "1" se teve o sigilo afastado.
											// Contas que possuem o CPF/CNPJ no arquivo do TSE devem ter o valor 1, para
											// CPF/CNPJ ue não esteja presente no arquivo do TSE o valor é 0.
	private Integer pessoaInvestigada;

	@Column(name = "NU_TIPO_PESSOA_TITULAR") // Indica se é pessoa natural ou jurídica. Utilizar "1" para pessoa natural
												// ou "2" para pessoa jurídica.
	private Integer tipoPessoaTitular;

	@Column(name = "NU_CPF_CNPJ_TITULAR") // Número da inscrição no Cadastro de Pessoas Físicas (CPF) ou no Cadastro
											// Nacional da Pessoa Jurídica (CNPJ), de acordo com o TIPO_PESSOA_TITULAR.
											// Utilizar somente números, sem separadores.
	private String cpfCNPJ;

	@Column(name = "NO_TITULAR") // Nome completo da pessoa.
	private String nomeCompleto;

	@Column(name = "NO_DOC_IDENTIFICACAO") // RG, Carteira de Trabalho, Identidade Funcional, qualquer documento que
											// constitua a pessoa Jurídica.
	private String nomeDocumentoIdentificacao;

	@Column(name = "NU_DOC_IDENTIFICACAO") // Número e complemento do documento de identificação, conforme registrado
											// pela instituição financeira, podendo conter formatação. Exemplos: RG
											// 123456 SSP/DF, OAB 1234-DF.
	private String numeroDocumentoIdentificacao;

	@Column(name = "NO_LOGRADOURO") // Endereço de domicílio da pessoa e complemento.
	private String logradouro;

	@Column(name = "NO_CIDADE") // Nome da cidade de domicílio da pessoa.
	private String cidade;

	@Column(name = "SG_UF") // Sigla da Unidade da Federação de domicílio da pessoa.
	private String uf;

	@Column(name = "NO_PAIS") // Nome do país de domicílio da pessoa.
	private String pais;

	@Column(name = "NU_CEP") // CEP da pessoa no formato 99999999.
	private String cep;

	@Column(name = "NU_TELEFONE_PESSOA") // Número de telefone da pessoa.
	private String telefone;

	@Column(name = "VR_RENDA") // Valor da renda declarada pelo correntista à instituição financeira.
	private Long renda;

	@Column(name = "DT_ATUALIZADA_RENDA") // Data da última atualização do valor da renda declarada pelo correntista à
											// instituição financeira.
	private String ultimaAtualizacaoRenda;

	@Column(name = "DT_INICIO_RELACIONAMENTO_CONTA") // Data de início de relacionamento com a conta.
	private String inicioRelacionamento;

	@Column(name = "DT_FIM_RELACIONAMENTO_CONTA") // Data de fim de relacionamento com a conta.
	private String fimRelacionamento;

	@Tolerate
	public Titular() {
		super();
	}

}