package br.com.caixa.sidce.domain.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.caixa.sidce.domain.model.AgendamentoETL;
import br.com.caixa.sidce.domain.model.SituacaoSigilo;
import br.com.caixa.sidce.domain.model.enums.EventoEnum;
import br.com.caixa.sidce.domain.model.enums.ParametroEnum;
import br.com.caixa.sidce.domain.model.enums.SituacaoEnum;
import br.com.caixa.sidce.infraestructure.persistence.jpa.AgendamentoETLCustomRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.AgendamentoETLRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.AuditoriaProcessamentoCustomRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.BancoCustomRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.ContaCustomRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.ExtratoCustomRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.OrigemDestinoCustomRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.SituacaoSigiloRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.TitularCustomRepository;
import br.com.caixa.sidce.interfaces.util.FileStorage;
import br.com.caixa.sidce.interfaces.web.dto.VisualizacaoArquivosDTO;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;
import br.com.caixa.sidce.util.infraestructure.log.Log;
import br.com.caixa.sidce.util.infraestructure.service.RestFullService;

@Service
public class AgendamentoETLConsultaService extends RestFullService<AgendamentoETL, Integer> {

	private static final String CNPJ = "cnpj";
	private static final String NUMERO_CONTA = "numeroConta";
	private static final String NUMERO_AGENCIA = "numeroAgencia";
	public static final String DOWNLOAD = "Download";
	public static final String ZIP_EXTENSION = ".zip";
	private static final String CODIGO = "codigo";

	@Autowired
	private AgendamentoETLCustomRepository crepo;

	@Autowired
	private AgendamentoETLRepository agendamentoETLRepository;

	@Autowired
	private AuditoriaProcessamentoCustomRepository auditoriaRepository;

	@Autowired
	private FileStorage fileStorage;

	@Autowired
	private BancoCustomRepository bancoCrepo;

	@Autowired
	private ContaCustomRepository contaCrepo;

	@Autowired
	private ExtratoCustomRepository extratoCrepo;

	@Autowired
	private OrigemDestinoCustomRepository origemDestinoCrepo;

	@Autowired
	private TitularCustomRepository titularCrepo;

	@Autowired
	private ParametroService parametroService;
	
	@Autowired
	private SituacaoSigiloRepository situacaoSigiloRepository;

	public String etlReportDir;
	
	@Autowired
	public AgendamentoETLConsultaService(AgendamentoETLRepository repository) {
		super(repository);
	}

	/**
	 * Seta caminho da pasta de relatório do ETL
	 * 
	 * @param etlReportDir
	 */
	public void setEtlReportDir(String etlReportDir) {
		this.etlReportDir = etlReportDir;
	}

	public List<Integer> buscaPeriodosGerados() {
		return ((AgendamentoETLRepository) repository).buscarPeriodosDisponiveis();
	}

	/**
	 * Busca os agendamentos solicitados e processados
	 * 
	 * @return
	 */
	public List<AgendamentoETL> executadosPorAplicacao() {
		return ((AgendamentoETLRepository) repository).buscaExecutadosPorAplicacao(EventoEnum.UPLOAD.getNome());
	}

	/**
	 * Busca os agendamentos processados, com filtro e paginação
	 * 
	 * @param filter
	 * @return
	 * @throws IOException
	 */
	public PageImpl<AgendamentoETL> consultaAgendamentosComFiltro(Map<String, String[]> filter) {
		try {
			expurgaArquivos();
		} catch (NegocioException e) {
			Log.error(AgendamentoETLConsultaService.class, e);
		}
		return crepo.consultaAgendamentosComFiltro(getPageRequest(filter), filter);
	}

	/**
	 * Solicita um reprocessamento (geração zip com 7 arquivos) de uma geração
	 * passada para consulta
	 * 
	 * @param codigo
	 * @param matricula
	 * @param hostname
	 * @return
	 * @throws NegocioException
	 */
	public AgendamentoETL inserirParametrosConsultaGeracao(String codigo, String matricula, String hostname)
			throws NegocioException {
		
		AgendamentoETL agendamentoOriginal = getAgendamentoOriginal(codigo);

		if (isProcessoNaFila(codigo)) {
			throw new NegocioException("process-already-on-queue");
		}

		if (isDisponivelDownload(agendamentoOriginal)) {
			throw new NegocioException("process-already-avaliable-for-download");
		}
		Optional<SituacaoSigilo> situacaoSigilo = situacaoSigiloRepository.findById(SituacaoEnum.INICIADO.getId());
		AgendamentoETL agendamento = AgendamentoETL.builder()
				.matricula(matricula)
				.periodo(agendamentoOriginal.getPeriodo())
				.evento(EventoEnum.DOWNLOAD.getNome())
				.situacao(situacaoSigilo.get())
				.dtHoraCadastro(new Date())
				.nomeArquivoCandidato(agendamentoOriginal.getNomeArquivoCandidato())
				.nomeArquivoPartido(agendamentoOriginal.getNomeArquivoPartido())
				.codigo(agendamentoOriginal.getCodigo())
				.hostname(hostname)
				.codigoSolicitacao(agendamentoOriginal.getCodigoSolicitacao())
				.dtHrProcessamento(null).build();
		return save(agendamento);
	}

	public AgendamentoETL getAgendamentoOriginal(String codigo) throws NegocioException {
		Map<String, String[]> param = new HashMap<>();
		param.put(CODIGO, new String[] { codigo });
		return findAll(param).stream().findAny().orElseThrow(() -> new NegocioException("no-original"));
	}

	/**
	 * Verifica se o processo possui os txts disponíveis (em banco), verifica se já
	 * foi gerado alguma vez
	 * 
	 * @param codigo
	 * @return
	 * @throws NegocioException
	 */
	public String[] buscaNomeArquivosProcessamento(String codigo) throws NegocioException {
		List<String> lista = auditoriaRepository.buscarTxtDisponiveis(codigo);
		Optional<String[]> nomeArquivos = Optional.ofNullable(lista.toArray(new String[lista.size()]));
		return nomeArquivos.orElseThrow(() -> new NegocioException("PROCESSAMENTO-SEM-ARQUIVOS"));
	}

	/**
	 * Busca os arquivos no diretório de relatórios do ETL
	 * 
	 * @param nomesArquivos - nomes dos arquivos relacionados
	 * @param codigo        - codigo do processo
	 * @return
	 * @throws NegocioException
	 * @throws IOException
	 */
	public Resource montaCaminhoArquivos(String[] nomesArquivos, String codigo) throws NegocioException {
		if (nomesArquivos.length == 0) {
			throw new NegocioException("ARQUIVO-NAO-ENCONTRADO");
		}
		List<File> files = new ArrayList<>();
		this.etlReportDir = parametroService.buscarPorChave(ParametroEnum.ETL_DIR_REPORT);
		for (int i = 0; i < nomesArquivos.length; i++) {
			files.add(new File(Paths.get(etlReportDir).resolve(nomesArquivos[i]).toString()));
		}
		File[] arquivos = new File[files.size()];
		arquivos = files.toArray(arquivos);
		fileStorage.zipArquivos(arquivos, Paths.get(etlReportDir), montarNomeArquivoZip(codigo));

		for (File file : files) {
			deleteFile(file);
		}

		return fileStorage.carregaResourceArquivo(montarNomeArquivoZip(codigo), Paths.get(etlReportDir));
	}

	public Resource consultaArquivoGerado(String codigo) throws NegocioException {
		return montaCaminhoArquivos(buscaNomeArquivosProcessamento(codigo), codigo);
	}

	/**
	 * Entra na pasta e verifica se o arquivo em questão já está zipado e pronto
	 * para download
	 * 
	 * @param codigo
	 * @param caminho
	 * @return
	 */
	public File verificaSeExisteZipado(String codigo, String caminho) {
		Optional<File> f = Arrays
				.asList(new File(caminho).listFiles((dir, name) -> name.equals(montarNomeArquivoZip(codigo)))).stream()
				.findFirst();
		return f.orElse(null);
	}

	/**
	 * Valida se o arquivo está dentro do período de vida de 30 dias. Caso sim,
	 * verifica se já está zipado e se não estiver zipa
	 * 
	 * @param codigo
	 * @return
	 * @throws NegocioException
	 * @throws IOException
	 */
	public Resource downloadConsultaArquivo(String codigo) throws NegocioException {
		if (arquivoGeradoUltimosTrintaDias(codigo)) {
			this.etlReportDir = parametroService.buscarPorChave(ParametroEnum.ETL_DIR_OUTPUT);
			File arquivo = verificaSeExisteZipado(codigo, etlReportDir);
			if (arquivo != null)
				return fileStorage.carregaResourceArquivo(arquivo.getName(), Paths.get(etlReportDir));
			else {
				return consultaArquivoGerado(codigo);
			}
		} else {
			throw new NegocioException("SOLICITE-NOVA-GERACAO");
		}

	}

	/**
	 * Carrega os processos de consulta que estão pendentes na fila
	 * 
	 * @return List<AgendamentoETL> - Processos pendentes
	 */
	public PageImpl<AgendamentoETL> processosGeracaoConsultaNaFila(Map<String, String[]> filter) {
		return crepo.processosGeracaoConsultaNaFila(getPageRequest(filter));
	}

	public File[] listaArquivosZipados() {
		File arquivo = new File(parametroService.buscarPorChave(ParametroEnum.ETL_DIR_REPORT));
		return arquivo.listFiles((dir1, name) -> name.endsWith(".zip"));
	}

	public boolean arquivoGeradoUltimosTrintaDias(String codigo) {
		return crepo.isArquivosGeradoUltimosTrintaDias(codigo);
	}

	public List<AgendamentoETL> listDisponivesiDownload() {
		List<AgendamentoETL> quebrasVigentes = crepo.processosDisponiveisParaDownload();
		return quebrasVigentes.stream()
				.filter(this::isArquivoZipExistente)
				.collect(Collectors.toList());
	}

	public boolean isArquivoZipExistente(AgendamentoETL agendamentoETL) {
		File arquivo = new File(parametroService.buscarPorChave(ParametroEnum.ETL_DIR_OUTPUT));
		arquivo = new File(arquivo.toPath().resolve(agendamentoETL.getCodigo() + ZIP_EXTENSION).toString());
		return arquivo.exists();
	}

	public boolean isDisponivelDownload(AgendamentoETL e) {
		List<AgendamentoETL> lista = listDisponivesiDownload();
		return lista.contains(e);
	}

	public String montarNomeArquivoZip(String codigo) {
		return codigo + ".zip";
	}

	public PageImpl<VisualizacaoArquivosDTO> detalhamentoCincoArquivos(Map<String, String[]> filter) {

		String codigo = (filter.get(CODIGO) != null ? filter.get(CODIGO)[0] : null);
		String cnpj = (filter.get(CNPJ) != null ? filter.get(CNPJ)[0] : null);
		String numeroAgencia = (filter.get(NUMERO_AGENCIA) != null ? filter.get(NUMERO_AGENCIA)[0] : null);
		String numeroConta = (filter.get(NUMERO_CONTA) != null ? filter.get(NUMERO_CONTA)[0] : null);

		VisualizacaoArquivosDTO.builder().banco(null).build();

		VisualizacaoArquivosDTO dto = VisualizacaoArquivosDTO.builder().codigo(codigo).cnpj(cnpj)
				.numeroAgencia(numeroAgencia).numeroConta(numeroConta).build();

		PageImpl<VisualizacaoArquivosDTO> response = crepo.detalhamentoCincoArquivos(getPageRequest(filter), dto);
		for (VisualizacaoArquivosDTO visualizacaoArquivosDTO : response) {
			visualizacaoArquivosDTO.setCnpj(cnpj);
			visualizacaoArquivosDTO.setCodigo(codigo);
			visualizacaoArquivosDTO.setNumeroAgencia(numeroAgencia);
			visualizacaoArquivosDTO.setNumeroConta(numeroConta);
		}
		return response;
	}

	public PageImpl<?> detalhamentoCincoArquivosPorArquivo(Map<String, String[]> filter) throws NegocioException {

		PageImpl<?> response;
		Pageable pageable = getPageRequest(filter);

		String codigo = (filter.get(CODIGO) != null ? filter.get(CODIGO)[0] : null);
		String cnpj = (filter.get(CNPJ) != null ? filter.get(CNPJ)[0] : null);
		String numeroAgencia = (filter.get(NUMERO_AGENCIA) != null ? filter.get(NUMERO_AGENCIA)[0] : null);
		String numeroConta = (filter.get(NUMERO_CONTA) != null ? filter.get(NUMERO_CONTA)[0] : null);
		String arquivo = (filter.get("arquivo") != null ? filter.get("arquivo")[0] : null);

		VisualizacaoArquivosDTO dto = VisualizacaoArquivosDTO.builder().codigo(codigo).cnpj(cnpj)
				.numeroAgencia(numeroAgencia).numeroConta(numeroConta).build();

		if (arquivo == null)
			throw new NegocioException("arquivo-nulo");

		response = selecionaInterfacePorArquivo(arquivo, pageable, dto);
		return response;
	}

	protected PageImpl<?> selecionaInterfacePorArquivo(String arquivo, Pageable pageable, VisualizacaoArquivosDTO dto)
			throws NegocioException {
		PageImpl<?> response = null;

		switch (arquivo) {
		case "banco":
			response = bancoCrepo.detalhamentoArquivoBanco(pageable, dto);
			break;
		case "conta":
			response = contaCrepo.detalhamentoArquivoConta(pageable, dto);
			break;
		case "extrato":
			response = extratoCrepo.detalhamentoArquivoExtrato(pageable, dto);
			break;
		case "origemDestino":
			response = origemDestinoCrepo.detalhamentoArquivoOrigemDestino(pageable, dto);
			break;
		case "titular":
			response = titularCrepo.detalhamentoArquivoTitular(pageable, dto);
			break;
		default:
			throw new NegocioException("tipo-nao-definido");
		}
		return response;
	}

	private boolean isProcessoNaFila(String codigo) {
		return (!agendamentoETLRepository.listProcessosFila(codigo).isEmpty());
	}

	protected void expurgaArquivos() throws NegocioException {
		File[] listaArquivos = Optional.ofNullable(getListaArquivos()).orElse(new File[] {});
		Log.info(this.getClass(), "Iniciando expurgo de arquivos:");
		for (File a : listaArquivos) {
			FileTime dtCriacaoInstant = getBFAofFile(a).creationTime();
			LocalDate dtCriacao = LocalDateTime.ofInstant(
						dtCriacaoInstant.toInstant(), 
						ZoneId.of(ZoneOffset.UTC.getId()))
					.toLocalDate();
			if (getDataValidaArquivos().isAfter(dtCriacao)) {
				Log.info(this.getClass(), "- Arquivo: " + a.getName());
				deleteFile(a);
			}
		}
	}

	protected BasicFileAttributes getBFAofFile(File f) throws NegocioException {
		try {
			return Files.readAttributes(f.toPath(), BasicFileAttributes.class);
		} catch (IOException e) {
			throw new NegocioException("erro-arquivo" + e);
		}
	}

	protected boolean deleteFile(File a) throws NegocioException {
		try {			
			Files.delete(a.toPath());
			Log.info(this.getClass(), "Deletado");
		} catch (IOException e) {
			Log.error(this.getClass(), "Erro ao deletetar o arquivo" + a.getName());
			throw new NegocioException("erro-delete-file" + e);
		}
		return true;
	}

	protected File[] getListaArquivos() {
		File arquivo = new File(parametroService.buscarPorChave(ParametroEnum.ETL_DIR_REPORT));
		return arquivo.listFiles((dir1, name) -> name.endsWith(".zip"));
	}

	protected LocalDate getDataValidaArquivos() {
		return LocalDate.now().minusDays(30);
	}
}
