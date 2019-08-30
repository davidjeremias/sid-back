package br.com.caixa.sidce.domain.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;

import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.caixa.sidce.domain.model.AgendamentoETL;
import br.com.caixa.sidce.domain.model.Evento;
import br.com.caixa.sidce.infraestructure.persistence.jpa.AgendamentoETLCustomRepository;
import br.com.caixa.sidce.util.infraestructure.auditoria.AuditoriaAspecto;

@RunWith(SpringRunner.class)
public class AgendamentoETLServiceTest {

	@Spy
	@InjectMocks
	AgendamentoETLService service;

	@Mock
	ParametroService parametroService;

	AgendamentoETL agendamentoETL;

	@Mock
	EventoService eventoService;

	@Mock
	private CodigoSolicitacaoService codigoSolicitacaoService;

	@Mock
	private AgendamentoETLCustomRepository crepo;

	@Mock
	private AuditoriaAspecto auditoria;
	
	@Mock
	private FactoryEmail email;

	Evento evento;

	@Before
	public void initialize() {
		MockitoAnnotations.initMocks(this);
		service = spy(service);
		agendamentoETL = AgendamentoETL.builder().id(1).build();
	}

	@Test
	public void testaInsereParametrosGeracaoAutomatica() throws UnknownHostException {
		doReturn(agendamentoETL).when(service).save(any());
		doReturn(evento).when(eventoService).buscarPorChave(any());
		assertNotNull(service.insereParametrosGeracaoRotina());
	}
}
