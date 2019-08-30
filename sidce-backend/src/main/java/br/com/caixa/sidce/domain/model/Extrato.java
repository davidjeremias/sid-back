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
@Table(name = "DCETB013_EXTRATO")
public class Extrato extends DadosCompartilhados {

	private static final long serialVersionUID = 3444204998006203647L;

	@Id
	@Basic(optional = false)
	@Column(name = "NU_ID", nullable = false)
	private Integer id; // Sequencial da tabela, auto incremento - chave primária.

	@Column(name = "CO_CHAVE_EXTRATO") // int Número sequencial gerado pela instituição financeira para identificação
										// dos lançamentos. Este código não pode ser repetido neste arquivo, portanto,
										// individualiza e vincula cada registro do arquivo "EXTRATO" aos seus
										// correspondentes registros no arquivo "ORIGEM_DESTINO".
	private Integer chaveExtrato;

	@Column(name = "NU_BANCO") // int Código COMPE da instituição financeira na qual o investigado possui
								// conta.
	private Integer codigoBanco;

	@Column(name = "NU_AGENCIA") // int Número da agência, sem dígito verificador, na qual o investigado possui
									// conta.
	private Integer numeroAgencia;

	@Column(name = "NU_CONTA") // varchar Número da conta investigada com o dígito verificador.
	private String numeroConta;

	@Column(name = "NU_TIPO_CONTA") // int Utilizar os seguintes códigos: "1" para conta corrente, "2" para conta de
									// poupança, "3" para conta investimento,"4" para outros casos.
	private Integer tipoConta;

	@Column(name = "DT_LANCAMENTO") // varchar Data em que foi realizado o lançamento.
	private String dataLancamento;

	@Column(name = "NU_DOCUMENTO") // varchar Código do documento utilizado pela instituição financeira para
									// identificar o lançamento.
	private String documento;

	@Column(name = "DE_LANCAMENTO") // varchar Histórico da transação, descrição do tipo de lançamento realizado.
	private String descricaoLancamento;

	@Column(name = "NU_TIPO_LANCAMENTO") // int Preencher com o código do tipo da transação, conforme Anexo desta
											// cartacircular.
	private Integer tipoLancamento;

	@Column(name = "VR_LANCAMENTO") // varchar Valor do lançamento.
	private String valorLancamento;

	@Column(name = "IC_NATUREZA_LANCAMENTO") // char Informar natureza do lançamento: "C" para Crédito, "D" para Débito,
												// sinal asterisco "*" para Outros (Exemplos: bloqueios, provisões,
												// lançamentos futuros).
	private String naturezaLancamento;

	@Column(name = "VR_SALDO") // varchar Valor do saldo da conta após o lançamento.
	private String valorSaldo;

	@Column(name = "IC_NATUREZA_SALDO") // char Informar a natureza do saldo:"C" para Credor, "D" para Devedor.
	private String naturezaSaldo;

	@Column(name = "DE_LOCAL_TRANSACAO") 
	private String localTransacao;

	@Column(name = "NO_ORIGEM") // varchar Interface de origem do dado.
	private String origem;

	@Tolerate
	public Extrato() {
		super();
	}

}