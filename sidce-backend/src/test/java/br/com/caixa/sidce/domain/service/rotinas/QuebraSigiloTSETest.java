package br.com.caixa.sidce.domain.service.rotinas;

import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doReturn;

import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.caixa.sidce.domain.model.AgendamentoETL;
import br.com.caixa.sidce.domain.service.AgendamentoETLService;
import br.com.caixa.sidce.domain.service.AuditoriaDownloadTSEService;
import br.com.caixa.sidce.domain.service.IntegracaoTSEService;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;

@RunWith(SpringRunner.class)
public class QuebraSigiloTSETest {

	@Spy
	@InjectMocks
	QuebraSigiloTSE rotina;

	@Mock
	IntegracaoTSEService mockIntegracaoService;

	@Mock
	AgendamentoETLService mockAgendamentoETLService;
	
	@Mock
	AuditoriaDownloadTSEService auditoria;

	AgendamentoETL agendamentoETL;
	
	static final Integer SUCESSO = 1;

	@Before
	public void initialize() {
		agendamentoETL = AgendamentoETL.builder().id(1).build();
	}

	@Test
	public void testaRotinaTSE() throws NegocioException, UnknownHostException {
		doReturn(LocalDate.now()).when(mockIntegracaoService).getDiaUtilMes();
		doReturn(LocalTime.now()).when(mockIntegracaoService).getHoraInicio();
		doNothing().when(mockIntegracaoService).cnpjPartidoTSE();
		doReturn(agendamentoETL).when(mockAgendamentoETLService).insereParametrosGeracaoRotina();
		doNothing().when(auditoria).salvaLogIntegracao(SUCESSO);
		rotina.rotinaTSE();

		doReturn(LocalTime.now().plusHours(1)).when(mockIntegracaoService).getHoraInicio();
		rotina.rotinaTSE();

		doReturn(LocalDate.now().plusDays(1)).when(mockIntegracaoService).getDiaUtilMes();
		rotina.rotinaTSE();

	}
	@Test
	public void testaRotinaTSEException() throws NegocioException, UnknownHostException {
		doReturn(LocalDate.now()).when(mockIntegracaoService).getDiaUtilMes();
		doReturn(LocalTime.now()).when(mockIntegracaoService).getHoraInicio();
		doNothing().when(mockIntegracaoService).cnpjPartidoTSE();
		doReturn(agendamentoETL).when(mockAgendamentoETLService).insereParametrosGeracaoRotina();
		Mockito.doThrow(NegocioException.class).when(auditoria).salvaLogIntegracao(SUCESSO);
		rotina.rotinaTSE();
	}

	@Test
	public void testaRotinaTSEDiaUtil() throws NegocioException, UnknownHostException {
		doReturn(LocalDate.now().plusDays(1)).when(mockIntegracaoService).getDiaUtilMes();
		doNothing().when(mockIntegracaoService).cnpjPartidoTSE();
		doReturn(agendamentoETL).when(mockAgendamentoETLService).insereParametrosGeracaoRotina();
		rotina.rotinaTSE();
	}

}
