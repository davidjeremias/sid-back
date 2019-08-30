package br.com.caixa.sidce.util.infraestructure.auditoria;

import java.security.Principal;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import br.com.caixa.sidce.domain.model.Auditoria;
import br.com.caixa.sidce.domain.model.CodigoSolicitacao;
import br.com.caixa.sidce.domain.model.UnidadeGestora;
import br.com.caixa.sidce.infraestructure.persistence.jpa.AuditoriaRepository;

@Aspect
@Component
@Transactional
public class AuditoriaAspecto {
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private AuditoriaRepository auditoriaRepository;
	
	 @Around("@annotation(auditoriainterface)")
	 public Object around(ProceedingJoinPoint point, AuditoriaInterface auditoriainterface) throws Throwable {
		 Principal principal = SecurityContextHolder.getContext().getAuthentication();
		 registrarAuditoria(auditoriainterface.func(),auditoriainterface.tipoEvento(), principal.getName(),request.getLocalName());
		 return point.proceed();
	 }
	 
	 private void registrarAuditoria(String funcionalidade, TipoEventoAuditoriaEnum tipoEvento, String matricula, String host) {
		 
		 Auditoria aud = Auditoria.builder().dtHrAuditoria(new Date())
				 .funcionalidade(funcionalidade)
				 .tipoEvento(tipoEvento)
				 .matricula(matricula)
				 .host(host).build();
		 
		 auditoriaRepository.save(aud);
	 }
	 
	 public void registrarAuditoria(String funcionalidade, TipoEventoAuditoriaEnum tipoEvento, String matricula, String host,CodigoSolicitacao codigoSolicitacao) {
		 
		 Auditoria aud = Auditoria.builder().dtHrAuditoria(new Date())
				 .funcionalidade(funcionalidade)
				 .tipoEvento(tipoEvento)
				 .matricula(matricula)
				 .host(host)
				 .codigoSolicitacao(codigoSolicitacao).build();
		 
		 auditoriaRepository.save(aud);
	 }
	 
	 public void registrarAuditoria(String funcionalidade, TipoEventoAuditoriaEnum tipoEvento, String matricula, String host, UnidadeGestora unidade) {
		 
		 Auditoria aud = Auditoria.builder().dtHrAuditoria(new Date())
				 .funcionalidade(funcionalidade)
				 .tipoEvento(tipoEvento)
				 .matricula(matricula)
				 .host(host)
				 .unidadeGestora(unidade).build();
		 
		 auditoriaRepository.save(aud);
	 }
}
