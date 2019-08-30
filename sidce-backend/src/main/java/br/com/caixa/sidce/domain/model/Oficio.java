package br.com.caixa.sidce.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
@Table(name = "DCETB035_OFICIO")
public class Oficio extends EntidadeBase<Integer> {
	
	private static final long serialVersionUID = -1061035077059170112L;

	@Id
	@Column(name = "NU_ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "NO_ARQUIVO_OFICO")	//	[varchar]	Nome do arquivo de ofício.
	private String nomeArquivo;
	
	@Column(name = "IM_ARQUIVO_OFICIO")	//	[varbinary]	Conteúdo do ofício.
	private String base64arquivo;

	@JsonBackReference(value="solicitacao")
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "NU_ID_TB034")
	private Solicitacao solicitacao;

	@Tolerate
	public Oficio() {
		super();
	}
}
