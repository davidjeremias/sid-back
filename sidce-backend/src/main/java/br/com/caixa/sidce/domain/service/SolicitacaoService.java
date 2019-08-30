package br.com.caixa.sidce.domain.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import br.com.caixa.sidce.domain.model.AgendamentoETL;
import br.com.caixa.sidce.domain.model.CodigoSolicitacao;
import br.com.caixa.sidce.domain.model.Oficio;
import br.com.caixa.sidce.domain.model.SituacaoSolicitacao;
import br.com.caixa.sidce.domain.model.Solicitacao;
import br.com.caixa.sidce.domain.model.SolicitacaoConta;
import br.com.caixa.sidce.domain.model.enums.ParametroEnum;
import br.com.caixa.sidce.domain.model.enums.SituacaoArquivoEnum;
import br.com.caixa.sidce.domain.model.enums.SituacaoContaEnum;
import br.com.caixa.sidce.domain.model.enums.SituacaoSolicitacaoEnum;
import br.com.caixa.sidce.domain.model.enums.TipoSolicitacaoEnum;
import br.com.caixa.sidce.infraestructure.persistence.jpa.OficioRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.SituacaoSolicitacaoRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.SolicitacaoContaRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.SolicitacaoCustomRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.SolicitacaoRepository;
import br.com.caixa.sidce.interfaces.web.dto.ContratosDTO;
import br.com.caixa.sidce.interfaces.web.dto.SolicitacaoDTO;
import br.com.caixa.sidce.util.infraestructure.RequisicaoUtil;
import br.com.caixa.sidce.util.infraestructure.auditoria.AuditoriaAspecto;
import br.com.caixa.sidce.util.infraestructure.auditoria.TipoEventoAuditoriaEnum;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;
import br.com.caixa.sidce.util.infraestructure.exception.PeriodoDatasException;
import br.com.caixa.sidce.util.infraestructure.log.Log;
import br.com.caixa.sidce.util.infraestructure.service.RestFullService;

@Service
public class SolicitacaoService extends RestFullService<Solicitacao, Integer> {

	private static final String MATRICULA = "matricula";
	private static final String CODIGO = "codigo";
	private static final String AFASTAMENTO = "isAfastamento";
	private static final String PENDENTE = "isPendentes";
	private static final String UNIDADE = "numeroUnidade";
	private static final String DT_FIM = "fimPeriodo";
	private static final String DT_INICIO = "iniPeriodo";
	private static final String TIPO_SOLICITACAO = "tipo";
	private static final String SITUACAO_SOLICITACAO = "situacao";
	private static final String SITUACAO_SOLICITACAO_ARRAY = SITUACAO_SOLICITACAO + "[]";
	private static final String CPF_CNPJ = "cpfCNPJ";
	private static final String ELEITORAL = "ELEITORAL";

	@Autowired
	private SolicitacaoCustomRepository crepo;

	@Autowired
	private SituacaoSolicitacaoRepository situacaoSolicitacaoRepository;

	@Autowired
	OficioRepository oficioRepository;

	@Autowired
	SolicitacaoContaRepository solicitacaoContaRepository;

	@Autowired
	private ParametroService parametroService;

	@Autowired
	private AfastamentoService afastamentoService;

	@Autowired
	private AgendamentoETLService agendamentoETLService;

	@Autowired
	private SIICOService siicoService;

	@Autowired
	private AuditoriaAspecto auditoria;

	@Autowired
	private CodigoSolicitacaoService codigoSolicitacaoService;

	@Autowired
	private FactoryEmail email;

	@Autowired
	SolicitacaoService(SolicitacaoRepository repository) {
		super(repository);
	}

	public ResponseEntity<List<SolicitacaoConta>> buscaContasPorCpfCnpj(Map<String, String[]> params, String token) {
		List<SolicitacaoConta> listaContas = new ArrayList<>();
		String cpfCNPJ = RequisicaoUtil.extrairParametro(params, CPF_CNPJ);
		String apiKey = parametroService.buscarPorChave(ParametroEnum.API_KEY);
		String url = parametroService.buscarPorChave(ParametroEnum.CONTA_DEPOSITO_ENDPOINT);
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<String> request = new HttpEntity<>(montaHeaders(token, apiKey));
		try {
			ResponseEntity<SolicitacaoDTO> response = restTemplate.exchange(
					url + "?cpfcnpj=" + cpfCNPJ + "&campos=contratos&classe=1", HttpMethod.GET, request,
					SolicitacaoDTO.class);

			listaContas = criarListaSolicitacaoConta(response.getBody().getContratos(), cpfCNPJ);
			return ResponseEntity.ok(listaContas);
		} catch (HttpStatusCodeException e) {
			Log.info(getClass(), "INTEGRAÇÃO-COM-SICLI", e);
			// A API retorna 400 quando não encontra contas
			if (e.getStatusCode().value() == 400) {
				return new ResponseEntity<>(listaContas, HttpStatus.NO_CONTENT);
			} else {
				return new ResponseEntity<>(listaContas, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	public List<SolicitacaoConta> criarListaSolicitacaoConta(List<ContratosDTO> contratos, String cpfCNPJ) {
		List<SolicitacaoConta> solicitacoes = new ArrayList<>();
		contratos.forEach(c -> {

			SolicitacaoConta solicitacao = SolicitacaoConta.builder().id(Long.parseLong(c.getCoIdentificacao().trim()))
					.cpfCNPJ(cpfCNPJ).numeroAgencia(Integer.parseInt(c.getNuUnidade())).numeroConta(recuperaConta(c))
					.digitoConta(recuperaDigito(c)).numeroOperacao(Integer.parseInt(c.getNuProduto()))
					.situacao(verificaSituacao(c.getIcSituacao()))
					.dataAbertura(c.getDtInicio().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
					.fimPeriodo(recuperaDataFim(c.getDtFim()))
					.nomeResponsavel(recuperaNomeResponsavel(c.getNoRdzdoContrato())).build();
			solicitacoes.add(solicitacao);
		});
		return solicitacoes;
	}

	private String recuperaNomeResponsavel(String nome) {
		if (nome != null) {
			return nome.trim();
		}
		return null;
	}

	private LocalDate recuperaDataFim(Date data) {
		if (data != null) {
			return data.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		}
		return null;
	}

	private String recuperaConta(ContratosDTO contrato) {
		String sistema = "SIDEC";
		if (contrato.getSgSistema().trim().equals(sistema)) {
			return contrato.getCoIdentificacao().substring(7, 15);
		} else {
			return contrato.getCoIdentificacao().substring(3, 12);
		}
	}

	private Integer recuperaDigito(ContratosDTO contrato) {
		String sistema = "SIDEC";
		if (contrato.getSgSistema().trim().equals(sistema)) {
			return Integer.parseInt(String.valueOf(contrato.getCoIdentificacao().charAt(15)));
		} else {
			return Integer.parseInt(String.valueOf(contrato.getCoIdentificacao().charAt(12)));
		}
	}

	private String verificaSituacao(String situacao) {
		return situacao != null ? SituacaoContaEnum.getEnumByName(Integer.parseInt(situacao)) : null;
	}

	private HttpHeaders montaHeaders(String token, String apiKey) {
		HttpHeaders createHeaders = new HttpHeaders();
		createHeaders.add("Authorization", token);
		createHeaders.add("apikey", apiKey);
		return createHeaders;
	}

	public List<SituacaoSolicitacao> buscaSituacoes(Boolean isPendente, Boolean isAfastamento) {
		List<SituacaoSolicitacao> lista = situacaoSolicitacaoRepository.findAll();
		if (isPendente) {
			return lista.stream().filter(e -> {
				boolean t1 = e.getNomeSituacao().equals(SituacaoSolicitacaoEnum.ENVIADO_PARA_APROVACAO.getLabel());
				boolean t2 = e.getNomeSituacao().equals(SituacaoSolicitacaoEnum.RECUSADO.getLabel());
				return t1 || t2;
			}).collect(Collectors.toList());
		} else {
			if (isAfastamento) {
				return lista.stream().filter(e -> {
					boolean t1 = e.getNomeSituacao().equals(SituacaoSolicitacaoEnum.AFASTAMENTO_EM_CRIACAO.getLabel());
					boolean t2 = e.getNomeSituacao().equals(SituacaoSolicitacaoEnum.CONFIRMADO.getLabel());
					boolean t3 = e.getNomeSituacao().equals(SituacaoSolicitacaoEnum.GERADO.getLabel());
					boolean t4 = e.getNomeSituacao().equals(SituacaoSolicitacaoEnum.TRANSMITIDO.getLabel());
					boolean t5 = e.getNomeSituacao().equals(SituacaoSolicitacaoEnum.REJEITADO.getLabel());
					return t1 || t2 || t3 || t4 || t5;
				}).collect(Collectors.toList());
			} else {
				return lista.stream().filter(e -> {
					boolean t1 = e.getNomeSituacao().equals(SituacaoSolicitacaoEnum.EM_CRIACAO.getLabel());
					boolean t2 = e.getNomeSituacao().equals(SituacaoSolicitacaoEnum.ENVIADO_PARA_APROVACAO.getLabel());
					boolean t3 = e.getNomeSituacao().equals(SituacaoSolicitacaoEnum.CONFIRMADO.getLabel());
					boolean t4 = e.getNomeSituacao().equals(SituacaoSolicitacaoEnum.RECUSADO.getLabel());
					return t1 || t2 || t3 || t4;
				}).collect(Collectors.toList());
			}
		}
	}

	@Transactional
	public Solicitacao salvarSolicitacao(SolicitacaoDTO dto, String matricula, String hostname)
			throws NegocioException {

		List<Oficio> oficios = montaOficio(dto);
		// Caso seja rascunho
		if (!dto.getRascunho()) {
			validaSolicitacao(dto);
		}

		removeIdsContas(dto);
		Solicitacao solicitacao = Solicitacao.builder().contas(dto.getContas()).dtHoraCadastro(LocalDateTime.now())
				.matricula(matricula).rascunho(dto.getRascunho()).tipoSolicitacao(dto.getTipoSolicitacao())
				.unidadeSolicitante(dto.getNumeroUnidade()).oficios(oficios).situacaoSolicitacao(defineSituacao(dto))
				.isPreAprovado(dto.getAfastamento()).codigoSolicitacao(dto.getCodigoSolicitacao()).build();

		// Alteração
		if (dto.getId() != null) {
			solicitacaoContaRepository.deleteAll(dto.getContas());
			solicitacao.setId(dto.getId());
		}

		solicitacao.getOficios().forEach(o -> o.setSolicitacao(solicitacao));
		if (solicitacao.getCodigoSolicitacao() == null) {
			solicitacao.setCodigoSolicitacao(gerarCodigoSolicitacao(dto.getTipoSolicitacao()));
		}
		Solicitacao solicitacaoSalva = addOrUpdate(solicitacao);
		// Cria auditoria para a solicitação que não é um rascunho e não é pré-aprovada
		if (!solicitacao.getRascunho() && !solicitacao.getIsPreAprovado()) {
			criarAuditoria(solicitacao.getTipoSolicitacao(), matricula, hostname, solicitacao.getCodigoSolicitacao(),
					false);
		}
		return solicitacaoSalva;
	}

	private CodigoSolicitacao gerarCodigoSolicitacao(TipoSolicitacaoEnum tipoSolicitacao) {
		if (tipoSolicitacao.getNome().equals(ELEITORAL)) {
			Integer numero = crepo.gerarNumeroCodigoSolicitacao(tipoSolicitacao.getNome());
			return codigoSolicitacaoService.save(
					CodigoSolicitacao.builder().prefixo("EL").numero(numero).codigo(gerarCodigo("EL", numero)).build());
		} else {
			Integer numero = crepo.gerarNumeroCodigoSolicitacao(tipoSolicitacao.getNome());
			return codigoSolicitacaoService.save(
					CodigoSolicitacao.builder().prefixo("GE").numero(numero).codigo(gerarCodigo("GE", numero)).build());
		}
	}

	private String gerarCodigo(String prefixo, Integer numero) {
		return prefixo + String.format("%06d", numero);
	}

	private void criarAuditoria(TipoSolicitacaoEnum solicitacaoEnum, String matricula, String hostname,
			CodigoSolicitacao codigoSolicitacao, Boolean isAnalise) {
		if (isAnalise) {
			if (solicitacaoEnum.getNome().equals(ELEITORAL)) {
				auditoria.registrarAuditoria("Análise de solicitação - Contas eleitorais",
						TipoEventoAuditoriaEnum.ANALISE, matricula, hostname, codigoSolicitacao);
			} else {
				auditoria.registrarAuditoria("Análise de solicitação - Contas gerais", TipoEventoAuditoriaEnum.ANALISE,
						matricula, hostname, codigoSolicitacao);
			}
		} else {
			if (solicitacaoEnum.getNome().equals(ELEITORAL)) {
				auditoria.registrarAuditoria("Solicitação de Afastamento de sigilo - Contas eleitorais",
						TipoEventoAuditoriaEnum.ENVIO_PARA_ANALISE, matricula, hostname, codigoSolicitacao);
			} else {
				auditoria.registrarAuditoria("Solicitação de Afastamento de sigilo - Contas gerais",
						TipoEventoAuditoriaEnum.ENVIO_PARA_ANALISE, matricula, hostname, codigoSolicitacao);
			}
		}
	}

	@Transactional
	public Solicitacao salvaSolicitacaoPreAprovado(SolicitacaoDTO dto, String matricula, String hostname)
			throws NegocioException {
		Solicitacao solicitacao = salvarSolicitacao(dto, matricula, hostname);
		if (dto.getRascunho()) {
			gerarAuditoriaAfastamento(dto.getId(), solicitacao.getTipoSolicitacao(), matricula, hostname,
					solicitacao.getCodigoSolicitacao());
			return solicitacao;
		}
		solicitacao.setUnidadeResponsavel(dto.getNumeroUnidade());
		solicitacao = aprovarSolicitacao(solicitacao, matricula, hostname);
		gerarAuditoriaAfastamento(dto.getId(), solicitacao.getTipoSolicitacao(), matricula, hostname,
				solicitacao.getCodigoSolicitacao());
		return solicitacao;
	}

	private void gerarAuditoriaAfastamento(Integer id, TipoSolicitacaoEnum tipoSolicitacao, String matricula,
			String hostname, CodigoSolicitacao codigoSolicitacao) {
		if (id != null) {
			criarAuditoriaAfastamento(tipoSolicitacao, matricula, hostname, codigoSolicitacao, false, false);
		} else {
			criarAuditoriaAfastamento(tipoSolicitacao, matricula, hostname, codigoSolicitacao, false, true);
		}
	}

	private void criarAuditoriaAfastamento(TipoSolicitacaoEnum solicitacaoEnum, String matricula, String hostname,
			CodigoSolicitacao codigoSolicitacao, Boolean isExclusao, Boolean isNovo) {
		String funcionalidadeEleitoral = "Afastamento de sigilo - Contas eleitorais";
		String funcionalidadeGeral = "Afastamento de sigilo - Contas gerais";
		if (isExclusao) {
			if (solicitacaoEnum.getNome().equals(ELEITORAL)) {
				auditoria.registrarAuditoria(funcionalidadeEleitoral, TipoEventoAuditoriaEnum.EXCLUSAO, matricula,
						hostname, codigoSolicitacao);
			} else {
				auditoria.registrarAuditoria(funcionalidadeGeral, TipoEventoAuditoriaEnum.EXCLUSAO, matricula, hostname,
						codigoSolicitacao);
			}
		} else {
			if (isNovo) {
				if (solicitacaoEnum.getNome().equals(ELEITORAL)) {
					auditoria.registrarAuditoria(funcionalidadeEleitoral, TipoEventoAuditoriaEnum.INSERCAO, matricula,
							hostname, codigoSolicitacao);
				} else {
					auditoria.registrarAuditoria(funcionalidadeGeral, TipoEventoAuditoriaEnum.INSERCAO, matricula,
							hostname, codigoSolicitacao);
				}
			} else {
				if (solicitacaoEnum.getNome().equals(ELEITORAL)) {
					auditoria.registrarAuditoria(funcionalidadeEleitoral, TipoEventoAuditoriaEnum.ALTERACAO, matricula,
							hostname, codigoSolicitacao);
				} else {
					auditoria.registrarAuditoria(funcionalidadeGeral, TipoEventoAuditoriaEnum.ALTERACAO, matricula,
							hostname, codigoSolicitacao);
				}
			}
		}
	}

	@Transactional
	public void excluirAfastamento(Solicitacao solicitacao, String matricula, String hostname) {
		TipoSolicitacaoEnum tipoSolicitacao = solicitacao.getTipoSolicitacao();
		CodigoSolicitacao codigoSolicitacao = solicitacao.getCodigoSolicitacao();
		delete(solicitacao.getId());
		criarAuditoriaAfastamento(tipoSolicitacao, matricula, hostname, codigoSolicitacao, true, false);
	}

	private SituacaoSolicitacao defineSituacao(SolicitacaoDTO dto) {
		if (dto.getRascunho()) {
			return dto.getAfastamento()
					? situacaoSolicitacaoRepository.getByNomeSituacao(SituacaoSolicitacaoEnum.AFASTAMENTO_EM_CRIACAO)
					: situacaoSolicitacaoRepository.getByNomeSituacao(SituacaoSolicitacaoEnum.EM_CRIACAO);
		} else {
			return dto.getAfastamento()
					? situacaoSolicitacaoRepository.getByNomeSituacao(SituacaoSolicitacaoEnum.CONFIRMADO)
					: situacaoSolicitacaoRepository.getByNomeSituacao(SituacaoSolicitacaoEnum.ENVIADO_PARA_APROVACAO);
		}
	}

	public Page<SolicitacaoDTO> consultaSolicitacaoPaginadoComFiltro(Map<String, String[]> filter)
			throws NegocioException {
		Pageable pageable = getPageRequest(filter);
		SolicitacaoDTO dto = montarDTO(filter);
		if (dto.getTipoSolicitacao() == null)
			throw new NegocioException("tipo-solicitacao-null");
		PageImpl<Solicitacao> solicitacoes = crepo.buscaSolicitacoesPaginado(pageable, dto);
		removeOficioConsulta(solicitacoes);
		return inserirDadosSolicitacao(solicitacoes);
	}

	private PageImpl<SolicitacaoDTO> inserirDadosSolicitacao(PageImpl<Solicitacao> solicitacoes) {
		List<SolicitacaoDTO> listDTO = new ArrayList<>();
		List<Solicitacao> list = solicitacoes.getContent();
		ModelMapper modelMapper = new ModelMapper();
		list.forEach(solicitacao -> {
			SolicitacaoDTO solicitacaoDTO = modelMapper.map(solicitacao, SolicitacaoDTO.class);
			List<AgendamentoETL> temp = agendamentoETLService.buscaAgendamentoPorSolicitacaoAll(solicitacao.getId());
			if (!temp.isEmpty()) {
				AgendamentoETL ag = temp.get(temp.size() - 1);
				if (ag.getDtHrProcessamento() != null) {
					if (verificaSituacaoSolicitacao(solicitacaoDTO.getSituacaoSolicitacao().getNomeSituacao())) {
						LocalDate dtProcessamento = ag.getDtHrProcessamento().toInstant().atZone(ZoneId.systemDefault())
								.toLocalDate();
						solicitacaoDTO.setSituacaoArquivo(verificaSituacaoArquivo(dtProcessamento));
					}
				} else {
					solicitacaoDTO.setSituacaoArquivo(SituacaoArquivoEnum.FILA.getNome());
				}
				solicitacaoDTO.setCodigo(temp.get(0).getCodigo());
			}
			try {
				solicitacaoDTO.setUnidadeDTO(siicoService.consultarUnidade(solicitacao.getUnidadeSolicitante()));
			} catch (NegocioException e) {
				Log.info(getClass(), "ERRO AO RECUPERAR A UNIDADE NA BASE DO SIICO", e);
			}
			listDTO.add(solicitacaoDTO);
		});
		return new PageImpl<>(listDTO, solicitacoes.getPageable(), solicitacoes.getTotalElements());
	}

	private boolean verificaSituacaoSolicitacao(String situacao) {
		return situacao.equals(SituacaoSolicitacaoEnum.GERADO.getLabel())
				|| situacao.equals(SituacaoSolicitacaoEnum.TRANSMITIDO.getLabel())
				|| situacao.equals(SituacaoSolicitacaoEnum.REJEITADO.getLabel());
	}

	private String verificaSituacaoArquivo(LocalDate dtProcessamento) {
		if (validaExistenciaArquivo(dtProcessamento)) {
			return SituacaoArquivoEnum.DELETADO.getNome();
		} else {
			return SituacaoArquivoEnum.DOWNLOAD.getNome();
		}
	}

	private Boolean validaExistenciaArquivo(LocalDate dtProcessamento) {
		return getDataValidaArquivos().isAfter(dtProcessamento);
	}

	private LocalDate getDataValidaArquivos() {
		return LocalDate.now().minusDays(30);
	}

	private void removeOficioConsulta(PageImpl<Solicitacao> solicitacoes) {
		for (Solicitacao s : solicitacoes.getContent()) {
			s.setOficios(null);
		}
	}

	private SolicitacaoDTO montarDTO(Map<String, String[]> filter) throws NegocioException {

		LocalDateTime dataInicio = null;
		LocalDateTime dataFim = null;
		TipoSolicitacaoEnum t = null;
		ArrayList<SituacaoSolicitacao> situacoes = new ArrayList<>();

		String matricula = RequisicaoUtil.extrairParametro(filter, MATRICULA);
		String codigo = RequisicaoUtil.extrairParametro(filter, CODIGO);
		Boolean isAfastamento = Boolean.parseBoolean(RequisicaoUtil.extrairParametro(filter, AFASTAMENTO));
		Boolean isPendente = Boolean.parseBoolean(RequisicaoUtil.extrairParametro(filter, PENDENTE));
		String numeroUnidade = RequisicaoUtil.extrairParametro(filter, UNIDADE);
		String tipoSolicitacao = RequisicaoUtil.extrairParametro(filter, TIPO_SOLICITACAO);
		String[] situacaoSolicitacaoArray = RequisicaoUtil.extrairParametros(filter, SITUACAO_SOLICITACAO_ARRAY);
		String situacaoSolicitacao = RequisicaoUtil.extrairParametro(filter, SITUACAO_SOLICITACAO);

		String cpfCNPJ = RequisicaoUtil.extrairParametro(filter, CPF_CNPJ);
		String dtInicio = RequisicaoUtil.extrairParametro(filter, DT_INICIO);
		String dtFim = RequisicaoUtil.extrairParametro(filter, DT_FIM);

		if (dtInicio != null) {
			Instant instant = Instant.parse(dtInicio);
			dataInicio = LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.getId()));
		}

		if (tipoSolicitacao != null) {
			t = TipoSolicitacaoEnum.valueOf(tipoSolicitacao);
		}
		// 1. Caso venha um array de params
		if (situacaoSolicitacaoArray != null) {

			for (String s : situacaoSolicitacaoArray) {
				situacoes.add(SituacaoSolicitacao.builder().id(Integer.parseInt(s)).build());
			}
		}
		// 2. Caso venha 1 param
		if (situacaoSolicitacao != null) {
			situacoes.add(SituacaoSolicitacao.builder().id(Integer.parseInt(situacaoSolicitacao)).build());
		}
		if (dtFim != null) {
			Instant instant = Instant.parse(dtFim);
			dataFim = LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.getId()));
			if (dtInicio != null && dataInicio.isAfter(dataFim))
				throw new PeriodoDatasException();
		}

		return SolicitacaoDTO.builder().matricula(matricula).codigo(codigo).afastamento(isAfastamento)
				.pendente(isPendente).numeroUnidade(!isAfastamento ? Integer.parseInt(numeroUnidade) : null)
				.tipoSolicitacao(t).situacoes(situacoes).dtInicio(dataInicio).dtFim(dataFim).cpfCNPJ(cpfCNPJ).build();

	}

	private void validaSolicitacao(SolicitacaoDTO solicitacaoDTO) throws NegocioException {
		if (solicitacaoDTO.getContas() == null)
			throw new NegocioException("contas-not-found");

		if (solicitacaoDTO.getOficio() == null)
			throw new NegocioException("oficio-not-found");

		for (SolicitacaoConta sc : solicitacaoDTO.getContas()) {
			if (sc.getInicioPeriodo().isAfter(sc.getFimPeriodo())) {
				throw new NegocioException("conta: " + sc.getNumeroConta() + " invalid-period");
			}
		}
	}

	private void removeIdsContas(SolicitacaoDTO dto) {
		for (SolicitacaoConta c : dto.getContas()) {
			c.setId(null);
		}
	}

	private List<Oficio> montaOficio(SolicitacaoDTO dto) {
		List<Oficio> oficios = new ArrayList<>();

		if (dto.getOficio() != null && dto.getNomeArquivo() != null) {
			oficios.add(Oficio.builder().base64arquivo(dto.getOficio()).nomeArquivo(dto.getNomeArquivo()).build());
		}
		return oficios;
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	public Solicitacao aprovarSolicitacao(Solicitacao solicitacao, String matricula, String hostname)
			throws NegocioException {
		solicitacao = analisaSolicitacao(solicitacao, matricula, SituacaoSolicitacaoEnum.CONFIRMADO.name());
		Solicitacao solicitacaoSalva = addOrUpdate(solicitacao);
		afastamentoService.criaAfastamento(matricula, solicitacao.getUnidadeResponsavel(), solicitacaoSalva);
		agendamentoETLService.insereParametrosGeracaoSobDemanda(matricula, hostname, solicitacaoSalva,
				solicitacaoSalva.getCodigoSolicitacao());
		if (!solicitacaoSalva.getIsPreAprovado()) {
			criarAuditoria(solicitacaoSalva.getTipoSolicitacao(), matricula, hostname,
					solicitacaoSalva.getCodigoSolicitacao(), true);
		}
		email.sendEmailAnaliseSolicitacao(solicitacaoSalva.getMatricula(), solicitacaoSalva.getCodigoSolicitacao(),
				SituacaoSolicitacaoEnum.CONFIRMADO.name());
		solicitacaoSalva.setOficios(null);
		return solicitacaoSalva;
	}

	public Solicitacao rejeitarSolicitacao(Solicitacao solicitacao, String matricula, String hostname)
			throws NegocioException {
		solicitacao = analisaSolicitacao(solicitacao, matricula, SituacaoSolicitacaoEnum.RECUSADO.name());

		if (!solicitacao.getIsPreAprovado()) {
			criarAuditoria(solicitacao.getTipoSolicitacao(), matricula, hostname, solicitacao.getCodigoSolicitacao(),
					true);
		}
		Solicitacao solicitacaoSalva = addOrUpdate(solicitacao);
		email.sendEmailAnaliseSolicitacao(solicitacaoSalva.getMatricula(), solicitacaoSalva.getCodigoSolicitacao(),
				SituacaoSolicitacaoEnum.RECUSADO.name());
		solicitacaoSalva.setOficios(null);
		return solicitacaoSalva;
	}

	private Solicitacao analisaSolicitacao(Solicitacao solicitacao, String matricula, String situacao)
			throws NegocioException {
		Optional<Solicitacao> optEntity = findOne(solicitacao.getId());
		if (!optEntity.isPresent())
			throw new NegocioException("id-not-found");
		Solicitacao r = optEntity.get();
		r.setDtHoraAnalise(LocalDateTime.now());
		r.setMatriculaResponsavel(matricula);
		r.setUnidadeResponsavel(solicitacao.getUnidadeResponsavel());
		r.setMotivoRejeicao(solicitacao.getMotivoRejeicao());
		r.setSituacaoSolicitacao(
				situacaoSolicitacaoRepository.getByNomeSituacao(SituacaoSolicitacaoEnum.valueOf(situacao)));
		return r;
	}

}
