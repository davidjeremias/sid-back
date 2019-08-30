package br.com.caixa.sidce.interfaces.web;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.caixa.sidce.domain.model.ParametrosIntegracaoTSE;
import br.com.caixa.sidce.domain.service.ParametrosIntegracaoService;
import br.com.caixa.sidce.interfaces.web.dto.ParametrosIntegracaoDTO;
import br.com.caixa.sidce.util.infraestructure.auditoria.AuditoriaInterface;
import br.com.caixa.sidce.util.infraestructure.auditoria.TipoEventoAuditoriaEnum;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("api/parametrosIntegracao")
@CrossOrigin(origins = "*")
@Api(value = "ParametrosIntegracaoTSE")
public class ParametrosIntegracaoController {

	@Autowired
	private ParametrosIntegracaoService service;
	
	@AuditoriaInterface(func="Parâmetros TSE", tipoEvento=TipoEventoAuditoriaEnum.INSERCAO)
	@PostMapping("/salvar")
	public ResponseEntity<ParametrosIntegracaoTSE> salvar(@RequestBody ParametrosIntegracaoDTO dto, Principal principal){
		ParametrosIntegracaoTSE retorno = service.salvar(dto, principal);
		return retorno == null ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
				: new ResponseEntity<>(retorno, HttpStatus.CREATED);
	}
	
	@GetMapping("/buscar")
	public ResponseEntity<List<ParametrosIntegracaoDTO>> buscaParametros(){
		List<ParametrosIntegracaoDTO> retorno = service.findAllParametros();;
		return retorno.isEmpty() ? new ResponseEntity<>(retorno, HttpStatus.NO_CONTENT)
				: new ResponseEntity<>(retorno, HttpStatus.OK);
	}
	
	@GetMapping("/verificaLink")
	public ResponseEntity<Resource> verificaLink(HttpServletRequest request) throws NegocioException {
		Resource retorno = service.verificaLink(request.getParameterMap());
		return retorno == null ? new ResponseEntity<>(retorno, HttpStatus.NO_CONTENT)
				: new ResponseEntity<>(retorno, HttpStatus.OK);
	}
}
