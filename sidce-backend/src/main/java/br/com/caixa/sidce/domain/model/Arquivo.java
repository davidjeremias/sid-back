package br.com.caixa.sidce.domain.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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
@Table(name = "DCETB029_ARQUIVO")
public class Arquivo extends EntidadeBase<Integer> {
	
	private static final long serialVersionUID = -3382803789531485779L;

	@Id
	@Column(name = "NU_ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "IM_ARQUIVO")
	private byte[] bytesArquivo;
	
	@Column(name = "NO_ARQUIVO")
	private String nomeArquivo;
	
	@Column(name = "DE_ARQUIVO")
	private String descricaoArquivo;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DH_CADASTRO")
	private Date dtHrCadastro;
	
	@Column(name = "CO_USUARIO")
	private String usuario;
	
	@GenericGenerator(name = "generator", strategy = "guid", parameters = {})
	@GeneratedValue(generator = "generator")
	@Column(name = "CO_IMPORTACAO", columnDefinition = "uniqueidentifier", nullable= true)
	private String codigo;
	
	@OneToOne
	@JoinColumn(referencedColumnName="NU_ID", name="NU_ID_TB027")
	private AgendamentoETL agendamento;

	@Tolerate
	public Arquivo() {
		super();
	}
}
