package br.com.caixa.sidce.interfaces.web;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.caixa.sidce.domain.model.Auditoria;
import br.com.caixa.sidce.domain.service.AuditoriaService;
import br.com.caixa.sidce.interfaces.web.dto.AuditoriaDTO;
import br.com.caixa.sidce.util.infraestructure.web.RestFullEndpoint;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/api/auditoria")
@CrossOrigin(origins = "*")
@Api(value = "Auditoria")
public class AuditoriaController extends RestFullEndpoint<Auditoria, Integer> { 

	
	@Autowired
	public AuditoriaController(AuditoriaService service) {
		super(service);
	}
	
	@GetMapping("/funcionalidades")
	public ResponseEntity<List<String>> funcionalidades() {
		List<String> retorno = ((AuditoriaService)service).funcionalidadesDisponiveis();
		if (retorno.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(retorno, HttpStatus.OK);
		}
	}
	
	@GetMapping("/eventos")
	public ResponseEntity<List<String>> eventos(HttpServletRequest request) {
		List<String> retorno = ((AuditoriaService)service).eventosDisponiveis(request.getParameterMap());
		if (retorno.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(retorno, HttpStatus.OK);
		}
	}

	@Override
	public ResponseEntity<?> findAllPageable(HttpServletRequest request, Principal principal) {
		PageImpl<AuditoriaDTO> retorno = ((AuditoriaService)service).buscaAuditoriaPaginado(request.getParameterMap());
		if (retorno.getContent().isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(retorno, HttpStatus.OK);
		}
	}
	
}
