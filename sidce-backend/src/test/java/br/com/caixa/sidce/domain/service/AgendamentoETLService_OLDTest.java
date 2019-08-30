package br.com.caixa.sidce.domain.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.NoTransactionException;
import org.springframework.web.multipart.MultipartFile;

import br.com.caixa.sidce.domain.model.AgendamentoETL;
import br.com.caixa.sidce.domain.model.Arquivo;
import br.com.caixa.sidce.domain.model.AuditoriaProcessamento;
import br.com.caixa.sidce.domain.model.CodigoSolicitacao;
import br.com.caixa.sidce.domain.model.Evento;
import br.com.caixa.sidce.domain.model.SituacaoSigilo;
import br.com.caixa.sidce.domain.model.SituacaoSolicitacao;
import br.com.caixa.sidce.domain.model.Solicitacao;
import br.com.caixa.sidce.domain.model.enums.EventoEnum;
import br.com.caixa.sidce.domain.model.enums.SituacaoEnum;
import br.com.caixa.sidce.domain.model.enums.SituacaoSolicitacaoEnum;
import br.com.caixa.sidce.infraestructure.persistence.jpa.AgendamentoETLCustomRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.AgendamentoETLRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.ArquivoRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.AuditoriaProcessamentoRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.EventoRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.SituacaoSigiloRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.SituacaoSolicitacaoRepository;
import br.com.caixa.sidce.interfaces.util.FileStorage;
import br.com.caixa.sidce.util.infraestructure.auditoria.AuditoriaAspecto;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;

@DataJpaTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = { "spring.jpa.hibernate.ddl-auto=create" })
@ComponentScan(basePackages = { "br.com.caixa.sidce.config", "br.com.caixa.sidce.util.infraestructure",
		"br.com.caixa.sidce.domain.service", "br.com.caixa.sidce.infraestructure.persistence.jpa",
		"br.com.caixa.sidce.interfaces.util" })
@RunWith(SpringRunner.class)
@SpringBootTest
public class AgendamentoETLService_OLDTest {

	@Autowired
	AgendamentoETLService agendamentoETLService;

	@Autowired
	AgendamentoETLRepository repository;

	@Autowired
	AgendamentoETLCustomRepository crepo;

	@Autowired
	AuditoriaProcessamentoRepository auditoriaProcessamentoRepository;

	@Autowired
	SituacaoSigiloRepository situacaoSigiloRepository;

	@Autowired
	ArquivoRepository arquivoRepository;

	@Autowired
	EventoRepository eventoRepository;

	@Autowired
	Environment env;

	@Autowired
	AuditoriaAspecto auditoria;

	@Spy
	@InjectMocks
	AgendamentoETLService mockService;

	@Mock
	AgendamentoETLCustomRepository mockCrepo;

	@Mock
	AgendamentoETLRepository mockRepo;

	@Mock
	FileStorage mockFileStorage;

	@Mock
	SituacaoSigiloRepository mockSituacaoSigiloRepo;

	@Mock
	ParametroService mockParametroService;

	@Mock
	ArquivoRepository mockArquivoRepository;

	@Mock
	EventoService eventoService;

	@Mock
	SituacaoSolicitacaoRepository mockSituacaoSolicitacaoRepo;

	@Mock
	File file;

	@Mock
	MultipartFile multipartFile;

	@Mock
	Principal principal;

	@Mock
	private CodigoSolicitacaoService codigoSolicitacaoService;

	@Mock
	private AuditoriaAspecto mockAuditoria;

	@Mock
	private FactoryEmail email;

	Evento evento;

	Solicitacao solicitacao;

	@Rule
	public final ExpectedException exception = ExpectedException.none();
	@Rule
	public TemporaryFolder output = new TemporaryFolder();
	@Rule
	public TemporaryFolder report = new TemporaryFolder();

	LocalDate agora = LocalDate.now();

	MockMultipartFile candidatoFile = new MockMultipartFile("user-file", "cnpj_candidatos_2018.txt", null,
			"test data".getBytes());
	MockMultipartFile partidoFile = new MockMultipartFile("user-file", "cnpj_partido_2018.txt", null,
			"test data".getBytes());
	MockMultipartFile testFile = new MockMultipartFile("user-file", "test.txt", null, "test data".getBytes());

	@Before
	public void initializeFolders() throws IOException {
		report.newFile("038-TSE-002018-10_AGENCIAS.txt");
		report.newFile("038-TSE-002018-10_ORIGEM_DESTINO.txt");
		report.newFile("038-TSE-002018-10_CONTAS.txt");
		report.newFile("038-TSE-002018-10_TITULARES.txt");
		report.newFile("038-TSE-002018-10_EXTRATO.txt");
		report.newFile("cnpj_candidatos_2018.txt");
		report.newFile("cnpj_partido_2018.txt");

		output.newFile("038-TSE-002018-10_AGENCIAS.txt");
		output.newFile("038-TSE-002018-10_ORIGEM_DESTINO.txt");
		output.newFile("038-TSE-002018-10_CONTAS.txt");
		output.newFile("038-TSE-002018-10_TITULARES.txt");
		output.newFile("038-TSE-002018-10_EXTRATO.txt");

		solicitacao = new Solicitacao();
		solicitacao.setId(7);

		MockitoAnnotations.initMocks(this);

		Evento evento1 = Evento.builder().descricaoEvento(EventoEnum.DOWNLOAD).build();
		eventoRepository.save(evento1);

		AuditoriaProcessamento auditoriaProcessamento1 = AuditoriaProcessamento.builder().id(1)
				.codigo("DE251ABD-4B2D-4994-93CC-582C83280113").data(new Date()).dataHoraProcessamento(new Date())
				.evento(EventoEnum.SOB_DEMANDA.getNome()).hostname("TesteHost").matricula("C123456")
				.nome("038-TSE-002018-10_TITULARES.txt").origem("SIDCE").periodo(201812).quantidadeRegistros(300)
				.build();

		AuditoriaProcessamento auditoriaProcessamento2 = auditoriaProcessamento1.toBuilder()
				.nome("038-TSE-002018-10_AGENCIAS.txt").id(2).build();
		AuditoriaProcessamento auditoriaProcessamento3 = auditoriaProcessamento1.toBuilder()
				.nome("038-TSE-002018-10_ORIGEM_DESTINO.txt").id(3).build();
		AuditoriaProcessamento auditoriaProcessamento4 = auditoriaProcessamento1.toBuilder()
				.nome("038-TSE-002018-10_CONTAS.txt").id(4).build();
		AuditoriaProcessamento auditoriaProcessamento5 = auditoriaProcessamento1.toBuilder()
				.nome("038-TSE-002018-10_EXTRATO.txt").id(5).build();
		AuditoriaProcessamento auditoriaProcessamento6 = auditoriaProcessamento1.toBuilder()
				.nome("cnpj_candidatos_2018.txt").id(6).build();
		AuditoriaProcessamento auditoriaProcessamento7 = auditoriaProcessamento1.toBuilder()
				.nome("cnpj_partido_2018.txt").id(7).build();

		List<AuditoriaProcessamento> auditorias = new ArrayList<>();

		auditorias.add(auditoriaProcessamento1);
		auditorias.add(auditoriaProcessamento2);
		auditorias.add(auditoriaProcessamento3);
		auditorias.add(auditoriaProcessamento4);
		auditorias.add(auditoriaProcessamento5);
		auditorias.add(auditoriaProcessamento6);
		auditorias.add(auditoriaProcessamento7);

		auditoriaProcessamentoRepository.saveAll(auditorias);

	}

	private void salvaSituacaoSigilo() {
		SituacaoSigilo situacao1 = SituacaoSigilo.builder().id(1).nomeSituacao(SituacaoEnum.GERADO.getNome())
				.descricaoSituacao("").dtHrCadastro(new Date()).build();
		SituacaoSigilo situacao2 = situacao1.toBuilder().id(2).nomeSituacao(SituacaoEnum.TRANSMITIDO.getNome()).build();
		SituacaoSigilo situacao3 = situacao1.toBuilder().id(3).nomeSituacao(SituacaoEnum.REJEITADO.getNome()).build();
		SituacaoSigilo situacao4 = situacao1.toBuilder().id(4).nomeSituacao(SituacaoEnum.INICIADO.getNome()).build();

		List<SituacaoSigilo> situacoes = new ArrayList<>();
		situacoes.add(situacao1);
		situacoes.add(situacao2);
		situacoes.add(situacao3);
		situacoes.add(situacao4);

		situacaoSigiloRepository.saveAll(situacoes);
	}

	@Test
	public void testaPattertnArquivoTSE() {
		StringBuilder esperado = new StringBuilder();
		esperado.append("TSE-00");
		if (agora.getMonthValue() == 1)
			esperado.append(agora.getYear() - 1);
		else
			esperado.append(agora.getYear());

		assertEquals(agendamentoETLService.patternPeriodoAtual("TSE-00"), esperado.toString());
	}

	@Test
	public void testaIdentificacaoSeparacaoNomeArquivosPartidoCandidato() {

		MultipartFile[] multipartFiles = new MultipartFile[] { candidatoFile, partidoFile };

		Map<String, String> mapaNomes = new HashMap<>();
		mapaNomes.put("candidato", "cnpj_candidatos_2018.txt");
		mapaNomes.put("partido", "cnpj_partido_2018.txt");

		assertEquals(mapaNomes, agendamentoETLService.separaNomeArquivos(multipartFiles));
	}

	@Test
	public void testaIdentificacaoSeparacaoNomeArquivosPartido() {

		MultipartFile[] multipartFiles = new MultipartFile[] { partidoFile };

		Map<String, String> mapaNomes = new HashMap<>();
		mapaNomes.put("partido", "cnpj_partido_2018.txt");

		assertEquals(agendamentoETLService.separaNomeArquivos(multipartFiles), mapaNomes);
	}

	@Test
	public void testaIdentificacaoSeparacaoNomeArquivosCandidato() {

		MultipartFile[] multipartFiles = new MultipartFile[] { candidatoFile };

		Map<String, String> mapaNomes = new HashMap<>();
		mapaNomes.put("candidato", "cnpj_candidatos_2018.txt");

		assertEquals(mapaNomes, agendamentoETLService.separaNomeArquivos(multipartFiles));
	}

	@Test
	public void testaIdentificacaoSeparacaoNomeArquivosNull() {

		MultipartFile[] multipartFiles = new MultipartFile[] {};

		Map<String, String> mapaNomes = new HashMap<>();

		mapaNomes.put("candidato", "");
		mapaNomes.put("partido", "");

		assertEquals(mapaNomes, agendamentoETLService.separaNomeArquivos(multipartFiles));
	}

	@Test
	public void testaSalvarArquivo() throws NegocioException {
		Mockito.doReturn("/sidce/test").when(mockParametroService).buscarPorChave(Mockito.any());
		String filename = mockService.salvarArquivoETL(candidatoFile);
		assertFalse(filename.isEmpty());
	}

	@Test(expected = NegocioException.class)
	public void testaSalvarArquivoException() throws NegocioException {
		Mockito.doThrow(NegocioException.class).when(mockFileStorage).salvarArquivo(Mockito.any(), Mockito.any());
		Mockito.doReturn("/sidce/test").when(mockParametroService).buscarPorChave(Mockito.any());
		mockService.salvarArquivoETL(candidatoFile);
	}

	@Test
	public void testaBuscaSituacaoSigiloDisponiveis() {
		List<SituacaoSigilo> list = Arrays.asList(new SituacaoSigilo());
		Mockito.doReturn(list).when(mockSituacaoSigiloRepo).findAll();
		assertEquals(list, mockService.buscaSituacaoSigiloDisponiveis());
	}

	@Test
	public void testaEnviadaUltimaGeracao() throws NegocioException {
		AgendamentoETL agendamentoETL = AgendamentoETL.builder().situacao(SituacaoSigilo.builder().id(1).build())
				.build();
		List<AgendamentoETL> processos = Arrays.asList(agendamentoETL);
		Mockito.doReturn(processos).when(mockCrepo).ultimoUploadGeracaoCadastradoGerado();
		assertFalse(mockService.isEnviadaSimbaUltimaGeracao());
	}

	@Test
	public void testaEnviadaUltimaGeracaoTrue() {
		SituacaoSigilo s = SituacaoSigilo.builder().id(5).build();
		AgendamentoETL agendamento = AgendamentoETL.builder().situacao(s).build();
		Mockito.doReturn(Arrays.asList(agendamento)).when(mockCrepo).ultimoUploadGeracaoCadastradoGerado();
		assertTrue(mockService.isEnviadaSimbaUltimaGeracao());
	}

	@Test
	public void testAgendamentoGeracaoPedenteUltimoUpload() {
		Mockito.doReturn(new ArrayList<>()).when(mockCrepo).ultimoUploadGeracaoCadastradoGerado();
		assertFalse(mockService.isAgendamentoGeracaoPedente());
	}

	@Test
	public void testAgendamentoGeracaoPedenteUltimoUploadSemDtHr() {
		AgendamentoETL agendamento = AgendamentoETL.builder().build();
		Mockito.doReturn(Arrays.asList(agendamento)).when(mockCrepo).ultimoUploadGeracaoCadastrado();
		assertTrue(mockService.isAgendamentoGeracaoPedente());
	}

	@Test
	public void testAgendamentoGeracaoPedenteUltimoUploadComDtHr() {
		AgendamentoETL agendamento = AgendamentoETL.builder().dtHrProcessamento(new Date()).build();
		Mockito.doReturn(Arrays.asList(agendamento)).when(mockCrepo).ultimoUploadGeracaoCadastrado();
		assertFalse(mockService.isAgendamentoGeracaoPedente());
	}

	@Test(expected = NegocioException.class)
	public void testaBuscaArquivoPorCodigoNotFound() throws NegocioException {
		mockService.buscaArquivoPorCodigo("1");
	}

	@Test(expected = NegocioException.class)
	public void testInsereTabelaParametroUploadError() throws NegocioException {
		MultipartFile[] file = {};
		mockService.insereTabelaParametro("noMatricula", 0, file, "hostname", EventoEnum.UPLOAD.getNome(), "codigo");
	}

	@Test
	public void testInsereTabelaParametroUpload() throws NegocioException {
		MultipartFile[] file = {};
		mockService.insereTabelaParametro("noMatricula", 0, file, "hostname", EventoEnum.DOWNLOAD.getNome(), "codigo");
	}

	@Test
	public void testInsereTabelaParametroUpload2() throws NegocioException {
		MultipartFile[] multipartFiles = new MultipartFile[] { partidoFile };
		mockService.insereTabelaParametro("noMatricula", 0, multipartFiles, "hostname", EventoEnum.DOWNLOAD.getNome(),
				"codigo");
	}

	@Test
	public void testInsereTabelaParametroUpload3() throws NegocioException {
		MultipartFile[] multipartFiles = new MultipartFile[] { partidoFile };
		mockService.insereTabelaParametro("noMatricula", 0, multipartFiles, "hostname", EventoEnum.UPLOAD.getNome(),
				"codigo");
	}

	@Test
	public void testIsETLDisponivel() {
		Mockito.doReturn(Boolean.TRUE).when(mockService).isAgendamentoGeracaoPedente();
		assertFalse(mockService.isETLDisponivel());
	}

	@Test
	public void testIsETLDisponivel2() {
		Mockito.doReturn(Boolean.FALSE).when(mockService).isEnviadaSimbaUltimaGeracao();
		assertFalse(mockService.isETLDisponivel());
	}

	@Test(expected = NegocioException.class)
	public void testaUploadArquivoErro() throws NegocioException, IOException {
		Mockito.doThrow(new IOException()).when(mockService).criaArquivo(Mockito.any());
		mockService.uploadArquivo(report.getRoot().getPath());
	}

	@Test(expected = NegocioException.class) // TODO - Testar execução completa do método
	public void testaCarregaOutputUltimoProcesso() throws NegocioException, IOException {

		salvaSituacaoSigilo();

		AgendamentoETL agendamentoETL = AgendamentoETL.builder().codigo("12461D53-53A5-477D-A682-52E5811639A1")
				.evento(EventoEnum.UPLOAD.getNome()).hostname("testHost").dtHoraCadastro(new Date())
				.dtHrProcessamento(new Date()).matricula("testUser").nomeArquivoCandidato("cnpj_candidatos_2018.txt")
				.nomeArquivoPartido("cnpj_partido_2018.txt").periodo(201901)
				.situacao(SituacaoSigilo.builder().id(1).nomeSituacao("Iniciado").build()).build();

		File f = output.newFile("TSE-002019File.txt");
		File[] mockedFileArray = { f };
		Resource res = Mockito.mock(Resource.class);

		Mockito.doReturn(agendamentoETL).when(mockService).buscaDadosUltimoProcessoGerado();
		Mockito.doReturn(mockedFileArray).when(mockService).carregarOutputETL();
		Mockito.doReturn(f).when(mockService).createFile();
		Mockito.doReturn(res).when(mockFileStorage).carregaResourceArquivo(Mockito.any(), Mockito.any());
		Mockito.doReturn("/sidce/test").when(mockParametroService).buscarPorChave(Mockito.any());

		Resource r = mockService.carregaOutputUltimoProcesso();

		assertNotNull(r);
	}

	@Test(expected = NegocioException.class)
	public void testaCarregaOutputUltimoProcessoDirectoryNotFound() throws NegocioException, IOException {
		doReturn(evento).when(eventoService).buscarPorChave(any());
		Mockito.doReturn(new AgendamentoETL()).when(mockService).buscaDadosUltimoProcessoGerado();
		// Mock para gerar exception
		File mockedFile = Mockito.mock(File.class);

		Mockito.when(mockedFile.exists()).thenReturn(false);
		Mockito.doReturn(mockedFile).when(mockService).createFile();
		Mockito.doReturn("/sidce/test").when(mockParametroService).buscarPorChave(Mockito.any());

		mockService.carregaOutputUltimoProcesso();
	}

	@Test(expected = NegocioException.class)
	public void testaCarregaOutputUltimoProcessoJaExisteZip() throws NegocioException, IOException {
		Mockito.doReturn(new AgendamentoETL()).when(mockService).buscaDadosUltimoProcessoGerado();
		// Mock para zipExistir
		Mockito.doReturn(new File(report.getRoot().getPath())).when(mockService).createFile(Mockito.anyString());
		Mockito.doReturn("/sidce/test").when(mockParametroService).buscarPorChave(Mockito.any());
		// Mock para retorno precoce
		Resource r = new PathResource(report.getRoot().getPath());
		Mockito.doReturn(r).when(mockFileStorage).carregaResourceArquivo(Mockito.anyString(), Mockito.any());
		assertEquals(r, mockService.carregaOutputUltimoProcesso());
	}

	@Test(expected = NegocioException.class)
	public void testaCarregaOutputUltimoProcessoFileNotFound() throws NegocioException, IOException {
		Mockito.doReturn(new AgendamentoETL()).when(mockService).buscaDadosUltimoProcessoGerado();
		Mockito.doReturn("/sidce/test").when(mockParametroService).buscarPorChave(Mockito.any());
		mockService.carregaOutputUltimoProcesso();
	}

	@Test
	public void testaCarregarOutputETL() throws NegocioException {
		Mockito.doReturn("/sidce/test").when(mockParametroService).buscarPorChave(Mockito.any());
		mockService.carregarOutputETL();
	}

	@Test
	public void isProntaUltimaGeracaoTest() {
		Mockito.doReturn(new ArrayList<>()).when(mockCrepo).ultimoUploadGeracaoCadastradoGerado();
		assertTrue(mockService.isProntaUltimaGeracao());
	}

	@Test
	public void isProntaUltimaGeracaoFalseTest() {
		SituacaoSigilo s = SituacaoSigilo.builder().id(4).build();
		AgendamentoETL agendamento = AgendamentoETL.builder().situacao(s).build();
		Mockito.doReturn(Arrays.asList(agendamento)).when(mockCrepo).ultimoUploadGeracaoCadastrado();
		assertFalse(mockService.isProntaUltimaGeracao());
	}

	@Test
	public void isProntaUltimaGeracaoTrueTest() {
		SituacaoSigilo s = SituacaoSigilo.builder().id(1).build();
		AgendamentoETL agendamento = AgendamentoETL.builder().situacao(s).build();
		Mockito.doReturn(Arrays.asList(agendamento)).when(mockCrepo).ultimoUploadGeracaoCadastrado();
		assertTrue(mockService.isProntaUltimaGeracao());
	}

	@Test
	public void insereParametrosGeracaoRotinaTest() throws UnknownHostException {
		assertNotNull(mockService.insereParametrosGeracaoRotina());
	}

	@Test
	public void insereParametrosGeracaoSobDemandaTest() throws UnknownHostException {
		assertNull(mockService.insereParametrosGeracaoSobDemanda("noMatricula", "hostname", new Solicitacao(),
				new CodigoSolicitacao()));
	}

	@Test
	public void isProntaUltimaGeracao2Test() {
		Mockito.doReturn(new ArrayList<>()).when(mockCrepo).ultimoUploadGeracaoCadastradoGerado();
		assertTrue(mockService.isProntaUltimaGeracao());
	}

	@Test
	public void buscaAgendamentoPorSolicitacaoTest() throws NegocioException {
		Mockito.doReturn(new AgendamentoETL()).when(mockRepo).buscaAgendamentoETLPorSolicitacao(any());
		mockService.buscaAgendamentoPorSolicitacao(1);
	}

	@Test(expected = NegocioException.class)
	public void buscaDadosUltimoProcessoGeradoTest() throws NegocioException {
		Mockito.doReturn(new AgendamentoETL()).when(mockRepo).buscaAgendamentoETLPorSolicitacao(any());
		mockService.buscaDadosUltimoProcessoGerado();
	}

	@Test
	public void buscaDadosUltimoProcessoGerado2Test() throws NegocioException {
		SituacaoSigilo s = SituacaoSigilo.builder().id(1).build();
		AgendamentoETL agendamento = AgendamentoETL.builder().situacao(s).build();
		Mockito.doReturn(Arrays.asList(agendamento)).when(mockCrepo).ultimoUploadGeracaoCadastradoGerado();
		mockService.buscaDadosUltimoProcessoGerado();
	}

	@Test
	public void buscaRetornoSimbaTest() throws NegocioException {
		Optional<Arquivo> arquivoOptional = Optional.of(new Arquivo());
		Mockito.doReturn(AgendamentoETL.builder().codigo("3ACF3524-7F1D-4DBD-AC95-395FCD09EC96").build()).when(mockRepo)
				.buscaAgendamentoETLPorSolicitacaoPrimeiro(any());
		Mockito.doReturn(arquivoOptional).when(mockArquivoRepository).findOne(any());
		assertNotNull(mockService.buscaRetornoSimba(2));
	}

	@Test
	public void uploadArquivosPartidoCandidatoTest() throws NegocioException {
		CodigoSolicitacao codigoSolicitacao = CodigoSolicitacao.builder().id(1).numero(1).prefixo("TEST")
				.codigo("TEST000001").build();
		MultipartFile[] multipartFiles = new MultipartFile[] { candidatoFile, partidoFile };
		Mockito.doReturn(new File(report.getRoot().getPath())).when(mockService).createFile(Mockito.anyString());
		Mockito.doReturn("/sidce/test").when(mockParametroService).buscarPorChave(Mockito.any());
		Mockito.doReturn(1).when(mockCrepo).gerarNumeroCodigoAfastamento();
		Mockito.doReturn(codigoSolicitacao).when(codigoSolicitacaoService).save(any());
		Mockito.doReturn(new AgendamentoETL()).when(mockService).save(any());
		mockService.uploadArquivosPartidoCandidato(multipartFiles, "matricula", "hostname");
	}

	@Test
	public void uploadArquivosPartidoCandidato5Test() throws NegocioException {
		CodigoSolicitacao codigoSolicitacao = CodigoSolicitacao.builder().id(1).numero(1).prefixo("TEST")
				.codigo("TEST000001").build();
		MultipartFile[] multipartFiles = new MultipartFile[] { candidatoFile, partidoFile };
		Mockito.doReturn(new File(report.getRoot().getPath())).when(mockService).createFile(Mockito.anyString());
		Mockito.doReturn("/sidce/test").when(mockParametroService).buscarPorChave(Mockito.any());
		Mockito.doReturn(1).when(mockCrepo).gerarNumeroCodigoAfastamento();
		Mockito.doReturn(codigoSolicitacao).when(codigoSolicitacaoService).save(any());
		mockService.uploadArquivosPartidoCandidato(multipartFiles, "matricula", "hostname");
	}

	@Test(expected = NegocioException.class)
	public void uploadArquivosPartidoCandidato2Test() throws NegocioException {
		AgendamentoETL agendamentoETL = AgendamentoETL.builder().situacao(SituacaoSigilo.builder().id(1).build())
				.build();
		List<AgendamentoETL> processos = Arrays.asList(agendamentoETL);
		Mockito.doReturn(processos).when(mockCrepo).ultimoUploadGeracaoCadastradoGerado();
		MultipartFile[] multipartFiles = new MultipartFile[] { candidatoFile, partidoFile };
		Mockito.doReturn(new File(report.getRoot().getPath())).when(mockService).createFile(Mockito.anyString());
		Mockito.doReturn("/sidce/test").when(mockParametroService).buscarPorChave(Mockito.any());
		mockService.uploadArquivosPartidoCandidato(multipartFiles, "matricula", "hostname");
	}

	@Test(expected = NegocioException.class)
	public void uploadArquivosPartidoCandidato3Test() throws NegocioException {
		MultipartFile[] multipartFiles = {};
		Mockito.doReturn(new File(report.getRoot().getPath())).when(mockService).createFile(Mockito.anyString());
		Mockito.doReturn("/sidce/test").when(mockParametroService).buscarPorChave(Mockito.any());
		mockService.uploadArquivosPartidoCandidato(multipartFiles, "matricula", "hostname");
	}

	@Test(expected = NoTransactionException.class)
	public void uploadArquivosPartidoCandidato4Test() throws NegocioException {
		MultipartFile[] multipartFiles = new MultipartFile[] { candidatoFile, partidoFile };
		Mockito.doReturn(new File(report.getRoot().getPath())).when(mockService).createFile(Mockito.anyString());
		Mockito.doReturn("/sidce/test").when(mockParametroService).buscarPorChave(Mockito.any());
		Mockito.doThrow(NegocioException.class).when(mockService).salvarArquivoETL(Mockito.any());
		mockService.uploadArquivosPartidoCandidato(multipartFiles, "matricula", "hostname");
	}

	@Test
	public void inserirArquivoSimbaTest() throws NegocioException, IOException {
		Map<String, String[]> parameterMap = new HashMap<String, String[]>();
		parameterMap.put("codigo", new String[] { "12461D53-53A5-477D-A682-52E5811639A1" });
		parameterMap.put("situacao", new String[] { "1" });
		parameterMap.put("id", new String[] { "1" });
		parameterMap.put("tipo", new String[] { "solicitacao" });

		SituacaoSigilo situacaoSigilo = SituacaoSigilo.builder().id(1).nomeSituacao("Transmitido").build();
		Optional<SituacaoSigilo> situacaoOptional = Optional.of(situacaoSigilo);

		SituacaoSolicitacao situacaoSolicitacao = SituacaoSolicitacao.builder().id(1)
				.nomeSituacao(SituacaoSolicitacaoEnum.REJEITADO).build();
		AgendamentoETL agendamentoETL = AgendamentoETL.builder().codigo("3ACF3524-7F1D-4DBD-AC95-395FCD09EC96")
				.solicitacao(new Solicitacao()).build();
		Mockito.doReturn(agendamentoETL).when(mockRepo).save(any());

		Mockito.doReturn(agendamentoETL).when(mockRepo).buscaAgendamentoETLPorSolicitacaoPrimeiro(any());
		Mockito.doReturn(situacaoOptional).when(mockSituacaoSigiloRepo).findById(any());
		Mockito.doReturn(situacaoSolicitacao).when(mockSituacaoSolicitacaoRepo).getByNomeSituacao(any());
		assertNotNull(mockService.inserirArquivoSimba(candidatoFile, "matricula", parameterMap));

		Mockito.doReturn(AgendamentoETL.builder().codigo("3ACF3524-7F1D-4DBD-AC95-395FCD09EC96").build()).when(mockRepo)
				.buscaAgendamentoETLPorSolicitacaoPrimeiro(any());
		assertNotNull(mockService.inserirArquivoSimba(candidatoFile, "matricula", parameterMap));

	}

	@Test(expected = NegocioException.class)
	public void inserirArquivoSimbaExceptionTest() throws NegocioException, IOException {
		Map<String, String[]> parameterMap = new HashMap<String, String[]>();
		parameterMap.put("codigo", new String[] { "12461D53-53A5-477D-A682-52E5811639A1" });
		parameterMap.put("situacao", new String[] { "1" });
		parameterMap.put("id", new String[] { "1" });
		parameterMap.put("tipo", new String[] { "solicitacao" });

		SituacaoSigilo situacaoSigilo = SituacaoSigilo.builder().id(1).nomeSituacao("Transmitido").build();
		Optional<SituacaoSigilo> situacaoOptional = Optional.of(situacaoSigilo);

		Mockito.doReturn(AgendamentoETL.builder().codigo("3ACF3524-7F1D-4DBD-AC95-395FCD09EC96")
				.solicitacao(new Solicitacao()).build()).when(mockRepo)
				.buscaAgendamentoETLPorSolicitacaoPrimeiro(any());
		Mockito.doReturn(situacaoOptional).when(mockSituacaoSigiloRepo).findById(any());
		Mockito.doReturn(new SituacaoSolicitacao()).when(mockSituacaoSolicitacaoRepo).getByNomeSituacao(any());
		Mockito.doReturn(true).when(mockArquivoRepository).exists(any());
		assertNull(mockService.inserirArquivoSimba(candidatoFile, "matricula", parameterMap));
	}

	@Test(expected = NoSuchElementException.class)
	public void inserirArquivoSimbaException2Test() throws NegocioException, IOException {
		Map<String, String[]> parameterMap = new HashMap<String, String[]>();
		parameterMap.put("codigo", new String[] { "12461D53-53A5-477D-A682-52E5811639A1" });
		parameterMap.put("situacao", new String[] { "1" });
		parameterMap.put("id", new String[] { "1" });
		parameterMap.put("tipo", new String[] { "solicitacao2" });

		SituacaoSigilo situacaoSigilo = SituacaoSigilo.builder().id(1).nomeSituacao("Iniciado").build();
		AgendamentoETL agendamentoETL = AgendamentoETL.builder().codigo("3ACF3524-7F1D-4DBD-AC95-395FCD09EC96")
				.solicitacao(new Solicitacao()).build();
		Optional<SituacaoSigilo> situacaoOptional = Optional.of(situacaoSigilo);
		Optional<AgendamentoETL> optional = Optional.of(agendamentoETL);

		Mockito.doReturn(agendamentoETL).when(mockRepo).buscaAgendamentoETLPorSolicitacaoPrimeiro(any());
		Mockito.doReturn(optional).when(mockRepo).findById(any());
		Mockito.doReturn(situacaoOptional).when(mockSituacaoSigiloRepo).findById(any());
		Mockito.doReturn(new SituacaoSolicitacao()).when(mockSituacaoSolicitacaoRepo).getByNomeSituacao(any());
		assertNull(mockService.inserirArquivoSimba(candidatoFile, "matricula", parameterMap));
	}

	@Test
	public void inserirArquivoSimba3Test() throws NegocioException, IOException {
		Map<String, String[]> parameterMap = new HashMap<String, String[]>();
		parameterMap.put("codigo", new String[] { "12461D53-53A5-477D-A682-52E5811639A1" });
		parameterMap.put("situacao", new String[] { "1" });
		parameterMap.put("id", new String[] { "1" });
		parameterMap.put("tipo", new String[] { "solicitacao" });

		SituacaoSigilo situacaoSigilo = SituacaoSigilo.builder().id(1).nomeSituacao("Rejeitado").build();
		Optional<SituacaoSigilo> situacaoOptional = Optional.of(situacaoSigilo);

		SituacaoSolicitacao situacaoSolicitacao = SituacaoSolicitacao.builder().id(1)
				.nomeSituacao(SituacaoSolicitacaoEnum.REJEITADO).build();
		AgendamentoETL agendamentoETL = AgendamentoETL.builder().codigo("3ACF3524-7F1D-4DBD-AC95-395FCD09EC96")
				.solicitacao(new Solicitacao()).build();
		Mockito.doReturn(agendamentoETL).when(mockRepo).save(any());

		Mockito.doReturn(agendamentoETL).when(mockRepo).buscaAgendamentoETLPorSolicitacaoPrimeiro(any());
		Mockito.doReturn(situacaoOptional).when(mockSituacaoSigiloRepo).findById(any());
		Mockito.doReturn(situacaoSolicitacao).when(mockSituacaoSolicitacaoRepo).getByNomeSituacao(any());
		assertNotNull(mockService.inserirArquivoSimba(candidatoFile, "matricula", parameterMap));
	}

	@Test
	public void inserirArquivoSimba4Test() throws NegocioException, IOException {
		Map<String, String[]> parameterMap = new HashMap<String, String[]>();
		parameterMap.put("codigo", new String[] { "12461D53-53A5-477D-A682-52E5811639A1" });
		parameterMap.put("situacao", new String[] { "1" });
		parameterMap.put("id", new String[] { "1" });
		parameterMap.put("tipo", new String[] { "solicitacao" });

		SituacaoSolicitacao situacaoSolicitacao = SituacaoSolicitacao.builder().id(1)
				.nomeSituacao(SituacaoSolicitacaoEnum.REJEITADO).build();
		AgendamentoETL agendamentoETL = AgendamentoETL.builder().codigo("3ACF3524-7F1D-4DBD-AC95-395FCD09EC96")
				.solicitacao(new Solicitacao()).build();
		Mockito.doReturn(agendamentoETL).when(mockRepo).save(any());

		SituacaoSigilo situacaoSigilo = SituacaoSigilo.builder().id(1).nomeSituacao("Gerado").build();
		Optional<SituacaoSigilo> situacaoOptional = Optional.of(situacaoSigilo);
		Mockito.doReturn(agendamentoETL).when(mockRepo).buscaAgendamentoETLPorSolicitacaoPrimeiro(any());
		Mockito.doReturn(situacaoOptional).when(mockSituacaoSigiloRepo).findById(any());
		Mockito.doReturn(situacaoSolicitacao).when(mockSituacaoSolicitacaoRepo).getByNomeSituacao(any());
		assertNotNull(mockService.inserirArquivoSimba(candidatoFile, "matricula", parameterMap));
	}

	@Test
	public void alterarArquivoSimbaTest() throws NegocioException, IOException {
		Map<String, String[]> parameterMap = new HashMap<String, String[]>();
		parameterMap.put("codigo", new String[] { "12461D53-53A5-477D-A682-52E5811639A1" });
		parameterMap.put("situacao", new String[] { "1" });
		parameterMap.put("id", new String[] { "1" });
		parameterMap.put("tipo", new String[] { "solicitacao" });

		SituacaoSigilo situacaoSigilo = SituacaoSigilo.builder().id(1).nomeSituacao("Iniciado").build();
		Optional<SituacaoSigilo> situacaoOptional = Optional.of(situacaoSigilo);

		SituacaoSolicitacao situacaoSolicitacao = SituacaoSolicitacao.builder().id(1)
				.nomeSituacao(SituacaoSolicitacaoEnum.REJEITADO).build();
		AgendamentoETL agendamentoETL = AgendamentoETL.builder().codigo("3ACF3524-7F1D-4DBD-AC95-395FCD09EC96")
				.solicitacao(new Solicitacao()).build();
		Mockito.doReturn(agendamentoETL).when(mockRepo).buscaAgendamentoETLPorSolicitacaoPrimeiro(any());
		Mockito.doReturn(situacaoOptional).when(mockSituacaoSigiloRepo).findById(any());
		Mockito.doReturn(situacaoSolicitacao).when(mockSituacaoSolicitacaoRepo).getByNomeSituacao(any());

		Optional<Arquivo> arquivoOptional = Optional.of(new Arquivo());
		Mockito.doReturn(arquivoOptional).when(mockArquivoRepository).findOne(any());
		Mockito.doReturn(agendamentoETL).when(mockRepo).save(any());

		assertNotNull(mockService.alterarArquivoSimba(candidatoFile, "matricula", parameterMap));
	}

	@Test
	public void alterarArquivoSimba2Test() throws NegocioException, IOException {
		Map<String, String[]> parameterMap = new HashMap<String, String[]>();
		parameterMap.put("codigo", new String[] { "12461D53-53A5-477D-A682-52E5811639A1" });
		parameterMap.put("situacao", new String[] { "1" });
		parameterMap.put("id", new String[] { "1" });
		parameterMap.put("tipo", new String[] { "solicitacao" });

		SituacaoSigilo situacaoSigilo = SituacaoSigilo.builder().id(1).nomeSituacao("Iniciado").build();
		Optional<SituacaoSigilo> situacaoOptional = Optional.of(situacaoSigilo);

		SituacaoSolicitacao situacaoSolicitacao = SituacaoSolicitacao.builder().id(1)
				.nomeSituacao(SituacaoSolicitacaoEnum.REJEITADO).build();
		AgendamentoETL agendamentoETL = AgendamentoETL.builder().codigo("3ACF3524-7F1D-4DBD-AC95-395FCD09EC96")
				.solicitacao(new Solicitacao()).build();
		Mockito.doReturn(agendamentoETL).when(mockRepo).save(any());

		Mockito.doReturn(agendamentoETL).when(mockRepo).buscaAgendamentoETLPorSolicitacaoPrimeiro(any());
		Mockito.doReturn(situacaoOptional).when(mockSituacaoSigiloRepo).findById(any());
		Mockito.doReturn(situacaoSolicitacao).when(mockSituacaoSolicitacaoRepo).getByNomeSituacao(any());
		Mockito.doReturn(Boolean.TRUE).when(multipartFile).isEmpty();

		Optional<Arquivo> arquivoOptional = Optional.of(new Arquivo());
		Mockito.doReturn(arquivoOptional).when(mockArquivoRepository).findOne(any());

		assertNotNull(mockService.alterarArquivoSimba(multipartFile, "matricula", parameterMap));
	}

	@Test
	public void removerArquivoSimbaTest() throws NegocioException, IOException {
		SituacaoSigilo situacaoSigilo = SituacaoSigilo.builder().id(1).nomeSituacao("Iniciado").build();
		Optional<SituacaoSigilo> situacaoOptional = Optional.of(situacaoSigilo);
		Optional<Arquivo> arquivoOptional = Optional.of(new Arquivo());
		Mockito.doReturn(arquivoOptional).when(mockArquivoRepository).findOne(any());
		Mockito.doReturn(situacaoOptional).when(mockSituacaoSigiloRepo).findById(any());
		Mockito.doReturn(AgendamentoETL.builder().codigo("3ACF3524-7F1D-4DBD-AC95-395FCD09EC96")
				.solicitacao(new Solicitacao()).build()).when(mockRepo)
				.buscaAgendamentoETLPorSolicitacaoPrimeiro(any());
		mockService.removerArquivoSimba(1);
	}

	@Test
	public void buscaAgendamentoPorSolicitacaoAllTest() {
		AgendamentoETL agendamento = AgendamentoETL.builder().build();
		Mockito.doReturn(Arrays.asList(agendamento)).when(mockRepo).buscaAgendamentoETLPorSolicitacaoAll(any());

		assertNotNull(mockService.buscaAgendamentoPorSolicitacaoAll(4));
	}

	@Test
	public void downloadArquivoPorAfastamentoTest() throws NegocioException, IOException, ParseException {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		SituacaoSigilo situacaoSigilo = SituacaoSigilo.builder().id(1).nomeSituacao("Transmitido").build();
		Optional<SituacaoSigilo> situacaoOptional = Optional.of(situacaoSigilo);
		AgendamentoETL agendamento = AgendamentoETL.builder().dtHrProcessamento(dateFormat.parse("11-11-2012"))
				.hostname("Hostname").evento("Evento").solicitacao(new Solicitacao()).build();
		Mockito.doReturn(situacaoOptional).when(mockSituacaoSigiloRepo).findById(any());
		Mockito.doReturn("Matricula").when(principal).getName();
		Mockito.doReturn(agendamento).when(mockRepo).buscaAgendamentoETLPorSolicitacao(any());
		mockService.downloadArquivoPorAfastamento(4, principal);
	}

	@Test(expected = NegocioException.class)
	public void downloadArquivoPorAfastamento2Test() throws NegocioException, IOException, ParseException {
		AgendamentoETL agendamento = AgendamentoETL.builder().dtHrProcessamento(new Date())
				.codigo("3ACF3524-7F1D-4DBD-AC95-395FCD09EC96").build();
		Mockito.doReturn("/sidce/test").when(mockParametroService).buscarPorChave(any());
		Mockito.doReturn(agendamento).when(mockRepo).buscaAgendamentoETLPorSolicitacao(any());
		mockService.downloadArquivoPorAfastamento(4, principal);
	}

}
