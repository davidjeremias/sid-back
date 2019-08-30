package br.com.caixa.sidce.interfaces.web.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.caixa.sidce.domain.model.CodigoSolicitacao;
import br.com.caixa.sidce.domain.model.Oficio;
import br.com.caixa.sidce.domain.model.SituacaoSolicitacao;
import br.com.caixa.sidce.domain.model.SolicitacaoConta;
import br.com.caixa.sidce.domain.model.enums.SituacaoContaEnum;
import br.com.caixa.sidce.domain.model.enums.TipoSolicitacaoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoDTO implements Serializable {

	private static final long serialVersionUID = -5997638584810786265L;
	// Entidade
	private Integer id;
	private String matricula;
	private TipoSolicitacaoEnum tipoSolicitacao;
	private LocalDateTime dtHoraCadastro;
	private Boolean rascunho;
	private SituacaoSolicitacao situacaoSolicitacao;
	private List<SolicitacaoConta> contas;
	private List<Oficio> oficios;
	private String motivoRejeicao;
	private LocalDateTime dtHoraAnalise;
	private String matriculaResponsavel;
	private Integer unidadeSolicitante;
	private Integer unidadeResponsavel;
	private Boolean isPreAprovado;
	private CodigoSolicitacao codigoSolicitacao;
	private String situacaoArquivo;
	private UnidadeDTO unidadeDTO;
	private String codigo;

	// Objeto
	private Boolean afastamento;
	private Boolean pendente;
	private String oficio;
	private String nomeArquivo;
	private Integer numeroUnidade;

	// Filtros
	private LocalDateTime dtInicio;
	private LocalDateTime dtFim;
	private SituacaoContaEnum situacaoConta;
	private String cpfCNPJ;
	private ArrayList<SituacaoSolicitacao> situacoes;

	// Integração CICLI
	private Integer classeSistema;
	private String sistema;
	private String classe;
	private Integer classeParametro;
	private String current_datetime;
	private String versao_api;
	private List<ContratosDTO> contratos;

}
