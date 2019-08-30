package br.com.caixa.sidce.domain.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.com.caixa.sidce.util.infraestructure.domain.model.EntidadeBase;
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
@Table(name = "DCETB038_CODIGO_SOLICITACAO")
public class CodigoSolicitacao extends EntidadeBase<Integer> implements Serializable {

	private static final long serialVersionUID = -4684901456065459615L;

	@Id
	@Column(name = "NU_ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id; // Sequencial da tabela, auto incremento - chave primária.

	@Column(name = "CO_PREFIXO")
	private String prefixo;

	@Column(name = "NU_SOLICITACAO")
	private Integer numero;

	@Column(name = "CO_SOLICITACAO")
	private String codigo;

	@Tolerate
	public CodigoSolicitacao() {
		super();
	}

}