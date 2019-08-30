package br.com.caixa.sidce.domain.model;

import java.util.Date;

import javax.persistence.*;

import lombok.*;
import lombok.experimental.Tolerate;

@Builder(toBuilder = true)
@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "DCETB009_BANCO")
public class Banco extends DadosCompartilhados {

	private static final long serialVersionUID = 3316751537171803346L;

	@Id
	@Basic(optional = false)
	@Column(name = "NU_ID", nullable = false)
	private Integer id; // Sequencial da tabela, auto incremento - chave primária.

	@Column(name = "NU_BANCO")
	private Integer codigoBanco;

	@Column(name = "NU_AGENCIA") // Número da agência sem dígito verificador.
	private Integer numeroAgencia;

	@Column(name = "NO_AGENCIA") // Nome da agência.
	private String nomeAgencia;

	@Column(name = "DE_ENDERECO") // Endereço e complemento da agência.
	private String endereco;

	@Column(name = "NO_PAIS") // Nome do país em que a agência está situada.
	private String pais;

	@Column(name = "NO_CIDADE") // Nome da cidade em que a agência está situada.
	private String cidade;

	@Column(name = "SG_UF") // Sigla da Unidade da Federação em que a agência está situada.
	private String uf;

	@Column(name = "NU_CEP") // CEP da agência no formato 99999999.
	private String cep;

	@Column(name = "NU_TELEFONE") // Número de telefone da agência.
	private String telefone;

	@Column(name = "DT_ABERTURA_AGENCIA") // Data de abertura da agência.
	private Date dataAberturaAgencia;

	@Column(name = "DT_FECHAMENTO_AGENCIA") // Data de fechamento da agência.
	private Date dataFechamentoAgencia;

	@Column(name = "DH_PROCESSAMENTO") // Data e hora de realização do processamento da informação.
	private Date dtHrProcessamento;

	@Tolerate
	public Banco() {
		super();
	}

}