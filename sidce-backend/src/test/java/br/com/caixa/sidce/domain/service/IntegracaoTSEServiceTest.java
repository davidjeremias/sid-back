package br.com.caixa.sidce.domain.service;

import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import br.com.caixa.sidce.domain.model.Calendario;
import br.com.caixa.sidce.domain.model.ParametrosIntegracaoTSE;
import br.com.caixa.sidce.infraestructure.persistence.jpa.CalendarioRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.ParametrosIntegracaoRepository;
import br.com.caixa.sidce.interfaces.util.FileStorage;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;

@RunWith(MockitoJUnitRunner.class)
public class IntegracaoTSEServiceTest {

	@InjectMocks
	IntegracaoTSEService service;

	@Mock
	FileStorage mockFs;

	@Mock
	RestTemplate mockRestTemplate;

	@Mock
	CalendarioRepository mockCalendarioRepository;

	@Mock
	private ParametroService mockParametroService;

	@Mock
	private ParametrosIntegracaoRepository parametrosIntegracao;

	private List<ParametrosIntegracaoTSE> list;

	private ParametrosIntegracaoTSE param;

	@Before
	public void setup() {
		param = ParametrosIntegracaoTSE.builder().url(URL_ARQUIVO_TSE).dia(1).hora(LocalTime.now()).build();
		list = new ArrayList<ParametrosIntegracaoTSE>();
		list.add(ParametrosIntegracaoTSE.builder().url(URL_ARQUIVO_TSE).dia(1).hora(LocalTime.now()).build());
	}

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private static final String URL_ARQUIVO_TSE = "http://agencia.tse.jus.br/estatistica/sead/odsele/prestacao_contas/CNPJ_campanha_atual.zip";

	@Test
	public void testaCnpjPartidoTSE() throws Exception {
		Resource r = new UrlResource(folder.newFile().toURI());
		doReturn(r).when(mockRestTemplate).getForObject(URL_ARQUIVO_TSE, Resource.class);
		doReturn(param).when(parametrosIntegracao).buscaUltimo();
		doReturn("Teste").when(mockParametroService).buscarPorChave(any());
		service.cnpjPartidoTSE();
	}

	@Test
	public void getDiaUtilMesTest() throws IOException, NegocioException {
		List<Calendario> calendarios = new ArrayList<>();
		calendarios.add(new Calendario());
		doReturn(calendarios).when(mockCalendarioRepository).buscaDiasUteisMes(Mockito.anyInt(), Mockito.anyInt());
		doReturn(param).when(parametrosIntegracao).buscaUltimo();
		service.getDiaUtilMes();
	}

	@Test
	public void getHoraInicioTest() throws IOException {
		doReturn(param).when(parametrosIntegracao).buscaUltimo();
		assertNotNull(service.getHoraInicio());
		param.setHora(null);
		doReturn(param).when(parametrosIntegracao).buscaUltimo();
		assertNull(service.getHoraInicio());
	}

	@Test
	public void testaBuscaArquivoTSE() throws IOException, NegocioException {
		Resource r = new UrlResource(folder.newFile().toURI());
		doReturn(r).when(mockRestTemplate).getForObject(URL_ARQUIVO_TSE, Resource.class);
		doReturn(param).when(parametrosIntegracao).buscaUltimo();
		Resource actual = service.buscaArquivoTSE();
		assertEquals(actual, r);
	}

	@Test(expected = NegocioException.class)
	public void testaBuscaArquivoExceptionTSE() throws IOException, NegocioException {
		Mockito.doThrow(HttpServerErrorException.class).when(mockRestTemplate).getForObject(URL_ARQUIVO_TSE,
				Resource.class);
		doReturn(param).when(parametrosIntegracao).buscaUltimo();
		service.buscaArquivoTSE();
	}

}
