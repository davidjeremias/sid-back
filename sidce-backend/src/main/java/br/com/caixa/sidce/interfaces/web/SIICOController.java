package br.com.caixa.sidce.interfaces.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.caixa.sidce.domain.service.SIICOService;
import br.com.caixa.sidce.interfaces.web.dto.UnidadeDTO;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;

@RestController
@RequestMapping("/api/siico")
@CrossOrigin(origins = "*")
public class SIICOController {

	@Autowired
	SIICOService service;

	@Autowired
	public SIICOController(SIICOService service) {
		this.service = service;
	}

	@GetMapping("/{unidade}/consultarUnidade")
	public ResponseEntity<UnidadeDTO> consultarUnidade(@PathVariable Integer unidade) throws NegocioException {
		UnidadeDTO unidadeDTO = service.consultarUnidade(unidade);
		return ResponseEntity.ok(unidadeDTO);
	}

}
