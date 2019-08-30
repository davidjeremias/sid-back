package br.com.caixa.sidce.domain.service;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import br.com.caixa.sidce.domain.model.AgendamentoETL;
import br.com.caixa.sidce.domain.model.Arquivo;
import br.com.caixa.sidce.domain.model.CodigoSolicitacao;
import br.com.caixa.sidce.domain.model.SituacaoSigilo;
import br.com.caixa.sidce.domain.model.SituacaoSolicitacao;
import br.com.caixa.sidce.domain.model.Solicitacao;
import br.com.caixa.sidce.domain.model.enums.EventoEnum;
import br.com.caixa.sidce.domain.model.enums.ParametroEnum;
import br.com.caixa.sidce.domain.model.enums.SituacaoEnum;
import br.com.caixa.sidce.domain.model.enums.SituacaoSolicitacaoEnum;
import br.com.caixa.sidce.infraestructure.persistence.jpa.AgendamentoETLCustomRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.AgendamentoETLRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.ArquivoRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.SituacaoSigiloRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.SituacaoSolicitacaoRepository;
import br.com.caixa.sidce.interfaces.util.FileStorage;
import br.com.caixa.sidce.interfaces.web.dto.RetornoSimbaDTO;
import br.com.caixa.sidce.util.infraestructure.auditoria.AuditoriaAspecto;
import br.com.caixa.sidce.util.infraestructure.auditoria.TipoEventoAuditoriaEnum;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;
import br.com.caixa.sidce.util.infraestructure.service.RestFullService;

@Service
public class AgendamentoETLService extends RestFullService<AgendamentoETL, Integer> {

	private static final String ARQUIVOS_TSE_ZIP = "Arquivos_TSE.zip";
	private static final String INICIO_PATTERN = "TSE-00";
	private static final String DESCRICAO_ARQUIVO = "Arquivo gerado como resposta do SIMBA no upload dos 5 arquivos";
	private static final String PARTIDO = "partido";
	private static final String CANDIDATO = "candidato";
	private static final String UPLOAD_ERROR = "upload-erro";
	public static final String SITUACAO = "situacao";
	public static final String CODIGO = "codigo";
	public static final String ID = "id";
	private static final String ZIP_EXTENSION = ".zip";
	public static final String TIPO = "tipo";
	public static final String STRING_VAZIA = "";

	@Autowired
	private AgendamentoETLCustomRepository crepo;

	@Autowired
	private AgendamentoETLRepository agendamentoETLRepository;

	@Autowired
	private ArquivoRepository arquivoRepository;

	@Autowired
	private FileStorage fileStorage;

	@Autowired
	private SituacaoSigiloRepository situacaoSigiloRepository;

	@Autowired
	private SituacaoSolicitacaoRepository situacaoSolicitacaoRepository;

	@Autowired
	private ParametroService parametroService;

	@Autowired
	private AuditoriaAspecto auditoria;

	@Autowired
	private CodigoSolicitacaoService codigoSolicitacaoService;
	
	@Autowired
	private FactoryEmail email;

	@Autowired
	AgendamentoETLService(AgendamentoETLRepository repository) {
		super(repository);
	}

	/**
	 * Prepara um novo processo de ETL
	 * 
	 * @param receivedFiles - 2 arquivos necessários com inputs de CNPJ
	 * @param matricula     - Usuário solicitante
	 * @param hostname      - Endereço lógico do usuário solicitante
	 * @throws NegocioException
	 * @throws IOException
	 */
	@Transactional
	public void uploadArquivosPartidoCandidato(MultipartFile[] receivedFiles, String matricula, String hostname)
			throws NegocioException {

		String eventoUpload = buscaEventoUpload();
		File arquivoTSE = new File(parametroService.buscarPorChave(ParametroEnum.ETL_DIR_OUTPUT));
		arquivoTSE = new File(arquivoTSE.toPath().resolve(ARQUIVOS_TSE_ZIP).toString() + ZIP_EXTENSION);
		if (!isETLDisponivel()) {
			throw new NegocioException("etl-already-busy");
		}
		if (receivedFiles.length == 0) {
			throw new NegocioException("file-not-be-null");
		}

		int periodoAtual = Integer.parseInt(patternPeriodoAtual(""));

		try {
			// Salva os arquivos na pasta de input do ETL
			for (MultipartFile receivedFile : receivedFiles) {
				salvarArquivoETL(receivedFile);
			}

			// Limpa o diretório de outputs do ETL
			FileUtils.deleteQuietly(arquivoTSE);

			// Insere os parametros necessários para a execução do ETL
			AgendamentoETL retorno = insereTabelaParametro(matricula, periodoAtual, receivedFiles, hostname,
					eventoUpload, null);
			if (retorno != null) {
				auditoria.registrarAuditoria("Geração de Solicitação de afastamento via arquivo TSE",
						TipoEventoAuditoriaEnum.SOLICITA_GERACAO, matricula, hostname, retorno.getCodigoSolicitacao());
			}

		} catch (NegocioException e) {
			TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
			throw new NegocioException(e);
		}

	}

	/**
	 * Verifica se o ultimo processo de quebra de sigilo foi processado
	 * 
	 * @return
	 */
	public Boolean isProntaUltimaGeracao() {
		List<AgendamentoETL> list = crepo.ultimoUploadGeracaoCadastrado();
		if (list.isEmpty())
			return true;
		AgendamentoETL ultima = list.get(0);
		return ultima.getSituacao().getId() != SituacaoEnum.INICIADO.getId();
	}

	/**
	 * Verifica se o ultimo processo GERADO(dtHrProcessamento preenchida) foi
	 * enviado para o SIMBA
	 * 
	 * @return
	 * @throws NegocioException
	 */
	public Boolean isEnviadaSimbaUltimaGeracao() {
		List<AgendamentoETL> list = crepo.ultimoUploadGeracaoCadastradoGerado();
		if (list.isEmpty())
			return true;
		AgendamentoETL ultima = list.get(0);
		return ultima.getSituacao().getId() != SituacaoEnum.GERADO.getId();
	}

	public boolean isAgendamentoGeracaoPedente() {
		List<AgendamentoETL> list = crepo.ultimoUploadGeracaoCadastrado();
		if (list.isEmpty())
			return false;
		else
			return (list.get(0).getDtHrProcessamento() == null);
	}

	public boolean isETLDisponivel() {
		boolean r = true;
		if (!isEnviadaSimbaUltimaGeracao())
			r = false;
		if (isAgendamentoGeracaoPedente())
			r = false;
		return r;
	}

	/**
	 * Insere as informações necessárias para que o ETL faça o processamento
	 * 
	 * @param matricula - matricula do solicitante
	 * @param periodo   - Período da informação
	 * @param arquivos  - Arquivos com os CNPJs de input
	 * @param hostname  - Endereço lógico do usuário
	 * @param evento    - Upload e geração, Download.. etc
	 * @param codigo    - Código caso seja uma regeração
	 * @throws NegocioException
	 */
	public AgendamentoETL insereTabelaParametro(String matricula, int periodo, MultipartFile[] arquivos,
			String hostname, String evento, String codigo) throws NegocioException {
		if (evento == EventoEnum.UPLOAD.getNome() && arquivos.length == 0) {
			throw new NegocioException(UPLOAD_ERROR);
		}

		Map<String, String> mapaNomes = separaNomeArquivos(arquivos);
		Integer co = crepo.gerarNumeroCodigoAfastamento();
		String codigoAfastamento = geraCodigoAfastamento("TSE", co);
		CodigoSolicitacao codigoSolicitacao = codigoSolicitacaoService
				.save(CodigoSolicitacao.builder().prefixo("TSE").numero(co).codigo(codigoAfastamento).build());
		AgendamentoETL agendamento = AgendamentoETL.builder()
				.matricula(matricula)
				.periodo(periodo)
				.evento(evento)
				.dtHoraCadastro(new Date())
				.nomeArquivoCandidato(mapaNomes.get(CANDIDATO))
				.nomeArquivoPartido(mapaNomes.get(PARTIDO))
				.hostname(hostname)
				.codigo(UUID.randomUUID().toString().toUpperCase())
				.situacao(SituacaoSigilo.builder().id(SituacaoEnum.INICIADO.getId()).build())
				.codigoSolicitacao(codigoSolicitacao).build();
		return save(agendamento);
	}

	private String geraCodigoAfastamento(String prefixo, Integer codigo) {
		return prefixo + String.format("%06d", codigo);
	}

	/**
	 * Insere as informações necessárias para que o ETL faça o processamento
	 * 
	 * @param matricula - matricula do solicitante - Automatico no caso da rotina
	 * @param periodo   - Período da informação - ultimo periodo disponivol no caso
	 *                  da rotina
	 * @param arquivos  - Arquivos com os CNPJs de input
	 * @param hostname  - Endereço lógico do servidor executor da rotina
	 * @param evento    - Upload e geração, Download.. etc
	 * @param codigo    - Código caso seja uma regeração
	 * @throws NegocioException
	 * @throws UnknownHostException
	 */
	public AgendamentoETL insereParametrosGeracaoRotina() throws UnknownHostException {
		InetAddress inetAddress = InetAddress.getLocalHost();
		String eventoUpload = buscaEventoUpload();
		Integer co = crepo.gerarNumeroCodigoAfastamento();
		String codigoAfastamento = geraCodigoAfastamento("TSE", co);
		CodigoSolicitacao codigoSolicitacao = codigoSolicitacaoService
				.save(CodigoSolicitacao.builder().prefixo("TSE").numero(co).codigo(codigoAfastamento).build());
		AgendamentoETL agendamento = AgendamentoETL.builder().matricula("Automatico")
				.periodo(Integer.parseInt(patternPeriodoAtual("")))
				.evento(eventoUpload)
				.dtHoraCadastro(new Date())
				.codigo(UUID.randomUUID().toString().toUpperCase())
				.nomeArquivoCandidato("cnpj_candidatos_atual.txt")
				.nomeArquivoPartido("cnpj_partido_atual.txt")
				.hostname(inetAddress.getHostName())
				.situacao(SituacaoSigilo.builder().id(SituacaoEnum.INICIADO.getId()).build())
				.codigoSolicitacao(codigoSolicitacao).build();
		save(agendamento);
		
		auditoria.registrarAuditoria("Geração de Solicitação de afastamento via arquivo TSE(Rotina) ",
				TipoEventoAuditoriaEnum.SOLICITA_GERACAO, "Automatico", inetAddress.getHostName(), agendamento.getCodigoSolicitacao());
		return agendamento;
	}

	/**
	 * Insere as informações necessárias para que o ETL faça o processamento
	 * 
	 * @param matricula   - matricula do responsável
	 * @param arquivos    - Arquivos com os CNPJs de input
	 * @param hostname    - Endereço lógico do resposável
	 * @param evento      - Upload e geração, Download.. etc
	 * @param codigo      - Código da geração
	 * @param solicitacao - Solicitação que esse agendamento está relacionado
	 * @throws NegocioException
	 * @throws UnknownHostException
	 */
	public AgendamentoETL insereParametrosGeracaoSobDemanda(String matricula, String hostname,
			Solicitacao solicitacao, CodigoSolicitacao codigoSolicitacao) {
		String codigo = UUID.randomUUID().toString().toUpperCase();
		AgendamentoETL agendamento = AgendamentoETL.builder().matricula(matricula).periodo(null)
				.evento(EventoEnum.SOB_DEMANDA.getNome()).dtHoraCadastro(new Date()).nomeArquivoCandidato(null)
				.nomeArquivoPartido(null).hostname(hostname)
				.situacao(SituacaoSigilo.builder().id(SituacaoEnum.INICIADO.getId()).build()).codigo(codigo)
				.solicitacao(solicitacao).codigoSolicitacao(codigoSolicitacao).build();

		return save(agendamento);
	}

	/**
	 * Verifica os arquivos de input e os categoriza em candidato e partido
	 * 
	 * @param arquivos
	 * @return
	 */
	public Map<String, String> separaNomeArquivos(MultipartFile[] arquivos) {

		Map<String, String> mapaNomes = new HashMap<>();

		if (arquivos.length > 0) {
			for (MultipartFile arquivo : arquivos) {
				if (FilenameUtils.getName(arquivo.getOriginalFilename()).contains(CANDIDATO))
					mapaNomes.put(CANDIDATO, FilenameUtils.getName(arquivo.getOriginalFilename()));
				else
					mapaNomes.put(PARTIDO, FilenameUtils.getName(arquivo.getOriginalFilename()));
			}
		} else {
			mapaNomes.put(CANDIDATO, "");
			mapaNomes.put(PARTIDO, "");
		}

		return mapaNomes;
	}

	/**
	 * Efetivamente salva os arquivos de input em disco
	 * 
	 * @param receivedFile
	 * @return
	 * @throws NegocioException
	 */
	public String salvarArquivoETL(MultipartFile receivedFile) throws NegocioException {
		String fileName = StringUtils.cleanPath(receivedFile.getOriginalFilename());

		// Salva o arquivo na pasta do ETL
		Path etlInput = Paths.get(parametroService.buscarPorChave(ParametroEnum.ETL_DIR_INPUT));

		uploadArquivo(etlInput);
		fileStorage.salvarArquivo(receivedFile, etlInput);

		return fileName;
	}

	protected void uploadArquivo(String path) throws NegocioException {
		uploadArquivo(Paths.get(path));
	}

	protected void uploadArquivo(Path path) throws NegocioException {
		try {
			criaArquivo(path);
		} catch (IOException e) {
			throw new NegocioException(UPLOAD_ERROR, e);
		}
	}

	protected void criaArquivo(Path path) throws IOException {
		Files.createDirectories(path);
	}

	protected File createFile(String path) {
		return new File(path);
	}

	protected File createFile() {
		return createFile(parametroService.buscarPorChave(ParametroEnum.ETL_DIR_OUTPUT));
	}

	/**
	 * Carrega os arquivos de saída da pasta de output do ETL
	 * 
	 * @return
	 * @throws NegocioException
	 */
	public File[] carregarOutputETL() throws NegocioException {

		File saidaETL = createFile();
		uploadArquivo(parametroService.buscarPorChave(ParametroEnum.ETL_DIR_OUTPUT));

		String pattern = patternPeriodoAtual(INICIO_PATTERN);
		Stream<File> arquivos2 = Arrays.stream(saidaETL.listFiles()).filter(e -> e.getName().endsWith(".txt"))
				.filter(e -> e.getName().contains(pattern));

		return arquivos2.toArray(File[]::new);
	}

	/**
	 * Traz o pattern autal de nome de arquivos
	 * 
	 * @param patternInicio
	 * @return
	 */
	public String patternPeriodoAtual(String patternInicio) {
		Optional<String> optPattern = Optional.of(patternInicio);
		LocalDate agora = LocalDate.now().minusMonths(1);

		StringBuilder retorno = new StringBuilder();
		retorno.append(patternInicio);

		retorno.append(agora.getYear());

		if (optPattern.get().equals(STRING_VAZIA))
			retorno.append(org.apache.commons.lang.StringUtils.leftPad(String.valueOf(agora.getMonthValue()), 2, "0"));

		return retorno.toString();
	}

	private Stream<String> streamOfNullable(String[] list) {
		return Optional.ofNullable(list).map(Stream::of).orElseGet(Stream::empty);
	}

	public AgendamentoETL inserirArquivoSimba(MultipartFile file, String matricula, Map<String, String[]> params)
			throws IOException, NegocioException {
		Optional<String> optIdenti = streamOfNullable(params.get(ID)).findFirst();
		Optional<String> optTipo = streamOfNullable(params.get(TIPO)).findFirst();
		
		AgendamentoETL agenda = buscaAgendamentoETL(optIdenti, optTipo);
		String[] strArray = new String[] { agenda.getCodigo() };

		Optional<String> optCodigo = streamOfNullable(strArray).findFirst();
		Optional<String> optSituac = streamOfNullable(params.get(SITUACAO)).findFirst();

		Stream<Optional<String>> stream = Arrays.asList(optCodigo, optSituac, optIdenti).stream();

		if (stream.anyMatch(e -> !e.isPresent())) {
			throw new NegocioException("codigo-situacao-file-id-cannot-be-null");
		}

		String codigo = optCodigo.get();
		Integer situacao = Integer.parseInt(optSituac.get());

		if (verificaSeExisteArquivo(codigo)) {
			throw new NegocioException("file-already-exists");
		}

		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		Arquivo arquivo = Arquivo.builder()
				.codigo(codigo)
				.nomeArquivo(fileName)
				.descricaoArquivo(DESCRICAO_ARQUIVO)
				.dtHrCadastro(new Date())
				.bytesArquivo(file.getBytes())
				.usuario(matricula)
				.agendamento(agenda).build();

		arquivoRepository.save(arquivo);
		atualizaSituacao(agenda, situacao);
		AgendamentoETL retorno = agendamentoETLRepository.save(agenda);
		enviarEmailRejeicaoSimba(retorno);
		return retorno;
	}
	
	private AgendamentoETL buscaAgendamentoETL(Optional<String> optIdenti, Optional<String> optTipo) {	
		String tipo = "solicitacao";
		if(optTipo.get().equals(tipo)) {
			return agendamentoETLRepository.buscaAgendamentoETLPorSolicitacaoPrimeiro(Integer.valueOf(optIdenti.get()));
		}else{
			Optional<AgendamentoETL> temp = repository.findById(Integer.valueOf(optIdenti.get()));
			return temp.get();
		}
	}

	private SituacaoSolicitacao buscaSituacaoSolicitacao(SituacaoSolicitacaoEnum param) {
		SituacaoSolicitacao situacaoSolicitacao = situacaoSolicitacaoRepository.getByNomeSituacao(param);
		if (param == null)
			new NegocioException("situacao-not-found");
		return situacaoSolicitacao;
	}

	public AgendamentoETL alterarArquivoSimba(MultipartFile file, String matricula, Map<String, String[]> params)
			throws NegocioException, IOException {
		Optional<String> optIdenti = streamOfNullable(params.get(ID)).findFirst();
		Optional<String> optTipo = streamOfNullable(params.get(TIPO)).findFirst();
		
		AgendamentoETL agenda = buscaAgendamentoETL(optIdenti, optTipo);
		
		String[] strArray = new String[] { agenda.getCodigo() };
		Optional<String> optCodigo = streamOfNullable(strArray).findFirst();
		Optional<String> optSituac = streamOfNullable(params.get(SITUACAO)).findFirst();

		Stream<Optional<String>> stream = Arrays.asList(optCodigo, optSituac).stream();

		if (stream.anyMatch(e -> !e.isPresent())) {
			throw new NegocioException("codigo-situacao-file-cannot-be-null");
		}

		String codigo = optCodigo.get();
		Integer situacao = Integer.valueOf(optSituac.get());

		Arquivo arquivo = buscaArquivoPorCodigo(codigo);
		arquivo.setBytesArquivo(file.getBytes());
		arquivo.setDtHrCadastro(new Date());
		arquivo.setUsuario(matricula);

		if (!file.isEmpty()) {
			String fileName = StringUtils.cleanPath(file.getOriginalFilename());
			arquivo.setNomeArquivo(fileName);
			arquivoRepository.save(arquivo);
		}

		atualizaSituacao(agenda, situacao);
		AgendamentoETL retorno = agendamentoETLRepository.save(agenda);
		enviarEmailRejeicaoSimba(retorno);
		return retorno;
	}
	
	public void enviarEmailRejeicaoSimba(AgendamentoETL retorno) throws NegocioException{
		if(retorno.getSituacao().getNomeSituacao().equals(SituacaoEnum.REJEITADO.getNome())) {
			email.sendEmailRejeicaoSimba(retorno);
		}
	}

	private void atualizaSituacao(AgendamentoETL agendamento, Integer situacao) {
		Optional<SituacaoSigilo> situacaoSigilo = buscaSituacaoSigilo(situacao);
		agendamento.setSituacao(situacaoSigilo.get());

		SituacaoSolicitacao situacaoSolicitacao = null;
		if (situacaoSigilo.get().getNomeSituacao().equals(SituacaoEnum.TRANSMITIDO.getNome())) {
			situacaoSolicitacao = buscaSituacaoSolicitacao(SituacaoSolicitacaoEnum.TRANSMITIDO);
		} else if (situacaoSigilo.get().getNomeSituacao().equals(SituacaoEnum.REJEITADO.getNome())) {
			situacaoSolicitacao = buscaSituacaoSolicitacao(SituacaoSolicitacaoEnum.REJEITADO);
		} else if (situacaoSigilo.get().getNomeSituacao().equals(SituacaoEnum.INICIADO.getNome())) {
			situacaoSolicitacao = buscaSituacaoSolicitacao(SituacaoSolicitacaoEnum.GERADO);
		}
		if(agendamento.getSolicitacao() != null) {
			agendamento.getSolicitacao().setSituacaoSolicitacao(situacaoSolicitacao);
		}
		}
			

	public void removerArquivoSimba(Integer id) throws NegocioException {
		AgendamentoETL agenda = agendamentoETLRepository.buscaAgendamentoETLPorSolicitacaoPrimeiro(id);
		String[] strArray = new String[] { agenda.getCodigo() };
		Optional<String> optCodigo = streamOfNullable(strArray).findFirst();

		if (!optCodigo.isPresent()) {
			throw new NegocioException("codigo-cannot-be-null");
		}

		atualizaSituacao(agenda, SituacaoEnum.INICIADO.getId());

		Arquivo arquivo = buscaArquivoPorCodigo(optCodigo.get());
		arquivoRepository.delete(arquivo);
	}

	public List<SituacaoSigilo> buscaSituacaoSigiloDisponiveis() {
		return situacaoSigiloRepository.findAll();
	}

	protected boolean verificaSeExisteArquivo(String codigo) {
		Example<Arquivo> example = Example.of(Arquivo.builder().codigo(codigo).build());
		return arquivoRepository.exists(example);
	}

	public Arquivo buscaArquivoPorCodigo(String codigo) throws NegocioException {
		Example<Arquivo> example = Example.of(Arquivo.builder().codigo(codigo).build());
		Optional<Arquivo> arquivo = arquivoRepository.findOne(example);
		return arquivo.orElseThrow(() -> new NegocioException("not-found"));
	}

	protected String buscaEventoUpload() {
		return EventoEnum.UPLOAD.getNome();
	}

	private Optional<SituacaoSigilo> buscaSituacaoSigilo(Integer situacao) {
		Optional<SituacaoSigilo> situacaoSigilo = situacaoSigiloRepository.findById(situacao);
		if (!situacaoSigilo.isPresent())
			new NegocioException("situacao-not-found");
		return situacaoSigilo;
	}

	public AgendamentoETL buscaDadosUltimoProcessoGerado() throws NegocioException {
		agendamentoETLRepository.findAll();
		List<AgendamentoETL> processos = crepo.ultimoUploadGeracaoCadastradoGerado();
		if (processos.isEmpty())
			throw new NegocioException("last-process-not-found");
		return processos.get(0);
	}

	public Resource carregaOutputUltimoProcesso() throws NegocioException {
		Resource zipResource = null;
		String etlOutputDir = parametroService.buscarPorChave(ParametroEnum.ETL_DIR_OUTPUT);
		AgendamentoETL ultimoProcesso = buscaDadosUltimoProcessoGerado();

		Optional<File> arquivo = downloadZip(etlOutputDir, ultimoProcesso.getCodigo());
		
		if (arquivo.isPresent()) {
			zipResource = fileStorage.carregaResourceArquivo(arquivo.get().getName(), Paths.get(etlOutputDir));
		}else {
			throw new NegocioException("ARQUIVO-NAO-ENCONTRADO");
		}
		return zipResource;
	}

	public AgendamentoETL buscaAgendamentoPorSolicitacao(Integer id) {
		return agendamentoETLRepository.buscaAgendamentoETLPorSolicitacao(id);
	}

	public RetornoSimbaDTO buscaRetornoSimba(Integer id) throws NegocioException {
		AgendamentoETL agenda = agendamentoETLRepository.buscaAgendamentoETLPorSolicitacaoPrimeiro(id);
		Arquivo arquivo = buscaArquivoPorCodigo(agenda.getCodigo());
		RetornoSimbaDTO retornoSimbaDTO = new RetornoSimbaDTO();
		retornoSimbaDTO.setAgendamentoETL(agenda);
		retornoSimbaDTO.setArquivo(arquivo);
		return retornoSimbaDTO;
	}
	
	public RetornoSimbaDTO buscaRetornoSimbaArquivos(Integer id) throws NegocioException {
		Optional<AgendamentoETL> agenda = repository.findById(id);
		Arquivo arquivo = buscaArquivoPorCodigo(agenda.get().getCodigo());
		RetornoSimbaDTO retornoSimbaDTO = new RetornoSimbaDTO();
		retornoSimbaDTO.setAgendamentoETL(agenda.get());
		retornoSimbaDTO.setArquivo(arquivo);
		return retornoSimbaDTO;
	}

	public List<AgendamentoETL> buscaAgendamentoPorSolicitacaoAll(Integer id) {
		return agendamentoETLRepository.buscaAgendamentoETLPorSolicitacaoAll(id);
	}

	public Resource downloadArquivoPorAfastamento(Integer id, Principal principal)
			throws NegocioException {
		AgendamentoETL agendamento = agendamentoETLRepository.buscaAgendamentoETLPorSolicitacao(id);
		LocalDate dtProcessamento = agendamento.getDtHrProcessamento().toInstant().atZone(ZoneId.systemDefault())
				.toLocalDate();
		Resource zipResource = null;
		// valida se existe arquivo pelo bd se não tiver gera novo afastamento
		if (validaExistenciaArquivo(dtProcessamento)) {
			AgendamentoETL novoAgendamento = new AgendamentoETL();
			novoAgendamento.setDtHoraCadastro(new Date());
			novoAgendamento.setMatricula(principal.getName());
			novoAgendamento.setHostname(agendamento.getHostname());
			novoAgendamento.setEvento(agendamento.getEvento());
			Optional<SituacaoSigilo> situacaoSigilo = situacaoSigiloRepository.findById(SituacaoEnum.INICIADO.getId());
			novoAgendamento.setSituacao(situacaoSigilo.get());
			novoAgendamento.setCodigo(agendamento.getCodigo());
			novoAgendamento.setSolicitacao(agendamento.getSolicitacao());
			save(novoAgendamento);
		} else {
			String etlOutputDir = parametroService.buscarPorChave(ParametroEnum.ETL_DIR_OUTPUT);
			Optional<File> arquivo = downloadZip(etlOutputDir, agendamento.getCodigo());
			
			if (arquivo.isPresent()) {
				zipResource = fileStorage.carregaResourceArquivo(arquivo.get().getName(), Paths.get(etlOutputDir));
			}else {
				throw new NegocioException("ARQUIVO-NAO-ENCONTRADO");
			}		
		}
		return zipResource;
	}

	private Boolean validaExistenciaArquivo(LocalDate dtProcessamento) {
		return getDataValidaArquivos().isAfter(dtProcessamento);
	}

	private LocalDate getDataValidaArquivos() {
		return LocalDate.now().minusDays(30);
	}
	
	private Optional<File> downloadZip(String outputDir, String codigo) throws NegocioException {
		File arq = new File(outputDir);
		Optional<File> arquivo = null;
		if(arq.listFiles() != null) {
			arquivo = Arrays.asList(arq.listFiles())
		    		.stream()
		    		.filter(e -> e.getName().equals(codigo+ZIP_EXTENSION))
		    		.findFirst();
		}else {
			throw new NegocioException("DIRETORIO-NAO-ENCONTRADO");
		}
		return arquivo;
	}

}
