package br.com.caixa.sidce.domain.service;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.testng.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import br.com.caixa.sidce.domain.model.Banco;
import br.com.caixa.sidce.infraestructure.persistence.jpa.SIICOCustomRepository;
import br.com.caixa.sidce.interfaces.web.dto.UnidadeDTO;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;

@RunWith(MockitoJUnitRunner.class)
public class SIICOServiceTest {

	@InjectMocks
	private SIICOService siicoService;

	@Mock
	private SIICOCustomRepository siicoCustomRepository;

	@Mock
	private Banco banco;

	@Mock
	private UnidadeDTO unidadeDTO;

	@Mock
	private Pageable pageable;

	@Mock
	private PageImpl<UnidadeDTO> page;

	@Test
	public void consultarUnidadeTest() throws NegocioException {
		given(siicoCustomRepository.consultarUnidade(anyInt())).willReturn(null);
		assertNotNull(siicoService.consultarUnidade(1));
		given(siicoCustomRepository.consultarUnidade(anyInt())).willReturn(unidadeDTO);
		assertNotNull(siicoService.consultarUnidade(1));
		assertNotNull(siicoService.consultarUnidade(null));
	}

	@Test(expected = NegocioException.class)
	public void consultarUnidadeExceptionTest() throws NegocioException {
		Mockito.doThrow(new NegocioException()).when(siicoCustomRepository).consultarUnidade(anyInt());
		siicoService.consultarUnidade(1);
	}

	@Test
	public void buscaUnidadeTest() throws NegocioException {
		given(siicoCustomRepository.buscaUnidades(Mockito.any(), anyInt())).willReturn(page);
		assertNotNull(siicoService.buscaUnidade(pageable, 1));
	}

	@Test(expected = NegocioException.class)
	public void buscaUnidadeExceptionTest() throws NegocioException {
		siicoService.buscaUnidade(pageable, null);
	}

}
