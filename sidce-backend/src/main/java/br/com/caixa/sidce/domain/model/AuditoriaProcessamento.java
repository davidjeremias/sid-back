package br.com.caixa.sidce.domain.model;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column
;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

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
@Table(name = "DCETB001_AUDITORIA_PROCESSAMENTO")
public class AuditoriaProcessamento extends EntidadeBase<Integer> {

	private static final long serialVersionUID = -8224180713936582287L;

	@Id
    @Basic(optional = false)
    @Column(name = "NU_ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
    @Column(name = "NO_ARQUIVO")
    private String nome;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_ARQUIVO")
    private Date data;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DH_PROCESSAMENTO", nullable = false)
    private Date dataHoraProcessamento;
    
    @Column(name = "QT_REGISTROS_ARQ")
    private Integer quantidadeRegistros;
    
    @Column(name = "NO_ORIGEM")
    private String origem;
    
    @Column(name = "CO_PERIODO")
    private Integer periodo;
    
    @Column(name = "CO_USUARIO")
    private String matricula;

    @Column(name = "NO_EQUIPAMENTO")
    private String hostname;

    @Column(name = "DE_EVENTO")
	private String evento;
    
    @GenericGenerator(name = "generator", strategy = "guid", parameters = {})
	@GeneratedValue(generator = "generator")
	@Column(name = "CO_IMPORTACAO", columnDefinition = "uniqueidentifier", nullable= true)
	private String codigo;
    
    @Tolerate
	public AuditoriaProcessamento() {
		super();
	}
}