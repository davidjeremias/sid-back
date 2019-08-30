package br.com.caixa.sidce.domain.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.testng.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.caixa.sidce.domain.model.AgendamentoETL;
import br.com.caixa.sidce.domain.model.Evento;
import br.com.caixa.sidce.domain.model.SituacaoSigilo;
import br.com.caixa.sidce.domain.model.enums.ParametroEnum;
import br.com.caixa.sidce.infraestructure.persistence.jpa.AgendamentoETLCustomRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.AgendamentoETLRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.SituacaoSigiloRepository;
import br.com.caixa.sidce.interfaces.util.FileStorage;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;

@RunWith(SpringRunner.class)
public class AgendamentoETLConsultaServiceTest {

	@Spy
	@InjectMocks
	AgendamentoETLConsultaService service;

	AgendamentoETL agendamentoETL;
	
	Evento evento;
	
	Optional<SituacaoSigilo> situacaoSigilo;

	@Mock
	AgendamentoETLRepository repo;
	
	@Mock
	SituacaoSigiloRepository sitRepo;

	@Mock
	AgendamentoETLCustomRepository crepo;
	
	@Mock
	ParametroService parametroService;
	
	@Mock
	EventoService eventoService;
	
	@Mock
	FileStorage fileStorage;
	
	@Mock
	private Resource resource;

	@Before
	public void initialize() {
		MockitoAnnotations.initMocks(this);
		service = spy(service);
		agendamentoETL = AgendamentoETL.builder().id(1).build();
		situacaoSigilo = Optional.of(SituacaoSigilo.builder().id(1).build());
	}

	@Test
	public void testInserirParametrosConsultaGeracao() throws NegocioException {
		doReturn(agendamentoETL).when(service).getAgendamentoOriginal(any());
		doReturn(situacaoSigilo).when(sitRepo).findById(any());
		doReturn(agendamentoETL).when(service).save(any());
		assertEquals(agendamentoETL, service.inserirParametrosConsultaGeracao("codigo", "matricula", "hostname"));
	}

	@Test(expected = NegocioException.class)
	public void testInserirParametrosConsultaGeracao_JaNaFila() throws Exception {
		List<AgendamentoETL> list = new ArrayList<AgendamentoETL>();
		list.add(agendamentoETL);
		doReturn(agendamentoETL).when(service).getAgendamentoOriginal(any());
		doReturn(list).when(repo).listProcessosFila(anyString());
		service.inserirParametrosConsultaGeracao("codigo", "matricula", "hostname");
	}

	@Test(expected = NegocioException.class)
	public void testInserirParametrosConsultaGeracao_JaDisponivelDownload() throws NegocioException {
		doReturn(agendamentoETL).when(service).getAgendamentoOriginal(any());
		doReturn(evento).when(eventoService).buscarPorChave(any());
		doReturn(true).when(service).isDisponivelDownload(any());
		service.inserirParametrosConsultaGeracao("codigo", "matricula", "hostname");
	}

	@Test
	public void testGetAgendamentoOriginal() throws NegocioException {
		List<AgendamentoETL> list = new ArrayList<AgendamentoETL>();
		list.add(agendamentoETL);
		doReturn(list).when(service).findAll(any(Map.class));
		service.getAgendamentoOriginal("codigo");
		assertEquals(agendamentoETL, service.getAgendamentoOriginal("codigo"));
	}

	@Test(expected = NegocioException.class)
	public void testGetAgendamentoOriginal_noOriginal() throws NegocioException {
		doReturn(new ArrayList<AgendamentoETL>()).when(service).findAll(any(Map.class));
		service.getAgendamentoOriginal("codigo");
	}
	
	@Test
	public void testDownloadConsultaArquivo_ArquivoNull() throws NegocioException, IOException {
		doReturn(true).when(service).arquivoGeradoUltimosTrintaDias("codigo");
		doReturn(ParametroEnum.ETL_DIR_REPORT.toString()).when(parametroService).buscarPorChave(any());
		doReturn(new File("caminho")).when(service).verificaSeExisteZipado(any(), any());
		doReturn(resource).when(fileStorage).carregaResourceArquivo(any(), any());
		assertEquals(resource, service.downloadConsultaArquivo("codigo"));
	}
	
	@Test
	public void testArquivoGeradoUltimosTrintaDias() {
		doReturn(true).when(crepo).isArquivosGeradoUltimosTrintaDias("codigo");
		assertTrue(service.arquivoGeradoUltimosTrintaDias("codigo"));
	}
	
	@Test
	public void testProcessosDisponiveisParaDownload() throws Exception {
		List<AgendamentoETL> quebrasVigentes = new ArrayList<AgendamentoETL>();
		quebrasVigentes.add(agendamentoETL);
		doReturn(quebrasVigentes).when(crepo).processosDisponiveisParaDownload();		
		doReturn(ParametroEnum.ETL_DIR_REPORT.toString()).when(parametroService).buscarPorChave(any());
		assertTrue(service.listDisponivesiDownload().isEmpty());
	}
	
	@Test
	public void testGetDataValidaArquivos() {
		assertEquals(service.getDataValidaArquivos(), LocalDate.now().minusDays(30));
	}
	
	@Test
	public void testDeleteFile() throws IOException, NegocioException {
		File arquivoCriado = File.createTempFile("teste", ".txt");
		service.deleteFile(arquivoCriado);
		assertFalse(arquivoCriado.exists());
	}
	
	@Test(expected = NegocioException.class)
	public void testDeleteFile_IOException() throws IOException, NegocioException {
		File f = File.createTempFile("teste", ".txt");
		service.deleteFile(f);
		service.deleteFile(f);
	}
	
	@Test
	public void testGetBFAofFile() throws NegocioException, IOException {
		File f = File.createTempFile("teste", ".txt");
		BasicFileAttributes esperado = Files.readAttributes(f.toPath(), BasicFileAttributes.class);
		assertEquals(service.getBFAofFile(f).creationTime(), esperado.creationTime());
	}
	
	@Test(expected = NegocioException.class)
	public void testGetBFAofFile_IOException() throws NegocioException, IOException {
		File f = File.createTempFile("teste", ".txt");
		service.deleteFile(f);
		service.getBFAofFile(f);
	}

	@Test
	public void testExpurgaArquivos_ListaComItem() throws NegocioException, IOException, ParseException {
		// Cria um arquivo temporário, adiciona em uma lista e retorna
		File f = File.createTempFile("teste", ".txt");
		File[] fa = {f};
		doReturn(fa).when(service).getListaArquivos();
		
		// Cria os atributos do arquivo na mão para gerar um filetime mockado
		BasicFileAttributes bfa = Mockito.mock(BasicFileAttributes.class);
		doReturn(bfa).when(service).getBFAofFile(any());
		String date = "01.01.2013 10:00:10";
		long milis = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").parse(date).getTime();
	    FileTime fileTime = FileTime.fromMillis(milis);
	    doReturn(fileTime).when(bfa).creationTime();
	    
	    // Antes do expurgo todos os arquivos existem, realiza o expurgo e todos os arquivos devem ser apagados
	    assertTrue(Arrays.stream(fa).allMatch(e -> e.exists()));
		service.expurgaArquivos();
		assertTrue(Arrays.stream(fa).allMatch(e -> !e.exists()));
	}
	
	@Test
	public void testExpurgaArquivos_ListaSemItem() throws IOException, NegocioException {
		File f = File.createTempFile("teste", ".txt");
		File[] fa = {f};
		doReturn(fa).when(service).getListaArquivos();
		service.expurgaArquivos();
		assertTrue(fa.length > 0);
	}
	

	@Test
	public void testaProcessosGeracaoConsultaNaFila() throws NegocioException, IOException {
		doReturn(null).when(crepo).processosGeracaoConsultaNaFila(Mockito.any());
		Map<String, String[]> parms = new HashMap<>();
		assertNull(service.processosGeracaoConsultaNaFila(parms));
	}
}
