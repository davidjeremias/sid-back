package br.com.caixa.sidce.domain.service;

import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.testng.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.caixa.sidce.domain.model.Parametro;
import br.com.caixa.sidce.domain.model.enums.ParametroEnum;
import br.com.caixa.sidce.infraestructure.persistence.jpa.ParametroRepository;

@RunWith(SpringRunner.class)
@PrepareForTest(fullyQualifiedNames = "com.baeldung.powermockito.introduction.*")
public class ParametroServiceTest {

	@InjectMocks
	ParametroService service;

	@Mock
	ParametroRepository repo;

	@Before
	public void initialize() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testBuscarPorChave() {
		String esperada = "param";
		doReturn(Parametro.builder().valor(esperada).build()).when(repo).getByChave(any());
		assertEquals(esperada, service.buscarPorChave(ParametroEnum.ETL_DIR_INPUT));
	}
}
