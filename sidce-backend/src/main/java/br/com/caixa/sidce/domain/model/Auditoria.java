package br.com.caixa.sidce.domain.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.caixa.sidce.util.infraestructure.auditoria.TipoEventoAuditoriaEnum;
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
@Table(name="DCETB031_AUDITORIA_APLICACAO")
public class Auditoria extends EntidadeBase<Integer> {

	private static final long serialVersionUID = 6250995919300056137L;

	@Id
	@Column(name = "NU_ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "NO_EVENTO")
    @Enumerated(EnumType.STRING)
    private TipoEventoAuditoriaEnum tipoEvento;
	
	@Column(name = "NO_EQUIPAMENTO")
	private String host;
	
	@Column(name = "CO_USUARIO")
	private String matricula;
	
	@Column(name = "NO_FUNCIONALIDADE")
	private String funcionalidade;
	
	@Column(name = "DH_EVENTO")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dtHrAuditoria;
	
	@OneToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(referencedColumnName="NU_ID", name="NU_ID_TB038")
	private CodigoSolicitacao codigoSolicitacao;
	
	@OneToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(referencedColumnName="NU_ID", name="NU_ID_TB046")
	private UnidadeGestora unidadeGestora;

	@Tolerate
	public Auditoria() {
		super();
	}
}
