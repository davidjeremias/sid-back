package br.com.caixa.sidce.domain.service;

import static org.powermock.api.mockito.PowerMockito.spy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.caixa.sidce.domain.model.Solicitacao;
import br.com.caixa.sidce.infraestructure.persistence.jpa.AfastamentoRepository;

@RunWith(SpringRunner.class)
public class AfastamentoServiceTest {

	@Spy
	@InjectMocks
	private AfastamentoService service;

	@Mock
	private AfastamentoRepository afastamentoRepository;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		service = spy(service);
	}

	@Test
	public void criaAfastamentoTest() {
		service.criaAfastamento("Maticula", 1, new Solicitacao());
	}

}
