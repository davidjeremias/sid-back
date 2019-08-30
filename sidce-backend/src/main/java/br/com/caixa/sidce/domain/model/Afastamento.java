package br.com.caixa.sidce.domain.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import br.com.caixa.sidce.util.infraestructure.domain.model.EntidadeBase;
import br.com.caixa.sidce.util.infraestructure.domain.model.serializer.LocalDateTimeDeserializer;
import br.com.caixa.sidce.util.infraestructure.domain.model.serializer.LocalDateTimeSerializer;
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
@Table(name = "DCETB039_AFASTAMENTO_SIGILO")
public class Afastamento extends EntidadeBase<Integer> {
	
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "NU_ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "CO_MATRICULA")	//	[varchar]
	private String matricula;
	
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@Column(name = "DH_CADASTRO")
	private LocalDateTime dtHoraCadastro;
	
	@Column(name = "NU_UNIDADE")	//	[int]
	private Integer unidade;

	@JsonBackReference(value="solicitacaoConta")
	@ManyToOne
	@JoinColumn(name = "NU_ID_TB034")	
	private Solicitacao solicitacao;
	
	@OneToOne
	@JoinColumn(referencedColumnName="NU_ID", name="NU_ID_TB027")
	private AgendamentoETL agendamento;
	
	@Tolerate
	public Afastamento() {
		super();
	}
}
