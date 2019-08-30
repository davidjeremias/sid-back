package br.com.caixa.sidce.domain.model;

import java.time.LocalDateTime;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
@Table(name = "DCETB045_AUDITORIA_DOWNLOAD_TSE")
public class AuditoriaDownloadTSE extends EntidadeBase<Integer>{
	
	private static final long serialVersionUID = 1473041433473809114L;
	
	@Id
    @Basic(optional = false)
    @Column(name = "NU_ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "DH_PROCESSAMENTO")
	private LocalDateTime dataHoraProcessamento;
	
	@Column(name = "IC_SUCESSO")
	private Integer status;
	
	@OneToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(referencedColumnName="NU_ID", name="NU_ID_TB044")
	private ParametrosIntegracaoTSE parametrosIntegracaoTSE;
	
	@Tolerate
	public AuditoriaDownloadTSE() {
		super();
	}
}
