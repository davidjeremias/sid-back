package br.com.caixa.sidce.domain.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.caixa.sidce.domain.model.UnidadeGestora;
import br.com.caixa.sidce.infraestructure.persistence.jpa.UnidadeGestoraRepository;
import br.com.caixa.sidce.interfaces.web.dto.UnidadeDTO;
import br.com.caixa.sidce.util.infraestructure.auditoria.AuditoriaAspecto;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;

@RunWith(SpringRunner.class)
public class UnidadeGestoraServiceTest {

	@Spy
	@InjectMocks
	private UnidadeGestoraService service;

	@Mock
	private SIICOService siicoService;
	
	@Mock
	private AuditoriaAspecto auditoria;
	
	@Mock
	private UnidadeGestoraRepository repo;
	
	@Mock
	private Pageable pageable;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		service = spy(service);
	}

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void criarListaSolicitacaoContaTest() {
		List<UnidadeGestora> list = Arrays.asList(new UnidadeGestora());
		doReturn(list).when(repo).findAll();
		assertNotNull(service.buscaUnidades());
	}
	
	@Test
	public void salvarTest() throws UnknownHostException {
		UnidadeGestora unidade = new UnidadeGestora();
		doReturn(new ArrayList<>()).when(repo).findAll();
		doReturn(unidade).when(repo).save(any());
		assertNotNull(service.salvar(unidade, "matricula"));
	}
	
	@Test
	public void salvar2Test() throws UnknownHostException {
		UnidadeGestora unidade = new UnidadeGestora();
		List<UnidadeGestora> list = Arrays.asList(new UnidadeGestora());
		doReturn(list).when(repo).findAll();
		doReturn(unidade).when(repo).save(any());
		assertNotNull(service.salvar(unidade, "matricula"));
	}
	
	@Test
	public void buscaUnidadePorNumeroTest() throws UnknownHostException, NegocioException {
		Map<String, String[]> params = new HashMap<>();
		params.put("unidade", new String[] { "1" });
		List<UnidadeDTO> unidades = Arrays.asList(new UnidadeDTO());
		PageImpl<UnidadeDTO> retorno = new PageImpl<>(unidades, pageable, 2);
		doReturn(retorno).when(siicoService).buscaUnidade(any(), anyInt());
		assertNotNull(service.buscaUnidadePorNumero(params));
	}
	
	@Test(expected = NegocioException.class)
	public void buscaUnidadePorNumeroExceptionTest() throws UnknownHostException, NegocioException {
		Map<String, String[]> params = new HashMap<>();
		List<UnidadeDTO> unidades = Arrays.asList(new UnidadeDTO());
		PageImpl<UnidadeDTO> retorno = new PageImpl<>(unidades, pageable, 2);
		doReturn(retorno).when(siicoService).buscaUnidade(any(), anyInt());
		service.buscaUnidadePorNumero(params);
	}

}
