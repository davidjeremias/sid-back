package br.com.caixa.sidce.domain.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import br.com.caixa.sidce.domain.model.enums.TipoSolicitacaoEnum;
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
@Table(name = "DCETB034_SOLICITACAO")
public class Solicitacao extends EntidadeBase<Integer> {
	
	private static final long serialVersionUID = -3201795972507500109L;

	@Id
	@Column(name = "NU_ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "CO_MATRICULA")	//	[varchar]
	private String matricula;
	
	@Column(name = "IC_TIPO_SOLICITACAO")
	@Enumerated(EnumType.STRING)
	private TipoSolicitacaoEnum tipoSolicitacao;
	
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@Column(name = "DH_CADASTRO")
	private LocalDateTime dtHoraCadastro;
	
	@Column(name = "IC_RASCUNHO")	//	[bit]
	private Boolean rascunho;
	
	@ManyToOne
	@JoinColumn(name = "NU_ID_TB037")	
	private SituacaoSolicitacao situacaoSolicitacao;
	
	@JsonManagedReference(value="solicitacaoConta")
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "NU_ID_TB034")
	private List<SolicitacaoConta> contas;

	@JsonIgnore
	@JsonManagedReference(value="solicitacao")
	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name = "NU_ID_TB034")
	private List<Oficio> oficios;
	
	@Column(name = "DE_MOTIVO_REJEICAO")	//	[varchar]
	private String motivoRejeicao;
	
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@Column(name = "DH_ANALISE")
	private LocalDateTime dtHoraAnalise;
	
	@Column(name = "CO_MATRICULA_RESPONSAVEL")
	private String matriculaResponsavel;
	
	@Column(name = "NU_UNIDADE")
	private Integer unidadeSolicitante;
	
	@Column(name = "NU_UNIDADE_RESPONSAVEL")
	private Integer unidadeResponsavel;
	
	@Column(name = "IC_PRE_APROVADO")
	private Boolean isPreAprovado;
	
	@OneToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(referencedColumnName="NU_ID", name="NU_ID_TB038")
	private CodigoSolicitacao codigoSolicitacao;
	
	@Tolerate
	public Solicitacao() {
		super();
	}
}
