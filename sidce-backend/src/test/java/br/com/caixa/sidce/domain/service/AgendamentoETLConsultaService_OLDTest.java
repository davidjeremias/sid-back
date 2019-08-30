package br.com.caixa.sidce.domain.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.caixa.sidce.domain.model.AgendamentoETL;
import br.com.caixa.sidce.domain.model.AuditoriaProcessamento;
import br.com.caixa.sidce.domain.model.Evento;
import br.com.caixa.sidce.domain.model.Titular;
import br.com.caixa.sidce.domain.model.enums.EventoEnum;
import br.com.caixa.sidce.infraestructure.persistence.jpa.AgendamentoETLCustomRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.AgendamentoETLRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.AuditoriaProcessamentoCustomRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.AuditoriaProcessamentoRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.EventoRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.TitularRepository;
import br.com.caixa.sidce.interfaces.util.FileStorage;
import br.com.caixa.sidce.interfaces.web.dto.VisualizacaoArquivosDTO;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;

@DataJpaTest
@TestPropertySource(properties = { "spring.jpa.hibernate.ddl-auto=create" })
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ComponentScan(basePackages = { 
		"br.com.caixa.sidce.config",
		"br.com.caixa.sidce.util.infraestructure",
		"br.com.caixa.sidce.domain.service",
		"br.com.caixa.sidce.infraestructure.persistence.jpa",
		"br.com.caixa.sidce.interfaces.util" })
@RunWith(SpringRunner.class)
@SpringBootTest
public class AgendamentoETLConsultaService_OLDTest {

	@Mock
	AgendamentoETLRepository repo;

	@Mock
	AgendamentoETLCustomRepository cepo;
	
	@Mock
	AuditoriaProcessamentoCustomRepository mockAuditoriaRepo;
	
	@Mock
	FileStorage mockFileStorage;
	
	@Mock
	ParametroService mockParametroService;
	
	@Spy
	@InjectMocks
	AgendamentoETLConsultaService service;

	@Autowired
	AgendamentoETLConsultaService agendamentoETLConsultaService;

	@Autowired
	AgendamentoETLRepository repository;

	@Autowired
	AgendamentoETLCustomRepository crepo;

	@Autowired
	AuditoriaProcessamentoRepository auditoriaProcessamentoRepository;

	@Autowired
	TitularRepository titularRepository;
	
	@Mock
	EventoRepository mockEventoRepository;
	
	@Autowired
	EventoRepository eventoRepository;

	@Autowired
	Environment env;

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
	}

	private AgendamentoETL criaMockAgendamentoETL() {
		return AgendamentoETL.builder().id(1).matricula("C123456").periodo(201801)
				.evento(EventoEnum.SOB_DEMANDA.getNome()).dtHoraCadastro(new Date())
				.dtHrProcessamento(new Date()).nomeArquivoCandidato("cnpj_candidatos_2018.txt")
				.nomeArquivoPartido("cnpj_partido_2018.txt").codigo("DE251ABD-4B2D-4994-93CC-582C83280113").hostname("")
				.build();
	}

	@Before
	public void initializeDatabase() {

		MockitoAnnotations.initMocks(this);
		
		AgendamentoETL agendamento = criaMockAgendamentoETL();
		AgendamentoETL agendamento2 = criaMockAgendamentoETL();
		
		Evento evento1 = Evento.builder().descricaoEvento(EventoEnum.DOWNLOAD).build();
		eventoRepository.save(evento1);
		Evento evento2 = Evento.builder().descricaoEvento(EventoEnum.UPLOAD).build();
		eventoRepository.save(evento2);
		eventoRepository.findAll();
		
		agendamento2.setDtHrProcessamento(
				Date.from(LocalDate.of(2015, 6, 12).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		agendamento2.setCodigo("06032983-CA8B-4ACD-86F3-B196D43E7029");
		repository.save(agendamento);
		repository.save(agendamento2);

		AuditoriaProcessamento auditoriaProcessamento1 = AuditoriaProcessamento.builder().id(1)
				.codigo("DE251ABD-4B2D-4994-93CC-582C83280113").data(new Date()).dataHoraProcessamento(new Date())
				.evento(EventoEnum.UPLOAD.getNome()).hostname("TesteHost").matricula("C123456")
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

	@Test
	public void testaBuscaPeriodosGerados() {
		List<Integer> result = agendamentoETLConsultaService.buscaPeriodosGerados();
		assertFalse(result.isEmpty());
	}

	@Test
	public void testaBuscaETLExecutadosPorAplicacao() {
		doReturn(Arrays.asList(AgendamentoETL.builder().build())).when(repo).buscaExecutadosPorAplicacao(Mockito.any());
		assertNotNull(service.executadosPorAplicacao());
	}

	@Test
	public void testaConsultaAgendamentosComFiltro() {
		service.setEtlReportDir(report.getRoot().toString());
		Map<String, String[]> filter = new HashMap<>();

		filter.put("matricula", new String[] { "C123456" });
		filter.put("periodoInformacao", new String[] { "201801" });
		filter.put("inicioPeriodoGeracao", new String[] { "2000-12-05 00:00:00" });
		filter.put("fimPeriodoGeracao", new String[] { "2200-12-06 00:00:00" });
		filter.put("page", new String[] { "0" });
		filter.put("limit", new String[] { "5" });
		
		Mockito.doReturn("/sidce/test").when(mockParametroService).buscarPorChave(Mockito.any());
		Mockito.doReturn(null).when(cepo).consultaAgendamentosComFiltro(Mockito.any(), Mockito.any());
		
		assertNull(service.consultaAgendamentosComFiltro(filter));
	}

	@Test
	public void testaExpugaArquivosMaisTrintaDias() throws IOException, NegocioException {
//		TODO refazer
		Mockito.doReturn("/sidce/test").when(mockParametroService).buscarPorChave(Mockito.any());
		
		service.expurgaArquivos();
	}

	@Test(expected = NegocioException.class)
	public void testaExpugaArquivosMaisTrintaDiasErroArquivo() throws NegocioException, IOException {
		
		
		File[] lista = {new File("teste")};
		Mockito.doReturn(lista).when(service).getListaArquivos();
		Mockito.doReturn(lista).when(service).listaArquivosZipados();
		Mockito.doThrow(new NegocioException()).when(service).getBFAofFile(Mockito.any());

		service.expurgaArquivos();
	}

	@Test
	public void testaConsultaAgendamentosComFiltroNULL() {
		service.setEtlReportDir(report.getRoot().toString());
		Map<String, String[]> filter = new HashMap<>();
		Mockito.doReturn("/sidce/test").when(mockParametroService).buscarPorChave(Mockito.any());
		Mockito.doReturn(null).when(cepo).consultaAgendamentosComFiltro(Mockito.any(), Mockito.any());
		assertNull(service.consultaAgendamentosComFiltro(filter));
	}
	
	@Test
	public void testaConsultaAgendamentosComFiltroException() throws NegocioException {
		service.setEtlReportDir(report.getRoot().toString());
		Map<String, String[]> filter = new HashMap<>();
		Mockito.doThrow(NegocioException.class).when(service).expurgaArquivos();
		service.consultaAgendamentosComFiltro(filter);
	}

	@Test(expected = NegocioException.class)
	public void testaInserirParametrosConsultaGeracaoIsNaFila() throws NegocioException {
		AgendamentoETL a = criaMockAgendamentoETL();
		MockitoAnnotations.initMocks(this); 

		Mockito.when(repo.listProcessosFila("06032983-CA8B-4ACD-86F3-B196D43E7029"))
				.thenReturn(Arrays.asList(criaMockAgendamentoETL()));

		Mockito.doReturn(a).when(service).getAgendamentoOriginal("06032983-CA8B-4ACD-86F3-B196D43E7029");

		AgendamentoETL esperado = service.inserirParametrosConsultaGeracao("06032983-CA8B-4ACD-86F3-B196D43E7029",
				"C123456", "testHost");
		service.findOne(esperado.getId());
	}

	@Test(expected = NegocioException.class)
	public void testaInserirParametrosConsultaGeracaoIsProcessosDisponiveisParaDownload() throws NegocioException {
		MockitoAnnotations.initMocks(this);
		AgendamentoETL a = criaMockAgendamentoETL();
		Mockito.when(cepo.processosDisponiveisParaDownload()).thenReturn(Arrays.asList(a));
		AgendamentoETL esperado = service.inserirParametrosConsultaGeracao("06032983-CA8B-4ACD-86F3-B196D43E7029",
				"C123456", "testHost");
		service.findOne(esperado.getId());
	}

	@Test(expected = NegocioException.class)
	public void inserirParametrosConsultaGeracaoDisponivelDownload() throws NegocioException {
		MockitoAnnotations.initMocks(this);
		AgendamentoETL a = criaMockAgendamentoETL();
		Mockito.doReturn(a).when(service).getAgendamentoOriginal("06032983-CA8B-4ACD-86F3-B196D43E7029");
		Mockito.doReturn(true).when(service).isDisponivelDownload(a);

		AgendamentoETL esperado = service.inserirParametrosConsultaGeracao("06032983-CA8B-4ACD-86F3-B196D43E7029",
				"C123456", "testHost");
		service.findOne(esperado.getId());
	}

	@Test
	public void testaBuscaNomeArquivosProcessamento() throws NegocioException {
		List<String> teste = new ArrayList<>();
		teste.add("teste");
		Mockito.doReturn(teste).when(mockAuditoriaRepo).buscarTxtDisponiveis(Mockito.anyString());
		String[] arquivos = service.buscaNomeArquivosProcessamento("123");
		String[] esperados = new String[] {"teste"};
		assertEquals(arquivos, esperados);
	}
	
	@Test
	public void testaBuscaNomeArquivosProcessamentoVazio() throws NegocioException {
		service.buscaNomeArquivosProcessamento("1231");
	}
	
	@Test
	public void testaConsultaArquivoGerado() throws NegocioException, IOException {
		Resource r = new PathResource("teste");
		Mockito.doReturn(new String[] {"teste"}).when(service).buscaNomeArquivosProcessamento(Mockito.anyString());
		Mockito.doReturn(r).when(service).montaCaminhoArquivos(Mockito.any(), Mockito.anyString());
		assertEquals(r, service.consultaArquivoGerado("123"));
	}

	@Test
	public void testaMontaCaminhoArquivos() throws NegocioException, IOException {
		String codigo = "DE251ABD-4B2D-4994-93CC-582C83280113";
		service.setEtlReportDir(report.getRoot().getPath());
		String[] arquivos = (String[]) Arrays.asList(codigo).toArray();
		Mockito.doReturn(true).when(service).deleteFile(Mockito.any(File.class));
		Mockito.doReturn(null).when(mockFileStorage).carregaResourceArquivo(Mockito.anyString(), Mockito.any());
		Mockito.doReturn("/sidce/test").when(mockParametroService).buscarPorChave(Mockito.any());
		service.montaCaminhoArquivos(arquivos, codigo);
	}

	@Test
	public void testaMontaCaminhoArquivosThrow() throws NegocioException, IOException {
		String codigo = "DE251ABD-4B2D-4994-93CC-582C83280113";
		agendamentoETLConsultaService.setEtlReportDir(report.getRoot().getPath());
		exception.expect(NegocioException.class);
		String[] arquivos = new String[] {};
		Resource resource = service.montaCaminhoArquivos(arquivos, codigo);
		assertTrue(resource.exists());
	}

	@Test
	public void testaVerificaSeExisteZipado() throws IOException {

		report.newFile("DE251ABD-4B2D-4994-93CC-582C83280113.zip");
		File arquivo = agendamentoETLConsultaService.verificaSeExisteZipado("DE251ABD-4B2D-4994-93CC-582C83280113",
				report.getRoot().getPath());
		assertEquals(arquivo.getName(), "DE251ABD-4B2D-4994-93CC-582C83280113.zip");
	}

	@Test
	public void testaVerificaSeExisteZipadoNULL() {
		File arquivo = agendamentoETLConsultaService.verificaSeExisteZipado("DE251ABD-4B2D-4994-93CC-582C83280113",
				report.getRoot().getPath());
		assertEquals(arquivo, null);
	}
	
	@Test
	public void testaDownloadConsultaArquivoNULL() throws NegocioException, IOException {
		Mockito.doReturn(null).when(service).verificaSeExisteZipado(Mockito.anyString(), Mockito.any());
		Mockito.doReturn(true).when(service).arquivoGeradoUltimosTrintaDias(Mockito.anyString());
		Mockito.doReturn(null).when(service).consultaArquivoGerado(Mockito.anyString());
		service.downloadConsultaArquivo("teste");
	}

	@Test(expected = NegocioException.class)
	public void testaDownloadConsultaArquivoException() throws NegocioException, IOException {
		Mockito.doReturn(false).when(service).arquivoGeradoUltimosTrintaDias(Mockito.anyString());
		service.downloadConsultaArquivo("teste");
	}

	@Test
	public void testaListaArquivosZipados() throws IOException {
		service.setEtlReportDir(report.getRoot().toString());
		Mockito.doReturn("/sidce/test").when(mockParametroService).buscarPorChave(Mockito.any());
		service.listaArquivosZipados();
	}

	@Test
	public void testaDetalhamentoCincoArquivosSemParam() {
		Map<String, String[]> parms = new HashMap<>();
		PageImpl<VisualizacaoArquivosDTO> pageable = agendamentoETLConsultaService.detalhamentoCincoArquivos(parms);
		assertTrue(pageable.getContent().isEmpty());
	}
	
	@Test
	public void testaDetalhamentoCincoArquivosComParam() {
		Map<String, String[]> params = new HashMap<>();
		params.put("codigo", new String[] { "DE251ABD-4B2D-4994-93CC-582C83280113" });
		params.put("cnpj", new String[] { "1321565" });
		params.put("numeroAgencia", new String[] { "49819819" });
		params.put("numeroConta", new String[] { "16516561" });
		
		Titular titular =  Titular.builder().cpfCNPJ("1321565").numeroAgencia(49819819)
				.numeroConta("16516561").build();
		titular.setCodigo("DE251ABD-4B2D-4994-93CC-582C83280113");
		List<VisualizacaoArquivosDTO> list = Arrays.asList(new VisualizacaoArquivosDTO(titular));
		PageImpl<VisualizacaoArquivosDTO> page = new PageImpl<>(list);
		
		Mockito.doReturn(page).when(cepo).detalhamentoCincoArquivos(Mockito.any(), Mockito.any());
				
		PageImpl<VisualizacaoArquivosDTO> pageable = service.detalhamentoCincoArquivos(params);
		assertFalse(pageable.getContent().isEmpty());
	}

	@Test
	public void testaDetalhamentoArquivo() throws NegocioException {

		Map<String, String[]> params = new HashMap<>();
		params.put("codigo", new String[] { "DE251ABD-4B2D-4994-93CC-582C83280113" });
		params.put("cnpj", new String[] { "1321565" });
		params.put("numeroAgencia", new String[] { "49819819" });
		params.put("numeroConta", new String[] { "16516561" });

		params.put("arquivo", new String[] { "banco" });
		agendamentoETLConsultaService.detalhamentoCincoArquivosPorArquivo(params);

		params.replace("arquivo", new String[] { "conta" });
		agendamentoETLConsultaService.detalhamentoCincoArquivosPorArquivo(params);

		params.replace("arquivo", new String[] { "extrato" });
		agendamentoETLConsultaService.detalhamentoCincoArquivosPorArquivo(params);

		params.replace("arquivo", new String[] { "origemDestino" });
		agendamentoETLConsultaService.detalhamentoCincoArquivosPorArquivo(params);

		params.replace("arquivo", new String[] { "titular" });
		agendamentoETLConsultaService.detalhamentoCincoArquivosPorArquivo(params);
	}
	
	@Test(expected = NegocioException.class)
	public void testaDetalhamentoArquivoDefault() throws NegocioException {
		Map<String, String[]> params = new HashMap<>();
		params.put("codigo", new String[] { "DE251ABD-4B2D-4994-93CC-582C83280113" });
		params.put("cnpj", new String[] { "1321565" });
		params.put("numeroAgencia", new String[] { "49819819" });
		params.put("numeroConta", new String[] { "16516561" });
		
		params.replace("arquivo", new String[] { "NaN" });
		agendamentoETLConsultaService.detalhamentoCincoArquivosPorArquivo(params);
	}

	@Test
	public void testaDetalhamentoArquivoException() throws NegocioException {

		Map<String, String[]> params = new HashMap<>();
		params.put("arquivo", new String[] { "banco" });
		agendamentoETLConsultaService.detalhamentoCincoArquivosPorArquivo(params);

		params.replace("arquivo", new String[] { "conta" });
		agendamentoETLConsultaService.detalhamentoCincoArquivosPorArquivo(params);

		params.replace("arquivo", new String[] { "extrato" });
		agendamentoETLConsultaService.detalhamentoCincoArquivosPorArquivo(params);

		params.replace("arquivo", new String[] { "origemDestino" });
		agendamentoETLConsultaService.detalhamentoCincoArquivosPorArquivo(params);

		params.replace("arquivo", new String[] { "titular" });
		agendamentoETLConsultaService.detalhamentoCincoArquivosPorArquivo(params);

		params.remove("arquivo");
		exception.expect(NegocioException.class);
		agendamentoETLConsultaService.detalhamentoCincoArquivosPorArquivo(params);

	}
	
	@Test(expected = NegocioException.class)
	public void testaSelecionaInterfacePorArquivoDefault() throws NegocioException {
		service.selecionaInterfacePorArquivo("teste", Pageable.unpaged(), new VisualizacaoArquivosDTO(new Titular()));
	}

}
