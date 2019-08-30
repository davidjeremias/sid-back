package br.com.caixa.sidce.domain.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

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

@Builder
@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper=false)
@Table(name = "DCETB044_PARAMETRO_DOWNLOAD_TSE")
public class ParametrosIntegracaoTSE extends EntidadeBase<Integer>{

	private static final long serialVersionUID = 5069062023289975938L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "NU_ID")
	private Integer id;
	
	@Column(name = "DE_URL")
	private String url;
	
	@Column(name = "NU_DIA_UTIL")
	private Integer dia;
	
	@Column(name = "HR_EXECUCAO")
	private LocalTime hora;
	
	@Column(name = "DH_EXCLUSAO")
	private LocalDateTime dataExclusao;
	
	@Column(name = "CO_MATRICULA")
	private String matricula;
	
	@Tolerate
	public ParametrosIntegracaoTSE() {
		super();
	}
}
