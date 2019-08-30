package br.com.caixa.sidce.domain.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.caixa.sidce.domain.model.Evento;
import br.com.caixa.sidce.domain.model.enums.EventoEnum;
import br.com.caixa.sidce.infraestructure.persistence.jpa.EventoRepository;

@RunWith(SpringRunner.class)
public class EventoServiceTest {

	@Spy
	@InjectMocks
	EventoService service;

	@Mock
	EventoRepository repo;
	

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		service = spy(service);
	}

	@Rule
	public final ExpectedException exception = ExpectedException.none();


	@Test
	public void criarListaSolicitacaoContaTest() {
		doReturn(new Evento()).when(repo).getByDescricaoEvento(any());
		assertNotNull(service.buscarPorChave(EventoEnum.DOWNLOAD));
	}


}
