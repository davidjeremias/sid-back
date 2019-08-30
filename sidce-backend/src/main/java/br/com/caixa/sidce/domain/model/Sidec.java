package br.com.caixa.sidce.domain.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import br.com.caixa.sidce.util.infraestructure.domain.model.EntidadeBase;
import br.com.caixa.sidce.util.infraestructure.domain.model.serializer.DataHoraDeserializer;
import br.com.caixa.sidce.util.infraestructure.domain.model.serializer.DataHoraSerializer;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Builder
@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper=false)
@Table(name = "DCETB026_CALENDARIO")
public class Sidec extends EntidadeBase<Integer> {

	private static final long serialVersionUID = 4247397696681453810L;

	@Id
	@Column(name = "NU_ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "NU_AGENCIA")	//	[int]	Número da agência.
	private Integer numeroAgencia;
	
	@Column(name = "NU_OPERACAO")	//	[smallint]	Número da operação.
	private Integer numeroOperacao;
	
	@Column(name = "NU_CONTA")	//	[int]	Número da conta.
	private Integer numeroConta;
	
	@Column(name = "NU_DV")	//	[smallint]	Número do dígito verificador.
	private Integer digitoVerificador;
	
	@Column(name = "NU_CPF_CNPJ")	//	[varchar]	Número do CPF/CNPJ do titular da conta.
	private String cpfCNPJ;
	
	@JsonSerialize(using = DataHoraSerializer.class)
	@JsonDeserialize(using = DataHoraDeserializer.class)
	@Column(name = "DT_ABERTURA")	//	[date]	Data de abertura da conta.
	private LocalDate data;

	@Tolerate
	public Sidec() {
		super();
	}
}
