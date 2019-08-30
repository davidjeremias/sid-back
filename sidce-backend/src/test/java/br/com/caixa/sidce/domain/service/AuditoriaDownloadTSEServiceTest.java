package br.com.caixa.sidce.domain.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;

import java.util.HashMap;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.caixa.sidce.domain.model.AuditoriaDownloadTSE;
import br.com.caixa.sidce.domain.model.ParametrosIntegracaoTSE;
import br.com.caixa.sidce.infraestructure.persistence.jpa.AuditoriaDownloadTSECustomRepositoryImpl;
import br.com.caixa.sidce.infraestructure.persistence.jpa.AuditoriaDownloadTSERepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.ParametrosIntegracaoRepository;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;

@RunWith(SpringRunner.class)
public class AuditoriaDownloadTSEServiceTest {

	@Spy
	@InjectMocks
	private AuditoriaDownloadTSEService service;

	@Mock
	private AuditoriaDownloadTSECustomRepositoryImpl crepo;

	@Mock
	private AuditoriaDownloadTSERepository repo;

	@Mock
	private ParametrosIntegracaoRepository parametrosIntegracao;

	@Mock
	private PageImpl<AuditoriaDownloadTSE> pageImpl;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		service = spy(service);
	}

	@Test
	public void salvaLogIntegracaoTest() throws NegocioException {
		doReturn(new ParametrosIntegracaoTSE()).when(parametrosIntegracao).buscaUltimo();
		service.salvaLogIntegracao(1);
	}
	
	@Test(expected = NegocioException.class)
	public void salvaLogIntegracaoExceptionTest() throws NegocioException {
		doReturn(null).when(parametrosIntegracao).buscaUltimo();
		service.salvaLogIntegracao(1);
	}

	@Test
	public void buscaAuditoriaRotinaTest() {
		Map<String, String[]> params = new HashMap<String, String[]>();

		doReturn(pageImpl).when(crepo).buscaAuditoriaRotina(any(), any(), any(), Mockito.anyInt());
		service.buscaAuditoriaRotina(params);

		params.put("inicio", new String[] { "2019-07-02T18:47:24.000Z" });
		params.put("fim", new String[] { "2019-07-02T18:47:24.000Z" });
		params.put("status", new String[] { "1" });

		doReturn(pageImpl).when(crepo).buscaAuditoriaRotina(any(), any(), any(), Mockito.anyInt());
		service.buscaAuditoriaRotina(params);
	}

	@Test
	public void buscaParametrosAtualTest() throws NegocioException {
		Optional<AuditoriaDownloadTSE> optional = Optional.of(new AuditoriaDownloadTSE());
		doReturn(optional).when(repo).findById(Mockito.anyInt());
		assertNotNull(service.buscaParametrosAtual(1));
	}

}
