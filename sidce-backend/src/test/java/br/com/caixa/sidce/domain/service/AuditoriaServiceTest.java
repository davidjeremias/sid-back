package br.com.caixa.sidce.domain.service;

import static org.testng.Assert.assertNotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.caixa.sidce.domain.model.Auditoria;
import br.com.caixa.sidce.infraestructure.persistence.jpa.AuditoriaRepository;
import br.com.caixa.sidce.interfaces.web.dto.AuditoriaDTO;
import br.com.caixa.sidce.util.infraestructure.auditoria.AuditoriaInterface;
import br.com.caixa.sidce.util.infraestructure.auditoria.TipoEventoAuditoriaEnum;

@DataJpaTest
@TestPropertySource(properties = { "spring.jpa.hibernate.ddl-auto=create" })
@ComponentScan(basePackages = {
		"br.com.caixa.sidce.config",
		"br.com.caixa.sidce.util.infraestructure",
		"br.com.caixa.sidce.domain.service",
		"br.com.caixa.sidce.infraestructure.persistence.jpa",
		"br.com.caixa.sidce.interfaces.util" })
@RunWith(SpringRunner.class)
@SpringBootTest
public class AuditoriaServiceTest {

	private static final String MATRICULA = "matricula";
	private static final String DT_FIM = "fimPeriodo";
	private static final String DT_INICIO = "iniPeriodo";
	private static final String EVENTO = "evento";
 	private static final String FUNCIONALIDADE = "funcionalidade";
 	private static final String CODIGO = "codigoSolicitacao";
	
	@Mock
	private ProceedingJoinPoint proceedingJoinPoint;
	
	@Mock
	AuditoriaInterface auditoriainterface;

	@Autowired
	AuditoriaService auditoriaService;

	@Autowired
	Environment env;
	
	@Autowired
	AuditoriaRepository repository;

	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	Map<String, String[]> filterNull = new HashMap<String, String[]>();
	Map<String, String[]> filter = new HashMap<String, String[]>();
	
	@Before
	public void initDatabase() {
		
		Auditoria a = Auditoria.builder()
		.matricula("TestUser")
		.funcionalidade("testFunc")
		.tipoEvento(TipoEventoAuditoriaEnum.BUSCA)
		.dtHrAuditoria(new Date())
		.host("testHost").build();
		repository.save(a);
	}
	

	@Test
	public void testaFuncionalidadesDisponiveis() {
		List<String> r = auditoriaService.funcionalidadesDisponiveis();
		assertNotNull(r);
	}
	
	@Test
	public void eventosDisponiveisNull() {
		List<String> r = auditoriaService.eventosDisponiveis(filterNull);
		filter.put(FUNCIONALIDADE, new String[] {"matricula"});
		r = auditoriaService.eventosDisponiveis(filter);
		assertNotNull(r);
	}
	
	@Test
	public void buscaAuditoriaPaginado() {
		filter.put(MATRICULA, new String[] {"matricula"});
		filter.put(DT_FIM, new String[] {"2019-02-12T18:45:00.676Z"});
		filter.put(DT_INICIO, new String[] {"2019-02-12T18:45:00.676Z"});
		filter.put(EVENTO, new String[] {TipoEventoAuditoriaEnum.BUSCA.toString()});
		filter.put(FUNCIONALIDADE, new String[] {"matricula"});
		filter.put(CODIGO, new String[] {"EL000001"});
		filter.put("page", new String[] {"1"});
		filter.put("limit", new String[] {"10"});
		PageImpl<AuditoriaDTO> r = auditoriaService.buscaAuditoriaPaginado(filterNull);
		PageImpl<AuditoriaDTO> r2 = auditoriaService.buscaAuditoriaPaginado(filter);
		assertNotNull(r);
		assertNotNull(r2);
	}

}
