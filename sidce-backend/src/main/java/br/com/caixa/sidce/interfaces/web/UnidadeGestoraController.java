package br.com.caixa.sidce.interfaces.web;

import java.net.UnknownHostException;
import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.caixa.sidce.domain.model.UnidadeGestora;
import br.com.caixa.sidce.domain.service.UnidadeGestoraService;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/api/unidadeGestora")
@CrossOrigin(origins = "*")
@Api(value = "Unidadegestora")
public class UnidadeGestoraController {

	@Autowired
	private UnidadeGestoraService service;

	@GetMapping
	public ResponseEntity<List<UnidadeGestora>> buscaUnidades() {
		List<UnidadeGestora> retorno = service.buscaUnidades();
		return retorno.isEmpty() ? new ResponseEntity<>(retorno, HttpStatus.NO_CONTENT)
				: new ResponseEntity<>(retorno, HttpStatus.OK);
	}

	@GetMapping("/unidadeSIICO")
	public ResponseEntity<Page<UnidadeGestora>> buscaUnidadePorNumero(HttpServletRequest request)
			throws NegocioException {
		Page<UnidadeGestora> retorno = service.buscaUnidadePorNumero(request.getParameterMap());
		return retorno == null ? new ResponseEntity<>(retorno, HttpStatus.NO_CONTENT)
				: new ResponseEntity<>(retorno, HttpStatus.OK);
	}

	@PutMapping("/salvar")
	public ResponseEntity<UnidadeGestora> salvar(@RequestBody UnidadeGestora unidade, Principal principal)
			throws UnknownHostException {
		UnidadeGestora retorno = service.salvar(unidade, principal.getName());
		return retorno == null ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
				: new ResponseEntity<>(retorno, HttpStatus.CREATED);
	}
}
