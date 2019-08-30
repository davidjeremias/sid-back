package br.com.caixa.sidce.domain.service;

import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.caixa.sidce.domain.model.AgendamentoETL;
import br.com.caixa.sidce.domain.model.CodigoSolicitacao;
import br.com.caixa.sidce.domain.model.UnidadeGestora;
import br.com.caixa.sidce.domain.model.enums.ParametroEnum;
import br.com.caixa.sidce.infraestructure.persistence.jpa.UnidadeGestoraRepository;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;
import br.com.caixa.sidce.util.infraestructure.log.Log;

@Service
public class FactoryEmail {

	private static final String DOMINIO_EMAIL = "@corp.caixa.gov.br";
	private static final String MATRICULA_AUTOMATICA = "Automatico";

	@Autowired
	private ParametroService parametroService;

	@Autowired
	private UnidadeGestoraRepository unidadeGestoraRepository;

	@Autowired
	private SIICOService siicoService;

	public JavaMailSender mailSender() {
		String host = parametroService.buscarPorChave(ParametroEnum.HOST_MAIL);
		Integer port = Integer.valueOf(parametroService.buscarPorChave(ParametroEnum.PORT_MAIL));
		String userName = parametroService.buscarPorChave(ParametroEnum.USERNAME_MAIL);
		String password = parametroService.buscarPorChave(ParametroEnum.PASSWORD_MAIL);

		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(host);
		mailSender.setPort(port);
		mailSender.setUsername(userName);
		mailSender.setPassword(password);

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "false");
		props.put("mail.smtp.starttls.enable", "false");
		props.put("mail.debug", "true");
		return mailSender;
	}

	public void sendEmailRejeicaoSimba(AgendamentoETL retorno) throws NegocioException {
		String emailUnidadeGestora = getEmailUnidadeGestora();

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(emailUnidadeGestora);
		if (retorno.getMatricula().equals(MATRICULA_AUTOMATICA)) {
			message.setTo(emailUnidadeGestora);
		} else {
			String emailUnidadeUsuario = getEmailUnidadeUsuario(retorno.getMatricula());
			message.setTo(retorno.getMatricula() + DOMINIO_EMAIL, emailUnidadeUsuario, emailUnidadeGestora);
		}
		message.setSubject("Mensagem automática portal https://sigilobancario.caixa");
		message.setText("Prezado(a) usuário(a),\r\n" + "\r\n"
				+ "Foi disponibilizado arquivo de rejeição referente a transmissão dos cinco arquivos, conforme Carta Circular BACEN"
				+ " nº 3.454/2010,  via Sistema de Investigação de Movimentações Bancárias (SIMBA) do afastamento de sigilo "
				+ retorno.getCodigoSolicitacao().getCodigo() + "\r\n"
				+ "O arquivo de rejeição está disponível para consulta no portal https://sigilobancario.caixa.\r\n"
				+ "\r\n" + "Este e-mail é automático, favor não responder!");
		mailSender().send(message);
		Log.info(this.getClass(), "Email enviado...");
	}

	public void sendEmailAnaliseSolicitacao(String matricula, CodigoSolicitacao codigoSolicitacao, String situacao)
			throws NegocioException {
		String emailUnidadeGestora = getEmailUnidadeGestora();

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(emailUnidadeGestora);
		String emailUnidadeUsuario = getEmailUnidadeUsuario(matricula);
		message.setTo(matricula + DOMINIO_EMAIL, emailUnidadeUsuario, emailUnidadeGestora);
		message.setSubject("Mensagem automática portal https://sigilobancario.caixa");
		message.setText("Prezado(a) usuário(a), \r\n" + "\r\n" + "A solicitação de afastamento de sigilo "
				+ codigoSolicitacao.getCodigo() + " foi " + situacao + " pela área operadora do sistema.\r\n"
				+ "O resultado da análise está disponível para consulta no portal https://sigilobancario.caixa.\r\n"
				+ "\r\n" + "Este e-mail é automático, favor não responder! ");
		mailSender().send(message);
		Log.info(this.getClass(), "Email enviado...");
	}

	@Transactional
	public String getEmailUnidadeUsuario(String matricula) throws NegocioException {
		return siicoService.buscaEmailUnidade(matricula.substring(1)).getEmail();
	}

	public String getEmailUnidadeGestora() {
		List<UnidadeGestora> unidades = unidadeGestoraRepository.findAll();
		String emailUnidadeGestora = null;
		if (!unidades.isEmpty()) {
			emailUnidadeGestora = unidades.get(unidades.size() - 1).getEmailUnidade();
		}
		return emailUnidadeGestora;
	}
}
