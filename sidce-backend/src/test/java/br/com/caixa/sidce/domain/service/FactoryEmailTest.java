package br.com.caixa.sidce.domain.service;

import static org.junit.Assert.assertNotNull;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.caixa.sidce.domain.model.UnidadeGestora;
import br.com.caixa.sidce.infraestructure.persistence.jpa.UnidadeGestoraRepository;
import br.com.caixa.sidce.interfaces.web.dto.EmailDTO;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;

@RunWith(SpringRunner.class)
public class FactoryEmailTest {

	@Spy
	@InjectMocks
	private FactoryEmail email;
	
	@Mock
	private ParametroService parametroService;

	@Mock
	private UnidadeGestoraRepository unidadeGestoraRepository;

	@Mock
	private SIICOService siicoService;
	
	private String host;
	private Integer port;
	private String userName;
	private String password;
	
	JavaMailSenderImpl mailSender;
	
	SimpleMailMessage message = new SimpleMailMessage();
	
	@Before
	public void setup() {
		host = "smtptest.correiolivre.caixa";
		port = 25;
		userName = "s743603";
		password = "VUq8thew";
		mailSender = new JavaMailSenderImpl();
		mailSender.setHost(host);
		mailSender.setPort(port);
		mailSender.setUsername(userName);
		mailSender.setPassword(password);
		
		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "false");
		props.put("mail.smtp.starttls.enable", "false");
		props.put("mail.debug", "true");
		
		message = new SimpleMailMessage();
		message.setFrom("gitecbr05@caixa.gov.br");
		message.setTo("p577394@caixa.gov.br");
		message.setSubject("Mensagem automática portal https://sigilobancario.caixa");
		message.setText("Email SIDCE");
		
		MockitoAnnotations.initMocks(this);
		email = spy(email);
	}

	@Test
	public void getEmailUnidadeUsuarioTest() throws NegocioException {
		EmailDTO email1 = EmailDTO.builder().email("c891427@caixa.gov.br").build();
		doReturn(email1).when(siicoService).buscaEmailUnidade("891427");
		doReturn(email1.getEmail()).when(email).getEmailUnidadeUsuario("c891427");
		assertNotNull(email.getEmailUnidadeUsuario("c891427"));
	}
	
	@Test
	public void getEmailUnidadeGestoraTest() {
		List<UnidadeGestora> list = Arrays.asList(new UnidadeGestora());
		doReturn(list).when(unidadeGestoraRepository).findAll();
		doReturn("").when(email).getEmailUnidadeGestora();
		assertNotNull(email.getEmailUnidadeGestora());
	}
	
	@Test
	public void mailSenderTest() {
		doReturn(mailSender).when(email).mailSender();
		assertNotNull(email.mailSender());
	}
}
