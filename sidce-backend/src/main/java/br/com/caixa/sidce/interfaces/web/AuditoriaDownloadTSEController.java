package br.com.caixa.sidce.interfaces.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import br.com.caixa.sidce.domain.model.AuditoriaDownloadTSE;
import br.com.caixa.sidce.domain.service.AuditoriaDownloadTSEService;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/api/auditoriaIntegracaoTSE")
@CrossOrigin(origins = "*")
@Api(value = "AuditoriaDownloadTSE")
public class AuditoriaDownloadTSEController{

	@Autowired
	private AuditoriaDownloadTSEService service;
	
	@GetMapping("/buscar")
	public ResponseEntity<Page<AuditoriaDownloadTSE>> buscaAuditoriaRotina(HttpServletRequest request){
		Page<AuditoriaDownloadTSE> retorno = service.buscaAuditoriaRotina(request.getParameterMap());
		return retorno.getContent().isEmpty() ? new ResponseEntity<>(retorno, HttpStatus.NO_CONTENT)
				: new ResponseEntity<>(retorno, HttpStatus.OK);
	}
	
	@GetMapping("/{id}/buscaParametrosAtual")
	public ResponseEntity<AuditoriaDownloadTSE> buscaParametrosAtual(@PathVariable("id")Integer id) throws NegocioException{
		AuditoriaDownloadTSE retorno = service.buscaParametrosAtual(id);
		return retorno == null ? new ResponseEntity<>(retorno, HttpStatus.NO_CONTENT)
				: new ResponseEntity<>(retorno, HttpStatus.OK);
	}
}
