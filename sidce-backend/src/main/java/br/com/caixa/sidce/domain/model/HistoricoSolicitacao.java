package br.com.caixa.sidce.domain.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import br.com.caixa.sidce.domain.model.enums.SituacaoSolicitacaoEnum;
import br.com.caixa.sidce.util.infraestructure.domain.model.EntidadeBase;
import br.com.caixa.sidce.util.infraestructure.domain.model.serializer.LocalDateTimeDeserializer;
import br.com.caixa.sidce.util.infraestructure.domain.model.serializer.LocalDateTimeSerializer;
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
@Table(name = "DCETB029_ARQUIVO") // TODO - Verificar nome da tabela
public class HistoricoSolicitacao extends EntidadeBase<Integer> {
	
	private static final long serialVersionUID = -3382803789531485779L;

	@Id
	@Column(name = "NU_ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "DE_EVENTO")
	@Enumerated(EnumType.STRING)
	private SituacaoSolicitacaoEnum situacao;
	
	@Column(name = "DE_MATRICULA")
	private String matricula;
	
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@Column(name = "DH_CADASTRO")
	private LocalDateTime dtHoraCadastro;
	
	@Tolerate
	public HistoricoSolicitacao() {
		super();
	}
}
