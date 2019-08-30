package br.com.caixa.sidce.domain.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.caixa.sidce.domain.model.AgendamentoETL;
import br.com.caixa.sidce.domain.model.Evento;
import br.com.caixa.sidce.domain.model.enums.EventoEnum;
import br.com.caixa.sidce.infraestructure.persistence.jpa.EventoRepository;


@DataJpaTest
@TestPropertySource(
        properties = {
                "spring.jpa.hibernate.ddl-auto=create"
        }
)
@ComponentScan(
    basePackages = {
    		"br.com.caixa.sidce.config",
    		"br.com.caixa.sidce.util.infraestructure",
            "br.com.caixa.sidce.domain.service",
            "br.com.caixa.sidce.infraestructure.persistence.jpa",
            "br.com.caixa.sidce.interfaces.util"
    }
)
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
public class AgendamentoETLRestFullServiceTest {

	@Autowired
	AgendamentoETLService agendamentoETLService;
	
	@Autowired
	EventoRepository eventoRepository;
	
	AgendamentoETL agendamento = null;

	@Before
    public void initializeDatabase(){
		
		Evento evento1 = Evento.builder().descricaoEvento(EventoEnum.DOWNLOAD).build();
		eventoRepository.save(evento1);
		
		 agendamento = AgendamentoETL.builder()
				.matricula("")
				.periodo(201801)
				.evento(EventoEnum.SOB_DEMANDA.getNome())
				.dtHoraCadastro(new Date())
				.dtHrProcessamento(new Date())
				.nomeArquivoCandidato("")
				.nomeArquivoPartido("")
				.codigo("")
				.hostname("")
				.build();
		
	      agendamentoETLService.save(agendamento);
    }
	
	@Test
	public void testSort() {
      List<AgendamentoETL> lista = agendamentoETLService.findAll(Sort.by(new Order(Sort.Direction.ASC, "evento")));
      assertNotNull("AgendamentoETL Not null:",lista);
      assertEquals("AgendamentoETL Size:",1, lista.size());
		
	}
	
	@Test
	public void testFindAll() {
      List<AgendamentoETL> lista = agendamentoETLService.findAll();
      assertNotNull("AgendamentoETL Not null:",lista);
      assertEquals("AgendamentoETL Size:",1, lista.size());
		
	}
	
	@Test
	public void testFindAll_2() {
		Map<String, String[]> params = new HashMap<String, String[]>();
		String[] myStringArray = {"desc"};
		params.put("descricaoEvento", myStringArray);
		
		List<AgendamentoETL> lista = agendamentoETLService.findAll(params);
		assertNotNull("AgendamentoETL Not null:",lista);
		assertEquals("AgendamentoETL Size:",1, lista.size());
	}
	
	@Test
	public void testPageRequest() {
		Map<String, String[]> params = new HashMap<String, String[]>();
		String[] myStringArray = {"desc"};
		params.put("descricaoEvento", myStringArray);
		
		String[] myStringArrayPage = {"1"};
		params.put("page", myStringArrayPage);
		
		String[] myStringArrayLimit = {"1"};
		params.put("limit", myStringArrayLimit);
		
		Page<AgendamentoETL> lista = agendamentoETLService.findAllPageable(params);
		assertNotNull("AgendamentoETL Not null:",lista);
		assertEquals("AgendamentoETL Size:",0, lista.getNumberOfElements());
	}
	
	@Test
	public void testFindOne() {
	  List<AgendamentoETL> lista = agendamentoETLService.findAll();
      Optional<AgendamentoETL> ag = agendamentoETLService.findOne(lista.get(0).getId());
      assertNotNull("AgendamentoETL Not null:",ag);
      assertEquals(ag.get(), agendamento);
	}
	
	@Test
	public void testUpdate() {
		Evento e = Evento.builder().id(1).descricaoEvento(EventoEnum.DOWNLOAD).build();
	  agendamento.setEvento(e.getDescricaoEvento().getNome());
	  agendamentoETLService.update(agendamento);
	  List<AgendamentoETL> lista = agendamentoETLService.findAll();
	  Optional<AgendamentoETL> ag = agendamentoETLService.findOne(lista.get(0).getId());
      assertNotNull("AgendamentoETL Not null:",ag);
      assertEquals(ag.get(), agendamento);
	}

}
