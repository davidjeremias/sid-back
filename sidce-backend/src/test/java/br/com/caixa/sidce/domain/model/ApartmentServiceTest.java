package br.com.caixa.sidce.domain.model;

import java.sql.Timestamp;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.caixa.sidce.domain.model.enums.EventoEnum;
import br.com.caixa.sidce.domain.service.AgendamentoETLService;
import br.com.caixa.sidce.domain.service.EventoService;

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
@SpringBootTest
public class ApartmentServiceTest {
	
	  @Autowired
	  AgendamentoETLService service;
	  
	  @Autowired
	  EventoService eService;
	 
	  @Test
	  public void getList() {
	    	
		  eService.save(Evento.builder().descricaoEvento(EventoEnum.DOWNLOAD).build());
    	Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    	
    	AgendamentoETL a = new AgendamentoETL();
    	a.setEvento(EventoEnum.UPLOAD.getNome());
    	a.setDtHoraCadastro(timestamp);
    	a.setDtHrProcessamento(timestamp);
    	a.setHostname("");
    	a.setMatricula("");
    	a.setNomeArquivoCandidato("");
    	a.setNomeArquivoPartido("");
    	a.setPeriodo(1);
    	service.save(a);
	    	    
	     List<AgendamentoETL> lista = service.findAll();
	     System.out.println(lista.size());
	    }

}
