package br.com.caixa.sidce.domain.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doReturn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;

import br.com.caixa.sidce.domain.model.CodigoLancamento;
import br.com.caixa.sidce.infraestructure.persistence.jpa.CodigoLancamentoCustomRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.CodigoLancamentoRepository;
import br.com.caixa.sidce.interfaces.web.dto.CodigoLancamentoDTO;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;

@RunWith(MockitoJUnitRunner.class)
public class CodigoLancamentoServiceTest {

	@InjectMocks
	private CodigoLancamentoService service;

	@Mock
	private CodigoLancamentoRepository repository;

	@Mock
	private CodigoLancamentoCustomRepository crepo;

	@Mock
	private PageImpl<CodigoLancamento> page;

	@Test
	public void buscaLancamentosTest() {
		Map<String, String[]> filter = new HashMap<String, String[]>();
		doReturn(page).when(crepo).buscaLancamentos(any(), anyString(), anyString(), anyString(), anyBoolean());
		service.buscaLancamentos(filter);

		filter.put("descricaoLancamento", new String[] { "teste" });
		filter.put("natureza", new String[] { "teste" });
		filter.put("codigo", new String[] { "1" });
		filter.put("isCodigo", new String[] { "true" });
		assertNotNull(service.buscaLancamentos(filter));
	}

	@Test
	public void salvarTest() throws NegocioException {
		CodigoLancamentoDTO dto = new CodigoLancamentoDTO();
		CodigoLancamento codigoLancamento = new CodigoLancamento();
		Optional<CodigoLancamento> optional = Optional.of(new CodigoLancamento());
		doReturn(optional).when(repository).findById(any());
		doReturn(codigoLancamento).when(repository).save(any());
		assertNotNull(service.salvar(dto));

		dto.setCodigoLancamento("123");
		assertNotNull(service.salvar(dto));
	}

	@Test(expected = NegocioException.class)
	public void salvarExceptionTest() throws NegocioException {
		service.salvar(new CodigoLancamentoDTO());
	}

	@Test
	public void salvarAllTest() throws NegocioException {
		CodigoLancamentoDTO dto = new CodigoLancamentoDTO();
		dto.setCodigoLancamento("123");
		List<CodigoLancamentoDTO> list = Arrays.asList(dto);

		Optional<CodigoLancamento> optional = Optional.of(new CodigoLancamento());
		doReturn(optional).when(repository).findById(any());
		doReturn(new ArrayList<>()).when(repository).saveAll(anyList());
		assertNotNull(service.salvarAll(list));
	}

}
