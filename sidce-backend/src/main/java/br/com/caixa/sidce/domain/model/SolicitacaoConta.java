package br.com.caixa.sidce.domain.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import br.com.caixa.sidce.util.infraestructure.domain.model.EntidadeBase;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Builder(toBuilder=true)
@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper=false)
@Table(name = "DCETB036_SOLICITACAO_CONTA")
public class SolicitacaoConta extends EntidadeBase<Long> {

	private static final long serialVersionUID = 589491074410817489L;

	@Id
	@Column(name = "NU_ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "NU_CPF_CNPJ")	//	[varchar]	CPF ou CNPJ alvo do afastamento do sigilo.
	private String cpfCNPJ;
	
	@Column(name = "NU_AGENCIA")	//	[int]	Número da agência referene à conta alvo de afastamento do sigilo.
	private Integer numeroAgencia;
	
	@Column(name = "NU_OPERACAO")	//	[int]	Número da operação referene à conta alvo de afastamento do sigilo.
	private Integer numeroOperacao;
	
	@Column(name = "NU_CONTA")	//	[varchar]	Número da conta alvo de afastamento do sigilo.
	private String numeroConta;
	
	@Column(name = "NU_DV_CONTA")	//	[tinyint]	Número da operação referene à conta alvo de afastamento do sigilo.
	private Integer digitoConta;
	
	@Column(name = "DT_ABERTURA")	//	[date]	Data de abertura da conta.
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataAbertura;
	
	@Column(name = "NO_SITUACAO_CONTA")	//	[varchar]	Indica a situação atual da conta.
	private String situacao;
	
	@Column(name = "DT_INICIO_PERIODO")	//	[date]	Data inicial do período referente ao afastamento do sigilo.
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate inicioPeriodo;
	
	@Column(name = "DT_FIM_PERIODO")	//	[date]	Data final do período referente ao afastamento do sigilo.
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonDeserialize(using = LocalDateDeserializer.class) 
	private LocalDate fimPeriodo;

	@JsonBackReference(value="solicitacaoConta")
	@ManyToOne
	@JoinColumn(name = "NU_ID_TB034")	
	private Solicitacao solicitacao;
	
	@Transient
	private String nomeResponsavel;

	@Tolerate
	public SolicitacaoConta() {
		super();
	}
}
