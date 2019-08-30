package br.com.caixa.sidce.domain.service;

import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.testng.Assert.assertNotNull;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import br.com.caixa.sidce.domain.model.ParametrosIntegracaoTSE;
import br.com.caixa.sidce.infraestructure.persistence.jpa.ParametrosIntegracaoRepository;
import br.com.caixa.sidce.interfaces.web.dto.ParametrosIntegracaoDTO;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;

@RunWith(MockitoJUnitRunner.class)
public class ParametrosIntegracaoServiceTest {

	@InjectMocks
	ParametrosIntegracaoService service;

	@Mock
	private ParametrosIntegracaoRepository repository;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private ResponseEntity<Resource> retorno;

	@Mock
	private Resource resource;

	@Mock
	private Principal principal;

	private ParametrosIntegracaoDTO dto;

	private static final String URL_ARQUIVO_TSE = "http://agencia.tse.jus.br/estatistica/sead/odsele/prestacao_contas/CNPJ_campanha_atual.zip";

	@Before
	public void setup() {
		dto = ParametrosIntegracaoDTO.builder().url(URL_ARQUIVO_TSE).dia(1).hora(LocalDateTime.now()).build();
	}

	@Test
	public void salvarTest() {
		doReturn(new ParametrosIntegracaoTSE()).when(repository).save(any());
		doReturn(new ParametrosIntegracaoTSE()).when(repository).buscaUltimo();
		doReturn("C03521584").when(principal).getName();
		assertNotNull(service.salvar(dto, principal));
		dto.setId(1);
		assertNotNull(service.salvar(dto, principal));
	}

	@Test
	public void atualizaParametroAntigoTest() {
		service.atualizaParametroAntigo(principal);

		doReturn("C03521584").when(principal).getName();
		doReturn(new ParametrosIntegracaoTSE()).when(repository).buscaUltimo();
		service.atualizaParametroAntigo(principal);
	}

	@Test
	public void findAllParametrosTest() {
		List<ParametrosIntegracaoTSE> list = Arrays
				.asList(ParametrosIntegracaoTSE.builder().id(1).dia(1).hora(LocalTime.now()).url("Teste").build());
		doReturn(list).when(repository).findAll();
		assertNotNull(service.findAllParametros());
		doReturn(new ArrayList<>()).when(repository).findAll();
		assertNotNull(service.findAllParametros());
	}

	@Test
	public void verificaLinkTest() throws NegocioException {
		Map<String, String[]> filter = new HashMap<String, String[]>();
		filter.put("url", new String[] { "http://agencia.tse.jus.br/test.zip" });
		doReturn(resource).when(retorno).getBody();
		doReturn(HttpStatus.OK).when(retorno).getStatusCode();
		doReturn(retorno).when(restTemplate).getForEntity(Mockito.anyString(), any());
		assertNotNull(service.verificaLink(filter));

		filter.put("url", new String[] { "http://agencia.tse.jus.br" });
		assertNull(service.verificaLink(filter));

		filter.put("url", new String[] { "http://www.test.com/test.zip" });
		assertNull(service.verificaLink(filter));

		filter.put("url", new String[] { "http://www.test.com" });
		assertNull(service.verificaLink(filter));
	}

	@Test
	public void verificaLinkExceptionTest() throws HttpStatusCodeException, NegocioException {
		Map<String, String[]> filter = new HashMap<String, String[]>();
		filter.put("url", new String[] { "http://agencia.tse.jus.br/test.zip" });
		doReturn(HttpStatus.ACCEPTED).when(retorno).getStatusCode();
		doReturn(retorno).when(restTemplate).getForEntity(Mockito.anyString(), any());
		assertNull(service.verificaLink(filter));
	}

}
